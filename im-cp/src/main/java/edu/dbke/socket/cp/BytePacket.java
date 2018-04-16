package edu.dbke.socket.cp;

/**
 * byte packet，用于所有通用的byte类型的数据发送,c#里面不支持负数的byte类型
 * @author huitang
 *
 */
public class BytePacket extends Packet<BytePacket> {
	public byte dataByte;//数据

	@Override
	protected void writeData() {
		data.put(dataByte);
	}

	@Override
	protected void readData() {
		this.dataByte = data.get();
	}

	public BytePacket() {
	}

	public BytePacket(short type) {
		this.type = type;
	}

	public BytePacket(short type, byte byteData) {
		this.type = type;
		this.dataByte = byteData;
	}

}
