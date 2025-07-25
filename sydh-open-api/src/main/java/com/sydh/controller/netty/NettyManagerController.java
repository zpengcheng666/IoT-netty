package com.sydh.controller.netty;

import com.sydh.base.service.ISessionStore;
import com.sydh.base.session.Session;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.enums.DeviceStatus;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.utils.MessageUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.common.utils.gateway.mq.Topics;
import com.sydh.mqtt.manager.ClientManager;
import com.sydh.mqtt.manager.SessionManger;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NETTY客户端可视化
 *
 * @author gsb
 * @date 2023/3/23 14:51
 */
@RestController
@RequestMapping("/iot/mqtt")
@Api(tags = "Netty可视化")
@Slf4j
public class NettyManagerController extends BaseController {

    @Resource
    private ISessionStore sessionStore;

    /**
     * 客户端管理列表
     * @param pageSize 分页大小
     * @param pageNum 页数
     * @param clientId  查询客户端id
     * @param serverCode 服务端类型编码  MQTT/TCP/UDP
     * @param isClient  是否只显示设备端 1-是/0或null-否
     * @return 客户端列表
     */
    @PreAuthorize("@ss.hasPermi('iot:emqx:client:list')")
    @GetMapping("/clients")
    @ApiOperation("netty客户端列表")
    public AjaxResult clients(Integer pageSize, Integer pageNum, String clientId, String serverCode,Integer isClient) {
        List<Session> list = new ArrayList<>();
        if (StringUtils.isEmpty(clientId)) {
            ConcurrentHashMap<String, Session> sourceMap = sessionStore.getSessionMap();
            ConcurrentHashMap<String, Session> selectMap = new ConcurrentHashMap<>();
            sourceMap.forEach((key, value) -> {
                if (serverCode.equals(value.getServerType().getCode())) {
                    if (null != isClient && isClient == 1){
                        if (!key.startsWith("server") && !key.startsWith("web") && !key.startsWith("phone") && !key.startsWith("test")){
                            selectMap.put(key,value);
                        }
                    }else {
                        selectMap.put(key, value);
                    }
                }
            });
            Map<String, Session> sessionMap = sessionStore.listPage(selectMap, pageSize, pageNum);
            List<Session> result = new ArrayList<>(sessionMap.values());
            for (Session session : result) {
                Map<String, Boolean> topicMap = ClientManager.clientTopicMap.get(session.getClientId());
                if (topicMap != null) {
                    List<Topics> topicsList = new ArrayList<>();
                    topicMap.keySet().forEach(topic -> {
                        Topics tBo = new Topics();
                        tBo.setTopicName(topic);
                        topicsList.add(tBo);
                    });
                    session.setTopics(topicsList);
                    session.setTopicCount(topicMap.size());
                } else {
                    session.setTopicCount(0);
                }
                list.add(session);
            }
            return AjaxResult.success(list, selectMap.size());
        } else {
            List<Session> result = new ArrayList<>();
            Session session = sessionStore.getSession(clientId);
            if (session != null) {
                Map<String, Boolean> topicMap = ClientManager.clientTopicMap.get(session.getClientId());
                if (topicMap != null) {
                    List<Topics> topicsList = new ArrayList<>();
                    topicMap.keySet().forEach(topic -> {
                        Topics tBo = new Topics();
                        tBo.setTopicName(topic);
                        topicsList.add(tBo);
                    });
                    session.setTopics(topicsList);
                    session.setTopicCount(topicMap.size());
                } else {
                    session.setTopicCount(0);
                }
                if (serverCode.equals(session.getServerType().getCode())) {
                    result.add(session);
                }
            }
            return AjaxResult.success(result, result.size());
        }

    }

    @PreAuthorize("@ss.hasPermi('iot:emqx:client:remove')")
    @GetMapping("/client/out")
    @ApiOperation("netty客户端踢出")
    public AjaxResult clientOut(String clientId, Integer status){
        Session session = sessionStore.getSession(clientId);
        Optional.ofNullable(session).orElseThrow(() -> new SecurityException(MessageUtils.message("netty.client.not.exists")));
        DeviceStatus deviceStatus = Objects.nonNull(status) &&  DeviceStatus.FORBIDDEN.getType() == status ? DeviceStatus.FORBIDDEN : DeviceStatus.OFFLINE;
        SessionManger.removeClient(clientId, deviceStatus);
        return AjaxResult.success();
    }

}
