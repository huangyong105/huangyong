package edu.dbke.socket.cp;

/**
 * bytes packet，用于所有通用的bytes类型的数据发送
 * @author huitang
 *
 */
public class BytesPacket extends Packet<BytesPacket> {
	public byte[] dataBytes;//数据

	@Override
	protected void writeData() {
		super.data.put(dataBytes);
	}

	@Override
	protected void readData() {
		dataBytes = new byte[data.limit() - data.position()];
		data.get(dataBytes);
	}

	public BytesPacket() {
	}

	public BytesPacket(short type) {
		this.type = type;
	}

	public BytesPacket(short type, byte[] dataBytes) {
		this.type = type;
		this.dataBytes = dataBytes;
	}
}
