package com.sydh.iot.data.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.enums.OTAUpgrade;
import com.sydh.iot.domain.FirmwareTaskDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sydh.iot.model.FirmwareTaskDetailInput;
import com.sydh.iot.model.FirmwareTaskDetailOutput;
import com.sydh.iot.model.FirmwareTaskDeviceStatistic;

/**
 * 固件升级任务详细对象Service接口
 *
 * @author kerwincui
 * @date 2024-08-18
 */
public interface IFirmwareTaskDetailService extends IService<FirmwareTaskDetail>
{

    /**
     * 查询固件升级任务详细对象列表
     *
     * @param firmwareTaskDetail 固件升级任务详细对象
     * @return 固件升级任务详细对象集合
     */
    public Page<FirmwareTaskDetail> selectFirmwareTaskDetailList(FirmwareTaskDetail firmwareTaskDetail);


    /**
     * 查询固件升级任务详细
     * @param taskId
     * @param serialNumber
     * @return
     */
    public FirmwareTaskDetail selectFirmwareTaskDetailByTaskIdAndSerialNumber(Long taskId, String serialNumber);



    /**
     * 更新升级状态
     * @param taskId
     * @param serialNumber
     * @param otaUpgrade
     */
    public void updateStatus(Long taskId, String serialNumber, OTAUpgrade otaUpgrade);

    public List<FirmwareTaskDetailOutput> selectFirmwareTaskDetailListByFirmwareId(FirmwareTaskDetailInput firmwareTaskDetailInput);

    public List<FirmwareTaskDeviceStatistic> deviceStatistic(FirmwareTaskDetailInput firmwareTaskDetailInput);
}
