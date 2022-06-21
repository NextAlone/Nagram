package tw.nekomimi.nekogram.proxy.tcp2ws;

import org.telegram.messenger.FileLog;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Tcp2wsServer extends Thread {

    public final WsLoader.Bean bean;
    public final int port;

    public Tcp2wsServer(WsLoader.Bean bean, int port) {
        this.bean = bean;
        this.port = port;
    }

    public static final Map<String, Integer> mapper = Collections.unmodifiableMap(new HashMap<String, Integer>() {{
        put("149.154.175.5", 1);
        put("95.161.76.100", 2);
        put("149.154.175.100", 3);
        put("149.154.167.91", 4);
        put("149.154.167.92", 4);
        put("149.154.171.5", 5);
        put("2001:b28:f23d:f001:0000:0000:0000:000a", 1);
        put("2001:67c:4e8:f002:0000:0000:0000:000a", 2);
        put("2001:b28:f23d:f003:0000:0000:0000:000a", 3);
        put("2001:67c:4e8:f004:0000:0000:0000:000a", 4);
        put("2001:b28:f23f:f005:0000:0000:0000:000a", 5);
        put("149.154.161.144", 2);
        put("149.154.167.", 2);
        put("149.154.175.1", 3);
        put("91.108.4.", 4);
        put("149.154.164.", 4);
        put("149.154.165.", 4);
        put("149.154.166.", 4);
        put("91.108.56.", 5);
        put("2001:b28:f23d:f001:0000:0000:0000:000d", 1);
        put("2001:67c:4e8:f002:0000:0000:0000:000d", 2);
        put("2001:b28:f23d:f003:0000:0000:0000:000d", 3);
        put("2001:67c:4e8:f004:0000:0000:0000:000d", 4);
        put("2001:b28:f23f:f005:0000:0000:0000:000d", 5);
        put("149.154.175.40", 6);
        put("149.154.167.40", 7);
        put("149.154.175.117", 8);
        put("2001:b28:f23d:f001:0000:0000:0000:000e", 6);
        put("2001:67c:4e8:f002:0000:0000:0000:000e", 7);
        put("2001:b28:f23d:f003:0000:0000:0000:000e", 8);
    }});

    @Override
    public void run() {
        FileLog.d("SOCKS server started...");
        try {
            handleClients(port);
            FileLog.d("SOCKS server stopped...");
        } catch (Exception e) {
            FileLog.d("SOCKS server crashed...");
            FileLog.e(e);
            interrupt();
        }
    }

    protected void handleClients(int port) throws Exception {
        final ServerSocket listenSocket = new ServerSocket(port);
        listenSocket.setSoTimeout(2000);
        FileLog.d("SOCKS server listening at port: " + listenSocket.getLocalPort());

        while (isAlive() && !isInterrupted()) {
            try {
                final Socket clientSocket = listenSocket.accept();
                FileLog.d("Connection from : " + clientSocket.getRemoteSocketAddress().toString());
                new WsProxyHandler(clientSocket, bean).start();
            } catch (InterruptedIOException e) {
                //	This exception is thrown when accept timeout is expired
            } catch (Exception e) {
                FileLog.e(e.getMessage(), e);
            }
        }
        try {
            listenSocket.close();
        } catch (IOException e) {
            FileLog.e(e);
        }
    }
}