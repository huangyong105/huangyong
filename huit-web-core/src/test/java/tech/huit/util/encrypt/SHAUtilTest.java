package tech.huit.util.encrypt;

/**
 * Created by huit on 2017/7/15.
 */
public class SHAUtilTest {
    public static void main(String[] args) {
        String sha = SHAUtil.SHA1("a");
        System.out.println("sha1:" + sha + " length:" + sha.length());
        sha = SHAUtil.SHA256("a");
        System.out.println("sha256:" + sha + " length:" + sha.length());
        sha = SHAUtil.SHA512("a");
        System.out.println("sha256:" + sha + " length:" + sha.length());
    }
}
