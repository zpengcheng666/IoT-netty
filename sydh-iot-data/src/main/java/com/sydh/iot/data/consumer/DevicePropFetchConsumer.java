package com.sydh.iot.data.consumer;

import com.alibaba.fastjson2.JSON;
import com.sydh.base.service.ISessionStore;
import com.sydh.base.session.Session;
import com.sydh.common.constant.SYDHConstant;
import com.sydh.common.core.redis.RedisCache;
import com.sydh.common.core.redis.RedisKeyBuilder;
import com.sydh.common.enums.DeviceStatus;
import com.sydh.common.enums.ServerType;
import com.sydh.common.enums.TopicType;
import com.sydh.common.extend.core.domin.mq.DeviceStatusBo;
import com.sydh.common.extend.core.domin.mq.message.ModbusPollMsg;
import com.sydh.common.extend.core.protocol.Message;
import com.sydh.common.extend.enums.ModbusJobCommantEnum;
import com.sydh.common.utils.DateUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.common.utils.gateway.mq.TopicsUtils;
import com.sydh.iot.data.service.impl.MessageManager;
import com.sydh.iot.domain.DeviceJob;
import com.sydh.iot.mapper.SubGatewayMapper;
import com.sydh.iot.model.ProductCode;
import com.sydh.iot.model.gateWay.GateSubDeviceParamsVO;
import com.sydh.iot.model.vo.DeviceStatusVO;
import com.sydh.iot.model.modbus.ModbusPollJob;
import com.sydh.iot.model.vo.ModbusJobVO;
import com.sydh.iot.service.IDeviceService;
import com.sydh.iot.service.IModbusJobService;
import com.sydh.iot.service.IModbusParamsService;
import com.sydh.mq.producer.MessageProducer;
import com.sydh.mqttclient.PubMqttClient;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 平台定时批量获取设备属性(或单个获取)
 *
 * @author bill
 */
@Slf4j
@Component
public class DevicePropFetchConsumer {


    @Autowired
    private PubMqttClient pubMqttClient;
    @Autowired
    private RedisCache redisCache;
    @Resource
    private MessageManager messageManager;
    @Resource
    private TopicsUtils topicsUtils;
    @Resource
    private IModbusJobService modbusJobService;
    @Resource
    private IDeviceService deviceService;
    @Resource
    private ISessionStore sessionStore;
    @Resource
    private IModbusParamsService modbusParamsService;
    @Resource
    private SubGatewayMapper subGatewayMapper;

    @Value("${server.broker.enabled}")
    private Boolean enabled;
    //锁集合
    private final ConcurrentHashMap<String, Lock> taskLocks = new ConcurrentHashMap<>();

    @Async(SYDHConstant.TASK.DEVICE_FETCH_PROP_TASK)
    public void consume(ModbusPollJob job) {
        Integer type = job.getType();
        DeviceJob deviceJob = job.getDeviceJob();
        ModbusPollMsg task = type == 1 ? job.getPollMsg() : getCommandList(deviceJob);
        if (CollectionUtils.isEmpty(task.getCommandList()))return;
        String serialNumber = task.getSerialNumber();
        //获取一个线程锁
        Lock lock = taskLocks.computeIfAbsent(serialNumber, k -> new ReentrantLock());
        //阻塞直到获取到锁
        lock.lock();
        try {
            Long productId = task.getProductId();
            List<String> commandList = task.getCommandList();
            ServerType serverType = ServerType.explain(task.getTransport());
            String topic = topicsUtils.buildTopic(productId, serialNumber, TopicType.FUNCTION_GET);
            for (String command : commandList) {
                String cacheKey = RedisKeyBuilder.buildModbusRuntimeCacheKey(serialNumber);
                redisCache.zSetAdd(cacheKey, command, DateUtils.getTimestampSeconds());
                switch (serverType) {
                    //通过mqtt内部客户端 下发指令
                    case MQTT:
                        publish(topic, ByteBufUtil.decodeHexDump(command));
                        log.info("=>MQTT-线程=[{}],轮询指令:[{}],主题:[{}]", Thread.currentThread().getName(), command, topic);
                        break;
                    //  下发TCP客户端
                    case TCP:
                        Message msg = new Message();
                        msg.setClientId(serialNumber);
                        msg.setPayload(Unpooled.wrappedBuffer(ByteBufUtil.decodeHexDump(command)));
                        messageManager.requestR(serialNumber, msg, Message.class);
                        log.info("=>TCP-线程=[{}],轮询指令:[{}]", Thread.currentThread().getName(), command);
                        break;
                }
                //指令间隔时间
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            log.error("线程错误e", e);
        } finally {
            lock.unlock();
        }
    }

    private ModbusPollMsg getCommandList(DeviceJob deviceJob) {
        ModbusPollMsg modbusPollMsg = new ModbusPollMsg();
        Long jobId = deviceJob.getJobId();
        ModbusJobVO modbusJobVO = new ModbusJobVO();
        modbusJobVO.setJobId(jobId);
        modbusJobVO.setStatus(0);
        modbusJobVO.setCommandType(ModbusJobCommantEnum.POLLING.getType());
        List<ModbusJobVO> modbusJobVOList = modbusJobService.selectModbusJobList(modbusJobVO);
        if (CollectionUtils.isEmpty(modbusJobVOList))return modbusPollMsg;
        //处理子设备情况
        handleSubDeviceStatus(modbusJobVOList);
        List<String> commandList = modbusJobVOList.stream().map(ModbusJobVO::getCommand).collect(Collectors.toList());
        // modbus-tcp 重新处理事务标识符
        List<String> tcpCommandList = new ArrayList<>();
        // modbus-tcp 重新处理事务标识符
        String parentSerialNumber = deviceJob.getSerialNumber();
        // 老数据可能没tcp事务key，还是严谨点查询处理把
        ProductCode protocolBySerialNumber = deviceService.getProtocolBySerialNumber(parentSerialNumber);
        if (Objects.nonNull(protocolBySerialNumber) && SYDHConstant.PROTOCOL.ModbusTcpOverRtu.equals(protocolBySerialNumber.getProtocolCode())) {
            String tcpRuntimeCacheKey = RedisKeyBuilder.buildModbusTcpRuntimeCacheKey(parentSerialNumber);
            for (String command : commandList) {
                String tranNo = command.substring(0, 4);
                int i = Integer.parseInt(tranNo, 16);
                Object stringHashValue = redisCache.getStringHashValue(tcpRuntimeCacheKey, String.valueOf(i));
                if (Objects.nonNull(stringHashValue)) {
                    tcpCommandList.add(command);
                    continue;
                }
                String modbusTcpId = redisCache.getCacheModbusTcpId(parentSerialNumber);
                String hexString = String.format("%04x", Integer.parseInt(modbusTcpId));
                String newTcpData = hexString + command.substring(4);
                redisCache.cacheModbusTcpData(parentSerialNumber, modbusTcpId, newTcpData);
                tcpCommandList.add(newTcpData);
            }
        }
        modbusPollMsg.setSerialNumber(deviceJob.getSerialNumber());
        modbusPollMsg.setProductId(deviceJob.getProductId());
        if (CollectionUtils.isEmpty(tcpCommandList)) {
            modbusPollMsg.setCommandList(commandList);
        } else {
            modbusPollMsg.setCommandList(tcpCommandList);
        }
        DeviceStatusVO deviceStatusVO = deviceService.selectDeviceStatusAndTransportStatus(modbusPollMsg.getSerialNumber());
        if (Objects.isNull(deviceStatusVO)) {
            modbusPollMsg.setCommandList(null);
            return modbusPollMsg;
        }
        modbusPollMsg.setTransport(deviceStatusVO.getTransport());
        Session session = sessionStore.getSession(modbusPollMsg.getSerialNumber());
        if (enabled && Objects.isNull(session)){
            log.info("设备：[{}],不在线",modbusPollMsg.getSerialNumber());
            return modbusPollMsg;
        }
        log.info("执行modbus轮询指令:[{}]", JSON.toJSONString(commandList));
        return modbusPollMsg;
    }


    /**
     * 处理子设备根据设备数据定时更新状态
     */
    public void handleSubDeviceStatus(List<ModbusJobVO> modbusJobVOList){
        List<String> addressList = modbusJobVOList.stream().map(ModbusJobVO::getAddress).distinct().collect(Collectors.toList());
        List<GateSubDeviceParamsVO> subGatewayList = subGatewayMapper.selectSubModbusParamsVO(modbusJobVOList.get(0).getSerialNumber(), addressList);
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(subGatewayList)) {
            return;
        }
        Map<String, GateSubDeviceParamsVO> subGatewayMap = subGatewayList.stream().collect(Collectors.toMap(GateSubDeviceParamsVO::getAddress, Function.identity()));
        for (ModbusJobVO modbusJobVO : modbusJobVOList) {
            GateSubDeviceParamsVO subGateway = subGatewayMap.get(modbusJobVO.getAddress());
            if (Objects.isNull(subGateway)) {
                continue;
            }
            Session session = sessionStore.getSession(subGateway.getSerialNumber());
            if (Objects.isNull(session))continue;
            //如果是网关子设备，则检测子设备是否在特定时间内有数据上报
            long deterTime = 300L; //这里默认使用5分钟,如果轮询时间大于5分钟，请在配置参数设置更长时间
            if (StringUtils.isNotEmpty(subGateway.getDeterTimer())){
                deterTime = Long.parseLong(subGateway.getDeterTimer());
            }

            long lastAccessTime = session.getLastAccessTime();
            //如果现在的时间 - 最后访问时间 > 判断时间则更新为离线
            long time = (System.currentTimeMillis() - lastAccessTime - 20) / 1000;
            if (time > deterTime){
                //处理设备离线
                DeviceStatusBo statusBo = DeviceStatusBo.builder().status(DeviceStatus.OFFLINE)
                        .serialNumber(subGateway.getSerialNumber())
                        .timestamp(DateUtils.getNowDate()).build();
                MessageProducer.sendStatusMsg(statusBo);
                sessionStore.cleanSession(subGateway.getSerialNumber());
                log.info("设备[{}],超过：[{}],未上报数据，更新为离线",subGateway.getSerialNumber(),deterTime);
            }
        }
    }

    public void publish(String topic, byte[] pushMessage) {
        pubMqttClient.publish(pushMessage, topic, false, 0);
    }
}
