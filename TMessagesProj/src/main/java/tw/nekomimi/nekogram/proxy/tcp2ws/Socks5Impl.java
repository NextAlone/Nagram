package tw.nekomimi.nekogram.proxy.tcp2ws;

import androidx.annotation.Nullable;

import org.telegram.messenger.FileLog;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Socks5Impl extends Socks4Impl {

    private static final int[] ADDR_Size = {
            -1, //'00' No such AType
            4, //'01' IP v4 - 4Bytes
            -1, //'02' No such AType
            -1, //'03' First Byte is Len
            16  //'04' IP v6 - 16bytes
    };
    private static final byte[] SRE_REFUSE = {(byte) 0x05, (byte) 0xFF};
    private static final byte[] SRE_ACCEPT = {(byte) 0x05, (byte) 0x00};
    private static final int MAX_ADDR_LEN = 255;
    private byte ADDRESS_TYPE;
    private DatagramSocket DGSocket = null;
    private DatagramPacket DGPack = null;
    private InetAddress UDP_IA = null;
    private int UDP_port = 0;

    Socks5Impl(ProxyHandler Parent) {
        super(Parent);
        DST_Addr = new byte[MAX_ADDR_LEN];
    }

    @SuppressWarnings("OctalInteger")
    public byte getSuccessCode() {
        return 00;
    }

    @SuppressWarnings("OctalInteger")
    public byte getFailCode() {
        return 04;
    }

    @Nullable
    public InetAddress calcInetAddress(byte AType, byte[] addr) {
        InetAddress IA;

        switch (AType) {
            // Version IP 4
            case 0x01:
                IA = Utils.calcInetAddress(addr);
                break;
            // Version IP DOMAIN NAME
            case 0x03:
                if (addr[0] <= 0) {
                    FileLog.e("SOCKS 5 - calcInetAddress() : BAD IP in command - size : " + addr[0]);
                    return null;
                }
                StringBuilder sIA = new StringBuilder();
                for (int i = 1; i <= addr[0]; i++) {
                    sIA.append((char) addr[i]);
                }
                try {
                    IA = InetAddress.getByName(sIA.toString());
                } catch (UnknownHostException e) {
                    return null;
                }
                break;
            default:
                return null;
        }
        return IA;
    }

    public boolean isInvalidAddress() {
        m_ServerIP = calcInetAddress(ADDRESS_TYPE, DST_Addr);
        m_nServerPort = Utils.calcPort(DST_Port[0], DST_Port[1]);

        m_ClientIP = m_Parent.m_ClientSocket.getInetAddress();
        m_nClientPort = m_Parent.m_ClientSocket.getPort();

        return !((m_ServerIP != null) && (m_nServerPort >= 0));
    }

    public void authenticate(byte SOCKS_Ver) throws Exception {
        super.authenticate(SOCKS_Ver); // Sets SOCKS Version...

        if (SOCKS_Version == SocksConstants.SOCKS5_Version) {
            if (!checkAuthentication()) {// It reads whole Cli Request
                refuseAuthentication("SOCKS 5 - Not Supported Authentication!");
                throw new Exception("SOCKS 5 - Not Supported Authentication.");
            }
            acceptAuthentication();
        }// if( SOCKS_Version...
        else {
            refuseAuthentication("Incorrect SOCKS version : " + SOCKS_Version);
            throw new Exception("Not Supported SOCKS Version -'" +
                    SOCKS_Version + "'");
        }
    }

    public void refuseAuthentication(String msg) {
        FileLog.d("SOCKS 5 - Refuse Authentication: '" + msg + "'");
        m_Parent.sendToClient(SRE_REFUSE);
    }


    public void acceptAuthentication() {
        FileLog.d("SOCKS 5 - Accepts Auth. method 'NO_AUTH'");
        byte[] tSRE_Accept = SRE_ACCEPT;
        tSRE_Accept[0] = SOCKS_Version;
        m_Parent.sendToClient(tSRE_Accept);
    }


    public boolean checkAuthentication() {
        final byte Methods_Num = getByte();
        final StringBuilder Methods = new StringBuilder();

        for (int i = 0; i < Methods_Num; i++) {
            Methods.append(",-").append(getByte()).append('-');
        }

        return ((Methods.indexOf("-0-") != -1) || (Methods.indexOf("-00-") != -1));
    }

    public void getClientCommand() throws Exception {
        SOCKS_Version = getByte();
        socksCommand = getByte();
        /*byte RSV =*/
        getByte(); // Reserved. Must be'00'
        ADDRESS_TYPE = getByte();

        int Addr_Len = ADDR_Size[ADDRESS_TYPE];
        DST_Addr[0] = getByte();
        if (ADDRESS_TYPE == 0x03) {
            Addr_Len = DST_Addr[0] + 1;
        }

        for (int i = 1; i < Addr_Len; i++) {
            DST_Addr[i] = getByte();
        }
        DST_Port[0] = getByte();
        DST_Port[1] = getByte();

        if (SOCKS_Version != SocksConstants.SOCKS5_Version) {
            FileLog.d("SOCKS 5 - Incorrect SOCKS Version of Command: " +
                    SOCKS_Version);
            refuseCommand((byte) 0xFF);
            throw new Exception("Incorrect SOCKS Version of Command: " +
                    SOCKS_Version);
        }

        if ((socksCommand < SocksConstants.SC_CONNECT) || (socksCommand > SocksConstants.SC_UDP)) {
            FileLog.e("SOCKS 5 - GetClientCommand() - Unsupported Command : \"" + commName(socksCommand) + "\"");
            refuseCommand((byte) 0x07);
            throw new Exception("SOCKS 5 - Unsupported Command: \"" + socksCommand + "\"");
        }

        if (ADDRESS_TYPE == 0x04) {
            FileLog.e("SOCKS 5 - GetClientCommand() - Unsupported Address Type - IP v6");
            refuseCommand((byte) 0x08);
            throw new Exception("Unsupported Address Type - IP v6");
        }

        if ((ADDRESS_TYPE >= 0x04) || (ADDRESS_TYPE <= 0)) {
            FileLog.e("SOCKS 5 - GetClientCommand() - Unsupported Address Type: " + ADDRESS_TYPE);
            refuseCommand((byte) 0x08);
            throw new Exception("SOCKS 5 - Unsupported Address Type: " + ADDRESS_TYPE);
        }

        if (isInvalidAddress()) {  // Gets the IP Address
            refuseCommand((byte) 0x04); // Host Not Exists...
            throw new Exception("SOCKS 5 - Unknown Host/IP address '" + m_ServerIP.toString() + "'");
        }

        FileLog.d("SOCKS 5 - Accepted SOCKS5 Command: \"" + commName(socksCommand) + "\"");
    }

    public void replyCommand(byte replyCode) {
        FileLog.d("SOCKS 5 - Reply to Client \"" + replyName(replyCode) + "\"");

        final int pt;

        byte[] REPLY = new byte[10];
        byte[] IP = new byte[4];

        if (m_Parent.m_ServerSocketRaw != null) {
            pt = m_Parent.m_ServerSocketRaw.getLocalPort();
        } else {
            IP[0] = 0;
            IP[1] = 0;
            IP[2] = 0;
            IP[3] = 0;
            pt = 0;
        }

        formGenericReply(replyCode, pt, REPLY, IP);

        m_Parent.sendToClient(REPLY);// BND.PORT
    }

    private void formGenericReply(byte replyCode, int pt, byte[] REPLY, byte[] IP) {
        REPLY[0] = SocksConstants.SOCKS5_Version;
        REPLY[1] = replyCode;
        REPLY[2] = 0x00;        // Reserved	'00'
        REPLY[3] = 0x01;        // DOMAIN NAME Address Type IP v4
        REPLY[4] = IP[0];
        REPLY[5] = IP[1];
        REPLY[6] = IP[2];
        REPLY[7] = IP[3];
        REPLY[8] = (byte) ((pt & 0xFF00) >> 8);// Port High
        REPLY[9] = (byte) (pt & 0x00FF);      // Port Low
    }

}