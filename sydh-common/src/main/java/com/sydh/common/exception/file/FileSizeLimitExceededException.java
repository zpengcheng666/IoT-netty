package com.sydh.common.exception.file;


public class FileSizeLimitExceededException
        extends FileException {
    private static final long M = 1L;

    public FileSizeLimitExceededException(long defaultMaxSize) {
        super("upload.exceed.maxSize", new Object[]{Long.valueOf(defaultMaxSize)});
    }
}
