package com.sydh.mqttclient;


import com.sydh.common.extend.core.domin.mq.DeviceReportBo;

/**
 * @author bill
 */
public interface IEmqxMessageProducer {

    public void sendEmqxMessage(String topicName, DeviceReportBo deviceReportBo);


}
