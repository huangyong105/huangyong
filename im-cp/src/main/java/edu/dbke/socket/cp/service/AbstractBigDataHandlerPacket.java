package edu.dbke.socket.cp.service;

import java.nio.ByteBuffer;

import org.apache.commons.lang3.builder.ToStringBuilder;

import edu.dbke.socket.cp.Packet;

/**
 * 大数据包处理逻辑继承此类
 * @author huitang
 */
public abstract class AbstractBigDataHandlerPacket<T> extends Packet<T> {
	public int dataId;//数据包编号

	/**
	 * 发送一个对象
	 * @return 
	 */
	@Override
	final public ByteBuffer writeObject() {
		data = ByteBuffer.allocate(Short.MAX_VALUE);
		data.putInt(-1);
		data.putShort(type);
		data.putInt(dataId);
		writeData();
		data.flip();
		data.putInt(0, data.limit());
		byte[] dataCopy = new byte[data.limit()];
		System.arraycopy(data.array(), 0, dataCopy, 0, data.limit());
		return ByteBuffer.wrap(dataCopy);
	}

	/**
	 * 读取一个对象
	 */
	@Override
	@SuppressWarnings("unchecked")
	final public T readObject(ByteBuffer data) {
		data.rewind();
		this.data = data;
		this.size = data.getInt();
		this.type = data.getShort();
		this.dataId = data.getInt();
		readData();
		if (size != data.limit()) {
			throw new RuntimeException("packet data error!expect " + size + " but " + data.limit() + "received");
		}
		return (T) this;
	}

	@Override
	protected abstract void writeData();

	@Override
	protected abstract void readData();

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
