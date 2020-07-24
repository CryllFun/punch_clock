package com.zerfu.demo.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//import org.apache.coyote.http2.ByteUtil;
import org.springframework.util.DigestUtils;

//import com.mchange.lang.ByteUtils;

/**
 * @author 王小东
 * @date 2020年7月10日  上午10:57:23
 * @version 1.0
 */
public class MD5 {

    /**
     * apache的工具类DigestUtils来进行加密
     * @param str
     * @return
     */
    public static String getMd5(String str) {
        return DigestUtils.md5DigestAsHex(str.getBytes());
    }

    /**
     * 使用JDK自带的MessageDigest类
     * @param str
     * @return
     */
    public static String getMd5JDK(String str) {
        String md5Str = "";
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            byte[] digest = md5.digest(str.getBytes());
            md5Str = new BigInteger(1,digest).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5Str;
    }

    /**
     * 有前缀和后缀并且二次md5加密
     * @param str
     * @return
     */
    public static String getTwoMD5(String str) {
        //对原串拼接前缀
        str = "o2o" + str;
        //md5加密 16进制
        str = DigestUtils.md5DigestAsHex(str.getBytes());
        //对加密后的密文再次拼接后缀
        str = str + "richwit";
        //md5二次加密
        str =  DigestUtils.md5DigestAsHex(str.getBytes());
        return str;
    }

    public static void main(String[] args) {
        System.out.println("getMd5(\"abc\"):" + getMd5("abc"));
        System.out.println("getMd5JDK(\"abc\"):"+getMd5JDK("abc"));
        System.out.println("getTwoMD5(\"abc\"):"+getTwoMD5("abc"));
    }
}