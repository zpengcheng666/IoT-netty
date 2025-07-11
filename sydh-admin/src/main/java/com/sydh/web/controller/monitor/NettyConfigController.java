package com.sydh.web.controller.monitor;

import com.sydh.common.core.domain.AjaxResult;
import com.sydh.server.config.NettyConfigFactory;
import com.sydh.server.config.NettyConfigProperties;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Netty配置监控控制器
 * 
 * @author zpc
 * @date 2025-7-9
 */
@Slf4j
@RestController
@RequestMapping("/monitor/netty")
@Api(tags = "监控：Netty配置")
public class NettyConfigController {

    @Autowired
    private NettyConfigFactory configFactory;

    /**
     * 获取Netty配置摘要
     */
    @PreAuthorize("@ss.hasPermi('monitor:netty:view')")
    @GetMapping("/config")
    @ApiOperation("获取Netty配置摘要")
    public AjaxResult getConfig() {
        try {
            String summary = configFactory.getConfigSummary();
            NettyConfigProperties properties = configFactory.getConfigProperties();
            
            Map<String, Object> data = new HashMap<>();
            data.put("summary", summary);
            data.put("deviceScale", properties.getDevice().getScale());
            data.put("expectedCount", properties.getDevice().getExpectedCount());
            data.put("adaptiveEnabled", properties.getAdaptive().isEnabled());
            
            // 各协议详细配置
            Map<String, Object> protocols = new HashMap<>();
            protocols.put("mqtt", properties.getNetty().getMqtt());
            protocols.put("tcp", properties.getNetty().getTcp());
            protocols.put("http", properties.getNetty().getHttp());
            data.put("protocols", protocols);
            
            return AjaxResult.success("获取配置成功", data);
        } catch (Exception e) {
            log.error("获取Netty配置失败", e);
            return AjaxResult.error("获取配置失败: " + e.getMessage());
        }
    }

    /**
     * 获取协议优化建议,根据协议类型和当前连接数获取优化建议
     * @param protocol 协议类型,支持mqtt, tcp, http
     * @param actualConnections 当前连接数
     */
    @PreAuthorize("@ss.hasPermi('monitor:netty:view')")
    @GetMapping("/suggestion")
    @ApiOperation("获取协议优化建议")
    public AjaxResult getOptimizationSuggestion(@RequestParam String protocol, @RequestParam int actualConnections) {
        try {
            // 根据协议类型和当前连接数获取优化建议
            String suggestion = configFactory.getOptimizationSuggestion(protocol, actualConnections);
            // 计算当前协议和连接数下的利用率
            double utilization = configFactory.getConfigProperties().calculateUtilization(protocol, actualConnections);

            // 创建一个Map来存储所有数据
            Map<String, Object> data = new HashMap<>();
            // 将协议类型转换为大写并存入Map
            data.put("protocol", protocol.toUpperCase());
            // 存储实际连接数
            data.put("actualConnections", actualConnections);
            // 存储计算出的利用率，格式为x.x%
            data.put("utilization", String.format("%.1f%%", utilization * 100));
            // 存储优化建议
            data.put("suggestion", suggestion);
            // 根据利用率获取并存储利用率状态
            data.put("status", getUtilizationStatus(utilization));

            return AjaxResult.success("获取建议成功", data);
        } catch (Exception e) {
            log.error("获取优化建议失败", e);
            return AjaxResult.error("获取建议失败: " + e.getMessage());
        }
    }

    /**
     * 更新设备数量
     */
    @PreAuthorize("@ss.hasPermi('monitor:netty:edit')")
    @PostMapping("/update-device-count")
    @ApiOperation("更新设备数量")
    public AjaxResult updateDeviceCount(@RequestParam int deviceCount) {
        try {
            if (deviceCount < 0) {
                return AjaxResult.error("设备数量不能为负数");
            }
            
            String oldScale = configFactory.getConfigProperties().getDevice().getScale();
            configFactory.updateDeviceCount(deviceCount);
            String newScale = configFactory.getConfigProperties().getDevice().getScale();
            
            Map<String, Object> data = new HashMap<>();
            data.put("deviceCount", deviceCount);
            data.put("oldScale", oldScale);
            data.put("newScale", newScale);
            data.put("scaleChanged", !oldScale.equals(newScale));
            
            return AjaxResult.success("设备数量更新成功", data);
        } catch (Exception e) {
            log.error("更新设备数量失败", e);
            return AjaxResult.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 手动调整设备规模
     * 设备规模: small(0-1万), medium(1万-10万), large(10万+)
     */
    @PreAuthorize("@ss.hasPermi('monitor:netty:edit')")
    @PostMapping("/adjust-scale")
    @ApiOperation("手动调整设备规模,设备规模: small(0-1万), medium(1万-10万), large(10万+)")
    public AjaxResult adjustScale(@RequestParam String scale) {
        try {
            if (!isValidScale(scale)) {
                return AjaxResult.error("无效的规模配置，支持: small, medium, large");
            }
            
            NettyConfigProperties properties = configFactory.getConfigProperties();
            String oldScale = properties.getDevice().getScale();
            properties.getDevice().setScale(scale.toLowerCase());
            properties.autoAdjustByScale();
            
            Map<String, Object> data = new HashMap<>();
            data.put("oldScale", oldScale);
            data.put("newScale", scale.toLowerCase());
            data.put("configSummary", configFactory.getConfigSummary());
            
            log.info("手动调整设备规模: {} -> {}", oldScale, scale.toLowerCase());
            return AjaxResult.success("规模调整成功", data);
        } catch (Exception e) {
            log.error("调整设备规模失败", e);
            return AjaxResult.error("调整失败: " + e.getMessage());
        }
    }

    /**
     * 获取资源使用情况
     */
    @PreAuthorize("@ss.hasPermi('monitor:netty:view')")
    @GetMapping("/resource-usage")
    @ApiOperation("获取netty资源使用情况")
    public AjaxResult getResourceUsage() {
        try {
            Map<String, Object> data = new HashMap<>();
            
            // 获取JVM信息
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            
            Map<String, String> memoryInfo = new HashMap<>();
            memoryInfo.put("used", usedMemory / 1024 / 1024 + " MB");
            memoryInfo.put("max", maxMemory / 1024 / 1024 + " MB");
            memoryInfo.put("utilization", String.format("%.1f%%", (double) usedMemory / maxMemory * 100));
            data.put("memory", memoryInfo);
            
            // 获取线程信息
            Map<String, Object> threadInfo = new HashMap<>();
            threadInfo.put("count", Thread.activeCount());
            threadInfo.put("peak", "N/A"); // 可以通过ThreadMXBean获取
            threadInfo.put("processors", runtime.availableProcessors());
            data.put("threads", threadInfo);
            
            // 获取当前配置信息
            NettyConfigProperties properties = configFactory.getConfigProperties();
            Map<String, Object> configInfo = new HashMap<>();
            configInfo.put("scale", properties.getDevice().getScale());
            configInfo.put("expectedDevices", properties.getDevice().getExpectedCount());
            configInfo.put("adaptiveEnabled", properties.getAdaptive().isEnabled());
            data.put("currentConfig", configInfo);
            
            return AjaxResult.success("获取资源使用情况成功", data);
        } catch (Exception e) {
            log.error("获取资源使用情况失败", e);
            return AjaxResult.error("获取失败: " + e.getMessage());
        }
    }

    // 辅助方法
    private String getUtilizationStatus(double utilization) {
        if (utilization < 0.3) {
            return "LOW";  // 利用率低
        } else if (utilization > 0.8) {
            return "HIGH"; // 利用率高
        } else {
            return "NORMAL"; // 正常
        }
    }

    private boolean isValidScale(String scale) {
        return scale != null && 
               (scale.equalsIgnoreCase("small") || 
                scale.equalsIgnoreCase("medium") || 
                scale.equalsIgnoreCase("large"));
    }
} 