package com.sydh.web.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 应用启动完成后的处理器
 *
 * @author sydh
 */
@Slf4j
@Component
public class StartupRunner implements ApplicationRunner {

    @Value("${server.port}")
    private String serverPort;

    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    @Value("${sydh.name:sydh}")
    private String appName;

    @Value("${sydh.version:2.7.0}")
    private String appVersion;

    @Override
    public void run(ApplicationArguments args) throws InterruptedException {
        Thread.sleep(1000);
        log.info("✅ ******(●'◡'●)ﾉﾞ SYDH-IOT物联网平台启动成功! ლ(´ڡ`ლ)ﾞ******\n");
        log.info("  ____  _   _ ____  _   _   _____ ____  _____");
        log.info(" / ___|| | | |  _ \\| | | | |_   _/ __ \\|_   _|");
        log.info(" \\___ \\| |_| | | | | |_| |   | || |  | | | |");
        log.info("  ___) |  _  | |_| |  _  |   | || |__| | | |");
        log.info(" |____/|_| |_|____/|_| |_|   |_| \\____/  |_|\n");
        log.info("✅ 🎉🎉🎉 " + appName + " v" + appVersion + " 启动完成! 🎉🎉🎉");
        log.info("本地访问: http://localhost:" + serverPort + ("/".equals(contextPath) ? "" : contextPath));
        log.info("API文档: http://localhost:" + serverPort + "/swagger-ui/index.html");
        log.info("Knife4j文档: http://localhost:" + serverPort + "/doc.html");
    }
} 