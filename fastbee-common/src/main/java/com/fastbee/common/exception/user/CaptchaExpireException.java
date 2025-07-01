package com.fastbee.common.exception.user;


public class CaptchaExpireException extends UserException {
    private static final long Y = 1L;

    public CaptchaExpireException() {
        super("user.jcaptcha.expire", null);
    }
}
