package com.sydh.common.utils.gateway.protocol;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;


public class ByteUtils {
    /**
     * 构建 Write10Build 实例，支持多种数据类型的打包
     *
     * @param args 可变参数，支持 Integer、Long、Float、Double、Short、Byte、String 等类型
     * @return Write10Build 实例
     */
    public static Write10Build write10Build(Object... args) {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("参数不能为空");
        }

        int fieldCount = 0;
        List<byte[]> byteList = new ArrayList<>();
        int totalLength = 0;

        for (Object obj : args) {
            if (obj == null) {
                continue;
            }

            if (obj instanceof Integer) {
                byte[] bytes = getBytes((Integer) obj);
                fieldCount += 1;
                byteList.add(bytes);
                totalLength += bytes.length;
            } else if (obj instanceof Long) {
                byte[] bytes = getBytes((Long) obj);
                fieldCount += 1;
                byteList.add(bytes);
                totalLength += bytes.length;
            } else if (obj instanceof Float) {
                byte[] bytes = getBytes((Float) obj);
                fieldCount += 1;
                byteList.add(bytes);
                totalLength += bytes.length;
            } else if (obj instanceof Double) {
                byte[] bytes = getBytes((Double) obj);
                fieldCount += 1;
                byteList.add(bytes);
                totalLength += bytes.length;
            } else if (obj instanceof Short) {
                byte[] bytes = getBytesOfReverse((Short) obj);
                fieldCount += 1;
                byteList.add(bytes);
                totalLength += bytes.length;
            } else if (obj instanceof Byte) {
                byte[] bytes = new byte[]{(byte) 0, (Byte) obj};
                fieldCount += 1;
                byteList.add(bytes);
                totalLength += bytes.length;
            } else if (obj instanceof String) {
                byte[] strBytes = ((String) obj).getBytes(StandardCharsets.UTF_8);
                int padding = strBytes.length % 2 == 0 ? 0 : 1;
                byte[] paddedBytes = new byte[strBytes.length + padding];
                System.arraycopy(strBytes, 0, paddedBytes, 0, strBytes.length);
                fieldCount += 1;
                byteList.add(paddedBytes);
                totalLength += paddedBytes.length;
            } else {
                throw new IllegalArgumentException("不支持的数据类型: " + obj.getClass().getName());
            }
        }

        byte[] result = new byte[totalLength];
        int offset = 0;

        for (byte[] bytes : byteList) {
            System.arraycopy(bytes, 0, result, offset, bytes.length);
            offset += bytes.length;
        }

        return new Write10Build(fieldCount, result);
    }


    public static class Write10Build {
        public int num;
        public byte[] message;

        public Write10Build(int num, byte[] message) {
            this.num = num;
            this.message = message;
        }
    }

    public static byte[] create(byte b) {
        return new byte[]{b};
    }


    public static byte[] getBytes(byte data) {
        return new byte[]{data};
    }


    public static byte[] getBytes(short data) {
        byte[] arrayOfByte = new byte[2];
        arrayOfByte[0] = (byte) (data & 0xFF);
        arrayOfByte[1] = (byte) ((data & 0xFF00) >> 8);
        return arrayOfByte;
    }


    public static byte[] getBytesOfReverse(short data) {
        byte[] arrayOfByte = new byte[2];
        arrayOfByte[1] = (byte) (data & 0xFF);
        arrayOfByte[0] = (byte) ((data & 0xFF00) >> 8);
        return arrayOfByte;
    }


    public static short bytesToShort(byte[] bytes, int offset) {
        return (short) (0xFF & bytes[0 + offset] | 0xFF00 & bytes[1 + offset] << 8);
    }


    public static short bytesToShortOfReverse(byte[] bytes) {
        return bytesToShortOfReverse(bytes, 0);
    }


    public static short bytesToShortOfReverse(byte[] bytes, int offset) {
        return (short) (0xFF & bytes[1 + offset] | 0xFF00 & bytes[0 + offset] << 8);
    }


    public static byte[] getBytes(int data) {
        byte[] arrayOfByte = new byte[4];
        arrayOfByte[0] = (byte) (data & 0xFF);
        arrayOfByte[1] = (byte) (data >> 8 & 0xFF);
        arrayOfByte[2] = (byte) (data >> 16 & 0xFF);
        arrayOfByte[3] = (byte) (data >> 24 & 0xFF);
        return arrayOfByte;
    }


    public static byte[] getBytesOfReverse(int data) {
        byte[] arrayOfByte = new byte[4];
        arrayOfByte[0] = (byte) (data >> 24 & 0xFF);
        arrayOfByte[1] = (byte) (data >> 16 & 0xFF);
        arrayOfByte[2] = (byte) (data >> 8 & 0xFF);
        arrayOfByte[3] = (byte) (data & 0xFF);
        return arrayOfByte;
    }


    public static byte[] getBytes(long data) {
        byte[] arrayOfByte = new byte[8];
        arrayOfByte[0] = (byte) (int) (data & 0xFFL);
        arrayOfByte[1] = (byte) (int) (data >> 8L & 0xFFL);
        arrayOfByte[2] = (byte) (int) (data >> 16L & 0xFFL);
        arrayOfByte[3] = (byte) (int) (data >> 24L & 0xFFL);
        arrayOfByte[4] = (byte) (int) (data >> 32L & 0xFFL);
        arrayOfByte[5] = (byte) (int) (data >> 40L & 0xFFL);
        arrayOfByte[6] = (byte) (int) (data >> 48L & 0xFFL);
        arrayOfByte[7] = (byte) (int) (data >> 56L & 0xFFL);
        return arrayOfByte;
    }


    public static byte[] getBytesOfReverse(long data) {
        byte[] arrayOfByte = new byte[8];
        arrayOfByte[7] = (byte) (int) (data & 0xFFL);
        arrayOfByte[6] = (byte) (int) (data >> 8L & 0xFFL);
        arrayOfByte[5] = (byte) (int) (data >> 16L & 0xFFL);
        arrayOfByte[4] = (byte) (int) (data >> 24L & 0xFFL);
        arrayOfByte[3] = (byte) (int) (data >> 32L & 0xFFL);
        arrayOfByte[2] = (byte) (int) (data >> 40L & 0xFFL);
        arrayOfByte[1] = (byte) (int) (data >> 48L & 0xFFL);
        arrayOfByte[0] = (byte) (int) (data >> 56L & 0xFFL);
        return arrayOfByte;
    }


    public static byte[] getBytes(float data) {
        int i = Float.floatToIntBits(data);
        return getBytes(i);
    }


    public static byte[] getBytesOfReverse(float data) {
        int i = Float.floatToIntBits(data);
        return getBytesOfReverse(i);
    }


    public static byte[] getBytes(double data) {
        long l = Double.doubleToLongBits(data);
        return getBytes(l);
    }


    public static byte[] getBytesOfReverse(double data) {
        long l = Double.doubleToLongBits(data);
        return getBytesOfReverse(l);
    }


    public static byte[] getBytes(String data) {
        return data.getBytes(StandardCharsets.UTF_8);
    }


    public static byte[] getBytes(String data, String charsetName) {
        Charset charset = Charset.forName(charsetName);
        return data.getBytes(charset);
    }


    public static byte[] hexToByte(String hexStr) {
        if (hexStr == null || hexStr.trim().isEmpty()) {
            return new byte[0];
        }

        int length = hexStr.length();
        if (length % 2 != 0) {
            hexStr = "0" + hexStr;
            length++;
        }

        char[] hexChars = hexStr.toCharArray();
        int byteLength = length / 2;
        byte[] result = new byte[byteLength];

        for (int i = 0; i < byteLength; i++) {
            int pos = i * 2;
            char high = hexChars[pos];
            char low = hexChars[pos + 1];

            // 手动验证字符是否在合法范围内
            if (!isValidHexChar(high) || !isValidHexChar(low)) {
                throw new IllegalArgumentException("Invalid hexadecimal character in input string.");
            }

            int highVal = Character.digit(high, 16);
            int lowVal = Character.digit(low, 16);

            result[i] = (byte) ((highVal << 4) | lowVal);
        }

        return result;
    }

    private static boolean isValidHexChar(char c) {
        return (c >= '0' && c <= '9') ||
                (c >= 'a' && c <= 'f') ||
                (c >= 'A' && c <= 'F');
    }


    public static byte getByte(byte[] src, int offset) {
        return src[offset];
    }


    public static final String bytesToHex(byte[] bArray) {
        StringBuffer stringBuffer = new StringBuffer(bArray.length);

        for (byte b = 0; b < bArray.length; b++) {
            String str = Integer.toHexString(0xFF & bArray[b]);
            if (str.length() < 2) stringBuffer.append(0);
            stringBuffer.append(str.toUpperCase());
        }
        return stringBuffer.toString();
    }


    public static final String bytesToHexByFormat(byte[] bArray) {
        StringBuffer stringBuffer = new StringBuffer(bArray.length);

        for (byte b = 0; b < bArray.length; b++) {
            String str = Integer.toHexString(0xFF & bArray[b]);
            if (str.length() < 2) stringBuffer.append(0);
            stringBuffer.append(str.toUpperCase()).append(' ');
        }
        return stringBuffer.toString();
    }


    public static final String bytesToHex(byte[] src, int offset, int length) {
        byte[] arrayOfByte = ArrayUtils.subarray(src, offset, offset + length);
        return bytesToHex(arrayOfByte);
    }

    public static final String byteToHex(byte value) {
        String str = Integer.toHexString(0xFF & value);
        if (str.length() == 1) return "0" + str;
        return str;
    }

    public static final String shortToHex(short value) {
        String str = Integer.toHexString(value);
        switch (str.length()) {
            case 1:
                return "000" + str;
            case 2:
                return "00" + str;
            case 3:
                return "0" + str;
        }
        return str;
    }


    public static final String intToHex(int value) {
        StringBuilder stringBuilder = new StringBuilder(Integer.toHexString(value));
        String str = stringBuilder.toString().replace("f", "");
        if (str.length() < 4) {
            for (byte b = 0; b < 4 - str.length(); b++) {
                stringBuilder.insert(0, "0");
            }
            return stringBuilder.toString().replace("f", "");
        }
        return stringBuilder.toString().replace("f", "");
    }

    public static final String hexTo8Bit(int value, int index) {
        String str = Integer.toBinaryString(value);
        StringBuilder stringBuilder = new StringBuilder(str);
        if (str.length() < index) {
            for (byte b = 0; b < index - str.length(); b++) {
                stringBuilder.insert(0, "0");
            }
        }
        return stringBuilder.toString();
    }


    public static String bcdToStr(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer(bytes.length * 2);

        for (byte b = 0; b < bytes.length; b++) {
            stringBuffer.append((byte) ((bytes[b] & 0xF0) >>> 4));
            stringBuffer.append((byte) (bytes[b] & 0xF));
        }

        return stringBuffer.toString();
    }


    public static String bcdToStr(byte[] src, int offset, int length) {
        byte[] arrayOfByte = ArrayUtils.subarray(src, offset, offset + length);
        return bcdToStr(arrayOfByte);
    }

    public static byte[] str2Bcd(String asc) {
        int i = asc.length();
        int j = i % 2;

        if (j != 0) {
            asc = "0" + asc;
            i = asc.length();
        }


        if (i >= 2) {
            i /= 2;
        }

        byte[] arrayOfByte2 = new byte[i];
        byte[] arrayOfByte1 = asc.getBytes();


        for (byte b = 0; b < asc.length() / 2; b++) {
            int k, m;
            if (arrayOfByte1[2 * b] >= 48 && arrayOfByte1[2 * b] <= 57) {
                k = arrayOfByte1[2 * b] - 48;
            } else if (arrayOfByte1[2 * b] >= 97 && arrayOfByte1[2 * b] <= 122) {
                k = arrayOfByte1[2 * b] - 97 + 10;
            } else {
                k = arrayOfByte1[2 * b] - 65 + 10;
            }

            if (arrayOfByte1[2 * b + 1] >= 48 && arrayOfByte1[2 * b + 1] <= 57) {
                m = arrayOfByte1[2 * b + 1] - 48;
            } else if (arrayOfByte1[2 * b + 1] >= 97 && arrayOfByte1[2 * b + 1] <= 122) {
                m = arrayOfByte1[2 * b + 1] - 97 + 10;
            } else {
                m = arrayOfByte1[2 * b + 1] - 65 + 10;
            }

            int n = (k << 4) + m;
            byte b1 = (byte) n;
            arrayOfByte2[b] = b1;
        }
        return arrayOfByte2;
    }

    private static byte a(char paramChar) {
        return (byte) paramChar;
    }


    public static int bytesToUShort(byte[] bytes, int offset) {
        return 0xFF & bytes[0 + offset] | 0xFF00 & bytes[1 + offset] << 8;
    }


    public static int bytesToUShortOfReverse(byte[] bytes) {
        return bytesToShortOfReverse(bytes, 0);
    }


    public static int bytesToUShortOfReverse(byte[] bytes, int offset) {
        return 0xFF & bytes[1 + offset] | 0xFF00 & bytes[0 + offset] << 8;
    }


    public static int bytesToInt(byte[] src, int offset) {
        return src[offset] & 0xFF | (src[offset + 1] & 0xFF) << 8 | (src[offset + 2] & 0xFF) << 16 | (src[offset + 3] & 0xFF) << 24;
    }


    public static int bytesToInt(byte[] src) {
        return bytesToInt(src, 0);
    }


    public static int bytesToIntOfReverse(byte[] bytes, int offset) {
        return 0xFF & bytes[3 + offset] | 0xFF00 & bytes[2 + offset] << 8 | 0xFF0000 & bytes[1 + offset] << 16 | 0xFF000000 & bytes[0 + offset] << 24;
    }


    public static int bytesToIntOfReverse(byte[] bytes) {
        return bytesToIntOfReverse(bytes, 0);
    }


    public static long bytesToUInt(byte[] bytes, int offset) {
        int i = bytesToInt(bytes, offset);
        if (i >= 0) return i;
        return 4294967296L + i;
    }


    public static long bytesToUInt(byte[] bytes) {
        return bytesToUInt(bytes, 0);
    }


    public static long bytesToUIntOfReverse(byte[] bytes, int offset) {
        int i = bytesToIntOfReverse(bytes, offset);
        if (i >= 0) return i;
        return 4294967296L + i;
    }


    public static long bytesToUIntOfReverse(byte[] bytes) {
        return bytesToUIntOfReverse(bytes, 0);
    }


    public static float bytesToFloat(byte[] bytes) {
        return Float.intBitsToFloat(bytesToInt(bytes, 0));
    }


    public static float bytesToFloat(byte[] bytes, int offset) {
        return Float.intBitsToFloat(bytesToInt(bytes, offset));
    }


    public static float bytesToFloatOfReverse(byte[] bytes) {
        return bytesToFloatOfReverse(bytes, 0);
    }


    public static float bytesToFloatOfReverse(byte[] bytes, int offset) {
        return Float.intBitsToFloat(bytesToIntOfReverse(bytes, offset));
    }


    public static double bytesToDouble(byte[] src) {
        return Double.longBitsToDouble(bytesToLong(src));
    }


    public static double bytesToDouble(byte[] src, int offset) {
        return Double.longBitsToDouble(bytesToLong(src, offset));
    }


    public static double bytesToDoubleOfReverse(byte[] src) {
        return bytesToDoubleOfReverse(src, 0);
    }


    public static double bytesToDoubleOfReverse(byte[] src, int offset) {
        return Double.longBitsToDouble(bytesToLongOfReverse(src, offset));
    }


    public static String bytesToString(byte[] src, Charset charset) {
        int i = Arrays.binarySearch(src, (byte) 0);
        return new String(Arrays.copyOf(src, i), charset);
    }


    public static String bytesToString(byte[] src) {
        return new String(a(src));
    }


    public static String bytesToString(byte[] src, int startIndex, int endIndex) {
        return new String(a(subBytes(src, startIndex, endIndex)));
    }


    public static String bytesToString(byte[] src, int startIndex, int endIndex, Charset charset) {
        return new String(a(subBytes(src, startIndex, endIndex)), charset);
    }


    public static void bytesReverse(byte[] reverse) {
        if (reverse != null) {
            byte b = 0;
            for (byte b1 = 0; b1 < reverse.length / 2; b1++) {
                b = reverse[b1];
                reverse[b1] = reverse[reverse.length - 1 - b1];
                reverse[reverse.length - 1 - b1] = b;
            }
        }
    }


    private static byte[] a(byte[] paramArrayOfbyte) {
        byte b1 = 0;
        for (byte b2 = 0; b2 < paramArrayOfbyte.length &&
                paramArrayOfbyte[b2] != 0; b2++) {


            b1++;
        }

        return subBytes(paramArrayOfbyte, 0, b1);
    }


    public static long bytesToLong(byte[] bytes) {
        return bytesToLong(bytes, 0);
    }


    public static long bytesToLong(byte[] bytes, int offset) {
        return 0xFFL & bytes[0 + offset] | 0xFF00L & bytes[1 + offset] << 8L | 0xFF0000L & bytes[2 + offset] << 16L | 0xFF000000L & bytes[3 + offset] << 24L | 0xFF00000000L & bytes[4 + offset] << 32L | 0xFF0000000000L & bytes[5 + offset] << 40L | 0xFF000000000000L & bytes[6 + offset] << 48L | 0xFF00000000000000L & bytes[7 + offset] << 56L;
    }


    public static long bytesToLongOfReverse(byte[] bytes) {
        return bytesToLongOfReverse(bytes, 0);
    }


    public static long bytesToLongOfReverse(byte[] bytes, int offset) {
        return 0xFFL & bytes[7 + offset] | 0xFF00L & bytes[6 + offset] << 8L | 0xFF0000L & bytes[5 + offset] << 16L | 0xFF000000L & bytes[4 + offset] << 24L | 0xFF00000000L & bytes[3 + offset] << 32L | 0xFF0000000000L & bytes[2 + offset] << 40L | 0xFF000000000000L & bytes[1 + offset] << 48L | 0xFF00000000000000L & bytes[0 + offset] << 56L;
    }


    public static byte[] subBytes(byte[] bytes, int beginIndex, int endIndex) {
        byte[] arrayOfByte = new byte[endIndex - beginIndex];
        System.arraycopy(bytes, beginIndex, arrayOfByte, 0, arrayOfByte.length);
        return arrayOfByte;
    }


    public static byte[] subBytes(byte[] bytes, int beginIndex) {
        return subBytes(bytes, beginIndex, bytes.length);
    }


    public static String subBytesToString(byte[] bytes, int beginIndex, int endIndex) {
        byte[] arrayOfByte = subBytes(bytes, beginIndex, endIndex);
        return bytesToString(arrayOfByte);
    }


    public static byte[] addBytes(byte[] sourceBytes, byte[] targetBytes, int beginIndex) {
        if (targetBytes == null) {
            return sourceBytes;
        }
        int i = targetBytes.length;
        if (sourceBytes == null) {
            beginIndex = 0;
            sourceBytes = new byte[i];
        }
        int j = sourceBytes.length;
        if (j - beginIndex < i) {
            return sourceBytes;
        }
        for (byte b = 0; b < i; b++) {
            sourceBytes[beginIndex + b] = targetBytes[b];
        }

        return sourceBytes;
    }


    public static byte[] UUID2Byte(UUID uuid) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(16);
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.writeLong(uuid.getMostSignificantBits());
            dataOutputStream.writeLong(uuid.getLeastSignificantBits());
            byteArrayOutputStream.close();
            dataOutputStream.close();
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }

        byte[] arrayOfByte = byteArrayOutputStream.toByteArray();

        byte b = arrayOfByte[0];
        arrayOfByte[0] = arrayOfByte[3];
        arrayOfByte[3] = b;
        b = arrayOfByte[1];
        arrayOfByte[1] = arrayOfByte[2];
        arrayOfByte[2] = b;

        b = arrayOfByte[4];
        arrayOfByte[4] = arrayOfByte[5];
        arrayOfByte[5] = b;

        b = arrayOfByte[6];
        arrayOfByte[6] = arrayOfByte[7];
        arrayOfByte[7] = b;

        return arrayOfByte;
    }


    public static byte bitAtByte(byte value, int index) {
        return (byte) (((value & 1 << index) > 0) ? 1 : 0);
    }


    public static byte[] boolArrayToByte(boolean[] array) {
        if (array == null) return null;

        int i = (array.length % 8 == 0) ? (array.length / 8) : (array.length / 8 + 1);
        byte[] arrayOfByte = new byte[i];

        for (byte b = 0; b < array.length; b++) {
            if (array[b]) {
                arrayOfByte[b / 8] = (byte) (arrayOfByte[b / 8] + (1 << b % 8));
            }
        }

        return arrayOfByte;
    }

    public static Integer cutMessageHexTo(byte[] source, int startIndex, int endIndex) {
        byte[] arrayOfByte = ArrayUtils.subarray(source, startIndex, endIndex);
        String str = bytesToHexString(arrayOfByte);
        return Integer.valueOf(Integer.parseInt(str, 16));
    }


    public static String bytesToHexString(byte[] bArray) {
        StringBuilder stringBuilder = new StringBuilder(bArray.length);
        for (byte b = 0; b < bArray.length; b++) {
            String str = Integer.toHexString(0xFF & bArray[b]);
            if (str.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(str.toUpperCase());
        }
        return stringBuilder.toString();
    }
}
