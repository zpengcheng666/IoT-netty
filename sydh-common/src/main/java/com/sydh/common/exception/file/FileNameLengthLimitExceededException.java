package com.sydh.common.exception.file;


public class FileNameLengthLimitExceededException
        extends FileException {
    private static final long L = 1L;

    public FileNameLengthLimitExceededException(int defaultFileNameLength) {
        super("upload.filename.exceed.length", new Object[]{Integer.valueOf(defaultFileNameLength)});
    }
}
