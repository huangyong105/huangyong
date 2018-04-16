package tech.huit.socket.nio.server;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * 数据包
 * 
 * @author huitang
 * 
 */
public class DataEvent {
	public SocketChannel socket;// 收到数据的socket
	public ByteBuffer data;// 收到的数据包

	public DataEvent(SocketChannel socket, ByteBuffer data) {
		this.socket = socket;
		this.data = data;
	}
}
