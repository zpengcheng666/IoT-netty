package com.sydh.common.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;


public class ExceptionUtils {
    public static Throwable getThrowable(HttpServletRequest request) {
        Throwable throwable = null;
        if (request.getAttribute("exception") != null) {
            throwable = (Throwable) request.getAttribute("exception");
        } else if (request.getAttribute("javax.servlet.error.exception") != null) {
            throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
        }

        return throwable;
    }

    public static String getStackTraceAsString(Throwable e) {
        if (e == null) {
            return "";
        }
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }


    public static boolean isCausedBy(Exception ex, Class... causeExceptionClasses) {
        for (Throwable throwable = ex.getCause(); throwable != null; throwable = throwable.getCause()) {
            Class[] arrayOfClass = causeExceptionClasses;
            int i = causeExceptionClasses.length;

            for (byte b = 0; b < i; b++) {
                Class clazz = arrayOfClass[b];
                if (clazz.isInstance(throwable)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static RuntimeException unchecked(Exception e) {
        return (e instanceof RuntimeException) ? (RuntimeException) e : new RuntimeException(e);
    }
}
