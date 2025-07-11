package com.sydh.server.config;

import com.sydh.common.constant.SYDHConstant;
import com.sydh.common.enums.ServerType;
import com.sydh.server.Server;
import com.sydh.server.TCPServer;
import com.sydh.server.UDPServer;
import com.sydh.base.codec.Delimiter;
import com.sydh.base.codec.LengthField;
import com.sydh.base.codec.MessageDecoder;
import com.sydh.base.codec.MessageEncoder;
import com.sydh.base.core.HandlerInterceptor;
import com.sydh.base.core.HandlerMapping;
import com.sydh.base.session.SessionManager;
import io.netty.util.NettyRuntime;
import io.netty.util.internal.ObjectUtil;

/**
 * 基础配置类 - 优化支持数十万设备并发
 *
 * @Author guanshubiao
 * @Date 2022/9/12 20:22
 */
public class NettyConfig {

    public final int workerCore;
    /*boss线程核数*/
    public final int businessCore;
    /*读空闲时间*/
    public final int readerIdleTime;
    /*写空闲时间*/
    public final int writerIdleTime;
    /*读写空闲时间*/
    public final int allIdleTime;
    /*端口*/
    public final Integer port;
    /*TCP/UDP数据最大长度限定*/
    public final Integer maxFrameLength;
    // 新增：连接数限制配置
    public final Integer maxConnections;
    public final Integer backlogSize;
    // 新增：业务队列配置
    public final Integer businessQueueSize;
    public final Integer businessMaxThreads;
    
    /*基础编码*/
    public final MessageDecoder decoder;
    /*基础解码*/
    public final MessageEncoder encoder;
    public final Delimiter[] delimiters;
    public final LengthField lengthField;
    public final HandlerMapping handlerMapping;
    public final HandlerInterceptor handlerInterceptor;
    public final SessionManager sessionManager;
    /*基础服务端*/
    public Server server;
    public String name;
    /*服务名*/
    public final ServerType type;

    public NettyConfig(int workerGroup,
                       int businessGroup,
                       int readerIdleTime,
                       int writerIdleTime,
                       int allIdleTime,
                       Integer port,
                       Integer maxFrameLength,
                       Integer maxConnections,
                       Integer backlogSize,
                       Integer businessQueueSize,
                       Integer businessMaxThreads,
                       LengthField lengthField,
                       Delimiter[] delimiters,
                       MessageDecoder decoder,
                       MessageEncoder encoder,
                       HandlerMapping handlerMapping,
                       HandlerInterceptor handlerInterceptor,
                       SessionManager sessionManager,
                       ServerType type,
                       String name,
                       Server server) {

        /*校验值是否正确*/
        ObjectUtil.checkNotNull(port, SYDHConstant.SERVER.PORT);
        ObjectUtil.checkPositive(port, SYDHConstant.SERVER.PORT);

        if (ServerType.UDP == type || ServerType.TCP == type) {
            ObjectUtil.checkNotNull(decoder, "decoder");
            ObjectUtil.checkNotNull(encoder, "encoder");
            ObjectUtil.checkNotNull(handlerMapping, "handlerMapping");
            ObjectUtil.checkNotNull(handlerInterceptor, "handlerInterceptor");
        }
        if (type == ServerType.TCP) {
            ObjectUtil.checkNotNull(maxFrameLength, SYDHConstant.SERVER.MAXFRAMELENGTH);
            ObjectUtil.checkPositive(maxFrameLength, SYDHConstant.SERVER.MAXFRAMELENGTH);
            // ObjectUtil.checkNotNull(delimiters,SYDHConstant.SERVER.DELIMITERS);

        }
        /*获取核数*/
        int processors = NettyRuntime.availableProcessors();
        
        // 原始配置（注释保留）
        // this.workerCore = workerGroup > 0 ? workerGroup : processors * 4; // 增加worker线程
        // this.businessCore = businessGroup > 0 ? businessGroup : processors * 2; // 增加业务线程
        // this.workerCore = workerGroup > 0 ? workerGroup : processors + 2;
        // this.businessCore = businessGroup > 0 ? businessGroup : Math.max(1, processors >> 1);
        
        // 优化配置：支持数十万设备并发（保留原有逻辑作为后备）
        if (workerGroup > 0 && businessGroup > 0) {
            // 如果明确指定了线程数，则使用指定值
            this.workerCore = workerGroup;
            this.businessCore = businessGroup;
        } else {
            // 否则使用自适应计算
            this.workerCore = calculateWorkerThreads(workerGroup, processors, type);
            this.businessCore = calculateBusinessThreads(businessGroup, processors, type);
        }
        
        // 新增配置项
        this.maxConnections = maxConnections != null ? maxConnections : getDefaultMaxConnections(type);
        this.backlogSize = backlogSize != null ? backlogSize : getDefaultBacklogSize(type);
        this.businessQueueSize = businessQueueSize != null ? businessQueueSize : getDefaultBusinessQueueSize(type);
        this.businessMaxThreads = businessMaxThreads != null ? businessMaxThreads : this.businessCore * 2;
        
        this.readerIdleTime = readerIdleTime;
        this.writerIdleTime = writerIdleTime;
        this.allIdleTime = allIdleTime;
        this.port = port;
        this.maxFrameLength = maxFrameLength;
        this.lengthField = lengthField;
        this.delimiters = delimiters;
        this.decoder = decoder;
        this.encoder = encoder;
        this.handlerMapping = handlerMapping;
        this.handlerInterceptor = handlerInterceptor;
        this.sessionManager = sessionManager != null ? sessionManager : new SessionManager();
        this.type = type;

        switch (type) {
            case TCP:
                this.server = new TCPServer(this);
                this.name = name != null ? name : ServerType.TCP.name();
                break;
            case UDP:
                this.name = name != null ? name : ServerType.UDP.name();
                this.server = new UDPServer(this);
                break;
            case MQTT:
            case WEBSOCKET:
                this.name = name != null ? name : ServerType.MQTT.name();
                this.server = server;
                this.server.config = this;
                break;
            case HTTP:
                this.name = name != null ? name : ServerType.HTTP.name();
                this.server = server;
                this.server.config = this;
                break;
            case COAP:
                this.name = name != null ? name : ServerType.COAP.name();
                this.server = server;
                ;
                this.server.config = this;
                break;
            default:
        }
    }

    /**
     * 计算Worker线程数 - 根据协议类型和预期连接数优化
     */
    private int calculateWorkerThreads(int workerGroup, int processors, ServerType type) {
        if (workerGroup > 0) {
            return workerGroup;
        }
        
        switch (type) {
            case MQTT:
                // MQTT协议：支持大量长连接，需要更多Worker线程
                return Math.min(processors * 8, 128); // 最多128个线程
            case TCP:
                // TCP协议：高并发数据传输
                return Math.min(processors * 6, 96);
            case HTTP:
                // HTTP协议：短连接为主
                return Math.min(processors * 4, 64);
            case UDP:
                // UDP协议：无连接状态，线程需求较少
                return Math.min(processors * 2, 32);
            case COAP:
                // CoAP协议：轻量级，中等线程数
                return Math.min(processors * 3, 48);
            default:
                return Math.min(processors * 4, 64);
        }
    }

    /**
     * 计算业务线程数 - 根据协议类型和业务复杂度优化
     */
    private int calculateBusinessThreads(int businessGroup, int processors, ServerType type) {
        if (businessGroup > 0) {
            return businessGroup;
        }
        
        switch (type) {
            case MQTT:
                // MQTT需要处理大量订阅/发布逻辑
                return Math.min(processors * 4, 200);
            case TCP:
                // TCP业务处理相对复杂
                return Math.min(processors * 3, 150);
            case HTTP:
                // HTTP业务处理可能较重
                return Math.min(processors * 2, 100);
            case UDP:
            case COAP:
                // UDP/CoAP业务相对简单
                return Math.min(processors * 2, 80);
            default:
                return Math.min(processors * 2, 100);
        }
    }

    /**
     * 获取默认最大连接数
     */
    private int getDefaultMaxConnections(ServerType type) {
        switch (type) {
            case MQTT:
                return 500000; // MQTT支持50万连接
            case TCP:
                return 300000; // TCP支持30万连接
            case HTTP:
                return 100000; // HTTP支持10万连接
            case UDP:
                return 200000; // UDP支持20万连接
            case COAP:
                return 150000; // CoAP支持15万连接
            default:
                return 100000;
        }
    }

    /**
     * 获取默认Backlog大小
     */
    private int getDefaultBacklogSize(ServerType type) {
        switch (type) {
            case MQTT:
            case TCP:
                return 8192; // 高并发协议使用更大的backlog
            case HTTP:
                return 4096;
            case UDP:
                return 2048;
            case COAP:
                return 2048;
            default:
                return 2048;
        }
    }

    /**
     * 获取默认业务队列大小
     */
    private int getDefaultBusinessQueueSize(ServerType type) {
        switch (type) {
            case MQTT:
                return 50000; // MQTT消息量大，需要更大队列
            case TCP:
                return 30000;
            case HTTP:
                return 20000;
            case UDP:
            case COAP:
                return 15000;
            default:
                return 20000;
        }
    }

    public Server build() {
        return server;
    }

    public static NettyConfig.Builder custom() {
        return new Builder();
    }

    public static class Builder {

        private int workerCore;
        private int businessCore;
        private int readerIdleTime;
        private int writerIdleTime = 0;
        private int allIdleTime = 0;
        private Integer port;
        private Integer maxFrameLength;
        // 新增配置项
        private Integer maxConnections;
        private Integer backlogSize;
        private Integer businessQueueSize;
        private Integer businessMaxThreads;
        
        private LengthField lengthField;
        private Delimiter[] delimiters;
        private MessageDecoder decoder;
        private MessageEncoder encoder;
        private HandlerMapping handlerMapping;
        private HandlerInterceptor handlerInterceptor;
        private SessionManager sessionManager;
        private ServerType type;
        private String name;
        private Server server;

        public Builder() {
        }

        public Builder setThreadGroup(int workerCore, int businessCore) {
            this.workerCore = workerCore;
            this.businessCore = businessCore;
            return this;
        }

        public Builder setIdleStateTime(int readerIdleTime, int writerIdleTime, int allIdleTime) {
            this.readerIdleTime = readerIdleTime;
            this.writerIdleTime = writerIdleTime;
            this.allIdleTime = allIdleTime;
            return this;
        }

        public Builder setPort(Integer port) {
            this.port = port;
            return this;
        }

        public Builder setServer(Server server) {
            this.server = server;
            return this;
        }

        public Builder setMaxFrameLength(Integer maxFrameLength) {
            this.maxFrameLength = maxFrameLength;
            return this;
        }

        // 新增方法
        public Builder setMaxConnections(Integer maxConnections) {
            this.maxConnections = maxConnections;
            return this;
        }

        public Builder setBacklogSize(Integer backlogSize) {
            this.backlogSize = backlogSize;
            return this;
        }

        public Builder setBusinessQueueSize(Integer businessQueueSize) {
            this.businessQueueSize = businessQueueSize;
            return this;
        }

        public Builder setBusinessMaxThreads(Integer businessMaxThreads) {
            this.businessMaxThreads = businessMaxThreads;
            return this;
        }

        public Builder setLengthField(LengthField lengthField) {
            this.lengthField = lengthField;
            return this;
        }

        public Builder setDelimiters(byte[][] delimiters) {
            Delimiter[] t = new Delimiter[delimiters.length];
            for (int i = 0; i < delimiters.length; i++) {
                t[i] = new Delimiter(delimiters[i]);
            }
            this.delimiters = t;
            return this;
        }

        public Builder setDelimiters(Delimiter... delimiters) {
            this.delimiters = delimiters;
            return this;
        }

        public Builder setDecoder(MessageDecoder decoder) {
            this.decoder = decoder;
            return this;
        }

        public Builder setEncoder(MessageEncoder encoder) {
            this.encoder = encoder;
            return this;
        }

        public Builder setHandlerMapping(HandlerMapping handlerMapping) {
            this.handlerMapping = handlerMapping;
            return this;
        }

        public Builder setHandlerInterceptor(HandlerInterceptor handlerInterceptor) {
            this.handlerInterceptor = handlerInterceptor;
            return this;
        }

        public Builder setSessionManager(SessionManager sessionManager) {
            this.sessionManager = sessionManager;
            return this;
        }

        public Builder setType(ServerType type) {
            this.type = type;
            return this;
        }


        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Server build() {
            return new NettyConfig(
                    this.workerCore,
                    this.businessCore,
                    this.readerIdleTime,
                    this.writerIdleTime,
                    this.allIdleTime,
                    this.port,
                    this.maxFrameLength,
                    this.maxConnections,
                    this.backlogSize,
                    this.businessQueueSize,
                    this.businessMaxThreads,
                    this.lengthField,
                    this.delimiters,
                    this.decoder,
                    this.encoder,
                    this.handlerMapping,
                    this.handlerInterceptor,
                    this.sessionManager,
                    this.type,
                    this.name,
                    this.server
            ).build();
        }
    }
}
