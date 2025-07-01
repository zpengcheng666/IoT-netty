package com.fastbee;

import com.dtflys.forest.springboot.annotation.ForestScan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 启动程序
 *
 * @author ruoyi
 */
@Slf4j
@SpringBootApplication
@ForestScan(basePackages = "com.fastbee")
public class FastBeeApplication {
    public static void main(String[] args) {
        SpringApplication.run(FastBeeApplication.class, args);
    }
}
