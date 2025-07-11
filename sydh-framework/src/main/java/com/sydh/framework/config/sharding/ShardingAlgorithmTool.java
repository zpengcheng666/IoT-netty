package com.sydh.framework.config.sharding;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.druid.util.StringUtils;
import com.sydh.framework.config.sharding.enums.ShardingTableCacheEnum;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

import java.util.stream.Collectors;

import org.apache.shardingsphere.driver.jdbc.core.datasource.ShardingSphereDataSource;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.mode.manager.ContextManager;
import org.apache.shardingsphere.sharding.algorithm.config.AlgorithmProvidedShardingRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.rule.ShardingTableRuleConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

public class ShardingAlgorithmTool {
    private static final Logger log = LoggerFactory.getLogger(ShardingAlgorithmTool.class);
    private static final Environment ENV = SpringUtil.getApplicationContext().getEnvironment();
    private static final String DATASOURCE_URL = ENV.getProperty("spring.shardingsphere.datasource.ds0.url");
    private static final String DATASOURCE_USERNAME = ENV.getProperty("spring.shardingsphere.datasource.ds0.username");
    private static final String DATASOURCE_PASSWORD = ENV.getProperty("spring.shardingsphere.datasource.ds0.password");


    public static Set<String> getShardingTablesAndCreate(ShardingTableCacheEnum logicTable, Collection<String> resultTableNames) {
        return resultTableNames.stream().map(o -> getShardingTableAndCreate(logicTable, o)).collect(Collectors.toSet());
    }


    public static String getShardingTableAndCreate(ShardingTableCacheEnum logicTable, String resultTableName) {
        if (logicTable.resultTableNamesCache().contains(resultTableName)) {
            return resultTableName;
        }

        boolean isSuccess = createShardingTable(logicTable, resultTableName);
        return isSuccess ? resultTableName : logicTable.logicTableName();
    }


    public static void tableNameCacheReloadAll() {
        Arrays.stream(ShardingTableCacheEnum.values()).forEach(ShardingAlgorithmTool::tableNameCacheReload);
    }


    public static void tableNameCacheReload(ShardingTableCacheEnum logicTable) {
        List<String> tableNameList = getAllTableNameBySchema(logicTable);

        logicTable.atomicUpdateCacheAndActualDataNodes(tableNameList);

        logicTable.resultTableNamesCache().clear();

        logicTable.resultTableNamesCache().addAll(tableNameList);

        actualDataNodesRefresh(logicTable.logicTableName(), tableNameList);
    }


    public static List<String> getAllTableNameBySchema(ShardingTableCacheEnum logicTable) {
        List<String> tableNames = new ArrayList<>();
        if (StringUtils.isEmpty(DATASOURCE_URL) || StringUtils.isEmpty(DATASOURCE_USERNAME) || StringUtils.isEmpty(DATASOURCE_PASSWORD)) {
            log.error(">>>>>>>>>> 【ERROR】数据库连接配置有误，请稍后重试，URL:{}, username:{}, password:{}", new Object[]{DATASOURCE_URL, DATASOURCE_USERNAME, DATASOURCE_PASSWORD});
            throw new IllegalArgumentException("数据库连接配置有误，请稍后重试");
        }
        try (Connection conn = DriverManager.getConnection(DATASOURCE_URL, DATASOURCE_USERNAME, DATASOURCE_PASSWORD);
             Statement st = conn.createStatement()) {
            String logicTableName = logicTable.logicTableName();
            try (ResultSet rs = st.executeQuery("show TABLES like '" + logicTableName + "_" + "%'")) {
                log.info("查询数据库所有表:{}", "show TABLES like '" + logicTableName + "_" + "%'");
                while (rs.next()) {
                    String tableName = rs.getString(1);
                    log.info("分表格式:{}", String.format("^(%s\\d{6})$", new Object[]{logicTableName + "_"}));

                    if (!StringUtils.isEmpty(tableName) && tableName.matches(String.format("^(%s\\d{6})$", new Object[]{logicTableName + "_"}))) {
                        tableNames.add(rs.getString(1));
                    }
                }
            }
        } catch (SQLException e) {
            log.error(">>>>>>>>>> 【ERROR】数据库连接失败，请稍后重试，原因：{}", e.getMessage(), e);
            throw new IllegalArgumentException("数据库连接失败，请稍后重试");
        }
        return tableNames;
    }


    public static void actualDataNodesRefresh(String logicTableName, List<String> tableNamesCache) {
        try {
            if (tableNamesCache == null) {
                return;
            }

            String dbName = "ds0";
            log.info(">>>>>>>>>> 【INFO】更新分表配置，logicTableName:{}，tableNamesCache:{}", logicTableName, tableNamesCache);


            String newActualDataNodes = tableNamesCache.stream().map(o -> String.format("%s.%s", new Object[]{dbName, o})).collect(Collectors.joining(","));
            ShardingSphereDataSource shardingSphereDataSource = (ShardingSphereDataSource) SpringUtil.getBean(ShardingSphereDataSource.class);
            updateShardRuleActualDataNodes(shardingSphereDataSource, logicTableName, newActualDataNodes);
        } catch (Exception e) {
            log.error("初始化 动态表单失败，原因：{}", e.getMessage(), e);
        }
    }


    private static void updateShardRuleActualDataNodes(ShardingSphereDataSource dataSource, String logicTableName, String newActualDataNodes) {
        ContextManager contextManager = dataSource.getContextManager();

        String schemaName = "logic_db";
        Collection<RuleConfiguration> newRuleConfigList = new LinkedList<>();

        Collection<RuleConfiguration> oldRuleConfigList = dataSource.getContextManager().getMetaDataContexts().getMetaData(schemaName).getRuleMetaData().getConfigurations();

        for (RuleConfiguration oldRuleConfig : oldRuleConfigList) {
            if (oldRuleConfig instanceof AlgorithmProvidedShardingRuleConfiguration) {

                AlgorithmProvidedShardingRuleConfiguration oldAlgorithmConfig = (AlgorithmProvidedShardingRuleConfiguration) oldRuleConfig;
                AlgorithmProvidedShardingRuleConfiguration newAlgorithmConfig = new AlgorithmProvidedShardingRuleConfiguration();

                Collection<ShardingTableRuleConfiguration> newTableRuleConfigList = new LinkedList<>();
                Collection<ShardingTableRuleConfiguration> oldTableRuleConfigList = oldAlgorithmConfig.getTables();

                oldTableRuleConfigList.forEach(oldTableRuleConfig -> {
                    if (logicTableName.equals(oldTableRuleConfig.getLogicTable())) {
                        ShardingTableRuleConfiguration newTableRuleConfig = new ShardingTableRuleConfiguration(oldTableRuleConfig.getLogicTable(), newActualDataNodes);

                        newTableRuleConfig.setTableShardingStrategy(oldTableRuleConfig.getTableShardingStrategy());

                        newTableRuleConfig.setDatabaseShardingStrategy(oldTableRuleConfig.getDatabaseShardingStrategy());
                        newTableRuleConfig.setKeyGenerateStrategy(oldTableRuleConfig.getKeyGenerateStrategy());
                        newTableRuleConfigList.add(newTableRuleConfig);
                    } else {
                        newTableRuleConfigList.add(oldTableRuleConfig);
                    }
                });
                newAlgorithmConfig.setTables(newTableRuleConfigList);
                newAlgorithmConfig.setAutoTables(oldAlgorithmConfig.getAutoTables());
                newAlgorithmConfig.setBindingTableGroups(oldAlgorithmConfig.getBindingTableGroups());
                newAlgorithmConfig.setBroadcastTables(oldAlgorithmConfig.getBroadcastTables());
                newAlgorithmConfig.setDefaultDatabaseShardingStrategy(oldAlgorithmConfig.getDefaultDatabaseShardingStrategy());
                newAlgorithmConfig.setDefaultTableShardingStrategy(oldAlgorithmConfig.getDefaultTableShardingStrategy());
                newAlgorithmConfig.setDefaultKeyGenerateStrategy(oldAlgorithmConfig.getDefaultKeyGenerateStrategy());
                newAlgorithmConfig.setDefaultShardingColumn(oldAlgorithmConfig.getDefaultShardingColumn());
                newAlgorithmConfig.setShardingAlgorithms(oldAlgorithmConfig.getShardingAlgorithms());
                newAlgorithmConfig.setKeyGenerators(oldAlgorithmConfig.getKeyGenerators());
                newRuleConfigList.add(newAlgorithmConfig);
            }
        }

        contextManager.alterRuleConfiguration(schemaName, newRuleConfigList);
    }


    private static boolean createShardingTable(ShardingTableCacheEnum logicTable, String resultTableName) {
        String month = resultTableName.replace(logicTable.logicTableName() + "_", "");
        YearMonth shardingMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyyMM"));
        if (shardingMonth.isAfter(YearMonth.now())) {
            return false;
        }

        synchronized (logicTable.logicTableName().intern()) {

            if (logicTable.resultTableNamesCache().contains(resultTableName)) {
                return false;
            }

            executeSql(Collections.singletonList("CREATE TABLE IF NOT EXISTS `" + resultTableName + "` LIKE `" + logicTable.logicTableName() + "`;"));

            tableNameCacheReload(logicTable);
        }
        return true;
    }


    private static void executeSql(List<String> sqlList) {
        if (StringUtils.isEmpty(DATASOURCE_URL) || StringUtils.isEmpty(DATASOURCE_USERNAME) || StringUtils.isEmpty(DATASOURCE_PASSWORD)) {
            log.error(">>>>>>>>>> 【ERROR】数据库连接配置有误，请稍后重试，URL:{}, username:{}, password:{}", new Object[]{DATASOURCE_URL, DATASOURCE_USERNAME, DATASOURCE_PASSWORD});
            throw new IllegalArgumentException("数据库连接配置有误，请稍后重试");
        }
        try (Connection conn = DriverManager.getConnection(DATASOURCE_URL, DATASOURCE_USERNAME, DATASOURCE_PASSWORD)) {
            try (Statement st = conn.createStatement()) {
                conn.setAutoCommit(false);
                for (String sql : sqlList) {
                    st.execute(sql);
                }
            } catch (Exception e) {
                conn.rollback();
                log.error(">>>>>>>>>> 【ERROR】数据表创建执行失败，请稍后重试，原因：{}", e.getMessage(), e);
                throw new IllegalArgumentException("数据表创建执行失败，请稍后重试");
            }
        } catch (SQLException e) {
            log.error(">>>>>>>>>> 【ERROR】数据库连接失败，请稍后重试，原因：{}", e.getMessage(), e);
            throw new IllegalArgumentException("数据库连接失败，请稍后重试");
        }
    }
}
