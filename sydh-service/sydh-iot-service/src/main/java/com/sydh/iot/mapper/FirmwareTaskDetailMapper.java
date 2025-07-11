package com.sydh.iot.mapper;

import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sydh.iot.domain.FirmwareTaskDetail;
import com.sydh.iot.model.FirmwareTaskDetailInput;
import com.sydh.iot.model.FirmwareTaskDetailOutput;
import com.sydh.iot.model.FirmwareTaskDeviceStatistic;

/**
 * 固件升级任务详细对象Mapper接口
 *
 * @author kerwincui
 * @date 2024-08-18
 */
public interface FirmwareTaskDetailMapper extends BaseMapper<FirmwareTaskDetail>
{

    /**
     * 根据固件id查询下属设备列表
     * @param firmwareTaskDetailInput
     * @return
     */
    public List<FirmwareTaskDetailOutput> selectFirmwareTaskDetailListByFirmwareId(FirmwareTaskDetailInput firmwareTaskDetailInput);


    /**
     * 固件升级设备统计
     * @param firmwareTaskDetailInput
     * @return
     */
    public List<FirmwareTaskDeviceStatistic> deviceStatistic(FirmwareTaskDetailInput firmwareTaskDetailInput);
}
