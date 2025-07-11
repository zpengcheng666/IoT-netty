package com.sydh.server.config;

import com.sydh.common.enums.ServerType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Netty配置工厂 - 简化版本，与现有项目集成
 * 
 * @author zpc
 * @date 2025-7-9
 */
@Getter
@Slf4j
@Component
public class NettyConfigFactory {

    /**
     * -- GETTER --
     *  获取配置属性（供其他组件使用）
     */
    @Autowired
    private NettyConfigProperties configProperties;

    @PostConstruct
    public void init() {
        // 应用启动时根据配置的规模自动调整参数
        if (configProperties.getAdaptive().isEnabled()) {
            configProperties.autoAdjustByScale();
            log.info("根据设备规模({})自动调整了Netty配置", configProperties.getDevice().getScale());
        }
    }

    /**
     * 创建适配当前项目的NettyConfig Builder
     */
    public NettyConfig.Builder createBuilder(ServerType serverType) {
        NettyConfigProperties.ProtocolConfig protocolConfig = getProtocolConfig(serverType);
        
        return NettyConfig.custom()
                .setThreadGroup(protocolConfig.getWorkerThreads(), protocolConfig.getBusinessThreads())
                .setMaxConnections(protocolConfig.getMaxConnections())
                .setBusinessQueueSize(protocolConfig.getQueueSize())
                .setBusinessMaxThreads(protocolConfig.getBusinessThreads() * 2)
                .setBacklogSize(getBacklogSize(serverType))
                .setType(serverType);
    }

    /**
     * 获取指定协议的配置
     */
    private NettyConfigProperties.ProtocolConfig getProtocolConfig(ServerType serverType) {
        switch (serverType) {
            case MQTT:
                return configProperties.getNetty().getMqtt();
            case TCP:
                return configProperties.getNetty().getTcp();
            case HTTP:
                return configProperties.getNetty().getHttp();
            default:
                return configProperties.getNetty().getTcp();
        }
    }

    /**
     * 获取Backlog大小
     */
    private int getBacklogSize(ServerType serverType) {
        NettyConfigProperties.ProtocolConfig config = getProtocolConfig(serverType);
        // 根据最大连接数动态计算backlog
        if (config.getMaxConnections() > 100000) {
            return 8192;
        } else if (config.getMaxConnections() > 20000) {
            return 2048;
        } else {
            return 1024;
        }
    }

    /**
     * 获取当前配置摘要
     */
    public String getConfigSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("=== Netty配置摘要 ===\n");
        summary.append(String.format("设备规模: %s (预期%d台)\n", 
                      configProperties.getDevice().getScale(), 
                      configProperties.getDevice().getExpectedCount()));
        
        // MQTT配置
        NettyConfigProperties.ProtocolConfig mqtt = configProperties.getNetty().getMqtt();
        summary.append(String.format("MQTT: Worker=%d, Business=%d, MaxConn=%d\n",
                      mqtt.getWorkerThreads(), mqtt.getBusinessThreads(), mqtt.getMaxConnections()));
        
        // TCP配置  
        NettyConfigProperties.ProtocolConfig tcp = configProperties.getNetty().getTcp();
        summary.append(String.format("TCP: Worker=%d, Business=%d, MaxConn=%d\n",
                      tcp.getWorkerThreads(), tcp.getBusinessThreads(), tcp.getMaxConnections()));
        
        // HTTP配置
        NettyConfigProperties.ProtocolConfig http = configProperties.getNetty().getHttp();
        summary.append(String.format("HTTP: Worker=%d, Business=%d, MaxConn=%d\n",
                      http.getWorkerThreads(), http.getBusinessThreads(), http.getMaxConnections()));
        
        summary.append("自适应监控: ").append(configProperties.getAdaptive().isEnabled() ? "启用" : "禁用").append("\n");
        summary.append("===================");
        
        return summary.toString();
    }

    /**
     * 更新设备数量并重新评估配置
     */
    public void updateDeviceCount(int deviceCount) {
        configProperties.getDevice().setExpectedCount(deviceCount);
        
        // 根据新的设备数量重新判断规模
        String newScale;
        if (deviceCount <= 10000) {
            newScale = "small";
        } else if (deviceCount <= 100000) {
            newScale = "medium";
        } else {
            newScale = "large";
        }
        
        if (!newScale.equals(configProperties.getDevice().getScale())) {
            log.info("设备数量变化({})，建议调整规模配置: {} -> {}", 
                    deviceCount, configProperties.getDevice().getScale(), newScale);
            
            // 可以选择自动调整或仅提供建议
            if (configProperties.getAdaptive().isAutoScale()) {
                configProperties.getDevice().setScale(newScale);
                configProperties.autoAdjustByScale();
                log.info("已自动调整为{}规模配置", newScale);
            }
        }
    }

    /**
     * 获取协议优化建议
     */
    public String getOptimizationSuggestion(String protocol, int actualConnections) {
        return configProperties.getOptimizationSuggestion(protocol, actualConnections);
    }

}