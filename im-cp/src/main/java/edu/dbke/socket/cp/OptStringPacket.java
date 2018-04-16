package edu.dbke.socket.cp;

import edu.dbke.socket.cp.util.ByteUtil;

/**
 * 服务器操作交互通用类型的包
 * @author huitang
 *
 */
public class OptStringPacket extends Packet<OptStringPacket> {
	public String optId;//操作ID
	public String optData;//操作结果

	@Override
	protected void writeData() {
		ByteUtil.write256String(data, optId);
		ByteUtil.writeShortString(data, optData);
	}

	@Override
	protected void readData() {
		optId = ByteUtil.read256String(data);
		optData = ByteUtil.readShortString(data);
	}

	public OptStringPacket() {
	}

	public OptStringPacket(short type) {
		this.type = type;
	}

	public OptStringPacket(short type, String optId) {
		this.type = type;
		this.optId = optId;
	}

	public OptStringPacket(short type, String optId, String optData) {
		this.type = type;
		this.optId = optId;
		this.optData = optData;
	}
}