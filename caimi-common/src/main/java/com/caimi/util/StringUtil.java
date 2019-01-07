package com.caimi.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class StringUtil {

    public static final Charset GBK = Charset.forName("GBK");
    public static final Charset UTF8 = Charset.forName("UTF-8");
    public static final Charset UTF16 = Charset.forName("UTF-16");
    public static final Charset UTF32 = Charset.forName("UTF-32");

    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static String md5(String input) {
        byte[] source;
        try {
            // Get byte according by specified coding.
            source = input.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            source = input.getBytes();
        }
        String result = null;
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(source);
            // The result should be one 128 integer
            byte temp[] = md.digest();
            char str[] = new char[16 * 2];
            int k = 0;
            for (int i = 0; i < 16; i++) {
                byte byte0 = temp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            result = new String(str);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 大小写敏感
     */
    public static boolean equals(String str1, String str2) {
        boolean str1e = isEmpty(str1);
        boolean str2e = isEmpty(str2);
        // both empty
        if (str1e && str2e) {
            return true;
        }
        if (str1e != str2e) {
            return false;
        }
        return str1.equals(str2);
    }

    public static String[] split(String str, String regex) {
        if (isEmpty(str)) {
            return new String[] {};
        }
        List<String> result = new ArrayList<>();
        for (String elem : str.split(regex)) {
            elem = elem.trim();
            if (!isEmpty(elem)) {
                result.add(elem);
            }
        }
        return result.toArray(new String[result.size()]);
    }

    public static List<String[]> splitKVs(String str) {
        List<String[]> result = new ArrayList<>();
        for (String p0 : str.split("(,|;)\\s*")) {
            String kv[] = p0.split("\\s*(:|=)\\s*");
            result.add(kv);
        }
        return result;
    }

    // 首字母大写
    public static String upperFirstString(String str) {
        char[] ca = str.toCharArray();
        ca[0] -= 32;
        return String.valueOf(ca);
    }

}
