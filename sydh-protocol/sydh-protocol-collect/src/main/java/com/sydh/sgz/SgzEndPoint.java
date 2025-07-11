package com.sydh.sgz;

import cn.hutool.json.JSONObject;
import com.sydh.base.core.annotation.Node;
import com.sydh.base.core.annotation.PakMapping;
import com.sydh.base.session.Session;
import com.sydh.common.extend.core.domin.mq.DeviceReport;
import com.sydh.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author gsb
 * @date 2024/3/29 16:06
 */
@Node
@Component
public class SgzEndPoint {

    @PakMapping(types = 150)
    public DeviceReport heart(DeviceReport report, Session session){
        JSONObject params = new JSONObject();
        params.putIfAbsent("device",report.getClientId());
        List<ThingsModelSimpleItem> items = report.getThingsModelSimpleItem();
        for (ThingsModelSimpleItem item : items) {
            if (item.getId().equals("dtype")){
                params.putIfAbsent("dtype",item.getValue());
            }
        }
        params.putIfAbsent("fuc",SgzMessageType.LINKING.type);
        params.putIfAbsent("sdata",SgzMessageType.LINKING.type);
        report.setBody(params);
        return report;
    }

    @PakMapping(types = 154)
    public DeviceReport dataCallBack(DeviceReport report){
        JSONObject params = new JSONObject();
        params.putIfAbsent("device",report.getClientId());
        List<ThingsModelSimpleItem> items = report.getThingsModelSimpleItem();
        for (ThingsModelSimpleItem item : items) {
            if (item.getId().equals("dtype")){
                params.putIfAbsent("dtype",item.getValue());
            }
        }
        params.putIfAbsent("fuc",SgzMessageType.CZROK.type);
        params.putIfAbsent("sdata",SgzMessageType.CZROK.type);
        report.setBody(params);
        return report;
    }

}
