package com.sydh.iot.mapper;

import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.iot.domain.DeviceJob;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 调度任务信息 数据层
 *
 * @author kerwincui
 */
@Repository
public interface DeviceJobMapper extends BaseMapperX<DeviceJob>
{


    /**
     * 根据设备Ids查询调度任务日志集合
     *
     * @param deviceIds 设备ID数组
     * @return 操作日志集合
     */
    public List<DeviceJob> selectShortJobListByDeviceIds(Long[] deviceIds);

    /**
     * 根据告警Ids查询调度任务日志集合
     *
     * @param alertIds 告警ID数组
     * @return 操作日志集合
     */
    public List<DeviceJob> selectShortJobListByAlertIds(Long[] alertIds);

    /**
     * 根据场景Ids查询调度任务日志集合
     *
     * @param sceneIds 场景ID数组
     * @return 操作日志集合
     */
    public List<DeviceJob> selectShortJobListBySceneIds(Long[] sceneIds);


    /**
     * 批量删除调度任务信息
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteJobByIds(Long[] ids);

    /**
     * 根据设备Ids批量删除调度任务信息
     *
     * @param deviceIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteJobByDeviceIds(Long[] deviceIds);

    /**
     * 根据告警Ids批量删除调度任务信息
     *
     * @param alertIds 需要删除的告警IDs
     * @return 结果
     */
    public int deleteJobByAlertIds(Long[] alertIds);

    /**
     * 根据场景联动Ids批量删除调度任务信息
     *
     * @param sceneIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteJobBySceneIds(Long[] sceneIds);


    /**
     * 批量查询定时任务
     * @param datasourceIds 源数据id集合
     * @param: jobType 定时任务类型
     * @return java.util.List<com.sydh.iot.domain.DeviceJob>
     */
    List<DeviceJob> selectListByJobTypeAndDatasourceIds(@Param("datasourceIds") Long[] datasourceIds, @Param("jobType") int jobType);

    /**
     * 批量删除定时任务
     * @param datasourceIds 源数据id集合
     * @param: jobType 类型
     * @return int
     */
    int deleteJobByJobTypeAndDatasourceIds(@Param("datasourceIds") Long[] datasourceIds, @Param("jobType") int jobType);

    /**
     * 根据条件批量删除定时任务
     */
    void deleteJobByJobTypeAndDeviceId(@Param("deviceId") Long deviceId, @Param("jobType") int jobType);
}
