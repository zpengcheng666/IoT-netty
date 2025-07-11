package com.sydh.common.utils.uuid;

import com.sydh.common.utils.DateUtils;
import com.sydh.common.utils.StringUtils;

import java.util.concurrent.atomic.AtomicInteger;


public class Seq {
    public static final String commSeqType = "COMMON";
    public static final String uploadSeqType = "UPLOAD";
    private static AtomicInteger cT = new AtomicInteger(1);


    private static AtomicInteger cU = new AtomicInteger(1);


    private static String cV = "A";


    public static String getId() {
        return getId("COMMON");
    }


    public static String getId(String type) {
        AtomicInteger atomicInteger = cT;
        if ("UPLOAD".equals(type)) {
            atomicInteger = cU;
        }
        return getId(atomicInteger, 3);
    }


    public static String getId(AtomicInteger atomicInt, int length) {
        String str = DateUtils.dateTimeNow();
        str = str + cV;
        str = str + a(atomicInt, length);
        return str;
    }


    private static synchronized String a(AtomicInteger paramAtomicInteger, int paramInt) {
        int i = paramAtomicInteger.getAndIncrement();


        int j = (int) Math.pow(10.0D, paramInt);
        if (paramAtomicInteger.get() >= j) {
            paramAtomicInteger.set(1);
        }

        return StringUtils.padl(Integer.valueOf(i), paramInt);
    }
}
