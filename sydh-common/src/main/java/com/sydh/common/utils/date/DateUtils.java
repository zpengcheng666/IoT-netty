package com.sydh.common.utils.date;

import cn.hutool.core.date.LocalDateTimeUtil;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;


public class DateUtils {
    public static final String TIME_ZONE_DEFAULT = "GMT+8";
    public static final long SECOND_MILLIS = 1000L;
    public static final String FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_HOUR_MINUTE_SECOND = "HH:mm:ss";

    public static Date of(LocalDateTime date) {
        ZonedDateTime zonedDateTime = date.atZone(ZoneId.systemDefault());

        Instant instant = zonedDateTime.toInstant();

        return Date.from(instant);
    }


    public static LocalDateTime of(Date date) {
        Instant instant = date.toInstant();

        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    @Deprecated
    public static Date addTime(Duration duration) {
        return new Date(System.currentTimeMillis() + duration.toMillis());
    }

    public static boolean isExpired(Date time) {
        return (System.currentTimeMillis() > time.getTime());
    }

    public static boolean isExpired(LocalDateTime time) {
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.isAfter(time);
    }

    public static long diff(Date endTime, Date startTime) {
        return endTime.getTime() - startTime.getTime();
    }


    public static Date buildTime(int year, int mouth, int day) {
        return buildTime(year, mouth, day, 0, 0, 0);
    }


    public static Date buildTime(int year, int mouth, int day, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1, year);
        calendar.set(2, mouth - 1);
        calendar.set(5, day);
        calendar.set(11, hour);
        calendar.set(12, minute);
        calendar.set(13, second);
        calendar.set(14, 0);
        return calendar.getTime();
    }

    public static Date max(Date a, Date b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return (a.compareTo(b) > 0) ? a : b;
    }

    public static LocalDateTime max(LocalDateTime a, LocalDateTime b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return a.isAfter(b) ? a : b;
    }


    public static Date addDate(int field, int amount) {
        return addDate(null, field, amount);
    }


    public static Date addDate(Date date, int field, int amount) {
        if (amount == 0) {
            return date;
        }
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        calendar.add(field, amount);
        return calendar.getTime();
    }


    public static boolean isToday(LocalDateTime date) {
        return LocalDateTimeUtil.isSameDay(date, LocalDateTime.now());
    }
}
