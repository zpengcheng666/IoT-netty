package com.fastbee.common.exception.user;


public class CaptchaException
        extends UserException {
    private static final long X = 1L;

    public CaptchaException() {
        super("user.jcaptcha.error", null);
    }
}
