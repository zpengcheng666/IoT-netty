/*     */ package com.fastbee.framework.config.sharding;
/*     */ import com.fastbee.framework.config.sharding.enums.ShardingTableCacheEnum;
/*     */ import com.google.common.collect.Range;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.time.Instant;
/*     */ import java.time.LocalDateTime;
/*     */ import java.time.ZoneId;
/*     */ import java.time.ZonedDateTime;
/*     */ import java.time.format.DateTimeFormatter;
/*     */ import java.util.*;
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ import java.util.function.Function;
/*     */ import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
/*     */ import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
/*     */ import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import org.springframework.util.CollectionUtils;
/*     */ 
/*     */ public class TimeShardingAlgorithm implements StandardShardingAlgorithm<Date> {
/*  25 */   private static final Logger log = LoggerFactory.getLogger(TimeShardingAlgorithm.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  30 */   private static final SimpleDateFormat TABLE_SHARD_Date_FORMATTER = new SimpleDateFormat("yyyyMM");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  35 */   private static final DateTimeFormatter TABLE_SHARD_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  40 */   private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss");
/*     */ 
/*     */ 
/*     */   
/*  44 */   private static final SimpleDateFormat DATE_TIME_FORMATTER_SPILE = new SimpleDateFormat("yyyy-MM-dd");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  49 */   private final String TABLE_SPLIT_SYMBOL = "_";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String doSharding(Collection<String> tableNames, PreciseShardingValue<Date> preciseShardingValue) {
/*  60 */     String logicTableName = preciseShardingValue.getLogicTableName();
/*  61 */     ShardingTableCacheEnum logicTable = ShardingTableCacheEnum.of(logicTableName);
/*  62 */     createAllTable(logicTable, tableNames);
/*     */ 
/*     */     
/*  65 */     log.info(">>>>>>>>>> 【INFO】精确分片，节点配置表名：{}，数据库缓存表名：{}", tableNames, logicTable.resultTableNamesCache());
/*     */     
/*  67 */     Date date = (Date)preciseShardingValue.getValue();
/*  68 */     Instant instant = date.toInstant();
/*  69 */     LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
/*  70 */     String resultTableName = logicTableName + "_" + TABLE_SHARD_TIME_FORMATTER.format(localDateTime);
/*     */     
/*  72 */     if (!tableNames.contains(resultTableName)) {
/*  73 */       tableNames.add(resultTableName);
/*     */     }
/*  75 */     return ShardingAlgorithmTool.getShardingTableAndCreate(logicTable, resultTableName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection<String> doSharding(Collection<String> tableNames, RangeShardingValue<Date> rangeShardingValue) {
/*  86 */     log.info("开始分表查询开始:{}", Long.valueOf(System.currentTimeMillis()));
/*  87 */     String logicTableName = rangeShardingValue.getLogicTableName();
/*  88 */     ShardingTableCacheEnum logicTable = ShardingTableCacheEnum.of(logicTableName);
/*  89 */     createAllTable(logicTable, tableNames);
/*     */ 
/*     */     
/*  92 */     log.info(">>>>>>>>>> 【INFO】范围分片，节点配置表名：{}，数据库缓存表名：{}", tableNames, logicTable.resultTableNamesCache());
/*     */ 
/*     */     
/*  95 */     Range<Date> valueRange = rangeShardingValue.getValueRange();
/*  96 */     boolean hasLowerBound = valueRange.hasLowerBound();
/*  97 */     boolean hasUpperBound = valueRange.hasUpperBound();
/*     */ 
/*     */     
/* 100 */     Set<String> tableNameCache = logicTable.resultTableNamesCache();
/* 101 */     String min = hasLowerBound ? String.valueOf(valueRange.lowerEndpoint()) : getLowerEndpoint(tableNameCache);
/* 102 */     String max = hasUpperBound ? String.valueOf(valueRange.upperEndpoint()) : getUpperEndpoint(tableNameCache);
/*     */     
/* 104 */     Set<String> resultTableNames = new LinkedHashSet<>();
/*     */     try {
/* 106 */       Date minDate = DATE_TIME_FORMATTER_SPILE.parse(min);
/* 107 */       Date maxDate = DATE_TIME_FORMATTER_SPILE.parse(max);
/* 108 */       Calendar calendar = Calendar.getInstance();
/* 109 */       while (minDate.before(maxDate) || minDate.equals(maxDate)) {
/* 110 */         String tableName = logicTableName + "_" + TABLE_SHARD_Date_FORMATTER.format(minDate);
/* 111 */         resultTableNames.add(tableName);
/* 112 */         calendar.setTime(minDate);
/* 113 */         calendar.add(5, 1);
/* 114 */         minDate = calendar.getTime();
/*     */       } 
/* 116 */       log.info("开始分表查询结束:{}", Long.valueOf(System.currentTimeMillis()));
/* 117 */       return ShardingAlgorithmTool.getShardingTablesAndCreate(logicTable, resultTableNames);
/* 118 */     } catch (Exception e) {
/* 119 */       return ShardingAlgorithmTool.getShardingTablesAndCreate(logicTable, logicTable.resultTableNamesCache());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void init() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public String getType() {
/* 131 */     return null;
/*     */   }
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
/*     */ 
/*     */   
/*     */   private String getLowerEndpoint(Collection<String> tableNames) {
/* 146 */     Optional<LocalDateTime> optional = tableNames.stream().map(o -> LocalDateTime.parse(o.replace("_", "") + "01 00:00:00", DATE_TIME_FORMATTER)).min(Comparator.comparing((Function)Function.identity()));
/* 147 */     if (optional.isPresent()) {
/* 148 */       ZonedDateTime zonedDateTime = ((LocalDateTime)optional.get()).atZone(ZoneId.systemDefault());
/* 149 */       Instant instant = zonedDateTime.toInstant();
/* 150 */       return String.valueOf(Date.from(instant));
/*     */     } 
/* 152 */     log.error(">>>>>>>>>> 【ERROR】获取数据最小分表失败，请稍后重试，tableName：{}", tableNames);
/* 153 */     throw new IllegalArgumentException("获取数据最小分表失败，请稍后重试");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String getUpperEndpoint(Collection<String> tableNames) {
/* 165 */     Optional<LocalDateTime> optional = tableNames.stream().map(o -> LocalDateTime.parse(o.replace("_", "") + "01 00:00:00", DATE_TIME_FORMATTER)).max(Comparator.comparing((Function)Function.identity()));
/* 166 */     if (optional.isPresent()) {
/* 167 */       ZonedDateTime zonedDateTime = ((LocalDateTime)optional.get()).atZone(ZoneId.systemDefault());
/* 168 */       Instant instant = zonedDateTime.toInstant();
/* 169 */       return String.valueOf(Date.from(instant));
/*     */     } 
/* 171 */     log.error(">>>>>>>>>> 【ERROR】获取数据最大分表失败，请稍后重试，tableName：{}", tableNames);
/* 172 */     throw new IllegalArgumentException("获取数据最大分表失败，请稍后重试");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void createAllTable(ShardingTableCacheEnum logicTable, Collection<String> tableNames) {
/* 182 */     if (!CollectionUtils.isEmpty(logicTable.resultTableNamesCache())) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 187 */     ShardingAlgorithmTool.getShardingTablesAndCreate(logicTable, tableNames);
/*     */     
/* 189 */     ShardingAlgorithmTool.tableNameCacheReload(logicTable);
/*     */   }
/*     */ }


/* Location:              D:\repository\fastbee-framework\fastbee-framework-2.6.0.jar!\com\fastbee\framework\config\sharding\TimeShardingAlgorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */