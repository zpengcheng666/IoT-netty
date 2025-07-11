package com.sydh.common.utils.wechat;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;


public class WXBizMsgCrypt {
    static Charset db = Charset.forName("utf-8");
    Base64 dc = new Base64();


    byte[] dd;


    String de;


    String df;


    public WXBizMsgCrypt(String token, String encodingAesKey, String receiveid) throws AesException {
        if (encodingAesKey.length() != 43) {
            throw new AesException(-40004);
        }

        this.de = token;
        this.df = receiveid;
        this.dd = Base64.decodeBase64(encodingAesKey + "=");
    }


    byte[] b(int paramInt) {
        byte[] arrayOfByte = new byte[4];
        arrayOfByte[3] = (byte) (paramInt & 0xFF);
        arrayOfByte[2] = (byte) (paramInt >> 8 & 0xFF);
        arrayOfByte[1] = (byte) (paramInt >> 16 & 0xFF);
        arrayOfByte[0] = (byte) (paramInt >> 24 & 0xFF);
        return arrayOfByte;
    }


    int f(byte[] paramArrayOfbyte) {
        int i = 0;
        for (byte b = 0; b < 4; b++) {
            i <<= 8;
            i |= paramArrayOfbyte[b] & 0xFF;
        }
        return i;
    }


    String h() {
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer stringBuffer = new StringBuffer();
        for (byte b = 0; b < 16; b++) {
            int i = random.nextInt(str.length());
            stringBuffer.append(str.charAt(i));
        }
        return stringBuffer.toString();
    }


    String encrypt(String randomStr, String text) throws AesException {
        a a = new a();
        byte[] arrayOfByte1 = randomStr.getBytes(db);
        byte[] arrayOfByte2 = text.getBytes(db);
        byte[] arrayOfByte3 = b(arrayOfByte2.length);
        byte[] arrayOfByte4 = this.df.getBytes(db);


        a.g(arrayOfByte1);
        a.g(arrayOfByte3);
        a.g(arrayOfByte2);
        a.g(arrayOfByte4);


        byte[] arrayOfByte5 = b.c(a.size());
        a.g(arrayOfByte5);


        byte[] arrayOfByte6 = a.i();


        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(this.dd, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(this.dd, 0, 16);
            cipher.init(1, secretKeySpec, ivParameterSpec);


            byte[] arrayOfByte = cipher.doFinal(arrayOfByte6);


            return this.dc.encodeToString(arrayOfByte);

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new AesException(-40006);
        }
    }


    String q(String paramString) throws AesException {
        byte[] arrayOfByte;
        String str1;
        String str2;
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(this.dd, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(Arrays.copyOfRange(this.dd, 0, 16));
            cipher.init(2, secretKeySpec, ivParameterSpec);


            byte[] arrayOfByte1 = Base64.decodeBase64(paramString);


            arrayOfByte = cipher.doFinal(arrayOfByte1);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new AesException(-40007);
        }


        try {
            byte[] arrayOfByte1 = b.h(arrayOfByte);


            byte[] arrayOfByte2 = Arrays.copyOfRange(arrayOfByte1, 16, 20);

            int i = f(arrayOfByte2);

            str1 = new String(Arrays.copyOfRange(arrayOfByte1, 20, 20 + i), db);
            str2 = new String(Arrays.copyOfRange(arrayOfByte1, 20 + i, arrayOfByte1.length), db);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new AesException(-40008);
        }


        if (!str2.equals(this.df)) {
            throw new AesException(-40005);
        }
        return str1;
    }


    public String EncryptMsg(String replyMsg, String timeStamp, String nonce) throws AesException {
        String str1 = encrypt(h(), replyMsg);


        if (timeStamp == "") {
            timeStamp = Long.toString(System.currentTimeMillis());
        }

        String str2 = SHA1.getSHA1(this.de, timeStamp, nonce, str1);


        return c.a(str1, str2, timeStamp, nonce);
    }


    public String DecryptMsg(String msgSignature, String timeStamp, String nonce, String postData) throws AesException {
        Object[] arrayOfObject = c.r(postData);


        String str = SHA1.getSHA1(this.de, timeStamp, nonce, arrayOfObject[1].toString());


        if (!str.equals(msgSignature)) {
            throw new AesException(-40001);
        }


        return q(arrayOfObject[1].toString());
    }


    public String VerifyURL(String msgSignature, String timeStamp, String nonce, String echoStr) throws AesException {
        String str = SHA1.getSHA1(this.de, timeStamp, nonce, echoStr);

        if (!str.equals(msgSignature)) {
            throw new AesException(-40001);
        }

        return q(echoStr);
    }
}
