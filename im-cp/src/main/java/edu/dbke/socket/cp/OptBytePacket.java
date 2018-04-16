package edu.dbke.socket.cp;

import edu.dbke.socket.cp.util.ByteUtil;

/**
 * 服务器操作交互通用类型的包
 * @author huitang
 *
 */
public class OptBytePacket extends Packet<OptBytePacket> {
	public String optId;//数据ID
	public byte optResult;//操作数据

	@Override
	protected void writeData() {
		ByteUtil.write256String(data, optId);
		data.put(optResult);
	}

	@Override
	protected void readData() {
		optId = ByteUtil.read256String(data);
		optResult = data.get();
	}

	public OptBytePacket() {
	}

	public OptBytePacket(short type) {
		this.type = type;
	}

	public OptBytePacket(short type, String optId) {
		this.type = type;
		this.optId = optId;
	}

	public OptBytePacket(short type, String optId, byte optResult) {
		this.type = type;
		this.optId = optId;
		this.optResult = optResult;
	}
}