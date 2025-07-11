package com.sydh.iot.service;

import com.sydh.http.model.HttpClientConfig;
import com.sydh.iot.domain.HttpClient;

/**
 * http桥接配置Service接口
 *
 * @author gx_magx_ma
 * @date 2024-06-03
 */
public interface IHttpClientService
{

    /**
     * 构建httpclient配置
     * @param httpClient
     * @return
     */
    public HttpClientConfig buildhttpclientconfig(HttpClient httpClient);
}
