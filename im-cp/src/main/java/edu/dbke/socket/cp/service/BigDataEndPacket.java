package edu.dbke.socket.cp.service;

import edu.dbke.socket.cp.Packet;
import edu.dbke.socket.cp.ProtocolType;

/**
 * 大数据传输完成协议
 * @author huitang
 */
public class BigDataEndPacket extends Packet<BigDataEndPacket> {
	public int dataId;//数据包编号

	@Override
	protected void writeData() {
		data.putInt(dataId);
	}

	@Override
	protected void readData() {
		dataId = data.getInt();
	}

	/**
	 * 默认构造函数
	 */
	public BigDataEndPacket() {
		this.type = ProtocolType.SERVER_BIGDATA_END;
	}

	public BigDataEndPacket(int dataId) {
		this.dataId = dataId;
		this.type = ProtocolType.SERVER_BIGDATA_END;
	}
}
