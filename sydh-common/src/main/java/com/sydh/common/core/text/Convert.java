package com.sydh.common.core.text;

import com.sydh.common.utils.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;


public class Convert {
    public static String toStr(Object value, String defaultValue) {
        if (null == value) {
            return defaultValue;
        }
        if (value instanceof String) {
            return (String) value;
        }
        return value.toString();
    }


    public static String toStr(Object value) {
        return toStr(value, null);
    }


    public static Character toChar(Object value, Character defaultValue) {
        if (null == value) {
            return defaultValue;
        }
        if (value instanceof Character) {
            return (Character) value;
        }

        String str = toStr(value, null);
        return Character.valueOf(StringUtils.isEmpty(str) ? defaultValue.charValue() : str.charAt(0));
    }


    public static Character toChar(Object value) {
        return toChar(value, null);
    }


    public static Byte toByte(Object value, Byte defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Byte) {
            return (Byte) value;
        }
        if (value instanceof Number) {
            return Byte.valueOf(((Number) value).byteValue());
        }
        String str = toStr(value, null);
        if (StringUtils.isEmpty(str)) {
            return defaultValue;
        }

        try {
            return Byte.valueOf(Byte.parseByte(str));
        } catch (Exception exception) {

            return defaultValue;
        }
    }


    public static Byte toByte(Object value) {
        return toByte(value, null);
    }


    public static Short toShort(Object value, Short defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Short) {
            return (Short) value;
        }
        if (value instanceof Number) {
            return Short.valueOf(((Number) value).shortValue());
        }
        String str = toStr(value, null);
        if (StringUtils.isEmpty(str)) {
            return defaultValue;
        }

        try {
            return Short.valueOf(Short.parseShort(str.trim()));
        } catch (Exception exception) {

            return defaultValue;
        }
    }


    public static Short toShort(Object value) {
        return toShort(value, null);
    }


    public static Number toNumber(Object value, Number defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return (Number) value;
        }
        String str = toStr(value, null);
        if (StringUtils.isEmpty(str)) {
            return defaultValue;
        }

        try {
            return NumberFormat.getInstance().parse(str);
        } catch (Exception exception) {

            return defaultValue;
        }
    }


    public static Number toNumber(Object value) {
        return toNumber(value, null);
    }


    public static Integer toInt(Object value, Integer defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return Integer.valueOf(((Number) value).intValue());
        }
        String str = toStr(value, null);
        if (StringUtils.isEmpty(str)) {
            return defaultValue;
        }

        try {
            return Integer.valueOf(Integer.parseInt(str.trim()));
        } catch (Exception exception) {

            return defaultValue;
        }
    }


    public static Integer toInt(Object value) {
        return toInt(value, null);
    }


    public static Integer[] toIntArray(String str) {
        return toIntArray(",", str);
    }


    public static Long[] toLongArray(String str) {
        return toLongArray(",", str);
    }


    public static Integer[] toIntArray(String split, String str) {
        if (StringUtils.isEmpty(str)) {
            return new Integer[0];
        }
        String[] arrayOfString = str.split(split);
        Integer[] arrayOfInteger = new Integer[arrayOfString.length];
        for (byte b = 0; b < arrayOfString.length; b++) {

            Integer integer = toInt(arrayOfString[b], Integer.valueOf(0));
            arrayOfInteger[b] = integer;
        }
        return arrayOfInteger;
    }


    public static Long[] toLongArray(String split, String str) {
        if (StringUtils.isEmpty(str)) {
            return new Long[0];
        }
        String[] arrayOfString = str.split(split);
        Long[] arrayOfLong = new Long[arrayOfString.length];
        for (byte b = 0; b < arrayOfString.length; b++) {

            Long long_ = toLong(arrayOfString[b], null);
            arrayOfLong[b] = long_;
        }
        return arrayOfLong;
    }


    public static String[] toStrArray(String str) {
        return toStrArray(",", str);
    }


    public static String[] toStrArray(String split, String str) {
        return str.split(split);
    }


    public static Long toLong(Object value, Long defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Number) {
            return Long.valueOf(((Number) value).longValue());
        }
        String str = toStr(value, null);
        if (StringUtils.isEmpty(str)) {
            return defaultValue;
        }


        try {
            return Long.valueOf((new BigDecimal(str.trim())).longValue());
        } catch (Exception exception) {

            return defaultValue;
        }
    }


    public static Long toLong(Object value) {
        return toLong(value, null);
    }


    public static Double toDouble(Object value, Double defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Double) {
            return (Double) value;
        }
        if (value instanceof Number) {
            return Double.valueOf(((Number) value).doubleValue());
        }
        String str = toStr(value, null);
        if (StringUtils.isEmpty(str)) {
            return defaultValue;
        }


        try {
            return Double.valueOf((new BigDecimal(str.trim())).doubleValue());
        } catch (Exception exception) {

            return defaultValue;
        }
    }


    public static Double toDouble(Object value) {
        return toDouble(value, null);
    }


    public static Float toFloat(Object value, Float defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Float) {
            return (Float) value;
        }
        if (value instanceof Number) {
            return Float.valueOf(((Number) value).floatValue());
        }
        String str = toStr(value, null);
        if (StringUtils.isEmpty(str)) {
            return defaultValue;
        }

        try {
            return Float.valueOf(Float.parseFloat(str.trim()));
        } catch (Exception exception) {

            return defaultValue;
        }
    }


    public static Float toFloat(Object value) {
        return toFloat(value, null);
    }


    public static Boolean toBool(Object value, Boolean defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        String str = toStr(value, null);
        if (StringUtils.isEmpty(str)) {
            return defaultValue;
        }
        str = str.trim().toLowerCase();
        switch (str) {

            case "true":
            case "yes":
            case "ok":
            case "1":
                return Boolean.valueOf(true);
            case "false":
            case "no":
            case "0":
                return Boolean.valueOf(false);
        }
        return defaultValue;
    }


    public static Boolean toBool(Object value) {
        return toBool(value, null);
    }


    public static <E extends Enum<E>> E toEnum(Class<E> clazz, Object value, E defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (clazz.isAssignableFrom(value.getClass())) {

            return (E) value;
        }

        String str = toStr(value, null);
        if (StringUtils.isEmpty(str)) {
            return defaultValue;
        }

        try {
            return Enum.valueOf(clazz, str);
        } catch (Exception exception) {

            return defaultValue;
        }
    }


    public static <E extends Enum<E>> E toEnum(Class<E> clazz, Object value) {
        return toEnum(clazz, value, null);
    }


    public static BigInteger toBigInteger(Object value, BigInteger defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof BigInteger) {
            return (BigInteger) value;
        }
        if (value instanceof Long) {
            return BigInteger.valueOf(((Long) value).longValue());
        }
        String str = toStr(value, null);
        if (StringUtils.isEmpty(str)) {
            return defaultValue;
        }

        try {
            return new BigInteger(str);
        } catch (Exception exception) {

            return defaultValue;
        }
    }


    public static BigInteger toBigInteger(Object value) {
        return toBigInteger(value, null);
    }


    public static BigDecimal toBigDecimal(Object value, BigDecimal defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Long) {
            return new BigDecimal(((Long) value).longValue());
        }
        if (value instanceof Double) {
            return BigDecimal.valueOf(((Double) value).doubleValue());
        }
        if (value instanceof Integer) {
            return new BigDecimal(((Integer) value).intValue());
        }
        String str = toStr(value, null);
        if (StringUtils.isEmpty(str)) {
            return defaultValue;
        }

        try {
            return new BigDecimal(str);
        } catch (Exception exception) {

            return defaultValue;
        }
    }


    public static BigDecimal toBigDecimal(Object value) {
        return toBigDecimal(value, null);
    }


    public static String utf8Str(Object obj) {
        return str(obj, CharsetKit.CHARSET_UTF_8);
    }


    public static String str(Object obj, String charsetName) {
        return str(obj, Charset.forName(charsetName));
    }


    public static String str(Object obj, Charset charset) {
        if (null == obj) {
            return null;
        }

        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof byte[]) {
            return str((byte[]) obj, charset);
        }
        if (obj instanceof Byte[]) {

            byte[] arrayOfByte = ArrayUtils.toPrimitive((Byte[]) obj);
            return str(arrayOfByte, charset);
        }
        if (obj instanceof ByteBuffer) {
            return str((ByteBuffer) obj, charset);
        }
        return obj.toString();
    }


    public static String str(byte[] bytes, String charset) {
        return str(bytes, StringUtils.isEmpty(charset) ? Charset.defaultCharset() : Charset.forName(charset));
    }


    public static String str(byte[] data, Charset charset) {
        if (data == null) {
            return null;
        }

        if (null == charset) {
            return new String(data);
        }
        return new String(data, charset);
    }


    public static String str(ByteBuffer data, String charset) {
        if (data == null) {
            return null;
        }

        return str(data, Charset.forName(charset));
    }


    public static String str(ByteBuffer data, Charset charset) {
        if (null == charset) {
            charset = Charset.defaultCharset();
        }
        return charset.decode(data).toString();
    }


    public static String toSBC(String input) {
        return toSBC(input, null);
    }


    public static String toSBC(String input, Set<Character> notConvertSet) {
        char[] arrayOfChar = input.toCharArray();
        for (byte b = 0; b < arrayOfChar.length; b++) {

            if (null == notConvertSet || !notConvertSet.contains(Character.valueOf(arrayOfChar[b]))) {


                if (arrayOfChar[b] == ' ') {

                    arrayOfChar[b] = '　';
                } else if (arrayOfChar[b] < '') {

                    arrayOfChar[b] = (char) (arrayOfChar[b] + 65248);
                }
            }
        }
        return new String(arrayOfChar);
    }


    public static String toDBC(String input) {
        return toDBC(input, null);
    }


    public static String toDBC(String text, Set<Character> notConvertSet) {
        char[] arrayOfChar = text.toCharArray();
        for (byte b = 0; b < arrayOfChar.length; b++) {

            if (null == notConvertSet || !notConvertSet.contains(Character.valueOf(arrayOfChar[b]))) {


                if (arrayOfChar[b] == '　') {

                    arrayOfChar[b] = ' ';
                } else if (arrayOfChar[b] > '＀' && arrayOfChar[b] < '｟') {

                    arrayOfChar[b] = (char) (arrayOfChar[b] - 65248);
                }
            }
        }
        return new String(arrayOfChar);
    }


    public static String digitUppercase(double n) {
        String[] arrayOfString1 = {"角", "分"};
        String[] arrayOfString2 = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
        String[][] arrayOfString = {{"元", "万", "亿"}, {"", "拾", "佰", "仟"}};

        String str1 = (n < 0.0D) ? "负" : "";
        n = Math.abs(n);

        String str2 = "";
        int i;
        for (i = 0; i < arrayOfString1.length; i++) {
            str2 = str2 + (arrayOfString2[(int) (Math.floor(n * 10.0D * Math.pow(10.0D, i)) % 10.0D)] + arrayOfString1[i]).replaceAll("(零.)+", "");
        }
        if (str2.length() < 1) {
            str2 = "整";
        }
        i = (int) Math.floor(n);

        for (byte b = 0; b < (arrayOfString[0]).length && i > 0; b++) {

            String str = "";
            for (byte b1 = 0; b1 < (arrayOfString[1]).length && n > 0.0D; b1++) {

                str = arrayOfString2[i % 10] + arrayOfString[1][b1] + str;
                i /= 10;
            }
            str2 = str.replaceAll("(零.)*零$", "").replaceAll("^$", "零") + arrayOfString[0][b] + str2;
        }
        return str1 + str2.replaceAll("(零.)*零元", "元").replaceFirst("(零.)+", "").replaceAll("(零.)+", "零").replaceAll("^整$", "零元整");
    }
}

