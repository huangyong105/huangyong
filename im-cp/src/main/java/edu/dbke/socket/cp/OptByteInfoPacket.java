package edu.dbke.socket.cp;

import edu.dbke.socket.cp.util.ByteUtil;

/**
 * 服务器操作交互通用类型的包
 * @author huitang
 *
 */
public class OptByteInfoPacket extends Packet<OptByteInfoPacket> {
	public String optId;//数据ID
	public byte optType;//操作类型,1增加2修改3删除4查询
	public byte optResult;//操作结果
	public String optInfo;//操作信息

	public final static byte TYPE_ADD = 1;
	public final static byte TYPE_UPDATE = 2;
	public final static byte TYPE_DELETE = 3;
	public final static byte TYPE_QUERY = 4;

	@Override
	protected void writeData() {
		ByteUtil.write256String(data, optId);
		data.put(optType);
		data.put(optResult);
		ByteUtil.writeShortString(data, optInfo);
	}

	@Override
	protected void readData() {
		optId = ByteUtil.read256String(data);
		optType = data.get();
		optResult = data.get();
		optInfo = ByteUtil.readShortString(data);
	}

	public OptByteInfoPacket() {
	}

	public OptByteInfoPacket(short type) {
		this.type = type;
	}

	public OptByteInfoPacket(short type, String optId) {
		this.type = type;
		this.optId = optId;
	}

	public OptByteInfoPacket(short type, String optId, byte optResult) {
		this.type = type;
		this.optId = optId;
		this.optResult = optResult;
	}

	public OptByteInfoPacket(short type, String optId, byte optResult, String optInfo) {
		this.type = type;
		this.optId = optId;
		this.optResult = optResult;
		this.optInfo = optInfo;
	}
}