package com.sydh.common.mybatis.enums;

import com.sydh.common.utils.StringUtils;

public enum DataBaseType {
    MY_SQL("MySQL"),
    ORACLE("Oracle"),
    POSTGRE_SQL("PostgreSQL"),
    SQL_SERVER("Microsoft SQL Server"),
    DM("DM DBMS");

    public String getType() {
        return this.type;
    }

    DataBaseType(String type) {
        this.type = type;
    }

    public static DataBaseType find(String databaseProductName) {
        if (StringUtils.isBlank(databaseProductName)) {
            return null;
        }
        for (DataBaseType dataBaseType : values()) {
            if (dataBaseType.getType().equals(databaseProductName)) {
                return dataBaseType;
            }
        }
        return null;
    }

    private final String type;
}
