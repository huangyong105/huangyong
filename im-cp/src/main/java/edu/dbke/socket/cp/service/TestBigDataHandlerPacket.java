package edu.dbke.socket.cp.service;

import edu.dbke.socket.cp.ProtocolType;

public class TestBigDataHandlerPacket extends AbstractBigDataHandlerPacket<TestBigDataHandlerPacket> {

	@Override
	protected void writeData() {

	}

	@Override
	protected void readData() {

	}

	public TestBigDataHandlerPacket() {
		this.type = ProtocolType.BIGDATA_HANDLE_TEST;
	}
}
