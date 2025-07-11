package com.sydh.record;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class, DataSourceAutoConfiguration.class, MybatisPlusAutoConfiguration.class})
@EnableScheduling
@ComponentScan(basePackages = {"com.sydh.common.core.redis", "com.sydh.record", "com.sydh.common.utils.spring"})
public class ZlmRecordApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZlmRecordApplication.class, args);
    }

}
