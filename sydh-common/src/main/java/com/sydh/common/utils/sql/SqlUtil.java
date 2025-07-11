package com.sydh.common.utils.sql;

import com.sydh.common.exception.UtilException;
import com.sydh.common.utils.StringUtils;


public class SqlUtil {
    public static String SQL_REGEX = "and |extractvalue|updatexml|exec |insert |select |delete |update |drop |count |chr |mid |master |truncate |char |declare |or |+|user()";


    public static String SQL_PATTERN = "[a-zA-Z0-9_\\ \\,\\.]+";


    public static String escapeOrderBySql(String value) {
        if (StringUtils.isNotEmpty(value) && !isValidOrderBySql(value)) {
            throw new UtilException("参数不符合规范，不能进行查询");
        }
        return value;
    }


    public static boolean isValidOrderBySql(String value) {
        return value.matches(SQL_PATTERN);
    }


    public static void filterKeyword(String value) {
        if (StringUtils.isEmpty(value)) {
            return;
        }

        String[] arrayOfString = StringUtils.split(SQL_REGEX, "\\|");
        for (String str : arrayOfString) {

            if (StringUtils.indexOfIgnoreCase(value, str) > -1) {
                throw new UtilException("参数存在SQL注入风险");
            }
        }
    }
}
