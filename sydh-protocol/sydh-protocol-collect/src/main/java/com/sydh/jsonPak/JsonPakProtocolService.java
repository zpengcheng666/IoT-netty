package com.sydh.jsonPak;

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
import com.sydh.protocol.base.protocol.IProtocol;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author gsb
 * @date 2022/10/10 16:55
 */
@Slf4j
@Component
@SysProtocol(name = "JSONObject解析协议",protocolCode = SYDHConstant.PROTOCOL.JsonObject,description = "系统内置JSONObject解析协议")
public class JsonPakProtocolService implements IProtocol {

    /**
     * 上报数据格式 <p>
     * {
     *  "params": {
     *     "DI" : 0,
     * 	"DO" : 1
     *   }
     * }
     */
    @Override
    public DeviceReport decode(DeviceData deviceData, String clientId) {
        try {
            DeviceReport reportMessage = new DeviceReport();
            // bytep[] 转String
            String data = new String(deviceData.getData(),StandardCharsets.UTF_8);
            JSONObject params = JSONObject.parseObject(data);
            List<ThingsModelSimpleItem> result = new ArrayList<>();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                ThingsModelSimpleItem item = new ThingsModelSimpleItem();
                item.setTs(DateUtils.getNowDate());
                item.setValue(entry.getValue()+"");
                item.setId(entry.getKey());
                result.add(item);
            }
            reportMessage.setThingsModelSimpleItem(result);
            reportMessage.setIsPackage(true);
            reportMessage.setMessageId("0");
            reportMessage.setClientId(clientId);
            reportMessage.setSerialNumber(clientId);
            return reportMessage;
        }catch (Exception e){
            throw new ServiceException(StringUtils.format(MessageUtils.message("protocol.data.parse.exception"), e.getMessage()));
        }
    }

    @Override
    public FunctionCallBackBo encode(MQSendMessageBo message) {
        try {
            FunctionCallBackBo callBack = new FunctionCallBackBo();
            String msg = message.getParams().toString();
            callBack.setMessage(msg.getBytes());
            callBack.setSources(msg);
            return callBack;
        }catch (Exception e){
            log.error("=>指令编码异常,device={},data={}",message.getSerialNumber(),
                    message.getParams());
            return null;
        }
    }
}
