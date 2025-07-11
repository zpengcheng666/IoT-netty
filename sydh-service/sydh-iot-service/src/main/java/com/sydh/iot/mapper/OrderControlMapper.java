package com.sydh.iot.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.iot.domain.OrderControl;
import com.sydh.iot.model.vo.OrderControlVO;
import org.apache.ibatis.annotations.Param;

/**
 * 指令权限控制Mapper接口
 *
 * @author kerwincui
 * @date 2024-07-01
 */
public interface OrderControlMapper extends BaseMapperX<OrderControl>
{

    /**
     * 查询指令权限控制列表
     *
     * @param orderControlVO 指令权限控制
     * @return 指令权限控制集合
     */
    public Page<OrderControlVO> selectOrderControlVoPage(Page<OrderControlVO> page, @Param("orderControlVO") OrderControlVO orderControlVO);

}
