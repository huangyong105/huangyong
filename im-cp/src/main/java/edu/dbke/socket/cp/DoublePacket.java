package edu.dbke.socket.cp;

/**
 * double packet，用于所有通用的double类型的数据发送
 * @author huitang
 *
 */
public class DoublePacket extends Packet<DoublePacket> {
	public double dataDouble;//数据

	@Override
	protected void writeData() {
		super.data.putDouble(dataDouble);
	}

	@Override
	protected void readData() {
		this.dataDouble = super.data.getDouble();
	}

	public DoublePacket() {
	}

	public DoublePacket(short type) {
		this.type = type;
	}

	public DoublePacket(short type, double data) {
		this.type = type;
		this.dataDouble = data;
	}
}
