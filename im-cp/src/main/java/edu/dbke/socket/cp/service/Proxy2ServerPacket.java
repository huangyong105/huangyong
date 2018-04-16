package edu.dbke.socket.cp.service;

import edu.dbke.socket.cp.Packet;
import edu.dbke.socket.cp.ProtocolType;
import edu.dbke.socket.cp.util.ByteUtil;

/**
 * 外部转发数据包
 * @author huitang
 */
public class Proxy2ServerPacket extends Packet<Proxy2ServerPacket> {
	public String proxyName;//目标服务
	public String clientId;//客户端id（ip:port）
	public byte[] bytesData;//数据

	@Override
	protected void writeData() {
		ByteUtil.write256String(data, proxyName);
		ByteUtil.writeShortString(data, clientId);
		data.put(bytesData);
	}

	@Override
	protected void readData() {
		proxyName = ByteUtil.read256String(data);
		clientId = ByteUtil.readShortString(data);
		bytesData = new byte[data.limit() - data.position()];
		data.get(bytesData);
	}

	public Proxy2ServerPacket() {
		this.type = ProtocolType.SERVER_PROXY_SERVER_DATA;
	}

	public Proxy2ServerPacket(String target) {
		this.proxyName = target;
		this.type = ProtocolType.SERVER_PROXY_SERVER_DATA;
	}

	public Proxy2ServerPacket(String proxyName, String clientid, byte[] bytesData) {
		this.proxyName = proxyName;
		this.clientId = clientid;
		this.bytesData = bytesData;
		this.type = ProtocolType.SERVER_PROXY_SERVER_DATA;
	}
}
