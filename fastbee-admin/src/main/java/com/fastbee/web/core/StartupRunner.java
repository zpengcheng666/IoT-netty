package com.fastbee.web.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 应用启动完成后的处理器
 * 
 * @author fastbee
 */
@Slf4j
@Component
@Order(Integer.MAX_VALUE) // 确保在所有其他 ApplicationRunner 之后执行
public class StartupRunner implements ApplicationRunner {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    @Value("${fastbee.name:FastBee}")
    private String appName;

    @Value("${fastbee.version:2.7.0}")
    private String appVersion;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            // 等待一小段时间确保所有服务都已经完全启动
            Thread.sleep(1000);
            
            log.info("========================================");
            log.info("应用程序 {} v{} 启动完成!", appName, appVersion);
            log.info("本地访问地址: http://localhost:{}{}", serverPort, "/".equals(contextPath) ? "" : contextPath);
            log.info("API文档地址: http://localhost:{}/swagger-ui/index.html", serverPort);
            log.info("========================================");
            
            // 在控制台也输出一次
            System.out.println("\n");
            System.out.println("🎉🎉🎉 " + appName + " v" + appVersion + " 启动完成! 🎉🎉🎉");
            System.out.println("本地访问: http://localhost:" + serverPort + ("/" .equals(contextPath) ? "" : contextPath));
            System.out.println("API文档: http://localhost:" + serverPort + "/swagger-ui/index.html");
            System.out.println("\n");
            
        } catch (Exception e) {
            log.error("启动完成处理器执行失败", e);
        }
    }
} 