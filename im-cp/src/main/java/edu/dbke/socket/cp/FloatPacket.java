package edu.dbke.socket.cp;

/**
 * float packet，用于所有通用的float类型的数据发送
 * @author huitang
 *
 */
public class FloatPacket extends Packet<FloatPacket> {
	public float dataFloat;//数据

	@Override
	protected void writeData() {
		data.putFloat(dataFloat);
	}

	@Override
	protected void readData() {
		this.dataFloat = data.getFloat();
	}

	public FloatPacket() {
	}

	public FloatPacket(short type) {
		this.type = type;
	}

	public FloatPacket(short type, float data) {
		this.type = type;
		this.dataFloat = data;
	}
}
