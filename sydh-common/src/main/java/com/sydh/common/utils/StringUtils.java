package com.sydh.common.utils;

import cn.hutool.core.collection.CollUtil;
import com.sydh.common.core.text.StrFormatter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.collections4.MapUtils;
import org.springframework.util.AntPathMatcher;

public class StringUtils extends org.apache.commons.lang3.StringUtils {
    private static final String aH = "";
    private static final char aI = '_';
    public static final String SEPARATOR2 = ",";
    public static final String SLASH = "/";

    public StringUtils() {
    }

    public static <T> T nvl(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    public static boolean isEmpty(Collection<?> coll) {
        return isNull(coll) || coll.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

    public static boolean isEmpty(Object[] objects) {
        return isNull(objects) || objects.length == 0;
    }

    public static boolean isNotEmpty(Object[] objects) {
        return !isEmpty(objects);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return isNull(map) || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static boolean isEmpty(String str) {
        return isNull(str) || "".equals(str.trim());
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isNull(Object object) {
        return object == null;
    }

    public static boolean isNotNull(Object object) {
        return !isNull(object);
    }

    public static boolean isArray(Object object) {
        return isNotNull(object) && object.getClass().isArray();
    }

    public static String trim(String str) {
        return str == null ? "" : str.trim();
    }

    public static String substring(String str, int start) {
        if (str == null) {
            return "";
        } else {
            if (start < 0) {
                start += str.length();
            }

            if (start < 0) {
                start = 0;
            }

            return start > str.length() ? "" : str.substring(start);
        }
    }

    public static String substring(String str, int start, int end) {
        if (str == null) {
            return "";
        } else {
            if (end < 0) {
                end += str.length();
            }

            if (start < 0) {
                start += str.length();
            }

            if (end > str.length()) {
                end = str.length();
            }

            if (start > end) {
                return "";
            } else {
                if (start < 0) {
                    start = 0;
                }

                if (end < 0) {
                    end = 0;
                }

                return str.substring(start, end);
            }
        }
    }

    public static String format(String template, Object... params) {
        return !isEmpty(params) && !isEmpty(template) ? StrFormatter.format(template, params) : template;
    }

    public static boolean ishttp(String link) {
        return startsWithAny(link, new CharSequence[]{"http://", "https://"});
    }

    public static final Set<String> str2Set(String str, String sep) {
        return new HashSet(str2List(str, sep, true, false));
    }

    public static final List<String> str2List(String str, String sep, boolean filterBlank, boolean trim) {
        ArrayList var4 = new ArrayList();
        if (isEmpty(str)) {
            return var4;
        } else if (filterBlank && isBlank(str)) {
            return var4;
        } else {
            String[] var5 = str.split(sep);
            String[] var6 = var5;
            int var7 = var5.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                String var9 = var6[var8];
                if (!filterBlank || !isBlank(var9)) {
                    if (trim) {
                        var9 = var9.trim();
                    }

                    var4.add(var9);
                }
            }

            return var4;
        }
    }

    public static boolean containsAny(Collection<String> collection, String... array) {
        if (!isEmpty(collection) && !isEmpty((Object[])array)) {
            String[] var2 = array;
            int var3 = array.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                String var5 = var2[var4];
                if (collection.contains(var5)) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public static boolean containsAnyIgnoreCase(CharSequence cs, CharSequence... searchCharSequences) {
        if (!isEmpty((CharSequence)cs) && !isEmpty((Object[])searchCharSequences)) {
            CharSequence[] var2 = searchCharSequences;
            int var3 = searchCharSequences.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                CharSequence var5 = var2[var4];
                if (containsIgnoreCase(cs, var5)) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public static String toUnderScoreCase(String str) {
        if (str == null) {
            return null;
        } else {
            StringBuilder var1 = new StringBuilder();
            boolean var2 = true;
            boolean var3 = true;
            boolean var4 = true;

            for(int var5 = 0; var5 < str.length(); ++var5) {
                char var6 = str.charAt(var5);
                if (var5 > 0) {
                    var2 = Character.isUpperCase(str.charAt(var5 - 1));
                } else {
                    var2 = false;
                }

                var3 = Character.isUpperCase(var6);
                if (var5 < str.length() - 1) {
                    var4 = Character.isUpperCase(str.charAt(var5 + 1));
                }

                if (var2 && var3 && !var4) {
                    var1.append('_');
                } else if (var5 != 0 && !var2 && var3) {
                    var1.append('_');
                }

                var1.append(Character.toLowerCase(var6));
            }

            return var1.toString();
        }
    }

    public static boolean inStringIgnoreCase(String str, String... strs) {
        if (str != null && strs != null) {
            String[] var2 = strs;
            int var3 = strs.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                String var5 = var2[var4];
                if (str.equalsIgnoreCase(trim(var5))) {
                    return true;
                }
            }
        }

        return false;
    }

    public static String convertToCamelCase(String name) {
        StringBuilder var1 = new StringBuilder();
        if (name != null && !name.isEmpty()) {
            if (!name.contains("_")) {
                return name.substring(0, 1).toUpperCase() + name.substring(1);
            } else {
                String[] var2 = name.split("_");
                String[] var3 = var2;
                int var4 = var2.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    String var6 = var3[var5];
                    if (!var6.isEmpty()) {
                        var1.append(var6.substring(0, 1).toUpperCase());
                        var1.append(var6.substring(1).toLowerCase());
                    }
                }

                return var1.toString();
            }
        } else {
            return "";
        }
    }

    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        } else {
            s = s.toLowerCase();
            StringBuilder var1 = new StringBuilder(s.length());
            boolean var2 = false;

            for(int var3 = 0; var3 < s.length(); ++var3) {
                char var4 = s.charAt(var3);
                if (var4 == '_') {
                    var2 = true;
                } else if (var2) {
                    var1.append(Character.toUpperCase(var4));
                    var2 = false;
                } else {
                    var1.append(var4);
                }
            }

            return var1.toString();
        }
    }

    public static boolean matches(String str, List<String> strs) {
        if (!isEmpty(str) && !isEmpty((Collection)strs)) {
            Iterator var2 = strs.iterator();

            String var3;
            do {
                if (!var2.hasNext()) {
                    return false;
                }

                var3 = (String)var2.next();
            } while(!isMatch(var3, str));

            return true;
        } else {
            return false;
        }
    }

    public static boolean isMatch(String pattern, String url) {
        AntPathMatcher var2 = new AntPathMatcher();
        return var2.match(pattern, url);
    }

    public static <T> T cast(Object obj) {
        return (T) obj;
    }

    public static final String padl(Number num, int size) {
        return padl(num.toString(), size, '0');
    }

    public static final String padl(String s, int size, char c) {
        StringBuilder var3 = new StringBuilder(size);
        int var4;
        if (s != null) {
            var4 = s.length();
            if (s.length() > size) {
                return s.substring(var4 - size, var4);
            }

            for(int var5 = size - var4; var5 > 0; --var5) {
                var3.append(c);
            }

            var3.append(s);
        } else {
            for(var4 = size; var4 > 0; --var4) {
                var3.append(c);
            }
        }

        return var3.toString();
    }

    public static String upperCase(String str) {
        char[] var1 = str.toLowerCase().toCharArray();
        if (var1[0] >= 'a' && var1[0] <= 'z') {
            var1[0] = (char)(var1[0] - 32);
        }

        return new String(var1);
    }

    public static String toString(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof ByteBuf) {
            return ByteBufUtil.hexDump((ByteBuf)value);
        } else if (!value.getClass().isArray()) {
            return value.toString();
        } else {
            StringBuilder var1 = new StringBuilder(32);
            toString(value, var1);
            return var1.toString();
        }
    }

    public static StringBuilder toString(Object value, StringBuilder builder) {
        if (value == null) {
            return builder;
        } else {
            builder.append('[');
            int var2 = builder.length();
            int var5;
            int var6;
            if (value instanceof long[]) {
                long[] var3 = (long[])((long[])value);
                long[] var4 = var3;
                var5 = var3.length;

                for(var6 = 0; var6 < var5; ++var6) {
                    long var7 = var4[var6];
                    builder.append(var7).append(',');
                }
            } else if (value instanceof int[]) {
                int[] var9 = (int[])((int[])value);
                int[] var12 = var9;
                var5 = var9.length;

                for(var6 = 0; var6 < var5; ++var6) {
                    int var28 = var12[var6];
                    builder.append(var28).append(',');
                }
            } else if (value instanceof short[]) {
                short[] var10 = (short[])((short[])value);
                short[] var14 = var10;
                var5 = var10.length;

                for(var6 = 0; var6 < var5; ++var6) {
                    short var29 = var14[var6];
                    builder.append(var29).append(',');
                }
            } else {
                char var30;
                if (value instanceof byte[]) {
                    byte[] var11 = (byte[])((byte[])value);
                    byte[] var16 = var11;
                    var5 = var11.length;

                    for(var6 = 0; var6 < var5; ++var6) {
                        var30 = (char)var16[var6];
                        builder.append(var30).append(',');
                    }
                } else if (value instanceof char[]) {
                    char[] var13 = (char[])((char[])value);
                    char[] var18 = var13;
                    var5 = var13.length;

                    for(var6 = 0; var6 < var5; ++var6) {
                        var30 = var18[var6];
                        builder.append(var30).append(',');
                    }
                } else if (value instanceof double[]) {
                    double[] var15 = (double[])((double[])value);
                    double[] var20 = var15;
                    var5 = var15.length;

                    for(var6 = 0; var6 < var5; ++var6) {
                        double var31 = var20[var6];
                        builder.append(var31).append(',');
                    }
                } else if (value instanceof float[]) {
                    float[] var17 = (float[])((float[])value);
                    float[] var22 = var17;
                    var5 = var17.length;

                    for(var6 = 0; var6 < var5; ++var6) {
                        float var32 = var22[var6];
                        builder.append(var32).append(',');
                    }
                } else if (value instanceof boolean[]) {
                    boolean[] var19 = (boolean[])((boolean[])value);
                    boolean[] var24 = var19;
                    var5 = var19.length;

                    for(var6 = 0; var6 < var5; ++var6) {
                        boolean var33 = var24[var6];
                        builder.append(var33).append(',');
                    }
                } else if (value instanceof String[]) {
                    String[] var21 = (String[])((String[])value);
                    String[] var26 = var21;
                    var5 = var21.length;

                    for(var6 = 0; var6 < var5; ++var6) {
                        String var34 = var26[var6];
                        builder.append(var34).append(',');
                    }
                } else {
                    Object[] var23;
                    Object[] var27;
                    Object var35;
                    if (a(value)) {
                        var23 = (Object[])((Object[])value);
                        var27 = var23;
                        var5 = var23.length;

                        for(var6 = 0; var6 < var5; ++var6) {
                            var35 = var27[var6];
                            toString(var35, builder).append(',');
                        }
                    } else if (value instanceof Object[]) {
                        var23 = (Object[])((Object[])value);
                        var27 = var23;
                        var5 = var23.length;

                        for(var6 = 0; var6 < var5; ++var6) {
                            var35 = var27[var6];
                            builder.append(var35).append(',');
                        }
                    }
                }
            }

            int var25 = builder.length();
            if (var25 <= var2) {
                builder.append(']');
            } else {
                builder.setCharAt(var25 - 1, ']');
            }

            return builder;
        }
    }

    private static boolean a(Object var0) {
        Class var1 = var0.getClass().getComponentType();
        return var1 == null ? false : var1.isArray();
    }

    public static String leftPad(String str, int size, char ch) {
        int var3 = str.length();
        int var4 = size - var3;
        if (var4 <= 0) {
            return str;
        } else {
            char[] var5 = new char[size];
            str.getChars(0, var3, var5, var4);

            while(var4 > 0) {
                --var4;
                var5[var4] = ch;
            }

            return new String(var5);
        }
    }

    public static Integer matcherNum(String str) {
        Pattern var1 = Pattern.compile("\\d+");
        Matcher var2 = var1.matcher(str);
        return var2.find() ? Integer.parseInt(var2.group()) : 0;
    }

    public static List<String> getVariables(String variable, String str) {
        ArrayList var2 = new ArrayList();
        Pattern var3 = null;
        switch (variable) {
            case "${}":
                var3 = Pattern.compile("\\$\\{([^}]+)}");
                break;
            case "{{}}":
                var3 = Pattern.compile("\\{\\{([^}]+)}}");
                break;
            case "{}":
                var3 = Pattern.compile("\\{([^}]+)}");
                break;
            case "#{}":
                var3 = Pattern.compile("#\\{([^}]+)}");
        }

        assert var3 != null;

        Matcher var4 = var3.matcher(str);

        while(var4.find()) {
            var2.add(var4.group(1));
        }

        return var2;
    }

    public static List<String> getWeChatMiniVariables(String content) {
        ArrayList var1 = new ArrayList();
        Pattern var2 = Pattern.compile("\\{\\{([^}]+)}}");
        Matcher var3 = var2.matcher(content);

        while(var3.find()) {
            var1.add(var3.group(1).replace(".DATA", ""));
        }

        return var1;
    }

    public static String parse(String openToken, String closeToken, String text, Object... args) {
        if (args != null && args.length > 0) {
            int var4 = 0;
            if (text != null && !text.isEmpty()) {
                char[] var5 = text.toCharArray();
                int var6 = 0;
                int var7 = text.indexOf(openToken, var6);
                if (var7 == -1) {
                    return text;
                } else {
                    StringBuilder var8 = new StringBuilder();

                    for(StringBuilder var9 = null; var7 > -1; var7 = text.indexOf(openToken, var6)) {
                        if (var7 > 0 && var5[var7 - 1] == '\\') {
                            var8.append(var5, var6, var7 - var6 - 1).append(openToken);
                            var6 = var7 + openToken.length();
                        } else {
                            if (var9 == null) {
                                var9 = new StringBuilder();
                            } else {
                                var9.setLength(0);
                            }

                            var8.append(var5, var6, var7 - var6);
                            var6 = var7 + openToken.length();

                            int var10;
                            for(var10 = text.indexOf(closeToken, var6); var10 > -1; var10 = text.indexOf(closeToken, var6)) {
                                if (var10 <= var6 || var5[var10 - 1] != '\\') {
                                    var9.append(var5, var6, var10 - var6);
                                    int var10000 = var10 + closeToken.length();
                                    break;
                                }

                                var9.append(var5, var6, var10 - var6 - 1).append(closeToken);
                                var6 = var10 + closeToken.length();
                            }

                            if (var10 == -1) {
                                var8.append(var5, var7, var5.length - var7);
                                var6 = var5.length;
                            } else {
                                String var11 = var4 <= args.length - 1 ? (args[var4] == null ? "" : args[var4].toString()) : var9.toString();
                                var8.append(var11);
                                var6 = var10 + closeToken.length();
                                ++var4;
                            }
                        }
                    }

                    if (var6 < var5.length) {
                        var8.append(var5, var6, var5.length - var6);
                    }

                    return var8.toString();
                }
            } else {
                return "";
            }
        } else {
            return text;
        }
    }

    public static String reverse(String str) {
        return (new StringBuilder(str)).reverse().toString();
    }

    public static String parseVariable(String text, Object... args) {
        return parse("${", "}", text, args);
    }

    public static String strReplaceVariable(String openIndex, String closeIndex, String content, LinkedHashMap<String, String> map) {
        if (!isEmpty(content) && !MapUtils.isEmpty(map)) {
            StringBuilder var4 = new StringBuilder(content);

            Map.Entry var6;
            for(Iterator var5 = map.entrySet().iterator(); var5.hasNext(); var4 = new StringBuilder(var4.toString().replace(openIndex + (String)var6.getKey() + closeIndex, (CharSequence)var6.getValue()))) {
                var6 = (Map.Entry)var5.next();
            }

            return var4.toString();
        } else {
            return content;
        }
    }

    public static List<String> splitEvenly(String str, int size) {
        ArrayList var2 = new ArrayList();
        int var3 = str.length();
        if (size <= var3 && size > 0) {
            for(int var4 = 0; var4 < var3; var4 += size) {
                var2.add(str.substring(var4, Math.min(var3, var4 + size)));
            }

            return var2;
        } else {
            throw new IllegalArgumentException("Size is too large or too small.");
        }
    }

    public static String underlineToHump(String param) {
        StringBuilder var1 = new StringBuilder();
        String[] var2 = param.split("_");
        String[] var3 = var2;
        int var4 = var2.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String var6 = var3[var5];
            var1.append(var6.substring(0, 1).toUpperCase()).append(var6.substring(1));
        }

        return var1.toString();
    }

    public static String toGet(String param) {
        String var1 = underlineToHump(param);
        return "get" + var1 + "()";
    }

    public static <E, T> List<T> toList(Collection<E> collection, Function<E, T> function) {
        return (List)(CollUtil.isEmpty(collection) ? CollUtil.newArrayList(new Object[0]) : (List)collection.stream().map(function).filter(Objects::nonNull).collect(Collectors.toList()));
    }
}