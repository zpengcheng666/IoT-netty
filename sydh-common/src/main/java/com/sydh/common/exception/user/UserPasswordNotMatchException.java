package com.sydh.common.exception.user;


public class UserPasswordNotMatchException
        extends UserException {
    private static final long aa = 1L;

    public UserPasswordNotMatchException() {
        super("user.password.not.match", null);
    }
}
