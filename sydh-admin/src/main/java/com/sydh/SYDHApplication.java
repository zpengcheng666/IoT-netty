package com.sydh;

import com.dtflys.forest.springboot.annotation.ForestScan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

/**
 * 启动程序
 *
 * @author ruoyi
 */
@Slf4j
@SpringBootApplication
@ForestScan(basePackages = "com.sydh")
public class SYDHApplication {

    public static void main(String[] args) {
        log.info("========================================");
        log.info("🚀 开始启动 SYDH 物联网平台...");
        log.info("版本: v2.7.0");
        log.info("Java版本: {}", System.getProperty("java.version"));
        log.info("操作系统: {} {}", System.getProperty("os.name"), System.getProperty("os.version"));
        log.info("========================================");
        SpringApplication.run(SYDHApplication.class, args);
    }
}
