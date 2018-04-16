package tech.huit.uuc.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.MessageLite;
import com.googlecode.protobuf.format.JsonFormat;
import edu.dbke.socket.cp.ProtocolType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.huit.http.HttpClientUtil;
import tech.huit.http.HttpException;
import tech.huit.socket.cp.Base;
import tech.huit.socket.cp.message.Msg;
import tech.huit.socket.nio.server.DataEvent;
import tech.huit.socket.nio.service.BaseServiceSupport;
import tech.huit.thread.ExecutorUtils;
import tech.huit.conf.SystemConf;
import tech.huit.util.encrypt.InvalidTokenException;
import tech.huit.util.encrypt.UserLoginInfo;
import tech.huit.uuc.entity.auth.App;
import tech.huit.uuc.entity.auth.User;
import tech.huit.uuc.service.auth.AppService;
import tech.huit.uuc.service.auth.UserService;
import tech.huit.uuc.service.dirty.DirtyService;
import tech.huit.uuc.system.ErrorCode;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import static tech.huit.uuc.message.SerialNumberUtil.MAX_MSG_SIZE;
import static tech.huit.uuc.message.SerialNumberUtil.MAX_SERIAL_NUMBER;
/**
 * 即时消息服务，使用步骤：
 * 1、ProtocolType.Msg_LoginRequest（加入聊天服务，必须先登录获取用户加密信息）
 * 2、其本相关相关协议的使用msg.proto协议定义文件
 *
 * @author huitang
 */
@Service
public class MsgService extends BaseServiceSupport {
    private Map<SocketChannel, Integer> socket2Uid = new ConcurrentHashMap();// 聊天用户列表
    public Map<Integer, MessageUser> p2pUserMap = new ConcurrentHashMap();// 聊天用户列表
    public Map<String, GroupMessageUser> groupMap = new ConcurrentHashMap();// 聊天群列表
    private String key = SystemConf.get("auth.aes.key");
    private String serSavePath = SystemConf.getPath("msg.ser.save.path");

    @Autowired
    private AppService appService;
    @Autowired
    UserService userService;

    @Autowired
    DirtyService dirtyService ;

    public Map getMsg(Integer id, boolean isGroup, boolean isSend, int offset, int size) {
        List<Msg.Notify> list = new ArrayList<>();
        List<Msg.Notify> msg = null;
        int thisOffset = 0;
        if (isGroup) {
            GroupMessageUser group = groupMap.get(id);
            if (null != group) {
                msg = group.getMessages();
                thisOffset = group.getRecvSn().get();
            }
        } else {
            MessageUser messageUser = p2pUserMap.get(id);
            if (null != messageUser) {
                if (isSend) {
                    msg = messageUser.getMessagesSend();
                    thisOffset = messageUser.getSendSn().get();
                } else {
                    msg = messageUser.getMessagesRecv();
                    thisOffset = messageUser.getRecvSn().get();
                }
            }
        }
        if (null != msg) {
            for (int i = offset; i < offset + size && i < msg.size(); i++) {
                list.add(msg.get(i));
            }
        }

        List<JSONObject> data = new ArrayList<>(list.size());
        JsonFormat jsonFormat = new JsonFormat();
        for (Msg.Notify notify : list) {
            String json = jsonFormat.printToString(notify);
            data.add(JSON.parseObject(json));
        }
        Map map = new HashMap();
        int total = null == msg ? 0 : msg.size();
        map.put("newestOffset", thisOffset);//当前最新偏移位置
        map.put("total", total);
        map.put("msg", data);
        return map;
    }

    @Override
    public void doTask(SocketChannel socket, short type) {
        switch (type) {
            case ProtocolType.Msg_TimeRequest:// 服务器时间当前时间查询
                Msg.TimeResponse.Builder build = Msg.TimeResponse.newBuilder().setTime((int) (System.currentTimeMillis() / 1000));
                server.send(socket, build, ProtocolType.Msg_TimeRequest);
                break;
            default:
                break;
        }
    }


    public void doTask(SocketChannel socket, short type, MessageLite packet) {
        if (logger.isDebugEnabled()) {
            Integer uid = -1;//web系统调用没有uid
            if (null != socket) {
                uid = socket2Uid.get(socket);
            }
            logger.debug("uid:{} type:{} packet:{}", uid, type, packet);
        }
        switch (type) {
            case ProtocolType.Msg_LoginRequest:// 加入聊天服务
                login(socket, (Msg.LoginRequest) packet);
                break;
            case ProtocolType.Msg_SendRequest:// 聊天发送消息
                sendMsg(socket, (Msg.SendRequest) packet);
                break;
            case ProtocolType.Msg_OfflineRequest:// 拉取离线消息
                pullOfflineMsg(socket, (Msg.OfflineRequest) packet);
                break;
            case ProtocolType.Msg_GroupJoinRequest:// 加入聊天室
                groupJoin(socket, (Msg.GroupJoinRequest) packet);
                break;
            case ProtocolType.Msg_GroupExitRequest:// 退出聊天室
                groupExit(socket, (Msg.GroupExitRequest) packet);
                break;
            case ProtocolType.Msg_FontSetRequest:// 设置消息字体
                fontSet(socket, (Msg.FontSetRequest) packet);
                break;
            case ProtocolType.Msg_FontSubscribeRequest:// 消息字体状态订购
                fontSubscribe(socket, (Msg.FontSubscribeRequest) packet);
                break;
            case ProtocolType.Msg_LastMsgRequest:// 拉取最近N条收发消息
                pullLastMsg(socket, (Msg.LastMsgRequest) packet);
                break;
            case ProtocolType.Msg_GroupInviteKickRequest:// 邀请加群和踢群
                inviteKick(socket, (Msg.GroupInviteKickRequest) packet);
                break;
            case ProtocolType.Msg_CancelRequest:// 撤销消息
                cancelMsg(socket, (Msg.CancelRequest) packet);
                break;
            default:
                break;
        }
    }

    /**
     * 撤销消息
     *
     * @param socket
     * @param packet
     */
    private void cancelMsg(SocketChannel socket, Msg.CancelRequest packet) {
        MessageUser messageUser = checkUserLoginAndSendNotify(socket, ProtocolType.Msg_SendRequest);
        if (null == messageUser) {
            return;
        }
        String groupNum = packet.getGroupNum();
        int socketUid = socket2Uid.get(socket);
        boolean isCancelSuccess = false;
        if (!StringUtils.isEmpty(groupNum) && !"0".equals(groupNum)) {
            GroupMessageUser groupMessageUser = groupMap.get(groupNum);
            if (null != groupMessageUser) {
                isCancelSuccess = groupMessageUser.cancelMessageNotify(server, socketUid, packet);
            }
        } else {
            MessageUser receiveUser = p2pUserMap.get(packet.getReceiverNum());
            if (null != receiveUser) {
                isCancelSuccess = receiveUser.cancelMessageNotify(server, socketUid, packet);
            }
        }
        Base.ResponseStatus.Builder rs = Base.ResponseStatus.newBuilder();
        rs.setStatus(true);
        if (isCancelSuccess) {
            rs.setStatus(true);
            rs.setData(packet.getMsgId() + "->撤销成功");
        } else {
            rs.setData(packet.getMsgId() + "->撤销失败");
        }
        logger.debug("msgCancel->uid:{} msgId:{}", socketUid, packet.getMsgId());
        server.send(socket, rs.build(), ProtocolType.Msg_CancelRequest);//发送回执
    }

    private void inviteKick(SocketChannel socket, Msg.GroupInviteKickRequest packet) {
        Msg.GroupInviteKickRequest inviteKickRequest = packet;
        Base.ResponseStatus.Builder rs = Base.ResponseStatus.newBuilder();
        Msg.GroupInviteKickNotify.Builder notify = Msg.GroupInviteKickNotify.newBuilder()
                .setMsg(packet.getMsg()).setIsInvite(packet.getIsInvite());
        for (String groupNum : inviteKickRequest.getGroupNumList()) {
            notify.addGroupNum(groupNum);
        }

        String groupIds = Arrays.toString(inviteKickRequest.getGroupNumList().toArray());
        String uids = Arrays.toString(inviteKickRequest.getUidsList().toArray());
        logger.debug("msgInviteKick->groupIds:{},uids:{},isInvite:{}", groupIds, uids, packet.getIsInvite());

        StringBuilder sb = new StringBuilder("notifyUids:");
        if (inviteKickRequest.getIsInvite()) {
            for (Integer uid : inviteKickRequest.getUidsList()) {
                MessageUser user = p2pUserMap.get(uid);
                if (null == user || user.getUserDevices().isEmpty()) {
                    continue;
                }
                sb.append(uid).append(",");
                notify.setUid(uid);
                sendInviteKickNotify(notify, user);
            }
        } else {
            for (String groupId : inviteKickRequest.getGroupNumList()) {
                GroupMessageUser groupMessageUser = groupMap.get(groupId);
                if (null == groupMessageUser) {
                    continue;
                }
                for (Integer uid : inviteKickRequest.getUidsList()) {
                    MessageUser user = p2pUserMap.get(uid);
                    if (null != user) {
                        sb.append(uid).append(",");
                        user.getJoinGroup().remove(groupId);
                        groupMessageUser.getUsers().remove(user);
                        notify.setUid(uid);
                        sendInviteKickNotify(notify, user);
                    }
                }
            }
        }
        rs.setStatus(true);
        rs.setData(sb.toString());
        logger.debug("msgInviteKickNotify->uids:{} isInvite:{}", sb.toString(), packet.getIsInvite());
        server.send(socket, rs.build(), ProtocolType.Msg_GroupInviteKickRequest);//发送回执
    }

    private void sendInviteKickNotify(Msg.GroupInviteKickNotify.Builder notify, MessageUser user) {
        for (UserDevice userDevice : user.getUserDevices()) {
            server.send(userDevice.getSocket(), notify, ProtocolType.Msg_GroupInviteKickNotify);
        }
    }

    private void pullLastMsg(SocketChannel socket, Msg.LastMsgRequest offline) {
        Integer loginUid = socket2Uid.get(socket);
        MessageUser messageUser;
        Msg.OfflineResponse.Builder response = Msg.OfflineResponse.newBuilder();
        if (null == loginUid || null == (messageUser = p2pUserMap.get(loginUid))) {
            Base.ResponseStatus.Builder rs = Base.ResponseStatus.newBuilder().setErrorCode(ErrorCode.MSG_USER_NOT_LOGIN.getCode()).setErrorMsg(ErrorCode.MSG_USER_NOT_LOGIN.getMsg());
            response.setResponseStatus(rs);
            server.send(socket, response, ProtocolType.Msg_LastMsgRequest);//发送错误回执
            return;
        }

        int lastOffset;
        int offset;
        List<Msg.Notify> msgs;

        msgs = messageUser.getMessagesSend();
        lastOffset = messageUser.getSendSn().get();//TODO lastOffset > size < 16384
        Collection<String> userGroup = offline.getGroupIdsList();
        offset = calOffestBySize(msgs, offline.getLastMsgSendSize(), lastOffset);
        pullOfflineMsg(response, msgs, offset, lastOffset, false, userGroup);//发送的消息
        logger.debug("lastPullSend uid:{} offset:{} lastOffset:{} sendSize:{} getSize:{}", loginUid, offset, lastOffset, msgs.size(), response.getMessagsSendList().size());

        if (!offline.getIsNotPullP2P()) {
            msgs = messageUser.getMessagesRecv();
            lastOffset = messageUser.getRecvSn().get();
            offset = calOffestBySize(msgs, offline.getLastMsgSendSize(), lastOffset);
            pullOfflineMsg(response, msgs, offset, lastOffset, true);//接收的p2p消息
            logger.debug("lastPullRecvP2P uid:{} offset:{} lastOffset:{} recvSize:{} getSize:{}", loginUid, offset, lastOffset, msgs.size(), response.getMessagsRecvList().size());
        }

        if (null == userGroup || userGroup.isEmpty()) {
            userGroup = messageUser.getJoinGroup();
            logger.debug("lastPullUseJoinGroup uid:{} joinGroupSize:{}", loginUid, userGroup.size());
        }

        if (null != userGroup && !userGroup.isEmpty()) {
            for (String groupNum : userGroup) {//接收的群
                GroupMessageUser group = groupMap.get(groupNum);
                if (null != group) {
                    msgs = group.getMessages();
                    lastOffset = group.getRecvSn().get();
                    offset = calOffestBySize(msgs, offline.getLastMsgSendSize(), lastOffset);
                    logger.debug("lastPullRecvGroup uid:{} offset:{} lastOffset:{} recvSize:{} getSize:{} groupNum:{}",
                            loginUid, offset, lastOffset, msgs.size(), response.getMessagsRecvList().size(), groupNum);
                    pullOfflineMsg(response, msgs, offset, lastOffset, true);
                }
            }
        }

        response.setResponseStatus(Base.ResponseStatus.newBuilder().setStatus(true));
        server.send(socket, response, ProtocolType.Msg_LastMsgRequest);
    }

    private void fontSubscribe(SocketChannel socket, Msg.FontSubscribeRequest packet) {
        Integer loginUid = socket2Uid.get(socket);
        MessageUser messageUser;
        if (null == loginUid || null == (messageUser = p2pUserMap.get(loginUid))) {
            Base.ResponseStatus.Builder rs = Base.ResponseStatus.newBuilder().setErrorCode(ErrorCode.MSG_USER_NOT_LOGIN.getCode()).setErrorMsg(ErrorCode.MSG_USER_NOT_LOGIN.getMsg());
            Msg.FontSubscribeResponse.Builder fsr = Msg.FontSubscribeResponse.newBuilder().setResponseStatus(rs);
            server.send(socket, fsr, ProtocolType.Msg_FontSubscribeRequest);//发送错误回执
            return;
        }
        Msg.FontSubscribeResponse.Builder fsr = Msg.FontSubscribeResponse.newBuilder().setResponseStatus(Base.ResponseStatus.newBuilder().setStatus(true));
        for (int uid : packet.getUidsList()) {
            MessageUser subUser = getMessageUserIfNullCreate(uid);
            subUser.getFontSubscribe().add(messageUser.getUid());
            Msg.FontNotify fontNotify = subUser.getFontNotify();
            if (null != fontNotify) {
                fsr.addMessageFonts(fontNotify);
            }
        }
        server.send(socket, fsr, ProtocolType.Msg_FontSubscribeRequest);//发送回执
    }

    private void fontSet(SocketChannel socket, Msg.FontSetRequest fontSetRequest) {
        MessageUser messageUser = checkUserLoginAndSendNotify(socket, ProtocolType.Msg_FontSetRequest);
        if (null == messageUser) {
            return;
        }
        Msg.FontNotify fontNotify = Msg.FontNotify.newBuilder().setFont(fontSetRequest.getFont()).setFontBold(fontSetRequest.getFontBold())
                .setFontColor(fontSetRequest.getFontColor()).setFontSize(fontSetRequest.getFontSize()).setFontItalic(fontSetRequest.getFontItalic())
                .setFontUnderline(fontSetRequest.getFontUnderline()).setUid(messageUser.getUid()).build();
        messageUser.setFontNotify(fontNotify);
        server.send(socket, Base.ResponseStatus.newBuilder().setStatus(true), ProtocolType.Msg_FontSetRequest);//发送回执

        for (int uid : messageUser.getFontSubscribe()) {//给订购的用户发送更新通知
            getMessageUserIfNullCreate(uid).sendFontNotify(server, fontNotify);
        }
    }

    //退群
    private void groupExit(SocketChannel socket, Msg.GroupExitRequest joinRequest) {
        MessageUser messageUser = checkUserLoginAndSendNotify(socket, ProtocolType.Msg_GroupJoinRequest);
        if (null == messageUser) {
            return;
        }
        Base.ResponseStatus.Builder rs = Base.ResponseStatus.newBuilder();
        rs.setStatus(true);
        StringBuilder sb = new StringBuilder();
        List<String> groupNums = joinRequest.getGroupNumList();
        for (String groupNum : groupNums) {
            GroupMessageUser groupMessageUser = groupMap.get(groupNum);
            if (null != groupMessageUser) {
                if (groupMessageUser.getUsers().remove(messageUser)) {
                    sb.append(groupNum).append(",");
                }
                //退出群
                this.exitEnterNotify(messageUser.getUserDevice(socket),groupMessageUser,messageUser.getUid(),Constants.OFFLINE);
            }
        }
        rs.setData(sb.toString());
        server.send(socket, rs, ProtocolType.Msg_GroupExitRequest);//发送回执
    }

    private void groupJoin(SocketChannel socket, Msg.GroupJoinRequest joinRequest) {
        MessageUser messageUser = checkUserLoginAndSendNotify(socket, ProtocolType.Msg_GroupJoinRequest);
        if (null == messageUser) {
            return;
        }
        List<String> groupNums = joinRequest.getGroupNumList();
        Base.ResponseStatus.Builder rs = Base.ResponseStatus.newBuilder();
        if (groupNums.size() == 0) {
            rs.setErrorCode(ErrorCode.MSG_PARAM_ERROR.getCode()).setErrorMsg(ErrorCode.MSG_PARAM_ERROR.getMsg());
        } else {
            rs.setStatus(true);
            rs.setData(groupNums + "");
            messageUser.getJoinGroup().addAll(groupNums);
        }
        StringBuilder sb = new StringBuilder();
        for (String groupNum : groupNums) {
            GroupMessageUser groupMessageUser = getGroupIfNullCreate(groupNum);
            Set<MessageUser> users = groupMessageUser.getUsers();
            users.remove(messageUser);
            users.add(messageUser);//TODO 处理重启
            this.exitEnterNotify(messageUser.getUserDevice(socket),groupMessageUser,messageUser.getUid(),Constants.ONLINE);
            sb.append(groupNum).append(",");
        }
        rs.setData(sb.toString());
        server.send(socket, rs, ProtocolType.Msg_GroupJoinRequest);//发送回执
    }

    /**
     * 获取登录的账号，如果未登录发送需要登录的错误提示
     *
     * @param socket
     * @return
     */
    private MessageUser checkUserLoginAndSendNotify(SocketChannel socket, short type) {
        Integer uid = socket2Uid.get(socket);
        MessageUser messageUser = null;
        if (null == uid || null == (messageUser = p2pUserMap.get(uid))) {
            Base.ResponseStatus.Builder rs = Base.ResponseStatus.newBuilder();
            rs.setErrorCode(ErrorCode.MSG_USER_NOT_LOGIN.getCode()).setErrorMsg(ErrorCode.MSG_USER_NOT_LOGIN.getMsg());
            server.send(socket, rs, type);
        }
        return messageUser;
    }

    private synchronized GroupMessageUser getGroupIfNullCreate(String groupNum) {
        GroupMessageUser groupMessageUser = groupMap.get(groupNum);
        if (null == groupMessageUser) {
            groupMessageUser = new GroupMessageUser(groupNum);
            groupMap.put(groupNum, groupMessageUser);
        }
        return groupMessageUser;
    }

    /**
     * 拉取用户离线消息
     *
     * @param socket
     * @param offline
     */
    private void pullOfflineMsg(SocketChannel socket, Msg.OfflineRequest offline) {
        Integer loginUid = socket2Uid.get(socket);
        MessageUser messageUser;
        Msg.OfflineResponse.Builder response = Msg.OfflineResponse.newBuilder();
        if (null == loginUid || null == (messageUser = p2pUserMap.get(loginUid))) {
            Base.ResponseStatus.Builder rs = Base.ResponseStatus.newBuilder().setErrorCode(ErrorCode.MSG_USER_NOT_LOGIN.getCode()).setErrorMsg(ErrorCode.MSG_USER_NOT_LOGIN.getMsg());
            response.setResponseStatus(rs);
            server.send(socket, response, ProtocolType.Msg_OfflineRequest);//发送错误回执
            return;
        }


        List<Msg.Notify> msgs = messageUser.getMessagesRecv();
        int offset = offline.getP2POffset();
        int lastOffset = messageUser.getRecvSn().get();
        pullOfflineMsg(response, msgs, offset, lastOffset, true);
        int addSize = response.getMessagsRecvList().size();
        logger.debug("pullOfflineMsgP2P uid:{} offset:{} lastOffset:{} addSize:{}", loginUid, offset, lastOffset, addSize);

        List<Msg.GroupOffset> groupOffsets = offline.getGroupOffsetsList();
        if (null != groupOffsets) {
            for (Msg.GroupOffset groupOffset : groupOffsets) {
                GroupMessageUser group = groupMap.get(groupOffset.getGroupNum());
                if (null != group) {
                    msgs = group.getMessages();
                    offset = groupOffset.getGroupOffset();
                    lastOffset = group.getRecvSn().get();
                    pullOfflineMsg(response, msgs, offset, lastOffset, true);
                    addSize = response.getMessagsRecvList().size();
                    logger.debug("pullOfflineMsgGroup uid:{} groupId:{} offset:{} lastOffset:{} addSize:{}",
                            loginUid, groupOffset.getGroupNum(), offset, lastOffset, addSize);
                }
            }
        }
        response.setResponseStatus(Base.ResponseStatus.newBuilder().setStatus(true));
        server.send(socket, response, ProtocolType.Msg_OfflineRequest);
    }

    /**
     * 根据条数据计算循环数组中的offset
     * 0 1 2 3 4 -> 第0个位置要取3个数= 1 + max- 1
     * @param msgs
     * @param size
     * @param lastOffset
     * @return
     */
    private int calOffestBySize(List<Msg.Notify> msgs, int size, int lastOffset) {
        int result = lastOffset - size + 1;
        if (result < 0) {
            if (msgs.size() == MAX_MSG_SIZE) {//已经达到最大值，说明已经进入循环队列
                result += MAX_SERIAL_NUMBER + 1;
            } else {
                result = 0;//从第一条取
            }
        }
        return result;
    }

    private void pullOfflineMsg(Msg.OfflineResponse.Builder builder, List<Msg.Notify> msgs, int offset, int lastOffset, boolean isRecv) {
        pullOfflineMsg(builder, msgs, offset, lastOffset, isRecv, null);//发送的消息
    }

    private void pullOfflineMsg(Msg.OfflineResponse.Builder builder, List<Msg.Notify> msgs, int offset, int lastOffset, boolean isRecv, Collection<String> filterGroup) {
        for (int i = offset; i < msgs.size(); i++) {
            Msg.Notify notify = msgs.get(i);
            if (isRecv) {
                builder.addMessagsRecv(notify);
            } else {
                if (null != filterGroup && !filterGroup.isEmpty() && !filterGroup.contains(notify.getGroupNum())) {
                    continue;//指定了群列表时从发送队列里过滤
                } else {
                    builder.addMessagsSend(notify);
                }
            }
            if (i == lastOffset) {
                break;//数据已经拉取完了
            }
            if (i == MAX_SERIAL_NUMBER) {//到了循环队列尾
                i = -1;
            }
        }
    }

    /**
     * 发送消息
     *
     * @param socket
     */

    private void sendMsg(SocketChannel socket, Msg.SendRequest message) {
        MessageUser messageUser = checkUserLoginAndSendNotify(socket, ProtocolType.Msg_SendRequest);
        if (null == messageUser) {
            return;
        }
        int msgTime = (int) (System.currentTimeMillis() / 1000);
        Base.ResponseStatus.Builder rs = sendMsgParamCheck(message, msgTime);
        if (!rs.getStatus()) {
            return;
        }

        String groupNum = message.getGroupNum();
        Msg.Notify.Builder msgNotify = Msg.Notify.newBuilder()
                .setGroupNum(groupNum)
                .setMsg(dirtyService.filterText(message.getMsg()))
                .setSenderUid(messageUser.getUid())
                .setTime(msgTime)
                .setFileOriginalName(message.getFileOriginalName())
                .setFileDownloadUrl(message.getFileDownloadUrl())
                .setFileSize(message.getFileSize())
                .setReceiverNum(message.getReceiverNum());
        messageUser.addSendMsg(msgNotify);
        int sendMsgId;
        if (!StringUtils.isEmpty(groupNum) && !"0".equals(groupNum)) {
            sendMsgId = getGroupIfNullCreate(groupNum).sendMessageNotify(server, msgNotify);
        } else {
            sendMsgId = getMessageUserIfNullCreate(message.getReceiverNum()).sendNotify(server, msgNotify);
        }
        if (message.getMsgId() != -1) {
            rs.setData(message.getMsgId() + "&" + msgTime + "&" + sendMsgId);
            server.send(socket, rs, ProtocolType.Msg_SendRequest);//发送回执
        }
    }

    /** 发送群聊消息 **/
    public  void sendGroupMsg( String content , String groupNum ,int msgTime) {
        Msg.Notify.Builder msgNotify = Msg.Notify.newBuilder()
            .setGroupNum(groupNum)
            .setMsg(content)
            .setSenderUid(0)
            .setTime(msgTime)
            .setType(2)
            ;
         getGroupIfNullCreate(groupNum).sendMessageNotify(server, msgNotify);
    }

    /** 获取房间用户列表 **/
    public  List<User>  loadGroupUser (String  groupNum) {
        GroupMessageUser groupMessageUser =  this.groupMap.get(groupNum);
        if (groupMessageUser == null) {
            return new ArrayList<>() ;
        }
        List<User> userList = new ArrayList<>();
        for (MessageUser user : groupMessageUser.getUsers()) {
           User userInfo =  this.userService.selectById(user.getUid()) ;
           userList.add(userInfo) ;
        }
        return  userList ;
    }

    private MessageUser getMessageUserIfNullCreate(int receiverNum) {
        MessageUser receiveUser = p2pUserMap.get(receiverNum);
        if (null == receiveUser) {
            receiveUser = new MessageUser(receiverNum);
            p2pUserMap.put(receiverNum, receiveUser);
        }
        return receiveUser;
    }


    /**
     * 用户登录
     *
     * @param socket
     * @param packet
     */
    private void login(SocketChannel socket, Msg.LoginRequest packet) {
        Msg.LoginRequest mlq = packet;
        String token = mlq.getToken();
        Base.ResponseStatus.Builder rs = Base.ResponseStatus.newBuilder();
        if (StringUtils.isEmpty(token) || null == mlq.getDeviceType()) {
            rs.setErrorCode(ErrorCode.MSG_LOGIN_PARAM_ERROR.getCode()).setErrorMsg(ErrorCode.MSG_LOGIN_PARAM_ERROR.getMsg());
            server.send(socket, rs, ProtocolType.Msg_LoginRequest);
            return;
        }

        try {
            UserLoginInfo userInfo = UserLoginInfo.parseUucToken(token, key);
            UserDevice device = new UserDevice(socket, mlq.getDeviceType());
            MessageUser messageUser = p2pUserMap.get(userInfo.getUid());
            if (null != messageUser) {//TODO 处理重启
                notifyNewDeviceLogin(messageUser, device);
            } else {
                messageUser = new MessageUser(userInfo.getUid(), userInfo.getNickname(), mlq.getDeviceType(), socket);
                p2pUserMap.put(userInfo.getUid(), messageUser);
            }
            socket2Uid.remove(socket);//防止切换账号连接未关闭的bug
            socket2Uid.put(socket, userInfo.getUid());
            messageUser.getUserDevices().add(device);
            rs.setStatus(true);
        } catch (InvalidTokenException e) {
            rs.setErrorCode(ErrorCode.AUTH_TOKEN_ERROR.getCode()).setErrorMsg(ErrorCode.AUTH_TOKEN_ERROR.getMsg());
        }
        server.send(socket, rs, ProtocolType.Msg_LoginRequest);
    }

    /**
     * 同一类型设备只允许登录一台,通知老设备下线并从列表中删除，通知其它类型终端有新设备上线
     *
     * @param messageUser
     */
    private void notifyNewDeviceLogin(MessageUser messageUser, UserDevice newDevice) {
        Iterator<UserDevice> devices = messageUser.getUserDevices().iterator();
        while (devices.hasNext()) {
            UserDevice device = devices.next();
            if (device.equals(newDevice)) {//如果存在老同类型设备进行通知下线
                Msg.KickNotify.Builder builder = Msg.KickNotify.newBuilder().setMsg("有新的设备在[" + newDevice.getIp() + "]登录，你已经被迫下线");
                server.send(device.getSocket(), builder, ProtocolType.Msg_KickNotify);
                devices.remove();//删除老设备
            } else {
                Msg.StatusNotify StatusNotify = Msg.StatusNotify.newBuilder().setDeviceType(newDevice.getDeviceType()).setIsOnline(true).build();
                server.send(device.getSocket(), StatusNotify, ProtocolType.Msg_StatusNotify);//通知其他类型的终端有新的设备上线
            }
        }
    }


    /**
     * 检查发送消息参数
     *
     * @return
     */
    private Base.ResponseStatus.Builder sendMsgParamCheck(Msg.SendRequest message, int msgTime) {
        String msg = message.getMsg();
        int receiverNum = message.getReceiverNum();
        Base.ResponseStatus.Builder msr = Base.ResponseStatus.newBuilder();
        if (StringUtils.isEmpty(msg) && StringUtils.isEmpty(message.getFileDownloadUrl())) {
            msr.setErrorMsg("消息不能为空");
            return msr;
        }

        if (0 == receiverNum && StringUtils.isEmpty(message.getGroupNum()) &&"0".equals(message.getGroupNum())) {
            msr.setErrorMsg("接收者不能为空");
            return msr;
        }

        msr.setStatus(true);
        return msr;
    }

    @Override
    public boolean dispatchEvent(DataEvent dataEvent) throws Exception {
        ByteBuffer data = dataEvent.data;
        Integer uid = socket2Uid.remove(dataEvent.socket);
        if (null == uid) {//没有加入过im服务，后面的删除逻辑不用执行
            return false;
        }
        switch (data.getShort(4)) {
            case ProtocolType.SERVER_SOCKET_CLOSED:
                Iterator<Entry<Integer, MessageUser>> userMapIt = p2pUserMap.entrySet().iterator();
                MessageUser messageUser = null;
                while (userMapIt.hasNext()) {
                    Entry<Integer, MessageUser> entry = userMapIt.next();
                    messageUser = entry.getValue();
                    Iterator<UserDevice> devices = messageUser.getUserDevices().iterator();
                    boolean hasFound = false;
                    Msg.StatusNotify exitNotify = null;
                    while (devices.hasNext()) {
                        UserDevice userDevice = devices.next();
                        if (dataEvent.socket.equals(userDevice.getSocket())) {
                            devices.remove();
                            socket2Uid.remove(dataEvent.socket);
                            if (logger.isDebugEnabled()) {
                                logger.debug("userDevice disconnect->uid:{},nickname:{},device:{}", messageUser.getUid(), messageUser.getNickname(), userDevice);
                            }
                            //通知主播下线
                            timeOutExitNotify(messageUser.getUid(),userDevice);
                            exitNotify = Msg.StatusNotify.newBuilder().setDeviceType(userDevice.getDeviceType()).setIsOnline(false).build();
                            hasFound = true;
                            break;
                        }
                    }
                    if (hasFound) {//一个链接只能登录一个uid和设备
                        synchronized (messageUser.getUserDevices()) {
                            for (UserDevice device : messageUser.getUserDevices()) {
                                server.send(device.getSocket(), exitNotify, ProtocolType.Msg_StatusNotify);//通知同一用户的其他类型的终端有设备下线
                                if (logger.isDebugEnabled()) {
                                    logger.debug("userDevice disconnect notify->device:{}", device);
                                }
                            }
                        }
                        break;
                    }
                }
                break;
            default:
                break;
        }
        return false;
    }

    /** 通知其他平台玩家离线 **/
    public  void exitEnterNotify(final  UserDevice device,final  GroupMessageUser guser ,final int  uid,String type ) {
        final User user = userService.selectById(uid);
        Runnable timeOutExitTask = () -> {
            final int user_count = guser == null?0:guser.getUsers().size();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userid", user.getAppUid());
            jsonObject.put("event_type",type);
            jsonObject.put("channelid", guser.getGroupId());
            jsonObject.put("user_count", user_count);
            MessageUser messageUser = guser.getExistUser(uid);
            if ( messageUser != null) {
                Set<UserDevice> userDeviceSet = messageUser.getUserDevices();
                Iterator iterator = userDeviceSet.iterator();
                //设置平台类型
                jsonObject.put("platform_type",device== null?0:device.deviceType.getNumber());
            }
            App app = appService.selectById(user.getAppId());
            try {
                Map<String ,Object> param = new HashMap<String,Object>() ;
                param.put("json",jsonObject.toJSONString());
                System.out.println("---------------"+jsonObject.toJSONString());
                HttpClientUtil.doPost(app.getImCallbackUrl(),3,param,null,null, Charset.forName("UTF-8"));
            } catch (HttpException e) {
                e.printStackTrace();
                logger.info("ExitNotify {}",e.getMessage());
            }
        };
        ExecutorUtils.execute(timeOutExitTask);
    }


    /** 通知其他平台玩家离线 **/
    public  void timeOutExitNotify(final int  uid ,final  UserDevice device) {
        final  User user = userService.selectById(uid);
        if(null == user){
            logger.warn("notFoundUid->uid:{}", uid);
            return;
        }
        Runnable timeOutExitTask = () -> {
            for (Entry<String,GroupMessageUser> guserEntry :this.groupMap.entrySet()) {
                String channelid = guserEntry.getKey();
                GroupMessageUser guser = guserEntry.getValue();
                if (!guser.checkExistUser(uid)) {
                    continue;
                }
                final int user_count = guser == null?0:guser.getUsers().size();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userid", user.getAppUid());
                jsonObject.put("event_type", Constants.TIMEOUT);
                jsonObject.put("channelid", channelid);
                jsonObject.put("user_count", user_count);
                MessageUser messageUser = guser.getExistUser(uid);
                if ( messageUser != null) {
                    Set<UserDevice> userDeviceSet = messageUser.getUserDevices();
                    Iterator iterator = userDeviceSet.iterator();
                    //设置平台类型
                    jsonObject.put("platform_type",device.deviceType.getNumber());
                }
                App app = appService.selectById(user.getAppId());
                try {
                    Map<String ,Object> param = new HashMap<String,Object>() ;
                    param.put("json",jsonObject.toJSONString());
                    System.out.println("---------------"+jsonObject.toJSONString());
                    HttpClientUtil.doPost(app.getImCallbackUrl(),3,param,null,null, Charset.forName("UTF-8"));
                } catch (HttpException e) {
                    e.printStackTrace();
                    logger.info("TimeOutExitNotify {}",e.getMessage());
                }
            }
        };
        ExecutorUtils.execute(timeOutExitTask);
    }

    @Override
    public void registerPacket() {
        server.getPacketMap().put(ProtocolType.Msg_LoginRequest, Msg.LoginRequest.getDefaultInstance());
        disposeType.add(ProtocolType.Msg_LoginRequest);
        server.getPacketMap().put(ProtocolType.Msg_SendRequest, Msg.SendRequest.getDefaultInstance());
        disposeType.add(ProtocolType.Msg_SendRequest);
        server.getPacketMap().put(ProtocolType.Msg_GroupJoinRequest, Msg.GroupJoinRequest.getDefaultInstance());
        disposeType.add(ProtocolType.Msg_GroupJoinRequest);
        server.getPacketMap().put(ProtocolType.Msg_OfflineRequest, Msg.OfflineRequest.getDefaultInstance());
        disposeType.add(ProtocolType.Msg_OfflineRequest);
        server.getPacketMap().put(ProtocolType.Msg_FontSetRequest, Msg.FontSetRequest.getDefaultInstance());
        disposeType.add(ProtocolType.Msg_FontSetRequest);
        server.getPacketMap().put(ProtocolType.Msg_GroupJoinRequest, Msg.GroupJoinRequest.getDefaultInstance());
        disposeType.add(ProtocolType.Msg_GroupJoinRequest);
        server.getPacketMap().put(ProtocolType.Msg_FontSubscribeRequest, Msg.FontSubscribeRequest.getDefaultInstance());
        disposeType.add(ProtocolType.Msg_FontSubscribeRequest);
        server.getPacketMap().put(ProtocolType.Msg_GroupExitRequest, Msg.GroupExitRequest.getDefaultInstance());
        disposeType.add(ProtocolType.Msg_GroupExitRequest);
        server.getPacketMap().put(ProtocolType.Msg_LastMsgRequest, Msg.LastMsgRequest.getDefaultInstance());
        disposeType.add(ProtocolType.Msg_LastMsgRequest);
        server.getPacketMap().put(ProtocolType.Msg_GroupInviteKickRequest, Msg.GroupInviteKickRequest.getDefaultInstance());
        disposeType.add(ProtocolType.Msg_GroupInviteKickRequest);
        server.getPacketMap().put(ProtocolType.Msg_CancelRequest, Msg.CancelRequest.getDefaultInstance());
        disposeType.add(ProtocolType.Msg_CancelRequest);
        disposeType.add(ProtocolType.Msg_TimeRequest);
    }

    @Override
    public void init() {
        ObjectInputStream p2pUserMapSer = null, groupMapSer = null;
        File file = null;
        try {
            file = new File(serSavePath + "p2pUserMap.ser");
            if (!file.isFile()) {
                return;
            }
            logger.info("msgServiceLoadBegin");
            long beginTime = System.currentTimeMillis();
            p2pUserMapSer = new ObjectInputStream(new FileInputStream(serSavePath + "p2pUserMap.ser"));
            p2pUserMap = (Map<Integer, MessageUser>) p2pUserMapSer.readObject();

            groupMapSer = new ObjectInputStream(new FileInputStream(serSavePath + "groupMap.ser"));
            groupMap = (Map<String, GroupMessageUser>) groupMapSer.readObject();
            logger.info("msgServiceLoadSuccess->p2pUserMapSize:{},groupMapSize:{},useTime:{}"
                    , p2pUserMap.size(), groupMap.size(), (System.currentTimeMillis() - beginTime));
        } catch (Exception e) {
            logger.error("msgServiceLoadializableError", e);
            file.delete();//说明类改过了，直接删除老的数据
        } finally {
            if (null != p2pUserMapSer) {
                try {
                    p2pUserMapSer.close();
                } catch (IOException e) {
                }
            }
            if (null != groupMapSer) {
                try {
                    groupMapSer.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @Override
    public void destroy() {
        //save(); 和Spring一起启动时会关闭老的服务，会导致消息被清空
    }

    /**
     * 保存聊天消息
     */
    public Object save() {
        ObjectOutputStream p2pUserMapSer = null, groupMapSer = null;
        Map map = new HashMap();
        map.put("time", new Date());
        try {
            File file = new File(serSavePath);
            if (!file.isDirectory()) {
                file.mkdirs();
                logger.info("msgServiceMakeSerDir");
            }
            logger.info("msgServiceSaveBegin");
            long beginTime = System.currentTimeMillis();
            p2pUserMapSer = new ObjectOutputStream(new FileOutputStream(serSavePath + "p2pUserMap.ser"));
            p2pUserMapSer.writeObject(p2pUserMap);

            groupMapSer = new ObjectOutputStream(new FileOutputStream(serSavePath + "groupMap.ser"));
            groupMapSer.writeObject(groupMap);
            logger.info("msgServiceSaveSuccess->p2pUserMapSize:{},groupMapSize:{},useTime:{}"
                    , p2pUserMap.size(), groupMap.size(), (System.currentTimeMillis() - beginTime));

            map.put("useTime", System.currentTimeMillis() - beginTime);
            map.put("p2pUserMapSize", p2pUserMap.size());
            map.put("groupMapSize", groupMap.size());
            return map;
        } catch (Exception e) {
            logger.error("msgServiceSaveError", e);
        } finally {
            if (null != p2pUserMapSer) {
                try {
                    p2pUserMapSer.close();
                } catch (IOException e) {
                }
            }
            if (null != groupMapSer) {
                try {
                    groupMapSer.close();
                } catch (IOException e) {
                }
            }
        }
        return map;
    }
}