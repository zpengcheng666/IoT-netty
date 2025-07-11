package com.sydh.iot.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.iot.domain.Device;
import com.sydh.iot.domain.DeviceLog;
import com.sydh.iot.model.HistoryBo;
import com.sydh.iot.model.HistoryModel;
import com.sydh.iot.model.MonitorModel;
import com.sydh.iot.model.vo.ThingsModelLogCountVO;
import com.sydh.iot.model.param.DataCenterParam;
import com.sydh.iot.tsdb.model.TdLogDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @package com.sydh.mysql.mysql.tdengine
 * 类名: DatabaseMapper
 * 时间: 2022/5/16,0016 1:27
 * 开发人: wxy
 */
@Repository
public interface TDDeviceLogMapper {

    /***
     * 创建数据库
     */
    int createDB(String database);

    /***
     * 创建超级表
     */
    int createSTable(String database);


    /***
     * 新增设备日志
     */
    int save(@Param("database") String database, @Param("device") DeviceLog deviceLog);

    /**
     * 批量插入数据
     *
     * @param database 数据库名
     * @param data     list集合
     */
    int saveBatch(@Param("database") String database, @Param("data") TdLogDto data);

    /***
     * 设备属性数据总数
     */
    Long selectPropertyLogCount(@Param("database") String database, @Param("device") Device device);

    /***
     * 设备功能数据总数
     */
    Long selectFunctionLogCount(@Param("database") String database, @Param("device") Device device);

    /***
     * 设备事件数据总数
     */
    Long selectEventLogCount(@Param("database") String database, @Param("device") Device device);

    /***
     * 设备监测数据总数
     */
    Long selectMonitorLogCount(@Param("database") String database, @Param("device") Device device);

    /***
     * 监测数据列表
     */
    List<MonitorModel> selectMonitorList(@Param("database") String database, @Param("device") DeviceLog deviceLog);

    /***
     * 日志列表
     */
    Page<DeviceLog> selectDeviceLogList(Page<DeviceLog> page, @Param("database") String database, @Param("device") DeviceLog deviceLog);
    Page<DeviceLog> selectEventLogList(Page<DeviceLog> page, @Param("database") String database, @Param("device") DeviceLog deviceLog);

    /***
     * 根据设备ID删除设备日志
     */
    int deleteDeviceLogByDeviceNumber(@Param("database") String dbName, @Param("serialNumber") String serialNumber);

    /***
     * 历史数据列表
     */
    List<HistoryModel> selectHistoryList(@Param("database") String database, @Param("device") DeviceLog deviceLog);

    List<HistoryModel> listHistory(@Param("database") String database, @Param("device") DeviceLog deviceLog);

    Page<HistoryModel> listhistoryGroupByCreateTime(Page<HistoryModel> page, @Param("database") String database, @Param("device") DeviceLog deviceLog);

    /**
     * 场景统计变量历史值
     *
     * @param deviceLog 实体类
     * @return java.lang.String
     */
    List<String> selectStatsValue(@Param("database") String database, @Param("device") DeviceLog deviceLog);

    /**
     * 统计设备物模型指令下发数量
     *
     * @param dataCenterParam 传参
     * @return com.sydh.common.core.domain.AjaxResult
     */
    List<ThingsModelLogCountVO> countThingsModelInvoke(@Param("database") String database, @Param("dataCenterParam") DataCenterParam dataCenterParam);

    /***
     * 历史数据列表
     */
    List<HistoryBo> selectHistoryListBo(@Param("database")  String database, @Param("device") DeviceLog deviceLog);
}
