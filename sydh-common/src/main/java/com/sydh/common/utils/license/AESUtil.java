package com.sydh.common.utils.license;

import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AESUtil {
    private static final Logger bZ = LoggerFactory.getLogger(AESUtil.class);

    private static final String ca = "AES/CBC/PKCS5Padding";
    private static final int cb = 256;
    private static final int cc = 16;
    private static final String cd = "FastBeeSSFDSFSDFDTXVXCZ@&";
    private static final String ce = "UTF-8";
    private static final String cf = "PBKDF2WithHmacSHA256";
    private static final int cg = 65536;
    private static final int ch = 256;

    public static String encrypt(String data, String password) throws Exception {
        SecretKey secretKey = a(password, "FastBeeSSFDSFSDFDTXVXCZ@&".getBytes());
        byte[] arrayOfByte1 = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(arrayOfByte1);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(arrayOfByte1);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(1, secretKey, ivParameterSpec);

        byte[] arrayOfByte2 = cipher.doFinal(data.getBytes("UTF-8"));
        byte[] arrayOfByte3 = new byte[16 + arrayOfByte2.length];
        System.arraycopy(arrayOfByte1, 0, arrayOfByte3, 0, 16);
        System.arraycopy(arrayOfByte2, 0, arrayOfByte3, 16, arrayOfByte2.length);

        return Base64.getEncoder().encodeToString(arrayOfByte3);
    }

    public static String decrypt(String encryptedData, String password) throws Exception {
        byte[] arrayOfByte1 = Base64.getDecoder().decode(encryptedData);
        byte[] arrayOfByte2 = new byte[16];
        System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, 16);
        byte[] arrayOfByte3 = new byte[arrayOfByte1.length - 16];
        System.arraycopy(arrayOfByte1, 16, arrayOfByte3, 0, arrayOfByte3.length);

        SecretKey secretKey = a(password, "FastBeeSSFDSFSDFDTXVXCZ@&".getBytes());
        IvParameterSpec ivParameterSpec = new IvParameterSpec(arrayOfByte2);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(2, secretKey, ivParameterSpec);

        return new String(cipher.doFinal(arrayOfByte3), "UTF-8");
    }

    private static SecretKey a(String paramString, byte[] paramArrayOfbyte) throws Exception {
        if (paramArrayOfbyte == null) {
            paramArrayOfbyte = new byte[16];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(paramArrayOfbyte);
        }

        char[] arrayOfChar = paramString.toCharArray();
        PBEKeySpec pBEKeySpec = new PBEKeySpec(arrayOfChar, paramArrayOfbyte, 65536, 256);
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] arrayOfByte = secretKeyFactory.generateSecret(pBEKeySpec).getEncoded();
        return new SecretKeySpec(arrayOfByte, "AES");
    }
}
