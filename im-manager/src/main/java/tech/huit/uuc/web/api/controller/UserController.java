package tech.huit.uuc.web.api.controller;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import tech.huit.util.ServletRequestGetIp;
import tech.huit.conf.SystemConf;
import tech.huit.util.encrypt.InvalidTokenException;
import tech.huit.util.encrypt.SHAUtil;
import tech.huit.util.encrypt.UserLoginInfo;
import tech.huit.uuc.entity.user.AppUser;
import tech.huit.uuc.service.user.AppUserService;
import tech.huit.uuc.system.ErrorCode;
import tech.huit.web.ResponseStatus;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 内置测试应用登录服务：业务系统集成融合通讯系统的参考实现，用于融合通讯系统基础功能的测试
 * <p>
 * 1、使用账号密码登录：http://uuc.huit.tech:8888/user/login_by_account?username=huit&password=test&captcha=1234
 * 默认不需要输入验证码，一定时间范围内错误次数达到阀值将收到AUTH_CAPTCHA_NEED错误，此时需要使用验证码才能正确登录
 * 1.1 使用账号密码登录时，获取图形验证码：http://uuc.huit.tech:8888/user/get_pic_captcha?username=huit
 * <p>
 * 2、使用手机验证码登录：http://uuc.huit.tech:8888/user/login_by_phone?phone=15328008307&captcha=1234
 * <p>
 * 2.1 使用手机验证登录、获取短信验证码：http://uuc.huit.tech:8888/user/get_phone_captcha?phone=15328008307
 * 3、使用appToken登录：http://uuc.huit.tech:8888/user/login_by_token?token=xxx
 * 每次成功登录后得到的appToken，一定时间范围内可以使用appToken实现自动登录，每次登录成功后会更新appToken的值，客户端应该更新保存最新值，遇到返回AUTH_TOKEN_EXPIRE时需要使用第1或第2种方式重新登录
 * <p>
 * 返回数据UTF-8编码的Json：{"data":{"appToken":"auth登录时需要","appId":1},"status":true,"errorCode":"错误编码","errorMsg":"错误信息描述"}
 * appToken=
 * <p>
 */
@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    public static final String APP_ID = "appId";
    public static final String APP_TOKEN = "appToken";
    private String appAesKey = SystemConf.get("app.aes.key");
    private int appId = SystemConf.get("app.id", Integer.class);
    private int userErrorCount = SystemConf.get("auth.captcha.user.error.count", Integer.class);
    private int ipErrorCount = SystemConf.get("auth.captcha.ip.error.count", Integer.class);
    private int tokenExpireTime = SystemConf.get("auth.token.expire.time", Integer.class);

    private Cache loginCaptcha;//手机短信验证码
    private Cache loginErrorCount;//账号和IP登录错误次数计数，账号达到3次或IP达到10次强制要求输入验证码

    @Autowired
    private AppUserService userService;

    @Autowired
    public void setCacheManager(CacheManager ehcacheManager) {
        loginCaptcha = ehcacheManager.getCache("authLoginCaptcha");
        loginErrorCount = ehcacheManager.getCache("authLoginErrorCount");
    }

    @ResponseBody
    @RequestMapping(value = "/login_by_account", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseStatus loginByAccount(HttpServletRequest request, String username, String password, String captcha) {
        ResponseStatus rs = new ResponseStatus();
        AppUser user = null;
        String ip = ServletRequestGetIp.getIpAddr(request);
        if (isCaptchaOk(username, captcha, rs, ip)) {
            user = userService.selectByUsernameAndPassword(username, SHAUtil.SHA1(password));
            if (null != user) {
                rs.setStatus(true);
            } else {
                rs.errorCode = ErrorCode.AUTH_USER_PWD_ERROR.getCode();
                rs.errorMsg = ErrorCode.AUTH_USER_PWD_ERROR.getMsg();
                addLoingErrorCount(username);
                addLoingErrorCount(ip);
            }
        }

        genToken(rs, user);
        logger.info("username:{} password:{} captcha:{} result:{}", username, password, captcha, rs);
        return rs;
    }

    @ResponseBody
    @RequestMapping(value = "/login_by_phone", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseStatus loginByPhone(String phone, String captcha) {
        ResponseStatus rs = new ResponseStatus();
        AppUser user = null;

        Element element = loginCaptcha.get(phone);
        if (null == captcha || null == element || !captcha.equals(element.getObjectValue())) {
            rs.errorCode = ErrorCode.AUTH_CAPTCHA_ERROR.getCode();
            rs.errorMsg = ErrorCode.AUTH_CAPTCHA_ERROR.getMsg();
        } else {
            user = userService.selectByPhone(phone);
            if (null == user) {//自动创建登录账号
                user = new AppUser();
                user.setPhone(phone);
                user.setNickname(phone.substring(0, 3) + "***" + phone.substring(phone.length() - 4));
                user.setCreateTime(new Date(System.currentTimeMillis()));
                userService.insert(user);
            }
        }

        genToken(rs, user);
        logger.info("phone:{} captcha:{} result:{}", phone, captcha, rs);
        return rs;
    }

    @ResponseBody
    @RequestMapping(value = "/login_by_token", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseStatus loginByToken(String token) {
        ResponseStatus rs = new ResponseStatus();
        AppUser user = null;
        try {
            UserLoginInfo userInfo = UserLoginInfo.parseAppToken(token, appAesKey);
            if (userInfo.getExpirationTime() < System.currentTimeMillis() / 1000) {
                rs.errorCode = ErrorCode.AUTH_TOKEN_EXPIRE.getCode();
                rs.errorMsg = ErrorCode.AUTH_TOKEN_EXPIRE.getMsg();
            } else {
                user = userService.selectById(userInfo.getAppUid());
            }
        } catch (InvalidTokenException e) {
            rs.errorCode = ErrorCode.AUTH_TOKEN_ERROR.getCode();
            rs.errorMsg = ErrorCode.AUTH_TOKEN_ERROR.getMsg();
        }

        genToken(rs, user);
        logger.info("token:{} result:{}", token, rs);
        return rs;
    }

    private void genToken(ResponseStatus rs, AppUser user) {
        if (null != user) {
            rs.setStatus(true);
            int time = (int) (System.currentTimeMillis() / 1000);
            UserLoginInfo loginInfo = new UserLoginInfo();
            loginInfo.setAppUid(user.getId() + "");
            loginInfo.setNickname(user.getNickname());
            loginInfo.setExpirationTime((int) (System.currentTimeMillis() / 1000 + tokenExpireTime));
            Map<String, Object> data = new HashMap();
            data.put(APP_ID, appId);
            data.put(APP_TOKEN, loginInfo.genAppToken(appAesKey));
            data.put("nickname", user.getNickname());
            data.put("authUrl", SystemConf.get("app.aes.url"));
            Map<String, String> appExtra = new HashMap<>();
            appExtra.put("identifyUploadUrl", "http://119.23.78.116:8080/FaceApi/FaceMNHandler.ashx?identifyType=1");
            data.put("appExtra", appExtra);
            rs.setData(data);
        }
    }

    /**
     * 判断用户验证码是否正确
     *
     * @param username
     * @param captcha
     * @param rs
     * @param ip
     * @return
     */
    private boolean isCaptchaOk(String username, String captcha, ResponseStatus rs, String ip) {
        boolean isCaptchaEnable = isNeedCaptcha(username, userErrorCount);
        if (!isCaptchaEnable) {
            isNeedCaptcha(ip, ipErrorCount);
        }
        if (isCaptchaEnable) {
            Element captchaInCache = loginCaptcha.get(username);
            if (null == captcha || null == captchaInCache) {
                rs.errorCode = ErrorCode.AUTH_CAPTCHA_NEED.getCode();
                rs.errorMsg = ErrorCode.AUTH_CAPTCHA_NEED.getMsg();
                return false;
            } else if (!captcha.equals(captchaInCache.getObjectValue())) {
                rs.errorCode = ErrorCode.AUTH_CAPTCHA_ERROR.getCode();
                rs.errorMsg = ErrorCode.AUTH_CAPTCHA_ERROR.getMsg();
                return false;
            }
        }
        return true;
    }

    /**
     * 累计错误次数
     *
     * @param key
     */
    private void addLoingErrorCount(String key) {
        Element element = loginErrorCount.get(key);
        if (null == element) {
            loginErrorCount.put(new Element(key, new AtomicInteger(1)));
        } else {
            ((AtomicInteger) element.getObjectValue()).incrementAndGet();
        }
    }


    /**
     * 使用账号密码登录时获取图形验证码
     *
     * @param response
     */
    @RequestMapping(value = "/get_pic_captcha", method = {RequestMethod.GET, RequestMethod.POST})
    public void captcha(@RequestParam String username, HttpServletResponse response) {
        //设置页面不缓存
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // 在内存中创建图象
        int width = 110, height = 40;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // 获取图形上下文
        Graphics g = image.getGraphics();

        //生成随机类
        Random random = new Random();

        // 设定背景色
        g.setColor(getRandColor(200, 250));
        g.fillRect(0, 0, width, height);

        //设定字体
        g.setFont(new Font("Times New Roman", Font.PLAIN, 45));

        //画边框
        //g.setColor(new Color());
        //g.drawRect(0,0,width-1,height-1);


        // 随机产生155条干扰线，使图象中的认证码不易被其它程序探测到
        g.setColor(getRandColor(160, 200));
        for (int i = 0; i < 155; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }

        // 取随机产生的认证码(4位数字)
        String sRand = "";
        for (int i = 0; i < 4; i++) {
            String rand = String.valueOf(random.nextInt(10));
            sRand += rand;
            // 将认证码显示到图象中
            // 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            g.drawString(rand, 25 * i + 5, 36);
        }

        // 将认证码存入缓存
        loginCaptcha.put(new Element(username, sRand));
        logger.info("putPicCaptcha->username:{} captcha:{}", username, sRand);

        // 图象生效
        g.dispose();

        // 输出图象到页面
        response.setContentType("image/png");
        try {
            ImageIO.write(image, "PNG", response.getOutputStream());
        } catch (java.io.IOException ignore) {
        }
    }

    /**
     * 给定范围获得随机颜色
     *
     * @param fc
     * @param bc
     * @return
     */
    private Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    /**
     * 判断是否需要验证码
     *
     * @param key
     * @param maxCount
     * @return
     */
    private boolean isNeedCaptcha(String key, int maxCount) {
        Element element = loginErrorCount.get(key);
        if (null != element) {
            AtomicInteger thisErrorCount = (AtomicInteger) element.getObjectValue();
            if (thisErrorCount.get() > maxCount) {
                logger.info("captchaEnableByKey->key:{} thisErrorCount:{} maxCount:{}", key, thisErrorCount.get(), maxCount);
                return true;
            }
        }
        return false;
    }
}



