package tech.huit.uuc.message;

import edu.dbke.socket.cp.ProtocolType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.huit.socket.cp.message.Msg;
import tech.huit.socket.nio.server.NioServer;

import java.io.Serializable;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 1对1聊天用户
 *
 * @author huitang
 */
public class MessageUser implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(GroupMessageUser.class);
    private int uid;//用户uid
    private String nickname;//用户昵称
    private Set<UserDevice> userDevices = Collections.synchronizedSet(new HashSet<>());//用户链接
    private List<Msg.Notify> messagesRecv = new Vector<>();//用户收到p2p离线消息
    private List<Msg.Notify> messagesSend = new Vector<>();//记录用户发送的消息
    private Msg.FontNotify fontNotify;//用户当前设置的字体（只记录最近一次设置的值）
    private Set<Integer> fontSubscribe = Collections.synchronizedSet(new HashSet<>());//用户字体信息订购
    private Set<String> joinGroup = Collections.synchronizedSet(new HashSet<>());//加入的群
    private AtomicInteger recvSn = new AtomicInteger(-1);//p2p接收消息序号
    private AtomicInteger sendSn = new AtomicInteger(-1);//发送消息序号
    private volatile boolean isSendMax = false;
    private volatile boolean isRecvMax = false;

    public MessageUser() {
    }

    public MessageUser(int uid) {
        this.uid = uid;
    }

    public MessageUser(int uid, UserDevice userSocket) {
        this.uid = uid;
        this.userDevices.add(userSocket);
    }

    public MessageUser(int uid, String nickname, Msg.DeviceType deviceType, SocketChannel socket) {
        this.uid = uid;
        this.nickname = nickname;
        this.userDevices.add(new UserDevice(socket, deviceType));
    }

    public Msg.FontNotify getFontNotify() {
        return fontNotify;
    }

    public void setFontNotify(Msg.FontNotify fontNotify) {
        this.fontNotify = fontNotify;
    }

    @Override
    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof MessageUser))
            return false;
        MessageUser castOther = (MessageUser) other;

        return new EqualsBuilder().append(uid, castOther.getUid()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(uid).hashCode();
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }


    public void addSendMsg(Msg.Notify.Builder builder) {
        int offset = SerialNumberUtil.getNextSerialNumber(sendSn);
        builder.setSerialNumber(offset);
        Msg.Notify notify = builder.build();
        if (isSendMax) {//已经进入循环队列逻辑 ,高并发写会导致丢消息，所以至处必须是同步的
            messagesSend.set(offset, notify);//加进发送消息队列
        } else {
            messagesSend.add(notify);
        }
        if (offset == SerialNumberUtil.MAX_SERIAL_NUMBER) {
            isSendMax = true;
        }
    }

    public int sendNotify(NioServer server, Msg.Notify.Builder builder) {
        int offset = SerialNumberUtil.getNextSerialNumber(recvSn);
        builder.setSerialNumber(offset);
        Msg.Notify notify = builder.build();
        if (isRecvMax) {//已经进入循环队列逻辑
            messagesRecv.set(offset, notify);//加进离线消息
        } else {
            messagesRecv.add(notify);
        }
        if (offset == SerialNumberUtil.MAX_SERIAL_NUMBER) {
            isRecvMax = true;
        }
        synchronized (userDevices) {
            for (UserDevice device : userDevices) {
                server.send(device.getSocket(), notify, ProtocolType.Msg_Notify);
            }
        }
        return offset;
    }

    public  UserDevice getUserDevice(SocketChannel socket) {
        for (UserDevice device : userDevices) {
            if (device.getSocket() == socket) {
                return device;
            }
        }
        return null;
    }

    public void sendFontNotify(NioServer server, Msg.FontNotify fontNotify) {
        synchronized (userDevices) {
            for (UserDevice device : userDevices) {
                server.send(device.getSocket(), fontNotify, ProtocolType.Msg_FontNotify);
            }
        }
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Set<UserDevice> getUserDevices() {
        return userDevices;
    }

    public void setUserDevices(Set<UserDevice> userDevices) {
        this.userDevices = userDevices;
    }

    public List<Msg.Notify> getMessagesRecv() {
        return messagesRecv;
    }

    public void setMessagesRecv(List<Msg.Notify> messagesRecv) {
        this.messagesRecv = messagesRecv;
    }

    public AtomicInteger getRecvSn() {
        return recvSn;
    }

    public void setRecvSn(AtomicInteger recvSn) {
        this.recvSn = recvSn;
    }

    public Set<Integer> getFontSubscribe() {
        return fontSubscribe;
    }

    public List<Msg.Notify> getMessagesSend() {
        return messagesSend;
    }

    public void setMessagesSend(List<Msg.Notify> messagesSend) {
        this.messagesSend = messagesSend;
    }

    public Set<String> getJoinGroup() {
        return joinGroup;
    }

    public void setJoinGroup(Set<String> joinGroup) {
        this.joinGroup = joinGroup;
    }

    public AtomicInteger getSendSn() {
        return sendSn;
    }

    public boolean cancelMessageNotify(NioServer server, Integer socketUid, Msg.CancelRequest packet) {
        Msg.Notify message = null;
        if (packet.getMsgId() < messagesRecv.size()) {
            message = messagesRecv.get(packet.getMsgId());
        }
        if (null != message) {
            if (socketUid != message.getSenderUid()) {
                logger.error("cancelMsgUidError->msgUid:{},socketUid:{} msgId:{}", message.getSenderUid(), socketUid, packet.getMsgId());
                return false;
            }
        } else {
            logger.error("cancelMsgIdError->msgId:{},msgSize:{}", packet.getMsgId(), messagesRecv.size());
            return false;
        }

        Msg.Notify.Builder cancelMsg = Msg.Notify.newBuilder()
                .setGroupNum(message.getGroupNum())
                .setMsg("消息被撤回")
                .setType(message.getType())
                .setSenderUid(message.getSenderUid())
                .setSerialNumber(packet.getMsgId())
                .setTime((int) (System.currentTimeMillis() / 1000))
                .setReceiverNum(message.getReceiverNum())
                .setStatus(1);

        messagesRecv.set(packet.getMsgId(), cancelMsg.build());

        Msg.CancelNotify cancelNotify = Msg.CancelNotify.newBuilder()
                .setGroupNum(packet.getGroupNum()).setMsgId(packet.getMsgId()).build();

        synchronized (userDevices) {
            for (UserDevice device : userDevices) {
                server.send(device.getSocket(), cancelNotify, ProtocolType.Msg_CancelNotify);
            }
        }
        return true;
    }
}
