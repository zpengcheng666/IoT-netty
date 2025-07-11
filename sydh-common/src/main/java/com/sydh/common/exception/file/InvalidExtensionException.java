package com.sydh.common.exception.file;

import java.util.Arrays;

import org.apache.commons.fileupload.FileUploadException;


public class InvalidExtensionException
        extends FileUploadException {
    private static final long N = 1L;
    private String[] O;
    private String P;
    private String Q;

    public InvalidExtensionException(String[] allowedExtension, String extension, String filename) {
        super("文件[" + filename + "]后缀[" + extension + "]不正确，请上传" + Arrays.toString((Object[]) allowedExtension) + "格式");
        this.O = allowedExtension;
        this.P = extension;
        this.Q = filename;
    }


    public String[] getAllowedExtension() {
        return this.O;
    }


    public String getExtension() {
        return this.P;
    }


    public String getFilename() {
        return this.Q;
    }

    public static class InvalidImageExtensionException
            extends InvalidExtensionException {
        private static final long S = 1L;

        public InvalidImageExtensionException(String[] allowedExtension, String extension, String filename) {
            super(allowedExtension, extension, filename);
        }
    }

    public static class InvalidFlashExtensionException
            extends InvalidExtensionException {
        private static final long R = 1L;

        public InvalidFlashExtensionException(String[] allowedExtension, String extension, String filename) {
            super(allowedExtension, extension, filename);
        }
    }

    public static class InvalidMediaExtensionException
            extends InvalidExtensionException {
        private static final long T = 1L;

        public InvalidMediaExtensionException(String[] allowedExtension, String extension, String filename) {
            super(allowedExtension, extension, filename);
        }
    }

    public static class InvalidVideoExtensionException
            extends InvalidExtensionException {
        private static final long U = 1L;

        public InvalidVideoExtensionException(String[] allowedExtension, String extension, String filename) {
            super(allowedExtension, extension, filename);
        }
    }
}
