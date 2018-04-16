package edu.dbke.socket.cp.service;

import edu.dbke.socket.cp.Packet;
import edu.dbke.socket.cp.ProtocolType;

/**
 * 大数据分块包，用于发送一段大数据的分块
 * @author huitang
 */
public class BigDataPacket extends Packet<BigDataPacket> {
	public int dataId;//数据包编号
	public int offset;//数据偏移量
	public byte[] bytesData;//数据，首次对应数据处理协议包

	@Override
	protected void writeData() {
		data.putInt(dataId);
		data.putInt(offset);
		data.put(bytesData);
	}

	@Override
	protected void readData() {
		dataId = data.getInt();
		offset = data.getInt();
		bytesData = new byte[data.limit() - data.position()];
		data.get(bytesData);
	}

	public BigDataPacket() {
		this.type = ProtocolType.SERVER_BIGDATA_DATA;
	}

	public BigDataPacket(int dataId, int offset, byte[] bytesData) {
		this.dataId = dataId;
		this.offset = offset;
		this.bytesData = bytesData;
		this.type = ProtocolType.SERVER_BIGDATA_DATA;
	}
}
