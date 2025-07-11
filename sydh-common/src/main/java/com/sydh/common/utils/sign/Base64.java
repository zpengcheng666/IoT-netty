package com.sydh.common.utils.sign;


public final class Base64 {
    private static final int cz = 128;
    private static final int cA = 64;
    private static final int cB = 24;
    private static final int cC = 8;
    private static final int cD = 16;
    private static final int cE = 4;
    private static final int cF = -128;
    private static final char cG = '=';
    private static final byte[] cH = new byte[128];
    private static final char[] cI = new char[64];

    static {
        byte b1;
        for (b1 = 0; b1 < ''; b1++) {
            cH[b1] = -1;
        }
        for (b1 = 90; b1 >= 65; b1--) {
            cH[b1] = (byte) (b1 - 65);
        }
        for (b1 = 122; b1 >= 97; b1--) {
            cH[b1] = (byte) (b1 - 97 + 26);
        }

        for (b1 = 57; b1 >= 48; b1--) {
            cH[b1] = (byte) (b1 - 48 + 52);
        }

        cH[43] = 62;
        cH[47] = 63;

        for (b1 = 0; b1 <= 25; b1++) {
            cI[b1] = (char) (65 + b1);
        }
        byte b2;
        for (b1 = 26, b2 = 0; b1 <= 51; b1++, b2++) {
            cI[b1] = (char) (97 + b2);
        }

        for (b1 = 52, b2 = 0; b1 <= 61; b1++, b2++) {
            cI[b1] = (char) (48 + b2);
        }
        cI[62] = '+';
        cI[63] = '/';
    }


    private static boolean b(char paramChar) {
        return (paramChar == ' ' || paramChar == '\r' || paramChar == '\n' || paramChar == '\t');
    }


    private static boolean c(char paramChar) {
        return (paramChar == '=');
    }


    private static boolean d(char paramChar) {
        return (paramChar < '' && cH[paramChar] != -1);
    }


    public static String encode(byte[] binaryData) {
        if (binaryData == null) {
            return null;
        }

        int i = binaryData.length * 8;
        if (i == 0) {
            return "";
        }

        int j = i % 24;
        int k = i / 24;
        int m = (j != 0) ? (k + 1) : k;
        char[] arrayOfChar = null;

        arrayOfChar = new char[m * 4];

        byte b1 = 0, b2 = 0, b3 = 0, b4 = 0, b5 = 0;

        byte b6 = 0;
        byte b7 = 0;
        byte b8;
        for (b8 = 0; b8 < k; b8++) {

            b3 = binaryData[b7++];
            b4 = binaryData[b7++];
            b5 = binaryData[b7++];

            b2 = (byte) (b4 & 0xF);
            b1 = (byte) (b3 & 0x3);

            byte b9 = ((b3 & 0xFFFFFF80) == 0) ? (byte) (b3 >> 2) : (byte) (b3 >> 2 ^ 0xC0);
            byte b10 = ((b4 & 0xFFFFFF80) == 0) ? (byte) (b4 >> 4) : (byte) (b4 >> 4 ^ 0xF0);
            byte b11 = ((b5 & 0xFFFFFF80) == 0) ? (byte) (b5 >> 6) : (byte) (b5 >> 6 ^ 0xFC);

            arrayOfChar[b6++] = cI[b9];
            arrayOfChar[b6++] = cI[b10 | b1 << 4];
            arrayOfChar[b6++] = cI[b2 << 2 | b11];
            arrayOfChar[b6++] = cI[b5 & 0x3F];
        }


        if (j == 8) {

            b3 = binaryData[b7];
            b1 = (byte) (b3 & 0x3);
            b8 = ((b3 & 0xFFFFFF80) == 0) ? (byte) (b3 >> 2) : (byte) (b3 >> 2 ^ 0xC0);
            arrayOfChar[b6++] = cI[b8];
            arrayOfChar[b6++] = cI[b1 << 4];
            arrayOfChar[b6++] = '=';
            arrayOfChar[b6++] = '=';
        } else if (j == 16) {

            b3 = binaryData[b7];
            b4 = binaryData[b7 + 1];
            b2 = (byte) (b4 & 0xF);
            b1 = (byte) (b3 & 0x3);

            b8 = ((b3 & 0xFFFFFF80) == 0) ? (byte) (b3 >> 2) : (byte) (b3 >> 2 ^ 0xC0);
            byte b = ((b4 & 0xFFFFFF80) == 0) ? (byte) (b4 >> 4) : (byte) (b4 >> 4 ^ 0xF0);

            arrayOfChar[b6++] = cI[b8];
            arrayOfChar[b6++] = cI[b | b1 << 4];
            arrayOfChar[b6++] = cI[b2 << 2];
            arrayOfChar[b6++] = '=';
        }
        return new String(arrayOfChar);
    }


    public static byte[] decode(String encoded) {
        if (encoded == null) {
            return null;
        }

        char[] arrayOfChar = encoded.toCharArray();

        int i = a(arrayOfChar);

        if (i % 4 != 0) {
            return null;
        }

        int j = i / 4;

        if (j == 0) {
            return new byte[0];
        }

        byte[] arrayOfByte = null;
        byte b1 = 0, b2 = 0, b3 = 0, b4 = 0;
        char c1 = Character.MIN_VALUE, c2 = Character.MIN_VALUE, c3 = Character.MIN_VALUE, c4 = Character.MIN_VALUE;

        byte b5 = 0;
        byte b6 = 0;
        byte b7 = 0;
        arrayOfByte = new byte[j * 3];

        for (; b5 < j - 1; b5++) {


            if (!d(c1 = arrayOfChar[b7++]) || !d(c2 = arrayOfChar[b7++]) ||
                    !d(c3 = arrayOfChar[b7++]) || !d(c4 = arrayOfChar[b7++])) {
                return null;
            }

            b1 = cH[c1];
            b2 = cH[c2];
            b3 = cH[c3];
            b4 = cH[c4];

            arrayOfByte[b6++] = (byte) (b1 << 2 | b2 >> 4);
            arrayOfByte[b6++] = (byte) ((b2 & 0xF) << 4 | b3 >> 2 & 0xF);
            arrayOfByte[b6++] = (byte) (b3 << 6 | b4);
        }

        if (!d(c1 = arrayOfChar[b7++]) || !d(c2 = arrayOfChar[b7++])) {
            return null;
        }

        b1 = cH[c1];
        b2 = cH[c2];

        c3 = arrayOfChar[b7++];
        c4 = arrayOfChar[b7++];
        if (!d(c3) || !d(c4)) {

            if (c(c3) && c(c4)) {

                if ((b2 & 0xF) != 0) {
                    return null;
                }
                byte[] arrayOfByte1 = new byte[b5 * 3 + 1];
                System.arraycopy(arrayOfByte, 0, arrayOfByte1, 0, b5 * 3);
                arrayOfByte1[b6] = (byte) (b1 << 2 | b2 >> 4);
                return arrayOfByte1;
            }
            if (!c(c3) && c(c4)) {

                b3 = cH[c3];
                if ((b3 & 0x3) != 0) {
                    return null;
                }
                byte[] arrayOfByte1 = new byte[b5 * 3 + 2];
                System.arraycopy(arrayOfByte, 0, arrayOfByte1, 0, b5 * 3);
                arrayOfByte1[b6++] = (byte) (b1 << 2 | b2 >> 4);
                arrayOfByte1[b6] = (byte) ((b2 & 0xF) << 4 | b3 >> 2 & 0xF);
                return arrayOfByte1;
            }
            return null;
        }


        b3 = cH[c3];
        b4 = cH[c4];
        arrayOfByte[b6++] = (byte) (b1 << 2 | b2 >> 4);
        arrayOfByte[b6++] = (byte) ((b2 & 0xF) << 4 | b3 >> 2 & 0xF);
        arrayOfByte[b6++] = (byte) (b3 << 6 | b4);


        return arrayOfByte;
    }


    private static int a(char[] paramArrayOfchar) {
        if (paramArrayOfchar == null) {
            return 0;
        }


        byte b1 = 0;
        int i = paramArrayOfchar.length;
        for (byte b2 = 0; b2 < i; b2++) {

            if (!b(paramArrayOfchar[b2])) {
                paramArrayOfchar[b1++] = paramArrayOfchar[b2];
            }
        }
        return b1;
    }
}
