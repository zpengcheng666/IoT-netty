package com.sydh.framework.manager;

import com.sydh.framework.utils.RedisUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonCache;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.transaction.TransactionAwareCacheDecorator;
import org.springframework.util.StringUtils;

/**
 * Spring缓存管理器实现
 * 基于Redisson和Caffeine的多级缓存管理器
 */
public class SpringCacheManager implements CacheManager {
    private boolean dynamic = true;
    private boolean allowNullValues = true;
    private boolean transactionAware = true;
    private Long ttl;
    
    private final Map<String, CacheConfig> configMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Cache> instanceMap = new ConcurrentHashMap<>();

    public SpringCacheManager() {
        // 默认构造函数
    }

    public SpringCacheManager(Long ttl) {
        this.ttl = ttl;
    }

    public void setAllowNullValues(boolean allowNullValues) {
        this.allowNullValues = allowNullValues;
    }

    public void setTransactionAware(boolean transactionAware) {
        this.transactionAware = transactionAware;
    }

    public void setCacheNames(Collection<String> names) {
        if (names != null) {
            for (String name : names) {
                getCache(name);
            }
            this.dynamic = false;
        } else {
            this.dynamic = true;
        }
    }

    public void setConfig(Map<String, ? extends CacheConfig> config) {
        this.configMap.putAll(config);
    }

    protected CacheConfig createDefaultConfig() {
        return new CacheConfig();
    }

    @Override
    public Cache getCache(String name) {
        String[] array = StringUtils.delimitedListToStringArray(name, "#");
        name = array[0];
        
        Cache cache = this.instanceMap.get(name);
        if (cache != null) {
            return cache;
        }
        
        if (!this.dynamic) {
            return null;
        }
        
        CacheConfig config = this.configMap.get(name);
        if (config == null) {
            config = createDefaultConfig();
            this.configMap.put(name, config);
        }
        
        if (this.ttl != null && this.ttl > 0L) {
            config.setTTL(this.ttl * 1000L);
        }
        
        if (array.length > 1) {
            config.setTTL(DurationStyle.detectAndParse(array[1]).toMillis());
        }
        
        if (array.length > 2) {
            config.setMaxIdleTime(DurationStyle.detectAndParse(array[2]).toMillis());
        }
        
        if (array.length > 3) {
            config.setMaxSize(Integer.parseInt(array[3]));
        }
        
        if (config.getMaxIdleTime() == 0L && config.getTTL() == 0L && config.getMaxSize() == 0) {
            return createMap(name, config);
        }
        return createMapCache(name, config);
    }

    private Cache createMap(String name, CacheConfig config) {
        RMap<Object, Object> map = RedisUtils.getClient().getMap(name);
        Cache cache = new RedissonCache(map, this.allowNullValues);
        
        Cache decoratedCache = cache;
        if (this.transactionAware) {
            decoratedCache = new TransactionAwareCacheDecorator(cache);
        }
        
        Cache oldCache = this.instanceMap.putIfAbsent(name, decoratedCache);
        return oldCache != null ? oldCache : decoratedCache;
    }

    private Cache createMapCache(String name, CacheConfig config) {
        RMapCache<Object, Object> map = RedisUtils.getClient().getMapCache(name);
        Cache cache = new RedissonCache(map, config, this.allowNullValues);
        
        Cache decoratedCache = cache;
        if (this.transactionAware) {
            decoratedCache = new TransactionAwareCacheDecorator(cache);
        }
        
        Cache oldCache = this.instanceMap.putIfAbsent(name, decoratedCache);
        if (oldCache == null) {
            map.setMaxSize(config.getMaxSize());
            return decoratedCache;
        }
        return oldCache;
    }

    @Override
    public Collection<String> getCacheNames() {
        return Collections.unmodifiableSet(this.configMap.keySet());
    }
}
