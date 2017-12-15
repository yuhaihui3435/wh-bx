package com.yhh.whbx.kits;

import com.xiaoleilu.hutool.crypto.SecureUtil;
import com.xiaoleilu.hutool.crypto.symmetric.AES;
import com.xiaoleilu.hutool.crypto.symmetric.SymmetricAlgorithm;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yuhaihui8913 on 2017/5/27.
 */
public class _StrKit {
    public static final String CHINESE_REGEX = "[\\u4e00-\\u9fa5]";


    public static String matchResult(Pattern p, String str)
    {
        StringBuilder sb = new StringBuilder();
        Matcher m = p.matcher(str);
        while (m.find())
            for (int i = 0; i <= m.groupCount(); i++)
            {
                sb.append(m.group());
            }
        return sb.toString();
    }

    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }



    public static void main(String[] args) throws IOException {
        String a=SymmetricAlgorithm.AES.getValue();
        System.out.printf(a);
        byte[] key= SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
        System.out.printf(new String(key));
        AES aes=SecureUtil.aes(key);
        String s=aes.encryptHex("111111");
        System.out.printf(s);
        System.out.printf(aes.decryptStr(s));

    }


}
