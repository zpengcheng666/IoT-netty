package com.fastbee.iot.mapper;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.iot.domain.FunctionLog;
import com.fastbee.iot.domain.GoviewProject;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 项目Mapper接口
 *
 * @author kami
 * @date 2022-10-27
 */
@Repository
public interface GoviewProjectMapper extends BaseMapperX<GoviewProject>
{

}
