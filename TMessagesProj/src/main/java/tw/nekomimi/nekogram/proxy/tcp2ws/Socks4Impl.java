package tw.nekomimi.nekogram.proxy.tcp2ws;

import org.jetbrains.annotations.NotNull;
import org.telegram.messenger.FileLog;

import java.net.InetAddress;

public class Socks4Impl {

    final ProxyHandler m_Parent;
    final byte[] DST_Port = new byte[2];
    byte[] DST_Addr = new byte[4];
    byte SOCKS_Version = 0;
    byte socksCommand;

    InetAddress m_ServerIP = null;
    int m_nServerPort = 0;
    InetAddress m_ClientIP = null;
    int m_nClientPort = 0;

    Socks4Impl(ProxyHandler Parent) {
        m_Parent = Parent;
    }

    public byte getSuccessCode() {
        return 90;
    }

    public byte getFailCode() {
        return 91;
    }

    @NotNull
    public String commName(byte code) {
        switch (code) {
            case 0x01:
                return "CONNECT";
            case 0x02:
                return "BIND";
            case 0x03:
                return "UDP Association";
            default:
                return "Unknown Command";
        }
    }

    @NotNull
    public String replyName(byte code) {
        switch (code) {
            case 0:
                return "SUCCESS";
            case 1:
                return "General SOCKS Server failure";
            case 2:
                return "Connection not allowed by ruleset";
            case 3:
                return "Network Unreachable";
            case 4:
                return "HOST Unreachable";
            case 5:
                return "Connection Refused";
            case 6:
                return "TTL Expired";
            case 7:
                return "Command not supported";
            case 8:
                return "Address Type not Supported";
            case 9:
                return "to 0xFF UnAssigned";
            case 90:
                return "Request GRANTED";
            case 91:
                return "Request REJECTED or FAILED";
            case 92:
                return "Request REJECTED - SOCKS server can't connect to Identd on the client";
            case 93:
                return "Request REJECTED - Client and Identd report diff user-ID";
            default:
                return "Unknown Command";
        }
    }

    public boolean isInvalidAddress() {
        // IP v4 Address Type
        m_ServerIP = Utils.calcInetAddress(DST_Addr);
        m_nServerPort = Utils.calcPort(DST_Port[0], DST_Port[1]);

        m_ClientIP = m_Parent.m_ClientSocket.getInetAddress();
        m_nClientPort = m_Parent.m_ClientSocket.getPort();

        return m_ServerIP == null || m_nServerPort < 0;
    }

    protected byte getByte() {
        try {
            return m_Parent.getByteFromClient();
        } catch (Exception e) {
            return 0;
        }
    }

    public void authenticate(byte SOCKS_Ver) throws Exception {
        SOCKS_Version = SOCKS_Ver;
    }

    public void getClientCommand() throws Exception {
        // Version was get in method Authenticate()
        socksCommand = getByte();

        DST_Port[0] = getByte();
        DST_Port[1] = getByte();

        for (int i = 0; i < 4; i++) {
            DST_Addr[i] = getByte();
        }

        //noinspection StatementWithEmptyBody
        while (getByte() != 0x00) {
            // keep reading bytes
        }

        if ((socksCommand < SocksConstants.SC_CONNECT) || (socksCommand > SocksConstants.SC_BIND)) {
            refuseCommand((byte) 91);
            throw new Exception("Socks 4 - Unsupported Command : " + commName(socksCommand));
        }

        if (isInvalidAddress()) {  // Gets the IP Address
            refuseCommand((byte) 92);    // Host Not Exists...
            throw new Exception("Socks 4 - Unknown Host/IP address '" + m_ServerIP.toString());
        }

        FileLog.d("Accepted SOCKS 4 Command: \"" + commName(socksCommand) + "\"");
    }

    public void replyCommand(byte ReplyCode) {
        FileLog.d("Socks 4 reply: \"" + replyName(ReplyCode) + "\"");

        byte[] REPLY = new byte[8];
        REPLY[0] = 0;
        REPLY[1] = ReplyCode;
        REPLY[2] = DST_Port[0];
        REPLY[3] = DST_Port[1];
        REPLY[4] = DST_Addr[0];
        REPLY[5] = DST_Addr[1];
        REPLY[6] = DST_Addr[2];
        REPLY[7] = DST_Addr[3];

        m_Parent.sendToClient(REPLY);
    }

    protected void refuseCommand(byte errorCode) {
        FileLog.d("Socks 4 - Refuse Command: \"" + replyName(errorCode) + "\"");
        replyCommand(errorCode);
    }

    public void connect() throws Exception {
        FileLog.d("Connecting...");
        //	Connect to the Remote Host
        m_Parent.connectToServer(m_ServerIP.getHostAddress(), () -> replyCommand(getSuccessCode()), () -> refuseCommand(getFailCode()));
    }

}