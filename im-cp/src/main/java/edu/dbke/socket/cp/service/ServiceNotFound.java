package edu.dbke.socket.cp.service;

import edu.dbke.socket.cp.AbstractPacket;
import edu.dbke.socket.cp.ProtocolType;

/**
 * 找不到的服务
 * @author huitang
 *
 */
public class ServiceNotFound extends AbstractPacket<ServiceNotFound> {
	public ServiceNotFound() {
		this.type = ProtocolType.SERVER_STATUS_NOT_FOUND;
	}
}
