package com.sydh.iot.data.job;

import com.sydh.iot.data.job.strategy.JobInvokeStrategy;
import com.sydh.iot.domain.DeviceJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 任务执行工具
 *
 * @author kerwincui
 */
@Slf4j
@Component
public class DeviceJobInvoke {

    private final JobInvokeStrategyFactory strategyFactory;


    public DeviceJobInvoke(JobInvokeStrategyFactory factory){
        this.strategyFactory = factory;
    }

    public void invokeMethod(DeviceJob deviceJob) throws Exception {
        JobInvokeStrategy strategy = strategyFactory.getStrategy(deviceJob.getJobType());
        strategy.invoke(deviceJob);
    }


}
