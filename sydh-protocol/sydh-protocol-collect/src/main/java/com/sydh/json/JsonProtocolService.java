package com.sydh.json;

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
import com.sydh.iot.model.ThingsModels.ValueItem;
import com.sydh.protocol.base.protocol.IProtocol;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @author gsb
 * @date 2022/10/10 16:55
 */
@Slf4j
@Component
@SysProtocol(name = "JSONArray解析协议", protocolCode = SYDHConstant.PROTOCOL.JsonArray, description = "系统内置JSONArray解析协议")
public class JsonProtocolService implements IProtocol {

    /**
     * 上报数据格式 <p>
     * [
     *   {
     *     "id": "switch",
     *     "value": "0"
     *   },
     *   {
     *     "id": "gear",
     *     "value": "0"
     *   }
     * ]
     */
    @Override
    public DeviceReport decode(DeviceData deviceData, String clientId) {
        try {
            DeviceReport reportMessage = new DeviceReport();
            // bytep[] 转String
            String data = new String(deviceData.getData(), StandardCharsets.UTF_8);
            List<ThingsModelSimpleItem> values = JSON.parseArray(data, ThingsModelSimpleItem.class);
            //上报数据时间
            for (ThingsModelSimpleItem value : values) {
                value.setTs(DateUtils.getNowDate());
            }
            reportMessage.setThingsModelSimpleItem(values);
            reportMessage.setClientId(clientId);
            reportMessage.setSerialNumber(clientId);
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
            JSONObject params = message.getParams();
            ValueItem valueItem = new ValueItem();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                valueItem.setId(entry.getKey());
                valueItem.setValue(entry.getValue() + "");
                valueItem.setRemark("");
            }
            String msg = "[" + JSONObject.toJSONString(valueItem) + "]";
            callBack.setSources(msg);
            callBack.setMessage(msg.getBytes());
            return callBack;
        } catch (Exception e) {
            log.error("=>指令编码异常,device={},data={}", message.getSerialNumber(),
                    message.getParams());
            return null;
        }
    }
}
