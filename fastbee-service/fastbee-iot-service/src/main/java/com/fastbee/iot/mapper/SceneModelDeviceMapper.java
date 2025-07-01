package com.fastbee.iot.mapper;

import java.util.List;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.iot.domain.SceneModelDevice;
import org.apache.ibatis.annotations.Param;

/**
 * 场景管理关联设备Mapper接口
 *
 * @author kerwincui
 * @date 2024-05-21
 */
public interface SceneModelDeviceMapper extends BaseMapperX<SceneModelDevice>
{

    /**
     * 校验是否有使用计算公式
     * @param id 主键
     * @return int
     */
    int checkContainAliasFormule(Long id);

    /**
     * 查询场景关联设备
     * @param productId 产品id
     * @return java.util.List<com.fastbee.iot.domain.SceneModelDevice>
     */
    List<SceneModelDevice> listDeviceByProductId(Long productId);
}
