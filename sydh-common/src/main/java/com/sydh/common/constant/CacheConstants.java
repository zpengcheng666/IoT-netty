package com.sydh.common.constant;

/**
 * 缓存的key 常量
 * 
 * @author sydh
 */
public class CacheConstants {
    
    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";
    
    /**
     * 在线用户 redis key
     */
    public static final String LOGIN_USERID_KEY = "login_userId:";
    
    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";
    
    /**
     * 参数管理 cache key
     */
    public static final String SYS_CONFIG_KEY = "sys_config:";
    
    /**
     * 字典管理 cache key
     */
    public static final String SYS_DICT_KEY = "sys_dict:";
    
    /**
     * SQL缓存 redis key
     */
    public static final String SQL_CACHE_KEY = "sql_cache:";
    
    /**
     * 防重提交 redis key
     */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";
    
    /**
     * 限流 redis key
     */
    public static final String RATE_LIMIT_KEY = "rate_limit:";
    
    /**
     * 登录账户密码错误次数 redis key
     */
    public static final String PWD_ERR_CNT_KEY = "pwd_err_cnt:";
    
    /**
     * 登录短信验证码 redis key
     */
    public static final String LOGIN_SMS_CAPTCHA_PHONE = "login_sms_captcha_phone:";
    
    /**
     * 微信access_token redis key
     */
    public static final String WECHAT_GET_ACCESS_TOKEN_APPID = "wechat_get_accessToken:";
    
    /**
     * 注册短信验证码 redis key
     */
    public static final String REGISTER_SMS_CAPTCHA_PHONE = "register_sms_captcha_phone:";
    
    /**
     * 设备OTA升级数据 redis key
     */
    public static final String DEVICE_OTA_DATA = "device:ota:";
    
    /**
     * 忘记密码短信验证 redis key
     */
    public static final String FORGOT_PASSWORD_SMS_VERIFY_PHONE = "forgot_password_sms_verify:";
    
    /**
     * 许可证数据 redis key
     */
    public static final String LICENSE_DATA = "sys_config:sys.license";
}


