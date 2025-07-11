package com.sydh.iot.cache;



import java.util.Map;

public interface IOtaTaskCache {

    void setOtaCache(String serialNumber, Map<String, String> hMap, long timeout);

    void removeOtaCache(String serialNumber);

    String getOtaCacheValue(String serialNumber, String hKey);

    void setOtaCacheValue(String serialNumber, String hKey, String value, long timeout);

    boolean checkOtaCacheExist(String serialNumber);
}
