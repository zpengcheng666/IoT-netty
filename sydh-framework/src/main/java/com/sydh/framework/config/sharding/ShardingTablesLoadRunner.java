package com.sydh.framework.config.sharding;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;


@Component
@ConditionalOnProperty(prefix = "spring.shardingsphere", name = {"enabled"}, havingValue = "true", matchIfMissing = true)
public class ShardingTablesLoadRunner
        implements CommandLineRunner {
    @Override
    public void run(String... args) {
        ShardingAlgorithmTool.tableNameCacheReloadAll();
    }
}

