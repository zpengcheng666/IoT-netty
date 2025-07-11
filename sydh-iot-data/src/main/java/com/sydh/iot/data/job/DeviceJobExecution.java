package com.sydh.iot.data.job;

import com.sydh.common.utils.spring.SpringUtils;
import com.sydh.iot.domain.DeviceJob;
import org.quartz.JobExecutionContext;

/**
 * 定时任务处理（允许并发执行）
 *
 * @author ruoyi
 *
 */
public class DeviceJobExecution extends DeviceAbstractQuartzJob
{
    @Override
    protected void doExecute(JobExecutionContext context, DeviceJob deviceJob) throws Exception
    {
        DeviceJobInvoke jobInvoke = SpringUtils.getBean(DeviceJobInvoke.class);
        jobInvoke.invokeMethod(deviceJob);
    }
}
