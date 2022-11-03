package tw.nekomimi.nekogram.proxy.tcp2ws;

import android.annotation.SuppressLint;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.telegram.messenger.FileLog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import tw.nekomimi.nekogram.NekoConfig;
import tw.nekomimi.nekogram.utils.DnsFactory;

public class WsProxyHandler extends Thread {

    private InputStream clientInputStream = null;
    private OutputStream clientOutputStream = null;

    private final WsLoader.Bean bean;
    private Socket clientSocket;
    private WebSocket wsSocket = null;
    private final byte[] buffer = new byte[4096];

    private String wsHost = "";

    private final AtomicInteger wsStatus = new AtomicInteger(0);
    private final static int STATUS_OPENED = 1;
    private final static int STATUS_CLOSED = 2;
    private final static int STATUS_FAILED = 3;

    private final CountDownLatch connecting = new CountDownLatch(1);

    public WsProxyHandler(Socket clientSocket, WsLoader.Bean bean) {
        this.bean = bean;
        this.clientSocket = clientSocket;
        FileLog.d("ProxyHandler Created.");
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void run() {
        FileLog.d("Proxy to " + wsHost + "Started.");
        try {
            clientInputStream = clientSocket.getInputStream();
            clientOutputStream = clientSocket.getOutputStream();
            // Handle Socks5 HandShake
            socks5Handshake();
            FileLog.d("socks5 handshake and websocket connection done");
            // Start read from client socket and send to websocket
            this.clientSocket.setSoTimeout(1000);
            while (clientSocket != null && wsSocket != null && wsStatus.get() == STATUS_OPENED && !clientSocket.isClosed() && !clientSocket.isInputShutdown()) {
                int readLen = 0;
                try {
                    readLen = this.clientInputStream.read(buffer);
                } catch (SocketTimeoutException ex) {
                    if (wsStatus.get() != STATUS_OPENED)
                        throw new Exception(String.format("[%s] timeout and ws closed", wsHost));
                    continue;
                }
                FileLog.d(String.format("[%s] read %d from local", wsHost, readLen));
                if (readLen == -1) throw new Exception(String.format("[%s] socks closed", wsHost));;
                if (wsStatus.get() != STATUS_OPENED) throw new Exception(String.format("[%s] ws closed when trying to write", wsHost));;
                this.wsSocket.send(ByteString.of(buffer, 0, readLen));
            }
        } catch (SocketException se) {
            if ("Socket closed".equals(se.getMessage())) {
                FileLog.d("socket closed from ws when reading from client");
                close();
            } else {
                FileLog.e(se);
                close();
            }
        } catch (Exception e) {
            FileLog.e(e);
            close();
        }
    }

    public void close() {
        if (wsStatus.get() == STATUS_CLOSED)
            return;
        wsStatus.set(STATUS_CLOSED);
        FileLog.d("ws handler closed");

        try {
            if (clientSocket != null)
                clientSocket.close();
        } catch (IOException e) {
            // ignore
        }
//        try {
//            if (wsSocket != null) {
//                wsSocket.cancel();
//            }
//        } catch (Exception e) {
//            // ignore
//        }

        clientSocket = null;
//        wsSocket = null;
    }

    private static volatile OkHttpClient okhttpClient = null;
    private static final Object okhttpLock = new Object();

    private static OkHttpClient getOkHttpClientInstance() {
        if (okhttpClient == null) {
            synchronized (okhttpLock) {
                if (okhttpClient == null) {
                    okhttpClient = new OkHttpClient.Builder()
                            .dns(s -> {
                                ArrayList<InetAddress> ret = new ArrayList<>();
                                FileLog.d("okhttpWS: resolving: " + s);
                                if (StringUtils.isNotBlank(NekoConfig.customPublicProxyIP.String())) {
                                    ret.add(InetAddress.getByName(NekoConfig.customPublicProxyIP.String()));
                                } else {
                                    ret.addAll(DnsFactory.lookup(s));
                                }
                                FileLog.d("okhttpWS: resolved: " + ret);
                                return ret;
                            })
                            .build();
                }
            }
        }
        return okhttpClient;
    }

    private void connectToServer(String wsHost) {
        this.wsHost = wsHost;
        FileLog.e(new Exception("WS: Connect To Server"));
        getOkHttpClientInstance()
                .newWebSocket(new Request.Builder()
                        .url((bean.getTls() ? "wss://" : "ws://") + wsHost + "/api")
                        .build(), new WebSocketListener() {
                    @Override
                    public void onOpen(@NotNull okhttp3.WebSocket webSocket, @NotNull Response response) {
                        WsProxyHandler.this.wsSocket = webSocket;
                        wsStatus.set(STATUS_OPENED);
                        connecting.countDown();
                    }

                    @Override
                    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                        FileLog.e("[" + wsHost + "] Failure:" + t);
                        wsStatus.set(STATUS_FAILED);
                        connecting.countDown();
                    }

                    @Override
                    public void onMessage(@NotNull okhttp3.WebSocket webSocket, @NotNull ByteString bytes) {
                        FileLog.d("[" + wsHost + "] Received " + bytes.size() + " bytes from ws");
                        try {
                            if (wsStatus.get() == STATUS_OPENED && !WsProxyHandler.this.clientSocket.isClosed())
                                WsProxyHandler.this.clientOutputStream.write(bytes.toByteArray());
                        } catch (IOException e) {
                            FileLog.e(e);
                            wsStatus.set(STATUS_FAILED);
                            webSocket.cancel();
                        }
                    }

                    @Override
                    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                        FileLog.d("[" + wsHost + "] Received text: " + text);
                    }

                    @Override
                    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                        FileLog.d("[" + wsHost + "] Closed: " + code + " " + reason);
                        wsStatus.set(STATUS_CLOSED);
                    }

                    @Override
                    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                        FileLog.d("[" + wsHost + "] Closing: " + code + " " + reason);
                        wsStatus.set(STATUS_CLOSED);
                    }
                });
    }

    private static final byte[] RESP_AUTH = new byte[]{0x05, 0x00};
    private static final byte[] RESP_SUCCESS = new byte[]{0x05, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    private static final byte[] RESP_FAILED = new byte[]{0x05, 0x01, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    private void socks5Handshake() throws Exception {
        byte socksVersion = readOneByteFromClient();

        if (socksVersion != 0x05) {
            throw new Exception("Invalid socks version:" + socksVersion);
        }
        FileLog.d("Accepted socks5 requests.");

        byte authMethodsLen = readOneByteFromClient();
        boolean isNoAuthSupport = false;
        for (int i = 0; i < authMethodsLen; i++) {
            byte authMethod = readOneByteFromClient();
            if (authMethod == 0x00)
                isNoAuthSupport = true;
        }
        if (!isNoAuthSupport) throw new Exception("NO_AUTH is not supported from client.");

        this.clientOutputStream.write(RESP_AUTH);
        this.clientOutputStream.flush();

        byte[] cmds = readBytesExactly(4);
        // cmds[0] -> VER
        // cmds[1] -> CMD
        // cmds[2] -> RSV
        // cmds[3] -> ADDR_TYPE
        if (cmds[0] != 0x05 || cmds[1] != 0x01 || cmds[2] != 0x00)
            throw new Exception("invalid socks5 cmds " + Arrays.toString(cmds));
        int addrType = cmds[3];
        String address;
        if (addrType == 0x01) { // ipv4
            address = InetAddress.getByAddress(readBytesExactly(4)).getHostAddress();
        } else if (addrType == 0x04) { // ipv6
            address = Inet6Address.getByAddress(readBytesExactly(16)).getHostAddress();
        } else { // not supported: domain
            throw new Exception("invalid addr type: " + addrType);
        }
        readBytesExactly(2); // read out port

        String wsHost = getWsHost(address);
        connectToServer(wsHost);

        connecting.await();

        if (wsStatus.get() == STATUS_OPENED) {
            this.clientOutputStream.write(RESP_SUCCESS);
            this.clientOutputStream.flush();
        } else {
            this.clientOutputStream.write(RESP_FAILED);
            this.clientOutputStream.flush();
            throw new Exception("websocket connect failed");
        }
        // just set status byte and ignore bnd.addr and bnd.port in RFC1928, since Telegram Android ignores it:
        // proxyAuthState == 6 in tgnet/ConnectionSocket.cpp
    }

    private String getWsHost(String address) throws Exception {
        Integer dcNumber = Tcp2wsServer.mapper.get(address);
        for (int i = 1; dcNumber == null && i < 4; i++) {
            dcNumber = Tcp2wsServer.mapper.get(address.substring(0, address.length() - i));
        }
        if (dcNumber == null)
            throw new Exception("no matched dc: " + address);
        if (dcNumber >= bean.getPayload().size())
            throw new Exception("invalid dc number & payload: " + dcNumber);
        String serverPrefix = bean.getPayload().get(dcNumber - 1);
        String wsHost = serverPrefix + "." + this.bean.getServer();
        FileLog.d("socks5 dest address: " + address + ", target ws host " + wsHost);
        return wsHost;
    }

    private byte readOneByteFromClient() throws Exception {
        return (byte) clientInputStream.read();
    }

    private byte[] readBytesExactly(int len) throws Exception {
        byte[] ret = new byte[len];
        int alreadyRead = 0;
        while (alreadyRead < len) {
            int read = this.clientInputStream.read(ret, alreadyRead, len - alreadyRead);
            alreadyRead += read;
        }
        return ret;
    }

}
