package com.example.fileproviderdemo;

import android.content.Intent;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author wanlijun
 * @description
 * @time 2018/3/20 11:05
 */

/**
 * 网络请求中数据加密一般采用如下方式：
 * 前端用RSA公钥加密AES的key和iv
 * AES加密要传输的数据
 * 后台用RSA私钥解密出key和iv
 * 再用AES解密出传输的数据
 * 后台将查询到的数据用AES加密传输给前端
 * 前端用自己的key和iv用AES解密出获得的数据
 */

public class AESUtils {
    /** 算法/模式/填充 **/
    private static final String CipherMode = "AES/CBC/PKCS5Padding";
    private static final String HEX = "0123456789ABCDEF";
    public static final String MY_SEED = "howdoyoudo";
    /** Android5.0及以下适用，5.0以上不适用，5.0以上加密后得到的数据为空**/
    public static String encrypt(String seed,String cleartext){
        byte[] result = null;
        try {
            byte[] rawKey = getRawKey(seed.getBytes());
            SecretKeySpec secretKeySpec = new SecretKeySpec(rawKey,"AES");
            Cipher cipher = Cipher.getInstance(CipherMode);
            cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec,new IvParameterSpec(new byte[cipher.getBlockSize()]));
            result = cipher.doFinal(cleartext.getBytes());
        }catch (Exception e){
            e.printStackTrace();
        }
        return toHex(result);
    }
    public static String decrypt(String seed,String ciphertext){
        String content = null;
        try {
            byte[] rawKey = getRawKey(seed.getBytes());
            byte[] dec = toByte(ciphertext);
            SecretKeySpec secretKeySpec = new SecretKeySpec(rawKey,"AES");
            Cipher cipher = Cipher.getInstance(CipherMode);
            cipher.init(Cipher.DECRYPT_MODE,secretKeySpec,new IvParameterSpec(new byte[cipher.getBlockSize()]));
            byte[] result = cipher.doFinal(dec);
            content = new String(result);
        }catch (Exception e){
            e.printStackTrace();
        }
        return content;
    }
    private static byte[] getRawKey(byte[] seed) throws Exception{
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        secureRandom.setSeed(seed);
        generator.init(128,secureRandom);
        SecretKey key = generator.generateKey();
        return key.getEncoded();
    }
    public static String toHex(byte[] buf){
        if(buf == null){
            return "";
        }
        StringBuffer sb = new StringBuffer(2*buf.length);
        for(int i=0;i<buf.length;i++){
            sb.append(HEX.charAt((buf[i]>>4) & 0x0f)).append(HEX.charAt(buf[i] & 0x0f));
        }
        return sb.toString();
    }
    public static byte[] toByte(String hexString){
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for(int i=0;i<len;i++){
            result[i] = Integer.valueOf(hexString.substring(2*i,2*i+2),16).byteValue();
        }
        return  result;
    }

    /**适用于所有Android系统版本，兼容性非常好**/
    public static String encrypt(String cleartext,String key,String iv){
        String ciphertext = "";
        try {
            byte[] data = cleartext.getBytes("UTF-8");
            SecretKeySpec secretKeySpec = createKey(key);
            Cipher cipher = Cipher.getInstance(CipherMode);
            cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec,createIv(iv));
            byte[] result = cipher.doFinal(data);
            ciphertext = byte2Hex(result);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ciphertext;
    }
    public static String decrypt(String ciphertext,String key,String iv){
        String cleartext = "";
        try {
            byte[] data = hex2Byte(ciphertext);
            SecretKeySpec secretKeySpec = createKey(key);
            Cipher cipher = Cipher.getInstance(CipherMode);
            cipher.init(Cipher.DECRYPT_MODE,secretKeySpec,createIv(iv));
            byte[] result = cipher.doFinal(data);
            cleartext = new String(result,"UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        return cleartext;
    }
    private static SecretKeySpec createKey(String key){
        byte[] data = null;
        if(key == null){
            key = "";
        }
        StringBuffer sb = new StringBuffer(16);
        sb.append(key);
        while (sb.length()<16){
            sb.append("0");
        }
        if(sb.length() > 16){
            sb.setLength(16);
        }
        try {
            data = sb.toString().getBytes("UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        return new SecretKeySpec(data,"AES");
    }
    private static IvParameterSpec createIv(String iv){
        byte[] data = null;
        if(iv == null){
            iv = "";
        }
        StringBuffer sb = new StringBuffer(16);
        sb.append(iv);
        while (sb.length() < 16){
            sb.append("0");
        }
        if(sb.length() > 16){
            sb.setLength(16);
        }
        try {
            data = sb.toString().getBytes("UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        return new IvParameterSpec(data);
    }

    private static String byte2Hex(byte[] bytes){
        StringBuffer sb = new StringBuffer(bytes.length * 2);
        String temp = "";
        for(int i=0;i<bytes.length;i++){
            temp = Integer.toHexString(bytes[i] & 0xff);
            if(temp.length() == 1){
                sb.append("0");
            }
            sb.append(temp);
        }
        return sb.toString().toUpperCase();
    }
    private static byte[] hex2Byte(String hexString){
        if(hexString == null || hexString.length() < 2){
            return new byte[0];
        }
        hexString = hexString.toLowerCase();
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for(int i=0;i<len;i++){
            String temp = hexString.substring(2*i,2*i+2);
            result[i] = (byte)(Integer.parseInt(temp,16) & 0XFF);
        }
        return result;
    }
}
