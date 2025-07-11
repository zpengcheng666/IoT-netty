package com.sydh.framework.config.sharding.enums;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.sydh.framework.config.sharding.ShardingAlgorithmTool;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 分片表缓存枚举
 * 用于管理分片表的缓存和实际数据节点
 */
public enum ShardingTableCacheEnum {
    DEVICE_LOG("iot_device_log", new HashSet<>());

    private static final Map<String, ShardingTableCacheEnum> VALUE_MAP = new HashMap<>();

    private final Set<String> resultTableNamesCache;
    private final String logicTableName;

    static {
        Arrays.stream(values()).forEach(enumItem -> VALUE_MAP.put(enumItem.logicTableName, enumItem));
    }

    ShardingTableCacheEnum(String logicTableName, Set<String> resultTableNamesCache) {
        this.logicTableName = logicTableName;
        this.resultTableNamesCache = resultTableNamesCache;
    }

    /**
     * 根据逻辑表名获取枚举实例
     *
     * @param value 逻辑表名
     * @return 对应的枚举实例，如果不存在则返回null
     */
    public static ShardingTableCacheEnum of(String value) {
        return VALUE_MAP.get(value);
    }

    /**
     * 获取逻辑表名
     *
     * @return 逻辑表名
     */
    public String logicTableName() {
        return this.logicTableName;
    }

    /**
     * 获取结果表名缓存
     *
     * @return 结果表名缓存集合
     */
    public Set<String> resultTableNamesCache() {
        return this.resultTableNamesCache;
    }

    /**
     * 原子性更新缓存和实际数据节点
     *
     * @param tableNameList 表名列表
     */
    public void atomicUpdateCacheAndActualDataNodes(List<String> tableNameList) {
        if (CollectionUtils.isEmpty(tableNameList)) {
            return;
        }
        
        synchronized (this.resultTableNamesCache) {
            // 清空现有缓存
            this.resultTableNamesCache.clear();
            // 添加新的表名
            this.resultTableNamesCache.addAll(tableNameList);
            // 刷新实际数据节点
            ShardingAlgorithmTool.actualDataNodesRefresh(this.logicTableName, tableNameList);
        }
    }

    /**
     * 获取所有逻辑表名
     *
     * @return 逻辑表名集合
     */
    public static Set<String> logicTableNames() {
        return VALUE_MAP.keySet();
    }

    @Override
    public String toString() {
        return "ShardingTableCacheEnum{" +
                "logicTableName='" + this.logicTableName + '\'' +
                ", resultTableNamesCache=" + this.resultTableNamesCache +
                '}';
    }
}
