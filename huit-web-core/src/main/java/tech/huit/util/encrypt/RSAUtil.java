package tech.huit.util.encrypt;

import java.io.*;
import java.net.URLDecoder;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class RSAUtil {
    public static final String KEY_ALGORITHM = "RSA";
    /**
     * 貌似默认是RSA/NONE/PKCS1Padding，未验证
     */
    public static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";
    //    public static final String CIPHER_ALGORITHM = "RSA/None/NoPadding";
    public static final String PUBLIC_KEY = "publicKey";
    public static final String PRIVATE_KEY = "privateKey";

    /**
     * RSA密钥长度必须是64的倍数，在512~65536之间。默认是1024
     */
//    public static final int KEY_SIZE = 2048;
    public static final int KEY_SIZE = 1024;
//    public static final int KEY_SIZE = 512;

    /**
     * 生成密钥对。注意这里是生成密钥对KeyPair，再由密钥对获取公私钥
     *
     * @return
     */
    public static Map<String, byte[]> generateKeyBytes() {

        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGenerator.initialize(KEY_SIZE);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

            Map<String, byte[]> keyMap = new HashMap<String, byte[]>();
            keyMap.put(PUBLIC_KEY, publicKey.getEncoded());
            keyMap.put(PRIVATE_KEY, privateKey.getEncoded());
            return keyMap;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成密钥对。注意这里是生成密钥对KeyPair，再由密钥对获取公私钥
     *
     * @return
     */
    public static Map<String, String> generateKeyBase64() {
        Map<String, byte[]> keyBytes = generateKeyBytes();
        Map<String, String> keyMap = new HashMap();
        keyMap.put(PUBLIC_KEY, UrlSafeBase64.encode(keyBytes.get(PUBLIC_KEY)));
        keyMap.put(PRIVATE_KEY, UrlSafeBase64.encode(keyBytes.get(PRIVATE_KEY)));
        return keyMap;
    }

    /**
     * 还原公钥，X509EncodedKeySpec 用于构建公钥的规范
     *
     * @param keyBytes
     * @return
     */
    public static PublicKey restorePublicKey(byte[] keyBytes) {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);

        try {
            KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
            PublicKey publicKey = factory.generatePublic(x509EncodedKeySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 还原私钥，PKCS8EncodedKeySpec 用于构建私钥的规范
     *
     * @param keyBytes
     * @return
     */
    public static PrivateKey restorePrivateKey(byte[] keyBytes) {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        try {
            KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey privateKey = factory.generatePrivate(pkcs8EncodedKeySpec);
            return privateKey;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加密，三步走。
     *
     * @param key
     * @param plainText
     * @return
     */
    public static byte[] encode(Key key, byte[] plainText) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(plainText);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加密成base64字符串
     *
     * @param key
     * @param plainText
     * @return
     */
    public static String encodeToBase64(Key key, byte[] plainText) {
        return Base64.getEncoder().encodeToString(encode(key, plainText));
    }

    /**
     * 加密成base64字符串
     *
     * @param key
     * @param plainText
     * @return
     */
    public static String encodeToBase64(Key key, String plainText) {
        if (null == plainText) {
            return null;
        }
        byte[] data = null;
        try {
            data = plainText.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ignore) {
        }
        return encodeToBase64(key, data);
    }

    /**
     * 解密，三步走。
     *
     * @param key
     * @param encodedText
     * @return
     */
    public static String decode(Key key, byte[] encodedText) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(encodedText));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decodeFromUrlEncodeAndBase64(Key key, String urlEncodeAndbase64Text) {
        String base64Text = null;
        try {
            base64Text = URLDecoder.decode(urlEncodeAndbase64Text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decode(key, Base64.getDecoder().decode(base64Text));
    }

    public static String decodeFromBase64(Key key, String base64Text) {
        if (null == base64Text) {
            return null;
        }
        byte[] encodeData = Base64.getDecoder().decode(base64Text);
        return decode(key, encodeData);
    }
}
