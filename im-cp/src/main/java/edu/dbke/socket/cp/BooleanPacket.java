package edu.dbke.socket.cp;

import edu.dbke.socket.cp.util.ByteUtil;

/**
 * boolean packet，用于所有通用的boolean类型的数据发送
 * @author huitang
 *
 */
public class BooleanPacket extends Packet<BooleanPacket> {
	public boolean dataBoolean;//数据

	@Override
	protected void writeData() {
		ByteUtil.writeBoolean(data, dataBoolean);
	}

	@Override
	protected void readData() {
		dataBoolean = ByteUtil.readBoolean(data);
	}

	public BooleanPacket() {
	}

	public BooleanPacket(short type) {
		this.type = type;
	}

	public BooleanPacket(short type, boolean databoolean) {
		this.type = type;
		this.dataBoolean = databoolean;
	}
}
