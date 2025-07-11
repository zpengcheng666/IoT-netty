package com.sydh.iot.data.service;


import com.sydh.common.extend.core.domin.mq.DeviceTestReportBo;

/**
 * @author bill
 */
public interface IDeviceTestService {

    public void messageHandler(DeviceTestReportBo testReportBo);
}
