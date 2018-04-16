package tech.huit.uuc.message;

import org.junit.Assert;
import org.junit.Test;

import tech.huit.socket.cp.message.Msg;


public class MsgServiceSerTest {

    @Test
    public void serReadTest() {
        MsgService msgService = new MsgService();
        msgService.init();
    }

    @Test
    public void serTest() {
        MsgService msgService = new MsgService();
        MessageUser user = new MessageUser(1, "uid1", Msg.DeviceType.PC, null);
        user.getFontSubscribe().add(2);
        user.setFontNotify(Msg.FontNotify.newBuilder().setFont("he").build());
        msgService.p2pUserMap.put(1, user);
        GroupMessageUser group = new GroupMessageUser("1");
        msgService.groupMap.put("1", group);
        msgService.destroy();
        msgService.init();
        Assert.assertEquals(1, msgService.p2pUserMap.size());
        Assert.assertEquals(user.getNickname(), msgService.p2pUserMap.get(1).getNickname());
        Assert.assertEquals(user.getUid(), msgService.p2pUserMap.get(1).getUid());
        Assert.assertEquals(user.getFontNotify(), msgService.p2pUserMap.get(1).getFontNotify());
        Assert.assertEquals(user.getFontSubscribe(), msgService.p2pUserMap.get(1).getFontSubscribe());
        Assert.assertEquals(1, msgService.groupMap.size());
    }
}
