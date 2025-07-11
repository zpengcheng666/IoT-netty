package com.sydh.rule.cmp.node;

import com.sydh.rule.cmp.data.DelayData;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;

import java.util.concurrent.TimeUnit;

@LiteflowComponent("delay")
public class DelayCmp  extends NodeComponent {
    @Override
    public void process() throws Exception {
        DelayData data = this.getCmpData(DelayData.class);
        if (data.getDelayType() == 1){
            TimeUnit.MILLISECONDS.sleep(data.getDelay());
        } else if(data.getDelayType() == 2){
            TimeUnit.SECONDS.sleep(data.getDelay());
        } else if (data.getDelayType() == 3) {
            TimeUnit.MINUTES.sleep(data.getDelay());
        } else if (data.getDelayType() == 4) {
            TimeUnit.HOURS.sleep(data.getDelay());
        } else if (data.getDelayType() == 5) {
            TimeUnit.DAYS.sleep(data.getDelay());
        }
    }
}
