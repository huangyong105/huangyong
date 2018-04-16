package tech.huit.util.encrypt;

import org.apache.commons.lang3.StringUtils;

/**
 * 接入服务的第三方业务系统根据AES生成加密数据：appToken=appUid&groupIds&nickname&expirationTime
 * 转换后得到：uucToke=uid&appUid&groupIds&nickname&loginTime&expirationTime
 * Created by huit on 2017/6/11.
 */
public class UserLoginInfo {
    private static final String DELIMITER = "&";
    private int uid;//全局uid，融合通讯服务器全局用户唯一标识
    private int appId;//业务系统id
    private String appUid;//业务系统中用户唯一标识，如：uid
    private String groupIds;//用户拥有访问权限的资源分组id逗号表达式，如：1,2,3
    private String nickname;//用户昵称
    private int expirationTime;//token过期时间

    public String genAppToken(String aesKey) {
        StringBuffer sb = new StringBuffer();
        sb.append(appUid).append(DELIMITER);
        sb.append(groupIds).append(DELIMITER);
        sb.append(nickname.replace(DELIMITER, "")).append(DELIMITER);//删除用户昵称的&关键字
        sb.append(expirationTime).append(DELIMITER);
        return AESUtil.encryptUrlSafeBase64(sb.toString(), aesKey);
    }


    /**
     * @param token 第三方系统生成的加密数据：app_uid&nickname&expirationTime
     * @param key   第三方系统key
     * @return
     * @throws InvalidTokenException
     */
    public static UserLoginInfo parseAppToken(String token, String key) throws InvalidTokenException {
        UserLoginInfo userInfo;
        try {
            String decodeData;
            if (StringUtils.isEmpty(token) || StringUtils.isEmpty(decodeData = AESUtil.decryptUrlSafeBase64(token, key))) {
                throw new InvalidTokenException();
            }
            String[] parseData = decodeData.split(DELIMITER);
            if (parseData.length != 4) {
                throw new InvalidTokenException();
            }

            userInfo = new UserLoginInfo();
            userInfo.setAppUid(parseData[0]);
            userInfo.setGroupIds(parseData[1]);
            userInfo.setNickname(parseData[2]);
            userInfo.setExpirationTime(Integer.valueOf(parseData[3]));
        } catch (Throwable e) {
            throw new InvalidTokenException();
        }
        return userInfo;
    }

    public String genUucToken(String aesKey) {
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(DELIMITER);
        sb.append(appId).append(DELIMITER);
        sb.append(appUid).append(DELIMITER);
        sb.append(groupIds).append(DELIMITER);
        sb.append(nickname).append(DELIMITER);
        sb.append(expirationTime).append(DELIMITER);
        return AESUtil.encryptUrlSafeBase64(sb.toString(), aesKey);
    }

    public static UserLoginInfo parseUucToken(String token, String key) throws InvalidTokenException {
        UserLoginInfo userInfo;
        try {
            String decodeData;
            if (StringUtils.isEmpty(token) || StringUtils.isEmpty(decodeData = AESUtil.decryptUrlSafeBase64(token, key))) {
                throw new InvalidTokenException();
            }
            String[] parseData = decodeData.split(DELIMITER);
            if (parseData.length != 6) {
                throw new InvalidTokenException();
            }

            userInfo = new UserLoginInfo();
            userInfo.setUid(Integer.valueOf(parseData[0]));
            userInfo.setAppId(Integer.valueOf(parseData[1]));
            userInfo.setAppUid(parseData[2]);
            userInfo.setGroupIds(parseData[3]);
            userInfo.setNickname(parseData[4]);
            userInfo.setExpirationTime(Integer.valueOf(parseData[5]));
        } catch (Throwable e) {
            throw new InvalidTokenException();
        }
        return userInfo;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getAppUid() {
        return appUid;
    }

    public String getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(String groupIds) {
        this.groupIds = groupIds;
    }

    public void setAppUid(String appUid) {
        this.appUid = appUid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(int expirationTime) {
        this.expirationTime = expirationTime;
    }

    @Override
    public String toString() {
        return "UserLoginInfo{" +
                "uid=" + uid +
                ", appId=" + appId +
                ", appUid=" + appUid +
                ", nickname='" + nickname + '\'' +
                ", expirationTime=" + expirationTime +
                '}';
    }

    /**
     * 判断token是否过期
     *
     * @return
     */
    public boolean isExpired() {
        return expirationTime < System.currentTimeMillis() / 1000;
    }
}
