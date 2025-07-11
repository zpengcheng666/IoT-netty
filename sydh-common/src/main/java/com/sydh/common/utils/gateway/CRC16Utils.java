package com.sydh.common.utils.gateway;

import com.sydh.common.utils.CaculateUtils;
import com.sydh.common.utils.gateway.protocol.ByteUtils;
import org.apache.commons.lang3.ArrayUtils;


public class CRC16Utils {
    private static int aW = 255;

    private static int aX = 1;


    private static final int aY = 4;


    private static final int aZ = 255;


    public static String getCRC(byte[] bytes) {
        return getCRC(bytes, true);
    }


    public static String getCRC(byte[] bytes, boolean lb) {
        int i = 65535;
        char c = 'ꀁ';


        for (byte b = 0; b < bytes.length; b++) {
            i ^= bytes[b] & 0xFF;
            for (byte b1 = 0; b1 < 8; b1++) {
                if ((i & 0x1) != 0) {
                    i >>= 1;
                    i ^= c;
                } else {
                    i >>= 1;
                }
            }
        }


        String str = Integer.toHexString(i).toUpperCase();
        if (str.length() != 4) {
            StringBuffer stringBuffer = new StringBuffer("0000");
            str = stringBuffer.replace(4 - str.length(), 4, str).toString();
        }

        if (lb) {
            str = str.substring(2, 4) + str.substring(0, 2);
        }

        return str;
    }


    public static byte[] getCrc16Byte(byte[] bytes) {
        int i = 65535;

        char c = 'ꀁ';
        for (byte b : bytes) {
            i ^= b & aW;
            for (byte b1 = 0; b1 < 8; b1++) {
                if ((i & aX) != 0) {
                    i >>= 1;
                    i ^= c;
                } else {
                    i >>= 1;
                }
            }
        }

        return new byte[]{(byte) (i & 0xFF), (byte) (i >> 8 & 0xFF)};
    }

    public static byte[] AddCRC(byte[] source) {
        byte[] arrayOfByte1 = new byte[source.length + 2];
        byte[] arrayOfByte2 = getCrc16Byte(source);
        System.arraycopy(source, 0, arrayOfByte1, 0, source.length);
        System.arraycopy(arrayOfByte2, 0, arrayOfByte1, arrayOfByte1.length - 2, 2);
        return arrayOfByte1;
    }

    public static byte[] AddPakCRC(byte[] source) {
        byte[] arrayOfByte1 = ArrayUtils.subarray(source, 11, source.length);
        byte[] arrayOfByte2 = new byte[source.length + 2];
        byte[] arrayOfByte3 = getCrc16Byte(arrayOfByte1);
        System.arraycopy(source, 0, arrayOfByte2, 0, source.length);
        System.arraycopy(arrayOfByte3, 0, arrayOfByte2, arrayOfByte2.length - 2, 2);
        return arrayOfByte2;
    }

    public static byte[] CRC(byte[] source) {
        source[2] = (byte) (source[2] * 2);
        byte[] arrayOfByte1 = new byte[source.length + 2];
        byte[] arrayOfByte2 = getCrc16Byte(source);
        System.arraycopy(source, 0, arrayOfByte1, 0, source.length);
        System.arraycopy(arrayOfByte2, 0, arrayOfByte1, arrayOfByte1.length - 2, 2);
        return arrayOfByte1;
    }

    public static byte CRC8(byte[] buffer) {
        int i = 255;
        for (byte b = 0; b < buffer.length; b++) {
            i ^= buffer[b] & 0xFF;
            for (byte b1 = 0; b1 < 8; b1++) {
                if ((i & 0x1) != 0) {
                    i >>= 1;
                    i ^= 0xB8;
                } else {
                    i >>= 1;
                }
            }
        }
        return (byte) i;
    }


    public static void main(String[] args) throws Exception {
        String str1 = "0103028000";
        byte[] arrayOfByte1 = ByteUtils.hexToByte(str1);
        String str2 = getCRC(arrayOfByte1);
        System.out.println(str2);
        String str3 = "680868333701120008C100";
        byte[] arrayOfByte2 = ByteUtils.hexToByte(str3);
        byte b = CRC8(arrayOfByte2);
        System.out.println(b);
        float f = CaculateUtils.toFloat32_CDAB(arrayOfByte1).floatValue();
        System.out.println(f);
    }
}
