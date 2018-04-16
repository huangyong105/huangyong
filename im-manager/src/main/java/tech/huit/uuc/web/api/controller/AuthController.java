package tech.huit.uuc.web.api.controller;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import tech.huit.conf.SystemConf;
import tech.huit.util.encrypt.InvalidTokenException;
import tech.huit.util.encrypt.UserLoginInfo;
import tech.huit.uuc.entity.auth.App;
import tech.huit.uuc.service.auth.AppService;
import tech.huit.uuc.service.auth.UserService;
import tech.huit.uuc.system.ErrorCode;
import tech.huit.web.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 融合通讯业务集成登录服务：本服务主要完成appToken到uucToken的转换（实际上就是建立业务系统账号到融合通讯系统全局账号的关系映射）
 * 1、接入融合通讯服务的业务系统首先要在融合通讯服务器上进行业务接入注册，注册后分配RSA和AES密码用于加密通讯
 * 2、由各业务系统自行完成用户登录认证（参考《内置测试应用登录服务》），使用AES密码给用户签发用于集成登录用的appToken{appUid（业务系统中的uid）&nickname（业务系统中的显示名）&expirationTime(token过期时间)}
 * 3、app或PC终端从业务系统得到登录appToken后调用http://uuc.huit.tech:8888/auth/login?appId=1&appToken=xxxx得到uucToken（失效时间同appToken）及socket服务器链接信息
 * 4、融合通讯的所有服务都采用uucToken作为用户标识和认证
 * <p>
 * 返回数据UTF-8编码的Json：{"data":{"uucToken":"认证token","socketHost":"uuc.huit.tech","socketPort":6415},"status":true,"errorCode":"错误编码","errorMsg":"错误信息描述"}
 * uucToken=UrlSafeBase64(AES(uid&appId&appUid&nickname&expirationTime))
 * <p>
 */
@Controller
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    public static final String UUC_TOKEN = "uucToken";
    private String authAesKey = SystemConf.get("auth.aes.key");
    private String socketHost = SystemConf.get("uuc.socket.host");
    private int socketPort = SystemConf.get("uuc.socket.port", Integer.class);

    @Autowired
    private UserService userService;

    @Autowired
    private AppService appService;


    @ResponseBody
    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseStatus login(@RequestParam int appId, @RequestParam String appToken) {
        ResponseStatus rs = new ResponseStatus();
        try {
            App app = appService.selectById(appId);
            UserLoginInfo userInfo = UserLoginInfo.parseAppToken(appToken, app.getAesKey());
            if (userInfo.isExpired()) {//token过期
                rs.errorCode = ErrorCode.AUTH_TOKEN_EXPIRE.getCode();
                rs.errorMsg = ErrorCode.AUTH_TOKEN_EXPIRE.getMsg();
            } else {
                userInfo.setAppId(appId);
                int uid = userService.appUserLogin(userInfo);
                userInfo.setUid(uid);
                rs.setStatus(true);
                Map<String, Object> data = new HashMap();
                data.put("socketHost", socketHost);
                data.put("socketPort", socketPort);
                data.put("uid", uid);
                data.put(UUC_TOKEN, userInfo.genUucToken(authAesKey));
                rs.setData(data);
            }
        } catch (InvalidTokenException e) {
            rs.errorCode = ErrorCode.AUTH_TOKEN_ERROR.getCode();
            rs.errorMsg = ErrorCode.AUTH_TOKEN_ERROR.getMsg();
        }
        logger.info("appId:{} token:{} result:{}", appId, appToken, JSON.toJSONString(rs));
        return rs;
    }
}



