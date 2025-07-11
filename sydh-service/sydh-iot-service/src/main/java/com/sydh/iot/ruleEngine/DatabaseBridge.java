package com.sydh.iot.ruleEngine;

import com.alibaba.fastjson2.JSON;
import com.sydh.iot.domain.Bridge;
import com.sydh.iot.domain.MultipleDataSource;
import com.sydh.iot.service.IDynamicDataSourceService;
import com.sydh.rule.context.MsgContext;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Slf4j
@LiteflowComponent("databaseBridge")
public class DatabaseBridge extends NodeComponent {
    @Autowired
    private IDynamicDataSourceService dataSourceService;

    @Override
    public void process() throws Exception {
        MsgContext cxt = this.getContextBean(MsgContext.class);
        Integer id = cxt.getData("databaseBridgeID");
        Bridge bridge = dataSourceService.selectByBridgeId(id.longValue());
        if (bridge == null || bridge.getType() != 5) {
            log.error("数据源不存在或者当前配置类型不是数据库存储，id={}", id);
        }
        if ("1".equals(bridge.getEnable())) {
            MultipleDataSource multipleDataSource = JSON.parseObject(bridge.getConfigJson(), MultipleDataSource.class);
            if (multipleDataSource == null) {
                log.error("未找到对应的多数据源配置，bridgeId: {}", id);
                throw new IllegalStateException("未找到对应的多数据源配置");
            }
            multipleDataSource.setPoolName(bridge.getName());
            DataSource dataSource = dataSourceService.selectDataSource(multipleDataSource);
            if (dataSource == null) {
                log.error("未找到对应的数据源配置，bridgeId: {}", id);
                throw new IllegalStateException("未找到对应的数据源配置");
            }
            String sql = cxt.placeholders(multipleDataSource.getSql());
            log.info("执行sql：{}", sql);
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.execute();
            } catch (SQLException e) {
                log.error("SQL执行失败，原因：{}", e.getMessage(), e);
                // 这里可以添加重试机制或其他异常处理逻辑
            }
        }
    }

}
