package com.sydh.iot.model.gateWay;

import com.sydh.common.core.domain.PageEntity;
import lombok.Data;

/**
 * 网关子设备
 * @author bill
 */
@Data
public class GateSubDeviceVO extends PageEntity {

    /**
     * 设备id
     */
    private Long deviceId;
    /**
     * 设备编号
     */
    private String serialNumber;
    /**
     * 设备名称
     */
    private String deviceName;
}
