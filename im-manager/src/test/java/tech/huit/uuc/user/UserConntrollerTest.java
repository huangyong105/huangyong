package tech.huit.uuc.user;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tech.huit.test.LoadConfigure;
import tech.huit.conf.SystemConf;
import tech.huit.util.encrypt.InvalidTokenException;
import tech.huit.util.encrypt.RSAUtil;
import tech.huit.util.encrypt.SHAUtil;
import tech.huit.util.encrypt.UserLoginInfo;
import tech.huit.uuc.entity.auth.App;
import tech.huit.uuc.entity.user.AppUser;
import tech.huit.uuc.service.auth.AppService;
import tech.huit.uuc.service.auth.UserService;
import tech.huit.uuc.service.user.AppUserService;
import tech.huit.uuc.system.ErrorCode;
import tech.huit.uuc.web.api.controller.AuthController;
import tech.huit.uuc.web.api.controller.UserController;
import tech.huit.web.ResponseStatus;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * Created by huit on 2017/7/11.
 */
public class UserConntrollerTest extends LoadConfigure {

    @Autowired
    UserController userController;
    @Autowired
    AuthController authController;
    @Autowired
    AppUserService appUserService;
    @Autowired
    UserService userService;

    Cache loginCaptcha, loginErrorCount;
    @Autowired
    AppService appService;

    App app;
    AppUser user;

    String username = "huit";
    String password = "huit";
    String aesKey = SystemConf.get("app.aes.key");
    String uucAesKey = SystemConf.get("auth.aes.key");

    @Before
    public void init() {
        app = new App();
        app.setName("内置测试应用");
        Map<String, String> keyPare = RSAUtil.generateKeyBase64();
        app.setRsaPubKey(keyPare.get(RSAUtil.PUBLIC_KEY));
        app.setRsaPrivateKey(keyPare.get(RSAUtil.PRIVATE_KEY));
        app.setAesKey(aesKey);
        appService.insert(app);

/*        user = new AppUser();
        user.setUsername(username);
        user.setPassword(SHAUtil.SHA1(password));
        user.setPhone("15328008307");
        user.setNickname("唐辉");
        user.setCreateTime(new Date(System.currentTimeMillis()));
        appUserService.insert(user);*/
    }

    @After
    public void after() {
        appService.deleteById(app.getId());
       // appUserService.deleteById(user.getId());
    }

    @Autowired
    public void setCacheManager(CacheManager ehcacheManager) {
        loginCaptcha = ehcacheManager.getCache("authLoginCaptcha");
        loginErrorCount = ehcacheManager.getCache("authLoginErrorCount");
    }

    @Test
    public void loginTest() throws IOException, InterruptedException {


        //使用账号密码登录
        ResponseStatus rs = (ResponseStatus) userController.loginByAccount(null, username, "errorPassword", null);//应该登录失败
        Assert.assertFalse(rs.status);
        Assert.assertEquals(ErrorCode.AUTH_USER_PWD_ERROR.getCode(), rs.errorCode);

        rs = (ResponseStatus) userController.loginByAccount(null, username, password, null);//应该登录成功
        Assert.assertTrue(rs.status);
        Map<String, String> data = (Map<String, String>) rs.getData();
        String appToken = data.get(UserController.APP_TOKEN);
        UserLoginInfo userLoginInfo = null;
        try {
            userLoginInfo = UserLoginInfo.parseAppToken(appToken, app.getAesKey());
        } catch (InvalidTokenException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(userLoginInfo);

        //使用Token登录
        rs = (ResponseStatus) userController.loginByToken(appToken + "error");//应该登录失败
        Assert.assertFalse(rs.status);
        Assert.assertEquals(ErrorCode.AUTH_TOKEN_ERROR.getCode(), rs.errorCode);
        rs = (ResponseStatus) userController.loginByToken(appToken);//应该登录成功
        Assert.assertTrue(rs.status);

        int tokenExpireTime = (int) (System.currentTimeMillis() / 1000 - SystemConf.get("auth.token.expire.time", Integer.class));
        userLoginInfo.setExpirationTime(tokenExpireTime);
        String token = userLoginInfo.genAppToken(aesKey);
        rs = (ResponseStatus) userController.loginByToken(token);//应该登录成功
        Assert.assertFalse(rs.status);
        Assert.assertEquals(ErrorCode.AUTH_TOKEN_EXPIRE.getCode(), rs.errorCode);


        //使用手机登录
        String phone = "15328008307", captcha = "testcode";
        loginCaptcha.put(new Element(phone, captcha));
        rs = (ResponseStatus) userController.loginByPhone(phone, null);
        Assert.assertFalse(rs.status);
        Assert.assertEquals(ErrorCode.AUTH_CAPTCHA_ERROR.getCode(), rs.errorCode);

        rs = (ResponseStatus) userController.loginByPhone(phone, "error");
        Assert.assertEquals(ErrorCode.AUTH_CAPTCHA_ERROR.getCode(), rs.errorCode);

        rs = (ResponseStatus) userController.loginByPhone(phone, captcha);
        Assert.assertTrue(rs.status);


        //使用appToken进行auth登录
        rs = (ResponseStatus) authController.login(app.getId(), appToken);
        Assert.assertTrue(rs.status);
        data = (Map<String, String>) rs.getData();
        String uucToken = data.get(AuthController.UUC_TOKEN);
        UserLoginInfo authLogin = null;
        try {
            authLogin = UserLoginInfo.parseUucToken(uucToken, uucAesKey);
        } catch (InvalidTokenException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(authLogin);
        Assert.assertTrue(authLogin.getUid() > 0);
        userService.deleteById(authLogin.getUid());//删除登录成功创建的账号
    }
}
