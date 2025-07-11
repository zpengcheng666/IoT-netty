package com.sydh.mqttclient;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class EmqxApiClient {

    private static final int BATCH_QUERY_SIZE = 50;
    private static final String CLIENTS_ENDPOINT = "clients/";
    private static final String BATCH_QUERY_PARAM = "?clientid=";

    // ISO 8601 时间格式解析器
    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    private final RestTemplate restTemplate;
    private final String emqxApiBaseUrl;
    private final String apiKey;
    private final String apiSecret;
    private final ObjectMapper objectMapper;
    @Getter
    private final boolean enabled;

    private volatile String cachedAuthHeader;

    @Autowired
    public EmqxApiClient(
            @Value("${emqx.api.enable:false}") boolean enable,
            @Value("${emqx.api.base-url}") String baseUrl,
            @Value("${emqx.api.key}") String apiKey,
            @Value("${emqx.api.secret}") String apiSecret,
            RestTemplate restTemplate) {

        this.restTemplate = restTemplate;
        this.emqxApiBaseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;

        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        this.enabled = enable &&
                StringUtils.hasText(baseUrl) &&
                StringUtils.hasText(apiKey) &&
                StringUtils.hasText(apiSecret);

        if (this.enabled) {
            log.info("EMQX API 已启用，基础URL: {}", baseUrl);
        } else if (enable) {
            log.warn("EMQX API 配置不完整，功能已被禁用。请检查以下配置: "
                            + "emqx.api.base-url: {}, "
                            + "emqx.api.key: {}, "
                            + "emqx.api.secret: {}",
                    baseUrl, apiKey, apiSecret);
        } else {
            log.info("EMQX API 已通过配置显式禁用");
        }
    }

    private String getAuthHeader() {
        if (cachedAuthHeader == null) {
            synchronized (this) {
                if (cachedAuthHeader == null) {
                    String credentials = apiKey + ":" + apiSecret;
                    cachedAuthHeader = "Basic " + Base64Utils.encodeToString(
                            credentials.getBytes(StandardCharsets.UTF_8)
                    );
                }
            }
        }
        return cachedAuthHeader;
    }

    private HttpHeaders createHeaders() {
        List<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", getAuthHeader());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(acceptableMediaTypes);
        return headers;
    }

    public ClientInfoDTO getClientInfo(String clientId) {
        if (!StringUtils.hasText(clientId)) {
            throw new IllegalArgumentException("设备编号为空");
        }

        String url = emqxApiBaseUrl + CLIENTS_ENDPOINT + clientId;
        HttpEntity<String> entity = new HttpEntity<>(createHeaders());

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // 使用自定义ObjectMapper解析JSON
                return objectMapper.readValue(response.getBody(), ClientInfoDTO.class);
            }

            log.warn("EMQX API 非常规响应: {} - {}",
                    response.getStatusCodeValue(),
                    response.getBody());
            throw new EmqxApiException("API returned non-OK status: " + response.getStatusCode());

        } catch (Exception e) {
            log.error("EMQX API 调用失败: {}", e.getMessage());
            throw new EmqxApiException("EMQX API 调用失败", e);
        }
    }

    public boolean isDeviceOnline(String clientId) {
        if (!enabled){
            log.debug("EMQX API 未启用，信任原始设备状态流程");
            return true;
        }

        try {
            ClientInfoDTO clientInfo = getClientInfo(clientId);
            return clientInfo != null && clientInfo.isConnected();
        } catch (EmqxApiException e) {
            log.warn("设备 {} 状态检查降级处理: {}", clientId, e.getMessage());
            return false; // 降级为离线状态
        }
    }

    public Map<String, Boolean> batchCheckOnlineStatus(List<String> deviceIds) {
        if (deviceIds == null || deviceIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 去重处理
        List<String> distinctIds = new ArrayList<>(new HashSet<>(deviceIds));
        Map<String, Boolean> results = new ConcurrentHashMap<>();

        // 分批处理
        for (int i = 0; i < distinctIds.size(); i += BATCH_QUERY_SIZE) {
            List<String> batch = distinctIds.subList(i, Math.min(i + BATCH_QUERY_SIZE, distinctIds.size()));
            processBatch(batch, results);
        }

        return results;
    }

    private void processBatch(List<String> deviceIds, Map<String, Boolean> results) {
        String idsParam = String.join(",", deviceIds);
        String url = emqxApiBaseUrl + CLIENTS_ENDPOINT + BATCH_QUERY_PARAM + idsParam;

        try {
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // 解析JSON数组
                ClientInfoDTO[] clients = objectMapper.readValue(response.getBody(), ClientInfoDTO[].class);

                for (ClientInfoDTO client : clients) {
                    if (client != null && client.getClientid() != null) {
                        results.put(client.getClientid(), client.isConnected());
                    }
                }
            }
        } catch (Exception e) {
            log.error("批量查询失败: {}", e.getMessage());
            // 标记本批次所有设备为离线
            deviceIds.forEach(id -> results.put(id, false));
        }

        // 补充未返回的设备（可能不存在）
        deviceIds.forEach(id -> results.putIfAbsent(id, false));
    }

    // ================ DTO 和异常类 ================ //

    @Data
    public static class ClientInfoDTO {
        @JsonProperty("clientid")
        private String clientid;

        @JsonProperty("connected")
        private boolean connected;

        @JsonProperty("connected_at")
        private String connectedAtRaw;

        @JsonProperty("disconnected_at")
        private String disconnectedAtRaw;

        // 获取连接时间戳（毫秒）
        public Long getConnectedAtTimestamp() {
            return parseIsoToTimestamp(connectedAtRaw);
        }

        // 获取断开时间戳（毫秒）
        public Long getDisconnectedAtTimestamp() {
            return parseIsoToTimestamp(disconnectedAtRaw);
        }

        private Long parseIsoToTimestamp(String isoDateTime) {
            if (isoDateTime == null || isoDateTime.isEmpty()) {
                return null;
            }
            try {
                LocalDateTime dateTime = LocalDateTime.parse(isoDateTime, ISO_FORMATTER);
                return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            } catch (Exception e) {
                log.warn("时间解析失败: {}", isoDateTime);
                return null;
            }
        }
    }

    public static class EmqxApiException extends RuntimeException {
        public EmqxApiException(String message) { super(message); }
        public EmqxApiException(String message, Throwable cause) { super(message, cause); }
    }
}
