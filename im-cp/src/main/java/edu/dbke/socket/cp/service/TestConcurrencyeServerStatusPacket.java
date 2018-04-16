package edu.dbke.socket.cp.service;

import edu.dbke.socket.cp.Packet;
import edu.dbke.socket.cp.ProtocolType;

/**
 * 并发测试服务器状态查询
 */
public class TestConcurrencyeServerStatusPacket extends Packet<TestConcurrencyeServerStatusPacket> {
	public long dataCount;//收到数据条数
	public int socketSize;//所有在线socket数量
	public int thisSocketSize;//当前服务在线socket数量
	public long getTime;//获取时间

	@Override
	protected void writeData() {
		data.putLong(dataCount);
		data.putInt(socketSize);
		data.putInt(thisSocketSize);
		data.putLong(getTime);//系统时间
	}

	@Override
	protected void readData() {
		dataCount = data.getLong();
		socketSize = data.getInt();
		thisSocketSize = data.getInt();
		getTime = data.getLong();
	}

	public TestConcurrencyeServerStatusPacket() {
		this.type = ProtocolType.SERVER_CONCURRENCY_TEST_COUNT;
	}

	public TestConcurrencyeServerStatusPacket(long dataCount, int socketSize, int thisSocketSize) {
		this.type = ProtocolType.SERVER_CONCURRENCY_TEST_COUNT;
		this.dataCount = dataCount;
		this.socketSize = socketSize;
		this.thisSocketSize = thisSocketSize;
	}

}
