package tech.huit.uuc.message;

import edu.dbke.socket.cp.ProtocolType;
import edu.dbke.socket.cp.util.ByteUtil;
import org.junit.Assert;
import org.junit.Test;
import tech.huit.socket.cp.Base;
import tech.huit.socket.cp.message.Msg;
import tech.huit.conf.SystemConf;
import tech.huit.util.encrypt.AESUtil;
import tech.huit.util.encrypt.InvalidTokenException;
import tech.huit.util.encrypt.UserLoginInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

public class MsgSpeedTest {
//        private static String host = "localhost";
    private static String host = "uuc.huit.tech";
//        private static int port = 6413;
    private static int port = 6415;
    private long testCount = 20 * 10000;


    /**
     * 测试发送和接收离线消息
     */
    @Test
    public void testRecvMsg() throws Exception {
        String token = "5PveXM7AZR3Q2b6MTN61TNOBsl5t0MSDQSjd4mVb5LM";//{uid=1, nickname='uid1', timestamp=1497536453}
        Socket socket = new Socket(host, port);
        InputStream is = socket.getInputStream();
        OutputStream os = socket.getOutputStream();
        Msg.LoginRequest.Builder builder = Msg.LoginRequest.newBuilder().setToken(token).setDeviceType(Msg.DeviceType.MOBILE);
        os.write(ByteUtil.convertPacketBytes(builder, ProtocolType.Msg_LoginRequest));
        Base.ResponseStatus rs = (Base.ResponseStatus) ByteUtil.readMessageLite(is, Base.ResponseStatus.getDefaultInstance(), ProtocolType.Msg_LoginRequest);
        Assert.assertTrue(rs.getStatus());//第1个链接
        System.out.println("接收设备MOBILE链接成功");
        long count = 0, lostCountTime = System.currentTimeMillis(), beginTime = System.currentTimeMillis();
        for (long i = 0; i < testCount; i++) {
            Msg.Notify notify = (Msg.Notify) ByteUtil.readMessageLite(is, Msg.Notify.getDefaultInstance(), ProtocolType.Msg_Notify);
            count++;
            Assert.assertEquals(2, notify.getSenderUid());
//            System.out.println("recv:" + notify);
            if (count % 10000 == 0) {
                System.out.println("recvCount:" + i + " speed:" + 10000 / (System.currentTimeMillis() - lostCountTime) * 1000);
                lostCountTime = System.currentTimeMillis();
            }
        }
        System.out.println("total recv count:" + count + " time:" + new Date() + " speed:" + testCount / 1.0 / (System.currentTimeMillis() - beginTime) * 1000);

    }

    /**
     * 测试发送和接收离线消息
     */
    @Test
    public void testSendMsg() throws Exception {
        String token = "acFvGpEmD0Hgb6_VE2TnnkY9udTlupHfj_ltuzOmpeQ";//{uid=2, nickname='uid2', timestamp=1497536411}
        Socket socket = new Socket(host, port);
        InputStream is = socket.getInputStream();
        OutputStream os = socket.getOutputStream();
        Msg.LoginRequest.Builder builder = Msg.LoginRequest.newBuilder().setToken(token).setDeviceType(Msg.DeviceType.MOBILE);
        os.write(ByteUtil.convertPacketBytes(builder, ProtocolType.Msg_LoginRequest));
        Base.ResponseStatus rs = (Base.ResponseStatus) ByteUtil.readMessageLite(is, Base.ResponseStatus.getDefaultInstance(), ProtocolType.Msg_LoginRequest);
        Assert.assertTrue(rs.getStatus());//第1个链接
        System.out.println("发送设备MOBILE链接成功");
        Msg.SendRequest.Builder msg = Msg.SendRequest.newBuilder().setMsg("hello 中国").setMsgId(-1).setReceiverNum(1);
        byte[] data = ByteUtil.convertPacketBytes(msg, ProtocolType.Msg_SendRequest);

        long count = 0, lastCountTime = System.currentTimeMillis(), beginTime = System.currentTimeMillis();
        for (long i = 0; i < testCount; i++) {
            os.write(data);
//            rs = (Base.ResponseStatus) ByteUtil.readMessageLite(is, Base.ResponseStatus.getDefaultInstance(), ProtocolType.Msg_SendRequest);
//            Assert.assertTrue(rs.getStatus());//发送消息成功
            count++;
//            System.out.println("send:" + msg);
            if (count % 10000 == 0) {
//            if (count % 100 == 0) {
                System.out.println("sendCount:" + i + " speed:" + 10000 / 1.0 / (System.currentTimeMillis() - lastCountTime) * 1000);
                lastCountTime = System.currentTimeMillis();
            }
        }
        System.out.println("total send count:" + count + " time:" + new Date() + " speed:" + testCount / 1.0 / (System.currentTimeMillis() - beginTime) * 1000);
    }
}
