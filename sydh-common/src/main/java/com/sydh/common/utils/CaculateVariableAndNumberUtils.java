package com.sydh.common.utils;

import com.sydh.common.exception.ServiceException;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CaculateVariableAndNumberUtils {
    private static final String ar = "+-,*/,(),%";
    private static final Map<String, Integer> as = new HashMap<String, Integer>() {

    };


    public static BigDecimal execute(String exeStr, Map<String, String> replaceMap) {
        List<String> list = suffixHandle(exeStr);
        System.out.println("计算结果： " + list);
        ArrayList<String> arrayList = new ArrayList();
        for (String str1 : list) {
            String str2 = replaceMap.get(str1);
            if (StringUtils.isNotEmpty(str2)) {
                arrayList.add(str2);
                continue;
            }
            arrayList.add(str1);
        }

        return caculateAnalyse(arrayList);
    }


    public static BigDecimal caculateAnalyse(List<String> suffixList) {
        BigDecimal bigDecimal1 = BigDecimal.ZERO;
        BigDecimal bigDecimal2 = BigDecimal.ZERO;

        Stack<BigDecimal> stack = new Stack();
        if (suffixList.size() > 1) {

            for (byte b = 0; b < suffixList.size(); b++) {
                String str = suffixList.get(b);
                if ("+-,*/,(),%".contains(str)) {
                    bigDecimal2 = stack.pop();
                    bigDecimal1 = stack.pop();
                    bigDecimal1 = caculate(bigDecimal1, bigDecimal2, str.toCharArray()[0]);
                    stack.push(bigDecimal1);
                } else if (isNumber(suffixList.get(b))) {
                    stack.push(new BigDecimal(suffixList.get(b)));
                } else {
                    throw new RuntimeException("公式异常！");
                }

            }
        } else if (suffixList.size() == 1) {
            String str = suffixList.get(0);
            if (isNumber(str)) {
                bigDecimal1 = BigDecimal.valueOf(Double.parseDouble(str));
            } else {
                throw new RuntimeException("公式异常！");
            }
        }
        return bigDecimal1;
    }


    public static BigDecimal caculate(BigDecimal a, BigDecimal b, char symbol) {
        int i;
        switch (symbol) {
            case '+':
                return a.add(b).stripTrailingZeros();

            case '-':
                return a.subtract(b).stripTrailingZeros();
            case '*':
                return a.multiply(b);
            case '%':
            case '/':
                i = a(a, b);
                return a.divide(b, i, 4);
        }
        throw new RuntimeException("操作符号异常！");
    }


    private static int a(BigDecimal paramBigDecimal1, BigDecimal paramBigDecimal2) {
        String str1 = paramBigDecimal1.toString();
        String str2 = paramBigDecimal2.toString();
        int i = 0;
        int j = 0;
        if (str1.contains(".")) {
            i = str1.split("\\.")[1].length();
        }
        if (str2.contains(".")) {
            j = str2.split("\\.")[1].length();
        }
        if (i == 0 && j == 0) {
            return 2;
        }
        return Math.max(i, j);
    }


    public static List<String> suffixHandle(String exeStr) {
        StringBuilder stringBuilder = new StringBuilder();
        Stack<Character> stack = new Stack();
        char[] arrayOfChar = exeStr.toCharArray();
        ArrayList<String> arrayList = new ArrayList();
        for (char c : arrayOfChar) {

            if ("+-,*/,(),%".indexOf(c) > -1) {

                if (stringBuilder.length() > 0) {

                    String str = stringBuilder.toString();

                    if (!isVariableAndNumber(str)) {
                        throw new RuntimeException(stringBuilder.append("  格式不对").toString());
                    }


                    arrayList.add(str);

                    stringBuilder.delete(0, stringBuilder.length());
                }
                if (!stack.isEmpty()) {


                    if (c == '(') {
                        stack.push(Character.valueOf(c));


                    } else if (c == ')') {
                        boolean bool = false;
                        while (!stack.isEmpty()) {
                            char c1 = ((Character) stack.peek()).charValue();
                            if (c1 == '(' && !bool) {
                                stack.pop();
                                bool = true;
                                continue;
                            }
                            if (!bool) {
                                arrayList.add(String.valueOf(stack.pop()));

                            }

                        }

                    } else {

                        int i = stack.size();
                        while (i > 0) {
                            char c1 = ((Character) stack.peek()).charValue();
                            if (compare(c1, c) > 0) {
                                arrayList.add(String.valueOf(stack.pop()));
                            }
                            i--;
                        }
                        stack.push(Character.valueOf(c));
                    }
                } else {
                    stack.push(Character.valueOf(c));
                }
            } else {
                stringBuilder.append(c);
            }
        }
        if (stringBuilder.length() > 0) {
            arrayList.add(stringBuilder.toString());
        }
        while (!stack.isEmpty()) {
            arrayList.add(String.valueOf(stack.pop()));
        }
        return arrayList;
    }


    public static int compare(char a, char b) {
        String str1 = String.valueOf(a);
        String str2 = String.valueOf(b);
        Integer integer1 = as.get(str1);
        Integer integer2 = as.get(str2);
        if (null != integer1 && null != integer2) {
            if (integer1.intValue() <= integer2.intValue()) {
                return 1;
            }
            return -1;
        }

        return 0;
    }


    public static boolean isNumber(String str) {
        Pattern pattern = Pattern.compile("[-+]?\\d+(?:\\.\\d+)?");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }


    public static boolean isVariable(String str) {
        Pattern pattern = Pattern.compile("^[A-Z]+$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }


    public static boolean isVariableAndNumber(String str) {
        Pattern pattern = Pattern.compile("[A-Z]|-?\\d+(\\.\\d+)?");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static String caculateReplace(String str, Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            str = str.replaceAll((String) entry.getKey(), (entry.getValue() == null) ? "1" : (String) entry.getValue());
        }
        return str;
    }

    public static String toFloat(byte[] bytes) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        try {
            float f = dataInputStream.readFloat();
            return String.format("%.6f", new Object[]{Float.valueOf(f)});
        } catch (Exception exception) {
            throw new ServiceException("modbus16转浮点数错误");
        } finally {

            dataInputStream.close();
            byteArrayInputStream.close();
        }
    }


    public static String toUnSign16(long value) {
        long l = value & 0xFFFFL;
        return l + "";
    }


    public static String toSign32_CDAB(long value) {
        byte[] arrayOfByte = intToBytes2((int) value);
        return bytesToInt2(arrayOfByte) + "";
    }


    public static String toUnSign32_ABCD(long value) {
        return Integer.toUnsignedString((int) value);
    }


    public static String toUnSign32_CDAB(long value) {
        byte[] arrayOfByte = intToBytes2((int) value);
        int i = bytesToInt2(arrayOfByte);
        return Integer.toUnsignedString(i);
    }


    public static float toFloat32_ABCD(byte[] bytes) {
        int i = bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | bytes[3] & 0xFF;
        return Float.intBitsToFloat(i);
    }


    public static Float toFloat32_CDAB(byte[] bytes) {
        int i = (bytes[2] & 0xFF) << 24 | (bytes[3] & 0xFF) << 16 | (bytes[0] & 0xFF) << 8 | bytes[1] & 0xFF;
        return Float.valueOf(Float.intBitsToFloat(i));
    }


    public static int bytesToInt2(byte[] src) {
        return (src[2] & 0xFF) << 24 | (src[3] & 0xFF) << 16 | (src[0] & 0xFF) << 8 | src[1] & 0xFF;
    }


    public static byte[] intToBytes2(int value) {
        byte[] arrayOfByte = new byte[4];
        arrayOfByte[0] = (byte) (value >> 24 & 0xFF);
        arrayOfByte[1] = (byte) (value >> 16 & 0xFF);
        arrayOfByte[2] = (byte) (value >> 8 & 0xFF);
        arrayOfByte[3] = (byte) (value & 0xFF);
        return arrayOfByte;
    }


    public static String subHexValue(String hexString) {
        String str = hexString.substring(4, 6);
        int i = Integer.parseInt(str);
        return hexString.substring(6, 6 + i * 2);
    }


    public static void main(String[] args) throws IOException {
        String str1 = "A/B*C";
        String str2 = "E-((A+B)-(C+D))%10";
        String str3 = "A-B-C*(D-E)+10*5";
        String str4 = "A-B-C*(D+E)-(A+B)+(2+3)";
        String str5 = "A-(A-(B-C)*(D+E))%10+B";
        String str6 = "A-(B+C)*D+10";
        String str7 = "1+2*3-2+2*(1-2+3*4+5-6/2+(2-1)+3*4-2)%10";


        boolean bool = isNumber("-10");
        System.out.println(bool);

        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put("A", "1");
        hashMap.put("B", "2");
        hashMap.put("C", "3");
        hashMap.put("D", "4");
        hashMap.put("E", "10");
        BigDecimal bigDecimal = execute(str7, (Map) hashMap);
        System.out.println(bigDecimal);
    }
}
