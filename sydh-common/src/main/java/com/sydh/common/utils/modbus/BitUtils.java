package com.sydh.common.utils.modbus;


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
        i++;
        return num >> i - 1 & 0x1;
    }


    public static int deterHex(String hex, int i) {
        return deter(Integer.parseInt(hex, 16), i);
    }

    public static void main(String[] args) {
        int i = deter(7, 0);
        int j = deterHex("10", 4);
        System.out.println(i);
        System.out.println(j);
    }
}
