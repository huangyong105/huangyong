package edu.dbke.socket.cp.service;

import java.io.File;
import java.nio.ByteBuffer;

import edu.dbke.socket.cp.Packet;
import edu.dbke.socket.cp.ProtocolType;

/**
 * 大数据开始协议
 * @author huitang
 */
public class BigDataStartPacket extends Packet<BigDataStartPacket> {
	public int dataId;//数据包编号
	public int offset;//数据偏移量，首次发为总数据包大小 ， 服务器断点回发时候，为续传位置，数据byte为0；
	public byte[] bytesData;//数据，首次对应数据处理协议包

	//以下不成变量不参与数据发送
	private File tempFile;//临时文件
	private long totalSize;//整个数据包的大小，对应offset
	private ByteBuffer handlerPacket;//数据业务处理包

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

	/**
	 * 默认构造函数
	 */
	public BigDataStartPacket() {
		this.type = ProtocolType.SERVER_BIGDATA_START;
	}

	public BigDataStartPacket(int dataId, int offset, byte[] bytesData) {
		this.dataId = dataId;
		this.offset = offset;
		this.bytesData = bytesData;
		this.type = ProtocolType.SERVER_BIGDATA_START;
	}

	public File getTempFile() {
		return tempFile;
	}

	public void setTempFile(File tempFile) {
		this.tempFile = tempFile;
	}

	public long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	public ByteBuffer getHandlerPacket() {
		return handlerPacket;
	}

	public void setHandlerPacket(ByteBuffer handlerPacket) {
		this.handlerPacket = handlerPacket;
	}
}
