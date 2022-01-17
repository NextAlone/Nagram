package tw.nekomimi.nekogram.proxy.tcp2ws;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.FileLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.internal.NativeImageTestsAccessorsKt;
import okio.ByteString;
import tw.nekomimi.nekogram.proxy.WsLoader;

public class ProxyHandler implements Runnable {

    private InputStream m_ClientInput = null;
    private OutputStream m_ClientOutput = null;
    private Object m_lock;
    private Socks4Impl comm = null;

    Socket m_ClientSocket;
    WebSocket m_ServerSocket = null;
    Socket m_ServerSocketRaw = null;
    Throwable error = null;
    HashMap<String, Integer> mapper;
    WsLoader.Bean bean;
    byte[] m_Buffer = new byte[SocksConstants.DEFAULT_BUF_SIZE];

    public ProxyHandler(Socket clientSocket, HashMap<String, Integer> mapper, WsLoader.Bean bean) {
        this.mapper = mapper;
        this.bean = bean;
        this.m_ClientSocket = clientSocket;
        try {
            m_ClientSocket.setSoTimeout(SocksConstants.DEFAULT_PROXY_TIMEOUT);
        } catch (SocketException e) {
            FileLog.e("Socket Exception during seting Timeout.");
        }
        FileLog.d("Proxy Created.");
    }

    public void setLock(Object lock) {
        m_lock = lock;
    }

    public void run() {
        FileLog.d("Proxy Started.");
        setLock(this);

        if (prepareClient()) {
            processRelay();
            close();
        } else {
            FileLog.e("Proxy - client socket is null !");
        }
    }

    public void close() {
        try {
            if (m_ClientOutput != null) {
                m_ClientOutput.flush();
                m_ClientOutput.close();
            }
        } catch (IOException e) {
            // ignore
        }

        try {
            if (m_ClientSocket != null) {
                m_ClientSocket.close();
            }
        } catch (IOException e) {
            // ignore
        }

        try {
            if (m_ServerSocket != null) {
                m_ServerSocket.close(1000, "");
            }
        } catch (Exception e) {
            // ignore
        }

        m_ServerSocket = null;
        m_ClientSocket = null;
        m_ServerSocketRaw = null;

        FileLog.d("Proxy Closed.");
    }

    public void sendToClient(byte[] buffer) {
        sendToClient(buffer, buffer.length);
    }

    public void sendToClient(byte[] buffer, int len) {
        if (m_ClientOutput != null && len > 0 && len <= buffer.length) {
            try {
                m_ClientOutput.write(buffer, 0, len);
                m_ClientOutput.flush();
            } catch (IOException e) {
                FileLog.e("Sending data to client", e);
            }
        }
    }

    public static Object okhttpClient;

    public void connectToServer(String server, Runnable succ, Runnable fail) throws IOException {
        if (server.equals("")) {
            close();
            FileLog.e("Invalid Remote Host Name - Empty String !!!");
            return;
        }

        Integer target = mapper.get(server);
        for (int i = 1; target == null && i < 4; i++) {
            target = mapper.get(server.substring(0, server.length() - i));
        }
        if (target == null || target.equals(-1)) {
            // Too many logs
            if (!mapper.containsKey(server)) {
                mapper.put(server, -1);
                FileLog.e("No route for ip " + server);
            }
            close();
            return;
        }

        String ip = server;

        if (bean.getPayload().size() >= target) {
            server = bean.getPayload().get(target - 1);
        }

        if (BuildConfig.DEBUG) {
            FileLog.d("Route " + ip + " to dc" + target + ": " + (bean.getTls() ? "wss://" : "ws://") + server + "." + bean.getServer() + "/api");
        }

        if (okhttpClient == null) {
            okhttpClient = new OkHttpClient.Builder().dns(new WsLoader.CustomDns()).build();
        }

        ((OkHttpClient) okhttpClient)
                .newWebSocket(new Request.Builder()
                        .url((bean.getTls() ? "wss://" : "ws://") + server + "." + bean.getServer() + "/api")
                        .build(), new WebSocketListener() {
                    @Override
                    public void onOpen(@NotNull okhttp3.WebSocket webSocket, @NotNull Response response) {
                        m_ServerSocket = webSocket;
                        m_ServerSocketRaw = NativeImageTestsAccessorsKt.getConnection(Objects.requireNonNull(NativeImageTestsAccessorsKt.getExchange(response))).socket();
                        succ.run();
                    }

                    @Override
                    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                        error = t;
                        fail.run();
                    }

                    @Override
                    public void onMessage(@NotNull okhttp3.WebSocket webSocket, @NotNull ByteString bytes) {
                        FileLog.d("[" + webSocket.request().url() + "] Reveived " + bytes.size() + " bytes");
                        ProxyHandler.this.sendToClient(bytes.toByteArray());
                    }

                    @Override
                    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                        FileLog.d("[" + webSocket.request().url() + "] Reveived text: " + text);
                    }

                    @Override
                    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                        FileLog.d("[" + webSocket.request().url() + "] Closed: " + code + " " + reason);
                    }

                    @Override
                    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                        FileLog.d("[" + webSocket.request().url() + "] Closing: " + code + " " + reason);
                        close();

                    }
                });
    }

    public boolean prepareClient() {
        if (m_ClientSocket == null) return false;

        try {
            m_ClientInput = m_ClientSocket.getInputStream();
            m_ClientOutput = m_ClientSocket.getOutputStream();
            return true;
        } catch (IOException e) {
            FileLog.e("Proxy - can't get I/O streams!");
            FileLog.e(e.getMessage(), e);
            return false;
        }
    }

    public void processRelay() {
        try {
            byte SOCKS_Version = getByteFromClient();

            switch (SOCKS_Version) {
                case SocksConstants.SOCKS4_Version:
                    comm = new Socks4Impl(this);
                    break;
                case SocksConstants.SOCKS5_Version:
                    comm = new Socks5Impl(this);
                    break;
                default:
                    FileLog.e("Invalid SOKCS version : " + SOCKS_Version);
                    return;
            }
            FileLog.d("Accepted SOCKS " + SOCKS_Version + " Request.");

            comm.authenticate(SOCKS_Version);
            comm.getClientCommand();

            if (comm.socksCommand == SocksConstants.SC_CONNECT) {
                comm.connect();
                relay();
            }
        } catch (Exception e) {
            FileLog.e(e.getMessage(), e);
        }
    }

    public byte getByteFromClient() throws Exception {
        while (m_ClientSocket != null) {
            int b;
            try {
                b = m_ClientInput.read();
            } catch (InterruptedIOException e) {
                Thread.yield();
                continue;
            }
            return (byte) b; // return loaded byte
        }
        throw new Exception("Interrupted Reading GetByteFromClient()");
    }

    public void relay() {
        for (boolean isActive = true; isActive; Thread.yield()) {
            int dlen = this.checkClientData();
            if (dlen < 0) {
                isActive = false;
            }

            if (dlen > 0) {
                while (m_ServerSocket == null && error == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                if (error != null) throw new RuntimeException(error);
                FileLog.d("[" + m_ServerSocket.request().url() + "] Send " + dlen + " bytes");
                this.m_ServerSocket.send(ByteString.of(Arrays.copyOf(this.m_Buffer, dlen)));
            }
        }

    }

    public int checkClientData() {
        synchronized (m_lock) {
            //	The client side is not opened.
            if (m_ClientInput == null) return -1;

            int dlen;

            try {
                dlen = m_ClientInput.read(m_Buffer, 0, SocksConstants.DEFAULT_BUF_SIZE);
            } catch (InterruptedIOException e) {
                return 0;
            } catch (IOException e) {
                FileLog.d("Client connection Closed!");
                close();    //	Close the server on this exception
                return -1;
            }

            if (dlen < 0) close();

            return dlen;
        }
    }

}
