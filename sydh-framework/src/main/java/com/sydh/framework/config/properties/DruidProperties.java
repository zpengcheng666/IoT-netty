package com.sydh.framework.config.properties;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "spring.datasource.dynamic.druid")
public class DruidProperties {
    private Integer initialSize;
    private Integer minIdle;
    private Integer maxActive;
    private Integer maxWait;
    private Long timeBetweenEvictionRunsMillis;

    public void setInitialSize(Integer initialSize) {
        this.initialSize = initialSize;
    }

    private Long minEvictableIdleTimeMillis;
    private Long maxEvictableIdleTimeMillis;
    private String validationQuery;
    private Boolean testWhileIdle;
    private Boolean testOnBorrow;
    private Boolean testOnReturn;

    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }

    public void setMaxActive(Integer maxActive) {
        this.maxActive = maxActive;
    }

    public void setMaxWait(Integer maxWait) {
        this.maxWait = maxWait;
    }

    public void setTimeBetweenEvictionRunsMillis(Long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public void setMinEvictableIdleTimeMillis(Long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public void setMaxEvictableIdleTimeMillis(Long maxEvictableIdleTimeMillis) {
        this.maxEvictableIdleTimeMillis = maxEvictableIdleTimeMillis;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public void setTestWhileIdle(Boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public void setTestOnBorrow(Boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public void setTestOnReturn(Boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof DruidProperties)) return false;
        DruidProperties other = (DruidProperties) o;
        if (!other.canEqual(this)) return false;
        Object this$initialSize = getInitialSize(), other$initialSize = other.getInitialSize();
        if ((this$initialSize == null) ? (other$initialSize != null) : !this$initialSize.equals(other$initialSize))
            return false;
        Object this$minIdle = getMinIdle(), other$minIdle = other.getMinIdle();
        if ((this$minIdle == null) ? (other$minIdle != null) : !this$minIdle.equals(other$minIdle)) return false;
        Object this$maxActive = getMaxActive(), other$maxActive = other.getMaxActive();
        if ((this$maxActive == null) ? (other$maxActive != null) : !this$maxActive.equals(other$maxActive))
            return false;
        Object this$maxWait = getMaxWait(), other$maxWait = other.getMaxWait();
        if ((this$maxWait == null) ? (other$maxWait != null) : !this$maxWait.equals(other$maxWait)) return false;
        Object this$timeBetweenEvictionRunsMillis = getTimeBetweenEvictionRunsMillis(), other$timeBetweenEvictionRunsMillis = other.getTimeBetweenEvictionRunsMillis();
        if ((this$timeBetweenEvictionRunsMillis == null) ? (other$timeBetweenEvictionRunsMillis != null) : !this$timeBetweenEvictionRunsMillis.equals(other$timeBetweenEvictionRunsMillis))
            return false;
        Object this$minEvictableIdleTimeMillis = getMinEvictableIdleTimeMillis(), other$minEvictableIdleTimeMillis = other.getMinEvictableIdleTimeMillis();
        if ((this$minEvictableIdleTimeMillis == null) ? (other$minEvictableIdleTimeMillis != null) : !this$minEvictableIdleTimeMillis.equals(other$minEvictableIdleTimeMillis))
            return false;
        Object this$maxEvictableIdleTimeMillis = getMaxEvictableIdleTimeMillis(), other$maxEvictableIdleTimeMillis = other.getMaxEvictableIdleTimeMillis();
        if ((this$maxEvictableIdleTimeMillis == null) ? (other$maxEvictableIdleTimeMillis != null) : !this$maxEvictableIdleTimeMillis.equals(other$maxEvictableIdleTimeMillis))
            return false;
        Object this$testWhileIdle = getTestWhileIdle(), other$testWhileIdle = other.getTestWhileIdle();
        if ((this$testWhileIdle == null) ? (other$testWhileIdle != null) : !this$testWhileIdle.equals(other$testWhileIdle))
            return false;
        Object this$testOnBorrow = getTestOnBorrow(), other$testOnBorrow = other.getTestOnBorrow();
        if ((this$testOnBorrow == null) ? (other$testOnBorrow != null) : !this$testOnBorrow.equals(other$testOnBorrow))
            return false;
        Object this$testOnReturn = getTestOnReturn(), other$testOnReturn = other.getTestOnReturn();
        if ((this$testOnReturn == null) ? (other$testOnReturn != null) : !this$testOnReturn.equals(other$testOnReturn))
            return false;
        Object this$validationQuery = getValidationQuery(), other$validationQuery = other.getValidationQuery();
        return !((this$validationQuery == null) ? (other$validationQuery != null) : !this$validationQuery.equals(other$validationQuery));
    }

    protected boolean canEqual(Object other) {
        return other instanceof DruidProperties;
    }

    // 建议在类中定义以下常量
    private static final int HASH_PRIME = 59;
    private static final int HASH_NULL = 43;

    public int hashCode() {
        int result = 1;

        // 工具方法简化 null 安全的 hash 计算
        Object $initialSize = getInitialSize();
        result = result * HASH_PRIME + hash($initialSize);

        Object $minIdle = getMinIdle();
        result = result * HASH_PRIME + hash($minIdle);

        Object $maxActive = getMaxActive();
        result = result * HASH_PRIME + hash($maxActive);

        Object $maxWait = getMaxWait();
        result = result * HASH_PRIME + hash($maxWait);

        Object $timeBetweenEvictionRunsMillis = getTimeBetweenEvictionRunsMillis();
        result = result * HASH_PRIME + hash($timeBetweenEvictionRunsMillis);

        Object $minEvictableIdleTimeMillis = getMinEvictableIdleTimeMillis();
        result = result * HASH_PRIME + hash($minEvictableIdleTimeMillis);

        Object $maxEvictableIdleTimeMillis = getMaxEvictableIdleTimeMillis();
        result = result * HASH_PRIME + hash($maxEvictableIdleTimeMillis);

        Object $testWhileIdle = getTestWhileIdle();
        result = result * HASH_PRIME + hash($testWhileIdle);

        Object $testOnBorrow = getTestOnBorrow();
        result = result * HASH_PRIME + hash($testOnBorrow);

        Object $testOnReturn = getTestOnReturn();
        result = result * HASH_PRIME + hash($testOnReturn);

        Object $validationQuery = getValidationQuery();
        result = result * HASH_PRIME + hash($validationQuery);

        return result;
    }

    // 辅助方法：安全地计算对象的 hash code
    private int hash(Object obj) {
        return obj == null ? HASH_NULL : obj.hashCode();
    }


    public String toString() {
        return "DruidProperties(initialSize=" + getInitialSize() + ", minIdle=" + getMinIdle() + ", maxActive=" + getMaxActive() + ", maxWait=" + getMaxWait() + ", timeBetweenEvictionRunsMillis=" + getTimeBetweenEvictionRunsMillis() + ", minEvictableIdleTimeMillis=" + getMinEvictableIdleTimeMillis() + ", maxEvictableIdleTimeMillis=" + getMaxEvictableIdleTimeMillis() + ", validationQuery=" + getValidationQuery() + ", testWhileIdle=" + getTestWhileIdle() + ", testOnBorrow=" + getTestOnBorrow() + ", testOnReturn=" + getTestOnReturn() + ")";
    }


    public DruidDataSource dataSource(DruidDataSource datasource) {
        datasource.setInitialSize(this.initialSize.intValue());
        datasource.setMaxActive(this.maxActive.intValue());
        datasource.setMinIdle(this.minIdle.intValue());
        datasource.setMaxWait(this.maxWait.intValue());
        datasource.setTimeBetweenEvictionRunsMillis(this.timeBetweenEvictionRunsMillis.longValue());
        datasource.setMinEvictableIdleTimeMillis(this.minEvictableIdleTimeMillis.longValue());
        datasource.setMaxEvictableIdleTimeMillis(this.maxEvictableIdleTimeMillis.longValue());
        datasource.setValidationQuery(this.validationQuery);
        datasource.setTestWhileIdle(this.testWhileIdle.booleanValue());
        datasource.setTestOnBorrow(this.testOnBorrow.booleanValue());
        datasource.setTestOnReturn(this.testOnReturn.booleanValue());
        return datasource;
    }
}
