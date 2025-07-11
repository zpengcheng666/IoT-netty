package com.sydh.iot.data.ruleEngine;

import com.alibaba.fastjson2.JSONObject;
import com.sydh.common.exception.ServiceException;
import com.sydh.common.extend.core.domin.notify.AlertPushParams;
import com.sydh.common.extend.core.domin.thingsModel.SceneThingsModelItem;
import com.sydh.common.utils.DateUtils;
import com.sydh.common.utils.MessageUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.iot.data.service.IDataHandler;
import com.sydh.iot.domain.AlertLog;
import com.sydh.iot.domain.AlertNotifyTemplate;
import com.sydh.iot.domain.Device;
import com.sydh.iot.domain.DeviceAlertUser;
import com.sydh.iot.model.vo.AlertSceneSendVO;
import com.sydh.iot.model.vo.DeviceAlertUserVO;
import com.sydh.iot.model.vo.SceneTerminalUserVO;
import com.sydh.iot.service.*;
import com.sydh.notify.core.service.NotifySendService;
import com.sydh.rule.cmp.data.AlarmData;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@LiteflowComponent("alarm")
public class AlarmCmp extends NodeComponent {
    @Resource
    private IAlertService alertService;

    @Resource
    private IDeviceService deviceService;

    @Resource
    private IDataHandler dataHandler;

    @Resource
    private IAlertLogService alertLogService;

    @Resource
    private NotifySendService notifySendService;

    @Resource
    private ISceneService sceneService;

    @Resource
    private IDeviceAlertUserService deviceAlertUserService;


    @Override
    public void process() throws Exception {
        AlarmData data = this.getCmpData(AlarmData.class);
        SceneContext cxt = this.getContextBean(SceneContext.class);
        List<SceneThingsModelItem> sceneThingsModelItems = cxt.getSceneThingsModelItems();

        Long sceneId = 1L;
        if (Objects.isNull(sceneThingsModelItems)) {
            return;
        }
        Set<Long> sceneIdSet = sceneThingsModelItems.stream().map(SceneThingsModelItem::getSceneId).collect(Collectors.toSet());
        List<SceneTerminalUserVO> sceneTerminalUserVOList = sceneService.selectTerminalUserBySceneIds(sceneIdSet);
        Map<Long, SceneTerminalUserVO> sceneTerminalUserMap = sceneTerminalUserVOList.stream().collect(Collectors.toMap(SceneTerminalUserVO::getSceneId, Function.identity()));
        List<AlertLog> alertLogList = new ArrayList<>();
        for (SceneThingsModelItem sceneThingsModelItem : sceneThingsModelItems) {
            // 查询设备信息
            Device device = deviceService.selectDeviceBySerialNumber(sceneThingsModelItem.getDeviceNumber());
            Optional.ofNullable(device).orElseThrow(() -> new ServiceException(StringUtils.format(MessageUtils.message("alert.push.fail.device.not.exist"), sceneThingsModelItem.getDeviceNumber())));
            // 判断是否是终端用户的场景
            SceneTerminalUserVO sceneTerminalUserVO = sceneTerminalUserMap.get(sceneId);
            if (1 == sceneTerminalUserVO.getTerminalUser()) {
                AlertLog alertLog = this.getTerminalUserAlertLog(sceneTerminalUserVO, device, sceneThingsModelItem);
                alertLogList.add(alertLog);
                continue;
            }

            // 获取场景相关的告警参数，告警必须要是启动状态
            List<AlertSceneSendVO> sceneSendVOList = alertService.listByAlertIds(sceneId);
            if (CollectionUtils.isEmpty(sceneSendVOList)) {
                continue;
            }

            // 获取告警关联模版id
            for (AlertSceneSendVO alertSceneSendVO : sceneSendVOList) {
                AlertPushParams alertPushParams = this.buildAlertPushParams(device, sceneThingsModelItem);
                List<AlertNotifyTemplate> alertNotifyTemplateList = alertService.listAlertNotifyTemplate(alertSceneSendVO.getAlertId());
                alertPushParams.setAlertName(alertSceneSendVO.getAlertName());
                for (AlertNotifyTemplate alertNotifyTemplate : alertNotifyTemplateList) {
                    alertPushParams.setNotifyTemplateId(alertNotifyTemplate.getNotifyTemplateId());
                    String mqttMsg = notifySendService.alertSend(alertPushParams);
                    if (StringUtils.isNotEmpty(mqttMsg)) {
                        dataHandler.notifyAlertWeb(mqttMsg, alertPushParams.getUserIdSet());
                    }
                }
                AlertLog alertLog = this.getAlertLog(alertSceneSendVO, device, sceneThingsModelItem);
                alertLogList.add(alertLog);
            }
        }
        // 保存告警日志
        if (CollectionUtils.isNotEmpty(alertLogList)) {
            alertLogService.insertAlertLogBatch(alertLogList);
        }
    }

    private AlertLog getTerminalUserAlertLog(SceneTerminalUserVO sceneTerminalUserVO, Device device, SceneThingsModelItem sceneThingsModelItem) {
        AlertLog alertLog = new AlertLog();
        alertLog.setAlertName("设备告警");
        alertLog.setAlertLevel(1L);
        alertLog.setSerialNumber(sceneThingsModelItem.getDeviceNumber());
        alertLog.setProductId(sceneThingsModelItem.getProductId());
        alertLog.setDeviceName(device.getDeviceName());
        alertLog.setUserId(sceneTerminalUserVO.getUserId());
        // 统一未处理
        alertLog.setStatus(2);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", sceneThingsModelItem.getId());
        jsonObject.put("name", sceneThingsModelItem.getName());
        jsonObject.put("value", sceneThingsModelItem.getValue());
        jsonObject.put("remark", sceneThingsModelItem.getRemark());
        alertLog.setDetail(jsonObject.toJSONString());
        alertLog.setCreateTime(new Date());
        alertLog.setCreateBy(device.getCreateBy());
        alertLog.setSceneId(sceneThingsModelItem.getSceneId());
        return alertLog;
    }

    private AlertLog getAlertLog(AlertSceneSendVO alertSceneSendVO, Device device, SceneThingsModelItem sceneThingsModelItem) {
        AlertLog alertLog = new AlertLog();
        alertLog.setAlertName(alertSceneSendVO.getAlertName());
        alertLog.setAlertLevel(alertSceneSendVO.getAlertLevel());
        alertLog.setSerialNumber(sceneThingsModelItem.getDeviceNumber());
        alertLog.setProductId(sceneThingsModelItem.getProductId());
        alertLog.setDeviceName(device.getDeviceName());
        alertLog.setUserId(device.getTenantId());
        // 统一未处理
        alertLog.setStatus(2);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", sceneThingsModelItem.getId());
        jsonObject.put("name", sceneThingsModelItem.getName());
        jsonObject.put("value", sceneThingsModelItem.getValue());
        jsonObject.put("remark", sceneThingsModelItem.getRemark());
        alertLog.setDetail(jsonObject.toJSONString());
        alertLog.setCreateTime(new Date());
        alertLog.setCreateBy(device.getCreateBy());
        alertLog.setSceneId(sceneThingsModelItem.getSceneId());
        return alertLog;
    }

    private AlertPushParams buildAlertPushParams(Device device, SceneThingsModelItem sceneThingsModelItem) {
        // 获取告警推送参数
        AlertPushParams alertPushParams = new AlertPushParams();
        alertPushParams.setDeviceName(device.getDeviceName());
        alertPushParams.setSerialNumber(sceneThingsModelItem.getDeviceNumber());
        // 多租户改版查询自己配置的告警用户
        DeviceAlertUser deviceAlertUser = new DeviceAlertUser();
        deviceAlertUser.setDeviceId(device.getDeviceId());
        List<DeviceAlertUserVO> deviceAlertUserVOList = deviceAlertUserService.selectDeviceAlertUserList(deviceAlertUser).getRecords();
        if (CollectionUtils.isNotEmpty(deviceAlertUserVOList)) {
            alertPushParams.setUserPhoneSet(deviceAlertUserVOList.stream().map(DeviceAlertUserVO::getPhoneNumber).filter(StringUtils::isNotEmpty).collect(Collectors.toSet()));
            alertPushParams.setUserIdSet(deviceAlertUserVOList.stream().map(DeviceAlertUserVO::getUserId).collect(Collectors.toSet()));
        }
        String address;
        if (StringUtils.isNotEmpty(device.getNetworkAddress())) {
            address = device.getNetworkAddress();
        } else if (StringUtils.isNotEmpty(device.getNetworkIp())) {
            address = device.getNetworkIp();
        } else if (Objects.nonNull(device.getLongitude()) && Objects.nonNull(device.getLatitude())) {
            address = device.getLongitude() + "," + device.getLatitude();
        } else {
            address = "未知地点";
        }
        alertPushParams.setAddress(address);
        alertPushParams.setAlertTime(DateUtils.parseDateToStr(DateUtils.YY_MM_DD_HH_MM_SS, new Date()));
        alertPushParams.setValue(sceneThingsModelItem.getValue());
        alertPushParams.setMatchValue(sceneThingsModelItem.getMatchValue());
        return alertPushParams;
    }


}
