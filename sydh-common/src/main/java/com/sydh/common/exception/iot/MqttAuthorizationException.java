package com.sydh.common.exception.iot;

import com.sydh.common.exception.GlobalException;


public class MqttAuthorizationException
        extends GlobalException {
    public MqttAuthorizationException() {
    }

    public MqttAuthorizationException(String messageId) {
        super(messageId);
    }
}
