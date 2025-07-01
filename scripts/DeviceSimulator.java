package com.fastbee.test;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import com.alibaba.fastjson2.JSON;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * FastBee设备模拟器
 * 用于模拟大量设备连接和数据上报
 */
public class DeviceSimulator {
    
    private static final String MQTT_HOST = System.getProperty("mqtt.host", "tcp://177.7.0.12:1883");
    private static final String USERNAME = System.getProperty("mqtt.username", "fastbee");
    private static final String PASSWORD = System.getProperty("mqtt.password", "fastbee");
    private static final int DEVICE_COUNT = Integer.parseInt(System.getProperty("device.count", "10000"));
    private static final int REPORT_INTERVAL = Integer.parseInt(System.getProperty("report.interval", "30"));
    private static final int CONNECTION_INTERVAL = Integer.parseInt(System.getProperty("conn.interval", "100"));
    private static final int BATCH_SIZE = Integer.parseInt(System.getProperty("batch.size", "1000"));
    
    private final AtomicInteger connectedDevices = new AtomicInteger(0);
    private final AtomicInteger failedConnections = new AtomicInteger(0);
    private final AtomicLong messagesSent = new AtomicLong(0);
    private final AtomicLong messagesSuccess = new AtomicLong(0);
    private final AtomicLong messagesFailed = new AtomicLong(0);
    
    private final ScheduledExecutorService connectionExecutor = Executors.newScheduledThreadPool(100);
    private final ScheduledExecutorService reportExecutor = Executors.newScheduledThreadPool(200);
    private final ScheduledExecutorService statsExecutor = Executors.newScheduledThreadPool(1);
    
    private final List<MqttClient> clients = Collections.synchronizedList(new ArrayList<>());
    private volatile boolean running = true;
    
    public static void main(String[] args) {
        System.out.println("==================== FastBee设备模拟器 ====================");
        System.out.println("MQTT服务器: " + MQTT_HOST);
        System.out.println("设备数量: " + DEVICE_COUNT);
        System.out.println("上报间隔: " + REPORT_INTERVAL + "秒");
        System.out.println("连接间隔: " + CONNECTION_INTERVAL + "毫秒");
        System.out.println("批次大小: " + BATCH_SIZE);
        System.out.println("=========================================================");
        
        DeviceSimulator simulator = new DeviceSimulator();
        simulator.startSimulation();
        
        // 注册优雅关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n正在关闭设备模拟器...");
            simulator.shutdown();
        }));
        
        // 保持主线程运行
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            System.out.println("程序被中断");
        }
    }
    
    public void startSimulation() {
        // 启动统计监控
        startStatsMonitor();
        
        // 分批连接设备
        System.out.println("开始连接设备...");
        for (int batch = 0; batch < DEVICE_COUNT; batch += BATCH_SIZE) {
            final int batchStart = batch;
            final int batchEnd = Math.min(batch + BATCH_SIZE, DEVICE_COUNT);
            
            connectionExecutor.schedule(() -> connectDeviceBatch(batchStart, batchEnd), 
                                      (long) batch / BATCH_SIZE * 5, TimeUnit.SECONDS);
        }
    }
    
    private void connectDeviceBatch(int start, int end) {
        System.out.println("连接设备批次: " + start + "-" + (end-1));
        
        for (int i = start; i < end; i++) {
            final int deviceIndex = i;
            connectionExecutor.schedule(() -> connectDevice(deviceIndex), 
                                      (long) (i - start) * CONNECTION_INTERVAL, TimeUnit.MILLISECONDS);
        }
    }
    
    private void connectDevice(int deviceIndex) {
        if (!running) return;
        
        String clientId = "FastBeeDevice_" + String.format("%06d", deviceIndex);
        
        try {
            MqttClient client = new MqttClient(MQTT_HOST, clientId, new MemoryPersistence());
            
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(USERNAME);
            options.setPassword(PASSWORD.toCharArray());
            options.setKeepAliveInterval(60);
            options.setCleanSession(true);
            options.setConnectionTimeout(30);
            options.setAutomaticReconnect(true);
            
            // 设置回调
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    connectedDevices.decrementAndGet();
                    System.out.println("设备连接丢失: " + clientId + ", 原因: " + cause.getMessage());
                }
                
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // 处理下发的消息
                    System.out.println("设备 " + clientId + " 收到消息: " + topic);
                }
                
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    messagesSuccess.incrementAndGet();
                }
            });
            
            // 连接到MQTT服务器
            client.connect(options);
            
            clients.add(client);
            connectedDevices.incrementAndGet();
            
            // 订阅下发主题
            String subscribeTopic = String.format("/fastbee/prod/%s/function/get", clientId);
            client.subscribe(subscribeTopic, 0);
            
            // 启动数据上报任务
            startReportingTask(client, clientId, deviceIndex);
            
            if (deviceIndex % 1000 == 0) {
                System.out.println("已连接设备数量: " + connectedDevices.get());
            }
            
        } catch (Exception e) {
            failedConnections.incrementAndGet();
            System.err.println("设备连接失败: " + clientId + ", 错误: " + e.getMessage());
        }
    }
    
    private void startReportingTask(MqttClient client, String clientId, int deviceIndex) {
        // 属性上报任务
        reportExecutor.scheduleWithFixedDelay(() -> {
            if (!running || !client.isConnected()) return;
            
            try {
                // 发送属性数据
                publishPropertyData(client, clientId, deviceIndex);
                
                // 随机发送事件数据
                if (Math.random() < 0.1) { // 10% 概率发送事件
                    publishEventData(client, clientId, deviceIndex);
                }
                
            } catch (Exception e) {
                messagesFailed.incrementAndGet();
                System.err.println("设备数据上报失败: " + clientId + ", 错误: " + e.getMessage());
            }
        }, REPORT_INTERVAL + (int)(Math.random() * 10), REPORT_INTERVAL, TimeUnit.SECONDS);
    }
    
    private void publishPropertyData(MqttClient client, String clientId, int deviceIndex) throws Exception {
        String topic = String.format("/fastbee/prod/%s/property/post", clientId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("id", System.currentTimeMillis());
        data.put("params", Arrays.asList(
            createPropertyData("temperature", ThreadLocalRandom.current().nextInt(20, 40), "温度传感器"),
            createPropertyData("humidity", ThreadLocalRandom.current().nextInt(40, 80), "湿度传感器"),
            createPropertyData("pressure", ThreadLocalRandom.current().nextInt(950, 1050), "气压传感器"),
            createPropertyData("pm25", ThreadLocalRandom.current().nextInt(10, 200), "PM2.5传感器"),
            createPropertyData("noise", ThreadLocalRandom.current().nextInt(30, 80), "噪声传感器")
        ));
        data.put("method", "thing.event.property.post");
        
        String payload = JSON.toJSONString(data);
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(0);
        
        client.publish(topic, message);
        messagesSent.incrementAndGet();
    }
    
    private void publishEventData(MqttClient client, String clientId, int deviceIndex) throws Exception {
        String topic = String.format("/fastbee/prod/%s/event/post", clientId);
        
        List<Map<String, Object>> events = Arrays.asList(
            createEventData("alarm_high_temp", "temperature", 45, "高温报警"),
            createEventData("alarm_low_battery", "battery", 15, "低电量报警"),
            createEventData("alarm_offline", "status", 0, "设备离线报警"),
            createEventData("maintenance_required", "maintenance", 1, "维护提醒")
        );
        
        Map<String, Object> selectedEvent = events.get(ThreadLocalRandom.current().nextInt(events.size()));
        
        Map<String, Object> data = new HashMap<>();
        data.put("id", System.currentTimeMillis());
        data.put("params", Arrays.asList(selectedEvent));
        data.put("method", "thing.event.post");
        
        String payload = JSON.toJSONString(data);
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(0);
        
        client.publish(topic, message);
        messagesSent.incrementAndGet();
    }
    
    private void publishInfoData(MqttClient client, String clientId, int deviceIndex) throws Exception {
        String topic = String.format("/fastbee/prod/%s/info/post", clientId);
        
        Map<String, Object> deviceInfo = new HashMap<>();
        deviceInfo.put("imei", "861536030073854");
        deviceInfo.put("iccid", "89860318740035532710");
        deviceInfo.put("firmwareVersion", "1.0." + (deviceIndex % 100));
        deviceInfo.put("status", 1);
        deviceInfo.put("rssi", -50 + ThreadLocalRandom.current().nextInt(20));
        
        // 模拟位置信息
        deviceInfo.put("longitude", 116.404 + ThreadLocalRandom.current().nextDouble(-0.1, 0.1));
        deviceInfo.put("latitude", 39.915 + ThreadLocalRandom.current().nextDouble(-0.1, 0.1));
        
        Map<String, Object> data = new HashMap<>();
        data.put("id", System.currentTimeMillis());
        data.put("params", deviceInfo);
        data.put("method", "thing.info.post");
        
        String payload = JSON.toJSONString(data);
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(0);
        
        client.publish(topic, message);
        messagesSent.incrementAndGet();
    }
    
    private Map<String, Object> createPropertyData(String identifier, Object value, String name) {
        Map<String, Object> property = new HashMap<>();
        property.put("identifier", identifier);
        property.put("value", value);
        property.put("name", name);
        return property;
    }
    
    private Map<String, Object> createEventData(String identifier, String paramKey, Object paramValue, String name) {
        Map<String, Object> event = new HashMap<>();
        event.put("identifier", identifier);
        event.put("name", name);
        
        Map<String, Object> params = new HashMap<>();
        params.put(paramKey, paramValue);
        event.put("value", params);
        
        return event;
    }
    
    private void startStatsMonitor() {
        statsExecutor.scheduleWithFixedDelay(() -> {
            if (!running) return;
            
            long currentTime = System.currentTimeMillis();
            int connected = connectedDevices.get();
            int failed = failedConnections.get();
            long sent = messagesSent.get();
            long success = messagesSuccess.get();
            long failedMsg = messagesFailed.get();
            
            System.out.println("\n========== 设备模拟器状态统计 ==========");
            System.out.println("时间: " + new Date(currentTime));
            System.out.println("目标设备数: " + DEVICE_COUNT);
            System.out.println("已连接设备: " + connected);
            System.out.println("连接失败数: " + failed);
            System.out.println("连接成功率: " + String.format("%.2f%%", 
                connected * 100.0 / (connected + failed)));
            System.out.println("消息发送总数: " + sent);
            System.out.println("消息成功数: " + success);
            System.out.println("消息失败数: " + failedMsg);
            if (sent > 0) {
                System.out.println("消息成功率: " + String.format("%.2f%%", 
                    success * 100.0 / sent));
            }
            System.out.println("=====================================\n");
            
        }, 30, 30, TimeUnit.SECONDS);
    }
    
    public void shutdown() {
        running = false;
        
        System.out.println("正在断开所有设备连接...");
        
        // 断开所有客户端连接
        for (MqttClient client : clients) {
            try {
                if (client.isConnected()) {
                    client.disconnect();
                    client.close();
                }
            } catch (Exception e) {
                System.err.println("断开连接时出错: " + e.getMessage());
            }
        }
        
        // 关闭线程池
        connectionExecutor.shutdownNow();
        reportExecutor.shutdownNow();
        statsExecutor.shutdownNow();
        
        // 打印最终统计
        System.out.println("\n========== 最终统计结果 ==========");
        System.out.println("目标设备数: " + DEVICE_COUNT);
        System.out.println("最大连接数: " + connectedDevices.get());
        System.out.println("连接失败数: " + failedConnections.get());
        System.out.println("消息发送总数: " + messagesSent.get());
        System.out.println("消息成功数: " + messagesSuccess.get());
        System.out.println("消息失败数: " + messagesFailed.get());
        System.out.println("===============================");
        
        System.out.println("设备模拟器已关闭");
    }
} 