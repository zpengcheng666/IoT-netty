package com.sydh.controller.media;

import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.utils.MessageUtils;
import com.sydh.sip.service.IPlayService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/sip/player")
public class PlayerController extends BaseController {
    @Autowired
    private IPlayService playService;

    @GetMapping("/getBigScreenUrl/{deviceId}/{channelId}")
    public AjaxResult getBigScreenUrl(@PathVariable String deviceId, @PathVariable String channelId) {
        return AjaxResult.success(MessageUtils.message("success"), playService.play(deviceId, channelId,false).getHttps_fmp4());
    }

    @ApiOperation("直播播放")
    @GetMapping("/play/{deviceId}/{channelId}")
    public AjaxResult play(@PathVariable String deviceId, @PathVariable String channelId) {
        return AjaxResult.success(MessageUtils.message("success"), playService.play(deviceId, channelId,false));
    }

    @ApiOperation("通道截图 (延迟一会后，需closeStream停止推流)")
    @GetMapping("/screenshot/{deviceId}/{channelId}")
    public AjaxResult screenshot(@PathVariable String deviceId, @PathVariable String channelId) {
        return AjaxResult.success(MessageUtils.message("success"), playService.screenshot(deviceId, channelId));
    }

    @ApiOperation("回放播放")
    @GetMapping("/playback/{deviceId}/{channelId}")
    public AjaxResult playback(@PathVariable String deviceId,
                               @PathVariable String channelId, String start, String end) {
        return AjaxResult.success(MessageUtils.message("success"), playService.playback(deviceId, channelId, start, end));
    }
    @ApiOperation("停止推流")
    @GetMapping("/closeStream/{deviceId}/{channelId}/{streamId}")
    public AjaxResult closeStream(@PathVariable String deviceId, @PathVariable String channelId, @PathVariable String streamId) {
        return AjaxResult.success("success!", playService.closeStream(deviceId, channelId, streamId));
    }

    @ApiOperation("回放暂停")
    @GetMapping("/playbackPause/{deviceId}/{channelId}/{streamId}")
    public AjaxResult playbackPause(@PathVariable String deviceId, @PathVariable String channelId, @PathVariable String streamId) {
        return AjaxResult.success(MessageUtils.message("success"), playService.playbackPause(deviceId, channelId, streamId));
    }
    @ApiOperation("回放恢复")
    @GetMapping("/playbackReplay/{deviceId}/{channelId}/{streamId}")
    public AjaxResult playbackReplay(@PathVariable String deviceId, @PathVariable String channelId, @PathVariable String streamId) {
        return AjaxResult.success(MessageUtils.message("success"), playService.playbackReplay(deviceId, channelId, streamId));
    }
    @ApiOperation("录像回放定位")
    @GetMapping("/playbackSeek/{deviceId}/{channelId}/{streamId}")
    public AjaxResult playbackSeek(@PathVariable String deviceId, @PathVariable String channelId, @PathVariable String streamId, long seek) {
        return AjaxResult.success(MessageUtils.message("success"), playService.playbackSeek(deviceId, channelId, streamId, seek));
    }
    @ApiOperation("录像倍速播放")
    @GetMapping("/playbackSpeed/{deviceId}/{channelId}/{streamId}")
    public AjaxResult playbackSpeed(@PathVariable String deviceId, @PathVariable String channelId, @PathVariable String streamId, Integer speed) {
        return AjaxResult.success(MessageUtils.message("success"), playService.playbackSpeed(deviceId, channelId, streamId, speed));
    }
}
