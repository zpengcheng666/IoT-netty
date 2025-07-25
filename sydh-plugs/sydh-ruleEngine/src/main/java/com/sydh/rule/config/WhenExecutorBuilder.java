package com.sydh.rule.config;

import com.yomahub.liteflow.thread.ExecutorBuilder;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.*;

public class WhenExecutorBuilder implements ExecutorBuilder {
    private ThreadFactory springThreadFactory = new CustomizableThreadFactory("liteflow-when-");
    @Override
    public ExecutorService buildExecutor() {
        return new ThreadPoolExecutor(
                10,
                30,
                5,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<Runnable>(1000),
                springThreadFactory);
    }
}
