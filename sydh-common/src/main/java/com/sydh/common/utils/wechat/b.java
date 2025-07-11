package com.sydh.common.utils.wechat;

import java.nio.charset.Charset;
import java.util.Arrays;


class b {
    static Charset db = Charset.forName("utf-8");
    static int dh = 32;


    static byte[] c(int paramInt) {
        int i = dh - paramInt % dh;
        if (i == 0) {
            i = dh;
        }

        char c = d(i);
        String str = new String();
        for (byte b1 = 0; b1 < i; b1++) {
            str = str + c;
        }
        return str.getBytes(db);
    }


    static byte[] h(byte[] paramArrayOfbyte) {
        byte b1 = paramArrayOfbyte[paramArrayOfbyte.length - 1];
        if (b1 < 1 || b1 > 32) {
            b1 = 0;
        }
        return Arrays.copyOfRange(paramArrayOfbyte, 0, paramArrayOfbyte.length - b1);
    }


    static char d(int paramInt) {
        byte b1 = (byte) (paramInt & 0xFF);
        return (char) b1;
    }
}
