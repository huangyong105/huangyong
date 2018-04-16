package tech.huit.uuc.message;

import edu.dbke.socket.cp.ProtocolType;
import edu.dbke.socket.cp.util.ByteUtil;
import org.junit.Assert;
import org.junit.Test;
import tech.huit.socket.cp.Base;
import tech.huit.socket.cp.message.Msg;
import tech.huit.conf.SystemConf;
import tech.huit.util.encrypt.UserLoginInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MsgServiceTest {
//    private static String host = "10.0.6.200";
    private static String host = "localhost";
//    private static String host = "47.95.37.150";
    private static int port = 6413;
    private String key = SystemConf.get("auth.aes.key");

//
//    @Autowired
//    AppService appService;
//    App app = new App();
//
//    @Before
//    public void init() throws Exception {
//        app.setName("Im测试App");
//        app.setAesKey(AESUtil.genRandomKey());
//        appService.insert(app);
//    }
//
//    @After
//    public void destroy() throws Exception {
//        appService.deleteById(app.getId());
//    }

    /**
     * 账号登录逻辑测试
     *
     * @throws Exception
     */
    @Test
    public void loginRequestTest() throws Exception {
        String token = getToken(1);
        Socket socket = new Socket(host, port);
        InputStream is = socket.getInputStream();
        OutputStream os = socket.getOutputStream();
        Msg.LoginRequest.Builder builder = Msg.LoginRequest.newBuilder().setToken(token).setDeviceType(Msg.DeviceType.MOBILE);
        byte[] packet = ByteUtil.convertPacketBytes(builder, ProtocolType.Msg_LoginRequest);
        os.write(packet);
        Base.ResponseStatus rs = (Base.ResponseStatus) ByteUtil.readMessageLite(is, Base.ResponseStatus.getDefaultInstance(), ProtocolType.Msg_LoginRequest);
        Assert.assertTrue(rs.getStatus());//第1个链接
        System.out.println("第1个设备MOBILE链接成功");

        Socket socket2 = new Socket(host, port);//第2个链接
        builder.setDeviceType(Msg.DeviceType.PAD);
        socket2.getOutputStream().write(ByteUtil.convertPacketBytes(builder, ProtocolType.Msg_LoginRequest));
        rs = (Base.ResponseStatus) ByteUtil.readMessageLite(socket2.getInputStream(), Base.ResponseStatus.getDefaultInstance(), ProtocolType.Msg_LoginRequest);
        Assert.assertTrue(rs.getStatus());
        System.out.println("第2个设备PAD链接成功");

        Thread.sleep(500);

        Msg.StatusNotify statusNotify = (Msg.StatusNotify) ByteUtil.readMessageLite(is, Msg.StatusNotify.getDefaultInstance(), ProtocolType.Msg_StatusNotify);
        Assert.assertTrue(statusNotify.getIsOnline()); //应该收到终端上线的通知
        Assert.assertEquals(Msg.DeviceType.PAD, statusNotify.getDeviceType());
        System.out.println("第1个设备MOBILE收到第2个设备PAD上线通知");

        Socket socket3 = new Socket(host, port);//第3个链接
        builder.setDeviceType(Msg.DeviceType.PC);
        socket3.getOutputStream().write(ByteUtil.convertPacketBytes(builder, ProtocolType.Msg_LoginRequest));
        rs = (Base.ResponseStatus) ByteUtil.readMessageLite(socket3.getInputStream(), Base.ResponseStatus.getDefaultInstance(), ProtocolType.Msg_LoginRequest);
        Assert.assertTrue(rs.getStatus());
        System.out.println("第3个设备PC链接成功");

        statusNotify = (Msg.StatusNotify) ByteUtil.readMessageLite(is, Msg.StatusNotify.getDefaultInstance(), ProtocolType.Msg_StatusNotify);
        Assert.assertEquals(Msg.DeviceType.PC, statusNotify.getDeviceType());
        Assert.assertTrue(statusNotify.getIsOnline()); //应该收到终端上线的通知
        System.out.println("第1个设备MOBILE收到第3个设备PC上线通知");

        statusNotify = (Msg.StatusNotify) ByteUtil.readMessageLite(socket2.getInputStream(), Msg.StatusNotify.getDefaultInstance(), ProtocolType.Msg_StatusNotify);
        Assert.assertTrue(statusNotify.getIsOnline()); //应该收到终端上线的通知
        Assert.assertEquals(Msg.DeviceType.PC, statusNotify.getDeviceType());
        System.out.println("第2个设备PAD收到第3个设备PC上线通知");

        socket2.close();
        System.out.println("第2个设备关闭链接");
        statusNotify = (Msg.StatusNotify) ByteUtil.readMessageLite(is, Msg.StatusNotify.getDefaultInstance(), ProtocolType.Msg_StatusNotify);
        Assert.assertTrue(!statusNotify.getIsOnline()); //应该收到终端下线的通知
        Assert.assertEquals(Msg.DeviceType.PAD, statusNotify.getDeviceType());
        System.out.println("第1个设备收到第2个设备下线通知");

        statusNotify = (Msg.StatusNotify) ByteUtil.readMessageLite(socket3.getInputStream(), Msg.StatusNotify.getDefaultInstance(), ProtocolType.Msg_StatusNotify);
        Assert.assertTrue(!statusNotify.getIsOnline()); //应该收到终端下线的通知
        Assert.assertEquals(Msg.DeviceType.PAD, statusNotify.getDeviceType());
        System.out.println("第3个设备收到第2个设备下线通知");


        Socket socket4 = new Socket(host, port);//第4个链接
        builder.setDeviceType(Msg.DeviceType.PC);
        socket4.getOutputStream().write(ByteUtil.convertPacketBytes(builder, ProtocolType.Msg_LoginRequest));
        rs = (Base.ResponseStatus) ByteUtil.readMessageLite(socket4.getInputStream(), Base.ResponseStatus.getDefaultInstance(), ProtocolType.Msg_LoginRequest);
        Assert.assertTrue(rs.getStatus());
        System.out.println("第4个设备PC链接成功");

        Msg.KickNotify kickNotify = (Msg.KickNotify) ByteUtil.readMessageLite(socket3.getInputStream(), Msg.KickNotify.getDefaultInstance(), ProtocolType.Msg_KickNotify);
        Assert.assertTrue(kickNotify.getMsg().length() > 0); //应该收到终端下线的通知
        System.out.println("第3个设备PC收到第4个设备PC踢自己下线的通知");

        socket3.getOutputStream().write(ByteUtil.convertPacketBytes(builder, ProtocolType.Msg_LoginRequest));
        rs = (Base.ResponseStatus) ByteUtil.readMessageLite(socket3.getInputStream(), Base.ResponseStatus.getDefaultInstance(), ProtocolType.Msg_LoginRequest);
        Assert.assertTrue(rs.getStatus());
        System.out.println("第3个设备PC再次登录成功");
        kickNotify = (Msg.KickNotify) ByteUtil.readMessageLite(socket4.getInputStream(), Msg.KickNotify.getDefaultInstance(), ProtocolType.Msg_KickNotify);
        Assert.assertTrue(kickNotify.getMsg().length() > 0); //应该收到终端下线的通知
        System.out.println("第4个设备PC收到第3个设备PC踢自己下线的通知");

        socket4.getOutputStream().write(ByteUtil.convertPacketBytes(builder, ProtocolType.Msg_LoginRequest));
        rs = (Base.ResponseStatus) ByteUtil.readMessageLite(socket4.getInputStream(), Base.ResponseStatus.getDefaultInstance(), ProtocolType.Msg_LoginRequest);
        Assert.assertTrue(rs.getStatus());
        System.out.println("第4个设备PC再次登录成功");
        kickNotify = (Msg.KickNotify) ByteUtil.readMessageLite(socket3.getInputStream(), Msg.KickNotify.getDefaultInstance(), ProtocolType.Msg_KickNotify);
        Assert.assertTrue(kickNotify.getMsg().length() > 0); //应该收到终端下线的通知
        System.out.println("第3个设备PC收到第4个设备PC踢自己下线的通知");

        Msg.SendRequest.Builder msg = Msg.SendRequest.newBuilder().setMsg("hello 中国").setMsgId(1).setReceiverNum(1);
        socket4.getOutputStream().write(ByteUtil.convertPacketBytes(msg, ProtocolType.Msg_SendRequest));

        Msg.Notify notify = (Msg.Notify) ByteUtil.readMessageLite(socket4.getInputStream(), Msg.Notify.getDefaultInstance(), ProtocolType.Msg_Notify);
        Assert.assertEquals(1, notify.getSenderUid());
        System.out.println("第4个设备PC收到第4个设备PC发送的p2p消息");

        rs = (Base.ResponseStatus) ByteUtil.readMessageLite(socket4.getInputStream(), Base.ResponseStatus.getDefaultInstance(), ProtocolType.Msg_SendRequest);
        Assert.assertTrue(rs.getStatus());//发送消息成功
        Assert.assertTrue(rs.getData().split("&").length == 3);
        System.out.println("第4个设备PC发送消息成功");

        notify = (Msg.Notify) ByteUtil.readMessageLite(is, Msg.Notify.getDefaultInstance(), ProtocolType.Msg_Notify);
        Assert.assertEquals(1, notify.getSenderUid());
        System.out.println("第1个设备收到第4个设备发送的p2p消息");

        Msg.GroupJoinRequest gjr = Msg.GroupJoinRequest.newBuilder().addGroupNum("1").build();
        socket4.getOutputStream().write(ByteUtil.convertPacketBytes(gjr, ProtocolType.Msg_GroupJoinRequest));
        rs = (Base.ResponseStatus) ByteUtil.readMessageLite(socket4.getInputStream(), Base.ResponseStatus.getDefaultInstance(), ProtocolType.Msg_GroupJoinRequest);
        Assert.assertTrue(rs.getStatus());//发送消息成功
        System.out.println("第4个设备PC加入聊天室成功");
    }

    /**
     * 消息收发测试
     *
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void msgSendRecvTest() throws Exception {
        String token = getToken(1);
        String groupIum = "1";
        Socket socket = new Socket(host, port);
        InputStream is = socket.getInputStream();
        OutputStream os = socket.getOutputStream();
        Msg.LoginRequest.Builder builder = Msg.LoginRequest.newBuilder().setToken(token).setDeviceType(Msg.DeviceType.MOBILE);
        os.write(ByteUtil.convertPacketBytes(builder, ProtocolType.Msg_LoginRequest));
        Base.ResponseStatus rs = (Base.ResponseStatus) ByteUtil.readMessageLite(is, Base.ResponseStatus.getDefaultInstance(), ProtocolType.Msg_LoginRequest);
        Assert.assertTrue(rs.getStatus());
        System.out.println("设备1链接成功");

        Msg.GroupJoinRequest gjr = Msg.GroupJoinRequest.newBuilder().addGroupNum(groupIum).build();
        os.write(ByteUtil.convertPacketBytes(gjr, ProtocolType.Msg_GroupJoinRequest));
        rs = (Base.ResponseStatus) ByteUtil.readMessageLite(is, Base.ResponseStatus.getDefaultInstance(), ProtocolType.Msg_GroupJoinRequest);
        Assert.assertTrue(rs.getStatus());
        System.out.println("设备1加入聊天室成功");

        String token2 = getToken(2);//{uid=2, nickname='uid2', timestamp=1497939893}
        Socket socket2 = new Socket(host, port);
        InputStream is2 = socket2.getInputStream();
        OutputStream os2 = socket2.getOutputStream();
        builder = Msg.LoginRequest.newBuilder().setToken(token2).setDeviceType(Msg.DeviceType.MOBILE);
        os2.write(ByteUtil.convertPacketBytes(builder, ProtocolType.Msg_LoginRequest));
        rs = (Base.ResponseStatus) ByteUtil.readMessageLite(is2, Base.ResponseStatus.getDefaultInstance(), ProtocolType.Msg_LoginRequest);
        Assert.assertTrue(rs.getStatus());
        System.out.println("设备2链接成功");

        gjr = Msg.GroupJoinRequest.newBuilder().addGroupNum("1").build();
        os2.write(ByteUtil.convertPacketBytes(gjr, ProtocolType.Msg_GroupJoinRequest));
        rs = (Base.ResponseStatus) ByteUtil.readMessageLite(is2, Base.ResponseStatus.getDefaultInstance(), ProtocolType.Msg_GroupJoinRequest);
        Assert.assertTrue(rs.getStatus());
        System.out.println("设备2加入聊天室成功");

        Msg.SendRequest msg = Msg.SendRequest.newBuilder().setMsg("hello 中国 groupMsg").setMsgId(1).setReceiverNum(1).setGroupNum(groupIum).build();
        os2.write(ByteUtil.convertPacketBytes(msg, ProtocolType.Msg_SendRequest));

        Msg.Notify notify = (Msg.Notify) ByteUtil.readMessageLite(is, Msg.Notify.getDefaultInstance(), ProtocolType.Msg_Notify);
        Assert.assertEquals(msg.getMsg(), notify.getMsg());
        Assert.assertTrue(notify.getTime() != 0);
        int time1 = notify.getTime();
        System.out.println("设备1收到聊天室消息");

        notify = (Msg.Notify) ByteUtil.readMessageLite(is2, Msg.Notify.getDefaultInstance(), ProtocolType.Msg_Notify);
        Assert.assertEquals(2, notify.getSenderUid());
        Assert.assertEquals(msg.getGroupNum(), notify.getGroupNum());
        Assert.assertEquals(msg.getMsg(), notify.getMsg());
        Assert.assertEquals(time1, notify.getTime());
        System.out.println("设备2收到聊天室消息");

        rs = (Base.ResponseStatus) ByteUtil.readMessageLite(is2, Base.ResponseStatus.getDefaultInstance(), ProtocolType.Msg_SendRequest);
        Assert.assertEquals(1, msg.getMsgId());
        Assert.assertTrue(rs.getStatus());
        System.out.println("设备2发送聊天室消息成功");
        String msgRes = rs.getData();

        //消息撤销
        String[] infos = msgRes.split("&");
        Msg.CancelRequest cancelRequest = Msg.CancelRequest.newBuilder().setGroupNum(groupIum).setMsgId(Integer.valueOf(infos[2])).build();
        os2.write(ByteUtil.convertPacketBytes(cancelRequest, ProtocolType.Msg_CancelRequest));

        rs = (Base.ResponseStatus) ByteUtil.readMessageLite(is2, Base.ResponseStatus.getDefaultInstance(), ProtocolType.Msg_CancelRequest);
        Assert.assertTrue(rs.getStatus());

        msg = Msg.SendRequest.newBuilder().setMsg("hello 中国 p2pMsg").setMsgId(1).setReceiverNum(1).build();
        os2.write(ByteUtil.convertPacketBytes(msg, ProtocolType.Msg_SendRequest));
        rs = (Base.ResponseStatus) ByteUtil.readMessageLite(is2, Base.ResponseStatus.getDefaultInstance(), ProtocolType.Msg_SendRequest);
        Assert.assertEquals(1, msg.getMsgId());
        Assert.assertTrue(rs.getStatus());
        System.out.println("设备2发送p2p消息成功");

        notify = (Msg.Notify) ByteUtil.readMessageLite(is, Msg.Notify.getDefaultInstance(), ProtocolType.Msg_Notify);
        Assert.assertEquals(msg.getMsg(), notify.getMsg());
        Assert.assertEquals(msg.getGroupNum(), notify.getGroupNum());
        Assert.assertTrue(notify.getTime() != 0);
        System.out.println("设备1收到p2p消息");
    }

    private String getToken(int uid) {
        UserLoginInfo loginInfo = new UserLoginInfo();
        loginInfo.setUid(uid);
        loginInfo.setNickname("IM测试账号" + uid);
        loginInfo.setAppUid(uid + "");
        loginInfo.setExpirationTime(Integer.MAX_VALUE);
        return loginInfo.genUucToken(key);
    }

    /**
     * 字体设置测试
     *
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void fontSetTest() throws Exception {
        String token = getToken(1);//{uid=1, nickname='uid1', timestamp=1497536453}
        Socket socket = new Socket(host, port);
        InputStream is = socket.getInputStream();
        OutputStream os = socket.getOutputStream();
        Msg.LoginRequest.Builder builder = Msg.LoginRequest.newBuilder().setToken(token).setDeviceType(Msg.DeviceType.MOBILE);
        os.write(ByteUtil.convertPacketBytes(builder, ProtocolType.Msg_LoginRequest));
        Base.ResponseStatus rs = (Base.ResponseStatus) ByteUtil.readMessageLite(is, Base.ResponseStatus.getDefaultInstance(), ProtocolType.Msg_LoginRequest);
        Assert.assertTrue(rs.getStatus());//第1个链接
        System.out.println("设备1链接成功");

        Msg.FontSetRequest.Builder font = Msg.FontSetRequest.newBuilder().setFont("微软雅黑");
        os.write(ByteUtil.convertPacketBytes(font, ProtocolType.Msg_FontSetRequest));
        rs = (Base.ResponseStatus) ByteUtil.readMessageLite(is, Base.ResponseStatus.getDefaultInstance(), ProtocolType.Msg_FontSetRequest);
        Assert.assertTrue(rs.getStatus());
        System.out.println("设备1字体设置成功");

        String token2 = getToken(2);//{uid=2, nickname='uid2', timestamp=1497939893}
        Socket socket2 = new Socket(host, port);
        InputStream is2 = socket2.getInputStream();
        OutputStream os2 = socket2.getOutputStream();
        builder = Msg.LoginRequest.newBuilder().setToken(token2).setDeviceType(Msg.DeviceType.MOBILE);
        os2.write(ByteUtil.convertPacketBytes(builder, ProtocolType.Msg_LoginRequest));
        rs = (Base.ResponseStatus) ByteUtil.readMessageLite(is2, Base.ResponseStatus.getDefaultInstance(), ProtocolType.Msg_LoginRequest);
        Assert.assertTrue(rs.getStatus());
        System.out.println("设备2链接成功");

        Msg.FontSubscribeRequest subscribeRequest = Msg.FontSubscribeRequest.newBuilder().addUids(1).addUids(2).addUids(5).build();
        os2.write(ByteUtil.convertPacketBytes(subscribeRequest, ProtocolType.Msg_FontSubscribeRequest));
        Msg.FontSubscribeResponse fontRs = (Msg.FontSubscribeResponse) ByteUtil.readMessageLite(is2, Msg.FontSubscribeResponse.getDefaultInstance(), ProtocolType.Msg_FontSubscribeRequest);
        Assert.assertTrue(fontRs.getResponseStatus().getStatus());
        Assert.assertEquals(font.getFont(), fontRs.getMessageFonts(0).getFont());
        System.out.println("设备2订购消息字体成功");

        font.setFont("修改一下字体");
        font.setFontSize(15);
        os.write(ByteUtil.convertPacketBytes(font, ProtocolType.Msg_FontSetRequest));
        rs = (Base.ResponseStatus) ByteUtil.readMessageLite(is, Base.ResponseStatus.getDefaultInstance(), ProtocolType.Msg_FontSetRequest);
        Assert.assertTrue(rs.getStatus());
        System.out.println("设备1字体更新成功");

        Msg.FontNotify notify = (Msg.FontNotify) ByteUtil.readMessageLite(is2, Msg.FontNotify.getDefaultInstance(), ProtocolType.Msg_FontNotify);
        System.out.println(notify);
        Assert.assertEquals(1, notify.getUid());
        Assert.assertEquals(font.getFont(), notify.getFont());
        Assert.assertEquals(font.getFontSize(), notify.getFontSize());
        System.out.println("设备2收到设备1消息字体更新通知");
    }


    /**
     * 测试发送和接收离线消息
     */
    @Test
    public void offlineRequestTest() throws Exception {
        String groupNum = "1";
        String token = getToken(1);//{uid=1, nickname='uid1', timestamp=1497536453}
        Socket socket = new Socket(host, port);
        InputStream is = socket.getInputStream();
        OutputStream os = socket.getOutputStream();
        Msg.LoginRequest.Builder builder = Msg.LoginRequest.newBuilder().setToken(token).setDeviceType(Msg.DeviceType.MOBILE);
        os.write(ByteUtil.convertPacketBytes(builder, ProtocolType.Msg_LoginRequest));
        Base.ResponseStatus rs = (Base.ResponseStatus) ByteUtil.readMessageLite(is, Base.ResponseStatus.getDefaultInstance(), ProtocolType.Msg_LoginRequest);
        Assert.assertTrue(rs.getStatus());//第1个链接
        System.out.println("设备1链接成功");

        for (int i = 0; i < 10; i++) {
            Msg.SendRequest.Builder msg = Msg.SendRequest.newBuilder().setMsg("hello 中国" + i).setMsgId(-1).setReceiverNum(2);
            os.write(ByteUtil.convertPacketBytes(msg, ProtocolType.Msg_SendRequest));
            Msg.SendRequest.Builder group = Msg.SendRequest.newBuilder().setMsg("hello 中国" + i).setMsgId(-1).setReceiverNum(1).setGroupNum(groupNum);//GROUP
            os.write(ByteUtil.convertPacketBytes(group, ProtocolType.Msg_SendRequest));
        }
        System.out.println("设备1给设备2发送了10条p2p和group消息");


        String token2 = getToken(2);//{uid=2, nickname='uid2', timestamp=1497939893}
        Socket socket2 = new Socket(host, port);
        InputStream is2 = socket2.getInputStream();
        OutputStream os2 = socket2.getOutputStream();
        builder = Msg.LoginRequest.newBuilder().setToken(token2).setDeviceType(Msg.DeviceType.MOBILE);
        os2.write(ByteUtil.convertPacketBytes(builder, ProtocolType.Msg_LoginRequest));
        rs = (Base.ResponseStatus) ByteUtil.readMessageLite(is2, Base.ResponseStatus.getDefaultInstance(), ProtocolType.Msg_LoginRequest);
        Assert.assertTrue(rs.getStatus());
        System.out.println("设备2链接成功");

        Msg.OfflineRequest or = Msg.OfflineRequest.newBuilder().setP2POffset(0).addGroupOffsets(Msg.GroupOffset.newBuilder().setGroupNum(groupNum).setGroupOffset(0)).build();
        os2.write(ByteUtil.convertPacketBytes(or, ProtocolType.Msg_OfflineRequest));
        Msg.OfflineResponse response = (Msg.OfflineResponse) ByteUtil.readMessageLite(is2, Msg.OfflineResponse.getDefaultInstance(), ProtocolType.Msg_OfflineRequest);
        Assert.assertTrue(response.getResponseStatus().getStatus());
        System.out.println("设备2拉取离线消息成功 msgSize:" + response.getMessagsRecvCount());
        Assert.assertTrue(response.getMessagsRecvCount() > 2);//多次测试后数量会大于20

        socket.close();
        socket2.close();
    }

    /**
     * 测试发送和接收的最近N条消息
     */
    @Test
    public void LastMsgRequestTest() throws Exception {
        String groupNum = "1";
        String token = getToken(1);//{uid=1, nickname='uid1', timestamp=1497536453}
        Socket socket = new Socket(host, port);
        InputStream is = socket.getInputStream();
        OutputStream os = socket.getOutputStream();
        Msg.LoginRequest.Builder builder = Msg.LoginRequest.newBuilder().setToken(token).setDeviceType(Msg.DeviceType.MOBILE);
        os.write(ByteUtil.convertPacketBytes(builder, ProtocolType.Msg_LoginRequest));
        Base.ResponseStatus rs = (Base.ResponseStatus) ByteUtil.readMessageLite(is, Base.ResponseStatus.getDefaultInstance(), ProtocolType.Msg_LoginRequest);
        Assert.assertTrue(rs.getStatus());//第1个链接
        System.out.println("设备1链接成功");

        for (int i = 1; i <= 10; i++) {
            Msg.SendRequest.Builder msg = Msg.SendRequest.newBuilder().setMsg("设备1发给设备2:" + i).setMsgId(-1).setReceiverNum(2);
            os.write(ByteUtil.convertPacketBytes(msg, ProtocolType.Msg_SendRequest));
            Msg.SendRequest.Builder group = Msg.SendRequest.newBuilder().setMsg("设备1发给讨论组1:" + i).setMsgId(-1).setGroupNum(groupNum);//GROUP
            os.write(ByteUtil.convertPacketBytes(group, ProtocolType.Msg_SendRequest));
        }
        System.out.println("设备1给设备2发送了10条p2p和group消息");


        String token2 = getToken(2);//{uid=2, nickname='uid2', timestamp=1497939893}
        Socket socket2 = new Socket(host, port);
        InputStream is2 = socket2.getInputStream();
        OutputStream os2 = socket2.getOutputStream();
        builder = Msg.LoginRequest.newBuilder().setToken(token2).setDeviceType(Msg.DeviceType.MOBILE);
        os2.write(ByteUtil.convertPacketBytes(builder, ProtocolType.Msg_LoginRequest));
        rs = (Base.ResponseStatus) ByteUtil.readMessageLite(is2, Base.ResponseStatus.getDefaultInstance(), ProtocolType.Msg_LoginRequest);
        Assert.assertTrue(rs.getStatus());
        System.out.println("设备2链接成功");

        for (int i = 1; i <= 5; i++) {
            Msg.SendRequest.Builder msg = Msg.SendRequest.newBuilder().setMsg("备2发给设备1设:" + i).setMsgId(-1).setReceiverNum(1);
            os2.write(ByteUtil.convertPacketBytes(msg, ProtocolType.Msg_SendRequest));
            Msg.SendRequest.Builder group = Msg.SendRequest.newBuilder().setMsg("设备2发给讨论组" + groupNum + ":" + i).setMsgId(-1).setGroupNum(groupNum);//GROUP
            os2.write(ByteUtil.convertPacketBytes(group, ProtocolType.Msg_SendRequest));
        }

        System.out.println("设备2给设备1发送了5条p2p和group消息");

        Msg.GroupJoinRequest.Builder join = Msg.GroupJoinRequest.newBuilder().addGroupNum(groupNum);
        os2.write(ByteUtil.convertPacketBytes(join, ProtocolType.Msg_GroupJoinRequest));
        rs = (Base.ResponseStatus) ByteUtil.readMessageLite(is2, Base.ResponseStatus.getDefaultInstance(), ProtocolType.Msg_GroupJoinRequest);
        Assert.assertTrue(rs.getStatus());
        System.out.println("设备2加入聊天室1成功");


        Thread.sleep(500);//远程服务器处理有一定的时间差

        Msg.LastMsgRequest or = Msg.LastMsgRequest.newBuilder().setLastMsgRecvSize(5).setLastMsgSendSize(5).build();
        os2.write(ByteUtil.convertPacketBytes(or, ProtocolType.Msg_LastMsgRequest));
        Msg.OfflineResponse response = (Msg.OfflineResponse) ByteUtil.readMessageLite(is2, Msg.OfflineResponse.getDefaultInstance(), ProtocolType.Msg_LastMsgRequest);
        Assert.assertTrue(response.getResponseStatus().getStatus());
        System.out.println("设备2拉取离线消息成功 recvSize:" + response.getMessagsRecvCount() + " sendSize:" + response.getMessagsSendCount());
        System.out.println("recv-->count:" + response.getMessagsRecvList().size());
        for (Msg.Notify notify : response.getMessagsRecvList()) {
            System.out.println("sn:" + notify.getSerialNumber() + " msg:" + notify.getMsg()
                    + " ReceiverNum:" + notify.getReceiverNum()
                    + " groupNum:" + notify.getGroupNum());
        }
        Assert.assertTrue(response.getMessagsRecvCount() == 10);//收到5条p2p，5条群消息

        System.out.println("send-->count:" + response.getMessagsSendList().size());
        for (Msg.Notify notify : response.getMessagsSendList()) {
            System.out.println("sn:" + notify.getSerialNumber() + " msg:" + notify.getMsg());
        }
        Assert.assertTrue(response.getMessagsSendCount() > 0);//发送消息会按组过滤
        socket.close();
        socket2.close();
    }

    /**
     * 邀请入群踢出群测试
     */
    @Test
    public void GoupInvateKickRequestTest() throws Exception {
        String groupNum = "1";
        String token = getToken(1);//{uid=1, nickname='uid1', timestamp=1497536453}
        Socket socket = new Socket(host, port);
        InputStream is = socket.getInputStream();
        OutputStream os = socket.getOutputStream();
        Msg.LoginRequest.Builder builder = Msg.LoginRequest.newBuilder().setToken(token).setDeviceType(Msg.DeviceType.MOBILE);
        os.write(ByteUtil.convertPacketBytes(builder, ProtocolType.Msg_LoginRequest));
        Base.ResponseStatus rs = (Base.ResponseStatus) ByteUtil.readMessageLite(is, Base.ResponseStatus.getDefaultInstance(), ProtocolType.Msg_LoginRequest);
        Assert.assertTrue(rs.getStatus());//第1个链接
        System.out.println("设备1链接成功");

        String token2 = getToken(2);//{uid=2, nickname='uid2', timestamp=1497939893}
        Socket socket2 = new Socket(host, port);
        InputStream is2 = socket2.getInputStream();
        OutputStream os2 = socket2.getOutputStream();
        builder = Msg.LoginRequest.newBuilder().setToken(token2).setDeviceType(Msg.DeviceType.MOBILE);
        os2.write(ByteUtil.convertPacketBytes(builder, ProtocolType.Msg_LoginRequest));
        rs = (Base.ResponseStatus) ByteUtil.readMessageLite(is2, Base.ResponseStatus.getDefaultInstance(), ProtocolType.Msg_LoginRequest);
        Assert.assertTrue(rs.getStatus());
        System.out.println("设备2链接成功");

        Msg.GroupInviteKickRequest gikq = Msg.GroupInviteKickRequest.newBuilder().addGroupNum(groupNum).addUids(1).setIsInvite(true).setMsg("inviteTest").build();
        os2.write(ByteUtil.convertPacketBytes(gikq, ProtocolType.Msg_GroupInviteKickRequest));
        rs = (Base.ResponseStatus) ByteUtil.readMessageLite(is2, Base.ResponseStatus.getDefaultInstance(), ProtocolType.Msg_GroupInviteKickRequest);
        Assert.assertTrue(rs.getStatus());
        System.out.println("设备2邀请设备1成功");


        Msg.GroupInviteKickNotify notify = (Msg.GroupInviteKickNotify) ByteUtil.readMessageLite(is, Msg.GroupInviteKickNotify.getDefaultInstance(), ProtocolType.Msg_GroupInviteKickNotify);
        Assert.assertEquals(notify.getMsg(), gikq.getMsg());
        System.out.println("设备1收到入群邀请通知");

        socket.close();
        socket2.close();
    }
}
