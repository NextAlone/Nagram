package tw.nekomimi.nekogram.proxy.tcp2ws;

import org.telegram.messenger.FileLog;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import tw.nekomimi.nekogram.proxy.WsLoader;

public class Tcp2wsServer extends Thread {

    public WsLoader.Bean bean;
    public int port;

    public Tcp2wsServer(WsLoader.Bean bean, int port) {
        this.bean = bean;
        this.port = port;
    }

    public static final HashMap<String, Integer> mapper = new HashMap<>();

    static {
        mapper.put("149.154.175.5", 1);
        mapper.put("95.161.76.100", 2);
        mapper.put("149.154.175.100", 3);
        mapper.put("149.154.167.91", 4);
        mapper.put("149.154.167.92", 4);
        mapper.put("149.154.171.5", 5);
        mapper.put("2001:b28:f23d:f001:0000:0000:0000:000a", 1);
        mapper.put("2001:67c:4e8:f002:0000:0000:0000:000a", 2);
        mapper.put("2001:b28:f23d:f003:0000:0000:0000:000a", 3);
        mapper.put("2001:67c:4e8:f004:0000:0000:0000:000a", 4);
        mapper.put("2001:b28:f23f:f005:0000:0000:0000:000a", 5);
        mapper.put("149.154.161.144", 2);
        mapper.put("149.154.167.", 2);
        mapper.put("149.154.175.1", 3);
        mapper.put("91.108.4.", 4);
        mapper.put("149.154.164.", 4);
        mapper.put("149.154.165.", 4);
        mapper.put("149.154.166.", 4);
        mapper.put("91.108.56.", 5);
        mapper.put("2001:b28:f23d:f001:0000:0000:0000:000d", 1);
        mapper.put("2001:67c:4e8:f002:0000:0000:0000:000d", 2);
        mapper.put("2001:b28:f23d:f003:0000:0000:0000:000d", 3);
        mapper.put("2001:67c:4e8:f004:0000:0000:0000:000d", 4);
        mapper.put("2001:b28:f23f:f005:0000:0000:0000:000d", 5);
        mapper.put("149.154.175.40", 6);
        mapper.put("149.154.167.40", 7);
        mapper.put("149.154.175.117", 8);
        mapper.put("2001:b28:f23d:f001:0000:0000:0000:000e", 6);
        mapper.put("2001:67c:4e8:f002:0000:0000:0000:000e", 7);
        mapper.put("2001:b28:f23d:f003:0000:0000:0000:000e", 8);
    }

    @Override
    public void run() {
        FileLog.d("SOCKS server started...");
        try {
            handleClients(port);
            FileLog.d("SOCKS server stopped...");
        } catch (IOException e) {
            FileLog.d("SOCKS server crashed...");
            interrupt();
        }
    }

    protected void handleClients(int port) throws IOException {
        final ServerSocket listenSocket = new ServerSocket(port);
        listenSocket.setSoTimeout(SocksConstants.LISTEN_TIMEOUT);
        Tcp2wsServer.this.port = listenSocket.getLocalPort();
        FileLog.d("SOCKS server listening at port: " + listenSocket.getLocalPort());

        while (isAlive() && !isInterrupted()) {
            handleNextClient(listenSocket);
        }

        try {
            listenSocket.close();
        } catch (IOException e) {
            // ignore
        }
    }

    private void handleNextClient(ServerSocket listenSocket) {
        try {
            final Socket clientSocket = listenSocket.accept();
            clientSocket.setSoTimeout(SocksConstants.DEFAULT_SERVER_TIMEOUT);
            FileLog.d("Connection from : " + Utils.getSocketInfo(clientSocket));
            new Thread(new ProxyHandler(clientSocket, mapper, bean)).start();
        } catch (InterruptedIOException e) {
            //	This exception is thrown when accept timeout is expired
        } catch (Exception e) {
            FileLog.e(e.getMessage(), e);
        }
    }
}