package caimi.common.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;

public class StringUtil {
	//字符集-设置编码方式
	public static final Charset GBK = Charset.forName("GBK");
	public static final Charset UTF8 = Charset.forName("UTF-8");
	public static final Charset UTF16 = Charset.forName("UTF-16");
	public static final Charset UTF32 = Charset.forName("UTF-32");
	
	public static boolean isEmpty(String str)
	{
		return str==null || str.trim().length()==0;
	} 
	
    public static String md5(String input) {
        byte[] source;
        try {
            //Get byte according by specified coding.
            source = input.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            source = input.getBytes();
        }
        String result = null;
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(source);
            //The result should be one 128 integer
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
	

}
