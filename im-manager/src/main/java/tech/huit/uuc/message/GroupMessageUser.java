package tech.huit.uuc.message;

import com.google.protobuf.MessageLite;
import edu.dbke.socket.cp.ProtocolType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.huit.socket.cp.message.Msg;
import tech.huit.socket.nio.server.NioServer;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 群聊用户
 *
 * @author huitang
 */
public class GroupMessageUser implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(GroupMessageUser.class);
    private String groupId;//分组id
    private List<Msg.Notify> messages = new ArrayList<>();//离线消息
    private Set<MessageUser> users = Collections.synchronizedSet(new HashSet<>());
    private AtomicInteger recvSn = new AtomicInteger(-1);//消息id
    private volatile boolean isRecvMax = false;

    public GroupMessageUser(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Set<MessageUser> getUsers() {
        return users;
    }

    public  boolean checkExistUser (int uid) {
        for (MessageUser user : this.getUsers()) {
            if (user.getUid() ==  uid) {
                return true;
            }
        }
        return false;
    }

    public  MessageUser getExistUser (int uid) {
        for (MessageUser user : this.getUsers()) {
            if (user.getUid() ==  uid) {
                return user;
            }
        }
        return null;
    }

    public void setUsers(Set<MessageUser> users) {
        this.users = users;
    }

    public AtomicInteger getRecvSn() {
        return recvSn;
    }

    public void setRecvSn(AtomicInteger recvSn) {
        this.recvSn = recvSn;
    }

    @Override
    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof GroupMessageUser))
            return false;
        GroupMessageUser castOther = (GroupMessageUser) other;

        return new EqualsBuilder().append(groupId, castOther.getGroupId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(groupId).hashCode();
    }

    public int sendMessageNotify(NioServer server, Msg.Notify.Builder builder) {
        int offset = SerialNumberUtil.getNextSerialNumber(recvSn);
        Msg.Notify notify = builder.setSerialNumber(offset).build();
        if (isRecvMax) {//已经进入循环队列逻辑
            messages.set(offset, notify);//加进离线消息
        } else {
            messages.add(notify);
        }
        if (offset == SerialNumberUtil.MAX_SERIAL_NUMBER) {
            isRecvMax = true;
        }

        sendNotify(server, notify, ProtocolType.Msg_Notify);
        return offset;
    }

    public boolean cancelMessageNotify(NioServer server, Integer socketUid, Msg.CancelRequest packet) {
        Msg.Notify message = null;
        if (packet.getMsgId() < messages.size()) {
            message = messages.get(packet.getMsgId());
        }
        if (null != message) {
            if (socketUid != message.getSenderUid()) {
                logger.error("cancelMsgUidError->msgUid:{},socketUid:{} msgId:{}", message.getSenderUid(), socketUid, packet.getMsgId());
                return false;
            }
        } else {
            logger.error("cancelMsgIdError->msgId:{},msgSize:{}", packet.getMsgId(), messages.size());
            return false;
        }

        Msg.Notify.Builder cancelMsg = Msg.Notify.newBuilder()
                .setGroupNum(message.getGroupNum())
                .setMsg("消息被撤回")
                .setSerialNumber(packet.getMsgId())
                .setSenderUid(message.getSenderUid())
                .setTime((int) (System.currentTimeMillis() / 1000))
                .setReceiverNum(message.getReceiverNum())
                .setStatus(1);
        messages.set(packet.getMsgId(), cancelMsg.build());

        Msg.CancelNotify cancelNotify = Msg.CancelNotify.newBuilder()
                .setGroupNum(packet.getGroupNum()).setMsgId(packet.getMsgId()).build();

        sendNotify(server, cancelNotify, ProtocolType.Msg_CancelNotify);
        return true;
    }

    private void sendNotify(NioServer server, MessageLite notify, short type) {
        synchronized (users) {
            for (MessageUser user : users) {
                Set<UserDevice> devices = user.getUserDevices();
                synchronized (devices) {
                    for (UserDevice device : devices) {
                        server.send(device.getSocket(), notify, type);
                    }
                }
            }
        }
    }

    public List<Msg.Notify> getMessages() {
        return messages;
    }

    public void setMessages(List<Msg.Notify> messages) {
        this.messages = messages;
    }
}
