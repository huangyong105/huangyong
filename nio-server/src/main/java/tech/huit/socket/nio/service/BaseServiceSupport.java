package tech.huit.socket.nio.service;

import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Set;

import com.google.protobuf.MessageLite;
import edu.dbke.socket.cp.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tech.huit.socket.nio.server.DataEvent;
import tech.huit.socket.nio.server.NioServer;

/**
 * 业务方法实现帮助类
 *
 * @author huitang
 */
public abstract class BaseServiceSupport implements BaseService {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 数据收发服务器
     */
    public NioServer server = null;
    /**
     * 注册可处理的数据包
     */
    protected Set<Class<?>> disposePacket = new HashSet<Class<?>>();
    /**
     * 注册可以处理协议类型
     */
    protected Set<Short> disposeType = new HashSet<Short>();

    @Override
    public boolean dispatchEvent(DataEvent dataEvent) throws Exception {
        return false;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init() {
    }

    @Override
    public void doTask(SocketChannel socket, short type) {
    }

    @Override
    public void doTask(SocketChannel socket, Packet<?> packet) {
    }

    @Override
    public void doTask(SocketChannel socket, short type, MessageLite packet) {
    }

    @Override
    public void setServer(NioServer server) {
        this.server = server;
    }

    @Override
    public Set<Class<?>> getDisposePacket() {
        registerPacket();
        return disposePacket;
    }

    /**
     * 注册要处理的数据包
     */
    public void registerPacket() {
    }

    @Override
    public Set<Short> getDisposeType() {
        registerType();
        return disposeType;
    }

    /**
     * 注册要处理的协议类型
     */
    protected void registerType() {
    }
}
