package com.sydh.framework.config;

import com.sydh.framework.manager.SpringCacheManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 缓存配置类
 * 提供基于Redis的分布式缓存配置
 */
@Configuration
@EnableCaching
@ConditionalOnProperty(name = {"spring.cache.enable"}, havingValue = "true")
public class CacheConfig {

    @Value("${spring.cache.ttl}")
    private Long ttl;

    /**
     * 配置Spring缓存管理器
     * @return Spring缓存管理器实例
     */
    @Bean
    public CacheManager cacheManager() {
        return new SpringCacheManager(this.ttl);
    }
}
