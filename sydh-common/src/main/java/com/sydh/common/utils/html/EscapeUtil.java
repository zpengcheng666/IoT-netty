package com.sydh.common.utils.html;

import com.sydh.common.utils.StringUtils;


public class EscapeUtil {
    public static final String RE_HTML_MARK = "(<[^<]*?>)|(<[\\s]*?/[^<]*?>)|(<[^<]*?/[\\s]*?>)";
    private static final char[][] bg = new char[64][];


    static {
        for (byte b = 0; b < 64; b++) {

            (new char[1])[0] = (char) b;
            bg[b] = new char[1];
        }


        bg[39] = "&#039;".toCharArray();
        bg[34] = "&#34;".toCharArray();
        bg[38] = "&#38;".toCharArray();
        bg[60] = "&#60;".toCharArray();
        bg[62] = "&#62;".toCharArray();
    }


    public static String escape(String text) {
        return c(text);
    }


    public static String unescape(String content) {
        return decode(content);
    }


    public static String clean(String content) {
        return (new HTMLFilter()).filter(content);
    }


    private static String c(String paramString) {
        if (StringUtils.isEmpty(paramString)) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder(paramString.length() * 6);

        for (byte b = 0; b < paramString.length(); b++) {

            char c = paramString.charAt(b);
            if (c < 'Ā') {

                stringBuilder.append("%");
                if (c < '\020') {
                    stringBuilder.append("0");
                }
                stringBuilder.append(Integer.toString(c, 16));
            } else {

                stringBuilder.append("%u");
                if (c <= '࿿') {

                    stringBuilder.append("0");
                }
                stringBuilder.append(Integer.toString(c, 16));
            }
        }
        return stringBuilder.toString();
    }


    public static String decode(String content) {
        if (StringUtils.isEmpty(content)) {
            return content;
        }

        StringBuilder stringBuilder = new StringBuilder(content.length());
        int i = 0, j = 0;

        while (i < content.length()) {

            j = content.indexOf("%", i);
            if (j == i) {

                if (content.charAt(j + 1) == 'u') {

                    char c1 = (char) Integer.parseInt(content.substring(j + 2, j + 6), 16);
                    stringBuilder.append(c1);
                    i = j + 6;

                    continue;
                }
                char c = (char) Integer.parseInt(content.substring(j + 1, j + 3), 16);
                stringBuilder.append(c);
                i = j + 3;

                continue;
            }

            if (j == -1) {

                stringBuilder.append(content.substring(i));
                i = content.length();

                continue;
            }
            stringBuilder.append(content.substring(i, j));
            i = j;
        }


        return stringBuilder.toString();
    }


    public static void main(String[] args) {
        String str1 = "<script>alert(1);</script>";
        String str2 = escape(str1);


        System.out.println("clean: " + clean(str1));
        System.out.println("escape: " + str2);
        System.out.println("unescape: " + unescape(str2));
    }
}
