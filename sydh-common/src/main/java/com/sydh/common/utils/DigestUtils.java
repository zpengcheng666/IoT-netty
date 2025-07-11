package com.sydh.common.utils;

import com.sydh.common.utils.uuid.IdUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;

import org.apache.commons.lang3.Validate;


public class DigestUtils {
    private static SecureRandom au = new SecureRandom();
    private static IdUtils av = new IdUtils(0L, 0L);

    public static String getId() {
        return String.valueOf(Math.abs(au.nextLong()));
    }

    public static String nextId() {
        return String.valueOf(av.nextId());
    }


    public static byte[] genSalt(int numBytes) {
        Validate.isTrue((numBytes > 0), "numBytes argument must be a positive integer (1 or larger)", numBytes);
        byte[] arrayOfByte = new byte[numBytes];
        au.nextBytes(arrayOfByte);
        return arrayOfByte;
    }

    public static byte[] digest(byte[] input, String algorithm, byte[] salt, int iterations) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            if (salt != null) {
                messageDigest.update(salt);
            }

            byte[] arrayOfByte = messageDigest.digest(input);

            for (byte b = 1; b < iterations; b++) {
                messageDigest.reset();
                arrayOfByte = messageDigest.digest(arrayOfByte);
            }

            return arrayOfByte;
        } catch (GeneralSecurityException generalSecurityException) {
            throw ExceptionUtils.unchecked(generalSecurityException);
        }
    }

    public static byte[] digest(InputStream input, String algorithm) throws IOException {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            char c = ' ';
            byte[] arrayOfByte = new byte[c];
            int i;
            for (i = input.read(arrayOfByte, 0, c); i > -1; i = input.read(arrayOfByte, 0, c)) {
                messageDigest.update(arrayOfByte, 0, i);
            }

            return messageDigest.digest();
        } catch (GeneralSecurityException generalSecurityException) {
            throw ExceptionUtils.unchecked(generalSecurityException);
        }
    }

    public static void main(String[] args) {
        for (byte b = 0; b < 10; b++) {
            System.out.println(nextId());
        }
    }
}
