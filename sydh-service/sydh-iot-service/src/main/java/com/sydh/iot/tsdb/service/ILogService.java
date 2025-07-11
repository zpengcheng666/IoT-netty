package com.sydh.iot.tsdb.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.iot.domain.Device;
import com.sydh.iot.domain.DeviceLog;
import com.sydh.iot.model.DeviceStatistic;
import com.sydh.iot.model.HistoryBo;
import com.sydh.iot.model.HistoryModel;
import com.sydh.iot.model.MonitorModel;
import com.sydh.iot.model.vo.ThingsModelLogCountVO;
import com.sydh.iot.model.param.DataCenterParam;
import com.sydh.iot.tsdb.model.TdLogDto;

import java.util.List;
import java.util.Map;

/**
 * @package iot.iot.log
 * 类名: LogService
 * 描述: 设备日志记录接口
 * 时间: 2022/5/19,0019 18:04
 * 开发人: admin
 */
public interface ILogService {

    int  createSTable(String database);

    /** 保存设备日志 **/
    int saveDeviceLog(DeviceLog deviceLog);

    /**
     * 批量保存日志
     */
    int saveBatch(TdLogDto dto);

    /** 根据设备编号删除设备日志 **/
    int deleteDeviceLogByDeviceNumber(String deviceNumber);

    /** 设备属性、功能、事件总数 **/
    DeviceStatistic selectCategoryLogCount(Device device);

    /** 查询物模型日志列表 **/
    Page<DeviceLog> selectDeviceLogList(DeviceLog deviceLog);

    Page<DeviceLog> selectEventLogList(DeviceLog deviceLog);
    /** 查询监测数据列表 **/
    List<MonitorModel> selectMonitorList(DeviceLog deviceLog);

    /**查询历史数据is_history=1*/
    Map<String, List<HistoryModel>> selectHistoryList(DeviceLog deviceLog);

    /**
     * 查询物模型历史数据
     * @param deviceLog 设备日志
     * @return java.util.List<com.sydh.iot.model.HistoryModel>
     */
    List<HistoryModel> listHistory(DeviceLog deviceLog);

    Page<HistoryModel> listhistoryGroupByCreateTime(DeviceLog deviceLog);

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
    List<ThingsModelLogCountVO> countThingsModelInvoke(DataCenterParam dataCenterParam);

    DeviceLog selectLastReport(DeviceLog deviceLog);

    /**
     * 查询单个属性历史数据
     */
    List<HistoryBo> selectHistorySingleBo(DeviceLog deviceLog);
}
