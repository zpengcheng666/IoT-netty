package com.sydh.controller.dashBoard;

import com.sydh.common.constant.SYDHConstant;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.redis.RedisCache;
import com.sydh.iot.model.dashBoard.DashMqttMetrics;
import com.sydh.iot.model.dashBoard.DashMqttStat;
import com.sydh.iot.model.dashBoard.DeviceMatchBo;
import com.sydh.iot.service.IDashBoardService;
import com.sydh.mqtt.manager.ClientManager;
import com.sydh.mqtt.manager.RetainMsgManager;
import com.sydh.base.service.ISessionStore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 首页面板和大屏数据
 *
 * @author gsb
 * @date 2023/3/27 17:00
 */
@Api(tags = "首页面板和大屏数据")
@RestController
@RequestMapping("/bashBoard")
public class DashBoardController {

    @Resource
    private RedisCache redisCache;
    @Resource
    private ISessionStore sessionStore;
    @Resource
    private IDashBoardService dashBoardService;

    @GetMapping("/stats")
    @ApiOperation("mqtt状态数据")
    public AjaxResult stats() {
        DashMqttStat stat = DashMqttStat.builder()
                .connection_count(sessionStore.getSessionMap().size())
                .connection_total(getTotal(SYDHConstant.REDIS.MESSAGE_CONNECT_TOTAL))
                .subscription_count(ClientManager.topicMap.size())
                .subscription_total(getTotal(SYDHConstant.REDIS.MESSAGE_SUBSCRIBE_TOTAL))
                .retain_count(RetainMsgManager.getSize())
                .retain_total(getTotal(SYDHConstant.REDIS.MESSAGE_RETAIN_TOTAL))
                .session_count(sessionStore.getSessionMap().size())
                .session_total(getTotal(SYDHConstant.REDIS.MESSAGE_CONNECT_TOTAL))
                .build();
        return AjaxResult.success(stat);

    }


    @GetMapping("/metrics")
    @ApiOperation("mqtt统计")
    public AjaxResult metrics() {
        DashMqttMetrics metrics = DashMqttMetrics.builder()
                .send_total(getTotal(SYDHConstant.REDIS.MESSAGE_SEND_TOTAL))
                .receive_total(getTotal(SYDHConstant.REDIS.MESSAGE_RECEIVE_TOTAL))
                .auth_total(getTotal(SYDHConstant.REDIS.MESSAGE_AUTH_TOTAL))
                .connect_total(getTotal(SYDHConstant.REDIS.MESSAGE_CONNECT_TOTAL))
                .subscribe_total(getTotal(SYDHConstant.REDIS.MESSAGE_SUBSCRIBE_TOTAL))
                .today_received(getTotal(SYDHConstant.REDIS.MESSAGE_RECEIVE_TODAY))
                .today_send(getTotal(SYDHConstant.REDIS.MESSAGE_SEND_TODAY))
                .build();
        return AjaxResult.success(metrics);
    }

    @GetMapping("/matchsData")
    @ApiOperation("大屏匹配数据使用")
    public AjaxResult matchData(DeviceMatchBo bo){
        return dashBoardService.dashCombine(bo);
    }


    public Integer getTotal(String key) {
        return redisCache.getCacheObject(key) == null ? 0 : redisCache.getCacheObject(key);
    }
}
