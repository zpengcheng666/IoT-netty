package com.sydh.iot.data.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sydh.common.enums.OTAUpgrade;
import com.sydh.common.exception.ServiceException;
import com.sydh.common.extend.core.domin.mq.ota.OtaUpgradeDelayTask;
import com.sydh.common.utils.DateUtils;
import com.sydh.common.utils.MessageUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.common.utils.bean.BeanUtils;
import com.sydh.iot.data.service.IFirmwareTaskDetailService;
import com.sydh.iot.data.service.IFirmwareTaskService;
import com.sydh.iot.data.service.IOtaUpgradeService;
import com.sydh.iot.domain.FirmwareTask;
import com.sydh.iot.domain.FirmwareTaskDetail;
import com.sydh.iot.mapper.FirmwareTaskMapper;
import com.sydh.iot.model.FirmwareTaskInput;
import com.sydh.iot.service.IDeviceService;
import com.sydh.mq.queue.DelayUpgradeQueue;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 固件升级任务对象Service业务层处理
 *
 * @author kerwincui
 * @date 2024-08-18
 */
@Service
public class FirmwareTaskServiceImpl extends ServiceImpl<FirmwareTaskMapper,FirmwareTask> implements IFirmwareTaskService {

    @Resource
    private FirmwareTaskMapper firmwareTaskMapper;
    @Resource
    private IFirmwareTaskDetailService firmwareTaskDetailService;
    @Resource
    private IOtaUpgradeService otaUpgradeService;
    @Resource
    private IDeviceService deviceService;



    /**
     * 查询固件升级任务对象列表
     *
     * @param firmwareTask 固件升级任务对象
     * @return 固件升级任务对象
     */
    @Override
    public Page<FirmwareTask> selectFirmwareTaskList(FirmwareTask firmwareTask) {
        LambdaQueryWrapper<FirmwareTask> lqw = buildQueryWrapper(firmwareTask);
        return baseMapper.selectPage(new Page<>(firmwareTask.getPageNum(), firmwareTask.getPageSize()),lqw);
    }

    private LambdaQueryWrapper<FirmwareTask> buildQueryWrapper(FirmwareTask query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<FirmwareTask> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(query.getTaskName()), FirmwareTask::getTaskName, query.getTaskName());
        lqw.eq(query.getFirmwareId() != null, FirmwareTask::getFirmwareId, query.getFirmwareId());
        lqw.eq(query.getUpgradeType() != null, FirmwareTask::getUpgradeType, query.getUpgradeType());
        lqw.eq(StringUtils.isNotBlank(query.getTaskDesc()), FirmwareTask::getTaskDesc, query.getTaskDesc());
        lqw.eq(query.getDeviceAmount() != null, FirmwareTask::getDeviceAmount, query.getDeviceAmount());
        lqw.eq(query.getBookTime() != null, FirmwareTask::getBookTime, query.getBookTime());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(FirmwareTask::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }


    /**
     * 新增固件升级任务
     *
     * @param firmwareTaskInput 固件升级任务
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long insertFirmwareTask(FirmwareTaskInput firmwareTaskInput)
    {
        //先查询任务是否存在，根据任务名查询
        int count = firmwareTaskMapper.selectCountFirmwareTaskByTaskName(firmwareTaskInput.getFirmwareId(), firmwareTaskInput.getTaskName());
        if (count >0){
            throw new ServiceException(StringUtils.format(MessageUtils.message("firmwareTask.add.fail.FirmwareTask.exist"), firmwareTaskInput.getTaskName()));
        }
        List<String> deviceList;
        if (1 == firmwareTaskInput.getUpgradeType()) {
            deviceList = deviceService.selectSerialNumbersByProductId(firmwareTaskInput.getProductId());
            firmwareTaskInput.setDeviceAmount((long) deviceList.size());
        } else {
            deviceList = firmwareTaskInput.getDeviceList();
        }
        FirmwareTask firmwareTask = new FirmwareTask();
        BeanUtils.copyBeanProp(firmwareTask,firmwareTaskInput);
        firmwareTask.setCreateTime(DateUtils.getNowDate());
        // 插入固件升级任务主表
        firmwareTaskMapper.insert(firmwareTask);
        List<FirmwareTaskDetail> detailList = new ArrayList<>(deviceList.size());
        Long taskId = firmwareTask.getId();
        for (String serialNumber : deviceList) {
            FirmwareTaskDetail detail = new FirmwareTaskDetail();
            detail.setTaskId(taskId);
            detail.setSerialNumber(serialNumber);
            detail.setUpgradeStatus(OTAUpgrade.AWAIT.getStatus());
            detail.setDetailMsg(OTAUpgrade.AWAIT.getDes());
            detail.setCreateTime(DateUtils.getNowDate());
            detailList.add(detail);
        }
        // 批量插入明细表
        firmwareTaskDetailService.saveBatch(detailList);
        if (taskId > 0){
            OtaUpgradeDelayTask otaTask = new OtaUpgradeDelayTask();
            otaTask.setTaskId(taskId);
            otaTask.setFirmwareId(firmwareTaskInput.getFirmwareId());
            otaTask.setDevices(firmwareTaskInput.getDeviceList());
            otaTask.setStartTime(firmwareTaskInput.getBookTime());
            otaTask.setUpgradeType(firmwareTaskInput.getUpgradeType());
            upgrade(otaTask);
        }
        return taskId;
    }

    /**
     * OTA升级
     * @param task
     */
    @Override
    public void upgrade(OtaUpgradeDelayTask task){
        // 判定是否是预定升级时间升级
        if (null != task.getStartTime()){
            if (DateUtils.getNowDate().after(task.getStartTime())){
                throw new ServiceException(MessageUtils.message("firmware.task.upgrade.failed.time.not.valid"));
            }
            //预定升级的任务放到 延迟任务队列
            DelayUpgradeQueue.offerTask(task);
            FirmwareTask updatePo = new FirmwareTask();
            updatePo.setId(task.getTaskId());
            updatePo.setBookTime(task.getStartTime());
            baseMapper.updateById(updatePo);
        }else {
            otaUpgradeService.upgrade(task);
        }
    }

    @Override
    public int deleteBatchByIds(List<Long> idList) {
        int i = firmwareTaskMapper.deleteBatchIds(idList);
        if (i > 0) {
            LambdaQueryWrapper<FirmwareTaskDetail> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(FirmwareTaskDetail::getTaskId, idList);
            firmwareTaskDetailService.remove(queryWrapper);
        }
        return i;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(FirmwareTask entity){
        //TODO 做一些数据校验,如唯一约束
    }

}
