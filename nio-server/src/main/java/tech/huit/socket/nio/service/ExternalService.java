package tech.huit.socket.nio.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import edu.dbke.socket.cp.Empty;
import edu.dbke.socket.cp.Packet;
import edu.dbke.socket.cp.ProtocolType;
import edu.dbke.socket.cp.StringPacket;
import edu.dbke.socket.cp.service.External2ClientPacket;
import edu.dbke.socket.cp.service.External2ServerPacket;
import edu.dbke.socket.cp.service.Proxy2ServerPacket;
import edu.dbke.socket.cp.util.ByteUtil;
import tech.huit.socket.nio.server.DataEvent;
import tech.huit.socket.nio.server.NioServer;

/**
 * 外部数据处理数据收发代理服务,socket到id的映射mp维护
 *
 * @author huitang
 */
public class ExternalService extends BaseServiceSupport {
    private Map<String, SocketChannel> serviceMap = new ConcurrentHashMap<String, SocketChannel>();// 服务对应的socket
    private Map<SocketChannel, Set<SocketChannel>> serverClients = new ConcurrentHashMap<SocketChannel, Set<SocketChannel>>();// 服务关联的终端socket
    private Map<SocketChannel, Set<SocketChannel>> clientServers = new ConcurrentHashMap<SocketChannel, Set<SocketChannel>>();// 终端socket关联的服务
    private Map<SocketChannel, String> socket2id = null;
    private String authDir;
    public static Map<String, SocketChannel> proxySocketMap = new ConcurrentHashMap<String, SocketChannel>();// 主服务器用于保存代理服务的连接
    public static Map<SocketChannel, String> socketProxyMap = new ConcurrentHashMap<SocketChannel, String>();// 主服务器保存当前连接对应的服务名
    public static Map<String, String> proxyInfoMap = new ConcurrentHashMap<String, String>();//主服务器用于保存的代理服务器列表信息
    public static Map<String, Queue<ByteBuffer>> proxyDataMap = new ConcurrentHashMap<String, Queue<ByteBuffer>>();// 本地服务器发送给主服务器的数据缓存
    public static Map<String, Thread> proxyThreadMap = new ConcurrentHashMap<String, Thread>();//本地服务器与主服务器进行数据收发的线程，在服务中断时用于终止线程

    @Override
    public void init() {
        socket2id = server.getSocket2id();
        if (NioServer.isWindos) {
            authDir = "C:/auth/";
        } else {
            authDir = "/auth/";
        }
    }

    @Override
    public void registerPacket() {
        disposePacket.add(External2ServerPacket.class);
        disposePacket.add(External2ClientPacket.class);
        disposeType.add(ProtocolType.SERVER_EXTERNAL_SERVER_JOIN);
        disposeType.add(ProtocolType.SERVER_EXTERNAL_SERVER_ONLINE_LIST);
        disposeType.add(ProtocolType.SERVER_EXTERNAL_SERVER_CLIENT_ONLINE_LIST);
        disposeType.add(ProtocolType.SERVER_EXTERNAL_SERVER_STATUS_QUERY);
        disposeType.add(ProtocolType.SERVER_EXTERNAL_SERVER_KICK);
        disposeType.add(ProtocolType.SERVER_EXTERNAL_CLIENT_SUBSCRIBE);
        disposeType.add(ProtocolType.SERVER_PROXY_REGISTER);
        disposeType.add(ProtocolType.SERVER_PROXY_LIST_QUERY);
        server.getPacketMap().put(ProtocolType.SERVER_EXTERNAL_SERVER_JOIN, StringPacket.class);
        server.getPacketMap().put(ProtocolType.SERVER_PROXY_REGISTER, StringPacket.class);
        server.getPacketMap().put(ProtocolType.SERVER_PROXY_LIST_QUERY, StringPacket.class);
        server.getPacketMap().put(ProtocolType.SERVER_EXTERNAL_SERVER_CLIENT_ONLINE_LIST, StringPacket.class);
    }

    @Override
    public void doTask(SocketChannel socket, short type) {
        try {
            switch (type) {
                case ProtocolType.SERVER_EXTERNAL_SERVER_ONLINE_LIST:
                    Iterator<Entry<String, SocketChannel>> it = serviceMap.entrySet().iterator();
                    StringBuffer sb = new StringBuffer();
                    while (it.hasNext()) {
                        Entry<String, SocketChannel> service = it.next();
                        sb.append(service.getKey()).append(" ").append(socket2id.get(service.getValue())).append("\r\n");
                    }
                    server.send(socket, new StringPacket(ProtocolType.SERVER_EXTERNAL_SERVER_ONLINE_LIST, sb.toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doTask(final SocketChannel socket, Packet<?> packet) {
        switch (packet.type) {
            case ProtocolType.SERVER_EXTERNAL_SERVER_DATA://直接传发不会被执行
                External2ServerPacket esp = (External2ServerPacket) packet;
                SocketChannel serverSocket = serviceMap.get(esp.target);
                esp.target = NioServer.getSocketId(socket);
                server.send(serverSocket, esp);
                break;
            case ProtocolType.SERVER_EXTERNAL_CLIENT_DATA://直接传发不会被执行
                External2ClientPacket ecp = (External2ClientPacket) packet;
                server.send(ecp.target, ecp);
                break;
            case ProtocolType.SERVER_EXTERNAL_CLIENT_SUBSCRIBE:
                StringPacket subscribe = (StringPacket) packet;
                SocketChannel serviceSocket = serviceMap.get(subscribe.dataStr);
                if (null == serviceSocket || !serviceSocket.isOpen()) {
                    if (null != serviceSocket) {//server socket 已经关闭，进行移出
                        serviceMap.remove(subscribe.dataStr);
                    }
                    subscribe.dataStr = "server not online";
                } else {
                    Set<SocketChannel> clients = serverClients.get(serviceSocket);
                    if (null != clients) {
                        clients.add(socket);//添加服务客户关联关系
                        logger.info("SERVER_EXTERNAL_CLIENT_SUBSCRIBE->clientId:" + socket2id.get(socket) + " data:" + subscribe.dataStr + " clientSize:" + clients.size());

                        Set<SocketChannel> servers = clientServers.get(socket);
                        if (null == servers) {
                            servers = Collections.synchronizedSet(new HashSet<SocketChannel>());
                            clientServers.put(socket, servers);
                        }
                        servers.add(serviceSocket);//添加客户服务关联关系
                    } else {
                        logger.error("server clients is null");
                    }
                    subscribe.dataStr = "true";
                }
                StringPacket sub = new StringPacket(ProtocolType.SERVER_EXTERNAL_CLIENT_SUBSCRIBE, socket2id.get(socket));
                server.send(serviceSocket, sub);//通知外部服务有客户订购
                server.send(socket, subscribe);//通知客户订购状态
                break;
            case ProtocolType.SERVER_EXTERNAL_SERVER_JOIN:
                StringPacket join = (StringPacket) packet;
                logger.info("SERVER_EXTERNAL_SERVER_JOIN->sid:" + socket2id.get(socket) + " data:" + join.dataStr);
                StringBuffer result = new StringBuffer();
                if (null != join.dataStr) {//服务名，是否订购socket关闭事件，加密狗UID，产品名
                    String[] parm = join.dataStr.split(",");
                    try {
                        if (Boolean.valueOf(parm[1])) {
                            serverClients.put(socket, Collections.synchronizedSet(new HashSet<SocketChannel>()));
                        }
                        serverSocket = serviceMap.get(parm[0]);
                        if (null == serverSocket || !serverSocket.isOpen()) {//不存在或已经关闭直接替换
                            if (parm.length > 2) {

                                //								String authorization = getAuthorization(Integer.valueOf(parm[2]), parm[3]);
                                String authorization = "{ProductName:智能导检系统,ProductManufacturers:四川智行电子科技有限公司,AuthorizedUser:国药阳光体检武汉分中心,AuthorizationInformation:35,PersonalServerAuth:true,CallCenterAuth:true,SmsAuth:true,SmsUid:iamfigo,SmsKey:fe580bf81ee431925593}";

                                if (null == authorization) {
                                    result.append("找不到授权记录");
                                } else {
                                    serviceMap.put(parm[0], socket);
                                    result.append("true,").append(authorization);
                                }
                            } else {
                                result.append("true");
                                serviceMap.put(parm[0], socket);
                            }
                        } else {
                            result.append("服务已存在[" + parm[0] + ":" + socket2id.get(serviceMap.get(parm[0])) + "]");
                        }
                    } catch (Exception e) {
                        result.append("参数格式错误，[serverName,<true or false>,(productName)]:[服务名，是否订购终端断开连接事件，加密狗UID]");
                    }
                } else {
                    result.append("参数格式错误，[serverName,<true or false>,(productName)]:[服务名，是否订购终端断开连接事件，加密狗UID]");
                }
                join.dataStr = result.toString();
                server.send(socket, join);
                break;
            case ProtocolType.SERVER_EXTERNAL_SERVER_STATUS_QUERY:
                serviceSocket = serviceMap.get(((StringPacket) packet).dataStr);
                String info = null;
                if (null != serviceSocket) {
                    info = socket2id.get(serviceSocket);
                } else {
                    info = "false";
                }
                server.send(socket, new StringPacket(ProtocolType.SERVER_EXTERNAL_SERVER_STATUS_QUERY, info));
                break;
            case ProtocolType.SERVER_EXTERNAL_SERVER_KICK:
                SocketChannel kickSocket = serviceMap.get(serviceMap.get(((StringPacket) packet).dataStr));
                if (null != kickSocket) {
                    serviceMap.remove(kickSocket);
                    server.send(kickSocket, new StringPacket(ProtocolType.SERVER_EXTERNAL_SERVER_KICK, socket2id.get(socket)));
                    serverClients.remove(kickSocket);
                }
                break;
            case ProtocolType.SERVER_PROXY_LIST_QUERY:
                StringPacket sp = (StringPacket) packet;

                StringBuffer sb = new StringBuffer();
                List<String> list = new ArrayList<String>(proxyInfoMap.values());
                Collections.sort(list);
                for (String value : list) {
                    if (null != sp.dataStr && sp.dataStr.length() > 0 && !value.startsWith(sp.dataStr)) {//需要进行过滤
                        continue;
                    }
                    sb.append(value).append(';');
                }

                sp.dataStr = sb.toString();
                server.send(socket, sp);
                break;
            case ProtocolType.SERVER_EXTERNAL_SERVER_CLIENT_ONLINE_LIST:
                sp = (StringPacket) packet;
                SocketChannel serverScoket = serviceMap.get(sp.dataStr);
                sp.dataStr = null;
                int totalSize = 0;
                if (null != serverScoket) {
                    Set<SocketChannel> clients = serverClients.get(serverScoket);
                    synchronized (clients) {
                        totalSize = clients.size();
                        StringBuffer ids = new StringBuffer();
                        int i = 0;
                        for (SocketChannel clientSocket : serverClients.get(serverScoket)) {
                            i++;
                            ids.append(socket2id.get(clientSocket)).append(',');
                            if (i % 2000 == 0) {//发包发送
                                sp.dataStr = ids.toString();
                                server.send(socket, sp);

                                sp = new StringPacket(ProtocolType.SERVER_EXTERNAL_SERVER_CLIENT_ONLINE_LIST);
                                ids = new StringBuffer();
                            }
                        }
                        if (ids.length() > 0) {
                            sp.dataStr = ids.toString();
                            server.send(socket, sp);
                        }
                    }
                }
                server.send(socket, new StringPacket(ProtocolType.SERVER_EXTERNAL_SERVER_CLIENT_ONLINE_LIST, "total:" + totalSize));
                break;
            case ProtocolType.SERVER_PROXY_REGISTER:
                sp = (StringPacket) packet;
                final String[] proxyInfo = sp.dataStr.split(";");//uuc.witaction;6413;tsetProxy,测试代理服务
                logger.info("SERVER_PROXY_REGISTER->" + sp.dataStr);
                if (proxyInfo.length == 1) {//当前服务器为主服务器
                    String proxyName = proxyInfo[0].split(",")[0];
                    proxySocketMap.put(proxyName, socket);
                    socketProxyMap.put(socket, proxyName);
                    proxyInfoMap.put(proxyName, proxyInfo[0]);
                    sp.dataStr = "true";
                    server.send(socket, sp);
                    return;
                }
                final String host = proxyInfo[0];
                final String port = proxyInfo[1];
                final String proxyName = proxyInfo[2].split(",")[0];
                socketProxyMap.put(socket, proxyName);
                proxySocketMap.put(proxyName, socket);
                Thread writeThread = new Thread(new Runnable() {
                    private long lastSendTime = System.currentTimeMillis();//上一次发送数据的时间
                    Socket proxySocket = null;
                    SocketAddress socketAddress = new InetSocketAddress(host, Integer.valueOf(port));
                    Queue<ByteBuffer> proxySendQueue;

                    @Override
                    public void run() {
                        OutputStream os = null;
                        Thread readThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                InputStream is = null;
                                while (!NioServer.isStop()) {
                                    try {
                                        if (null == proxySocket || !proxySocket.isConnected() || proxySocket.isClosed()) {
                                            try {
                                                Thread.sleep(100);
                                            } catch (InterruptedException e) {
                                                try {
                                                    proxySocket.close();
                                                } catch (Exception ignore) {
                                                }
                                                break;//代理服务中断，退出数据代理线程
                                            }
                                            continue;
                                        }
                                        if (null == is) {
                                            is = proxySocket.getInputStream();
                                            StringPacket sp = new StringPacket().readObject(ByteUtil.readPacket(is,
                                                    ProtocolType.SERVER_PROXY_REGISTER));
                                            if (null != sp && sp.dataStr.startsWith("true")) {
                                                logger.info("connect to main server success");
                                            }
                                        }
                                        ByteBuffer buf = ByteUtil.readPacket(is);
                                        if (null != buf) {
                                            short ptype = buf.getShort(4);
                                            if (ProtocolType.SERVER_PROXY_SERVER_DATA == ptype) {//主服务器转发过来的数据
                                                Proxy2ServerPacket psp = new Proxy2ServerPacket().readObject(buf);
                                                ExternalService.proxySocketMap.get(psp.proxyName);
                                                server.send(ExternalService.proxySocketMap.get(psp.proxyName), psp);
                                                logger.debug("proxyReadData:" + Arrays.toString(buf.array()));
                                            }
                                        }
                                    } catch (java.net.SocketException e) {
                                        if (null != is) {
                                            try {
                                                proxySocket.close();
                                                is.close();
                                                is = null;
                                                logger.error(Thread.currentThread().getName() + "-close", e);
                                            } catch (Throwable e1) {
                                            }
                                        }
                                    } catch (Throwable e) {
                                        logger.error("proxyReadError", e);
                                    }
                                }
                                logger.info(Thread.currentThread().getName() + "-shutdown");
                            }
                        }, "proxy-" + host + ":" + port + "-" + proxyName + "-read");
                        readThread.start();

                        proxyThreadMap.put(proxyName + "[proxy read]", readThread);
                        while (!NioServer.isStop()) {
                            try {
                                if (null != proxySendQueue && !proxySendQueue.isEmpty() && !proxySocket.isClosed()) {
                                    ByteBuffer buf = proxySendQueue.poll();
                                    byte[] bytes = buf.array();
                                    os.write(bytes);
                                    logger.debug("proxyWriteData:" + Arrays.toString(bytes));
                                    lastSendTime = System.currentTimeMillis();
                                } else {
                                    if (null == proxySocket || !proxySocket.isConnected()) {//断线重连
                                        proxySocket = new Socket();
                                        try {
                                            proxySocket.connect(socketAddress, 2000);//断线重连，不记录异常
                                        } catch (java.net.ConnectException ignore) {
                                            try {
                                                Thread.sleep(5000);
                                            } catch (InterruptedException e) {
                                                break;//代理服务中断，退出数据代理线程
                                            }
                                            continue;
                                        }
                                        os = proxySocket.getOutputStream();
                                        os.write(new StringPacket(ProtocolType.SERVER_PROXY_REGISTER, proxyInfo[2])
                                                .writeByteObject());

                                        proxySendQueue = new LinkedList<ByteBuffer>();
                                        proxyDataMap.put(proxyName, proxySendQueue);
                                    } else {
                                        if (System.currentTimeMillis() - lastSendTime > 15000 && proxySocket.isConnected()) {
                                            os.write(new Empty(ProtocolType.SERVER_SOCKET_CHECK).writeByteObject());
                                            lastSendTime = System.currentTimeMillis();
                                        } else {
                                            try {
                                                Thread.sleep(1);
                                            } catch (InterruptedException e) {
                                                try {
                                                    proxySocket.close();
                                                } catch (Exception ignore) {
                                                }
                                                break;//代理服务中断，退出数据代理线程
                                            }
                                        }
                                    }
                                }
                            } catch (java.net.SocketException e) {
                                try {
                                    if (null != proxySocket) {
                                        proxySocket.close();
                                        proxySocket = null;
                                        logger.error(Thread.currentThread().getName() + "-close", e);
                                    }
                                    if (null != os) {
                                        os.close();
                                        os = null;
                                    }
                                } catch (IOException e1) {
                                }
                            } catch (Throwable e) {
                                logger.error("proxyReadError", e);
                            }
                        }
                        logger.info(Thread.currentThread().getName() + "-shutdown");
                    }
                }, "proxy-" + host + ":" + port + "-" + proxyName + "-write");
                writeThread.start();
                proxyThreadMap.put(proxyName + "[proxy write]", writeThread);
                break;
            default:
                break;
        }
    }

    /**
     * 得到指定产品的授权信息
     *
     * @param productName 产品名
     * @param result      待解析授权信息
     * @return
     */
    private static String getProductAuthorization(String productName, String result) {
        if (null == result) {
            return null;
        }
        String subStr = null;
        int index1 = result.indexOf('['), index2 = result.indexOf(']'), index3;
        if (index1 != -1 && index2 != -1) {
            subStr = result.substring(index1 + 1, index2);
            index3 = result.indexOf('{');
            if (index3 != -1) {
                subStr = result.substring(index1 + 1, index3);
                if (subStr.equals(productName)) {
                    subStr = result.substring(index3, index2);
                } else {
                    result = result.substring(index2 + 1);
                    if (result.length() > 0) {
                        return getProductAuthorization(productName, result);
                    } else {
                        subStr = null;
                    }
                }
            }
        }
        return subStr;
    }

    public static void main(String[] args) {
        System.out.println(getProductAuthorization("voip", "{abc:def}{voip:xxx}"));
    }

    @Override
    public boolean dispatchEvent(DataEvent dataEvent) throws Exception {
        try {
            short protocolType = dataEvent.data.getShort(4);//得到协议类型
            switch (protocolType) {
                case ProtocolType.SERVER_SOCKET_CLOSED:
                    //socketClose(dataEvent.socket);//NioServer 会直接调用 externalService.socketClose(socketChannel);
                    String proxyName = socketProxyMap.remove(dataEvent.socket);
                    if (null != proxyName) {
                        Thread threadRead = proxyThreadMap.remove(proxyName + "[proxy read]");
                        if (null != threadRead) {
                            threadRead.interrupt();
                            Thread threadWrite = proxyThreadMap.remove(proxyName + "[proxy write]");
                            threadWrite.interrupt();
                        }
                        proxyDataMap.remove(proxyName);
                        proxyInfoMap.remove(proxyName);
                        logger.info("proxy server closed:" + proxyName);
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logger.error("dispatchEventError", e);
        }
        return false;
    }

    /**
     * 由NioServer 直接同步调用才能得到id
     *
     * @param socket
     */
    public void socketClose(SocketChannel socket) {
        Set<SocketChannel> notifyServers = clientServers.remove(socket);
        if (null != notifyServers) {//客户端断开
            for (SocketChannel service : notifyServers) {
                Set clients = serverClients.get(service);
                if (null != clients) {
                    clients.remove(socket);
                }
                StringPacket sp = new StringPacket(ProtocolType.SERVER_EXTERNAL_CLIENT_SCOKET_CLOSE, socket2id.get(socket));
                if (service.isOpen()) {
                    server.send(service, sp);// 向服务发送socket断开连接事件
                }
                logger.info("SERVER_EXTERNAL_CLIENT_SCOKET_CLOSE->clientId:" + socket2id.get(socket) + " serverId:" + socket2id.get(service) + " clientSize:" + clients.size());
            }
        }

        Set<SocketChannel> notifyClients = serverClients.remove(socket);
        if (null != notifyClients) {//server端断开
            for (SocketChannel client : notifyClients) {
                Set servers = clientServers.get(client);
                if (null != servers) {
                    servers.remove(socket);
                }
                if (client.isOpen()) {
                    server.socketClosed(client);//外部服务关闭导致关闭客户终端socket
                }
                logger.info("server close lead to client socket close:" + socket2id.get(client));
            }
        }
    }

    public Map<String, SocketChannel> getServiceMap() {
        return serviceMap;
    }
}
