package com.sydh.iot.service.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.dynamic.datasource.creator.druid.DruidConfig;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.sydh.framework.config.properties.DruidProperties;
import com.sydh.iot.domain.Bridge;
import com.sydh.iot.domain.MultipleDataSource;
import com.sydh.iot.service.IBridgeService;
import com.sydh.iot.service.IDynamicDataSourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * @author zhuangpengli
 */
@Slf4j
@Service
public class DynamicDataSourceServiceImpl implements IDynamicDataSourceService {
    @Resource
    private DataSource dataSource;
    @Resource
    private DefaultDataSourceCreator dataSourceCreator;
    @Resource
    private DynamicDataSourceProperties dynamicDataSourceProperties;
    @Resource
    private IBridgeService bridgeService;

    @Resource
    private DruidProperties druidProperties;

    @Override
    public List<MultipleDataSource> selectList(MultipleDataSource ds) {
        Map<String, DataSourceProperty> dataSourceProperties = dynamicDataSourceProperties.getDatasource();
        if (dataSourceProperties != null) {
            List<MultipleDataSource> result = new ArrayList<>();
            for (Map.Entry<String, DataSourceProperty> entry : dataSourceProperties.entrySet()) {
                DataSourceProperty dstemp = entry.getValue();
                if (checkadd(ds, dstemp)) {
                    MultipleDataSource multipleDataSource = new MultipleDataSource();
                    BeanUtils.copyProperties(dstemp, multipleDataSource);
                    result.add(multipleDataSource);
                }
            }
        }
        return Collections.emptyList();
    }

    public Boolean checkadd(MultipleDataSource ds, DataSourceProperty dstemp) {
        if (ds.getType() != null && !ds.getType().equals(dstemp.getType())) {
            return false;
        }
        if (ds.getDriverClassName() != null && !ds.getDriverClassName().equals(dstemp.getDriverClassName())) {
            return false;
        }
        if (ds.getUrl() != null && !ds.getUrl().equals(dstemp.getUrl())) {
            return false;
        }

        return true;
    }

    @Override
    public MultipleDataSource selectByDataName(String dataName) {
//        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
//        Map<String, DataSource> map = ds.getDataSources();
        Map<String, DataSourceProperty> dataSourceProperties = dynamicDataSourceProperties.getDatasource();
        DataSourceProperty ds = dataSourceProperties.get(dataName);
        if (ds != null) {
            MultipleDataSource multipleDataSource = new MultipleDataSource();
            BeanUtils.copyProperties(ds, multipleDataSource);
            return multipleDataSource;
        }
        return null;
    }

    @Override
    public Bridge selectByBridgeId(Long id) {
        return bridgeService.queryByIdWithCache(id);
    }

    @Override
    public DataSource selectDataSource(MultipleDataSource config) {
        DataSourceProperty dataSourceProperty = buildDataSourceProperty(config);
        //新增数据源
        DynamicRoutingDataSource dsr = (DynamicRoutingDataSource) dataSource;
        DataSource dataSource = dataSourceCreator.createDataSource(dataSourceProperty);
        dsr.addDataSource(config.getPoolName(), dataSource);
        return dataSource;
    }

    @Override
    public int insert(MultipleDataSource ds) {
        DataSourceProperty dataSourceProperty = buildDataSourceProperty(ds);
        //新增数据源
        DynamicRoutingDataSource dsr = (DynamicRoutingDataSource) dataSource;
        DataSource dataSource = dataSourceCreator.createDataSource(dataSourceProperty);
        dsr.addDataSource(ds.getPoolName(), dataSource);
        return 1;
    }

    @Override
    public int update(MultipleDataSource ds) {
        return 1;
    }


    @Override
    public void deletesByDataName(String dataName) {
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        ds.removeDataSource(dataName);
    }


    private DataSourceProperty buildDataSourceProperty(MultipleDataSource config) {
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        //配置全局Druid配置
        DruidConfig Druidconf = new DruidConfig();
        BeanUtils.copyProperties(druidProperties, Druidconf);
        BeanUtils.copyProperties(config, dataSourceProperty);
        switch (config.getDatabaseSource()) {
            case "MySQL":
                dataSourceProperty.setDriverClassName("com.mysql.cj.jdbc.Driver");
                dataSourceProperty.setUrl("jdbc:" + config.getDatabaseSource().toLowerCase() + "://" + config.getHost() + "/" + config.getDataBaseName() + "?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8");
                break;
            case "SQLServer":
                dataSourceProperty.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                dataSourceProperty.setUrl("jdbc:" + config.getDatabaseSource().toLowerCase() + "://" + config.getHost() + ";DatabaseName=" + config.getDataBaseName());
                break;
            case "Oracle":
                dataSourceProperty.setDriverClassName("oracle.jdbc.driver.OracleDriver");
                dataSourceProperty.setUrl("jdbc:" + config.getDatabaseSource().toLowerCase() + ":thin:@" + config.getHost() + "/" + config.getDataBaseName());
                Druidconf.setValidationQuery("SELECT 1 FROM DUAL");
                break;
            case "PostgreSQL":
                dataSourceProperty.setDriverClassName("org.postgresql.Driver");
                dataSourceProperty.setUrl("jdbc:" + config.getDatabaseSource().toLowerCase() + "://" + config.getHost() + "/" + config.getDataBaseName());
                break;
            case "H2":
                dataSourceProperty.setDriverClassName("org.h2.Driver");
                dataSourceProperty.setUrl("jdbc:" + config.getDatabaseSource().toLowerCase() + "://" + config.getHost() + "/" + config.getDataBaseName());
                break;
            default:
                log.error("Unsupported database type: {}", config.getDatabaseSource());
                break;
        }
        dataSourceProperty.setDruid(Druidconf);
        dataSourceProperty.setType(DruidDataSource.class);
        return dataSourceProperty;
    }
}
