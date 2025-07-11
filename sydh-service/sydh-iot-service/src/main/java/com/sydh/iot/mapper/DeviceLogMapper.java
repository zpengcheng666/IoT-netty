package com.sydh.iot.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.iot.domain.Device;
import com.sydh.iot.domain.DeviceLog;
import com.sydh.iot.model.DeviceStatistic;
import com.sydh.iot.model.HistoryBo;
import com.sydh.iot.model.HistoryModel;
import com.sydh.iot.model.MonitorModel;
import com.sydh.iot.model.vo.ThingsModelLogCountVO;
import com.sydh.iot.model.param.DataCenterParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 设备日志Mapper接口
 *
 * @author kerwincui
 * @date 2022-01-13
 */
@Repository
public interface DeviceLogMapper extends BaseMapperX<DeviceLog>
{
    /**
     * 查询设备日志
     *
     * @param logId 设备日志主键
     * @return 设备日志
     */
    public DeviceLog selectDeviceLogByLogId(Long logId);

    /**
     * 查询日志分类总数
     *
     * @return 设备日志
     */
    public DeviceStatistic selectDeviceLogCount(@Param("device") Device device);

    /**
     * 查询设备日志列表
     *
     * @param deviceLog 设备日志
     * @return 设备日志集合
     */
    public Page<DeviceLog> selectDeviceLogList(Page<DeviceLog> page, @Param("deviceLog") DeviceLog deviceLog);

    /**
     * 查询设备监测数据
     *
     * @param deviceLog 设备日志
     * @return 设备日志集合
     */
    public List<MonitorModel> selectMonitorList(DeviceLog deviceLog);

    /**
     * 新增设备日志
     *
     * @param deviceLog 设备日志
     * @return 结果
     */
    public int insertDeviceLog(DeviceLog deviceLog);

    /**
     * 批量保存图片
     */
    public int saveBatch(@Param("list") List<DeviceLog> list);

    /**
     * 修改设备日志
     *
     * @param deviceLog 设备日志
     * @return 结果
     */
    public int updateDeviceLog(DeviceLog deviceLog);

    /**
     * 删除设备日志
     *
     * @param logId 设备日志主键
     * @return 结果
     */
    public int deleteDeviceLogByLogId(Long logId);

    /**
     * 批量删除设备日志
     *
     * @param logIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDeviceLogByLogIds(Long[] logIds);

    /**
     * 根据设备Ids批量删除设备日志
     *
     * @param deviceNumber 需要删除的数据设备Id
     * @return 结果
     */
    public int deleteDeviceLogByDeviceNumber(String deviceNumber);

    /**
     * 查询历史数据
     */
    public List<HistoryModel> selectHistoryList(DeviceLog deviceLog);

    /**
     * 查询物模型历史数据
     * @param deviceLog 设备日志
     * @return java.util.List<com.sydh.iot.model.HistoryModel>
     */
    List<HistoryModel> listHistory(DeviceLog deviceLog);

    Page<HistoryModel> listhistoryGroupByCreateTime(Page<HistoryModel> page, @Param("deviceLog") DeviceLog deviceLog, @Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

    IPage<DeviceLog> selectLastReport(IPage<DeviceLog> page, @Param("deviceLog") DeviceLog deviceLog, @Param("beginTime") Date beginTime, @Param("endTime") Date endTime);
    /**
     * 场景统计变量历史值
     * @param deviceLog 实体类
     * @return java.lang.String
     */
    List<String> selectStatsValue(DeviceLog deviceLog);

    /**
     * 统计设备物模型指令下发数量
     * @param dataCenterParam 传参
     * @return com.sydh.common.core.domain.AjaxResult
     */
    List<ThingsModelLogCountVO> countThingsModelInvoke(@Param("dataCenterParam") DataCenterParam dataCenterParam, @Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

    List<HistoryBo> selectHistorySingleBo(DeviceLog deviceLog);
}
