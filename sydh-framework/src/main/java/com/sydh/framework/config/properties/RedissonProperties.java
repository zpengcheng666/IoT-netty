package com.sydh.framework.config.properties;

import org.redisson.config.ReadMode;
import org.redisson.config.SubscriptionMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "redisson")
public class RedissonProperties {
    private String keyPrefix;
    private int threads;
    private int nettyThreads;
    private SingleServerConfig singleServerConfig;
    private ClusterServersConfig clusterServersConfig;

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public void setNettyThreads(int nettyThreads) {
        this.nettyThreads = nettyThreads;
    }

    public void setSingleServerConfig(SingleServerConfig singleServerConfig) {
        this.singleServerConfig = singleServerConfig;
    }

    public void setClusterServersConfig(ClusterServersConfig clusterServersConfig) {
        this.clusterServersConfig = clusterServersConfig;
    }
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RedissonProperties)) {
            return false;
        }
        RedissonProperties other = (RedissonProperties) o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (getThreads() != other.getThreads()) {
            return false;
        }
        if (getNettyThreads() != other.getNettyThreads()) {
            return false;
        }
        Object this$keyPrefix = getKeyPrefix(), other$keyPrefix = other.getKeyPrefix();
        if ((this$keyPrefix == null) ? (other$keyPrefix != null) : !this$keyPrefix.equals(other$keyPrefix)) {
            return false;
        }
        Object this$singleServerConfig = getSingleServerConfig(), other$singleServerConfig = other.getSingleServerConfig();
        if ((this$singleServerConfig == null) ? (other$singleServerConfig != null) : !this$singleServerConfig.equals(other$singleServerConfig)) {
            return false;
        }
        Object this$clusterServersConfig = getClusterServersConfig(), other$clusterServersConfig = other.getClusterServersConfig();
        return !((this$clusterServersConfig == null) ? (other$clusterServersConfig != null) : !this$clusterServersConfig.equals(other$clusterServersConfig));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RedissonProperties;
    }

    private static final int HASH_MULTIPLIER = 59;
    private static final int NULL_HASH_CODE = 43;
    @Override
    public int hashCode() {
        int result = 1;
        result = result * HASH_MULTIPLIER + getThreads();
        result = result * HASH_MULTIPLIER + getNettyThreads();

        Object $keyPrefix = getKeyPrefix();
        result = result * HASH_MULTIPLIER + (($keyPrefix == null) ? NULL_HASH_CODE : $keyPrefix.hashCode());

        Object $singleServerConfig = getSingleServerConfig();
        result = result * HASH_MULTIPLIER + (($singleServerConfig == null) ? NULL_HASH_CODE : $singleServerConfig.hashCode());

        Object $clusterServersConfig = getClusterServersConfig();
        return result * HASH_MULTIPLIER + (($clusterServersConfig == null) ? NULL_HASH_CODE : $clusterServersConfig.hashCode());
    }

    @Override
    public String toString() {
        return "RedissonProperties(keyPrefix=" + getKeyPrefix() + ", threads=" + getThreads() + ", nettyThreads=" + getNettyThreads() + ", singleServerConfig=" + getSingleServerConfig() + ", clusterServersConfig=" + getClusterServersConfig() + ")";
    }


    public String getKeyPrefix() {
        return this.keyPrefix;
    }


    public int getThreads() {
        return this.threads;
    }


    public int getNettyThreads() {
        return this.nettyThreads;
    }


    public SingleServerConfig getSingleServerConfig() {
        return this.singleServerConfig;
    }


    public ClusterServersConfig getClusterServersConfig() {
        return this.clusterServersConfig;
    }

    public static class SingleServerConfig {
        private String clientName;
        private int connectionMinimumIdleSize;
        private int connectionPoolSize;
        private int idleConnectionTimeout;
        private int timeout;
        private int subscriptionConnectionPoolSize;

        public void setClientName(String clientName) {
            this.clientName = clientName;
        }

        public void setConnectionMinimumIdleSize(int connectionMinimumIdleSize) {
            this.connectionMinimumIdleSize = connectionMinimumIdleSize;
        }

        public void setConnectionPoolSize(int connectionPoolSize) {
            this.connectionPoolSize = connectionPoolSize;
        }

        public void setIdleConnectionTimeout(int idleConnectionTimeout) {
            this.idleConnectionTimeout = idleConnectionTimeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public void setSubscriptionConnectionPoolSize(int subscriptionConnectionPoolSize) {
            this.subscriptionConnectionPoolSize = subscriptionConnectionPoolSize;
        }
        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof SingleServerConfig)) {
                return false;
            }
            SingleServerConfig other = (SingleServerConfig) o;
            if (!other.canEqual(this)) {
                return false;
            }
            if (getConnectionMinimumIdleSize() != other.getConnectionMinimumIdleSize()) {
                return false;
            }
            if (getConnectionPoolSize() != other.getConnectionPoolSize()) {
                return false;
            }
            if (getIdleConnectionTimeout() != other.getIdleConnectionTimeout()) {
                return false;
            }
            if (getTimeout() != other.getTimeout()) {
                return false;
            }
            if (getSubscriptionConnectionPoolSize() != other.getSubscriptionConnectionPoolSize()) {
                return false;
            }
            Object this$clientName = getClientName(), other$clientName = other.getClientName();
            return !((this$clientName == null) ? (other$clientName != null) : !this$clientName.equals(other$clientName));
        }

        protected boolean canEqual(Object other) {
            return other instanceof SingleServerConfig;
        }


        private static final int HASH_MULTIPLIER = 59;
        private static final int NULL_HASH_CODE = 43;
        @Override
        public int hashCode() {
            int hash = 1;
            hash = hash * HASH_MULTIPLIER + getConnectionMinimumIdleSize();
            hash = hash * HASH_MULTIPLIER + getConnectionPoolSize();
            hash = hash * HASH_MULTIPLIER + getIdleConnectionTimeout();
            hash = hash * HASH_MULTIPLIER + getTimeout();
            hash = hash * HASH_MULTIPLIER + getSubscriptionConnectionPoolSize();
            Object clientName = getClientName();
            return hash * HASH_MULTIPLIER + (clientName == null ? NULL_HASH_CODE : clientName.hashCode());
        }

        @Override
        public String toString() {
            return "RedissonProperties.SingleServerConfig(clientName=" + getClientName() + ", connectionMinimumIdleSize=" + getConnectionMinimumIdleSize() + ", connectionPoolSize=" + getConnectionPoolSize() + ", idleConnectionTimeout=" + getIdleConnectionTimeout() + ", timeout=" + getTimeout() + ", subscriptionConnectionPoolSize=" + getSubscriptionConnectionPoolSize() + ")";
        }


        public String getClientName() {
            return this.clientName;
        }


        public int getConnectionMinimumIdleSize() {
            return this.connectionMinimumIdleSize;
        }


        public int getConnectionPoolSize() {
            return this.connectionPoolSize;
        }


        public int getIdleConnectionTimeout() {
            return this.idleConnectionTimeout;
        }


        public int getTimeout() {
            return this.timeout;
        }


        public int getSubscriptionConnectionPoolSize() {
            return this.subscriptionConnectionPoolSize;
        }
    }

    public static class ClusterServersConfig {
        private String clientName;
        private int masterConnectionMinimumIdleSize;
        private int masterConnectionPoolSize;
        private int slaveConnectionMinimumIdleSize;
        private int slaveConnectionPoolSize;

        public void setClientName(String clientName) {
            this.clientName = clientName;
        }

        private int idleConnectionTimeout;
        private int timeout;
        private int subscriptionConnectionPoolSize;
        private ReadMode readMode;
        private SubscriptionMode subscriptionMode;

        public void setMasterConnectionMinimumIdleSize(int masterConnectionMinimumIdleSize) {
            this.masterConnectionMinimumIdleSize = masterConnectionMinimumIdleSize;
        }

        public void setMasterConnectionPoolSize(int masterConnectionPoolSize) {
            this.masterConnectionPoolSize = masterConnectionPoolSize;
        }

        public void setSlaveConnectionMinimumIdleSize(int slaveConnectionMinimumIdleSize) {
            this.slaveConnectionMinimumIdleSize = slaveConnectionMinimumIdleSize;
        }

        public void setSlaveConnectionPoolSize(int slaveConnectionPoolSize) {
            this.slaveConnectionPoolSize = slaveConnectionPoolSize;
        }

        public void setIdleConnectionTimeout(int idleConnectionTimeout) {
            this.idleConnectionTimeout = idleConnectionTimeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public void setSubscriptionConnectionPoolSize(int subscriptionConnectionPoolSize) {
            this.subscriptionConnectionPoolSize = subscriptionConnectionPoolSize;
        }

        public void setReadMode(ReadMode readMode) {
            this.readMode = readMode;
        }

        public void setSubscriptionMode(SubscriptionMode subscriptionMode) {
            this.subscriptionMode = subscriptionMode;
        }
        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof ClusterServersConfig)) {
                return false;
            }
            ClusterServersConfig other = (ClusterServersConfig) o;
            if (!other.canEqual(this)) {
                return false;
            }
            if (getMasterConnectionMinimumIdleSize() != other.getMasterConnectionMinimumIdleSize()) {
                return false;
            }
            if (getMasterConnectionPoolSize() != other.getMasterConnectionPoolSize()) {
                return false;
            }
            if (getSlaveConnectionMinimumIdleSize() != other.getSlaveConnectionMinimumIdleSize()) {
                return false;
            }
            if (getSlaveConnectionPoolSize() != other.getSlaveConnectionPoolSize()) {
                return false;
            }
            if (getIdleConnectionTimeout() != other.getIdleConnectionTimeout()) {
                return false;
            }
            if (getTimeout() != other.getTimeout()) {
                return false;
            }
            if (getSubscriptionConnectionPoolSize() != other.getSubscriptionConnectionPoolSize()) {
                return false;
            }
            Object this$clientName = getClientName(), other$clientName = other.getClientName();
            if ((this$clientName == null) ? (other$clientName != null) : !this$clientName.equals(other$clientName)) {
                return false;
            }
            Object this$readMode = getReadMode(), other$readMode = other.getReadMode();
            if ((this$readMode == null) ? (other$readMode != null) : !this$readMode.equals(other$readMode)) {
                return false;
            }
            Object this$subscriptionMode = getSubscriptionMode(), other$subscriptionMode = other.getSubscriptionMode();
            return !((this$subscriptionMode == null) ? (other$subscriptionMode != null) : !this$subscriptionMode.equals(other$subscriptionMode));
        }

        protected boolean canEqual(Object other) {
            return other instanceof ClusterServersConfig;
        }

        @Override
        public int hashCode() {
            // PRIME 用于哈希计算因子
            final int PRIME = 59;
            // NULL_HASH_CODE 用于 null 值的替代哈希值
            final int NULL_HASH_CODE = 43;

            int result = 1;

            result = result * PRIME + getMasterConnectionMinimumIdleSize();
            result = result * PRIME + getMasterConnectionPoolSize();
            result = result * PRIME + getSlaveConnectionMinimumIdleSize();
            result = result * PRIME + getSlaveConnectionPoolSize();
            result = result * PRIME + getIdleConnectionTimeout();
            result = result * PRIME + getTimeout();
            result = result * PRIME + getSubscriptionConnectionPoolSize();

            Object $clientName = getClientName();
            result = result * PRIME + (($clientName == null) ? NULL_HASH_CODE : $clientName.hashCode());

            Object $readMode = getReadMode();
            result = result * PRIME + (($readMode == null) ? NULL_HASH_CODE : $readMode.hashCode());

            Object $subscriptionMode = getSubscriptionMode();
            result = result * PRIME + (($subscriptionMode == null) ? NULL_HASH_CODE : $subscriptionMode.hashCode());

            return result;
        }

        @Override
        public String toString() {
            return "RedissonProperties.ClusterServersConfig(clientName=" + getClientName() + ", masterConnectionMinimumIdleSize=" + getMasterConnectionMinimumIdleSize() + ", masterConnectionPoolSize=" + getMasterConnectionPoolSize() + ", slaveConnectionMinimumIdleSize=" + getSlaveConnectionMinimumIdleSize() + ", slaveConnectionPoolSize=" + getSlaveConnectionPoolSize() + ", idleConnectionTimeout=" + getIdleConnectionTimeout() + ", timeout=" + getTimeout() + ", subscriptionConnectionPoolSize=" + getSubscriptionConnectionPoolSize() + ", readMode=" + getReadMode() + ", subscriptionMode=" + getSubscriptionMode() + ")";
        }


        public String getClientName() {
            return this.clientName;
        }


        public int getMasterConnectionMinimumIdleSize() {
            return this.masterConnectionMinimumIdleSize;
        }


        public int getMasterConnectionPoolSize() {
            return this.masterConnectionPoolSize;
        }


        public int getSlaveConnectionMinimumIdleSize() {
            return this.slaveConnectionMinimumIdleSize;
        }


        public int getSlaveConnectionPoolSize() {
            return this.slaveConnectionPoolSize;
        }


        public int getIdleConnectionTimeout() {
            return this.idleConnectionTimeout;
        }


        public int getTimeout() {
            return this.timeout;
        }


        public int getSubscriptionConnectionPoolSize() {
            return this.subscriptionConnectionPoolSize;
        }


        public ReadMode getReadMode() {
            return this.readMode;
        }


        public SubscriptionMode getSubscriptionMode() {
            return this.subscriptionMode;
        }
    }

}
