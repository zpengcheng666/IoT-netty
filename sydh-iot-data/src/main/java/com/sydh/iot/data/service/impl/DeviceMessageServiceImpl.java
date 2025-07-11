package com.sydh.iot.data.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sydh.common.constant.SYDHConstant;
import com.sydh.common.enums.DeviceStatus;
import com.sydh.common.enums.ServerType;
import com.sydh.common.enums.TopicType;
import com.sydh.common.exception.ServiceException;
import com.sydh.common.extend.core.domin.model.device.DeviceAndProtocol;
import com.sydh.common.extend.core.domin.mq.DeviceReportBo;
import com.sydh.common.extend.core.domin.mq.MQSendMessageBo;
import com.sydh.common.extend.core.domin.mq.message.*;
import com.sydh.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.sydh.common.extend.core.protocol.modbus.ModbusCode;
import com.sydh.common.extend.utils.modbus.ModbusUtils;
import com.sydh.common.utils.BitUtils;
import com.sydh.common.utils.DateUtils;
import com.sydh.common.utils.MessageUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.common.utils.gateway.CRC16Utils;
import com.sydh.common.utils.gateway.mq.TopicsUtils;
import com.sydh.common.utils.json.JsonUtils;
import com.sydh.iot.cache.ITSLCache;
import com.sydh.iot.data.service.IDeviceMessageService;
import com.sydh.iot.data.service.IMqttMessagePublish;
import com.sydh.iot.domain.ModbusConfig;
import com.sydh.iot.enums.DeviceType;
import com.sydh.iot.model.ThingsModels.ThingsModelValueItem;
import com.sydh.iot.model.ThingsModels.ValueItem;
import com.sydh.iot.model.modbus.ModbusPollJob;
import com.sydh.iot.model.vo.DeviceStatusVO;
import com.sydh.iot.model.vo.ModbusJobVO;
import com.sydh.iot.model.vo.VariableReadVO;
import com.sydh.iot.service.IDeviceService;
import com.sydh.iot.service.IModbusJobService;
import com.sydh.modbus.codec.ModbusEncoder;
import com.sydh.modbus.codec.ModbusProtocol;
import com.sydh.modbus.model.ModbusRtu;
import com.sydh.modbus.tcp.config.ModbusScheduler;
import com.sydh.modbustcp.codec.overRtu.ModbusTcpOverRtuEncoder;
import com.sydh.modbustcp.codec.overRtu.ModbusTcpOverRtuProtocol;
import com.sydh.modbustcp.model.ModbusTcp;
import com.sydh.mq.producer.MessageProducer;
import com.sydh.mqttclient.PubMqttClient;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DeviceMessageServiceImpl implements IDeviceMessageService {

    @Resource
    private PubMqttClient mqttClient;
    @Resource
    private ModbusEncoder modbusMessageEncoder;
    @Resource
    private IDeviceService deviceService;
    @Resource
    private TopicsUtils topicsUtils;
    @Resource
    private ITSLCache itslCache;
    @Resource
    private ModbusProtocol modbusProtocol;
    @Resource
    private IModbusJobService modbusJobService;
    @Resource
    private ModbusTcpOverRtuProtocol modbusTcpOverRtuProtocol;
    @Resource
    private ModbusTcpOverRtuEncoder modbusTcpOverRtuEncoder;
    @Resource
    private IMqttMessagePublish mqttMessagePublish;
    @Resource
    private ModbusScheduler modbusScheduler;

    @Override
    public void messagePost(DeviceMessage deviceMessage) {
        String topicName = deviceMessage.getTopicName();
        String serialNumber = deviceMessage.getSerialNumber();
        DeviceStatusVO deviceStatusVO = deviceService.selectDeviceStatusAndTransportStatus(serialNumber);
        String transport = deviceStatusVO.getTransport();
        TopicType type = TopicType.getType(topicName);
        topicName = topicsUtils.buildTopic(deviceStatusVO.getProductId(), serialNumber,type);
        switch (type){
            case FUNCTION_GET:
                if (transport.equals(ServerType.MQTT.getCode())){
                    mqttClient.publish(0, false, topicName, deviceMessage.getMessage().toString());
                }else if (transport.equals(ServerType.TCP.getCode())){
                    //处理TCP下发
                    ModbusPollMsg modbusPollMsg = new ModbusPollMsg();
                    modbusPollMsg.setSerialNumber(serialNumber);
                    modbusPollMsg.setProductId(deviceStatusVO.getProductId());
                    List<String> commandList = new ArrayList<>();
                    commandList.add(deviceMessage.getMessage().toString());
                    modbusPollMsg.setCommandList(commandList);
                    modbusPollMsg.setTransport(deviceStatusVO.getTransport());
                    if (deviceStatusVO.getStatus() != DeviceStatus.ONLINE.getType()){
                        log.info("设备：[{}],不在线",modbusPollMsg.getSerialNumber());
                        return;
                    }
                    ModbusPollJob modbusPollJob = new ModbusPollJob();
                    modbusPollJob.setPollMsg(modbusPollMsg);
                    modbusPollJob.setType(1);
                    MessageProducer.sendPropFetch(modbusPollJob);
                }
                break;
            case PROPERTY_POST:
                //下发的不经过mqtt或TCP直接转发到数据处理模块
                DeviceReportBo reportBo = DeviceReportBo.builder()
                        .serverType(ServerType.explain(transport))
                        .data(BitUtils.hexStringToByteArray(deviceMessage.getMessage().toString()))
                        .platformDate(DateUtils.getNowDate())
                        .serialNumber(serialNumber)
                        .topicName(topicName).build();
                MessageProducer.sendPublishMsg(reportBo);
                break;
        }
    }

    @Override
    public String messageEncode(ModbusRtu modbusRtu) {
        ByteBuf out;
        if (SYDHConstant.PROTOCOL.ModbusTcpOverRtu.equals(modbusRtu.getProtocolCode())) {
            ModbusTcp modbusTcp = modbusTcpOverRtuEncoder.change(modbusRtu);
//            if (StringUtils.isNotEmpty(modbusTcp.getSerialNumber())) {
//                modbusTcpId = redisCache.getCacheModbusTcpId(modbusTcp.getSerialNumber());
//            }
            modbusTcp.setTransactionIdentifier(0);
            out = modbusTcpOverRtuEncoder.encode(modbusTcp);
        } else {
            //兼容15、16功能码
            if (modbusRtu.getCode() == ModbusCode.Write10.getCode()){
                //计算:字节数=2*N；N为寄存器个数
                modbusRtu.setBitCount(2 * modbusRtu.getCount());
            }else if (modbusRtu.getCode() == ModbusCode.Write0F.getCode()){
                //计算:字节数=N/8 余数为0是 N需要再+1。N是线圈个数
                int i = modbusRtu.getCount() / 8;
                if (modbusRtu.getCount() % 8!= 0){
                    i++;
                }
                modbusRtu.setBitCount(i);
                //计算线圈值，前端返回二进制的字符串，需要将高低位先翻转，在转化为16进制
                String reverse = StringUtils.reverse(modbusRtu.getBitString());
                modbusRtu.setBitData(BitUtils.string2bytes(reverse));
            }
            out = modbusMessageEncoder.encode(modbusRtu);
        }
        byte[] result = new byte[out.writerIndex()];
        out.readBytes(result);
        ReferenceCountUtil.release(out);
        if (SYDHConstant.PROTOCOL.ModbusTcpOverRtu.equals(modbusRtu.getProtocolCode())) {
//            redisCache.cacheModbusTcpData(modbusRtu.getSerialNumber(), modbusTcpId, s);
            return ByteBufUtil.hexDump(result);
        }
        return ByteBufUtil.hexDump(CRC16Utils.AddCRC(result));
    }

    @Override
    public List<ThingsModelSimpleItem> messageDecode(DeviceMessage deviceMessage) {
        // ProductCode productCode = productService.getProtocolBySerialNumber(deviceMessage.getSerialNumber());
        // ByteBuf buf = Unpooled.wrappedBuffer(ByteBufUtil.decodeHexDump(deviceMessage.getMessage()));
        // DeviceData deviceData = DeviceData.builder()
        //         .buf(buf)
        //         .productId(productCode.getProductId())
        //         .serialNumber(productCode.getSerialNumber())
        //         .data(ByteBufUtil.getBytes(buf))
        //         .build();
        // return modbusRtuPakProtocol.decodeMessage(deviceData, deviceMessage.getSerialNumber());
        return null;
    }

    /**
     * 变量读取
     * @param readVO
     */
    @Override
    public void readVariableValue(VariableReadVO readVO){
        String serialNumber = readVO.getSerialNumber();
        assert !Objects.isNull(serialNumber) : "设备编号为空";
//        DeviceAndProtocol deviceAndProtocol = deviceService.selectProtocolBySerialNumber(serialNumber);
        DeviceAndProtocol deviceProtocolDetail = deviceService.getDeviceProtocolDetail(readVO.getSerialNumber());
        Optional.ofNullable(deviceProtocolDetail).orElseThrow(() -> new ServiceException(StringUtils.format(MessageUtils.message("mqtt.service.send.device.not.exist"), readVO.getSerialNumber())));
        String address = deviceProtocolDetail.getAddress();
        Long subProductId = deviceProtocolDetail.getProductId();
        Integer subDeviceType = deviceProtocolDetail.getDeviceType();
        // 处理子设备，用绑定网关下发指令
        if (DeviceType.SUB_GATEWAY.getCode() == deviceProtocolDetail.getDeviceType()) {
            Optional.ofNullable(deviceProtocolDetail.getAddress()).orElseThrow(() -> new ServiceException(StringUtils.format(MessageUtils.message("mqtt.service.send.device.not.exist"), readVO.getSerialNumber())));
            DeviceAndProtocol gwDeviceAndProtocol = deviceService.getDeviceProtocolDetail(deviceProtocolDetail.getGwSerialNumber());
            Optional.ofNullable(gwDeviceAndProtocol).orElseThrow(() -> new ServiceException(StringUtils.format(MessageUtils.message("mqtt.service.send.device.not.exist"), readVO.getSerialNumber())));
            serialNumber = gwDeviceAndProtocol.getSerialNumber();
            deviceProtocolDetail = gwDeviceAndProtocol;
        }
        if (!(SYDHConstant.PROTOCOL.ModbusRtu.equals(deviceProtocolDetail.getProtocolCode()) || SYDHConstant.PROTOCOL.ModbusTcpOverRtu.equals(deviceProtocolDetail.getProtocolCode())
                || SYDHConstant.PROTOCOL.ModbusTcp.equals(deviceProtocolDetail.getProtocolCode()))) {
            throw new ServiceException("非modbus协议请先配置主动采集协议");
        }
        Integer type = readVO.getType();
        type = Objects.isNull(type) ? 1 : type;
        List<ModbusDevice> modbusDeviceList = new ArrayList<>();
        ModbusDevice modbusDevice = new ModbusDevice();
        modbusDevice.setDeviceId(deviceProtocolDetail.getDeviceId());
        modbusDevice.setSerialNumber(deviceProtocolDetail.getSerialNumber());
        modbusDevice.setDeviceIp(deviceProtocolDetail.getDeviceIp());
        modbusDevice.setStatus(deviceProtocolDetail.getStatus());
        if (Objects.nonNull(deviceProtocolDetail.getDevicePort())) {
            modbusDevice.setDevicePort(deviceProtocolDetail.getDevicePort());
        }
        if (type  == 1){
            //单个变量获取
            String identifier = readVO.getIdentifier();
            ThingsModelValueItem thingModels = itslCache.getSingleThingModels(subProductId, identifier);
            ModbusConfig config = thingModels.getConfig();
            if (Objects.isNull(config)) {
                throw new ServiceException(MessageUtils.message("modbus.point.not.config"));
            }
            thingModels.getConfig().setModbusCode(ModbusUtils.getReadModbusCode(config.getType(),config.getIsReadonly()));
            if (subDeviceType == DeviceType.SUB_GATEWAY.getCode()) {
                //这里获取绑定网关时，设置的子设备地址
                thingModels.getConfig().setAddress(StringUtils.isNotEmpty(address) ? address : deviceProtocolDetail.getModbusAddress());
            } else if (DeviceType.DIRECT_DEVICE.getCode() == subDeviceType) {
                if (StringUtils.isEmpty(config.getAddress())) {
                    thingModels.getConfig().setAddress(deviceProtocolDetail.getModbusAddress());
                } else {
                    address = config.getAddress();
                }
            }
            if (SYDHConstant.PROTOCOL.ModbusTcp.equals(deviceProtocolDetail.getProtocolCode())) {
                Command command = new Command();
                command.setAddress(Integer.parseInt(address));
                command.setRegister(thingModels.getConfig().getRegister());
                ModbusConfig modbusConfig = thingModels.getConfig();
                int code;
                if (1 == modbusConfig.getType()) {
                    code = 1 == modbusConfig.getIsReadonly() ? 2 : 1;
                } else {
                    code = 1 == modbusConfig.getIsReadonly() ? 4 : 3;
                }
                command.setCode(code);
                command.setQuantity(1);
                List<Command> commandList = new ArrayList<>();
                commandList.add(command);
                modbusDevice.setCommands(commandList);
                modbusDevice.setCommand(JSON.toJSONString(commandList));
                modbusDeviceList.add(modbusDevice);
                modbusScheduler.processBatch(modbusDeviceList);
                return;
            }

            MQSendMessageBo messageBo = new MQSendMessageBo();
            messageBo.setThingsModel(JSON.toJSONString(thingModels));
            messageBo.setSerialNumber(readVO.getSerialNumber());
            messageBo.setParentSerialNumber(readVO.getParentSerialNumber());
            FunctionCallBackBo encode;
            if (SYDHConstant.PROTOCOL.ModbusTcpOverRtu.equals(deviceProtocolDetail.getProtocolCode())) {
                encode = modbusTcpOverRtuProtocol.encode(messageBo);
            } else {
                encode = modbusProtocol.encode(messageBo);
            }
            List<String> commandList = new ArrayList<>();
            commandList.add(encode.getSources());
            ModbusPollMsg modbusPollMsg = new ModbusPollMsg();
            modbusPollMsg.setSerialNumber(serialNumber);
            modbusPollMsg.setProductId(deviceProtocolDetail.getProductId());
            modbusPollMsg.setCommandList(commandList);
            DeviceStatusVO deviceStatusVO = deviceService.selectDeviceStatusAndTransportStatus(serialNumber);
            modbusPollMsg.setTransport(deviceStatusVO.getTransport());
            if (deviceStatusVO.getStatus() != DeviceStatus.ONLINE.getType()){
                log.info("设备：[{}],不在线",modbusPollMsg.getSerialNumber());
                return;
            }
            ModbusPollJob job = new ModbusPollJob();
            job.setPollMsg(modbusPollMsg);
            job.setType(1);
            MessageProducer.sendPropFetch(job);
        }else {
            List<ModbusJobVO> modbusJobVOList = modbusJobService.selectDevicesJobByDeviceType(serialNumber);
            if (SYDHConstant.PROTOCOL.ModbusTcp.equals(deviceProtocolDetail.getProtocolCode())) {
                ModbusJobVO modbusJobVO = modbusJobVOList.get(0);
                String command = modbusJobVO.getCommand();
                List<Command> commandList = JsonUtils.parseArray(command, Command.class);
                if (StringUtils.isNotEmpty(address)) {
                    String finalAddress = address;
                    List<Command> commandList1 = commandList.stream().filter(command1 -> Integer.parseInt(finalAddress) == command1.getAddress()).collect(Collectors.toList());
                    modbusDevice.setCommands(commandList1);
                    modbusDevice.setCommand(JSON.toJSONString(commandList1));
                } else {
                    modbusDevice.setCommands(commandList);
                    modbusDevice.setCommand(command);
                }
                modbusDeviceList.add(modbusDevice);
                modbusScheduler.processBatch(modbusDeviceList);
            } else {
                if (StringUtils.isNotEmpty(address)) {
                    String finalAddress1 = address;
                    modbusJobVOList = modbusJobVOList.stream().filter(m -> finalAddress1.equals(m.getAddress())).collect(Collectors.toList());
                }
                ModbusPollMsg msg = new ModbusPollMsg();
                msg.setCommandList(modbusJobVOList.stream().map(ModbusJobVO::getCommand).collect(Collectors.toList()));
                msg.setTransport(deviceProtocolDetail.getTransport());
                msg.setProductId(deviceProtocolDetail.getProductId());
                msg.setSerialNumber(serialNumber);
                ModbusPollJob job = new ModbusPollJob();
                job.setPollMsg(msg);
                job.setType(1);
                MessageProducer.sendPropFetch(job);
            }
        }
    }

    @Override
    public String commandGenerate(MQSendMessageBo messageBo) {
        String result;
        String protocolCode = messageBo.getDp().getProtocolCode();
        Long productId = messageBo.getDp().getProductId();
        JSONObject params = messageBo.getParams();
        if (SYDHConstant.PROTOCOL.JsonArray.equals(protocolCode)) {
            List<ValueItem> list = new ArrayList<>();
            ValueItem valueItem = new ValueItem();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                valueItem.setId(entry.getKey());
                valueItem.setValue(entry.getValue() + "");
                valueItem.setRemark("");
                list.add(valueItem);
            }
            result = JSON.toJSONString(list);
        } else {
            Map<String, ThingsModelValueItem> valueItemMap = itslCache.getCacheThMapByProductId(productId);
            ThingsModelValueItem thingsModelValueItem = new ThingsModelValueItem();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                thingsModelValueItem = valueItemMap.get(entry.getKey());
            }
            messageBo.setThingsModel(JSON.toJSONString(thingsModelValueItem));
            FunctionCallBackBo functionCallBackBo = mqttMessagePublish.buildMessage(messageBo);
            result = functionCallBackBo.getSources();
        }
        return result;
    }

}
