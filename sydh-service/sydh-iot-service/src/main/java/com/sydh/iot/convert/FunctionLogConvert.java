package com.sydh.iot.convert;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.iot.domain.FunctionLog;
import com.sydh.iot.model.vo.FunctionLogVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 设备服务下发日志Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */
@Mapper
public interface FunctionLogConvert
{

    FunctionLogConvert INSTANCE = Mappers.getMapper(FunctionLogConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param functionLog
     * @return 设备服务下发日志集合
     */
    FunctionLogVO convertFunctionLogVO(FunctionLog functionLog);

    /**
     * VO类转换为实体类集合
     *
     * @param functionLogVO
     * @return 设备服务下发日志集合
     */
    FunctionLog convertFunctionLog(FunctionLogVO functionLogVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param functionLogList
     * @return 设备服务下发日志集合
     */
    List<FunctionLogVO> convertFunctionLogVOList(List<FunctionLog> functionLogList);

    /**
     * VO类转换为实体类
     *
     * @param functionLogVOList
     * @return 设备服务下发日志集合
     */
    List<FunctionLog> convertFunctionLogList(List<FunctionLogVO> functionLogVOList);

    Page<FunctionLogVO> convertFunctionLogVoPage(Page<FunctionLog> functionLogPage);
}
