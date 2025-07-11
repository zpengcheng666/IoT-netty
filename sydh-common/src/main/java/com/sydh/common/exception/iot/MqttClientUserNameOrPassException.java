package com.sydh.common.exception.iot;

import com.sydh.common.exception.GlobalException;


public class MqttClientUserNameOrPassException
        extends GlobalException {
    public MqttClientUserNameOrPassException() {
    }

    public MqttClientUserNameOrPassException(String message) {
        super(message);
    }
}
