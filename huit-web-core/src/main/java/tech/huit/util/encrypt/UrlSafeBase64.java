package tech.huit.util.encrypt;


import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 生成url安全的base64编码，('+','/','=')分别替换为('-','_','')，字符串使用UTF-8编码，生成http get能正确使用的base64
 * <p>
 * Created by huit on 2017/6/4.
 */
public class UrlSafeBase64 {
    public static String encode(byte[] data) {
        return urlSafeEncode(Base64.getEncoder().encodeToString(data));
    }

    public static String encode(String data) {
        return urlSafeEncode(Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8)));
    }

    public static String urlSafeEncode(String data) {
        char[] base64 = data.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (char c : base64) {
            if (c == '+') {
                c = '-';
            } else if (c == '/') {
                c = '_';
            } else if (c == '=') {
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static String urlSafeDecode(String data) {
        char[] base64 = data.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (char c : base64) {
            if (c == '-') {
                c = '+';
            } else if (c == '_') {
                c = '/';
            }
            sb.append(c);
        }
        int mod = sb.length() % 4;
//        for (int i = 0; i < 4 - mod; i++) {//数据补全,java版的经过测试不用补全也能正常使用
//            sb.append('=');
//        }
        return sb.toString();
    }

    public static byte[] decode(String data) {
        return Base64.getDecoder().decode(urlSafeDecode(data).getBytes(StandardCharsets.UTF_8));
    }

    public static void main(String[] args) {
        String data = "uid=1&deadline中国" + System.currentTimeMillis() / 1000 + 5;
        System.out.println("data:" + data + " len:" + data.length());
        String key = AESUtil.genRandomKey();
        String encodeData = AESUtil.encryptUrlSafeBase64(data, key);
        System.out.println("encodeData:" + encodeData + " len:" + encodeData.length());
        String decodeData = AESUtil.decryptUrlSafeBase64(encodeData, key);
        System.out.println("decodeData:" + decodeData + " len:" + decodeData.length());
    }


}
