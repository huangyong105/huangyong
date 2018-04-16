package tech.huit.socket.nio.server;

import com.google.protobuf.MessageLite;
import edu.dbke.socket.cp.*;
import edu.dbke.socket.cp.service.ServiceNotFound;
import tech.huit.socket.nio.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * 数据调度（及处理）线程，根据CUP核心数量启动对应线程数量，原则加上收发线程等于CPU个数<br/>
 * 当服务为同步方法时，此线程也就是数据处理的线程,如果服务比较消时为增加性能可以加大worker线程的数量
 *
 * @author huitang
 */
public class Worker implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Worker.class);
    private BlockingQueue<DataEvent> queue;// 待处理数据队列
    private Map<Short, Object> packetMap;//服务器业务方法
    private Map<Short, BaseService> serviceMap;//服务器业务方法
    private Map<Class<?>, BaseService> classToServiceMap;
    private Map<SocketChannel, String> socket2id = null;
    private Map<String, SocketChannel> id2socket = null;

    private NioServer server;//数据收发服务器

    public Worker(NioServer server) {
        this.server = server;
        this.queue = server.getPendingDisposeDataQueue();
        this.packetMap = server.getPacketMap();
        this.serviceMap = server.getPacketToSerivceMap();
        this.classToServiceMap = server.getClassToServiceMap();
        socket2id = server.getSocket2id();
        id2socket = server.getId2socket();
    }

    public void run() {
        DataEvent dataEvent;
        while (!server.isStop()) {
            try {
                dataEvent = queue.take();//等待数据队列
                dataEvent.data.flip();
                short protocolType = dataEvent.data.getShort(4);//得到协议类型
                switch (protocolType) {
                    case ProtocolType.SERVER_SOCKET_CHECK://客户端socket有效性检查
                        break;
                    case ProtocolType.SERVER_SOCKET_CLOSED://socket关闭
                        dispatchEvent(dataEvent);
                        break;
                    case ProtocolType.SERVER_STATUS_SHUTDOWN://服务器关闭
                        dispatchEvent(dataEvent);
                        server.stop();
                        break;
                    case ProtocolType.SERVER_STATUS_ECHO://回音测试
                        server.send(dataEvent.socket, dataEvent.data);
                        break;
                    case ProtocolType.SERVER_STATUS_SYN_TIME://获取服务器当前时间。
                        server.send(dataEvent.socket, new StringPacket(ProtocolType.SERVER_STATUS_SYN_TIME, String.valueOf(System.currentTimeMillis())));
                        break;
                    default:
                        Object clazz = packetMap.get(protocolType);//得到包对应的解析类
                        BaseService service = serviceMap.get(protocolType);//包处理服务
                        if (null == service) {
                            logger.error("收到不能处理的包:" + protocolType + ":" + dataEvent.data);
                            server.send(dataEvent.socket, new ServiceNotFound());
                            continue;
                        }
                        if (null != clazz) {
                            if (clazz instanceof MessageLite) {
                                dataEvent.data.position(6);
                                MessageLite prototype = ((MessageLite) clazz).getParserForType().parseFrom(dataEvent.data.slice());
                                logger.debug("processData " + NioServer.getSocketId(dataEvent.socket) + " packet:" + prototype);
                                service.doTask(dataEvent.socket, protocolType, prototype);
                            } else if (clazz instanceof Packet) {
                                Packet<?> packet = (Packet<?>) ((Packet<?>) ((Class) clazz).getConstructor().newInstance()).readObject(dataEvent.data);//包解析
                                logger.debug("processData " + NioServer.getSocketId(dataEvent.socket) + " packet:" + packet);
                                service.doTask(dataEvent.socket, packet);
                            }
                        } else if (-1000 > protocolType && protocolType > -6000) {//如果这一段负数的逻辑要注释掉，之前所有使用默认的地方是配置上协议解析映射
                            service.doTask(dataEvent.socket, new StringPacket().readObject(dataEvent.data));
                        } else if (-6000 > protocolType && protocolType > -10000) {
                            service.doTask(dataEvent.socket, new BytesPacket().readObject(dataEvent.data));
                        } else if (-10000 > protocolType && protocolType > -13000) {
                            service.doTask(dataEvent.socket, new BytePacket().readObject(dataEvent.data));
                        } else if (-13000 > protocolType && protocolType > -15000) {
                            service.doTask(dataEvent.socket, new ShortPacket().readObject(dataEvent.data));
                        } else if (-15000 > protocolType && protocolType > -17000) {
                            service.doTask(dataEvent.socket, new IntPacket().readObject(dataEvent.data));
                        } else if (-17000 > protocolType && protocolType > -19000) {
                            service.doTask(dataEvent.socket, new LongPacket().readObject(dataEvent.data));
                        } else if (-19000 > protocolType && protocolType > -21000) {
                            service.doTask(dataEvent.socket, new FloatPacket().readObject(dataEvent.data));
                        } else if (-21000 > protocolType && protocolType > -23000) {
                            service.doTask(dataEvent.socket, new DoublePacket().readObject(dataEvent.data));
                        } else if (-23000 > protocolType && protocolType > -25000) {
                            service.doTask(dataEvent.socket, new BooleanPacket().readObject(dataEvent.data));
                        } else if (-25000 > protocolType && protocolType > -27000) {
                            service.doTask(dataEvent.socket, new DatePacket().readObject(dataEvent.data));
                        } else {//直接处理指令
                            service.doTask(dataEvent.socket, protocolType);
                        }
                }
            } catch (java.lang.InterruptedException e) {//服务器关闭才会处罚这个，不打印异常
            } catch (Throwable e) {
                logger.error("workerError:", e);
            }
        }
    }

    /**
     * 服务器事件派发
     *
     * @param dataEvent
     * @throws Exception
     */

    private void dispatchEvent(DataEvent dataEvent) throws Exception {
        Iterator<BaseService> it = classToServiceMap.values().iterator();
        while (it.hasNext()) {
            BaseService serviceImp = it.next();
            if (serviceImp.dispatchEvent(dataEvent)) {
                logger.debug("serviceImp break dispatch:" + serviceImp.getClass());
                break;
            }
        }

        //清理socket和id的映射map
        String id = socket2id.get(dataEvent.socket);
        if (null != id) {//check thread may remove the id
            logger.debug("socket remove:" + id);
            socket2id.remove(dataEvent.socket);
            id2socket.remove(id);
        }
    }
}