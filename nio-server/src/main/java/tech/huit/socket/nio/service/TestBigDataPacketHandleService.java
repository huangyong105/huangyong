package tech.huit.socket.nio.service;

import java.nio.channels.SocketChannel;

import edu.dbke.socket.cp.Packet;
import edu.dbke.socket.cp.ProtocolType;
import edu.dbke.socket.cp.service.TestBigDataHandlerPacket;

/**
 * 大数据处理测试类
 * @author huitang
 */
public class TestBigDataPacketHandleService extends BaseServiceSupport {

	@Override
	public void registerPacket() {
		disposePacket.add(TestBigDataHandlerPacket.class);
	}

	@Override
	public void doTask(SocketChannel socket, Packet<?> packet) {
		try {
			switch (packet.type) {
			case ProtocolType.BIGDATA_HANDLE_TEST:
				TestBigDataHandlerPacket bp = (TestBigDataHandlerPacket) packet;
				logger.info(bp.toString());
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
