package com.sydh.common.extend.core.domin.mq;

import lombok.Data;

/**
 * @author bill
 */
@Data
public class SubDeviceBo {
    /**
     * 网关设备id
     */

    private String parentClientId;

    /**
     * 子设备id
     */
    private String subClientId;

    /**
     * 从机地址
     */
    private String address;
    /**
     * 子设备名称
     */
    private String subDeviceName;

    private Long subProductId;
}
