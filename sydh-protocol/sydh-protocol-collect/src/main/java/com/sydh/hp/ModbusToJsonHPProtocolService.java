package com.sydh.hp;

import com.alibaba.fastjson2.JSON;
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
import com.sydh.iot.domain.ModbusConfig;
import com.sydh.iot.domain.SubGateway;
import com.sydh.iot.model.ThingsModels.ThingsModelValueItem;
import com.sydh.iot.service.ISubGatewayService;
import com.sydh.modbusToJson.FYModel;
import com.sydh.protocol.base.protocol.IProtocol;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author gsb
 * @date 2023/8/14 16:04
 */
@Slf4j
@Component
@SysProtocol(name = "Modbus转Json解析协议-华普物联",protocolCode = SYDHConstant.PROTOCOL.ModbusToJsonHP,description = "modbus转json解析协议-华普物联")
public class ModbusToJsonHPProtocolService implements IProtocol {

    @Resource
    private ISubGatewayService subGatewayService;

    /**
     * 上报数据格式:              <p>
     *  device1：   从机1标识     <p>
     *  name：      物模型标识符   <p>
     *  value：     上报值        <p>
     * {
     * 	"device1": [
     *                {
     * 			"name": "J2",
     * 			"value": 8.331631
     *        },
     *        {
     * 			"name": "J1",
     * 			"value": -130.123718
     *        }
     * 	],
     * 	"device2": [
     *        {
     * 			"name": "J4",
     * 			"value": -16.350224
     *        },
     *        {
     * 			"name": "J3",
     * 			"value": 94.769806
     *        }
     * 	]
     * }
     *
     * 下发报文格式<p>
     * device   从机编号  <p>
     * name     标识符    <p>
     * value    值        <p>
     * serNo    流水号    <p>
     * {
     * 	"device": 1,
     * 	"name": "template",
     * 	"value": 111,
     * 	"serNo": "213245489543789"
     * }
     * </p>
     *
     * 下发指令回复格式<p>
     * serNo   平台的流水号，用于对应回复消息   <p>
     * ack     下发指令状态 0是失败 1是成功    <p>
     *   {
     *  "serNo": "213245489543789",
     *  "ack": 1
     * }
     * </p>
     *
     */
    @Override
    public DeviceReport decode(DeviceData deviceData, String clientId) {
        try {
            DeviceReport reportMessage = new DeviceReport();
            String data = new String(deviceData.getData(),StandardCharsets.UTF_8);
            List<ThingsModelSimpleItem> result = new ArrayList<>();
            Map<String,Object> values = JSON.parseObject(data, Map.class);
            if (values.containsKey("serNo")){
                reportMessage.setIsReply(true);
                reportMessage.setProtocolCode(SYDHConstant.PROTOCOL.ModbusToJsonHP);
                for (Map.Entry<String, Object> entry : values.entrySet()) {
                    ThingsModelSimpleItem simpleItem = new ThingsModelSimpleItem();
                    simpleItem.setTs(DateUtils.getNowDate());
                    simpleItem.setId(entry.getKey());
                    simpleItem.setValue(entry.getValue()+"");
                    if (entry.getKey().equals("serNo")){
                        reportMessage.setMessageId(entry.getValue()+"");
                    }
                    result.add(simpleItem);
                }
            }else {
                for (Map.Entry<String, Object> entry : values.entrySet()) {
                    String slaveKey = entry.getKey();
                    Integer address = StringUtils.matcherNum(slaveKey);
                    matchSubDev(reportMessage,clientId,address);
                    List<FYModel> valueList = JSON.parseArray(JSON.toJSONString(entry.getValue()), FYModel.class);
                    for (FYModel fyModel : valueList) {
                        ThingsModelSimpleItem item = new ThingsModelSimpleItem();
                        item.setTs(DateUtils.getNowDate());
                        item.setValue(fyModel.getValue());
                        item.setId(fyModel.getName());
                        result.add(item);
                    }
                }
            }
            reportMessage.setThingsModelSimpleItem(result);
            reportMessage.setSources(data);
            return reportMessage;
        }catch (Exception e){
            throw new ServiceException(StringUtils.format(MessageUtils.message("protocol.data.parse.exception"), e.getMessage()));
        }
    }


    /**
     * 匹配子设备编号
     */
    private void matchSubDev(DeviceReport report, String serialNumber, Integer address) {
        List<SubGateway> subDeviceList = subGatewayService.getSubDeviceListByGw(serialNumber);
        if (!CollectionUtils.isEmpty(subDeviceList)) {
            for (SubGateway vo : subDeviceList) {
                if (vo.getAddress().equals(address.toString())) {
                    report.setSerialNumber(vo.getSubClientId());
                    report.setProductId(vo.getSubProductId());
                    report.setClientId(vo.getSubClientId());
                    break;
                }
            }
        }
    }

    @Override
    public FunctionCallBackBo encode(MQSendMessageBo message) {
        try {
            FunctionCallBackBo callBack = new FunctionCallBackBo();
            String thingsModel = message.getThingsModel();
            ThingsModelValueItem item = JSONObject.parseObject(thingsModel, ThingsModelValueItem.class);
            ModbusConfig modbusConfig = item.getConfig();
            JSONObject params = new JSONObject();
            params.put("device",modbusConfig.getAddress());
            params.put("name",message.getIdentifier());
            params.put("value",message.getValue());
            params.put("serNo",message.getMessageId());
            String msg = JSONObject.toJSONString(params);
            callBack.setSources(msg);
            callBack.setMessage(msg.getBytes());
            return callBack;
        }catch (Exception e){
            log.error("=>指令编码异常,device={}",message.getSerialNumber());
            return null;
        }
    }

}
