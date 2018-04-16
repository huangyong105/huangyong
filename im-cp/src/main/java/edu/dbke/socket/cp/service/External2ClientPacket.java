package edu.dbke.socket.cp.service;

import edu.dbke.socket.cp.Packet;
import edu.dbke.socket.cp.ProtocolType;
import edu.dbke.socket.cp.util.ByteUtil;

/**
 * 外部转发数据包
 * @author huitang
 */
public class External2ClientPacket extends Packet<External2ClientPacket> {
	public String target;//目标主机(socketid,222.18.159.2:1234)
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

	public External2ClientPacket() {
		this.type = ProtocolType.SERVER_EXTERNAL_CLIENT_DATA;
	}

	public External2ClientPacket(String target, byte[] bytesData) {
		this.target = target;
		this.bytesData = bytesData;
		this.type = ProtocolType.SERVER_EXTERNAL_CLIENT_DATA;
	}
}
