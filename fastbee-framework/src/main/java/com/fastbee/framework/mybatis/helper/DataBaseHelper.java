/*     */ package com.fastbee.framework.mybatis.helper;
/*     */ 
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
/*     */ import com.fastbee.common.exception.ServiceException;
/*     */ import com.fastbee.common.mybatis.enums.DataBaseType;
/*     */ import com.fastbee.common.utils.spring.SpringUtils;
/*     */ import java.sql.Connection;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.sql.DataSource;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DataBaseHelper
/*     */ {
/*  26 */   private static final DynamicRoutingDataSource DS = (DynamicRoutingDataSource)SpringUtils.getBean(DynamicRoutingDataSource.class);
/*     */ 
/*     */   
/*     */   public static final String DEFAULT_DATASOURCE_NAME = "master";
/*     */ 
/*     */   
/*     */   public static DataBaseType getDataBaseType(String dataName) {
/*  33 */     DataSource dataSource = (DataSource)DS.getDataSources().get(dataName);
/*  34 */     try (Connection conn = dataSource.getConnection()) {
/*  35 */       DatabaseMetaData metaData = conn.getMetaData();
/*  36 */       String databaseProductName = metaData.getDatabaseProductName();
/*  37 */       return DataBaseType.find(databaseProductName);
/*  38 */     } catch (SQLException e) {
/*  39 */       throw new ServiceException(e.getMessage());
/*     */     } 
/*     */   }
/*     */   
/*     */   public static boolean isMySql() {
/*  44 */     return (DataBaseType.MY_SQL == getDataBaseType("master"));
/*     */   }
/*     */   
/*     */   public static boolean isOracle() {
/*  48 */     return (DataBaseType.ORACLE == getDataBaseType("master"));
/*     */   }
/*     */   
/*     */   public static boolean isPostgerSql() {
/*  52 */     return (DataBaseType.POSTGRE_SQL == getDataBaseType("master"));
/*     */   }
/*     */   
/*     */   public static boolean isSqlServer() {
/*  56 */     return (DataBaseType.SQL_SERVER == getDataBaseType("master"));
/*     */   }
/*     */   
/*     */   public static boolean isDm() {
/*  60 */     return (DataBaseType.DM == getDataBaseType("master"));
/*     */   }
/*     */   
/*     */   public static boolean isMySql(String dataName) {
/*  64 */     return (DataBaseType.MY_SQL == getDataBaseType(dataName));
/*     */   }
/*     */   
/*     */   public static boolean isOracle(String dataName) {
/*  68 */     return (DataBaseType.ORACLE == getDataBaseType(dataName));
/*     */   }
/*     */   
/*     */   public static boolean isPostgerSql(String dataName) {
/*  72 */     return (DataBaseType.POSTGRE_SQL == getDataBaseType(dataName));
/*     */   }
/*     */   
/*     */   public static boolean isSqlServer(String dataName) {
/*  76 */     return (DataBaseType.SQL_SERVER == getDataBaseType(dataName));
/*     */   }
/*     */   
/*     */   public static boolean isDm(String dataName) {
/*  80 */     return (DataBaseType.DM == getDataBaseType(dataName));
/*     */   }
/*     */   
/*     */   public static String findInSet(Object var1, String var2) {
/*  84 */     DataBaseType dataBasyType = getDataBaseType("master");
/*  85 */     String var = Convert.toStr(var1);
/*  86 */     if (dataBasyType == DataBaseType.SQL_SERVER)
/*     */     {
/*  88 */       return String.format("charindex(',%s,' , ','+%s+',') <> 0", new Object[] { var, var2 }); } 
/*  89 */     if (dataBasyType == DataBaseType.POSTGRE_SQL)
/*     */     {
/*  91 */       return String.format("(select strpos(','||%s||',' , ',%s,')) <> 0", new Object[] { var2, var }); } 
/*  92 */     if (dataBasyType == DataBaseType.ORACLE || dataBasyType == DataBaseType.DM)
/*     */     {
/*  94 */       return String.format("instr(','||%s||',' , ',%s,') <> 0", new Object[] { var2, var });
/*     */     }
/*     */     
/*  97 */     return String.format("find_in_set(%s , %s) <> 0", new Object[] { var, var2 });
/*     */   }
/*     */   
/*     */   public static String findInSetColumn(String var1, String var2) {
/* 101 */     DataBaseType dataBasyType = getDataBaseType("master");
/* 102 */     String var = Convert.toStr(var1);
/* 103 */     if (dataBasyType == DataBaseType.SQL_SERVER)
/*     */     {
/* 105 */       return String.format("charindex(',' + CAST(%s AS VARCHAR(50)) + ',' , ',' + CAST('%s' AS VARCHAR(500)) + ',') <> 0", new Object[] { var, var2 }); } 
/* 106 */     if (dataBasyType == DataBaseType.POSTGRE_SQL)
/*     */     {
/* 108 */       return String.format("(select strpos(','||%s||',' , ','|| %s ||',')) <> 0", new Object[] { var2, var }); } 
/* 109 */     if (dataBasyType == DataBaseType.ORACLE || dataBasyType == DataBaseType.DM)
/*     */     {
/* 111 */       return String.format("instr(','||%s||',' , ','||%s||',') <> 0", new Object[] { var2, var });
/*     */     }
/*     */     
/* 114 */     return String.format("find_in_set(%s , '%s') <> 0", new Object[] { var, var2 });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<String> getDataSourceNameList() {
/* 121 */     return new ArrayList<>(DS.getDataSources().keySet());
/*     */   }
/*     */   
/*     */   public static String getDeptCondition(Long deptId) {
/* 125 */     if (deptId == null || deptId.longValue() == 0L)
/*     */     {
/* 127 */       return "1=1";
/*     */     }
/* 129 */     if (isPostgerSql())
/* 130 */       return "SELECT u.user_id FROM sys_user u WHERE u.dept_id IN (SELECT dept_id FROM sys_dept WHERE " + deptId + "::text = ANY(string_to_array(ancestors, ',')) OR dept_id = " + deptId + ")"; 
/* 131 */     if (isSqlServer())
/* 132 */       return "SELECT u.user_id FROM sys_user u WHERE u.dept_id IN (SELECT dept_id FROM sys_dept WHERE CHARINDEX(',' + CAST(" + deptId + " AS VARCHAR) + ',', ',' + ancestors + ',') > 0 OR dept_id = " + deptId + ")"; 
/* 133 */     if (isOracle())
/* 134 */       return "SELECT u.user_id FROM sys_user u WHERE u.dept_id IN (SELECT dept_id FROM sys_dept WHERE INSTR(',' || ancestors || ',', ',' || " + deptId + " || ',') > 0 OR dept_id = " + deptId + ")"; 
/* 135 */     if (isDm())
/* 136 */       return "SELECT u.user_id FROM sys_user u WHERE u.dept_id IN (SELECT dept_id FROM sys_dept WHERE INSTR(',' || ancestors || ',', ',' || " + deptId + " || ',') > 0 OR dept_id = " + deptId + ")"; 
/* 137 */     if (isMySql()) {
/* 138 */       return "SELECT u.user_id FROM sys_user u WHERE u.dept_id IN (SELECT dept_id FROM sys_dept WHERE FIND_IN_SET(" + deptId + ", ancestors) > 0 OR dept_id = " + deptId + ")";
/*     */     }
/* 140 */     throw new UnsupportedOperationException("Unsupported database type");
/*     */   }
/*     */ 
/*     */   
/*     */   public static String checkTime(Integer timeout) {
/* 145 */     if (timeout == null || timeout.intValue() == 0)
/*     */     {
/* 147 */       return "";
/*     */     }
/* 149 */     if (isPostgerSql())
/* 150 */       return "CURRENT_TIMESTAMP > last_connect_time + interval '1 seconds' * " + timeout; 
/* 151 */     if (isSqlServer())
/* 152 */       return "CURRENT_TIMESTAMP > DATEADD(SECOND, " + timeout + " last_connect_time)"; 
/* 153 */     if (isOracle())
/* 154 */       return "CURRENT_TIMESTAMP > last_connect_time + (" + timeout + " / 86400)"; 
/* 155 */     if (isDm())
/* 156 */       return "CURRENT_TIMESTAMP > DATEADD(SECOND, " + timeout + ", last_connect_time)"; 
/* 157 */     if (isMySql()) {
/* 158 */       return "CURRENT_TIMESTAMP > DATE_ADD(last_connect_time, INTERVAL " + timeout + " SECOND)";
/*     */     }
/* 160 */     throw new UnsupportedOperationException("Unsupported database type");
/*     */   }
/*     */ }


/* Location:              D:\repository\fastbee-framework\fastbee-framework-2.6.0.jar!\com\fastbee\framework\mybatis\helper\DataBaseHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */