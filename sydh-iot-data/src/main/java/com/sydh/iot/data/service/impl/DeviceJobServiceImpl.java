package com.sydh.iot.data.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sydh.common.constant.ScheduleConstants;
import com.sydh.common.exception.job.TaskException;
import com.sydh.common.utils.DateUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.iot.convert.DeviceJobConvert;
import com.sydh.iot.data.job.DeviceScheduleUtils;
import com.sydh.iot.domain.DeviceJob;
import com.sydh.iot.mapper.DeviceJobMapper;
import com.sydh.iot.model.vo.DeviceJobVO;
import com.sydh.iot.service.IDeviceJobService;
import com.sydh.quartz.domain.SysJob;
import com.sydh.quartz.mapper.SysJobMapper;
import com.sydh.quartz.util.CronUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 定时任务调度信息 服务层
 *
 * @author kerwincui
 */
@Service
@Slf4j
public class DeviceJobServiceImpl extends ServiceImpl<DeviceJobMapper,DeviceJob> implements IDeviceJobService
{
    @Resource
    private Scheduler scheduler;

    @Resource
    private DeviceJobMapper jobMapper;

    @Resource
    private SysJobMapper sysJobMapper;


    /**
     * 项目启动时，初始化定时器 主要是防止手动修改数据库导致未同步到定时任务处理（注：不能手动修改数据库ID和任务组名，否则会导致脏数据）
     */
    @PostConstruct
    public void init() throws SchedulerException, TaskException
    {
        scheduler.clear();
        // 设备定时任务
        List<DeviceJob> jobList = jobMapper.selectList();
        for (DeviceJob deviceJob : jobList)
        {
            DeviceScheduleUtils.createScheduleJob(scheduler, deviceJob);
        }

        // 系统定时任务
        List<SysJob> sysJobList = sysJobMapper.selectList();
        for (SysJob job : sysJobList)
        {
            com.sydh.quartz.util.ScheduleUtils.createScheduleJob(scheduler, job);
        }
    }

    /**
     * 获取quartz调度器的计划任务列表
     *
     * @param job 调度信息
     * @return
     */
    @Override
    public List<DeviceJob> selectJobList(DeviceJob job)
    {
        LambdaQueryWrapper<DeviceJob> wrapper = buildQueryWrapper(job);
         return jobMapper.selectList(wrapper);
    }

    /**
     * 查询设备定时分页列表
     *
     * @param deviceJob 设备定时
     * @return 设备定时
     */
    @Override
    public Page<DeviceJobVO> pageDeviceJobVO(DeviceJob deviceJob) {
        LambdaQueryWrapper<DeviceJob> lqw = buildQueryWrapper(deviceJob);
        lqw.orderByDesc(DeviceJob::getCreateTime);
        Page<DeviceJob> deviceJobPage = baseMapper.selectPage(new Page<>(deviceJob.getPageNum(), deviceJob.getPageSize()), lqw);
        return DeviceJobConvert.INSTANCE.convertDeviceJobVOPage(deviceJobPage);
    }


    /**
     * 通过调度任务ID查询调度信息
     *
     * @param jobId 调度任务ID
     * @return 调度任务对象信息
     */
    @Override
    public DeviceJob selectJobById(Long jobId)
    {
        return jobMapper.selectById(jobId);
    }

    /**
     * 暂停任务
     *
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int pauseJob(DeviceJob job) throws SchedulerException
    {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        job.setUpdateTime(DateUtils.getNowDate());
        int rows = jobMapper.updateById(job);
        if (rows > 0)
        {
            scheduler.pauseJob(DeviceScheduleUtils.getJobKey(jobId, jobGroup));
        }
        return rows;
    }

    /**
     * 恢复任务
     *
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int resumeJob(DeviceJob job) throws SchedulerException
    {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        job.setStatus(ScheduleConstants.Status.NORMAL.getValue());
        job.setUpdateTime(DateUtils.getNowDate());
        int rows = jobMapper.updateById(job);
        if (rows > 0)
        {
            scheduler.resumeJob(DeviceScheduleUtils.getJobKey(jobId, jobGroup));
        }
        return rows;
    }

    /**
     * 删除任务后，所对应的trigger也将被删除
     *
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteJob(DeviceJob job) throws SchedulerException
    {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        int rows = jobMapper.deleteById(jobId);
        if (rows > 0)
        {
            scheduler.deleteJob(DeviceScheduleUtils.getJobKey(jobId, jobGroup));
        }
        return rows;
    }

    /**
     * 批量删除调度信息
     *
     * @param jobIds 需要删除的任务ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJobByIds(Long[] jobIds) throws SchedulerException
    {
        for (Long jobId : jobIds)
        {
            DeviceJob job = this.selectJobById(jobId);
            if (Objects.nonNull(job)) {
                deleteJob(job);
            }
        }
    }

    /**
     * 根据设备Ids批量删除调度信息
     *
     * @param deviceIds 需要删除数据的设备Ids
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJobByDeviceIds(Long[] deviceIds) throws SchedulerException
    {
        // 查出所有job
        List<DeviceJob> deviceJobs=jobMapper.selectShortJobListByDeviceIds(deviceIds);
        // 批量删除job
        int rows=jobMapper.deleteJobByDeviceIds(deviceIds);
        // 批量删除调度器
        for(DeviceJob job:deviceJobs){
            scheduler.deleteJob(DeviceScheduleUtils.getJobKey(job.getJobId(), job.getJobGroup()));
        }
    }

    /**
     * 根据告警Ids批量删除调度信息
     *
     * @param alertIds 需要删除数据的告警Ids
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJobByAlertIds(Long[] alertIds) throws SchedulerException
    {
        // 查出所有job
        List<DeviceJob> deviceJobs=jobMapper.selectShortJobListByAlertIds(alertIds);
        // 批量删除job
        int rows=jobMapper.deleteJobByAlertIds(alertIds);
        // 批量删除调度器
        for(DeviceJob job:deviceJobs){
            scheduler.deleteJob(DeviceScheduleUtils.getJobKey(job.getJobId(), job.getJobGroup()));
        }
    }

    /**
     * 根据场景联动Ids批量删除调度信息
     *
     * @param sceneIds 需要删除数据的场景Ids
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJobBySceneIds(Long[] sceneIds) throws SchedulerException
    {
        // 查出所有job
        List<DeviceJob> deviceJobs=jobMapper.selectShortJobListBySceneIds(sceneIds);
        // 批量删除job
        int rows=jobMapper.deleteJobBySceneIds(sceneIds);
        // 批量删除调度器
        for(DeviceJob job:deviceJobs){
            scheduler.deleteJob(DeviceScheduleUtils.getJobKey(job.getJobId(), job.getJobGroup()));
        }
    }

    /**
     * 任务调度状态修改
     *
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(DeviceJob job) throws SchedulerException
    {
        int rows = 0;
        Integer status = job.getStatus();
        if (ScheduleConstants.Status.NORMAL.getValue().equals(status))
        {
            rows = resumeJob(job);
        }
        else if (ScheduleConstants.Status.PAUSE.getValue().equals(status))
        {
            rows = pauseJob(job);
        }
        return rows;
    }

    /**
     * 立即运行任务
     *
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(DeviceJob job) throws SchedulerException
    {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        DeviceJob properties = selectJobById(job.getJobId());
        // 参数
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(ScheduleConstants.TASK_PROPERTIES, properties);
        scheduler.triggerJob(DeviceScheduleUtils.getJobKey(jobId, jobGroup), dataMap);
    }

    /**
     * 新增任务
     *
     * @param deviceJob 调度信息 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertJob(DeviceJob deviceJob) throws SchedulerException, TaskException
    {
        deviceJob.setCreateTime(DateUtils.getNowDate());
        int rows = jobMapper.insert(deviceJob);
        if (rows > 0)
        {
            DeviceScheduleUtils.createScheduleJob(scheduler, deviceJob);
        }
        return rows;
    }

    /**
     * 更新任务的时间表达式
     *
     * @param deviceJob 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateJob(DeviceJob deviceJob) throws SchedulerException, TaskException
    {
        DeviceJob properties = selectJobById(deviceJob.getJobId());
        deviceJob.setUpdateTime(DateUtils.getNowDate());
        int rows = jobMapper.updateById(deviceJob);
        if (rows > 0)
        {
            updateSchedulerJob(deviceJob, properties.getJobGroup());
        }
        return rows;
    }

    /**
     * 更新任务
     *
     * @param deviceJob 任务对象
     * @param jobGroup 任务组名
     */
    public void updateSchedulerJob(DeviceJob deviceJob, String jobGroup) throws SchedulerException, TaskException
    {
        Long jobId = deviceJob.getJobId();
        // 判断是否存在
        JobKey jobKey = DeviceScheduleUtils.getJobKey(jobId, jobGroup);
        if (scheduler.checkExists(jobKey))
        {
            // 防止创建时存在数据问题 先移除，然后在执行创建操作
            scheduler.deleteJob(jobKey);
        }
        DeviceScheduleUtils.createScheduleJob(scheduler, deviceJob);
    }

    /**
     * 校验cron表达式是否有效
     *
     * @param cronExpression 表达式
     * @return 结果
     */
    @Override
    public boolean checkCronExpressionIsValid(String cronExpression)
    {
        return CronUtils.isValid(cronExpression);
    }

    @Override
    public List<DeviceJob> listShortJobBySceneId(Long[] sceneIds) {
        return jobMapper.selectShortJobListBySceneIds(sceneIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJobByJobTypeAndDatasourceIds(Long[] datasourceIds, int jobType) throws SchedulerException {
        // 查出所有job
        List<DeviceJob> deviceJobs = jobMapper.selectListByJobTypeAndDatasourceIds(datasourceIds, jobType);
        // 批量删除job
        int rows = jobMapper.deleteJobByJobTypeAndDatasourceIds(datasourceIds, jobType);
        // 批量删除调度器
        for(DeviceJob job:deviceJobs){
            scheduler.deleteJob(DeviceScheduleUtils.getJobKey(job.getJobId(), job.getJobGroup()));
        }
    }

    @Override
    public List<DeviceJob> selectListByJobTypeAndDatasourceIds(Long[] datasourceIds, int jobType) {
        return jobMapper.selectListByJobTypeAndDatasourceIds(datasourceIds, jobType);
    }


    /**
     * 根据条件批量删除定时任务
     * @param deviceId
     * @param jobType
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJobByJobTypeAndDeviceId(Long deviceId, int jobType) throws SchedulerException {
        // 查出所有job
        DeviceJob deviceJob = new DeviceJob();
        deviceJob.setJobType(jobType);
        deviceJob.setDeviceId(deviceId);
        List<DeviceJob> deviceJobs = this.selectJobList(deviceJob);
        // 批量删除job
        jobMapper.deleteJobByJobTypeAndDeviceId(deviceId, jobType);
        // 批量删除调度器
        for(DeviceJob job:deviceJobs){
            scheduler.deleteJob(DeviceScheduleUtils.getJobKey(job.getJobId(), job.getJobGroup()));
        }
    }

    private LambdaQueryWrapper<DeviceJob> buildQueryWrapper(DeviceJob query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<DeviceJob> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getJobId() != null, DeviceJob::getJobId, query.getJobId());
        lqw.like(StringUtils.isNotBlank(query.getJobName()), DeviceJob::getJobName, query.getJobName());
        lqw.eq(StringUtils.isNotBlank(query.getJobGroup()), DeviceJob::getJobGroup, query.getJobGroup());
        lqw.eq(StringUtils.isNotBlank(query.getCronExpression()), DeviceJob::getCronExpression, query.getCronExpression());
        lqw.eq(StringUtils.isNotBlank(query.getMisfirePolicy()), DeviceJob::getMisfirePolicy, query.getMisfirePolicy());
        lqw.eq(StringUtils.isNotBlank(query.getConcurrent()), DeviceJob::getConcurrent, query.getConcurrent());
        lqw.eq(query.getStatus() != null, DeviceJob::getStatus, query.getStatus());
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), DeviceJob::getCreateBy, query.getCreateBy());
        lqw.eq(query.getCreateTime() != null, DeviceJob::getCreateTime, query.getCreateTime());
        lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), DeviceJob::getUpdateBy, query.getUpdateBy());
        lqw.eq(query.getUpdateTime() != null, DeviceJob::getUpdateTime, query.getUpdateTime());
        lqw.eq(StringUtils.isNotBlank(query.getRemark()), DeviceJob::getRemark, query.getRemark());
        lqw.eq(query.getDeviceId() != null, DeviceJob::getDeviceId, query.getDeviceId());
        lqw.eq(StringUtils.isNotBlank(query.getSerialNumber()), DeviceJob::getSerialNumber, query.getSerialNumber());
        lqw.like(StringUtils.isNotBlank(query.getDeviceName()), DeviceJob::getDeviceName, query.getDeviceName());
        lqw.eq(query.getIsAdvance() != null, DeviceJob::getIsAdvance, query.getIsAdvance());
        lqw.eq(StringUtils.isNotBlank(query.getActions()), DeviceJob::getActions, query.getActions());
        lqw.eq(query.getJobType() != null, DeviceJob::getJobType, query.getJobType());
        lqw.eq(query.getProductId() != null, DeviceJob::getProductId, query.getProductId());
        lqw.like(StringUtils.isNotBlank(query.getProductName()), DeviceJob::getProductName, query.getProductName());
        lqw.eq(query.getSceneId() != null, DeviceJob::getSceneId, query.getSceneId());
        lqw.eq(query.getAlertId() != null, DeviceJob::getAlertId, query.getAlertId());
        lqw.eq(StringUtils.isNotBlank(query.getAlertTrigger()), DeviceJob::getAlertTrigger, query.getAlertTrigger());
        lqw.eq(query.getDatasourceId() != null, DeviceJob::getDatasourceId, query.getDatasourceId());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(DeviceJob::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DeviceJob entity){
        //TODO 做一些数据校验,如唯一约束
    }
}
