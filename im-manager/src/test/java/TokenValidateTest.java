
import tech.huit.util.encrypt.RSAUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by huit on 2017/5/18.
 */
public class TokenValidateTest {
    public static void main(String[] args) {
        String url = "rtmp://221.236.245.41:1935/ssl/S+6/CA9kToAXeuXFIT+NojDGt8sCAggPS/Nzsas8FrHQWm";//加密数据
        if (url.contains("ssl")) {//根据ssl判断数据是否加密
            String encodeData = url.substring(url.indexOf("ssl"));//截取出加密数据
            String decodeData = getResoureIdFromEncodeData(encodeData);//调用解密函数解析得到真正的数据
            if (null == decodeData) {//非法请求
                return;
            } else {
                url = url.substring(0, url.indexOf("ssl")) + decodeData;//拼接出真正的url地址：rtmp://221.236.245.41:1935/xxxx
            }
        }
        //play url
    }

    private static Map<String, Long> onlyOnceCache = new ConcurrentHashMap<>();//url只能使用一次的情况

    /**
     * resource_id（资源标识）&deadline=1494814358（数据有效期）&only_once=true（是否只能使用一次）
     * 判断一个请求的数据是否有效
     *
     * @param encodeData
     * @return 如果有效请求返回资源标识:channelid，deadline和only_once只是getResoureIdFromEncodeData()这个方法内部判断逻辑有用。无效请求返回空
     */
    public static String getResoureIdFromEncodeData(String encodeData) {
        if (onlyOnceCache.containsKey(encodeData)) {//判断是否已经被使用过
        }
        String decodeData = RSAUtil.decodeFromBase64(RSAUtil.restorePublicKey("pubkey".getBytes()), encodeData);
        if (null == decodeData) {//判断能否正确解密
        }
        if (decodeData.contains("deadline")) {//判断有效期
        }
        if (decodeData.contains("only_once=true")) {//判读是否只能访问一次
            onlyOnceCache.put(encodeData, 1l);//vale使用deadline，如果没传deadline设定1天有效
        }
        return null;
    }
}
