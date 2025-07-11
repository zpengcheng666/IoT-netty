package com.sydh.common.exception.job;


public class TaskException
        extends Exception {
    private static final long V = 1L;
    private Code W;

    public TaskException(String msg, Code code) {
        this(msg, code, null);
    }


    public TaskException(String msg, Code code, Exception nestedEx) {
        super(msg, nestedEx);
        this.W = code;
    }


    public Code getCode() {
        return this.W;
    }

    public enum Code {
        TASK_EXISTS, NO_TASK_EXISTS, TASK_ALREADY_STARTED, UNKNOWN, CONFIG_ERROR, TASK_NODE_NOT_AVAILABLE;
    }
}
