package com.fastbee.common.exception.user;

import com.fastbee.common.exception.base.BaseException;


public class UserException
        extends BaseException {
    private static final long Z = 1L;

    public UserException(String code, Object[] args) {
        super("user", code, args, null);
    }
}
