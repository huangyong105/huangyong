package tech.huit.uuc.init;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tech.huit.test.LoadConfigure;
import tech.huit.conf.SystemConf;
import tech.huit.util.encrypt.RSAUtil;
import tech.huit.util.encrypt.SHAUtil;
import tech.huit.uuc.entity.auth.App;
import tech.huit.uuc.entity.user.AppUser;
import tech.huit.uuc.service.auth.AppService;
import tech.huit.uuc.service.user.AppUserService;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by huit on 2017/7/11.
 */
public class DataInitTest extends LoadConfigure {
    @Autowired
    AppUserService appUserService;
    @Autowired
    AppService appService;

    App app, app2;
    AppUser user;

    String username = "huit";
    String password = "huit";
    String appName = "内置测试应用", appName2 = "张杰测试应用";
    String groupName = "默认分组";
    String aesKey = SystemConf.get("app.aes.key");
    String uucAesKey = SystemConf.get("auth.aes.key");


    @Test
    public void init() {
        app = appService.selectByName(appName);
        if (null == app) {
            app = new App();
            app.setName(appName);
            app.setAuthorizeSipCount(50);
            app.setAuthorizeCallCount(10);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, 1);
            app.setExpiryDate(calendar.getTime());
            Map<String, String> keyPare = RSAUtil.generateKeyBase64();
            app.setRsaPubKey(keyPare.get(RSAUtil.PUBLIC_KEY));
            app.setRsaPrivateKey(keyPare.get(RSAUtil.PRIVATE_KEY));
            app.setAesKey(aesKey);
            appService.insert(app);
        }

        app2 = appService.selectByName(appName2);
        if (null == app2) {
            app2 = new App();
            app2.setName(appName2);
            app2.setAuthorizeSipCount(50);
            app2.setAuthorizeCallCount(10);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, 1);
            app2.setExpiryDate(calendar.getTime());
            Map<String, String> keyPare = RSAUtil.generateKeyBase64();
            app2.setRsaPubKey(keyPare.get(RSAUtil.PUBLIC_KEY));
            app2.setRsaPrivateKey(keyPare.get(RSAUtil.PRIVATE_KEY));
            app2.setAesKey(aesKey);
            appService.insert(app2);
        }

        user = appUserService.selectByUsername(username);
        if (null == user) {
            user = new AppUser();
            user.setUsername(username);
            user.setPassword(SHAUtil.SHA1(password));
            user.setPhone("15328008307");
            user.setNickname("唐辉");
            user.setCreateTime(new Date(System.currentTimeMillis()));
            appUserService.insert(user);
        }

        user = appUserService.selectByUsername("donglind");
        if (null == user) {
            user = new AppUser();
            user.setUsername("donglind");
            user.setPassword(SHAUtil.SHA1("deng88"));
            user.setPhone("15881142004");
            user.setNickname("张东林donglind");
            user.setCreateTime(new Date(System.currentTimeMillis()));
            appUserService.insert(user);
        }

        user = appUserService.selectByUsername("witaction");
        if (null == user) {
            user = new AppUser();
            user.setUsername("witaction");
            user.setPassword(SHAUtil.SHA1("Dbke6413"));
            user.setPhone("15328018817");
            user.setNickname("张东林witaction");
            user.setCreateTime(new Date(System.currentTimeMillis()));
            appUserService.insert(user);
        }

        user = appUserService.selectByUsername("test01");
        if (null == user) {
            user = new AppUser();
            user.setUsername("test01");
            user.setPassword(SHAUtil.SHA1("test"));
            user.setPhone("15828629878");
            user.setNickname("李伟test01");
            user.setCreateTime(new Date(System.currentTimeMillis()));
            appUserService.insert(user);
        }

        user = appUserService.selectByUsername("test02");
        if (null == user) {
            user = new AppUser();
            user.setUsername("test02");
            user.setPassword(SHAUtil.SHA1("test"));
            user.setNickname("李伟test02");
            user.setCreateTime(new Date(System.currentTimeMillis()));
            appUserService.insert(user);
        }


        user = appUserService.selectByUsername("Dbke6413");
        if (null == user) {
            user = new AppUser();
            user.setUsername("Dbke6413");
            user.setPassword(SHAUtil.SHA1("Dbke6413"));
            user.setNickname("余长江Dbke6413");
            user.setPhone("15881113607");
            user.setCreateTime(new Date(System.currentTimeMillis()));
            appUserService.insert(user);
        }


        user = appUserService.selectByUsername("test03");
        if (null == user) {
            user = new AppUser();
            user.setUsername("test03");
            user.setPassword(SHAUtil.SHA1("test"));
            user.setNickname("余长江test03");
            user.setCreateTime(new Date(System.currentTimeMillis()));
            appUserService.insert(user);
        }


        user = appUserService.selectByUsername("test04");
        if (null == user) {
            user = new AppUser();
            user.setUsername("test04");
            user.setPassword(SHAUtil.SHA1("test"));
            user.setNickname("余长江test04");
            user.setCreateTime(new Date(System.currentTimeMillis()));
            appUserService.insert(user);
        }
    }
}
