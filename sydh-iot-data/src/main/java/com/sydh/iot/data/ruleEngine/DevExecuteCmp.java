package com.sydh.iot.data.ruleEngine;

import com.alibaba.fastjson2.JSONObject;
import com.sydh.common.extend.core.domin.mq.InvokeReqDto;
import com.sydh.iot.data.service.IFunctionInvoke;
import com.sydh.rule.cmp.data.DevExecuteCData;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@LiteflowComponent("dev-execute")
public class DevExecuteCmp  extends NodeComponent {
    @Resource
    private IFunctionInvoke functionInvoke;

    @Override
    public void process() throws Exception {
        DevExecuteCData data = this.getCmpData(DevExecuteCData.class);
        InvokeReqDto reqDto = new InvokeReqDto();
        //reqDto.setProductId(scriptTemplate.getProductId());
        reqDto.setSerialNumber(data.getDeviceId());
        reqDto.setModelName("");
        reqDto.setType(1);
        reqDto.setIdentifier(data.getModelId());
        Map<String, Object> params = new HashMap<>();
        params.put(data.getModelId(), data.getValue());
        reqDto.setRemoteCommand(params);
        reqDto.setParams(new JSONObject(reqDto.getRemoteCommand()));
        functionInvoke.invokeNoReply(reqDto);
    }
}
