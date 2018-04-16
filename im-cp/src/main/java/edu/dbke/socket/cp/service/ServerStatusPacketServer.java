package edu.dbke.socket.cp.service;

import edu.dbke.socket.cp.Packet;
import edu.dbke.socket.cp.ProtocolType;

/**
 * 服务器状态
 * @author huitang
 *
 */
public class ServerStatusPacketServer extends Packet<ServerStatusPacketServer> {
	public int waitQueueSize;//待处理数据队列
	public int socketSize;//在线socket数量
	public long serverUpTime;//服务启动时间
	public long receiveCount;//接收数据包计数
	public long receiveSize;//接收数据包总长度
	public long sendCount;//发送数据包计数
	public long sendSize;//发送数据包总长度

	@Override
	protected void writeData() {
		data.putInt(socketSize);
		data.putInt(waitQueueSize);
		data.putLong(serverUpTime);
		data.putLong(receiveCount);
		data.putLong(receiveSize);
		data.putLong(sendCount);
		data.putLong(sendSize);
	}

	@Override
	protected void readData() {
		socketSize = data.getInt();
		waitQueueSize = data.getInt();
		serverUpTime = data.getLong();
		receiveCount = data.getLong();
		receiveSize = data.getLong();
		sendCount = data.getLong();
		sendSize = data.getLong();
	}

	public ServerStatusPacketServer() {
		this.type = ProtocolType.SERVER_STATUS_QUERY;
	}

	public ServerStatusPacketServer(int waitQueueSize, int socketSize) {
		this.waitQueueSize = waitQueueSize;
		this.socketSize = socketSize;
		this.type = ProtocolType.SERVER_STATUS_QUERY;
	}

}
