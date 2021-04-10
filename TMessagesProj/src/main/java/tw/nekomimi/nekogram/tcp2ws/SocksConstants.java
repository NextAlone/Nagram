package tw.nekomimi.nekogram.tcp2ws;

public interface SocksConstants {

	// refactor
	int LISTEN_TIMEOUT = 200;
	int DEFAULT_SERVER_TIMEOUT = 200;

	int DEFAULT_BUF_SIZE = 4096;
	int DEFAULT_PROXY_TIMEOUT = 10;

	byte SOCKS5_Version = 0x05;
	byte SOCKS4_Version = 0x04;

	byte SC_CONNECT = 0x01;
	byte SC_BIND = 0x02;
	byte SC_UDP = 0x03;
}