package com.sydh.common.utils.license.os;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.dtflys.forest.Forest;
import com.sydh.common.utils.StringUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractServerInfo {
    private static final Logger co = LoggerFactory.getLogger(AbstractServerInfo.class);
    private static final String cp = "http://whois.pconline.com.cn/ipJson.jsp";
    private static final String cq = "http://ip-api.com/json/";
    private static final String cr = "https://api.ipapi.is";

    public abstract List<String> getIpAddress() throws Exception;
    public abstract List<String> getMacAddress() throws Exception;


    public abstract String getCPUSerial() throws Exception;
    public abstract String getMainBoardSerial() throws Exception;


    protected List<InetAddress> getLocalAllInetAddress() throws Exception {
        ArrayList<InetAddress> arrayList = new ArrayList(4);
        for (Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces(); enumeration.hasMoreElements(); ) {
            NetworkInterface networkInterface = enumeration.nextElement();

            for (Enumeration<InetAddress> enumeration1 = networkInterface.getInetAddresses(); enumeration1.hasMoreElements(); ) {
                InetAddress inetAddress = enumeration1.nextElement();
                if (!inetAddress.isLoopbackAddress() &&
                        !inetAddress.isLinkLocalAddress() && !inetAddress.isMulticastAddress()) {
                    arrayList.add(inetAddress);
                }
            }
        }
        return arrayList;
    }


    protected String getMacByInetAddress(InetAddress inetAddr) {
        try {
            byte[] arrayOfByte = NetworkInterface.getByInetAddress(inetAddr).getHardwareAddress();
            StringBuilder stringBuilder = new StringBuilder();

            for (byte b = 0; b < arrayOfByte.length; b++) {
                if (b != 0) {
                    stringBuilder.append("-");
                }


                String str = Integer.toHexString(arrayOfByte[b] & 0xFF);
                if (str.length() == 1) {
                    stringBuilder.append("0").append(str);
                } else {
                    stringBuilder.append(str);
                }
            }
            return stringBuilder.toString().toUpperCase();
        } catch (SocketException socketException) {
            co.error("获取Mac地址异常 {}", socketException.getMessage());
            return null;
        }
    }

    public List<String> getCityCode() throws Exception {
        ArrayList<String> arrayList = new ArrayList();
        Boolean bool = Boolean.valueOf(true);
        try {
            List<String> list = null;
            String str = (String) Forest.get("http://whois.pconline.com.cn/ipJson.jsp").addQuery("json", Boolean.valueOf(true)).execute(String.class);

            if (StringUtils.isEmpty(str)) {
                return Collections.emptyList();
            }
            JSONObject jSONObject = JSON.parseObject(str);
            if (!Objects.equals(jSONObject.getString("city"), "")) {
                co.info("location:{}", jSONObject);


                String str1 = jSONObject.getString("proCode");
                String str2 = jSONObject.getString("cityCode");


                arrayList.add(str1);
                arrayList.add(str2);
            } else {
                bool = Boolean.valueOf(false);
                list = getCityCode2();
            }
            return list;
        } catch (Exception exception) {
            if (bool.booleanValue()) {
                co.warn("换获取地址重试");
                return getCityCode2();
            }

            co.error("获取区域编码异常 {}", exception.getMessage());

            return Collections.emptyList();
        }
    }

    public List<String> getCityCode2() throws Exception {
        ArrayList<String> arrayList = new ArrayList();

        String str = (String) Forest.get("https://api.ipapi.is").execute(String.class);
        if (StringUtils.isEmpty(str)) {
            return Collections.emptyList();
        }
        JSONObject jSONObject1 = JSON.parseObject(str);
        JSONObject jSONObject2 = jSONObject1.getJSONObject("location");
        if (jSONObject2 != null) {
            String str1 = jSONObject2.getString("country_code");
            String str2 = str1 + jSONObject2.getString("state");
            String str3 = str2 + jSONObject2.getString("city");
            arrayList.add(str1);
            arrayList.add(str2);
            arrayList.add(str3);
        }
        co.info("location:{}", jSONObject2);
        return arrayList;
    }

    public String getServerRegion() {
        Boolean bool = Boolean.valueOf(true);
        try {
            String str = (String) Forest.get("http://whois.pconline.com.cn/ipJson.jsp").addQuery("json", Boolean.valueOf(true)).execute(String.class);

            if (StringUtils.isEmpty(str)) {
                return "区域编码异常";
            }
            JSONObject jSONObject = JSON.parseObject(str);
            if (!Objects.equals(jSONObject.getString("cityCode"), "0")) {
                co.info("location:{}", jSONObject);
                JSONObject jSONObject1 = new JSONObject();


                jSONObject1.put("ip", jSONObject.get("ip"));

                String str1 = jSONObject.getString("proCode");
                String str2 = jSONObject.getString("cityCode");


                jSONObject1.put("regionCode", str1);
                jSONObject1.put("cityCode", str2);
                jSONObject1.put("city", jSONObject.getString("city"));
                return jSONObject1.toString();
            }
            bool = Boolean.valueOf(false);
            return getServerRegion2();
        } catch (Exception exception) {
            if (bool.booleanValue()) {
                co.warn("换获取地址重试");
                return getServerRegion2();
            }
            co.error("获取区域编码异常 {}", exception.getMessage());
            return "区域编码异常";
        }
    }


    public String getServerRegion2() {
        String str1 = (String) Forest.get("https://api.ipapi.is").execute(String.class);
        if (StringUtils.isEmpty(str1)) {
            return "区域编码异常";
        }
        JSONObject jSONObject1 = JSON.parseObject(str1);
        JSONObject jSONObject2 = jSONObject1.getJSONObject("location");
        JSONObject jSONObject3 = new JSONObject();
        jSONObject3.put("ip", jSONObject1.get("ip"));
        String str2 = jSONObject2.getString("country_code");
        String str3 = str2 + jSONObject2.getString("state");
        String str4 = str3 + jSONObject2.getString("city");
        jSONObject3.put("countryCode", str2);
        jSONObject3.put("regionCode", str3);
        jSONObject3.put("cityCode", str4);
        jSONObject3.put("city", jSONObject2.getString("city"));
        co.info("location:{}", jSONObject3);
        return jSONObject3.toString();
    }
}
