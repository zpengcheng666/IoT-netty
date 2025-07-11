package com.sydh.iot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sydh.iot.domain.AlertLog;
import com.sydh.iot.model.vo.AlertCountVO;
import com.sydh.iot.model.DeviceAlertCount;
import com.sydh.iot.model.param.DataCenterParam;
import com.sydh.iot.model.vo.AlertLogVO;

import java.util.List;

/**
 * 设备告警Service接口
 *
 * @author kerwincui
 * @date 2022-01-13
 */
public interface IAlertLogService extends IService<AlertLog>
{

    /**
     * 查询设备告警日志列表
     *
     * @param alertLogVO 设备告警日志
     * @return 设备告警日志集合
     */
    Page<AlertLogVO> pageAlertLogVO(AlertLogVO alertLogVO);

    /**
     * 查询设备告警
     *
     * @param alertLogId 设备告警主键
     * @return 设备告警
     */
    public AlertLog selectAlertLogByAlertLogId(Long alertLogId);

    public List<AlertLog> selectAlertLogListByCreateBy(String createBy, String remark, Integer status);
    /**
     * 查询设备告警列表总数
     *
     * @param alertLog 设备告警
     * @return 设备告警集合
     */
    public Long selectAlertLogListCount(AlertLog alertLog);

    public List<DeviceAlertCount> selectDeviceAlertCount(List<String> serialNumberList);
    public DeviceAlertCount selectDeviceAlertCountBySN(String serialNumber);
    public List<DeviceAlertCount> selectSceneAlertCount();
    public DeviceAlertCount selectSceneAlertCountBySceneId(String sceneId);
    /**
     * 新增设备告警
     *
     * @param alertLog 设备告警
     * @return 结果
     */
    public int insertAlertLog(AlertLog alertLog);
    public int insertAlertLogBatch(List<AlertLog> alertLogList);

    /**
     * 修改设备告警
     *
     * @param alertLog 设备告警
     * @return 结果
     */
    public int updateAlertLog(AlertLog alertLog);

    public int updateAlertLogStatus(AlertLog alertLog);

    /**
     * 通过设备编号删除设备告警信息
     *
     * @param serialNumber 设备告警主键
     * @return 结果
     */
    public int deleteAlertLogBySerialNumber(String serialNumber);

    /**
     * 统计告警处理信息
     * @param dataCenterParam 传参
     * @return com.sydh.iot.model.vo.AlertCountVO
     */
    List<AlertCountVO> countAlertProcess(DataCenterParam dataCenterParam);

    /**
     * 统计告警级别信息
     * @param dataCenterParam 传参
     * @return com.sydh.iot.model.vo.AlertCountVO
     */
    List<AlertCountVO> countAlertLevel(DataCenterParam dataCenterParam);
}
