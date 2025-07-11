package com.sydh.iot.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.iot.domain.SubGateway;
import com.sydh.iot.model.gateWay.GateSubDeviceParamsVO;
import com.sydh.iot.model.gateWay.GateSubDeviceVO;
import com.sydh.iot.model.gateWay.SubDeviceListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 网关与子设备关联Mapper接口
 *
 * @author gsb
 * @date 2024-05-27
 */
public interface SubGatewayMapper extends BaseMapperX<SubGateway>
{

    /**
     * 查询网关与子设备关联列表
     *
     * @param gateway 网关与子设备关联
     * @return 网关与子设备关联集合
     */
    public Page<SubDeviceListVO> selectGatewayList(Page<SubDeviceListVO> page, @Param("gateway") SubGateway gateway);

    /**
     * 获取可选的网关子设备列表
     * @return
     */
    Page<GateSubDeviceVO> getIsSelectGateSubDevice(Page<GateSubDeviceVO> page, @Param("subDeviceVO")GateSubDeviceVO subDeviceVO);

    /**
     * @description: 查询子设备地址
     * @param: subDeviceId 子设备id
     * @return: java.lang.Integer
     */
    String selectSlaveIdBySubDeviceId(Long subDeviceId);

    /**
     * @date 2025-04-27 15:24
     * @param parentClientId
     * @param addressList
     * @return java.util.List<com.sydh.iot.model.gateWay.GateSubDeviceParamsVO>
     */
    List<GateSubDeviceParamsVO> selectSubModbusParamsVO(@Param("parentClientId") String parentClientId, @Param("addressList") List<String> addressList);
}
