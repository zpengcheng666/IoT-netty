package com.sydh.iot.tsdb.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.utils.DateUtils;
import com.sydh.iot.domain.Device;
import com.sydh.iot.domain.DeviceLog;
import com.sydh.iot.model.DeviceStatistic;
import com.sydh.iot.model.HistoryBo;
import com.sydh.iot.model.HistoryModel;
import com.sydh.iot.model.vo.ThingsModelLogCountVO;
import com.sydh.iot.model.param.DataCenterParam;
import com.sydh.iot.tsdb.service.ILogService;
import com.sydh.iot.model.MonitorModel;
import com.sydh.iot.mapper.TDDeviceLogMapper;
import com.sydh.iot.tsdb.model.TdLogDto;
import com.sydh.iot.util.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 类名: TdengineLogServiceImpl
 * 描述: TDengine存储日志数据实现类
 * 时间: 2022/5/22,0022 13:38
 * 开发人: admin
 */
@Slf4j
@Primary
@ConditionalOnProperty(name = "spring.datasource.dynamic.datasource.taos.enabled", havingValue = "true")
@DS("taos")
@Service("Tdengine")
public class TdengineLogServiceImpl implements ILogService {

    @Autowired
    private TDDeviceLogMapper tDDeviceLogMapper;


    private SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(1);

    @Value("${spring.datasource.dynamic.datasource.taos.dbName}")
    private String dbName;


    @Override
    public int createSTable(String database) {
        return tDDeviceLogMapper.createSTable(database);
    }

    /***
     * 新增设备日志
     * @return
     */
    @Override
    public int saveDeviceLog(DeviceLog deviceLog) {
        long logId = snowflakeIdWorker.nextId();
        deviceLog.setLogId(logId);
        return tDDeviceLogMapper.save(dbName, deviceLog);
    }

    /**
     * 批量保存日志
     */
    @Override
    public int saveBatch(TdLogDto dto) {
        return tDDeviceLogMapper.saveBatch(dbName, dto);
    }

    /***
     * 设备属性、功能、事件和监测数据总数
     * @return
     */
    @Override
    public DeviceStatistic selectCategoryLogCount(Device device) {
        DeviceStatistic statistic = new DeviceStatistic();
        Long property = tDDeviceLogMapper.selectPropertyLogCount(dbName, device);
        Long event = tDDeviceLogMapper.selectEventLogCount(dbName, device);
        Long monitor = tDDeviceLogMapper.selectMonitorLogCount(dbName, device);
        statistic.setPropertyCount(property == null ? 0 : property);
        statistic.setEventCount(event == null ? 0 : event);
        statistic.setMonitorCount(monitor == null ? 0 : monitor);
        return statistic;
    }

    /***
     * 日志列表
     * @return
     */
    @Override
    public Page<DeviceLog> selectDeviceLogList(DeviceLog deviceLog) {
        return tDDeviceLogMapper.selectDeviceLogList(new Page<>(deviceLog.getPageNum(), deviceLog.getPageSize()), dbName, deviceLog);
    }

    @Override
    public Page<DeviceLog> selectEventLogList(DeviceLog deviceLog) {
        return tDDeviceLogMapper.selectEventLogList(new Page<>(deviceLog.getPageNum(), deviceLog.getPageSize()), dbName, deviceLog);
    }

    /***
     * 监测数据列表
     * @return
     */
    @Override
    public List<MonitorModel> selectMonitorList(DeviceLog deviceLog) {
        if (deviceLog.getIdentify() != null) {
            deviceLog.setIdentify("%" + deviceLog.getIdentify() + "%");
        }
        return tDDeviceLogMapper.selectMonitorList(dbName, deviceLog);
    }

    /***
     * 根据设备ID删除设备日志
     * @return
     */
    @Override
    public int deleteDeviceLogByDeviceNumber(String deviceNumber) {
        return tDDeviceLogMapper.deleteDeviceLogByDeviceNumber(dbName, deviceNumber);
    }

    /**
     * 查询历史数据
     * is_Montor=1 或 is_history=1
     */
    @Override
    public Map<String, List<HistoryModel>> selectHistoryList(DeviceLog deviceLog) {
        List<HistoryModel> historyList = tDDeviceLogMapper.selectHistoryList(dbName, deviceLog);
        return historyList.stream().collect(Collectors.groupingBy(HistoryModel::getIdentify));
    }

    @Override
    public List<HistoryModel> listHistory(DeviceLog deviceLog) {
        List<HistoryModel> historyModelList = tDDeviceLogMapper.listHistory(dbName, deviceLog);
        for (HistoryModel historyModel : historyModelList) {
            historyModel.setTime(DateUtils.dateRemoveMs(historyModel.getTime()));
        }
        return historyModelList;
    }

    @Override
    public Page<HistoryModel> listhistoryGroupByCreateTime(DeviceLog deviceLog) {
        return tDDeviceLogMapper.listhistoryGroupByCreateTime(new Page<>(deviceLog.getPageNum(), deviceLog.getPageSize()), dbName, deviceLog);
    }

    @Override
    public List<String> selectStatsValue(DeviceLog deviceLog) {
        return tDDeviceLogMapper.selectStatsValue(dbName, deviceLog);
    }

    @Override
    public List<ThingsModelLogCountVO> countThingsModelInvoke(DataCenterParam dataCenterParam) {
        return tDDeviceLogMapper.countThingsModelInvoke(dbName, dataCenterParam);
    }

    public DeviceLog selectLastReport(DeviceLog deviceLog) {
        return null;
    }

    /**
     * 查询单个属性历史数据
     */
    @Override
    public   List<HistoryBo> selectHistorySingleBo(DeviceLog deviceLog){
        return tDDeviceLogMapper.selectHistoryListBo(dbName,deviceLog);
    }
}
