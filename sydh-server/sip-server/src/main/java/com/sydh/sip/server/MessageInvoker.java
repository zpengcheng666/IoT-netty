package com.sydh.sip.server;

import com.sydh.sip.domain.SipDevice;
import com.sydh.sip.server.msg.DeviceControl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.sip.RequestEvent;
import java.util.Date;

public interface MessageInvoker {

    /**
     * 发送订阅报警指令
     *
     * @param device             设备信息
     * @param startAlarmPriority 开始报警级别
     * @param endAlarmPriority   结束报警级别
     * @param expires            过期时间
     * @param alarmMethod        报警方式
     * @param startAlarmTime     开始报警时间
     * @param endAlarmTime       结束报警时间
     * @return void
     */
    void subscribe(@Nonnull SipDevice device,
                   int startAlarmPriority,
                   int endAlarmPriority,
                   int expires,
                   @Nonnull String alarmMethod,
                   @Nullable Date startAlarmTime,
                   @Nullable Date endAlarmTime);

    /**
     * 发送设备控制指令,{@link DeviceControl#getDeviceId()}为通道ID
     *
     * @param device  设备信息
     * @param command 控制指令
     */
    void deviceControl(SipDevice device, DeviceControl command);

    /**
     * 获取设备详细信息
     *
     * @param device 设备信息
     */
    void deviceInfoQuery(SipDevice device);

    /**
     * 获取通道列表
     *
     * @param device 设备信息
     * @return 通道列表
     */
    void catalogQuery(SipDevice device);

    void recordInfoQuery(SipDevice device, String sn, String channelId, Date start, Date end);

    SipMessage messageToObj(RequestEvent event);

    <T> T getExecResult(String key, long timeout);
}
