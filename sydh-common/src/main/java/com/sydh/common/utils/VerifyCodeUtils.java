/*     */ package com.sydh.common.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import javax.imageio.ImageIO;

public class VerifyCodeUtils {
    public static final String VERIFY_CODES = "123456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static Random aP = new SecureRandom();

    public VerifyCodeUtils() {
    }

    public static String generateVerifyCode(int verifySize) {
        return generateVerifyCode(verifySize, "123456789ABCDEFGHJKLMNPQRSTUVWXYZ");
    }

    public static String generateVerifyCode(int verifySize, String sources) {
        if (sources == null || sources.length() == 0) {
            sources = "123456789ABCDEFGHJKLMNPQRSTUVWXYZ";
        }

        int var2 = sources.length();
        Random var3 = new Random(System.currentTimeMillis());
        StringBuilder var4 = new StringBuilder(verifySize);

        for(int var5 = 0; var5 < verifySize; ++var5) {
            var4.append(sources.charAt(var3.nextInt(var2 - 1)));
        }

        return var4.toString();
    }

    public static void outputImage(int w, int h, OutputStream os, String code) throws IOException {
        int var4 = code.length();
        BufferedImage var5 = new BufferedImage(w, h, 1);
        Random var6 = new Random();
        Graphics2D var7 = var5.createGraphics();
        var7.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color[] var8 = new Color[5];
        Color[] var9 = new Color[]{Color.WHITE, Color.CYAN, Color.GRAY, Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.YELLOW};
        float[] var10 = new float[var8.length];

        for(int var11 = 0; var11 < var8.length; ++var11) {
            var8[var11] = var9[var6.nextInt(var9.length)];
            var10[var11] = var6.nextFloat();
        }

        Arrays.sort(var10);
        var7.setColor(Color.GRAY);
        var7.fillRect(0, 0, w, h);
        Color var20 = a(200, 250);
        var7.setColor(var20);
        var7.fillRect(0, 2, w, h - 4);
        Random var12 = new Random();
        var7.setColor(a(160, 200));

        int var14;
        int var15;
        int var16;
        int var17;
        for(int var13 = 0; var13 < 20; ++var13) {
            var14 = var12.nextInt(w - 1);
            var15 = var12.nextInt(h - 1);
            var16 = var12.nextInt(6) + 1;
            var17 = var12.nextInt(12) + 1;
            var7.drawLine(var14, var15, var14 + var16 + 40, var15 + var17 + 20);
        }

        float var21 = 0.05F;
        var14 = (int)(var21 * (float)w * (float)h);

        int var18;
        for(var15 = 0; var15 < var14; ++var15) {
            var16 = var12.nextInt(w);
            var17 = var12.nextInt(h);
            var18 = b();
            var5.setRGB(var16, var17, var18);
        }

        a(var7, w, h, var20);
        var7.setColor(a(100, 160));
        var15 = h - 4;
        Font var22 = new Font("Algerian", 2, var15);
        var7.setFont(var22);
        char[] var23 = code.toCharArray();

        for(var18 = 0; var18 < var4; ++var18) {
            AffineTransform var19 = new AffineTransform();
            var19.setToRotation(0.7853981633974483 * var6.nextDouble() * (double)(var6.nextBoolean() ? 1 : -1), (double)(w / var4 * var18 + var15 / 2), (double)(h / 2));
            var7.setTransform(var19);
            var7.drawChars(var23, var18, 1, (w - 10) / var4 * var18 + 5, h / 2 + var15 / 2 - 10);
        }

        var7.dispose();
        ImageIO.write(var5, "jpg", os);
    }

    private static Color a(int var0, int var1) {
        if (var0 > 255) {
            var0 = 255;
        }

        if (var1 > 255) {
            var1 = 255;
        }

        int var2 = var0 + aP.nextInt(var1 - var0);
        int var3 = var0 + aP.nextInt(var1 - var0);
        int var4 = var0 + aP.nextInt(var1 - var0);
        return new Color(var2, var3, var4);
    }

    private static int b() {
        int[] var0 = c();
        int var1 = 0;
        int[] var2 = var0;
        int var3 = var0.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            int var5 = var2[var4];
            var1 <<= 8;
            var1 |= var5;
        }

        return var1;
    }

    private static int[] c() {
        int[] var0 = new int[3];

        for(int var1 = 0; var1 < 3; ++var1) {
            var0[var1] = aP.nextInt(255);
        }

        return var0;
    }

    private static void a(Graphics var0, int var1, int var2, Color var3) {
        b(var0, var1, var2, var3);
        c(var0, var1, var2, var3);
    }

    private static void b(Graphics var0, int var1, int var2, Color var3) {
        int var4 = aP.nextInt(2);
        boolean var5 = true;
        byte var6 = 1;
        int var7 = aP.nextInt(2);

        for(int var8 = 0; var8 < var2; ++var8) {
            double var9 = (double)(var4 >> 1) * Math.sin((double)var8 / (double)var4 + 6.283185307179586 * (double)var7 / (double)var6);
            var0.copyArea(0, var8, var1, 1, (int)var9, 0);
            if (var5) {
                var0.setColor(var3);
                var0.drawLine((int)var9, var8, 0, var8);
                var0.drawLine((int)var9 + var1, var8, var1, var8);
            }
        }

    }

    private static void c(Graphics var0, int var1, int var2, Color var3) {
        int var4 = aP.nextInt(40) + 10;
        boolean var5 = true;
        byte var6 = 20;
        byte var7 = 7;

        for(int var8 = 0; var8 < var1; ++var8) {
            double var9 = (double)(var4 >> 1) * Math.sin((double)var8 / (double)var4 + 6.283185307179586 * (double)var7 / (double)var6);
            var0.copyArea(var8, 0, 1, var2, 0, (int)var9);
            if (var5) {
                var0.setColor(var3);
                var0.drawLine(var8, (int)var9, var8, 0);
                var0.drawLine(var8, (int)var9 + var2, var8, var2);
            }
        }

    }
}