package com.sydh.framework.mybatis.helper;

import cn.hutool.core.convert.Convert;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.sydh.common.exception.ServiceException;
import com.sydh.common.mybatis.enums.DataBaseType;
import com.sydh.common.utils.spring.SpringUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;


public class DataBaseHelper {
    private static final DynamicRoutingDataSource DS = (DynamicRoutingDataSource) SpringUtils.getBean(DynamicRoutingDataSource.class);


    public static final String DEFAULT_DATASOURCE_NAME = "master";


    public static DataBaseType getDataBaseType(String dataName) {
        DataSource dataSource = (DataSource) DS.getDataSources().get(dataName);
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            String databaseProductName = metaData.getDatabaseProductName();
            return DataBaseType.find(databaseProductName);
        } catch (SQLException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public static boolean isMySql() {
        return (DataBaseType.MY_SQL == getDataBaseType("master"));
    }

    public static boolean isOracle() {
        return (DataBaseType.ORACLE == getDataBaseType("master"));
    }

    public static boolean isPostgerSql() {
        return (DataBaseType.POSTGRE_SQL == getDataBaseType("master"));
    }

    public static boolean isSqlServer() {
        return (DataBaseType.SQL_SERVER == getDataBaseType("master"));
    }

    public static boolean isDm() {
        return (DataBaseType.DM == getDataBaseType("master"));
    }

    public static boolean isMySql(String dataName) {
        return (DataBaseType.MY_SQL == getDataBaseType(dataName));
    }

    public static boolean isOracle(String dataName) {
        return (DataBaseType.ORACLE == getDataBaseType(dataName));
    }

    public static boolean isPostgerSql(String dataName) {
        return (DataBaseType.POSTGRE_SQL == getDataBaseType(dataName));
    }

    public static boolean isSqlServer(String dataName) {
        return (DataBaseType.SQL_SERVER == getDataBaseType(dataName));
    }

    public static boolean isDm(String dataName) {
        return (DataBaseType.DM == getDataBaseType(dataName));
    }

    public static String findInSet(Object var1, String var2) {
        DataBaseType dataBasyType = getDataBaseType("master");
        String var = Convert.toStr(var1);
        if (dataBasyType == DataBaseType.SQL_SERVER) {
            return String.format("charindex(',%s,' , ','+%s+',') <> 0", new Object[]{var, var2});
        }
        if (dataBasyType == DataBaseType.POSTGRE_SQL) {
            return String.format("(select strpos(','||%s||',' , ',%s,')) <> 0", new Object[]{var2, var});
        }
        if (dataBasyType == DataBaseType.ORACLE || dataBasyType == DataBaseType.DM) {
            return String.format("instr(','||%s||',' , ',%s,') <> 0", new Object[]{var2, var});
        }

        return String.format("find_in_set(%s , %s) <> 0", new Object[]{var, var2});
    }

    public static String findInSetColumn(String var1, String var2) {
        DataBaseType dataBasyType = getDataBaseType("master");
        String var = Convert.toStr(var1);
        if (dataBasyType == DataBaseType.SQL_SERVER) {
            return String.format("charindex(',' + CAST(%s AS VARCHAR(50)) + ',' , ',' + CAST('%s' AS VARCHAR(500)) + ',') <> 0", new Object[]{var, var2});
        }
        if (dataBasyType == DataBaseType.POSTGRE_SQL) {
            return String.format("(select strpos(','||%s||',' , ','|| %s ||',')) <> 0", new Object[]{var2, var});
        }
        if (dataBasyType == DataBaseType.ORACLE || dataBasyType == DataBaseType.DM) {
            return String.format("instr(','||%s||',' , ','||%s||',') <> 0", new Object[]{var2, var});
        }

        return String.format("find_in_set(%s , '%s') <> 0", new Object[]{var, var2});
    }


    public static List<String> getDataSourceNameList() {
        return new ArrayList<>(DS.getDataSources().keySet());
    }

    public static String getDeptCondition(Long deptId) {
        if (deptId == null || deptId.longValue() == 0L) {
            return "1=1";
        }
        if (isPostgerSql()) {
            return "SELECT u.user_id FROM sys_user u WHERE u.dept_id IN (SELECT dept_id FROM sys_dept WHERE " + deptId + "::text = ANY(string_to_array(ancestors, ',')) OR dept_id = " + deptId + ")";
        }
        if (isSqlServer()) {
            return "SELECT u.user_id FROM sys_user u WHERE u.dept_id IN (SELECT dept_id FROM sys_dept WHERE CHARINDEX(',' + CAST(" + deptId + " AS VARCHAR) + ',', ',' + ancestors + ',') > 0 OR dept_id = " + deptId + ")";
        }
        if (isOracle()) {
            return "SELECT u.user_id FROM sys_user u WHERE u.dept_id IN (SELECT dept_id FROM sys_dept WHERE INSTR(',' || ancestors || ',', ',' || " + deptId + " || ',') > 0 OR dept_id = " + deptId + ")";
        }
        if (isDm()) {
            return "SELECT u.user_id FROM sys_user u WHERE u.dept_id IN (SELECT dept_id FROM sys_dept WHERE INSTR(',' || ancestors || ',', ',' || " + deptId + " || ',') > 0 OR dept_id = " + deptId + ")";
        }
        if (isMySql()) {
            return "SELECT u.user_id FROM sys_user u WHERE u.dept_id IN (SELECT dept_id FROM sys_dept WHERE FIND_IN_SET(" + deptId + ", ancestors) > 0 OR dept_id = " + deptId + ")";
        }
        throw new UnsupportedOperationException("Unsupported database type");
    }


    public static String checkTime(Integer timeout) {
        if (timeout == null || timeout.intValue() == 0) {
            return "";
        }
        if (isPostgerSql()) {
            return "CURRENT_TIMESTAMP > last_connect_time + interval '1 seconds' * " + timeout;
        }
        if (isSqlServer()) {
            return "CURRENT_TIMESTAMP > DATEADD(SECOND, " + timeout + " last_connect_time)";
        }
        if (isOracle()) {
            return "CURRENT_TIMESTAMP > last_connect_time + (" + timeout + " / 86400)";
        }
        if (isDm()) {
            return "CURRENT_TIMESTAMP > DATEADD(SECOND, " + timeout + ", last_connect_time)";
        }
        if (isMySql()) {
            return "CURRENT_TIMESTAMP > DATE_ADD(last_connect_time, INTERVAL " + timeout + " SECOND)";
        }
        throw new UnsupportedOperationException("Unsupported database type");
    }
}
