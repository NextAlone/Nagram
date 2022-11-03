package tw.nekomimi.nekogram.proxy.tcp2ws;

import android.annotation.SuppressLint;

import com.neovisionaries.ws.client.ThreadType;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketListener;
import com.neovisionaries.ws.client.WebSocketState;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.common.value.qual.IntVal;
import org.telegram.messenger.FileLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;

import cn.hutool.http.ssl.AndroidSupportSSLFactory;
import cn.hutool.http.ssl.CustomProtocolsSSLFactory;
import tw.nekomimi.nekogram.NekoConfig;
import tw.nekomimi.nekogram.utils.DnsFactory;

public class WsProxyHandler extends Thread {

    private InputStream clientInputStream = null;
    private OutputStream clientOutputStream = null;

    private final WsLoader.Bean bean;
    private Socket clientSocket;
    private WebSocket webSocket = null;
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
            while (clientSocket != null && webSocket != null && wsStatus.get() == STATUS_OPENED && !clientSocket.isClosed() && !clientSocket.isInputShutdown()) {
                int readLen = 0;
                try {
                    readLen = this.clientInputStream.read(buffer);
                } catch (SocketTimeoutException ex) {
                    if (wsStatus.get() != STATUS_OPENED)
                        throw new Exception(String.format("[%s] timeout and ws closed", wsHost));
                    continue;
                }
                FileLog.d(String.format("[%s] read %d from local", wsHost, readLen));
                if (readLen == -1) throw new Exception(String.format("[%s] socks closed", wsHost));
                ;
                if (wsStatus.get() != STATUS_OPENED)
                    throw new Exception(String.format("[%s] ws closed when trying to write", wsHost));
                ;
                this.webSocket.sendBinary(Arrays.copyOf(buffer, readLen));
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
        try {
            if (webSocket != null) {
                webSocket.sendClose();
            }
        } catch (Exception e) {
            // ignore
        }

        clientSocket = null;
        webSocket = null;
    }

    private void connectToServer(String wsHost) throws Exception {
        this.wsHost = wsHost;
        WebSocketFactory factory = new WebSocketFactory().setConnectionTimeout(5000);
        webSocket = factory.createSocket((bean.getTls() ? "wss://" : "ws://") + wsHost + "/api");
        webSocket.addListener(new WebSocketAdapter() {
            @Override
            public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
                WsProxyHandler.this.clientOutputStream.write(binary);
            }

            @Override
            public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
                FileLog.e(cause);
                wsStatus.set(STATUS_FAILED);
            }

            @Override
            public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
                FileLog.d(String.format("[%s] WS connect failed: %s", wsHost, exception.toString()));
                wsStatus.set(STATUS_FAILED);
                connecting.countDown();
            }

            @Override
            public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                FileLog.d(String.format("[%s] WS connected", wsHost));
                wsStatus.set(STATUS_OPENED);
                connecting.countDown();
            }
        });
        webSocket.addProtocol("binary");
        webSocket.connect();
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
