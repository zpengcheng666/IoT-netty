package com.sydh.iot.data.service;

import com.sydh.common.extend.core.domin.mq.MQSendMessageBo;
import com.sydh.common.extend.core.domin.mq.message.DeviceMessage;
import com.sydh.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.sydh.iot.model.vo.VariableReadVO;
import com.sydh.modbus.model.ModbusRtu;

import java.util.List;

/**
 * 设备消息Service接口
 */
public interface IDeviceMessageService {

    void messagePost(DeviceMessage deviceMessage);

    String messageEncode(ModbusRtu modbusRtu);

    List<ThingsModelSimpleItem> messageDecode(DeviceMessage deviceMessage);


    /**
     * 变量读取
     * @param readVO
     */
    public void readVariableValue(VariableReadVO readVO);

    String commandGenerate(MQSendMessageBo messageBo);
}
