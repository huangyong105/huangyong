package edu.dbke.socket.cp;

import edu.dbke.socket.cp.util.ByteUtil;

/**
 * 服务器操作交互通用类型的包
 * @author huitang
 *
 */
public class OptStringInfoPacket extends Packet<OptStringInfoPacket> {
	public String optId;//操作ID
	public String optResult;//操作结果
	public String optInfo;//操作信息

	@Override
	protected void writeData() {
		ByteUtil.write256String(data, optId);
		ByteUtil.writeShortString(data, optResult);
		ByteUtil.writeShortString(data, optInfo);
	}

	@Override
	protected void readData() {
		optId = ByteUtil.read256String(data);
		optResult = ByteUtil.readShortString(data);
		optInfo = ByteUtil.readShortString(data);
	}

	public OptStringInfoPacket() {
	}

	public OptStringInfoPacket(short type) {
		this.type = type;
	}

	public OptStringInfoPacket(short type, String optId) {
		this.type = type;
		this.optId = optId;
	}

	public OptStringInfoPacket(short type, String optId, String optResult) {
		this.type = type;
		this.optId = optId;
		this.optResult = optResult;
	}

	public OptStringInfoPacket(short type, String optId, String optResult, String optInfo) {
		this.type = type;
		this.optId = optId;
		this.optResult = optResult;
		this.optInfo = optInfo;
	}
}