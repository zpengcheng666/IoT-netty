package com.sydh.iot.model;

import com.sydh.common.extend.core.domin.model.device.DeviceAndProtocol;
import lombok.Data;

import java.util.List;

/**
 * @author gsb
 * @date 2024/7/3 16:06
 */
@Data
public class ModbusPollBo {

    private DeviceAndProtocol gateway;
    private List<ModbusJobBo> commandList;
}
