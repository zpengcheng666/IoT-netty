package com.sydh.iot.mapper;

import java.util.Date;
import java.util.List;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.iot.domain.Device;
import com.sydh.iot.domain.FunctionLog;
import com.sydh.iot.model.HistoryModel;
import com.sydh.iot.model.vo.ThingsModelLogCountVO;
import com.sydh.iot.model.param.DataCenterParam;
import com.sydh.iot.model.vo.FunctionLogVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 设备服务下发日志Mapper接口
 *
 * @author kerwincui
 * @date 2022-10-22
 */
@Repository
public interface FunctionLogMapper extends BaseMapperX<FunctionLog>
{

    /**
     * 查询设备服务下发日志列表
     *
     * @param functionLogVO 设备服务下发日志
     * @return 设备服务下发日志集合
     */
    public Page<FunctionLogVO> selectFunctionLogList(Page<FunctionLog> page, @Param("functionLogVO") FunctionLogVO functionLogVO);

    /**
     * 根据标识符前缀和设备编号批量删除日志
     *
     * @param
     * @return 结果
     */
    public int deleteFunctionLogByPreIdentify(FunctionLog functionLog);

    /**
     * 批量更新日志状态值
     * @param log 参数
     */
    public void updateFuncLogBatch(FunctionLog log);

    /**
     * 查询物模型历史数据
     * @param functionLogVO 功能日志
     * @return java.util.List<com.sydh.iot.model.HistoryModel>
     */
    List<HistoryModel> listHistory(FunctionLogVO functionLogVO);

    /**
     * 统计设备物模型指令下发数量
     * @param dataCenterParam 传参
     * @return com.sydh.common.core.domain.AjaxResult
     */
    List<ThingsModelLogCountVO> countThingsModelInvoke(@Param("dataCenterParam") DataCenterParam dataCenterParam, @Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

    Long selectFunctionLogCount(@Param("device")Device device);
}
