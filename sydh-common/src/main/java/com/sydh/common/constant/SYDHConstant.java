package com.sydh.common.constant;

public interface SYDHConstant {
    interface TRANSPORT {
        public static final String MQTT = "MQTT";
        public static final String TCP = "TCP";
        public static final String COAP = "COAP";
        public static final String UDP = "UDP";
        public static final String GB28181 = "GB28181";
    }

    public static interface URL {
        public static final String WX_MINI_PROGRAM_PUSH_URL_PREFIX = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send";
        public static final String WX_GET_ACCESS_TOKEN_URL_PREFIX = "https://api.weixin.qq.com/sns/oauth2/access_token";
        public static final String WX_MINI_PROGRAM_GET_USER_SESSION_URL_PREFIX = "https://api.weixin.qq.com/sns/jscode2session";
        public static final String WX_MINI_PROGRAM_GET_ACCESS_TOKEN_URL_PREFIX = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";
        public static final String WX_GET_USER_INFO_URL_PREFIX = "https://api.weixin.qq.com/sns/userinfo";
        public static final String WX_GET_USER_PHONE_URL_PREFIX = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=";
        public static final String WECOM_GET_ACCESSTOKEN = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";
        public static final String WECOM_APPLY_SEND = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=";
        public static final String WX_PUBLIC_ACCOUNT_GET_USER_INFO_URL_PREFIX = "https://api.weixin.qq.com/cgi-bin/user/info";
        public static final String WX_PUBLIC_ACCOUNT_TEMPLATE_SEND_URL_PREFIX = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=";
    }

    public static interface PROTOCOL {
        public static final String ModbusRtu = "MODBUS-RTU";
        public static final String YinErDa = "YinErDa";
        public static final String JsonObject = "JSONOBJECT";
        public static final String JsonArray = "JSON";
        public static final String ModbusRtuPak = "MODBUS-RTU-PAK";
        public static final String NetOTA = "OTA-NET";
        public static final String FlowMeter = "FlowMeter";
        public static final String RJ45 = "RJ45";
        public static final String ModbusToJson = "MODBUS-JSON";
        public static final String ModbusToJsonHP = "MODBUS-JSON-HP";
        public static final String ModbusToJsonZQWL = "MODBUS-JSON-ZQWL";
        public static final String JsonObject_ChenYi = "JSONOBJECT-CHENYI";
        public static final String GEC6100D = "MODBUS-JSON-GEC6100D";
        public static final String SGZ = "SGZ";
        public static final String CH = "CH";
        public static final String ModbusTcp = "MODBUS-TCP";
        public static final String ModbusTcpOverRtu = "MODBUS-TCP-OVER-RTU";
        public static final String JsonGateway = "JSON-GATEWAY";
    }

    public static interface REDIS {
        public static final String DEVICE_ONLINE_LIST = "device:online:list";
        public static final String DEVICE_RUNTIME_DATA = "device:runtime:";
        public static final String DEVICE_PROTOCOL_PARAM = "device:param:";
        public static final String DEVICE_MESSAGE_ID = "device:messageId:";
        public static final String FIRMWARE_VERSION = "device:firmware:";
        public static final String DEVICE_MSG = "device:msg:";
        public static final String PROP_READ_STORE = "prop:read:store:";
        public static final String RECORDINFO_KEY = "sip:recordinfo:";
        public static final String DEVICEID_KEY = "sip:deviceidset:";
        public static final String CHANNELID_KEY = "sip:channelset:";
        public static final String STREAM_KEY = "sip:stream:";
        public static final String INVITE_KEY = "sip:invite:";
        public static final String SIP_CSEQ_PREFIX = "sip:CSEQ:";
        public static final String DEFAULT_SIP_CONFIG = "sip:config";
        public static final String DEFAULT_MEDIA_CONFIG = "sip:mediaconfig";
        public static final String SENDRTP_KEY = "sip:sendrtp:";
        public static final String SENDRTP_CALLID_KEY = "sip:sendrtp:callid:";
        public static final String SENDRTP_STREAM_KEY = "sip:sendrtp:stream:";
        public static final String SENDRTP_CHANNEL_KEY = "sip:sendrtp:channel:";
        public static final String RULE_SILENT_TIME = "rule:SilentTime";
        public static final String MESSAGE_RETAIN_TOTAL = "message:retain:total";
        public static final String MESSAGE_SEND_TOTAL = "message:send:total";
        public static final String MESSAGE_RECEIVE_TOTAL = "message:receive:total";
        public static final String MESSAGE_CONNECT_TOTAL = "message:connect:total";
        public static final String MESSAGE_AUTH_TOTAL = "message:auth:total";
        public static final String MESSAGE_SUBSCRIBE_TOTAL = "message:subscribe:total";
        public static final String MESSAGE_RECEIVE_TODAY = "message:receive:today";
        public static final String MESSAGE_SEND_TODAY = "message:send:today";
        public static final String DEVICE_PRE_KEY = "TSLV:";
        public static final String TSL_PRE_KEY = "TSL:";
        public static final String MODBUS_PRE_KEY = "MODBUS:";
        public static final String POLL_MODBUS_KEY = "MODBUS:POLL:";
        public static final String MODBUS_RUNTIME = "MODBUS:RUNTIME:";
        public static final String MODBUS_LOCK = "MODBUS:LOCK:";
        public static final String NOTIFY_WECOM_APPLY_ACCESSTOKEN = "notify:wecom:apply:";
        public static final String SCENE_MODEL_TAG_ID = "SMTV:";
        public static final String MODBUS_TCP = "MODBUS:TCP:";
        public static final String MODBUS_TCP_RUNTIME = "MODBUS:TCP:RUNTIME:";
        public static final String DEVICE_OTA_DATA = "device:ota:";
    }

    public static interface CHANNEL {
        public static final String DEVICE_STATUS = "device_status";
        public static final String PROP_READ = "prop_read";
        public static final String PUBLISH = "publish";
        public static final String FUNCTION_INVOKE = "function_invoke";
        public static final String EVENT = "event";
        public static final String OTHER = "other";
        public static final String PUBLISH_ACK = "publish_ack";
        public static final String PUB_REC = "pub_rec";
        public static final String PUB_REL = "pub_rel";
        public static final String PUB_COMP = "pub_comp";
        public static final String UPGRADE = "upgrade";
        public static final String SUFFIX = "group";
        public static final String DEVICE_STATUS_GROUP = "device_statusgroup";
        public static final String PROP_READ_GROUP = "prop_readgroup";
        public static final String FUNCTION_INVOKE_GROUP = "function_invokegroup";
        public static final String PUBLISH_GROUP = "publishgroup";
        public static final String PUBLISH_ACK_GROUP = "publish_ackgroup";
        public static final String PUB_REC_GROUP = "pub_recgroup";
        public static final String PUB_REL_GROUP = "pub_relgroup";
        public static final String PUB_COMP_GROUP = "pub_compgroup";
        public static final String UPGRADE_GROUP = "upgradegroup";
    }

    public static interface MQTT {
        public static final String PREDIX = "/+/+";
        public static final String ONE_PREDIX = "/+";
        public static final String OTA_REPLY = "/upgrade/reply";
    }

    public static interface TASK {
        public static final String DEVICE_STATUS_TASK = "deviceStatusTask";
        public static final String DEVICE_UP_MESSAGE_TASK = "deviceUpMessageTask";
        public static final String DEVICE_REPLY_MESSAGE_TASK = "deviceReplyMessageTask";
        public static final String DEVICE_DOWN_MESSAGE_TASK = "deviceDownMessageTask";
        public static final String FUNCTION_INVOKE_TASK = "functionInvokeTask";
        public static final String DEVICE_FETCH_PROP_TASK = "deviceFetchPropTask";
        public static final String DEVICE_OTHER_TASK = "deviceOtherMsgTask";
        public static final String DEVICE_TEST_TASK = "deviceTestMsgTask";
        public static final String MESSAGE_CONSUME_TASK = "messageConsumeTask";
        public static final String MESSAGE_CONSUME_TASK_PUB = "messageConsumeTaskPub";
        public static final String MESSAGE_CONSUME_TASK_FETCH = "messageConsumeTaskFetch";
        public static final String DELAY_UPGRADE_TASK = "delayUpgradeTask";
        public static final String OTA_THREAD_POOL = "otaThreadPoolTaskExecutor";
    }

    public static interface WS {
        public static final String HEART_BEAT = "heartbeat";
        public static final String HTTP_SERVER_CODEC = "httpServerCodec";
        public static final String AGGREGATOR = "aggregator";
        public static final String COMPRESSOR = "compressor";
        public static final String PROTOCOL = "protocol";
        public static final String MQTT_WEBSOCKET = "mqttWebsocket";
        public static final String DECODER = "decoder";
        public static final String ENCODER = "encoder";
        public static final String BROKER_HANDLER = "brokerHandler";
    }

    public static interface CLIENT {
        public static final String TOKEN = "sydh-smart!@#$123";
    }

    public static interface SERVER {
        public static final String UFT8 = "UTF-8";
        public static final Long DEVICE_PING_EXPIRED = Long.valueOf(90000L);
        public static final String GB2312 = "GB2312";
        public static final String MQTT = "mqtt";
        public static final String PORT = "port";
        public static final String ADAPTER = "adapter";
        public static final String FRAMEDECODER = "frameDecoder";
        public static final String DISPATCHER = "dispatcher";
        public static final String DECODER = "decoder";
        public static final String ENCODER = "encoder";
        public static final String MAXFRAMELENGTH = "maxFrameLength";
        public static final String SLICER = "slicer";
        public static final String DELIMITERS = "delimiters";
        public static final String IDLE = "idle";
        public static final String WS_PREFIX = "web-";
        public static final String WM_PREFIX = "server-";
        public static final String FAST_PHONE = "phone-";
    }
}

