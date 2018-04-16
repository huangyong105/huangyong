package edu.dbke.socket.cp;

/**
 * int packet，用于所有通用的int类型的数据发送
 * @author huitang
 *
 */
public class IntPacket extends Packet<IntPacket> {
	public int dataInt;//数据

	@Override
	protected void writeData() {
		data.putInt(dataInt);
	}

	@Override
	protected void readData() {
		dataInt = data.getInt();
	}

	public IntPacket() {
	}

	public IntPacket(short type) {
		this.type = type;
	}

	public IntPacket(short type, int dataInt) {
		this.type = type;
		this.dataInt = dataInt;
	}
}
