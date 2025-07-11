package com.sydh.common.utils.file;

import com.sydh.common.config.RuoYiConfig;
import com.sydh.common.exception.file.FileNameLengthLimitExceededException;
import com.sydh.common.exception.file.FileSizeLimitExceededException;
import com.sydh.common.exception.file.InvalidExtensionException;
import com.sydh.common.utils.DateUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.common.utils.uuid.Seq;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;


public class FileUploadUtils {
    public static final long DEFAULT_MAX_SIZE = 52428800L;
    public static final int DEFAULT_FILE_NAME_LENGTH = 100;
    private static String aU = RuoYiConfig.getProfile();


    public static void setDefaultBaseDir(String defaultBaseDir) {
        aU = defaultBaseDir;
    }


    public static String getDefaultBaseDir() {
        return aU;
    }


    public static final String upload(MultipartFile file) throws IOException {
        try {
            return upload(getDefaultBaseDir(), file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
        } catch (Exception exception) {

            throw new IOException(exception.getMessage(), exception);
        }
    }


    public static final String upload(String baseDir, MultipartFile file) throws IOException {
        try {
            return upload(baseDir, file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
        } catch (Exception exception) {

            throw new IOException(exception.getMessage(), exception);
        }
    }


    public static final String upload(String baseDir, MultipartFile file, String[] allowedExtension) throws FileSizeLimitExceededException, IOException, FileNameLengthLimitExceededException, InvalidExtensionException {
        int i = ((String) Objects.<String>requireNonNull(file.getOriginalFilename())).length();
        if (i > 100) {
            throw new FileNameLengthLimitExceededException(100);
        }

        assertAllowed(file, allowedExtension);

        String str1 = extractFilename(file);

        String str2 = getAbsoluteFile(baseDir, str1).getAbsolutePath();
        file.transferTo(Paths.get(str2, new String[0]));
        return getPathFileName(baseDir, str1);
    }


    public static final String extractFilename(MultipartFile file) {
        return StringUtils.format("{}/{}_{}.{}", new Object[]{DateUtils.datePath(),
                FilenameUtils.getBaseName(file.getOriginalFilename()), Seq.getId("UPLOAD"), getExtension(file)});
    }


    public static final File getAbsoluteFile(String uploadDir, String fileName) throws IOException {
        File file = new File(uploadDir + File.separator + fileName);

        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
        }
        return file;
    }


    public static final String getPathFileName(String uploadDir, String fileName) throws IOException {
        int i = RuoYiConfig.getProfile().length() + 1;
        String str = StringUtils.substring(uploadDir, i);
        return "/profile/" + str + "/" + fileName;
    }


    public static final void assertAllowed(MultipartFile file, String[] allowedExtension) throws FileSizeLimitExceededException, InvalidExtensionException {
        long l = file.getSize();
        if (l > 52428800L) {
            throw new FileSizeLimitExceededException(50L);
        }

        String str1 = file.getOriginalFilename();
        String str2 = getExtension(file);
        if (allowedExtension != null && !isAllowedExtension(str2, allowedExtension)) {

            if (allowedExtension == MimeTypeUtils.IMAGE_EXTENSION) {
                throw new InvalidExtensionException.InvalidImageExtensionException(allowedExtension, str2, str1);
            }

            if (allowedExtension == MimeTypeUtils.FLASH_EXTENSION) {
                throw new InvalidExtensionException.InvalidFlashExtensionException(allowedExtension, str2, str1);
            }

            if (allowedExtension == MimeTypeUtils.MEDIA_EXTENSION) {
                throw new InvalidExtensionException.InvalidMediaExtensionException(allowedExtension, str2, str1);
            }

            if (allowedExtension == MimeTypeUtils.VIDEO_EXTENSION) {
                throw new InvalidExtensionException.InvalidVideoExtensionException(allowedExtension, str2, str1);
            }


            throw new InvalidExtensionException(allowedExtension, str2, str1);
        }
    }


    public static final boolean isAllowedExtension(String extension, String[] allowedExtension) {
        for (String str : allowedExtension) {

            if (str.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }


    public static final String getExtension(MultipartFile file) {
        String str = FilenameUtils.getExtension(file.getOriginalFilename());
        if (StringUtils.isEmpty(str)) {
            str = MimeTypeUtils.getExtension(Objects.<String>requireNonNull(file.getContentType()));
        }
        return str;
    }
}
