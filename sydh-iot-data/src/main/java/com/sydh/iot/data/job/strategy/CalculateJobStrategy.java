package com.sydh.iot.data.job.strategy;

import com.sydh.common.utils.StringUtils;
import com.sydh.iot.data.service.IDataHandler;
import com.sydh.iot.domain.DeviceJob;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 场景运算
 * @author gsb
 * @date 2025/3/18 15:42
 */
@Component
public class CalculateJobStrategy implements JobInvokeStrategy{

    @Resource
    private IDataHandler dataHandler;

    @Override
    public boolean supports(int jobType) {
        return 4 == jobType;
    }

    @Override
    public void invoke(DeviceJob job) {
        System.out.println("------------------[定时执行场景运算型变量]---------------------");
        String s = dataHandler.calculateSceneModelTagValue(job.getDatasourceId());
        if (StringUtils.isEmpty(s)) {
            System.out.println("------------------[定时执行场景运算型变量失败：+" + s + "]---------------------");
        }
    }
}
