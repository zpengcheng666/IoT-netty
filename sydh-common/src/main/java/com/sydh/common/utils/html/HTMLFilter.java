/*     */ package com.sydh.common.utils.html;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HTMLFilter {
    private static final int bh = 34;
    private static final Pattern bi = Pattern.compile("<!--(.*?)-->", 32);
    private static final Pattern bj = Pattern.compile("^!--(.*)--$", 34);
    private static final Pattern bk = Pattern.compile("<(.*?)>", 32);
    private static final Pattern bl = Pattern.compile("^/([a-z0-9]+)", 34);
    private static final Pattern bm = Pattern.compile("^([a-z0-9]+)(.*?)(/?)$", 34);
    private static final Pattern bn = Pattern.compile("([a-z0-9]+)=([\"'])(.*?)\\2", 34);
    private static final Pattern bo = Pattern.compile("([a-z0-9]+)(=)([^\"\\s']+)", 34);
    private static final Pattern bp = Pattern.compile("^([^:]+):", 34);
    private static final Pattern bq = Pattern.compile("&#(\\d+);?");
    private static final Pattern br = Pattern.compile("&#x([0-9a-f]+);?");
    private static final Pattern bs = Pattern.compile("%([0-9a-f]{2});?");
    private static final Pattern bt = Pattern.compile("&([^&;]*)(?=(;|&|$))");
    private static final Pattern bu = Pattern.compile("(>|^)([^<]+?)(<|$)", 32);
    private static final Pattern bv = Pattern.compile("^>");
    private static final Pattern bw = Pattern.compile("<([^>]*?)(?=<|$)");
    private static final Pattern bx = Pattern.compile("(^|>)([^<]*?)(?=>)");
    private static final Pattern by = Pattern.compile("<([^>]*?)(?=<|$)");
    private static final Pattern bz = Pattern.compile("(^|>)([^<]*?)(?=>)");
    private static final Pattern bA = Pattern.compile("&");
    private static final Pattern bB = Pattern.compile("\"");
    private static final Pattern bC = Pattern.compile("<");
    private static final Pattern bD = Pattern.compile(">");
    private static final Pattern bE = Pattern.compile("<>");
    private static final ConcurrentMap<String, Pattern> bF = new ConcurrentHashMap();
    private static final ConcurrentMap<String, Pattern> bG = new ConcurrentHashMap();
    private final Map<String, List<String>> bH;
    private final Map<String, Integer> bI = new HashMap();
    private final String[] bJ;
    private final String[] bK;
    private final String[] bL;
    private final String[] bM;
    private final String[] bN;
    private final String[] bO;
    private final String[] bP;
    private final boolean bQ;
    private final boolean bR;
    private final boolean bS;

    public HTMLFilter() {
        this.bH = new HashMap();
        ArrayList var1 = new ArrayList();
        var1.add("href");
        var1.add("target");
        this.bH.put("a", var1);
        ArrayList var2 = new ArrayList();
        var2.add("src");
        var2.add("width");
        var2.add("height");
        var2.add("alt");
        this.bH.put("img", var2);
        ArrayList var3 = new ArrayList();
        this.bH.put("b", var3);
        this.bH.put("strong", var3);
        this.bH.put("i", var3);
        this.bH.put("em", var3);
        this.bJ = new String[]{"img"};
        this.bK = new String[]{"a", "b", "strong", "i", "em"};
        this.bL = new String[0];
        this.bN = new String[]{"http", "mailto", "https"};
        this.bM = new String[]{"src", "href"};
        this.bO = new String[]{"a", "b", "strong", "i", "em"};
        this.bP = new String[]{"amp", "gt", "lt", "quot"};
        this.bQ = true;
        this.bR = true;
        this.bS = false;
    }

    public HTMLFilter(Map<String, Object> conf) {
        assert conf.containsKey("vAllowed") : "configuration requires vAllowed";

        assert conf.containsKey("vSelfClosingTags") : "configuration requires vSelfClosingTags";

        assert conf.containsKey("vNeedClosingTags") : "configuration requires vNeedClosingTags";

        assert conf.containsKey("vDisallowed") : "configuration requires vDisallowed";

        assert conf.containsKey("vAllowedProtocols") : "configuration requires vAllowedProtocols";

        assert conf.containsKey("vProtocolAtts") : "configuration requires vProtocolAtts";

        assert conf.containsKey("vRemoveBlanks") : "configuration requires vRemoveBlanks";

        assert conf.containsKey("vAllowedEntities") : "configuration requires vAllowedEntities";

        this.bH = Collections.unmodifiableMap((HashMap)conf.get("vAllowed"));
        this.bJ = (String[])((String[])conf.get("vSelfClosingTags"));
        this.bK = (String[])((String[])conf.get("vNeedClosingTags"));
        this.bL = (String[])((String[])conf.get("vDisallowed"));
        this.bN = (String[])((String[])conf.get("vAllowedProtocols"));
        this.bM = (String[])((String[])conf.get("vProtocolAtts"));
        this.bO = (String[])((String[])conf.get("vRemoveBlanks"));
        this.bP = (String[])((String[])conf.get("vAllowedEntities"));
        this.bQ = conf.containsKey("stripComment") ? (Boolean)conf.get("stripComment") : true;
        this.bR = conf.containsKey("encodeQuotes") ? (Boolean)conf.get("encodeQuotes") : true;
        this.bS = conf.containsKey("alwaysMakeTags") ? (Boolean)conf.get("alwaysMakeTags") : true;
    }

    private void d() {
        this.bI.clear();
    }

    public static String chr(int decimal) {
        return String.valueOf((char)decimal);
    }

    public static String htmlSpecialChars(String s) {
        String var1 = a(bA, "&amp;", s);
        var1 = a(bB, "&quot;", var1);
        var1 = a(bC, "&lt;", var1);
        var1 = a(bD, "&gt;", var1);
        return var1;
    }

    public String filter(String input) {
        this.d();
        String var2 = this.d(input);
        var2 = this.e(var2);
        var2 = this.f(var2);
        var2 = this.g(var2);
        return var2;
    }

    public boolean isAlwaysMakeTags() {
        return this.bS;
    }

    public boolean isStripComments() {
        return this.bQ;
    }

    private String d(String var1) {
        Matcher var2 = bi.matcher(var1);
        StringBuffer var3 = new StringBuffer();
        if (var2.find()) {
            String var4 = var2.group(1);
            var2.appendReplacement(var3, Matcher.quoteReplacement("<!--" + htmlSpecialChars(var4) + "-->"));
        }

        var2.appendTail(var3);
        return var3.toString();
    }

    private String e(String var1) {
        if (this.bS) {
            var1 = a(bv, "", var1);
            var1 = a(bw, "<$1>", var1);
            var1 = a(bx, "$1<$2", var1);
        } else {
            var1 = a(by, "&lt;$1", var1);
            var1 = a(bz, "$1$2&gt;<", var1);
            var1 = a(bE, "", var1);
        }

        return var1;
    }

    private String f(String var1) {
        Matcher var2 = bk.matcher(var1);
        StringBuffer var3 = new StringBuffer();

        while(var2.find()) {
            String var4 = var2.group(1);
            var4 = this.h(var4);
            var2.appendReplacement(var3, Matcher.quoteReplacement(var4));
        }

        var2.appendTail(var3);
        StringBuilder var8 = new StringBuilder(var3.toString());
        Iterator var5 = this.bI.keySet().iterator();

        while(var5.hasNext()) {
            String var6 = (String)var5.next();

            for(int var7 = 0; var7 < (Integer)this.bI.get(var6); ++var7) {
                var8.append("</").append(var6).append(">");
            }
        }

        var1 = var8.toString();
        return var1;
    }

    private String g(String var1) {
        String var2 = var1;
        String[] var3 = this.bO;
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String var6 = var3[var5];
            if (!bF.containsKey(var6)) {
                bF.putIfAbsent(var6, Pattern.compile("<" + var6 + "(\\s[^>]*)?></" + var6 + ">"));
            }

            var2 = a((Pattern)bF.get(var6), "", var2);
            if (!bG.containsKey(var6)) {
                bG.putIfAbsent(var6, Pattern.compile("<" + var6 + "(\\s[^>]*)?/>"));
            }

            var2 = a((Pattern)bG.get(var6), "", var2);
        }

        return var2;
    }

    private static String a(Pattern var0, String var1, String var2) {
        Matcher var3 = var0.matcher(var2);
        return var3.replaceAll(var1);
    }

    private String h(String var1) {
        Matcher var2 = bl.matcher(var1);
        String var3;
        if (var2.find()) {
            var3 = var2.group(1).toLowerCase();
            if (this.n(var3) && !a(var3, this.bJ) && this.bI.containsKey(var3)) {
                this.bI.put(var3, (Integer)this.bI.get(var3) - 1);
                return "</" + var3 + ">";
            }
        }

        var2 = bm.matcher(var1);
        if (!var2.find()) {
            var2 = bj.matcher(var1);
            return !this.bQ && var2.find() ? "<" + var2.group() + ">" : "";
        } else {
            var3 = var2.group(1).toLowerCase();
            String var4 = var2.group(2);
            String var5 = var2.group(3);
            if (!this.n(var3)) {
                return "";
            } else {
                StringBuilder var6 = new StringBuilder();
                Matcher var7 = bn.matcher(var4);
                Matcher var8 = bo.matcher(var4);
                ArrayList var9 = new ArrayList();
                ArrayList var10 = new ArrayList();

                while(var7.find()) {
                    var9.add(var7.group(1));
                    var10.add(var7.group(3));
                }

                while(var8.find()) {
                    var9.add(var8.group(1));
                    var10.add(var8.group(3));
                }

                for(int var13 = 0; var13 < var9.size(); ++var13) {
                    String var11 = ((String)var9.get(var13)).toLowerCase();
                    String var12 = (String)var10.get(var13);
                    if (this.b(var3, var11)) {
                        if (a(var11, this.bM)) {
                            var12 = this.i(var12);
                        }

                        var6.append(' ').append(var11).append("=\\\"").append(var12).append("\\\"");
                    }
                }

                if (a(var3, this.bJ)) {
                    var5 = " /";
                }

                if (a(var3, this.bK)) {
                    var5 = "";
                }

                if (var5 != null && var5.length() >= 1) {
                    var5 = " /";
                } else if (this.bI.containsKey(var3)) {
                    this.bI.put(var3, (Integer)this.bI.get(var3) + 1);
                } else {
                    this.bI.put(var3, 1);
                }

                return "<" + var3 + var6 + var5 + ">";
            }
        }
    }

    private String i(String var1) {
        var1 = this.j(var1);
        Matcher var2 = bp.matcher(var1);
        if (var2.find()) {
            String var3 = var2.group(1);
            if (!a(var3, this.bN)) {
                var1 = "#" + var1.substring(var3.length() + 1);
                if (var1.startsWith("#//")) {
                    var1 = "#" + var1.substring(3);
                }
            }
        }

        return var1;
    }

    private String j(String var1) {
        StringBuffer var2 = new StringBuffer();
        Matcher var3 = bq.matcher(var1);

        String var4;
        int var5;
        while(var3.find()) {
            var4 = var3.group(1);
            var5 = Integer.decode(var4);
            var3.appendReplacement(var2, Matcher.quoteReplacement(chr(var5)));
        }

        var3.appendTail(var2);
        var1 = var2.toString();
        var2 = new StringBuffer();
        var3 = br.matcher(var1);

        while(var3.find()) {
            var4 = var3.group(1);
            var5 = Integer.valueOf(var4, 16);
            var3.appendReplacement(var2, Matcher.quoteReplacement(chr(var5)));
        }

        var3.appendTail(var2);
        var1 = var2.toString();
        var2 = new StringBuffer();
        var3 = bs.matcher(var1);

        while(var3.find()) {
            var4 = var3.group(1);
            var5 = Integer.valueOf(var4, 16);
            var3.appendReplacement(var2, Matcher.quoteReplacement(chr(var5)));
        }

        var3.appendTail(var2);
        var1 = var2.toString();
        var1 = this.k(var1);
        return var1;
    }

    private String k(String var1) {
        StringBuffer var2 = new StringBuffer();
        Matcher var3 = bt.matcher(var1);

        while(var3.find()) {
            String var4 = var3.group(1);
            String var5 = var3.group(2);
            var3.appendReplacement(var2, Matcher.quoteReplacement(this.a(var4, var5)));
        }

        var3.appendTail(var2);
        return this.l(var2.toString());
    }

    private String l(String var1) {
        if (!this.bR) {
            return var1;
        } else {
            StringBuffer var2 = new StringBuffer();
            Matcher var3 = bu.matcher(var1);

            while(var3.find()) {
                String var4 = var3.group(1);
                String var5 = var3.group(2);
                String var6 = var3.group(3);
                var3.appendReplacement(var2, Matcher.quoteReplacement(var4 + var5 + var6));
            }

            var3.appendTail(var2);
            return var2.toString();
        }
    }

    private String a(String var1, String var2) {
        return ";".equals(var2) && this.m(var1) ? '&' + var1 : "&amp;" + var1;
    }

    private boolean m(String var1) {
        return a(var1, this.bP);
    }

    private static boolean a(String var0, String[] var1) {
        String[] var2 = var1;
        int var3 = var1.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String var5 = var2[var4];
            if (var5 != null && var5.equals(var0)) {
                return true;
            }
        }

        return false;
    }

    private boolean n(String var1) {
        return (this.bH.isEmpty() || this.bH.containsKey(var1)) && !a(var1, this.bL);
    }

    private boolean b(String var1, String var2) {
        return this.n(var1) && (this.bH.isEmpty() || ((List)this.bH.get(var1)).contains(var2));
    }
}