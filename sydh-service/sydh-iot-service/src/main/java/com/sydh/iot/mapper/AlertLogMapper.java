package com.sydh.iot.mapper;

import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.iot.domain.AlertLog;
import com.sydh.iot.model.DeviceAlertCount;
import com.sydh.iot.model.DeviceAllShortOutput;
import com.sydh.iot.model.vo.AlertCountVO;
import com.sydh.iot.model.param.DataCenterParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 设备告警Mapper接口
 *
 * @author kerwincui
 * @date 2022-01-13
 */
@Repository
public interface AlertLogMapper extends BaseMapperX<AlertLog>
{

    /**
     * 查询设备告警列表总数
     *
     * @param alertLog 设备告警
     * @return 设备告警集合
     */
    public Long selectAlertLogListCount(AlertLog alertLog);

    public List<DeviceAlertCount> selectDeviceAlertCount(@Param("serialNumberList") List<String> serialNumberList);
    public DeviceAlertCount selectDeviceAlertCountBySN(String serialNumber);
    public List<DeviceAlertCount> selectSceneAlertCount();
    public DeviceAlertCount selectSceneAlertCountBySceneId(String sceneId);

    /**
     * 统计告警处理信息
     * @param dataCenterParam 传参
     * @return com.sydh.iot.model.vo.AlertCountVO
     */
    List<AlertCountVO> countAlertProcess(@Param("dataCenterParam") DataCenterParam dataCenterParam, @Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

    /**
     * 统计告警级别信息
     * @param dataCenterParam 传参
     * @return com.sydh.iot.model.vo.AlertCountVO
     */
    List<AlertCountVO> countAlertLevel(@Param("dataCenterParam") DataCenterParam dataCenterParam, @Param("beginTime")Date beginTime, @Param("endTime") Date endTime);

    List<DeviceAllShortOutput> selectHasAlertMap(@Param("serialNumberList") List<String> serialNumberList);
}
