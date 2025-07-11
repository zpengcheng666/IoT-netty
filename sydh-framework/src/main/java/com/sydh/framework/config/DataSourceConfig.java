package com.sydh.framework.config;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.dynamic.datasource.provider.AbstractDataSourceProvider;
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;


@Configuration
public class DataSourceConfig {
    private final DynamicDataSourceProperties properties;
    private final DefaultDataSourceCreator dataSourceCreator;
    private final DataSource shardingSphereDataSource;

    /**
     * 构造数据源配置实例
     *
     * @param properties               动态数据源配置属性，不可为 null
     * @param dataSourceCreator        默认数据源创建器，不可为 null
     * @param shardingSphereDataSource 分片数据源，不可为 null
     */
    public DataSourceConfig(DynamicDataSourceProperties properties,
                            DefaultDataSourceCreator dataSourceCreator,
                            @Lazy @Qualifier("shardingSphereDataSource") DataSource shardingSphereDataSource) {
        if (properties == null) {
            throw new IllegalArgumentException("DynamicDataSourceProperties 不能为 null");
        }
        if (dataSourceCreator == null) {
            throw new IllegalArgumentException("DefaultDataSourceCreator 不能为 null");
        }
        if (shardingSphereDataSource == null) {
            throw new IllegalArgumentException("ShardingSphere 数据源不能为 null");
        }

        this.properties = properties;
        this.dataSourceCreator = dataSourceCreator;
        this.shardingSphereDataSource = shardingSphereDataSource;
    }


    @Bean
    public DynamicDataSourceProvider dynamicDataSourceProvider() {
        return new AbstractDataSourceProvider(this.dataSourceCreator) {
            @Override
            public Map<String, DataSource> loadDataSources() {
                Map<String, DataSource> dataSourceMap = new HashMap<>();

                if (DataSourceConfig.this.shardingSphereDataSource != null) {
                    dataSourceMap.put("shardingSphere", DataSourceConfig.this.shardingSphereDataSource);
                }

                return dataSourceMap;
            }
        };
    }

    @Primary
    @Bean
    public DataSource dataSource(List<DynamicDataSourceProvider> providers) {
        DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource(providers);
        dataSource.setPrimary(this.properties.getPrimary());
        dataSource.setStrict(this.properties.getStrict());
        dataSource.setStrategy(this.properties.getStrategy());
        dataSource.setP6spy(this.properties.getP6spy());
        dataSource.setSeata(this.properties.getSeata());
        return dataSource;
    }


    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
