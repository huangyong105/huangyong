package edu.dbke.socket.cp;

/**
 * long packet，用于所有通用的long类型的数据发送，flex 不支持long，和flex相关的避免使用
 * @author huitang
 *
 */
public class LongPacket extends Packet<LongPacket> {
	public long dataLong;//数据

	@Override
	protected void writeData() {
		data.putLong(dataLong);
	}

	@Override
	protected void readData() {
		dataLong = data.getLong();
	}

	public LongPacket() {
	}

	public LongPacket(short type) {
	}

	public LongPacket(short type, long dataLong) {
		this.type = type;
		this.dataLong = dataLong;
	}

}
