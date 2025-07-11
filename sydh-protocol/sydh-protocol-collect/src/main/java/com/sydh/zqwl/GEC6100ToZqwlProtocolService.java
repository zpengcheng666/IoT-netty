package com.sydh.zqwl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.sydh.common.annotation.SysProtocol;
import com.sydh.common.constant.SYDHConstant;
import com.sydh.common.exception.ServiceException;
import com.sydh.common.extend.core.domin.mq.DeviceReport;
import com.sydh.common.extend.core.domin.mq.MQSendMessageBo;
import com.sydh.common.extend.core.domin.mq.message.DeviceData;
import com.sydh.common.extend.core.domin.mq.message.FunctionCallBackBo;
import com.sydh.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.sydh.common.utils.DateUtils;
import com.sydh.common.utils.MessageUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.common.utils.gateway.CRC16Utils;
import com.sydh.iot.domain.ModbusConfig;
import com.sydh.iot.model.ThingsModels.ThingsModelValueItem;
import com.sydh.modbus.codec.ModbusEncoder;
import com.sydh.modbus.model.ModbusRtu;
import com.sydh.protocol.base.protocol.IProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author bill
 */
@Slf4j
@Component
@SysProtocol(name = "GEC6100D发电机控制器协议", protocolCode = SYDHConstant.PROTOCOL.GEC6100D, description = "GEC6100D发电机控制器协议-繁易")
public class GEC6100ToZqwlProtocolService implements IProtocol {

    @Resource
    private ModbusEncoder messageEncoder;


    @Override
    public DeviceReport decode(DeviceData deviceData, String clientId) {

        try {
            DeviceReport reportMessage = new DeviceReport();
            String data = new String(deviceData.getData(), StandardCharsets.UTF_8);
            List<ThingsModelSimpleItem> result = new ArrayList<>();
            Map<String, Object> values = JSON.parseObject(data, Map.class);
            if (values.containsKey("sn")) {
                reportMessage.setIsReply(true);
                for (Map.Entry<String, Object> entry : values.entrySet()) {
                    ThingsModelSimpleItem item = new ThingsModelSimpleItem();
                    item.setTs(DateUtils.getNowDate());
                    item.setId(entry.getKey());
                    item.setValue(entry.getValue()+"");
                    result.add(item);
                    if (entry.getKey().equals("sn")){
                        reportMessage.setMessageId(entry.getValue()+"");
                    }
                }
            } else {
                for (Map.Entry<String, Object> entry : values.entrySet()) {
                    if (entry.getValue() instanceof JSONArray) {
                        JSONArray array = (JSONArray) entry.getValue();
                        int index = parseKey(entry.getKey());
                        for (int i = 0; i < array.size(); i++) {
                            ThingsModelSimpleItem item = new ThingsModelSimpleItem();
                            item.setTs(DateUtils.getNowDate());
                            String s = array.get(i) + "";
                            if (s.equals("32768")){
                                s = "-1";
                            }
                            item.setValue(s);
                            item.setId("k" + (index + i));
                            result.add(item);
                        }
                    }
                }
            }
            reportMessage.setThingsModelSimpleItem(result);
            reportMessage.setClientId(deviceData.getSerialNumber());
            reportMessage.setSerialNumber(deviceData.getSerialNumber());
            reportMessage.setProductId(deviceData.getProductId());
            reportMessage.setProtocolCode(SYDHConstant.PROTOCOL.GEC6100D);
            reportMessage.setSources(data);
            return reportMessage;
        } catch (Exception e) {
            throw new ServiceException(StringUtils.format(MessageUtils.message("protocol.data.parse.exception"), e.getMessage()));
        }
    }

    @Override
    public FunctionCallBackBo encode(MQSendMessageBo message) {
        try {
            FunctionCallBackBo callBack = new FunctionCallBackBo();
            ModbusRtu rtu = new ModbusRtu();
            String thingsModel = message.getThingsModel();
            ThingsModelValueItem item = JSONObject.parseObject(thingsModel, ThingsModelValueItem.class);
            ModbusConfig modbusConfig = item.getConfig();
            switch (modbusConfig.getModbusCode()) {
                case Write05:
                case Write06:
                    write0506(message,item, rtu);
                    break;
                case Write10:
                    write10(message,item,rtu);
                    break;

            }
            rtu.setByteCount(1);
            //rtu.setByteLength(2);
            ByteBuf out = messageEncoder.encode(rtu);
            byte[] data = new byte[out.writerIndex()];
            out.readBytes(data);
            ReferenceCountUtil.release(out);
            byte[] bytes = CRC(data);
            //下发指令
            String hexDump = ByteBufUtil.hexDump(bytes);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("mb", hexDump);
            jsonObject.put("sn", message.getMessageId());
            jsonObject.put("ack", 1);
            jsonObject.put("crc", 1);
            String result = JSONObject.toJSONString(jsonObject);
            callBack.setMessage(result.getBytes());
            callBack.setSources(result);
            return callBack;
        } catch (Exception e) {
            log.error("=>指令编码异常,device={},data={},msg={}", message.getSerialNumber(),
                    message.getParams(), e);
            throw new ServiceException(e.getMessage());
        }
    }

    public static int parseKey(String key) {
        String s = key.substring(1);
        return Integer.parseInt(s);
    }

    /**
     * writ05/06指令配置
     */
    private void write0506(MQSendMessageBo message,ThingsModelValueItem item, ModbusRtu rtu) {
        ModbusConfig modbusConfig = item.getConfig();
        rtu.setRegister(modbusConfig.getRegister());
        String value = message.getValue();
        int data;
        if (value.contains("0x")) {
            data = Integer.parseInt(value.substring(2), 16);
        } else {
            data = Integer.parseInt(value);
        }
        rtu.setWriteData(data);
        rtu.setCode(modbusConfig.getModbusCode().getCode());
        rtu.setAddress(modbusConfig.getAddress() == null ? 1 : Integer.parseInt(modbusConfig.getAddress()));
    }

    /**
     * writ05/06指令配置
     */
    private void write10(MQSendMessageBo message,ThingsModelValueItem item, ModbusRtu rtu) {
        ModbusConfig modbusConfig = item.getConfig();
        rtu.setRegister(modbusConfig.getRegister());
        String value = message.getValue();
        int data = Integer.parseInt(value);
        //rtu.setControl(data);
        rtu.setCode(modbusConfig.getModbusCode().getCode());
        rtu.setAddress(modbusConfig.getAddress() == null ? 1 : Integer.parseInt(modbusConfig.getAddress()));
    }

    public byte[] CRC(byte[] source) {
        byte[] result = new byte[source.length + 2];
        byte[] crc16Byte = CRC16Utils.getCrc16Byte(source);
        System.arraycopy(source, 0, result, 0, source.length);
        System.arraycopy(crc16Byte, 0, result, result.length - 2, 2);
        return result;
    }
}
