package tech.huit.uuc.auth;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tech.huit.test.LoadConfigure;
import tech.huit.conf.SystemConf;
import tech.huit.util.encrypt.AESUtil;
import tech.huit.util.encrypt.InvalidTokenException;
import tech.huit.util.encrypt.UserLoginInfo;
import tech.huit.uuc.entity.auth.App;
import tech.huit.uuc.service.auth.AppService;
import tech.huit.uuc.service.auth.UserService;
import tech.huit.uuc.system.ErrorCode;
import tech.huit.uuc.web.api.controller.AuthController;
import tech.huit.web.ResponseStatus;

import java.io.IOException;
import java.util.Map;

/**
 * Created by huit on 2017/7/11.
 */
public class AuthControllerTest extends LoadConfigure {

    @Autowired
    AuthController authController;

    @Autowired
    AppService appService;

    @Autowired
    UserService userService;


    @Test
    public void loginTest() throws IOException, InterruptedException {
        App app = new App();
        app.setName("登录测试App");
        app.setAesKey(AESUtil.genRandomKey());
        appService.insert(app);

        UserLoginInfo loginInfo = new UserLoginInfo();
        loginInfo.setNickname("登录测试账号");
        loginInfo.setAppUid("888");
        loginInfo.setExpirationTime((int) (System.currentTimeMillis() / 1000 + 60));


        //使用账号密码登录
        ResponseStatus rs = (ResponseStatus) authController.login(app.getId(), loginInfo.genAppToken(app.getAesKey()) + "error");//应该登录失败
        Assert.assertFalse(rs.status);
        Assert.assertEquals(ErrorCode.AUTH_TOKEN_ERROR.getCode(), rs.errorCode);

        //应该登录成功1
        rs = (ResponseStatus) authController.login(app.getId(), loginInfo.genAppToken(app.getAesKey()));
        Assert.assertTrue(rs.status);
        Map<String, String> data = (Map<String, String>) rs.getData();
        String getToken = data.get(AuthController.UUC_TOKEN);
        UserLoginInfo responseUserInfo = null;
        try {
            responseUserInfo = UserLoginInfo.parseUucToken(getToken, SystemConf.get("auth.aes.key"));
        } catch (InvalidTokenException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(responseUserInfo);
        int uid1 = responseUserInfo.getUid();

        //应该登录成功2
        rs = (ResponseStatus) authController.login(app.getId(), loginInfo.genAppToken(app.getAesKey()));
        Assert.assertTrue(rs.status);
        data = (Map<String, String>) rs.getData();
        getToken = data.get(AuthController.UUC_TOKEN);
        responseUserInfo = null;
        try {
            responseUserInfo = UserLoginInfo.parseUucToken(getToken, SystemConf.get("auth.aes.key"));
        } catch (InvalidTokenException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(responseUserInfo);
        Assert.assertEquals(uid1, responseUserInfo.getUid());//两次登录应该是同一个uid

        int tokenExpireTime = (int) (System.currentTimeMillis() / 1000 - 1);
        loginInfo.setExpirationTime(tokenExpireTime);

        rs = (ResponseStatus) authController.login(app.getId(), loginInfo.genAppToken(app.getAesKey()));//登录token过期
        Assert.assertFalse(rs.status);
        Assert.assertEquals(ErrorCode.AUTH_TOKEN_EXPIRE.getCode(), rs.errorCode);

        userService.deleteById(responseUserInfo.getUid());
        appService.deleteById(app.getId());
    }
}
