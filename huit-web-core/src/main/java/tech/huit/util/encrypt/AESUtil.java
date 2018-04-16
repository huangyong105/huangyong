package tech.huit.util.encrypt;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.Random;

/**
 * <p>功能:AES加密工具类</p>
 */
public class AESUtil {
    private final static String AES = "AES";
    //    public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";//"算法/模式/补码方式" ,c++解密后最后面有padding的乱码
    public static final String CIPHER_ALGORITHM = "AES/CBC/NoPadding";//"算法/模式/补码方式"

    /**
     * 根据键值进行加密
     *
     * @param data
     * @param key  加密键key 转换成字节数组必须是16位
     * @return
     * @throws Exception
     */
    public static String encryptUrlSafeBase64(String data, String key) {
        byte[] bt = null;
        try {
            bt = encrypt(data.getBytes(StandardCharsets.UTF_8), key.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
        return UrlSafeBase64.encode(bt);
    }

    /**
     * 根据键值进行加密
     *
     * @param data
     * @param key  加密键key 转换成字节数组必须是16位
     * @return
     * @throws Exception
     */
    public static String decryptUrlSafeBase64(String data, String key) {
        byte[] bt = UrlSafeBase64.decode(data);
        try {
            bt = decrypt(bt, key.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ignore) {
            ignore.printStackTrace();
            return null;
        }
        return new String(bt, StandardCharsets.UTF_8).trim();
    }

    /**
     * 数据AES加密
     *
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, AES);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        IvParameterSpec iv = new IvParameterSpec(key);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
//        cipher.init(Cipher.ENCRYPT_MODE, secretKey);// ECB用这个
        int blockSize = cipher.getBlockSize();
        int plaintextLength = data.length;
        if (plaintextLength % blockSize != 0) {
            plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
        }
        byte[] plaintext = new byte[plaintextLength];
        System.arraycopy(data, 0, plaintext, 0, data.length);
        byte[] result = cipher.doFinal(plaintext);
        return result; // 加密
    }

    /**
     * 数据AES解密
     *
     * @param data 要加密的数据
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, AES);
        IvParameterSpec iv = new IvParameterSpec(key);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);// 初始化
//        cipher.init(Cipher.DECRYPT_MODE, secretKey);// ECB用这个
        return cipher.doFinal(data);     //解密
    }


    public static final int TEST_COUNT = 80000;

    //测试
    public static void main(String args[]) throws Exception {
        //待加密内容
        String str = "k=hvfkN/qlp/zhXR3cuerq6jd2Z7g&t=1494296287";
        //密码，长度要是16或32位字符
        String password = "12345678123456781234567812345678";
//        String password = "1234567812345678";
//        String password = genRandomKey(16);
//        String password = genRandomKey(32);
        System.out.println("aes-source-data:" + str + " length:" + str.length());
        System.out.println("aes-key:" + password + " 算法/模式/补码方式:" + CIPHER_ALGORITHM + " 密钥位数:" + password.length() * 8);
//        performanceTest(str, password);

        String result = AESUtil.encryptUrlSafeBase64(str, password);
//        String result = "8tFDiPv8WDyhxTbN5RP-VvpKFUEwO0oaMwvlpiwL6hw";
        System.out.println("aes-encode-base64-urlSafe：" + result + " length:" + result.length());
        String decryResult = AESUtil.decryptUrlSafeBase64(result, password);
        System.out.println("aes-decode-base64-urlSafe：" + decryResult + " length:" + decryResult.length());

    }

    static {
        System.out.println(System.getProperty("java.version"));
        for (Provider provider : Security.getProviders())
            System.out.println(provider);
    }



    private static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < length; ++i) {
            int number = random.nextInt(62);//[0,62)

            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 得到一个128位的随机key
     *
     * @return
     */
    public static String genRandomKey() {
        try {
            return genRandomKey(16);
        } catch (Exception e) {
        }
        return null;
    }

    public static String genRandomKey(int keySize) throws Exception {
        if (16 != keySize && 32 != keySize) {
            throw new Exception("长度必须是16或32");
        }
        return getRandomString(keySize);
    }

    private static void performanceTest(String str, String password) throws Exception {
        for (int i = 0; i < TEST_COUNT; i++) {//预热一把
            AESUtil.encryptUrlSafeBase64(str, password);
        }
        long beginTime = System.currentTimeMillis();
        for (int i = 0; i < TEST_COUNT; i++) {
            //AESUtil.encrypt(str, password);
            AESUtil.encryptUrlSafeBase64(str, password);
        }
        long useTime = System.currentTimeMillis() - beginTime;
        System.out.println("aes-encoded UseTime:" + useTime + " speed:" + TEST_COUNT / 1.0 / useTime * 1000);

        beginTime = System.currentTimeMillis();
        String result = AESUtil.encryptUrlSafeBase64(str, password);
        String decode;
        for (int i = 0; i < TEST_COUNT; i++) {
            decode = AESUtil.decryptUrlSafeBase64(result, password);
        }
        useTime = System.currentTimeMillis() - beginTime;
        System.out.println("aes-decoded UseTime:" + useTime + " speed:" + TEST_COUNT / 1.0 / useTime * 1000);
    }

    /**
     * 1.创建一个KeyGenerator 2.调用KeyGenerator.generateKey方法
     * 由于某些原因，这里只能是128，如果设置为256会报异常
     * 如果要使用256下载并替换http://download.oracle.com/otn-pub/java/jce/8/jce_policy-8.zip?AuthParam=1494897964_cb65b914c679577c993d53373194040b
     *
     * @return
     */
    public static byte[] generateAESSecretKey() {
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(AES);
            keyGenerator.init(256);
            return keyGenerator.generateKey().getEncoded();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}