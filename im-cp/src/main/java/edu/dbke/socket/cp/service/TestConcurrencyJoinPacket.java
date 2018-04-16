package edu.dbke.socket.cp.service;

import edu.dbke.socket.cp.AbstractPacket;
import edu.dbke.socket.cp.ProtocolType;

/**
 * 加入并发测试
 */
public class TestConcurrencyJoinPacket extends AbstractPacket<TestConcurrencyJoinPacket> {
	public TestConcurrencyJoinPacket() {
		this.type = ProtocolType.SERVER_CONCURRENCY_TEST_JOIN;
	}
}
