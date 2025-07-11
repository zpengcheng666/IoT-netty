package com.sydh.common.exception.user;


public class UserPasswordRetryLimitExceedException
        extends UserException {
    private static final long ab = 1L;

    public UserPasswordRetryLimitExceedException(int retryLimitCount, int lockTime) {
        super("user.password.retry.limit.exceed", new Object[]{Integer.valueOf(retryLimitCount), Integer.valueOf(lockTime)});
    }
}
