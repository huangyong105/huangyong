package edu.dbke.socket.cp.service;

import edu.dbke.socket.cp.Packet;
import edu.dbke.socket.cp.ProtocolType;

/**
 * 协议包数据长度错误
 * @author huitang
 */
public class PacketDataErrorPacket extends Packet<PacketDataErrorPacket> {
	public int pSize;//收到的数据包长度（取值范围为0到1048590）

	@Override
	protected void writeData() {
		data.putInt(pSize);
	}

	@Override
	protected void readData() {
		pSize = data.getInt();
	}

	/**
	 * 默认构造函数
	 */
	public PacketDataErrorPacket() {
		this.type = ProtocolType.SERVER_PACKET_SIZE_ERROR;
	}

	public PacketDataErrorPacket(int pSize) {
		this.pSize = pSize;
		this.type = ProtocolType.SERVER_PACKET_SIZE_ERROR;
	}
}
