package com.sydh.common.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.lang3.exception.ExceptionUtils;


public class ExceptionUtil {
    public static String getExceptionMessage(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter, true));
        return stringWriter.toString();
    }


    public static String getRootErrorMessage(Exception e) {
        Throwable throwable = ExceptionUtils.getRootCause(e);
        throwable = (throwable == null) ? e : throwable;
        if (throwable == null) {
            return "";
        }
        String str = throwable.getMessage();
        if (str == null) {
            return "null";
        }
        return StringUtils.defaultString(str);
    }
}
