package com.sydh.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Excel {
    int sort() default 2147483647;

    String name() default "";

    String dateFormat() default "";

    String dictType() default "";

    String readConverterExp() default "";

    String separator() default ",";

    int scale() default -1;

    int roundingMode() default 6;

    double height() default 14.0D;

    double width() default 16.0D;

    String suffix() default "";

    String defaultValue() default "";

    String prompt() default "";

    String[] combo() default {};

    boolean needMerge() default false;

    boolean isExport() default true;

    String targetAttr() default "";

    boolean isStatistics() default false;

    ColumnType cellType() default ColumnType.STRING;

    IndexedColors headerBackgroundColor() default IndexedColors.GREY_50_PERCENT;

    IndexedColors headerColor() default IndexedColors.WHITE;

    IndexedColors backgroundColor() default IndexedColors.WHITE;

    IndexedColors color() default IndexedColors.BLACK;

    HorizontalAlignment align() default HorizontalAlignment.CENTER;

    Class<?> handler() default com.sydh.common.utils.poi.ExcelHandlerAdapter.class;

    String[] args() default {};

    Type type() default Type.ALL;

    public enum Type {
        ALL(0), EXPORT(1), IMPORT(2);

        private final int value;

        Type(int value) {
            this.value = value;
        }


        public int value() {
            return this.value;
        }
    }

    public enum ColumnType {
        NUMERIC(0), STRING(1), IMAGE(2);

        private final int value;

        ColumnType(int value) {
            this.value = value;
        }


        public int value() {
            return this.value;
        }
    }
}
