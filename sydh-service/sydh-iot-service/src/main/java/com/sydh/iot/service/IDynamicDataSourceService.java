package com.sydh.iot.service;

import com.sydh.iot.domain.Bridge;
import com.sydh.iot.domain.MultipleDataSource;

import javax.sql.DataSource;
import java.util.List;

public interface IDynamicDataSourceService {

    List<MultipleDataSource> selectList(MultipleDataSource ds);

    MultipleDataSource selectByDataName(String dataName);

    Bridge selectByBridgeId(Long id);

    DataSource selectDataSource(MultipleDataSource config);

    int insert(MultipleDataSource ds);

    int update(MultipleDataSource ds);

    void deletesByDataName(String dataName);
}
