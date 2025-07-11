package com.sydh.iot.data.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.extend.core.domin.mq.ota.OtaUpgradeDelayTask;
import com.sydh.iot.domain.FirmwareTask;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sydh.iot.model.FirmwareTaskInput;

/**
 * 固件升级任务对象Service接口
 *
 * @author kerwincui
 * @date 2024-08-18
 */
public interface IFirmwareTaskService extends IService<FirmwareTask>
{

    /**
     * 查询固件升级任务对象列表
     *
     * @param firmwareTask 固件升级任务对象
     * @return 固件升级任务对象集合
     */
    public Page<FirmwareTask> selectFirmwareTaskList(FirmwareTask firmwareTask);


    /**
     * 新增固件升级任务
     *
     * @param firmwareTaskInput 固件升级任务
     * @return 结果
     */
    public Long insertFirmwareTask(FirmwareTaskInput firmwareTaskInput);

    /**
     * OTA升级
     * @param task
     */
    void upgrade(OtaUpgradeDelayTask task);

    /**
     * @description: 批量删除任务
     * @param: idList
     * @return: int
     */
    int deleteBatchByIds(List<Long> idList);
}
