package com.sydh.record.controller;

import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.redis.RedisCache;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.utils.StringUtils;
import com.sydh.oss.domain.OssDetail;
import com.sydh.oss.entity.UploadResult;
import com.sydh.oss.enums.AccessPolicyType;
import com.sydh.oss.service.OssClient;
import com.sydh.oss.service.OssFactory;
import com.sydh.record.dto.UserSettings;
import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/file")
public class DownloadController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(DownloadController.class);

    @Autowired
    private UserSettings userSettings;

    @Autowired
    private RedisCache redisCache;
    /**
     * 获取app+stream列表
     *
     * @return
     */
    @GetMapping(value = "/download/**")
    @ResponseBody
    public void download(HttpServletRequest request, HttpServletResponse response) {

        String resourcePath = request.getServletPath();
        resourcePath = resourcePath.substring("/file/download".length() + 1, resourcePath.length());
        String record = userSettings.getRecord();
        File file = new File(record + resourcePath);
        if (!file.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        /**
         * 参考实现来自： CSDN 进修的CODER SpringBoot Java实现Http方式分片下载断点续传+实现H5大视频渐进式播放
         * https://blog.csdn.net/lovequanquqn/article/details/104562945
         */
        String range = request.getHeader("Range");
        logger.info("current request rang:" + range);
        //开始下载位置
        long startByte = 0;
        //结束下载位置
        long endByte = file.length() - 1;
        logger.info("文件开始位置：{}，文件结束位置：{}，文件总长度：{}", startByte, endByte, file.length());

        //有range的话
        if (range != null && range.contains("bytes=") && range.contains("-")) {
            range = range.substring(range.lastIndexOf("=") + 1).trim();
            String[] ranges = range.split("-");
            try {
                //判断range的类型
                if (ranges.length == 1) {
                    // 类型一：bytes=-2343,
                    if (range.startsWith("-")) {
                        endByte = Long.parseLong(ranges[0]);
                    }
                    //类型二：bytes=2343-
                    else if (range.endsWith("-")) {
                        startByte = Long.parseLong(ranges[0]);
                    }
                }
                //类型三：bytes=22-2343
                else if (ranges.length == 2) {
                    startByte = Long.parseLong(ranges[0]);
                    endByte = Long.parseLong(ranges[1]);
                }

            } catch (NumberFormatException e) {
                startByte = 0;
                endByte = file.length() - 1;
                logger.error("Range Occur Error,Message:{}", e.getLocalizedMessage());
            }


        }

        // 要下载的长度
        long contentLength = endByte - startByte + 1;
        // 文件名
        String fileName = file.getName();
        // 文件类型
        String contentType = request.getSession().getServletContext().getMimeType(fileName);

        // 解决下载文件时文件名乱码问题
        byte[] fileNameBytes = fileName.getBytes(StandardCharsets.UTF_8);
        fileName = new String(fileNameBytes, 0, fileNameBytes.length, StandardCharsets.ISO_8859_1);

        response.setHeader("Content-Type", contentType);
        response.setHeader("Content-Length", String.valueOf(contentLength));
        //inline表示浏览器直接使用，attachment表示下载，fileName表示下载的文件名
        response.setHeader("Content-Disposition", "inline;filename=" + fileName);
        response.setContentType(contentType);
        if (range != null) {
            //各种响应头设置
            //支持断点续传，获取部分字节内容：
            response.setHeader("Accept-Ranges", "bytes");
            //http状态码要为206：表示获取部分内容
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            // Content-Range，格式为：[要下载的开始位置]-[结束位置]/[文件总大小]
            response.setHeader("Content-Range", "bytes " + startByte + "-" + endByte + "/" + file.length());
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
        }


        BufferedOutputStream outputStream = null;
        RandomAccessFile randomAccessFile = null;
        //已传送数据大小
        long transmitted = 0;
        try {
            randomAccessFile = new RandomAccessFile(file, "r");

            outputStream = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[4096];
            int len = 0;
            randomAccessFile.seek(startByte);
            //warning：判断是否到了最后不足4096（buff的length）个byte这个逻辑（(transmitted + len) <= contentLength）要放前面
            //不然会会先读取randomAccessFile，造成后面读取位置出错;
            while ((transmitted + len) <= contentLength && (len = randomAccessFile.read(buff)) != -1) {
                outputStream.write(buff, 0, len);
                transmitted += len;
            }
            //处理不足buff.length部分
            if (transmitted < contentLength) {
                len = randomAccessFile.read(buff, 0, (int) (contentLength - transmitted));
                outputStream.write(buff, 0, len);
                transmitted += len;
            }

            outputStream.flush();
            response.flushBuffer();
            randomAccessFile.close();
            logger.info("下载完毕：" + startByte + "-" + endByte + "：" + transmitted);
        } catch (ClientAbortException e) {
            logger.warn("用户停止下载：" + startByte + "-" + endByte + "：" + transmitted);
            //捕获此异常表示拥护停止下载
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("用户下载IO异常，Message：{}", e.getLocalizedMessage());
        } finally {
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @GetMapping(value = "/upload")
    @ResponseBody
    public AjaxResult upload(@RequestParam String resourcePath, HttpServletResponse response) {
        String record = userSettings.getRecord();
        File file = new File(record + resourcePath);
        if (!file.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return AjaxResult.error();
        }

        String originalfileName = file.getName();
        String suffix = StringUtils.substring(originalfileName, originalfileName.lastIndexOf("."), originalfileName.length());
        OssClient storage = OssFactory.instance(redisCache);
        UploadResult uploadResult = storage.uploadSuffix(file, suffix);
        // 保存文件信息
        return AjaxResult.success(buildResultEntity(originalfileName, suffix, storage.getConfigKey(), uploadResult));
    }

    private OssDetail buildResultEntity(String originalfileName, String suffix, String configKey, UploadResult uploadResult) {
        OssDetail oss = OssDetail.builder()
                .url(uploadResult.getUrl())
                .fileSuffix(suffix)
                .fileName(uploadResult.getFilename())
                .originalName(originalfileName)
                .service(configKey)
                .build();
        return this.matchingUrl(oss);
    }

    private OssDetail matchingUrl(OssDetail oss) {
        OssClient storage = OssFactory.instance(oss.getService(),redisCache);
        // 仅修改桶类型为 private 的URL，临时URL时长为120s
        if (AccessPolicyType.PRIVATE == storage.getAccessPolicy()) {
            oss.setUrl(storage.getPrivateUrl(oss.getFileName(), 120));
        }
        return oss;
    }

}


