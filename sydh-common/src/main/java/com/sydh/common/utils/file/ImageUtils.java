package com.sydh.common.utils.file;

import com.sydh.common.config.RuoYiConfig;
import com.sydh.common.utils.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ImageUtils {
    private static final Logger aV = LoggerFactory.getLogger(ImageUtils.class);


    public static byte[] getImage(String imagePath) {
        InputStream inputStream = getFile(imagePath);

        try {
            return IOUtils.toByteArray(inputStream);
        } catch (Exception exception) {

            aV.error("图片加载异常 {}", exception);
            return null;
        } finally {

            IOUtils.closeQuietly(inputStream);
        }
    }


    public static InputStream getFile(String imagePath) {
        try {
            byte[] arrayOfByte = readFile(imagePath);
            arrayOfByte = Arrays.copyOf(arrayOfByte, arrayOfByte.length);
            return new ByteArrayInputStream(arrayOfByte);
        } catch (Exception exception) {

            aV.error("获取图片异常 {}", exception);

            return null;
        }
    }


    public static byte[] readFile(String url) {
        InputStream inputStream = null;

        try {
            if (url.startsWith("http")) {


                URL uRL = new URL(url);
                URLConnection uRLConnection = uRL.openConnection();
                uRLConnection.setConnectTimeout(30000);
                uRLConnection.setReadTimeout(60000);
                uRLConnection.setDoInput(true);
                inputStream = uRLConnection.getInputStream();

            } else {

                String str1 = RuoYiConfig.getProfile();
                String str2 = str1 + StringUtils.substringAfter(url, "/profile");
                inputStream = new FileInputStream(str2);
            }
            return IOUtils.toByteArray(inputStream);
        } catch (Exception exception) {

            aV.error("获取文件路径异常 {}", exception);
            return null;
        } finally {

            IOUtils.closeQuietly(inputStream);
        }
    }
}
