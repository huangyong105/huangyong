package edu.dbke.socket.cp;

import edu.dbke.socket.cp.Packet;

/**
 * 空包，用以给服务器发送一个控制指令
 * @author huitang
 *
 */
public class Empty extends Packet<Empty> {
	@Override
	protected void writeData() {

	}

	@Override
	protected void readData() {

	}

	public Empty() {
	}

	public Empty(short type) {
		this.type = type;
	}
}
