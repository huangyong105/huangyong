package edu.dbke.socket.cp;

/**
 * short packet，用于所有通用的short类型的数据发送
 * @author huitang
 *
 */
public class ShortPacket extends Packet<ShortPacket> {
	public short dataShort;//数据

	@Override
	protected void writeData() {
		super.data.putShort(dataShort);
	}

	@Override
	protected void readData() {
		this.dataShort = super.data.getShort();
	}

	public ShortPacket() {
	}

	public ShortPacket(short type) {
		this.type = type;
	}

	public ShortPacket(short type, short dataShort) {
		this.type = type;
		this.dataShort = dataShort;
	}
}
