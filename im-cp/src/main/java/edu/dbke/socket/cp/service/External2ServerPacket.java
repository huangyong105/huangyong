package edu.dbke.socket.cp.service;

import edu.dbke.socket.cp.Packet;
import edu.dbke.socket.cp.ProtocolType;
import edu.dbke.socket.cp.util.ByteUtil;

/**
 * 外部转发数据包
 * @author huitang
 */
public class External2ServerPacket extends Packet<External2ServerPacket> {
	public String target;//目标服务
	public byte[] bytesData;//数据

	@Override
	protected void writeData() {
		ByteUtil.write256String(data, target);
		data.put(bytesData);
	}

	@Override
	protected void readData() {
		target = ByteUtil.read256String(data);
		bytesData = new byte[data.limit() - data.position()];
		data.get(bytesData);
	}

	public External2ServerPacket() {
		this.type = ProtocolType.SERVER_EXTERNAL_SERVER_DATA;
	}
	public External2ServerPacket(String target) {
		this.target = target;
		this.type = ProtocolType.SERVER_EXTERNAL_SERVER_DATA;
	}

	public External2ServerPacket(String target, byte[] bytesData) {
		this.target = target;
		this.bytesData = bytesData;
		this.type = ProtocolType.SERVER_EXTERNAL_SERVER_DATA;
	}
}
