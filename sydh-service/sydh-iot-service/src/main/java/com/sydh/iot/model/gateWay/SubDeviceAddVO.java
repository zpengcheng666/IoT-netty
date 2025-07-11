package com.sydh.iot.model.gateWay;

import lombok.Data;

/**
 * @author gsb
 * @date 2024/5/29 15:11
 */
@Data
public class SubDeviceAddVO {
    /**
     * 网关设备id
     */
    private String parentClientId;
    /**
     * 子设备id集合
     */
    private String[] subClientIds;

    /**
     * 网关产品id
     */
    private Long parentProductId;
}
