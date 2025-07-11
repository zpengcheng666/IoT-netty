package com.sydh.common.constant;

import de.schlichtherle.util.ObfuscatedString;


public class Constants {
    public static final String UTF8 = "UTF-8";
    public static final String GBK = "GBK";
    public static final String WWW = "www.";
    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    public static final Integer SUCCESS = Integer.valueOf(0);

    public static final Integer FAIL = Integer.valueOf(1);

    public static final String LOGIN_SUCCESS = "Success";


    public static final String LOGOUT = "Logout";


    public static final String REGISTER = "Register";


    public static final String LOGIN_FAIL = "Error";


    public static final Integer CAPTCHA_EXPIRATION = Integer.valueOf(2);


    public static final String TOKEN = "token";


    public static final String TOKEN_PREFIX = "Bearer ";


    public static final String LOGIN_USER_KEY = "login_user_key";


    public static final String JWT_USERID = "userid";


    public static final String JWT_USERNAME = "sub";


    public static final String JWT_AVATAR = "avatar";


    public static final String JWT_CREATED = "created";


    public static final String JWT_AUTHORITIES = "authorities";


    public static final String RESOURCE_PREFIX = "/profile";


    public static final String LOOKUP_RMI = "rmi:";


    public static final String LOOKUP_LDAP = "ldap:";


    public static final String LOOKUP_LDAPS = "ldaps:";


    public static final String[] JOB_WHITELIST_STR = new String[]{"com.sydh"};


    public static final String[] JOB_ERROR_STR = new String[]{"java.net.URL", "javax.naming.InitialContext", "org.yaml.snakeyaml", "org.springframework", "org.apache", "com.sydh.common.utils.file", "com.sydh.common.config"};


    public static final String LANGUAGE = "language";


    public static final String ZH_CN = "zh-CN";


    public static final String EN_US = "en-US";


    public static final String MENU = "menu";

    public static final String DICT_DATA = "dict_data";

    public static final String DICT_TYPE = "dict_type";

    public static final String THINGS_MODEL = "things_model";

    public static final String THINGS_MODEL_TEMPLATE = "things_model_template";

    public static final String SCADA_SHARE_KEY = "scada_share_key";

    public static final String EXC_LICENSE_HAS_EXPIRED = (new ObfuscatedString(new long[]{1000558500458715757L, -6998261911041258483L, -5490039629745846648L, 3561172928787106880L})).toString();
}
