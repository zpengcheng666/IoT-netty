package com.sydh.iot.ruleEngine;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.dtflys.forest.http.ForestRequest;
import com.sydh.http.service.HttpClientFactory;
import com.sydh.iot.domain.Bridge;
import com.sydh.iot.domain.HttpClient;
import com.sydh.iot.service.IBridgeService;
import com.sydh.iot.service.IHttpClientService;
import com.sydh.rule.context.MsgContext;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.hikvision.artemis.sdk.config.ArtemisConfig;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.util.Map;

@LiteflowComponent("httpBridge")
public class HttpBridge extends NodeComponent {

    @Autowired
    private IHttpClientService httpClientService;
    @Autowired
    private IBridgeService bridgeService;

    @Override
    public void process() throws Exception {
        // 获取上下文对象
        MsgContext cxt = this.getContextBean(MsgContext.class);
        Integer id = null;
        Boolean httpPull = null;
        try {
            id = cxt.getData("httpBridgeID");
            httpPull = cxt.getData("httpPull");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bridge bridge = bridgeService.queryByIdWithCache(Long.valueOf(id));
        if (bridge != null && "1".equals(bridge.getEnable())) {
            HttpClient config = JSON.parseObject(bridge.getConfigJson(), HttpClient.class);
            if (config.getRequestConfig() == null || config.getRequestConfig().isEmpty()) {
                ForestRequest request = HttpClientFactory.instance(httpClientService.buildhttpclientconfig(config));
                if (config.getRequestBody() != null && !config.getRequestBody().isEmpty()) {
                    request.addBody(cxt.placeholders(config.getRequestBody()));
                } else {
                    request.addBody("payload", "text/plian", cxt.getPayload());
                }
                Boolean finalHttpPull = httpPull;
                request.onSuccess(((data, req, res) -> {
                    if (finalHttpPull != null && finalHttpPull) {
                        cxt.setPayload(res.getContent());
                    }
                })).execute();
            } else {
                //海康平台获取直播流接口
                String hostUrl = config.getHostUrl();
                URI uri = new URI(hostUrl);
                String host = uri.getHost();
                int port = uri.getPort();
                String urlPath = uri.getPath();
                // 构建请求path
                Map<String, String> path = MapUtil.builder("https://", urlPath).build();
                ArtemisConfig artemisConfig = new ArtemisConfig();
                JSONObject configJson = JSON.parseObject(config.getRequestConfig());
                String appKey = (String) configJson.get("appKey");
                String appSecret = (String) configJson.get("appSecret");
                artemisConfig.setHost(host + ":" + port);
                artemisConfig.setAppKey(appKey);
                artemisConfig.setAppSecret(appSecret);
                JSONObject querysJson = JSON.parseObject(config.getRequestQuerys());
                ArtemisHttpUtil.doPostStringArtemis(path, querysJson.toString(), null, null, "application/json");
            }
        }
    }
}
