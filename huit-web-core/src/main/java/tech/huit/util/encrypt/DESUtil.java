package tech.huit.util.encrypt;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Created by huit on 2017/5/9.
 */
public class DESUtil {
    public static final int TEST_COUNT = 80000;

    public DESUtil() {
    }

    //测试
    public static void main(String args[]) throws Exception {
        //待加密内容
        String str = "k=hvfkN/qlp/zhXR3cuerq6jd2Z7g&t=1494296287";
        //密码，长度要是8的倍数
        String password = "1234567812345678";

        for (int i = 0; i < TEST_COUNT; i++) {//预热一把
            Base64.getEncoder().encodeToString(DESUtil.encrypt(str.getBytes(), password));
        }

        long beginTime = System.currentTimeMillis();
        for (int i = 0; i < TEST_COUNT; i++) {
            Base64.getEncoder().encodeToString(DESUtil.encrypt(str.getBytes(), password));
        }
        long useTime = System.currentTimeMillis() - beginTime;
        System.out.println("des-encoded UseTime:" + useTime + " speed:" + TEST_COUNT / 1.0 / useTime * 1000);

        byte[] result = DESUtil.encrypt(str.getBytes(), password);
        String resultStr = Base64.getEncoder().encodeToString(result);
        System.out.println("加密后：" + resultStr + " length:" + resultStr.length());
        //直接将如上内容解密
        beginTime = System.currentTimeMillis();
        for (int i = 0; i < TEST_COUNT; i++) {
            byte[] decryResult = DESUtil.decrypt(result, password);
        }
        useTime = System.currentTimeMillis() - beginTime;
        System.out.println("des-decoded UseTime:" + useTime + " speed:" + TEST_COUNT / 1.0 / useTime * 1000);

        byte[] decryResult = DESUtil.decrypt(result, password);
        System.out.println("解密后：" + new String(decryResult));

    }

    /**
     * 加密
     *
     * @param datasource byte[]
     * @param password   String
     * @return byte[]
     */
    public static byte[] encrypt(byte[] datasource, String password) {
        try {
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(password.getBytes());
            //创建一个密匙工厂，然后用它把DESKeySpec转换成
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESUtil");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            //Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance("DESUtil");
            //用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
            //现在，获取数据并加密
            //正式执行加密操作
            return cipher.doFinal(datasource);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     *
     * @param src      byte[]
     * @param password String
     * @return byte[]
     * @throws Exception
     */
    public static byte[] decrypt(byte[] src, String password) throws Exception {
        // DES算法要求有一个可信任的随机数源
        SecureRandom random = new SecureRandom();
        // 创建一个DESKeySpec对象
        DESKeySpec desKey = new DESKeySpec(password.getBytes());
        // 创建一个密匙工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESUtil");
        // 将DESKeySpec对象转换成SecretKey对象
        SecretKey securekey = keyFactory.generateSecret(desKey);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DESUtil");
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, random);
        // 真正开始解密操作
        return cipher.doFinal(src);
    }
}
