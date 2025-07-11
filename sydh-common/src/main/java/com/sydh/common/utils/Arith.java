package com.sydh.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class Arith {
    private static final int al = 10;

    public static double add(double v1, double v2) {
        BigDecimal bigDecimal1 = new BigDecimal(Double.toString(v1));
        BigDecimal bigDecimal2 = new BigDecimal(Double.toString(v2));
        return bigDecimal1.add(bigDecimal2).doubleValue();
    }


    public static double sub(double v1, double v2) {
        BigDecimal bigDecimal1 = new BigDecimal(Double.toString(v1));
        BigDecimal bigDecimal2 = new BigDecimal(Double.toString(v2));
        return bigDecimal1.subtract(bigDecimal2).doubleValue();
    }


    public static double mul(double v1, double v2) {
        BigDecimal bigDecimal1 = new BigDecimal(Double.toString(v1));
        BigDecimal bigDecimal2 = new BigDecimal(Double.toString(v2));
        return bigDecimal1.multiply(bigDecimal2).doubleValue();
    }


    public static double div(double v1, double v2) {
        return div(v1, v2, 10);
    }


    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }

        BigDecimal bigDecimal1 = new BigDecimal(Double.toString(v1));
        BigDecimal bigDecimal2 = new BigDecimal(Double.toString(v2));
        if (bigDecimal1.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.doubleValue();
        }
        return bigDecimal1.divide(bigDecimal2, scale, RoundingMode.HALF_UP).doubleValue();
    }


    public static double round(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }

        BigDecimal bigDecimal1 = new BigDecimal(Double.toString(v));
        BigDecimal bigDecimal2 = BigDecimal.ONE;
        return bigDecimal1.divide(bigDecimal2, scale, RoundingMode.HALF_UP).doubleValue();
    }
}
