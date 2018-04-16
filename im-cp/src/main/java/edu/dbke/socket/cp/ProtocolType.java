package edu.dbke.socket.cp;

import edu.dbke.socket.cp.util.PacketCheckUitl;

/**
 * @author huitang
 */
public class ProtocolType {
    /**
     * 协议版本号
     */
    public final static String VERSION = "1.0.0";
    // 服务器相关
    @Desc(key = ProtocolType.SERVER_PROTOCOL_VERSION, desc = "通讯协议版本查询")
    public final static short SERVER_PROTOCOL_VERSION = -1;
    @Desc(key = ProtocolType.SERVER_ACCEPT, desc = "接受一个连接")
    public final static short SERVER_ACCEPT = 0;
    @Desc(key = ProtocolType.SERVER_PACKET_END, desc = "包发送结束")
    public final static short SERVER_PACKET_END = 1;
    @Desc(key = ProtocolType.SERVER_STATUS_OK, desc = "成功返回")
    public final static short SERVER_STATUS_OK = 2;
    @Desc(key = ProtocolType.SERVER_SOCKET_CLOSED, desc = "终端断线")
    public final static short SERVER_SOCKET_CLOSED = 3;
    @Desc(key = ProtocolType.SERVER_STATUS_NOT_FOUND, desc = "请求的服务找不到")
    public final static short SERVER_STATUS_NOT_FOUND = 4;
    @Desc(key = ProtocolType.SERVER_STATUS_SERVER_ERROR, desc = "服务器内部错误")
    public final static short SERVER_STATUS_SERVER_ERROR = 5;
    @Desc(key = ProtocolType.SERVER_STATUS_SHUTDOWN, desc = "服务器关闭")
    public final static short SERVER_STATUS_SHUTDOWN = 6;
    @Desc(key = ProtocolType.SERVER_STATUS_SHUTDOWN_GRACE, desc = "服务器安全关闭")
    public final static short SERVER_STATUS_SHUTDOWN_GRACE = 7;
    @Desc(key = ProtocolType.SERVER_BIGDATA_START, desc = "大数据开始")
    public final static short SERVER_BIGDATA_START = 8;
    @Desc(key = ProtocolType.SERVER_BIGDATA_DATA, desc = "大数据数据包")
    public final static short SERVER_BIGDATA_DATA = 9;
    @Desc(key = ProtocolType.SERVER_BIGDATA_END, desc = "大数据数据包接收完成")
    public final static short SERVER_BIGDATA_END = 10;
    @Desc(key = ProtocolType.BIGDATA_HANDLE_TEST, desc = "大数据处理测试")
    public final static short BIGDATA_HANDLE_TEST = 11;
    @Desc(key = ProtocolType.SERVER_CONCURRENCY_TEST_JOIN, desc = "服务器并发测试加入")
    public final static short SERVER_CONCURRENCY_TEST_JOIN = 12;
    @Desc(key = ProtocolType.SERVER_CONCURRENCY_TEST_DATA, desc = "服务器并发测试数据")
    public final static short SERVER_CONCURRENCY_TEST_DATA = 13;
    @Desc(key = ProtocolType.SERVER_CONCURRENCY_TEST_COUNT, desc = "获取服务器收到记录条数")
    public final static short SERVER_CONCURRENCY_TEST_COUNT = 14;
    @Desc(key = ProtocolType.SERVER_SOCKET_CHECK, desc = "socket有效性检查，心跳协议")
    public final static short SERVER_SOCKET_CHECK = 15;
    @Desc(key = ProtocolType.SERVER_STATUS_QUERY, desc = "服务器状态查询")
    public final static short SERVER_STATUS_QUERY = 16;
    @Desc(key = ProtocolType.SERVER_STATUS_MONITOR, desc = "服务器监控")
    public final static short SERVER_STATUS_MONITOR = 17;
    @Desc(key = ProtocolType.SERVER_STATUS_MONITOR_END, desc = "服务器监控取消")
    public final static short SERVER_STATUS_MONITOR_END = 18;
    @Desc(key = ProtocolType.SERVER_STATUS_ECHO, desc = "服务器回音测试，使用Empty可做为有回执的心跳协议")
    public final static short SERVER_STATUS_ECHO = 19;
    @Desc(key = ProtocolType.SERVER_STATUS_SYN_TIME, desc = "获取服务器当前时间，发送Empty; 返回StringPacket(时间缀)")
    public final static short SERVER_STATUS_SYN_TIME = -19;

    @Desc(key = ProtocolType.SERVER_PACKET_SIZE_ERROR, desc = "服务器数据包接收错误")
    public final static short SERVER_PACKET_SIZE_ERROR = 20;

    @Desc(key = ProtocolType.SERVER_SOCKET_ONLINE_LIST, desc = "连接socket查询")
    public final static short SERVER_SOCKET_ONLINE_LIST = 31;
    @Desc(key = ProtocolType.PRODUCT_AUTH_REPORT, desc = "产品授权及使用状态上报，StringPacket({userid:用户标识,serverid:mac或主机标识,sipCount:当前注册sip数量,concurrenceCount:当前最大并发数量}")
    public final static short PRODUCT_AUTH_REPORT = 32;
    @Desc(key = ProtocolType.PRODUCT_AUTH_GET, desc = "获取产品授权，StringPacket({userid:用户标识[请求时发送],serverid:mac或主机标识[请求时发送],sipCount:授权注册sip数量,concurrenceCount:授权最大并发数量,expiryDate:授权过期时间})")
    public final static short PRODUCT_AUTH_GET = 33;

    @Desc(key = ProtocolType.SERVER_EXTERNAL_SERVER_JOIN, desc = "外部服务加入注册")
    public final static short SERVER_EXTERNAL_SERVER_JOIN = -1008;
    @Desc(key = ProtocolType.SERVER_EXTERNAL_CLIENT_SUBSCRIBE, desc = "客户端注册外部服务")
    public final static short SERVER_EXTERNAL_CLIENT_SUBSCRIBE = -1009;
    @Desc(key = ProtocolType.SERVER_EXTERNAL_CLIENT_SCOKET_CLOSE, desc = "客户端socket连接关闭")
    public final static short SERVER_EXTERNAL_CLIENT_SCOKET_CLOSE = -1010;
    @Desc(key = ProtocolType.SERVER_EXTERNAL_SERVER_STATUS_QUERY, desc = "外部服务状态查询")
    public final static short SERVER_EXTERNAL_SERVER_STATUS_QUERY = -1011;
    @Desc(key = ProtocolType.SERVER_EXTERNAL_SERVER_SCOKET_CLOSE, desc = "外部服务连接关闭")
    public final static short SERVER_EXTERNAL_SERVER_SCOKET_CLOSE = -1016;
    @Desc(key = ProtocolType.SERVER_EXTERNAL_SERVER_KICK, desc = "外部服务踢出")
    public final static short SERVER_EXTERNAL_SERVER_KICK = -1017;

    @Desc(key = ProtocolType.SERVER_EXTERNAL_SERVER_DATA, desc = "发给外部服务的数据")
    public final static short SERVER_EXTERNAL_SERVER_DATA = 21;
    @Desc(key = ProtocolType.SERVER_EXTERNAL_CLIENT_DATA, desc = "外部服务发给客户端的数据")
    public final static short SERVER_EXTERNAL_CLIENT_DATA = 22;
    @Desc(key = ProtocolType.SERVER_EXTERNAL_SERVER_ONLINE_LIST, desc = "查询在线外部服务列表")
    public final static short SERVER_EXTERNAL_SERVER_ONLINE_LIST = 23;
    @Desc(key = ProtocolType.SERVER_EXTERNAL_SERVER_CLIENT_ONLINE_LIST, desc = "查询在线外部服务关联的全部订购列表,StringPacket(服务名)，返回终端id逗号表达示列表,一次2000个，最后一个包total:totalSize 代表发送结束")
    public final static short SERVER_EXTERNAL_SERVER_CLIENT_ONLINE_LIST = 123;

    @Desc(key = ProtocolType.SERVER_EXTERNAL_SERVER_DATA_STRING, desc = "发给外部服务的string类型数据")
    public final static short SERVER_EXTERNAL_SERVER_DATA_STRING = 121;
    @Desc(key = ProtocolType.SERVER_EXTERNAL_CLIENT_DATA_STRING, desc = "外部服务发给客户端的string类型数据")
    public final static short SERVER_EXTERNAL_CLIENT_DATA_STRING = 122;

    @Desc(key = ProtocolType.SERVER_PROXY_REGISTER, desc = "代理服务注册,StringPacket(uuc.witaction:6413,uuc.witaction:6413;tsetProxy;服务描述信息及附加字段)")
    public final static short SERVER_PROXY_REGISTER = 24;
    @Desc(key = ProtocolType.SERVER_PROXY_CLIENT_DATA, desc = "通过代理服务发给终端的数据")
    public final static short SERVER_PROXY_CLIENT_DATA = 25;
    @Desc(key = ProtocolType.SERVER_PROXY_SERVER_DATA, desc = "通过代理服务发给服务端的数据")
    public final static short SERVER_PROXY_SERVER_DATA = 26;
    @Desc(key = ProtocolType.SERVER_PROXY_LIST_QUERY, desc = "从主服务器查询被代理的服务器列表(StringPacket)")
    public final static short SERVER_PROXY_LIST_QUERY = 27;


    //聊天相关协议
    public static final short Msg_LoginRequest = 10000;//聊天服务登录
    public static final short Msg_StatusNotify = 10001;//同一账号不同类型终端上下线通知
    public static final short Msg_KickNotify = 10009;//同一账号相同类型设备登录，强制老设备下线通知
    public static final short Msg_SendRequest = 10002;//聊天信息发送请求
    public static final short Msg_Notify = 10003;//服务器转发的聊天消息
    public static final short Msg_FontSetRequest = 10004;//用户聊天消息字体设置请求
    public static final short Msg_FontNotify = 10005;//用户聊天消息字体设置转发
    public static final short Msg_OfflineRequest = 10006;//拉取离线消息请求
    public static final short Msg_GroupJoinRequest = 10007;//加入聊天室
    public static final short Msg_FontSubscribeRequest = 10008;//用户聊天消息字体设置状态订购
    public static final short Msg_LastMsgRequest = 10010;//拉取最近N条收发消息
    public static final short Msg_GroupExitRequest = 10011;//退出聊天室
    public static final short Msg_GroupInviteKickRequest = 10012;//邀请或踢出群请求
    public static final short Msg_GroupInviteKickNotify = 10013;//邀请或踢出群通知
    public static final short Msg_CancelRequest = 10014;//消息撤销请求
    public static final short Msg_CancelNotify = 10015;//消息撤销通知
    public static final short Msg_TimeRequest = 10016;//消息服务器当前时间查询


    @Desc(key = ProtocolType.MAX, desc = "保留值最大值")
    public static final short MAX = 32767;
    @Desc(key = ProtocolType.MIN, desc = "保留值最小值")
    public static final short MIN = -32768;

    public static void main(String[] args) throws Exception {
        PacketCheckUitl.main(null);
    }
}
