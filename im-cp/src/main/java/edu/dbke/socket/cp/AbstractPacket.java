package edu.dbke.socket.cp;

/**
 * 数据包空实现
 * @author huitang
 */
public abstract class AbstractPacket<T> extends Packet<T> {

	/**
	 * 写数据
	 * @return
	 */
	@Override
	protected void writeData() {
	}

	/**
	 * 读数据
	 * @return
	 */
	@Override
	protected void readData() {
	}

}
