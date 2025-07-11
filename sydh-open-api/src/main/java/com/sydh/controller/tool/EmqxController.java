package com.sydh.controller.tool;

import com.sydh.common.core.domain.AjaxResult;
import com.sydh.mqttclient.EmqxApiClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * emq Rest API调用测试
 * @author gsb
 * @date 2025/6/3 15:55
 */
@RestController
@RequestMapping("/api/emq")
public class EmqxController {


    @Resource
    private EmqxApiClient emqxApiClient;

    @GetMapping("/getInfo")
    public AjaxResult getInfo(String clientId){
        EmqxApiClient.ClientInfoDTO clientInfo = emqxApiClient.getClientInfo(clientId);
        return AjaxResult.success(clientInfo);
    }

    @GetMapping("/checkStatus")
    public AjaxResult checkStatus(String clientId){
        boolean deviceOnline = emqxApiClient.isDeviceOnline(clientId);
        return AjaxResult.success(deviceOnline);
    }

}
