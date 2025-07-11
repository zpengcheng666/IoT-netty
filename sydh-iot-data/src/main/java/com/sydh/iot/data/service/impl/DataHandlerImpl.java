package com.sydh.iot.data.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sydh.common.enums.DeviceLogTypeEnum;
import com.sydh.common.enums.FunctionReplyStatus;
import com.sydh.common.enums.TopicType;
import com.sydh.common.exception.ServiceException;
import com.sydh.common.extend.core.domin.mq.InvokeReqDto;
import com.sydh.common.extend.core.domin.mq.message.ReportDataBo;
import com.sydh.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.sydh.common.extend.core.domin.thingsModel.ThingsModelValuesInput;
import com.sydh.common.extend.enums.scenemodel.SceneModelTagOpreationEnum;
import com.sydh.common.utils.CaculateVariableAndNumberUtils;
import com.sydh.common.utils.DateUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.common.utils.date.LocalDateTimeUtils;
import com.sydh.common.utils.gateway.mq.TopicsUtils;
import com.sydh.iot.cache.SceneModelTagCache;
import com.sydh.iot.data.service.IDataHandler;
import com.sydh.iot.data.service.IMqttMessagePublish;
import com.sydh.iot.data.service.IRuleEngine;
import com.sydh.iot.domain.*;

import com.sydh.iot.mapper.SceneTagPointsMapper;
import com.sydh.iot.model.scenemodel.SceneModelTagCacheVO;
import com.sydh.iot.model.scenemodel.SceneModelTagCycleVO;
import com.sydh.iot.model.vo.DeviceVO;
import com.sydh.iot.model.vo.SceneModelTagVO;
import com.sydh.iot.service.*;
import com.sydh.iot.tsdb.service.ILogService;
import com.sydh.mqtt.manager.MqttRemoteManager;
import com.sydh.mqtt.model.PushMessageBo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 上报数据处理方法集合
 * @author bill
 */
@Service
@Slf4j
public class DataHandlerImpl implements IDataHandler {


    @Resource
    private IDeviceService deviceService;
    @Resource
    private IMqttMessagePublish messagePublish;
    @Resource
    private IRuleEngine ruleEngine;
    @Resource
    private MqttRemoteManager remoteManager;
    @Resource
    private TopicsUtils topicsUtils;

    @Resource
    private SceneTagPointsMapper sceneTagPointsMapper;

    @Resource
    private SceneModelTagCache sceneModelTagCache;

    @Resource
    private ILogService logService;

    @Resource
    private ISceneModelTagService sceneModelTagService;

    @Resource
    private IFunctionLogService functionLogService;

    /**
     * 上报属性或功能处理
     *
     * @param bo 上报数据模型
     */
    @Override
    public void reportData(ReportDataBo bo) {
        try {
            List<ThingsModelSimpleItem> thingsModelSimpleItems = bo.getDataList();
            if (CollectionUtils.isEmpty(bo.getDataList()) || bo.getDataList().size() == 0) {
                thingsModelSimpleItems = JSON.parseArray(bo.getMessage(), ThingsModelSimpleItem.class);
            }
            if (CollectionUtils.isEmpty(thingsModelSimpleItems)) return;
            ThingsModelValuesInput input = new ThingsModelValuesInput();
            input.setProductId(bo.getProductId());
            // 这里上报设备编号是转的大写，后面存缓存也是使用大写的，所以在查询物模型的值时添加把设备编号转大写后取值
            input.setDeviceNumber(bo.getSerialNumber().toUpperCase());
            input.setThingsModelValueRemarkItem(thingsModelSimpleItems);
            List<ThingsModelSimpleItem> result = deviceService.reportDeviceThingsModelValue(input, bo.getType(), bo.isShadow());
            // 只有设备上报进入规则引擎流程
            if (bo.isRuleEngine() && !bo.getSerialNumber().startsWith("server-")){
                ruleEngine.ruleMatch(bo);
            }
            //发送至前端
            PushMessageBo messageBo = new PushMessageBo();
            messageBo.setTopic(topicsUtils.buildTopic(bo.getProductId(), bo.getSerialNumber(), TopicType.WS_SERVICE_INVOKE));
            JSONObject pushObj = new JSONObject();
            pushObj.put("message", result);
            pushObj.put("sources",bo.getSources());
            messageBo.setMessage(JSON.toJSONString(pushObj));
            remoteManager.pushCommon(messageBo);
            if (!Objects.isNull(bo.getGwDeviceBo())){
                messageBo.setTopic(topicsUtils.buildTopic(bo.getGwDeviceBo().getProductId(),bo.getGwDeviceBo().getSerialNumber(),TopicType.WS_SERVICE_INVOKE));
                remoteManager.pushCommon(messageBo);
            }
            // 上报属性给小度音箱，接入小度音箱后可放开
//            try {
//                List<String> identifierList = thingsModelSimpleItems.stream().map(ThingsModelSimpleItem::getId).collect(Collectors.toList());
//                speakerService.reportDuerosAttribute(bo.getSerialNumber(), identifierList);
//            } catch (Exception e) {
//                log.error("=>上报属性信息给小度音箱异常", e);
//            }
        } catch (Exception e) {
            log.error("接收属性数据，解析数据时异常 message={},e={}", e.getMessage(),e);
        }
    }


    /**
     * 上报事件
     *
     * @param bo 上报数据模型
     */
    @Override
    public void reportEvent(ReportDataBo bo) {
        try {
            List<ThingsModelSimpleItem> thingsModelSimpleItems = JSON.parseArray(bo.getMessage(), ThingsModelSimpleItem.class);
            Device device = deviceService.selectDeviceBySerialNumber(bo.getSerialNumber());
            List<DeviceLog> results = new ArrayList<>();
            for (int i = 0; i < thingsModelSimpleItems.size(); i++) {
                DeviceLog deviceLog = new DeviceLog();
                deviceLog.setDeviceId(device.getDeviceId());
                deviceLog.setDeviceName(device.getDeviceName());
                deviceLog.setLogValue(thingsModelSimpleItems.get(i).getValue());
                deviceLog.setRemark(thingsModelSimpleItems.get(i).getRemark());
                deviceLog.setSerialNumber(device.getSerialNumber());
                deviceLog.setIdentify(thingsModelSimpleItems.get(i).getId());
                deviceLog.setLogType(3);
                deviceLog.setIsMonitor(0);
                deviceLog.setUserId(device.getTenantId());
                deviceLog.setUserName(device.getTenantName());
                deviceLog.setTenantId(device.getTenantId());
                deviceLog.setTenantName(device.getTenantName());
                deviceLog.setCreateBy(device.getCreateBy());
                deviceLog.setCreateTime(DateUtils.getNowDate());
                // 1=影子模式，2=在线模式，3=其他
                deviceLog.setMode(2);
                results.add(deviceLog);
            }

            for (DeviceLog deviceLog : results) {
                logService.saveDeviceLog(deviceLog);
            }

            if (bo.isRuleEngine()){
                ruleEngine.ruleMatch(bo);
            }
        } catch (Exception e) {
            log.error("接收事件，解析数据时异常 message={}", e.getMessage());
        }
    }

    /**
     * 上报设备信息
     */
    @Override
    public void reportDevice(ReportDataBo bo) {
        try {
            // 设备实体
            Device deviceEntity = deviceService.selectDeviceBySerialNumber(bo.getSerialNumber());
            if(deviceEntity != null){
                // 上报设备信息
                DeviceVO deviceVO = JSON.parseObject(bo.getMessage(), DeviceVO.class);
                deviceVO.setProductId(bo.getProductId());
                deviceVO.setSerialNumber(bo.getSerialNumber());
                deviceService.reportDevice(deviceVO, deviceEntity);
                // 发布设备状态
                messagePublish.publishStatus(bo.getProductId(), bo.getSerialNumber(), 3, deviceEntity.getIsShadow(), deviceVO.getRssi());
            }

        } catch (Exception e) {
            log.error("接收设备信息，解析数据时异常 message={}", e.getMessage());
            throw new ServiceException(e.getMessage(), 1);
        }
    }

    @Override
    public String calculateSceneModelTagValue(Long id) {
        LocalDateTime now = LocalDateTime.now();
        SceneModelTagVO sceneModelTagVO = sceneModelTagService.selectSceneModelTagAndTenantById(id);
        if (null == sceneModelTagVO) {
            return "场景运算型变量计算错误：变量为空";
        }
        if ((StringUtils.isEmpty(sceneModelTagVO.getAliasFormule()) && org.apache.commons.collections4.CollectionUtils.isEmpty(sceneModelTagVO.getTagPointsVOList()))) {
            return "场景运算型变量计算错误：没有计算公式";
        }
        String checkMsg = sceneModelTagService.checkAliasFormule(sceneModelTagVO);
        if (StringUtils.isNotEmpty(checkMsg)) {
            return "场景运算型变量计算错误：" + checkMsg;
        }
        LambdaQueryWrapper<SceneTagPoints> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SceneTagPoints::getTagId, sceneModelTagVO.getId());
        List<SceneTagPoints> sceneTagPointsList = sceneTagPointsMapper.selectList(queryWrapper);
        Map<String, String> replaceMap = new HashMap<>(2);
        // 计算周期
        SceneModelTagCycleVO sceneModelTagCycleVO = new SceneModelTagCycleVO();
        boolean b = sceneTagPointsList.stream().anyMatch(s -> !SceneModelTagOpreationEnum.ORIGINAL_VALUE.getCode().equals(s.getOperation()));
        if (b) {
            sceneModelTagCycleVO = sceneModelTagService.handleTimeCycle(sceneModelTagVO.getCycleType(), sceneModelTagVO.getCycle(), now);
        }
        // 需不需要判断每个变量启用了没有 todo
        for (SceneTagPoints sceneTagPoints : sceneTagPointsList) {
            String value;
            value = sceneModelTagService.getSceneModelDataValue(sceneTagPoints, sceneModelTagCycleVO);
            // value没值先兜底0
            if (StringUtils.isEmpty(value)) {
                value = "0";
            }
            replaceMap.put(sceneTagPoints.getAlias(), value);
        }
        BigDecimal execute = CaculateVariableAndNumberUtils.execute(sceneModelTagVO.getAliasFormule(), replaceMap);
        String resultValue = execute.toPlainString();
        this.saveSceneModelTagValue(sceneModelTagVO, resultValue, now);
        return resultValue;
    }

    @Override
    public void invokeSceneModelTagValue(InvokeReqDto reqDto, String messageId) {
        LocalDateTime now = LocalDateTime.now();
        String sceneModelTagId = reqDto.getIdentifier();
        Map<String, Object> remoteCommand = reqDto.getRemoteCommand();
        String value = remoteCommand.get(sceneModelTagId).toString();
        FunctionLog functionLog = new FunctionLog();
        functionLog.setIdentify(reqDto.getIdentifier());
        functionLog.setFunType(4);
        functionLog.setFunValue(value);
        functionLog.setMessageId(messageId);
        functionLog.setSerialNumber(reqDto.getSceneModelId().toString());
        functionLog.setMode(3);
        functionLog.setModelName(reqDto.getModelName());
        SceneModelTagVO sceneModelTagVO = sceneModelTagService.selectSceneModelTagAndTenantById(Long.valueOf(sceneModelTagId));
        if (null == sceneModelTagVO) {
            functionLog.setResultCode(201);
            functionLog.setResultMsg("指令执行失败");
            functionLogService.insertFunctionLog(functionLog);
            return;
        }
        functionLog.setUserId(sceneModelTagVO.getTenantId());
        functionLog.setCreateBy(sceneModelTagVO.getCreateBy());
        this.saveSceneModelTagValue(sceneModelTagVO, value, now);
        functionLog.setResultCode(FunctionReplyStatus.SUCCESS.getCode());
        functionLog.setResultMsg(FunctionReplyStatus.SUCCESS.getMessage());
        functionLogService.insertFunctionLog(functionLog);
    }

    @Override
    public void notifyAlertWeb(String mqttMsg, Set<Long> userIdSet) {
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(userIdSet)) {
            for (Long userId : userIdSet) {
                //发送至前端
                PushMessageBo messageBo = new PushMessageBo();
                // /场景id/变量id/scene/report（对应变量标识）
                messageBo.setTopic("/notify/alert/web/push/" + userId);
                messageBo.setMessage(mqttMsg);
                remoteManager.pushCommon(messageBo);
            }
        }
    }

    /**
     * 保存场景变量值
     * @param sceneModelTagVO 变量类
     * @param: value 值
     * @param: now 执行时间
     * @return void
     */
    private void saveSceneModelTagValue(SceneModelTagVO sceneModelTagVO, String value, LocalDateTime now) {
        // 保存运算型变量值,存缓存
        SceneModelTagCacheVO sceneModelTagCacheVO = new SceneModelTagCacheVO();
        sceneModelTagCacheVO.setId(sceneModelTagVO.getId().toString());
        sceneModelTagCacheVO.setTs(LocalDateTimeUtils.localDateTimeToStr(now, LocalDateTimeUtils.YYYY_MM_DD_HH_MM_SS));
        sceneModelTagCacheVO.setValue(value);
        sceneModelTagCache.addSceneModelTagValue(sceneModelTagVO.getSceneModelId(), sceneModelTagCacheVO);
        // 是否历史存储
        if (1 == sceneModelTagVO.getStorage()) {
            DeviceLog deviceLog = new DeviceLog();
            deviceLog.setIdentify(sceneModelTagVO.getId().toString());
            deviceLog.setModelName(sceneModelTagVO.getName());
            deviceLog.setLogType(DeviceLogTypeEnum.SCENE_VARIABLE_REPORT.getType());
            deviceLog.setLogValue(value);
            deviceLog.setIsMonitor(0);
            deviceLog.setMode(3);
            deviceLog.setCreateTime(new Date());
            deviceLog.setCreateBy(sceneModelTagVO.getCreateBy());
            deviceLog.setTenantId(sceneModelTagVO.getTenantId());
            deviceLog.setUserId(sceneModelTagVO.getTenantId());
            logService.saveDeviceLog(deviceLog);
        }
        //发送至前端
        List<SceneModelTagCacheVO> sendMsg = new ArrayList<>();
        sendMsg.add(sceneModelTagCacheVO);
        PushMessageBo messageBo = new PushMessageBo();
        // /场景id/变量id/scene/report（对应变量标识）
        messageBo.setTopic(TopicsUtils.buildSceneReportTopic(sceneModelTagVO.getSceneModelId(), sceneModelTagVO.getSceneModelDeviceId()));
        messageBo.setMessage(JSON.toJSONString(sendMsg));
        remoteManager.pushCommon(messageBo);
    }


}
