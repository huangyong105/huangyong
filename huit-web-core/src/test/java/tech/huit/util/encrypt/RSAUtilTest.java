package tech.huit.util.encrypt;

import java.io.FileWriter;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

/**
 * Created by huit on 2017/7/15.
 */
public class RSAUtilTest {

    public static final String PLAIN_TEXT = "authorize_code=testAuthorizeCode&deadline=" + (System.currentTimeMillis() / 1000 + 600000);
    private static final String KEY_SAVE_PATH = "D:\\project\\uuc";
    public static final String D_RSA_PUB_KEY = KEY_SAVE_PATH + "/rsa-pub.key";//公钥存放路径
    public static final String D_RSA_PRIVATE_KEY = KEY_SAVE_PATH + "/rsa-private.key";//私钥存放路径
    public static final int PUB_TEST_COUNT = 80000;
    public static final int PRIVITE_TEST_COUNT = 5000;
    public static boolean isPerformanceTesting = false;

    private static void performanceTest(PublicKey pubKey, PrivateKey priKey) {
        String encodedStr = null, decodeStr = null;
        byte[] encodedText = null;
        long beginTime, useTime;
        if (isPerformanceTesting) {
            for (int i = 0; i < PUB_TEST_COUNT; i++) {//预热不计算时间
                encodedText = RSAUtil.encode(pubKey, PLAIN_TEXT.getBytes());
                encodedStr = Base64.getEncoder().encodeToString(encodedText);
            }
            beginTime = System.currentTimeMillis();
            for (int i = 0; i < PUB_TEST_COUNT; i++) {
                encodedText = RSAUtil.encode(pubKey, PLAIN_TEXT.getBytes());
                encodedStr = Base64.getEncoder().encodeToString(encodedText);
            }
            useTime = System.currentTimeMillis() - beginTime;
            System.out.println("rsa-encoded-by-pub UseTime:" + useTime + " speed:" + PUB_TEST_COUNT / 1.0 / useTime * 1000);

            beginTime = System.currentTimeMillis();
            for (int i = 0; i < PRIVITE_TEST_COUNT; i++) {
                decodeStr = RSAUtil.decode(priKey, encodedText);
            }
            useTime = System.currentTimeMillis() - beginTime;
            System.out.println("rsa-decoded-by-pri UseTime:" + useTime + " speed:" + PRIVITE_TEST_COUNT / 1.0 / useTime * 1000);

            beginTime = System.currentTimeMillis();
            for (int i = 0; i < PRIVITE_TEST_COUNT; i++) {
                encodedText = RSAUtil.encode(priKey, PLAIN_TEXT.getBytes());
                encodedStr = Base64.getEncoder().encodeToString(encodedText);
            }
            useTime = System.currentTimeMillis() - beginTime;
            System.out.println("rsa-encoded-by-pri UseTime:" + useTime + " speed:" + PRIVITE_TEST_COUNT / 1.0 / useTime * 1000);

            beginTime = System.currentTimeMillis();
            for (int i = 0; i < PUB_TEST_COUNT; i++) {
                decodeStr = RSAUtil.decode(pubKey, encodedText);
            }
            useTime = System.currentTimeMillis() - beginTime;
            System.out.println("rsa-decoded-by-pub UseTime:" + useTime + " speed:" + PUB_TEST_COUNT / 1.0 / useTime * 1000);
        }
    }
    private static void genKeyPair() throws NoSuchAlgorithmException, IOException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSAUtil.KEY_ALGORITHM);
        keyPairGenerator.initialize(RSAUtil.KEY_SIZE);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        String pubKeyStr = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String priKeyStr = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        writePubAndPrivateKey(pubKeyStr, priKeyStr);
        System.out.println("pubKey:" + pubKeyStr);
        System.out.println("priKey:" + priKeyStr);
    }

    private static void writePubAndPrivateKey(String pubKeyStr, String priKeyStr) throws IOException {
        FileWriter file = new FileWriter(D_RSA_PUB_KEY);
        file.write(pubKeyStr);
        file.close();

        file = new FileWriter(D_RSA_PRIVATE_KEY);
        file.write(priKeyStr);
        file.close();
    }
}
