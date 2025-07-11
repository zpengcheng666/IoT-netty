package com.sydh.controller.media;

import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.utils.MessageUtils;
import com.sydh.sip.service.ITalkService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/sip/talk")
public class TalkController extends BaseController {
    @Autowired
    private ITalkService talkService;

    @ApiOperation("获取推流地址")
    @GetMapping("/getPushUrl/{deviceId}/{channelId}")
    public AjaxResult getPushUrl(@PathVariable String deviceId, @PathVariable String channelId) {
        return AjaxResult.success(MessageUtils.message("success"),talkService.getBroadcastUrl(deviceId, channelId));
    }
    @ApiOperation("语音广播")
    @GetMapping("/broadcast/{deviceId}/{channelId}")
    public AjaxResult broadcast(@PathVariable String deviceId, @PathVariable String channelId) {
        return AjaxResult.success(MessageUtils.message("success"),talkService.broadcast(deviceId, channelId));
    }

    @ApiOperation("停止语音广播")
    @GetMapping("/broadcast/stop/{deviceId}/{channelId}")
    public AjaxResult broadcastStop(@PathVariable String deviceId, @PathVariable String channelId) {
        return AjaxResult.success(MessageUtils.message("success"), talkService.broadcastStop(deviceId, channelId));
    }
}
