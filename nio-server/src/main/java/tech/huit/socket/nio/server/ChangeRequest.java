package tech.huit.socket.nio.server;

import java.nio.channels.SocketChannel;

/**
 * SocketChannel监听事件状态切换
 * @author huitang
 *
 */
public class ChangeRequest {
	/**
	 * 注册，目前只用在客户端注册OP_CONNECT事件，用来判断是否可以发送数据
	 */
	public static final int REGISTER = 1;
	public static final int CHANGEOPS = 2;//改变

	public SocketChannel socket;
	public int type;
	public int ops;

	public ChangeRequest(SocketChannel socket, int type, int ops) {
		this.socket = socket;
		this.type = type;
		this.ops = ops;
	}
}
