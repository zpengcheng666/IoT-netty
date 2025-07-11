package com.sydh.common.utils.ip;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sydh.common.config.RuoYiConfig;
import com.sydh.common.utils.StringUtils;
import com.sydh.common.utils.http.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AddressUtils {
    private static final Logger bW = LoggerFactory.getLogger(AddressUtils.class);


    public static final String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp";


    public static final String UNKNOWN = "XX XX";


    public static String getRealAddressByIP(String ip) {
        if (IpUtils.internalIp(ip)) {
            return "内网IP";
        }
        if (RuoYiConfig.isAddressEnabled()) {

            try {

                String str1 = HttpUtils.sendGet("http://whois.pconline.com.cn/ipJson.jsp", "ip=" + ip + "&json=true", "GBK");
                if (StringUtils.isEmpty(str1)) {

                    bW.error("获取地理位置异常 {}", ip);
                    return "XX XX";
                }
                JSONObject jSONObject = JSON.parseObject(str1);
                String str2 = jSONObject.getString("pro");
                String str3 = jSONObject.getString("city");
                return String.format("%s %s", new Object[]{str2, str3});
            } catch (Exception exception) {

                bW.error("获取地理位置异常 {}", ip);
            }
        }
        return "XX XX";
    }
}
