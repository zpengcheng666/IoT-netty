package com.sydh.common.utils;

import java.util.Arrays;


public class BitUtils {
    public static int getBitFlag(long num, int bit) {
        return (int) num >> bit & 0x1;
    }


    public static long updateBitValue(long num, int bit, boolean flagValue) {
        if (flagValue) {
            return num | (1 << bit);
        }

        return num ^ (getBitFlag(num, bit) << bit);
    }


    public static String toBinaryString(long num) {
        return Long.toBinaryString(num);
    }


    public static int deter(int num, int i) {
        return num >> i - 1 & 0x1;
    }

    public static String bin2hex(String input) {
        StringBuilder stringBuilder = new StringBuilder();
        int i = input.length();
        System.out.println("原数据长度：" + (i / 8) + "字节");

        for (byte b = 0; b < i / 4; b++) {

            String str1 = input.substring(b * 4, (b + 1) * 4);
            int j = Integer.parseInt(str1, 2);
            String str2 = Integer.toHexString(j).toUpperCase();
            stringBuilder.append(str2);
        }

        return stringBuilder.toString();
    }

    public static int bin2Dec(String binaryString) {
        int i = 0;
        for (byte b = 0; b < binaryString.length(); b++) {
            char c = binaryString.charAt(b);
            if (c > '2' || c < '0') {
                throw new NumberFormatException(String.valueOf(b));
            }
            i = i * 2 + binaryString.charAt(b) - 48;
        }
        return i;
    }

    public static int[] string2Ins(String input) {
        StringBuilder stringBuilder = new StringBuilder(input);
        int i = stringBuilder.length() % 8;
        if (i > 0) {
            for (byte b1 = 0; b1 < 8 - i; b1++) {
                stringBuilder.append("0");
            }
        }
        int[] arrayOfInt = new int[stringBuilder.length() / 8];


        for (byte b = 0; b < arrayOfInt.length; b++) {
            arrayOfInt[b] = Integer.parseInt(stringBuilder.substring(b * 8, b * 8 + 8), 2);
        }
        return arrayOfInt;
    }

    public static byte[] string2bytes(String input) {
        StringBuilder stringBuilder = new StringBuilder(input);
        int i = stringBuilder.length() % 8;
        if (i > 0) {
            for (byte b1 = 0; b1 < 8 - i; b1++) {
                stringBuilder.insert(0, "0");
            }
        }
        byte[] arrayOfByte = new byte[stringBuilder.length() / 8];


        for (byte b = 0; b < arrayOfByte.length; b++) {
            arrayOfByte[b] = (byte) Integer.parseInt(stringBuilder.substring(b * 8, b * 8 + 8), 2);
        }
        return arrayOfByte;
    }


    private static String a(byte[] paramArrayOfbyte, Object paramObject, Object... paramVarArgs) {
        if (paramArrayOfbyte != null && paramArrayOfbyte.length > 0 && paramObject != null) {
            try {
                byte[] arrayOfByte;
                int i = Integer.parseInt(paramObject.toString());
                if (paramVarArgs != null && paramVarArgs.length > 0) {
                    int j = Integer.parseInt(paramVarArgs[0].toString());
                    if (i >= j || j <= 0) {
                        arrayOfByte = Arrays.copyOfRange(paramArrayOfbyte, i, i + 1);
                    } else {
                        arrayOfByte = Arrays.copyOfRange(paramArrayOfbyte, i, j + 1);
                    }
                } else {
                    arrayOfByte = Arrays.copyOfRange(paramArrayOfbyte, i, i + 1);
                }
                if (arrayOfByte != null && arrayOfByte.length > 0) {
                    long l = 0L;
                    byte b = -1;
                    for (int j = arrayOfByte.length - 1; j >= 0; j--, b++) {
                        byte b1 = arrayOfByte[j];
                        if (b1 < 0) {
                            b1 += 256;
                        }
                        l += Math.round(b1 * Math.pow(16.0D, (2 * b + 2)));
                    }
                    return (new Long(l)).toString();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }


    private static byte[] a(Integer paramInteger, int... paramVarArgs) {
        return a(Integer.toHexString(paramInteger.intValue()), paramVarArgs);
    }


    private static byte[] a(String paramString, int... paramVarArgs) {
        paramString = paramString.toLowerCase();
        if (paramString.length() % 2 != 0) {
            paramString = "0" + paramString;
        }
        int i = paramString.length() / 2;
        if (i < 1) {
            i = 1;
        }
        int j = i;
        if (paramVarArgs != null && paramVarArgs.length > 0 && paramVarArgs[0] >= i) {
            j = paramVarArgs[0];
        }
        byte[] arrayOfByte = new byte[j];
        byte b1 = 0;
        for (byte b2 = 0; b2 < j; b2++) {
            if (b2 < j - i) {
                arrayOfByte[b2] = 0;
            } else {
                byte b = (byte) (Character.digit(paramString.charAt(b1), 16) & 0xFF);
                if (b1 + 1 < paramString.length()) {
                    byte b3 = (byte) (Character.digit(paramString.charAt(b1 + 1), 16) & 0xFF);
                    arrayOfByte[b2] = (byte) (b << 4 | b3);
                } else {
                    arrayOfByte[b2] = b;
                }
                b1 += 2;
            }
        }
        return arrayOfByte;
    }


    private static byte[] a(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
        if (paramArrayOfbyte1 == null) {
            return paramArrayOfbyte2;
        }
        if (paramArrayOfbyte2 == null) {
            return paramArrayOfbyte1;
        }
        return a(new byte[][]{paramArrayOfbyte1, paramArrayOfbyte2});
    }


    private static byte[] a(byte[]... paramVarArgs) {
        if (paramVarArgs != null && paramVarArgs.length > 0) {
            int i = 0;
            for (byte b1 = 0; b1 < paramVarArgs.length; b1++) {
                i += (paramVarArgs[b1]).length;
            }
            byte[] arrayOfByte = new byte[i];
            byte b2 = 0;
            for (byte b3 = 0; b3 < paramVarArgs.length; b3++) {
                byte[] arrayOfByte1 = paramVarArgs[b3];
                for (byte b = 0; b < arrayOfByte1.length; b++) {
                    arrayOfByte[b2++] = arrayOfByte1[b];
                }
            }
            return arrayOfByte;
        }
        return null;
    }

    public static byte[] hexStringToByteArray(String s) {
        int i = s.length();
        byte[] arrayOfByte = new byte[i / 2];
        for (byte b = 0; b < i; b += 2) {
            arrayOfByte[b / 2] =
                    (byte) ((Character.digit(s.charAt(b), 16) << 4) + Character.digit(s.charAt(b + 1), 16));
        }
        return arrayOfByte;
    }


    public static void main(String[] args) {
        String str = bin2hex("1111111000000000");
        int i = bin2Dec("1111111000000000");
        byte[] arrayOfByte = string2bytes("111111000000000");
        System.out.println(str);
        System.out.println(i);
    }
}
