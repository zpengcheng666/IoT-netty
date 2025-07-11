package com.sydh.sip.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@Data
@ConfigurationProperties(prefix = "sip")
public class SysSipConfig {
    boolean enabled;
    String ip;
    Integer port;
    String domain;
    String id;
    String password;
    boolean log;
    String zlmRecordPath;
    String mp4MaxSecond;
}
