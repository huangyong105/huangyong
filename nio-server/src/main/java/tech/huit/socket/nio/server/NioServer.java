package tech.huit.socket.nio.server;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import edu.dbke.socket.cp.Packet;
import edu.dbke.socket.cp.ProtocolType;
import edu.dbke.socket.cp.StringPacket;
import edu.dbke.socket.cp.service.*;
import edu.dbke.socket.cp.util.ByteUtil;
import tech.huit.json.JacksonFactory;
import tech.huit.json.JsonManager;
import tech.huit.socket.nio.service.BaseService;
import tech.huit.socket.nio.service.ExternalService;
import tech.huit.socket.util.PacketAndServiceScanUtil;
import tech.huit.socket.web.MyCountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * NIO服务器，连接请求监听，数据收发服务，单线程
 *
 * @author huitang
 */
public class NioServer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(NioServer.class);
    private static final String TYPE_READ = "read";
    private static final String TYPE_WRITE = "write";
    public static int PROT = 6413;// 监听端口
    public static int WORK_THREAD_COUNT = 4;//至少4个线程，最大等于cpu数量
    public static boolean isWindos = "windows".equals(System.getProperties().get("sun.desktop"));
    public static final int PACKET_HEADER_SIZE = 6;//协议头大小
    private static final int MAX_DISPOSE_PACKET = 102400;//待处理大于指定数量丢弃新接收数据包
    private static final int MAX_SOCKET_SEND_PACKET = 102400;//单个socket待发送数据大于指定数量丢弃数据包

    private InetAddress hostAddress;// 服务器地址
    private static boolean isStop = false;// 是否停止服务器
    private ServerSocketChannel serverChannel;// socket服务器
    private Selector selector;// 数据监听
    private Worker[] workers;

    private ByteBuffer readPacketSize = ByteBuffer.allocate(4);// 协议包大小
    private BlockingQueue<DataEvent> pendingDisposeDataQueue = new LinkedBlockingQueue<DataEvent>();// 待处理数据队列
    private List<ChangeRequest> pendingChanges = Collections.synchronizedList(new LinkedList<ChangeRequest>());// 待切换监听事件
    private Map<SocketChannel, Vector<ByteBuffer>>  pendingSendDataMap = new ConcurrentHashMap<SocketChannel, Vector<ByteBuffer>>();// 待发送数据,使用Vector同步控制
    private Map<SocketChannel, ByteBuffer> imperfectDataPacketMap = new ConcurrentHashMap<SocketChannel, ByteBuffer>();// 未读满数据包
    private Map<SocketChannel, Long> onlineSocketMap = new ConcurrentHashMap<SocketChannel, Long>();// 所有连接的soket及最后一次正常通讯的时间,由检测线程进行删除
    private Map<SocketChannel, Long> socketAcceptTimeMap = new ConcurrentHashMap<SocketChannel, Long>();//soket建立连接时间
    private Map<SocketChannel, String> socket2id = new ConcurrentHashMap<SocketChannel, String>();// 通过soket查询id标志
    private Map<String, SocketChannel> id2socket = new ConcurrentHashMap<String, SocketChannel>();// 通过id查询socket
    private Map<Short, Object> packetMap = new ConcurrentHashMap<Short, Object>();// 协议解析包映射
    private Map<Short, BaseService> packetToSerivceMap = new ConcurrentHashMap<Short, BaseService>();// 数据包处理服务映射
    private Map<Class<?>, BaseService> classToServiceMap = new ConcurrentHashMap<Class<?>, BaseService>();// 服务类实例映射
    private ExternalService externalService;
    private Map<String, SocketChannel> serviceMap;// 外部服务对应的socket
    public Map<String, List<ByteBuffer>> httpPacket = new ConcurrentHashMap<String, List<ByteBuffer>>();// 外部服务对应的socket
    public final Map<String, MyCountDownLatch> swap = new ConcurrentHashMap();

    public long serverUpTime;// 服务启动时间
    public long receiveCount;// 接收数据包计数
    public long receiveSize;// 接收数据包总长度
    public long sendCount;// 发送数据包计数
    public long sendSize;// 发送数据包总长度

    private Thread checkThread;//socket连接有效性检测线程
    private Thread[] workThread;//工作线程

    public NioServer() {
        this(null, PROT, "edu.dbke");
    }

    public NioServer(int port) {
        this(null, port, "edu.dbke");
    }

    public NioServer(int port, String classScanPath) {
        this(null, port, classScanPath);
    }

    public NioServer(String classScanPath) {
        this(null, PROT, classScanPath);
    }

    public NioServer(InetAddress hostAddress, int port, String classScanBasePath) {
        logger.info("nio server startup port:" + port + " classScanBasePath:" + classScanBasePath);
        long begin = System.currentTimeMillis();
        this.hostAddress = hostAddress;
        NioServer.PROT = port;
        try {
            this.selector = SelectorProvider.provider().openSelector();
            this.serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);

            InetSocketAddress isa = new InetSocketAddress(this.hostAddress, NioServer.PROT);
            boolean isBindSuccess = false;
            int reTryCount = 0;
            do {
                try {
                    serverChannel.socket().bind(isa);//linux下面快速关闭重启端口可能未及时释放
                    isBindSuccess = true;
                } catch (Exception e) {
                    logger.error("portBindError->" + e.getMessage());
                    reTryCount++;
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }

            } while (!isBindSuccess && reTryCount < 4);
            if (!isBindSuccess) {
                logger.error("portBindError->exit");
                System.exit(1);
            }

            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            logger.error("IOException", e);
        }
        PacketAndServiceScanUtil.registerPacketAndService(packetMap, packetToSerivceMap, classToServiceMap, this,
                classScanBasePath);
        externalService = (ExternalService) classToServiceMap.get(ExternalService.class);
        if (null != externalService) {
            serviceMap = externalService.getServiceMap();
        }

        JsonManager.me().setDefaultJsonFactory(JacksonFactory.me());

        startWorkThread();

        checkThread = new Thread(new SocketCheck(this), "nio socket check");

        checkThread.setDaemon(true);
        checkThread.start();
        long end = System.currentTimeMillis();
        logger.info("nio server startup port:" + port + " use time:" + (end - begin));
        serverUpTime = end;
    }

    /**
     * 处理新的连接请求
     *
     * @param key
     * @throws IOException
     */
    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(this.selector, SelectionKey.OP_READ);
        long time = System.currentTimeMillis();
        onlineSocketMap.put(socketChannel, time);
        socketAcceptTimeMap.put(socketChannel, time);
        pendingSendDataMap.put(socketChannel, new Vector<ByteBuffer>());

        String id = getSocketId(socketChannel);
        logger.info("socket-connect:" + id);
        id2socket.put(id, socketChannel);
        socket2id.put(socketChannel, id);
    }

    /**
     * 得到socketID
     *
     * @param socketChannel
     * @return
     */
    public static String getSocketId(SocketChannel socketChannel) {
        SocketAddress address = socketChannel.socket().getRemoteSocketAddress();
        if (null != address) {
            return address.toString().substring(1);
        } else {
            return null;
        }
    }

    public void run() {
        // 服务初始化操作
        Iterator<Entry<Class<?>, BaseService>> service = classToServiceMap.entrySet().iterator();
        while (service.hasNext()) {
            try {
                service.next().getValue().init();
            } catch (Throwable e) {
                logger.error("servierInitError", e);
            }
        }
        while (!isStop) {
            SelectionKey key = null;
            try {
                synchronized (this.pendingChanges) {
                    Iterator<ChangeRequest> changes = this.pendingChanges.iterator();
                    while (changes.hasNext()) {
                        ChangeRequest change = changes.next();
                        switch (change.type) {
                            case ChangeRequest.CHANGEOPS:
                                key = change.socket.keyFor(this.selector);
                                if (null != key && key.isValid()) {
                                    key.interestOps(change.ops);
                                }
                        }
                    }
                    this.pendingChanges.clear();
                }
                this.selector.select();// 等待已注册的事件
                Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    key = selectedKeys.next();
                    selectedKeys.remove();

                    if (!key.isValid()) {
                        continue;
                    }

                    if (key.isReadable()) {
                        this.read(key, null, null);
                    } else if (key.isWritable()) {
                        this.write(key);
                    } else if (key.isAcceptable()) {
                        this.accept(key);
                    }
                }
            } catch (Throwable e) {
                logger.error("ioError", e);
                socketClosed(key);//ioException
            }
        }
        logger.info("nio server shutdown!");
    }

    /**
     * 启动工作线程，根据CPU核心数量启动对应线程数量，原则加上收发线程等于CPU个数
     */
    private void startWorkThread() {
        int processorsCount = Runtime.getRuntime().availableProcessors();
        int workerCount = Math.max(processorsCount, WORK_THREAD_COUNT);
        logger.info("nio server startup worker thread count:" + workerCount);
        workThread = new Thread[workerCount];
        workers = new Worker[workerCount];
        for (int i = 0; i <= workerCount - 1; i++) {
            Worker worker = new Worker(this);
            workers[i] = worker;
            workThread[i] = new Thread(worker, "nio work thread:" + i);
            workThread[i].setDaemon(true);
            workThread[i].start();
        }
    }

    /**
     * 读取数据，每一次读取之后都需要判断流是否关闭
     *
     * @param key                 得到数据的socket
     * @param uid                 放的协议包的uid，用于得处理结果数据
     * @param imperfectDataPacket 放入的协议包数
     */
    public void read(SelectionKey key, String uid, ByteBuffer imperfectDataPacket) throws IOException {
        SocketChannel socketChannel = null;
        int readCount = 0;
        if (null != key) {
            socketChannel = (SocketChannel) key.channel();
            imperfectDataPacket = imperfectDataPacketMap.get(socketChannel);
            if (null == imperfectDataPacket) {// 查看是否有未读完整的包
                this.readPacketSize.clear();
                int tempCount = 0;
                while (readCount < 4) {//一次读取一个完整的协议包头
                    tempCount = socketChannel.read(readPacketSize);
                    readCount += tempCount;
                    if (tempCount == -1) {
                        socketClosed(key);//client close socket
                        return;
                    }
                }
                readPacketSize.flip();
                int size = readPacketSize.getInt();
                if (size > 0 && size < 1048590) {// 防止非法的数据包协议头导致服务器崩溃
                    imperfectDataPacket = ByteBuffer.allocate(size);// 设置数据包的大小java.lang.OutOfMemoryErrors内存泄漏导致异常
                    imperfectDataPacket.putInt(size);
                    imperfectDataPacketMap.put(socketChannel, imperfectDataPacket);
                } else {
                    logger.error("收到非法的数据包协议头：" + socket2id.get(socketChannel) + ",size:" + size);
                    send(socketChannel, new PacketDataErrorPacket(size));
                    socketClosed(key);// 非法的数据包协议头,关闭连接
                }
            }
            readCount = socketChannel.read(imperfectDataPacket);
        }
        if (null != imperfectDataPacket && !imperfectDataPacket.hasRemaining()) {// 处理读取完整的数据包
            if (null != socketChannel) {
                imperfectDataPacketMap.remove(socketChannel);//先移出，防止处理逻辑有异常导致这个协议包没有被移出
            }
            receiveCount++;
            receiveSize += imperfectDataPacket.limit();
            short ptype = imperfectDataPacket.getShort(4);
            if (ProtocolType.SERVER_SOCKET_CHECK == ptype || ProtocolType.SERVER_STATUS_ECHO == ptype) {//过滤掉心跳测试
                if (null != socketChannel) {
                    logger.trace("read data " + socket2id.get(socketChannel) + " type:" + ptype);
                }
            } else {
                writeLog(uid, imperfectDataPacket, socketChannel, TYPE_READ);
            }
            try {
                if (ProtocolType.SERVER_EXTERNAL_CLIENT_DATA == ptype) {//外部服务发送给客户机的数据直接转发
                    imperfectDataPacket.position(6);
                    String clientId = ByteUtil.read256String(imperfectDataPacket);
                    if (null == clientId) {
                        logger.warn("dataErrorClientIdIsNull->socketId:" + socket2id.get(socketChannel) + " data:" + Arrays.toString(imperfectDataPacket.array()));
                        return;
                    }
                    if (clientId.startsWith("uid")) {
                        List<ByteBuffer> tmpData = httpPacket.get(clientId);
                        if (null == tmpData) {
                            tmpData = new ArrayList<ByteBuffer>();
                            httpPacket.put(clientId, tmpData);
                        }
                        tmpData.add(imperfectDataPacket);
                    } else {
                        imperfectDataPacket.position(0);
                        SocketChannel client = id2socket.get(clientId);
                        send(client, imperfectDataPacket);
                    }
                } else if (ProtocolType.SERVER_EXTERNAL_SERVER_DATA == ptype) {//客户机发送给外部服务的数据直接转发
                    imperfectDataPacket.position(6);
                    String serverName = ByteUtil.read256String(imperfectDataPacket);
                    byte[] bytesData = new byte[imperfectDataPacket.limit() - imperfectDataPacket.position()];
                    imperfectDataPacket.get(bytesData);
                    if (null == serverName) {
                        logger.warn("dataErrorServerNameIsNull->socketId:" + socket2id.get(socketChannel) + " data:" + Arrays.toString(imperfectDataPacket.array()));
                        return;
                    }
                    SocketChannel serverSocket = serviceMap.get(serverName);
                    String clientId = null;
                    if (uid != null) {
                        clientId = uid;
                    } else {
                        clientId = getSocketId(socketChannel);
                    }
                    send(serverSocket, new External2ServerPacket(clientId, bytesData));
                } else if (ProtocolType.SERVER_PROXY_CLIENT_DATA == ptype) {//代理服务发送给客户机的数据直接转发
                    Proxy2ClientPacket pcp = new Proxy2ClientPacket().readObject(imperfectDataPacket);
                    SocketChannel client = id2socket.get(pcp.clientId);
                    if (null != client) {//客户端在本地，判断为被代理的服务器，直接转发
                        imperfectDataPacket.position(0);
                        send(client, imperfectDataPacket);
                    } else {
                        Queue<ByteBuffer> queue = ExternalService.proxyDataMap.get(pcp.proxyName);
                        if (null != queue) {
                            queue.add(imperfectDataPacket);//转发给代理服务器
                        }
                    }
                } else if (ProtocolType.SERVER_EXTERNAL_CLIENT_DATA_STRING == ptype) {//外部服务发送给客户机的String类型数据
                    imperfectDataPacket.position(6);
                    String clientId = ByteUtil.read256String(imperfectDataPacket);
                    MyCountDownLatch countDownLatch = swap.get(clientId);
                    if (null != countDownLatch) {
                        countDownLatch.responseData = imperfectDataPacket;
                        countDownLatch.countDownLatch.countDown();//让http线程立马返回
                    }
                    logger.debug("http2socketToClient->clientId:{} data:{}", clientId, new String(countDownLatch.responseData.array()));
                } else if (ProtocolType.SERVER_EXTERNAL_SERVER_DATA_STRING == ptype) {//客户机发送给外部服务的String类型数据
                    imperfectDataPacket.position(6);
                    String serverName = ByteUtil.read256String(imperfectDataPacket);
                    byte[] bytesData = new byte[imperfectDataPacket.limit() - imperfectDataPacket.position()];
                    imperfectDataPacket.get(bytesData);
                    SocketChannel serverSocket = serviceMap.get(serverName);
                    if (null == serverSocket) {
                        logger.warn("http2socketServerNotFound->server:{}", serverName);
                    } else {
                        logger.debug("http2socketToServer->server:{} data:{}", serverName, new String(bytesData));
                        External2ServerPacket esp = new External2ServerPacket(uid, bytesData);
                        esp.type = ProtocolType.SERVER_EXTERNAL_SERVER_DATA_STRING;
                        send(serverSocket, esp);
                    }
                } else if (ProtocolType.SERVER_PROXY_SERVER_DATA == ptype) {//客户机发送给代理服务器的数据直接转发
                    Proxy2ServerPacket psp = new Proxy2ServerPacket().readObject(imperfectDataPacket);
                    SocketChannel socket = ExternalService.proxySocketMap.get(psp.proxyName);
                    if (null != socket) {
                        if (null == psp.clientId) {//由客户端发给主服务器
                            psp.clientId = socket2id.get(socketChannel);//设置客户端id为当前socket
                        }
                        send(socket, psp);//当前服务器为主服务器，直接转发给要求代理的socket
                    } else {
                        logger.debug("proxyNotConnected:" + psp.proxyName);
                    }
                } else if (ProtocolType.SERVER_STATUS_QUERY == ptype) {// 处理系统命令,在这里进行处理为了提高系统命令相应速度（后续可以单独提出到一个系统命令服务里）
                    ServerStatusPacketServer ssp = new ServerStatusPacketServer(pendingDisposeDataQueue.size(),
                            onlineSocketMap.size());
                    ssp.serverUpTime = System.currentTimeMillis() - serverUpTime;
                    ssp.receiveCount = receiveCount;
                    ssp.receiveSize = receiveSize;
                    ssp.sendCount = sendCount;
                    ssp.sendSize = sendSize;
                    send(socketChannel, ssp);
                } else if (ProtocolType.SERVER_SOCKET_ONLINE_LIST == ptype) {// 在线socket列表查询
                    StringBuffer sb = new StringBuffer();
                    List<String> list = new ArrayList<String>(socket2id.values());
                    Collections.sort(list);
                    long time = System.currentTimeMillis();
                    for (String id : list) {
                        long acceptTime = (time - socketAcceptTimeMap.get(id2socket.get(id))) / 1000;
                        sb.append(id).append(':').append(acceptTime).append(';');
                    }
                    send(socketChannel, new StringPacket(ProtocolType.SERVER_SOCKET_ONLINE_LIST, sb.toString()));
                } else if (ProtocolType.SERVER_PROTOCOL_VERSION == ptype) {// 服务器协议版本查询
                    send(socketChannel, new StringPacket(ProtocolType.SERVER_PROTOCOL_VERSION, ProtocolType.VERSION));
                } else {
                    if (pendingDisposeDataQueue.size() < MAX_DISPOSE_PACKET) {//待处理数据包数量大于指定值丢弃数据
                        pendingDisposeDataQueue.put(new DataEvent(socketChannel, imperfectDataPacket));
                    } else {
                        logger.error("丢弃待处理数据包：" + imperfectDataPacket);
                    }
                }
            } catch (InterruptedException ignore) {
                logger.error("InterruptedException", ignore);
            }
        }
        if (readCount != -1) {
            if (null != socketChannel) {
                onlineSocketMap.put(socketChannel, System.currentTimeMillis());// 更新最后一次正常通讯时间
            }
        } else {
            logger.info("client close socket");
            socketClosed(key);//client close socket
        }
    }

    private void writeLog(String uid, ByteBuffer buf, SocketChannel socketChannel, String optType) {
        if (!logger.isDebugEnabled()) {
            return;
        }
        try {
            short type = buf.getShort(4);
            Object clazz = packetMap.get(type);
            Packet packet = null;
            MessageLite prototype = null;
            if (null != clazz && clazz instanceof Packet) {
                packet = (Packet) ((Class) clazz).newInstance();
                packet.readObject(buf);
            } else if (clazz instanceof MessageLite) {
                buf.position(6);
                prototype = ((MessageLite) clazz).getParserForType().parseFrom(buf.slice());
                buf.position(buf.limit());
            }
            StringBuffer debugInfo = new StringBuffer(optType);
            debugInfo.append("Data type:").append(type);
            if (null != socketChannel) {
                debugInfo.append(" sid:").append(socket2id.get(socketChannel));
            }
            if (null != uid) {
                debugInfo.append(" uid:").append(uid);
            }

            debugInfo.append(" packet:");
            if (null != packet) {
                debugInfo.append(packet);
            } else if (null != prototype) {
                String msg = JsonFormat.printer().preservingProtoFieldNames().print((MessageOrBuilder) prototype);
                debugInfo.append(msg);
            }
            logger.debug(debugInfo.toString());
        } catch (Exception e) {
            logger.error("writeLogError", e);
        }
    }

    /**
     * 写数据
     *
     * @param key
     * @throws IOException
     */

    private void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        if (socketChannel.isOpen()) {
            Vector<ByteBuffer> queue;
            queue = this.pendingSendDataMap.get(socketChannel);
            while (!queue.isEmpty()) {// 一次写完所有数据
                ByteBuffer buf = queue.get(0);
                if (buf != null) {
                    socketChannel.write(buf);
                    if (buf.remaining() > 0) {// socket's buffer fills up
                        break;
                    }
                    if (logger.isDebugEnabled()) {
                        short type = buf.getShort(4);
                        Packet<?> packet = null;
                        try {
                            Object clazz = packetMap.get(type);
                            if (null != clazz && clazz instanceof Class) {
                                packet = (Packet) ((Class) clazz).newInstance();
                                packet.readObject(buf);
                            }
                        } catch (Exception e) {
                            logger.error("readPacketError", e);
                        }

                        StringBuffer debugInfo = new StringBuffer("writeData type:").append(type);
                        if (null != socketChannel) {
                            debugInfo.append(" sid:").append(socket2id.get(socketChannel));
                        }
                        if (null != packet) {
                            debugInfo.append(" packet:").append(packet);
                        }
                        logger.debug(debugInfo.toString());
                    }
                    writeLog(null, buf, socketChannel, TYPE_WRITE);

                    onlineSocketMap.put(socketChannel, System.currentTimeMillis());// 更新最后一次正常通讯时间
                    queue.remove(0);
                    sendCount++;
                    sendSize += buf.limit();
                }
            }

            if (queue.isEmpty()) {// 所有数据发送毕，切回数据读取监听
                key.interestOps(SelectionKey.OP_READ);
            }
        }
    }

    /**
     * 通过socketId号发送数据
     *
     * @param socketId
     * @param packet
     */
    public void send(String socketId, Packet<?> packet) {
        if (null != socketId && null != packet) {
            send(id2socket.get(socketId), packet.writeObject());
        }
    }


    /**
     * 发送数据
     *
     * @param socket
     * @param data
     */
    public void send(SocketChannel socket, MessageLite.Builder data, short type) {
        if (null != socket && null != data) {
            send(socket, data.build(), type);
        }
    }

    /**
     * 发送数据
     *
     * @param socket
     * @param data
     */
    public void send(SocketChannel socket, MessageLite data, short type) {
        if (null != socket && null != data) {
            byte[] dataByte = data.toByteArray();
            ByteBuffer buf = ByteBuffer.allocate(dataByte.length + PACKET_HEADER_SIZE);
            buf.putInt(buf.limit());
            buf.putShort(type);
            buf.put(dataByte);
            send(socket, buf);
        }
    }

    /**
     * 发送数据
     *
     * @param socket
     * @param data
     */
    public void send(SocketChannel socket, byte[] data) {
        if (null != socket && null != data) {
            send(socket, ByteBuffer.wrap(data));
        }
    }

    /**
     * 通过socketId号发送数据
     *
     * @param socketId
     * @param data
     */
    public void send(String socketId, ByteBuffer data) {
        if (null != socketId && null != data) {
            send(id2socket.get(socketId), data);
        }
    }

    /**
     * 发送ByteBuffer，每个socket待发送队列不大于100个数据包，大于100个数据包线程将挂起待添加成功才返回
     *
     * @param socket
     * @param data
     */
    public synchronized void send(SocketChannel socket, ByteBuffer data) {
        if (null == socket || null == data || !socket.isOpen()) {
            return;
        }
        data.position(0);
        List<ByteBuffer> queue = this.pendingSendDataMap.get(socket);
        if (null != queue) {
            if (queue.size() < MAX_SOCKET_SEND_PACKET) {
                queue.add(data);
                this.pendingChanges.add(new ChangeRequest(socket, ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));
                this.selector.wakeup();
            } else {
                logger.error("丢弃待发送数据包：" + data);
            }
        }
    }

    /**
     * 添加一个socket断开的数据
     *
     * @param socketChannel
     */
    public void dispachSocketClosed(SocketChannel socketChannel) {
        ByteBuffer data = ByteBuffer.allocate(PACKET_HEADER_SIZE);
        data.putInt(PACKET_HEADER_SIZE);
        data.putShort(ProtocolType.SERVER_SOCKET_CLOSED);
        try {
            this.pendingDisposeDataQueue.put(new DataEvent(socketChannel, data));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加数据处理事件
     *
     * @param dataEvent
     */
    public void addDataEvent(DataEvent dataEvent) {
        try {
            this.pendingDisposeDataQueue.put(dataEvent);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭Socket
     */
    public void socketClosed(SelectionKey key) {
        if (null != key) {
            socketClosed((SocketChannel) key.channel());//socketClosed()
        }
    }

    /**
     * 关闭Socket
     *
     * @throws IOException
     */
    public void socketClosed(SocketChannel socketChannel) {
        if (null != socketChannel) {
            try {
                logger.info("socket-closed:" + socket2id.get(socketChannel));
                socketChannel.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            if (null != externalService) {
                externalService.socketClose(socketChannel);//要在 socket2id.remove(socketChannel); 之前
            }
            dispachSocketClosed(socketChannel);
            onlineSocketMap.remove(socketChannel);
            pendingSendDataMap.remove(socketChannel);
            imperfectDataPacketMap.remove(socketChannel);
            socketAcceptTimeMap.remove(socketChannel);
            String id = socket2id.remove(socketChannel);
            if (null != id) {
                id2socket.remove(id);
            }
        }
    }

    /**
     * 发送一个协议包
     *
     * @param socket
     * @param packet
     */
    public void send(SocketChannel socket, Packet<?> packet) {
        if (null != socket && null != packet) {
            send(socket, packet.writeObject());
        }
    }

    public static void main(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("port=")) {
                PROT = Integer.valueOf(arg.split("=")[1]);
            } else if ("start".equals(arg)) {
                logger.info("nio server start...");
            } else if ("stop".equals(arg)) {
                logger.info("nio server stop...");
                System.exit(0);
            }
        }
        logger.info("nio server start at port:" + PROT);
        new Thread(new NioServer(PROT), "nio server").start();
    }

    public Map<Short, Object> getPacketMap() {
        return packetMap;
    }


    public Map<Short, BaseService> getPacketToSerivceMap() {
        return packetToSerivceMap;
    }


    public Map<Class<?>, BaseService> getClassToServiceMap() {
        return classToServiceMap;
    }


    public BlockingQueue<DataEvent> getPendingDisposeDataQueue() {
        return pendingDisposeDataQueue;
    }


    public Map<SocketChannel, Long> getOnlineSocketMap() {
        return onlineSocketMap;
    }


    public Map<SocketChannel, String> getSocket2id() {
        return socket2id;
    }

    public Map<String, SocketChannel> getId2socket() {
        return id2socket;
    }

    public static boolean isStop() {
        return isStop;
    }

    public void stop() {
        NioServer.isStop = true;
        logger.info("nio server stop");
        try {
            serverChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        checkThread.interrupt();
        for (Thread thread : workThread) {
            thread.interrupt();
        }
        for (BaseService bs : classToServiceMap.values()) {
            bs.destroy();
        }
    }
}
