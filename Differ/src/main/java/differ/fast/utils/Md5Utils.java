package differ.fast.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * <br>Created by Soybeany on 2019/9/27.
 */
public class Md5Utils {

    /**
     * 获得字节数组形式的md5码
     */
    public static byte[] getByteArr(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        return md.digest(input.getBytes());
    }

    /**
     * 对比两组字节数组形式的md5码是否一致
     */
    public static boolean hasSameContent(byte[] input1, byte[] input2) {
        for (int i = 0; i < input2.length; i++) {
            if (input1[i] != input2[i]) {
                return false;
            }
        }
        return true;
    }
}
