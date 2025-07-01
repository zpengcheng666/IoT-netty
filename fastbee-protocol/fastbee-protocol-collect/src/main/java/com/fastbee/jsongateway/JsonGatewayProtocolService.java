package com.fastbee.jsongateway;

import com.alibaba.fastjson2.JSON;
import com.fastbee.common.annotation.SysProtocol;
import com.fastbee.common.constant.FastBeeConstant;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.model.device.DeviceAndProtocol;
import com.fastbee.common.extend.core.domin.mq.DeviceReport;
import com.fastbee.common.extend.core.domin.mq.GwDeviceBo;
import com.fastbee.common.extend.core.domin.mq.MQSendMessageBo;
import com.fastbee.common.extend.core.domin.mq.message.DeviceData;
import com.fastbee.common.extend.core.domin.mq.message.FunctionCallBackBo;
import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.enums.DeviceType;
import com.fastbee.iot.service.IDeviceService;
import com.fastbee.jsongateway.model.JsonGatewayPoint;
import com.fastbee.protocol.base.protocol.IProtocol;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author gsb
 * @date 2022/10/10 16:55
 */
@Slf4j
@Component
@SysProtocol(name = "JSON网关解析协议", protocolCode = FastBeeConstant.PROTOCOL.JsonGateway, description = "系统内置JSON网关解析协议")
public class JsonGatewayProtocolService implements IProtocol {

    @Resource
    private IDeviceService deviceService;

    /**
     * 上报数据格式 <p>
     [{
     "deviceNumber":"164635379",
     "properties":{
            "latitude":"78°25'",
            "longitude":"25°47'"
        }
     }]
     */
    @Override
    public DeviceReport decode(DeviceData deviceData, String clientId) {
        try {
            DeviceReport reportMessage = new DeviceReport();
            // bytep[] 转String
            String data = new String(deviceData.getData(), StandardCharsets.UTF_8);
            JsonGatewayPoint jsonGatewayPoint = JSON.parseObject(data, JsonGatewayPoint.class);
            // 处理子设备
            this.matchSubDev(reportMessage, jsonGatewayPoint.getSerialNumber());
            Map<String, Object> map = jsonGatewayPoint.getProperties();
            List<ThingsModelSimpleItem> result = new ArrayList<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                ThingsModelSimpleItem item = new ThingsModelSimpleItem();
                item.setTs(DateUtils.getNowDate());
                item.setValue(entry.getValue()+"");
                item.setId(entry.getKey());
                result.add(item);
            }
            reportMessage.setThingsModelSimpleItem(result);
            reportMessage.setSources(data);
            return reportMessage;
        } catch (Exception e) {
            throw new ServiceException(StringUtils.format(MessageUtils.message("protocol.data.parse.exception"), e));
        }
    }


    /**
     * 下发 [{"id":"switch","value":"0","remark":""}]
     *
     * @param message
     * @return
     */
    @Override
    public FunctionCallBackBo encode(MQSendMessageBo message) {
        try {
            FunctionCallBackBo callBack = new FunctionCallBackBo();
            JsonGatewayPoint jsonGatewayPoint = new JsonGatewayPoint();
            jsonGatewayPoint.setSerialNumber(StringUtils.isNotEmpty(message.getSubSerialNumber()) ? message.getSubSerialNumber() : message.getSerialNumber());
            jsonGatewayPoint.setProperties(message.getParams());
            String msg = JSON.toJSONString(jsonGatewayPoint);
            callBack.setMessage(msg.getBytes());
            callBack.setSources(msg);
            return callBack;
        } catch (Exception e) {
            log.error("=>指令编码异常,device={},data={}", message.getSerialNumber(),
                    message.getParams());
            return null;
        }
    }

    /**
     * 匹配子设备编号
     */
    private void matchSubDev(DeviceReport report, String subSerialNumber) {
        DeviceAndProtocol deviceProtocolDetail = deviceService.getDeviceProtocolDetail(subSerialNumber);
        if (Objects.nonNull(deviceProtocolDetail) && DeviceType.SUB_GATEWAY.getCode() == deviceProtocolDetail.getDeviceType()) {
            report.setProductId(deviceProtocolDetail.getProductId());
            report.setSerialNumber(subSerialNumber);
            GwDeviceBo gwDeviceBo = new GwDeviceBo();
            gwDeviceBo.setProductId(deviceProtocolDetail.getGwProductId());
            gwDeviceBo.setSerialNumber(deviceProtocolDetail.getGwSerialNumber());
            report.setGwDeviceBo(gwDeviceBo);
        }
    }
}
