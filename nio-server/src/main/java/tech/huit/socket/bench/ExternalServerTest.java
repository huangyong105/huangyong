package tech.huit.socket.bench;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

import edu.dbke.socket.cp.Empty;
import edu.dbke.socket.cp.ProtocolType;
import edu.dbke.socket.cp.StringPacket;
import edu.dbke.socket.cp.service.External2ServerPacket;
import edu.dbke.socket.cp.util.ByteUtil;

/**
 * 外部数据处理服务
 *
 * @author huitang
 */
public class ExternalServerTest {
	//	static String serverIp = "10.254.244.39";
	//	static String serverIp = "222.18.159.2";
	//	static String serverIp = "222.18.159.5";
	//		static String serverIp = "222.18.159.6";
	//			static String serverIp = "222.18.159.8";
	//	static String serverIp = "222.18.159.13";
	//	static String serverIp = "222.18.159.15";
	//			static String serverIp = "222.18.159.19";
	//	static String serverIp = "210.41.225.46";
	//	static String serverIp = "210.41.225.52";

		static String serverIp = "localhost";
//	static String serverIp = "172.20.16.48";
	//	public static String serverIp = "121.199.30.91";
	public static String service = "testServer";
	public static int port = 6413;
	//	static String service = "QueueServer2";
	static String clientCloseEvent = "true";
	static String productName = ",698694574,queue";

	//	static String productName = "";

	public static void main(String[] args) {
		for (String arg : args) {
			if (arg.startsWith("host=")) {
				serverIp = arg.split("=")[1];
			} else if (arg.startsWith("port=")) {
				port = Integer.valueOf(arg.split("=")[1]);
			}
		}

		System.out.println("host:" + serverIp + " port:" + port);

		Set<String> onlineUser = new HashSet<String>();
		try {
			Socket socket = new Socket(serverIp, port);
			final OutputStream os = socket.getOutputStream();

			/*服务注册*/
			String join = service + "," + clientCloseEvent + productName;
			os.write(new StringPacket(ProtocolType.SERVER_EXTERNAL_SERVER_JOIN, join).writeByteObject());
			InputStream is = socket.getInputStream();
			StringPacket sp = new StringPacket().readObject(ByteUtil.readPacket(is,
					ProtocolType.SERVER_EXTERNAL_SERVER_JOIN));
			if (sp.dataStr.startsWith("true")) {
				System.out.println("external server join success:" + sp.dataStr);
			} else {
				System.out.println("external server join fail:" + sp.dataStr);
			}

			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						try {
							os.write(new Empty(ProtocolType.SERVER_SOCKET_CHECK).writeByteObject());
							Thread.sleep(15 * 1000);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
			/*服务注册完成*/

			while (true) {
				ByteBuffer buff = ByteUtil.readPacket(is);
				if (ProtocolType.SERVER_EXTERNAL_SERVER_DATA == buff.getShort(4)) {
					External2ServerPacket esp = new External2ServerPacket().readObject(buff);
					esp.type = ProtocolType.SERVER_EXTERNAL_CLIENT_DATA;
					//System.out.println("receive data:" + esp);
					for (int i = 0; i < 1; i++) {
						os.write(esp.writeByteObject());//收到什么响应什么
					}
				} else if (ProtocolType.SERVER_EXTERNAL_CLIENT_SCOKET_CLOSE == buff.getShort(4)) {
					sp = new StringPacket();
					System.out.println("external client socket close:" + sp.readObject(buff).dataStr);
					onlineUser.remove(sp.dataStr);

					pintOnline(onlineUser);
					System.out.println();
				} else if (ProtocolType.SERVER_EXTERNAL_CLIENT_SUBSCRIBE == buff.getShort(4)) {
					sp = sp.readObject(buff);
					onlineUser.add(sp.dataStr);
					System.out.println("receive subscribe:" + sp.dataStr);
					pintOnline(onlineUser);
				} else if (ProtocolType.SERVER_STATUS_ECHO == buff.getShort(4)) {
					System.out.println("SERVER_STATUS_ECHO");
				} else if (ProtocolType.SERVER_EXTERNAL_SERVER_KICK == buff.getShort(4)) {
					System.out.println("SERVER_EXTERNAL_SERVER_KICK");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static void pintOnline(Set<String> onlineUser) {
		System.out.print("当前在线socket：");
		for (String string : onlineUser) {
			System.out.print(string + "\t");
		}
		System.out.println();
	}
}
