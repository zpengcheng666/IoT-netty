package com.sydh.iot.data.job.strategy;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sydh.common.extend.core.domin.mq.InvokeReqDto;
import com.sydh.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.sydh.iot.data.service.IFunctionInvoke;
import com.sydh.iot.data.service.IMqttMessagePublish;
import com.sydh.iot.domain.DeviceJob;
import com.sydh.iot.model.Action;
import com.sydh.iot.ruleEngine.RuleProcess;
import com.sydh.mqttclient.PubMqttClient;
import com.sydh.rule.context.MsgContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author gsb
 * @date 2025/3/18 15:50
 */
@Component
public class SceneJobStrategy implements JobInvokeStrategy {

    @Resource
    private IFunctionInvoke functionInvoke;
    @Resource
    private IMqttMessagePublish messagePublish;

    @Resource
    private RuleProcess ruleProcess;

    @Resource
    private PubMqttClient mqttClient;

    @Override
    public boolean supports(int jobType) {
        return 1 == jobType;
    }

    @Override
    public void invoke(DeviceJob deviceJob) {
        System.out.println("------------------------执行定时任务-----------------------------");
        if ("MODBUS".equals(deviceJob.getJobGroup())) {
            List<Action> actions = JSON.parseArray(deviceJob.getActions(), Action.class);
            for (Action action : actions) {
                InvokeReqDto reqDto = new InvokeReqDto();
                reqDto.setProductId(deviceJob.getProductId());
                reqDto.setSerialNumber(deviceJob.getSerialNumber());
                reqDto.setModelName("");
                reqDto.setType(1);
                reqDto.setIdentifier(action.getId());
                Map<String, Object> params = new HashMap<>();
                params.put(action.getId(), action.getValue());
                reqDto.setRemoteCommand(params);
                reqDto.setParams(new JSONObject(reqDto.getRemoteCommand()));
                functionInvoke.invokeNoReply(reqDto);
            }
        } else {
            List<Action> actions = JSON.parseArray(deviceJob.getActions(), Action.class);
            List<ThingsModelSimpleItem> propertys = new ArrayList<>();
            List<ThingsModelSimpleItem> functions = new ArrayList<>();
            for (int i = 0; i < actions.size(); i++) {
                ThingsModelSimpleItem model = new ThingsModelSimpleItem();
                model.setId(actions.get(i).getId());
                model.setValue(actions.get(i).getValue());
                model.setRemark("设备定时");
                if (actions.get(i).getType() == 1) {
                    propertys.add(model);
                } else if (actions.get(i).getType() == 2) {
                    functions.add(model);
                }
            }
            String proTopic = deviceJob.getProductId() + "/" + deviceJob.getSerialNumber() + "/property/get";
            // 发布属性
            if (propertys.size() > 0) {
                // 调用 RuleProcess 处理规则脚本
                MsgContext msgContext = ruleProcess.processRuleScript(
                        deviceJob.getSerialNumber(),
                        2,
                        proTopic,
                        JSON.toJSONString(propertys)
                );

                // 使用转换后的内容发布属性
                if (!Objects.isNull(msgContext)) {
                    mqttClient.publish(msgContext.getPayload().getBytes(),msgContext.getTopic(),  false,  1);
                }else {
                    messagePublish.publishProperty(deviceJob.getProductId(), deviceJob.getSerialNumber(), propertys, 0);
                }

            }

            // 发布功能
            String funTopic = deviceJob.getProductId() + "/" + deviceJob.getSerialNumber() + "/function/get";
            if (functions.size() > 0) {
                // 调用 RuleProcess 处理规则脚本
                MsgContext msgContext = ruleProcess.processRuleScript(
                        deviceJob.getSerialNumber(),
                        2,
                        funTopic,
                        JSON.toJSONString(functions)
                );
                if (!Objects.isNull(msgContext)) {
                    mqttClient.publish(msgContext.getPayload().getBytes(),msgContext.getTopic(),  false,  1);
                }else {
                    messagePublish.publishFunction(deviceJob.getProductId(), deviceJob.getSerialNumber(), functions, 0);
                }
            }
//            // 发布属性
//            if (propertys.size() > 0) {
//                messagePublish.publishProperty(deviceJob.getProductId(), deviceJob.getSerialNumber(), propertys, 0);
//            }
//            // 发布功能
//            if (functions.size() > 0) {
//                messagePublish.publishFunction(deviceJob.getProductId(), deviceJob.getSerialNumber(), functions, 0);
//            }
        }
    }
}
