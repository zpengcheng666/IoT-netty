package com.sydh.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Netty配置属性 - 从application.yml读取配置
 * 
 * @author zpc
 * @date 2025-7-9
 */
@Data
@Component
@ConfigurationProperties(prefix = "server")
public class NettyConfigProperties {

    private Device device = new Device();
    private Adaptive adaptive = new Adaptive();
    private NettyConfig netty = new NettyConfig();

    @Data
    public static class Device {
        private String scale = "small";           // 设备规模
        private int expectedCount = 5000;        // 预期设备数量
    }

    @Data
    public static class Adaptive {
        private boolean enabled = true;           // 启用自适应
        private int monitorInterval = 300;       // 监控间隔
        private boolean autoScale = true;        // 自动扩缩容
    }

    @Data
    public static class NettyConfig {
        private ProtocolConfig mqtt = new ProtocolConfig(8, 4, 10000, 2000);
        private ProtocolConfig tcp = new ProtocolConfig(6, 3, 5000, 1500);
        private ProtocolConfig http = new ProtocolConfig(4, 2, 2000, 1000);
    }

    @Data
    public static class ProtocolConfig {
        private int workerThreads;          // Worker线程数
        private int businessThreads;        // 业务线程数
        private int maxConnections;         // 最大连接数
        private int queueSize;              // 队列大小

        public ProtocolConfig() {}

        public ProtocolConfig(int workerThreads, int businessThreads, int maxConnections, int queueSize) {
            this.workerThreads = workerThreads;
            this.businessThreads = businessThreads;
            this.maxConnections = maxConnections;
            this.queueSize = queueSize;
        }
    }

    /**
     * 根据设备规模自动调整配置
     */
    public void autoAdjustByScale() {
        DeviceScale scale = getDeviceScaleEnum();
        int processors = Runtime.getRuntime().availableProcessors();

        switch (scale) {
            case SMALL:
                adjustForSmallScale(processors);
                break;
            case MEDIUM:
                adjustForMediumScale(processors);
                break;
            case LARGE:
                adjustForLargeScale(processors);
                break;
        }
    }

    private void adjustForSmallScale(int processors) {
        // MQTT小规模配置
        netty.mqtt.workerThreads = Math.min(processors * 2, 16);
        netty.mqtt.businessThreads = Math.min(processors, 8);
        netty.mqtt.maxConnections = 20000;
        netty.mqtt.queueSize = 5000;

        // TCP小规模配置
        netty.tcp.workerThreads = Math.min(processors, 12);
        netty.tcp.businessThreads = Math.min(processors, 6);
        netty.tcp.maxConnections = 15000;
        netty.tcp.queueSize = 3000;

        // HTTP小规模配置
        netty.http.workerThreads = Math.min(processors, 8);
        netty.http.businessThreads = Math.min(processors, 4);
        netty.http.maxConnections = 5000;
        netty.http.queueSize = 2000;
    }

    private void adjustForMediumScale(int processors) {
        // MQTT中规模配置
        netty.mqtt.workerThreads = Math.min(processors * 4, 32);
        netty.mqtt.businessThreads = Math.min(processors * 2, 16);
        netty.mqtt.maxConnections = 150000;
        netty.mqtt.queueSize = 15000;

        // TCP中规模配置
        netty.tcp.workerThreads = Math.min(processors * 3, 24);
        netty.tcp.businessThreads = Math.min(processors * 2, 12);
        netty.tcp.maxConnections = 100000;
        netty.tcp.queueSize = 10000;

        // HTTP中规模配置
        netty.http.workerThreads = Math.min(processors * 2, 16);
        netty.http.businessThreads = Math.min(processors, 8);
        netty.http.maxConnections = 30000;
        netty.http.queueSize = 6000;
    }

    private void adjustForLargeScale(int processors) {
        // MQTT大规模配置
        netty.mqtt.workerThreads = Math.min(processors * 6, 64);
        netty.mqtt.businessThreads = Math.min(processors * 3, 32);
        netty.mqtt.maxConnections = 500000;
        netty.mqtt.queueSize = 50000;

        // TCP大规模配置
        netty.tcp.workerThreads = Math.min(processors * 4, 48);
        netty.tcp.businessThreads = Math.min(processors * 3, 24);
        netty.tcp.maxConnections = 300000;
        netty.tcp.queueSize = 30000;

        // HTTP大规模配置
        netty.http.workerThreads = Math.min(processors * 3, 32);
        netty.http.businessThreads = Math.min(processors * 2, 16);
        netty.http.maxConnections = 100000;
        netty.http.queueSize = 20000;
    }

    public DeviceScale getDeviceScaleEnum() {
        switch (device.scale.toLowerCase()) {
            case "medium":
                return DeviceScale.MEDIUM;
            case "large":
                return DeviceScale.LARGE;
            default:
                return DeviceScale.SMALL;
        }
    }

    public enum DeviceScale {
        SMALL, MEDIUM, LARGE
    }

    /**
     * 获取指定协议的配置
     */
    public ProtocolConfig getProtocolConfig(String protocol) {
        switch (protocol.toLowerCase()) {
            case "mqtt":
                return netty.mqtt;
            case "tcp":
                return netty.tcp;
            case "http":
                return netty.http;
            default:
                return netty.tcp; // 默认返回TCP配置
        }
    }

    /**
     * 计算资源利用率
     */
    public double calculateUtilization(String protocol, int actualConnections) {
        ProtocolConfig config = getProtocolConfig(protocol);
        return (double) actualConnections / config.maxConnections;
    }

    /**
     * 获取优化建议
     */
    public String getOptimizationSuggestion(String protocol, int actualConnections) {
        double utilization = calculateUtilization(protocol, actualConnections);
        
        if (utilization < 0.2) {
            return String.format("%s协议连接利用率较低(%.1f%%)，可考虑缩容节约资源", 
                               protocol.toUpperCase(), utilization * 100);
        } else if (utilization > 0.8) {
            return String.format("%s协议连接利用率较高(%.1f%%)，建议考虑扩容", 
                               protocol.toUpperCase(), utilization * 100);
        } else {
            return String.format("%s协议资源配置合理，利用率%.1f%%", 
                               protocol.toUpperCase(), utilization * 100);
        }
    }
} 