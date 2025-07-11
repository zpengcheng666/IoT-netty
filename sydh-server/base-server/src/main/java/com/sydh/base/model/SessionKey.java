package com.sydh.base.model;

import com.sydh.base.session.Session;

/**
 * @author gsb
 * @date 2023/3/9 10:03
 */
public enum SessionKey {

    DeviceMsg;

    public static DeviceMsg getDeviceMsg(Session session){
       return (DeviceMsg)session.getAttribute(SessionKey.DeviceMsg);
    }
}
