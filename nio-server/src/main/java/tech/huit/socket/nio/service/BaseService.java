package tech.huit.socket.nio.service;

import java.nio.channels.SocketChannel;
import java.util.Set;

import com.google.protobuf.MessageLite;
import edu.dbke.socket.cp.Packet;
import tech.huit.socket.nio.server.DataEvent;
import tech.huit.socket.nio.server.NioServer;

/**
 * 业务方法
 * @author huitang
 *
 */
public interface BaseService {
	/**
	 * 服务销毁进行资源释放等操作
	 * @return
	 */
	public void destroy();

	/**
	 * 服务器事件派发：用于socket连接中断事件，服务器关闭事件，服务关键事件，禁止乱发消息,返回true阻止事情再进行派发
	 * @param dataEvent
	 * @throws Exception
	 */
	public boolean dispatchEvent(DataEvent dataEvent) throws Exception;

	/**
	 * doTask方法耗时的操作使用异步的方法，新开单独的线程
	 * @param packet 数据包
	 * @throws Exception
	 */
	public void doTask(SocketChannel socket, Packet<?> packet);

    /**
     * doTask方法耗时的操作使用异步的方法，新开单独的线程
     *
     * @param packet 数据包
     * @throws Exception
     */
    public void doTask(SocketChannel socket, short type, MessageLite packet);


    /**
	 * doTask方法耗时的操作使用异步的方法，新开单独的线程
	 * @param socket 收到数据的socket
	 * @param type 协议类型
	 * @throws Exception
	 */
	public void doTask(SocketChannel socket, short type);

	/**
	 * 得到要处理的包
	 * @return
	 */
	public Set<Class<?>> getDisposePacket();

	/**
	 * 得到要处理协议类型
	 * @return
	 */
	public Set<Short> getDisposeType();

	/**
	 * 服务创建进行初始化操作，服务业务方法可以些通过getPacketMap直接添加type到协议包的映射
	 * @return
	 */
	public void init();

	/**
	 * 设置服务器
	 * @param server
	 */
	public void setServer(NioServer server);

}
