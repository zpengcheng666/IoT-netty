package com.sydh.iot.data.job.strategy;

import com.sydh.iot.domain.DeviceJob;

public interface JobInvokeStrategy {
    boolean supports(int jobType);
    void invoke(DeviceJob job);
}
