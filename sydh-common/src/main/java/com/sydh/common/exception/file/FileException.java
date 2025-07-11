package com.sydh.common.exception.file;

import com.sydh.common.exception.base.BaseException;


public class FileException
        extends BaseException {
    private static final long K = 1L;

    public FileException(String code, Object[] args) {
        super("file", code, args, null);
    }
}
