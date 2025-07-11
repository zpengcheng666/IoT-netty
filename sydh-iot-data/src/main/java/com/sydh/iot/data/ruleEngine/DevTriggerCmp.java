package com.sydh.iot.data.ruleEngine;

import cn.hutool.core.util.ObjectUtil;
import com.sydh.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.sydh.rule.cmp.data.DevTriggerData;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;

import java.util.List;
import java.util.Objects;

@LiteflowComponent("dev-trigger")
public class DevTriggerCmp  extends NodeComponent {

    @Override
    public boolean isAccess() {
        DevTriggerData data = this.getCmpData(DevTriggerData.class);
        SceneContext cxt = this.getContextBean(SceneContext.class);
        List<ThingsModelSimpleItem> thingsModelSimpleItems = cxt.getThingsModelSimpleItems();
        if (ObjectUtil.isEmpty(thingsModelSimpleItems)) {
            return false;
        }
        if (!Objects.equals(data.getDeviceId(), cxt.getDeviceNum())) {
            return false;
        }
        boolean find = false;
        for(ThingsModelSimpleItem thingsModelSimpleItem : thingsModelSimpleItems) {
            if(thingsModelSimpleItem.getId().equals(data.getModelId())) {
                find = true;
            }
        }
        return find;
    }

//    @Override
//    public boolean isEnd() {
//        Boolean isEnd = this.getRefNode().getIsEnd();
//        return ObjectUtil.isNull(isEnd) ? false : isEnd;
//    }

    @Override
    public void process() throws Exception {
        DevTriggerData data = this.getCmpData(DevTriggerData.class);

    }
}
