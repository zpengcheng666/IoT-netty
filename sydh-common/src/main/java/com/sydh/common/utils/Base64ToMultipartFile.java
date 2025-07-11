package com.sydh.common.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.web.multipart.MultipartFile;

/**
 * 将 Base64 编码的数据转换为 MultipartFile 接口实现类
 */
public class Base64ToMultipartFile implements MultipartFile {

    private final byte[] fileBytes;
    private final String fileExtension;
    private final String contentType;
    private final String originalFilename;

    /**
     * 通过 Base64 数据和 Data URI 构建 MultipartFile
     *
     * @param base64    Base64 编码字符串
     * @param dataUri   MIME 类型信息，如 "image/png"
     */
    public Base64ToMultipartFile(String base64, String dataUri) {
        if (base64 == null || dataUri == null || !dataUri.contains("/")) {
            throw new IllegalArgumentException("Base64 或 dataUri 参数不合法");
        }

        this.fileBytes = Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8));
        String[] mediaTypeParts = dataUri.split(";")[0].split("/");
        if (mediaTypeParts.length < 2) {
            throw new IllegalArgumentException("Data URI 格式错误");
        }
        this.fileExtension = mediaTypeParts[1];
        this.contentType = mediaTypeParts[0] + "/" + fileExtension;
        this.originalFilename = generateOriginalFilename();
    }

    /**
     * 自定义扩展名和内容类型构建 MultipartFile
     *
     * @param base64       Base64 编码字符串
     * @param extension    文件扩展名
     * @param contentType  内容类型
     */
    public Base64ToMultipartFile(String base64, String extension, String contentType) {
        this.fileBytes = Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8));
        this.fileExtension = extension;
        this.contentType = contentType;
        this.originalFilename = generateOriginalFilename();
    }

    private String generateOriginalFilename() {
        return "file_" + System.currentTimeMillis() + "." + fileExtension;
    }

    @Override
    public String getName() {
        return "param_" + System.currentTimeMillis();
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return fileBytes == null || fileBytes.length == 0;
    }

    @Override
    public long getSize() {
        return fileBytes.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return fileBytes;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(fileBytes);
    }

    @Override
    public void transferTo(File file) throws IOException, IllegalStateException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(this.fileBytes);
        }
    }
}
