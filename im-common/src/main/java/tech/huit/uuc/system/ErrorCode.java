package tech.huit.uuc.system;

/**
 * Created by huit on 2017/6/3.
 */
public enum ErrorCode {
    AUTH_CAPTCHA_NEED("0001", "登录错误次数太多，需要短信验证码"),
    AUTH_CAPTCHA_ERROR("0002", "登录验证码错误"),
    AUTH_TOKEN_EXPIRE("0003", "token长时间未登录已经过期，请重新登录"),
    AUTH_TOKEN_ERROR("0004", "登录Token错误"),
    AUTH_USER_PWD_ERROR("0005", "用户名或密码错误"),
    SIP_WITHOUT_USABLE_ACCOUNT("0200", "没有可用的SIP账号"),
    CONTACT_UID_ERROR("0300", "联系人uid错误"),
    MSG_LOGIN_PARAM_ERROR("0100", "参数错误,token和deviceType不能为空"),
//    MSG_LOGIN_NEW_DEVICE("0101", "有新的同类型终端登录，你已经被迫下线"),
    MSG_PARAM_ERROR("0102", "参数错误"),
    MSG_USER_NOT_LOGIN("0120", "当前操作用户没有正确登录，请检查登录状态");


    private String code;
    private String msg;

    ErrorCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
