package com.sydh.http.service;

import com.dtflys.forest.Forest;
import com.dtflys.forest.http.ForestRequest;
import com.sydh.http.model.HttpClientConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpClientFactory {
    public static ForestRequest instance(HttpClientConfig config) {
        ForestRequest request = Forest.request();
        if (config.getQuerys() != null) {
            request.addQuery(config.getQuerys());
        }
        if (config.getHeaders() != null) {
            request.addHeader(config.getHeaders());
        }
        if (config.getBody() != null) {
            request.addBody(config.getBody());
        }
        return request.url(config.getUrl())
                .type(config.getMethod())
                .backend(config.getBackend());
    }
}
