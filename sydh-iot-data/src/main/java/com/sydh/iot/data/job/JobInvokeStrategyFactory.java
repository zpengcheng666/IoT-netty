package com.sydh.iot.data.job;

import com.sydh.iot.data.job.strategy.JobInvokeStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 策略类
 * @author gsb
 * @date 2025/3/18 15:42
 */
@Component
public class JobInvokeStrategyFactory {
    @Autowired
    private List<JobInvokeStrategy> strategies;

    public JobInvokeStrategy getStrategy(int jobType) {
        return strategies.stream()
                .filter(s -> s.supports(jobType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported job type"));
    }
}
