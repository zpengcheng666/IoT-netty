package com.sydh.sip.handler.req;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONNull;
import cn.hutool.json.JSONUtil;
import com.sydh.common.utils.DateUtils;
import com.sydh.iot.domain.Device;
import com.sydh.iot.service.IDeviceService;
import com.sydh.sip.conf.SysSipConfig;
import com.sydh.sip.domain.SipConfig;
import com.sydh.sip.domain.SipDevice;
import com.sydh.sip.domain.SipDeviceChannel;
import com.sydh.sip.handler.IReqHandler;
import com.sydh.sip.model.SipDate;
import com.sydh.sip.server.IGBListener;
import com.sydh.sip.server.MessageInvoker;
import com.sydh.sip.server.SIPSender;
import com.sydh.sip.service.IMqttService;
import com.sydh.sip.service.ISipConfigService;
import com.sydh.sip.service.ISipDeviceChannelService;
import com.sydh.sip.service.ISipDeviceService;
import com.sydh.sip.util.DigestAuthUtil;
import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.header.Expires;
import gov.nist.javax.sip.header.SIPDateHeader;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.header.*;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
public class RegisterReqHandler extends ReqAbstractHandler implements InitializingBean, IReqHandler {

    @Autowired
    private ISipDeviceService sipDeviceService;
    @Autowired
    private ISipDeviceChannelService sipDeviceChannelService;
    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private ISipConfigService sipConfigService;

    @Autowired
    private IGBListener sipListener;

    @Autowired
    private MessageInvoker messageInvoker;

    @Autowired
    private SysSipConfig sysSipConfig;

    @Autowired
    private IMqttService mqttService;

    @Autowired
    private SIPSender sipSender;

    @Override
    public void processMsg(RequestEvent evt) {
        try {
            log.info("[sip register]收到注册请求，开始处理");
            Request request = evt.getRequest();
            SIPRequest Sipreq = (SIPRequest)evt.getRequest();
            log.info("[sip register]request:{}", JSONUtil.toJsonStr(request));
            log.info("[sip register]Sipreq:{}", JSONUtil.toJsonStr(Sipreq));
            Response response;
            // 注册标志  0：未携带授权头或者密码错误  1：注册成功   2：注销成功
            int registerFlag;
            FromHeader fromHeader = (FromHeader) request.getHeader(FromHeader.NAME);
            AddressImpl address = (AddressImpl) fromHeader.getAddress();
            SipUri uri = (SipUri) address.getURI();
            String sipId = uri.getUser();
            //取默认Sip配置
            SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(sipId);
            if (sipConfig != null) {
                AuthorizationHeader authorhead = (AuthorizationHeader) request.getHeader(AuthorizationHeader.NAME);
                // 校验密码是否正确
                if (authorhead == null && !ObjectUtils.isEmpty(sipConfig.getPassword())) {
                    log.info("未携带授权头 回复401，sipId："+ sipId);
                    response = getMessageFactory().createResponse(Response.UNAUTHORIZED, request);
                    new DigestAuthUtil().generateChallenge(getHeaderFactory(), response, sipConfig.getDomainAlias());
                    sipSender.transmitRequest(Sipreq.getLocalAddress().getHostAddress(), response);
                    return;
                }

                boolean pcheck = new DigestAuthUtil().doAuthenticatePlainTextPassword(request,
                        sipConfig.getPassword());

                boolean syscheck = new DigestAuthUtil().doAuthenticatePlainTextPassword(request,
                        sysSipConfig.getPassword());
                log.info("[sip register]pcheck:{}, syscheck:{}", pcheck, syscheck);
                if (!pcheck && !syscheck) {
                    // 注册失败
                    response = getMessageFactory().createResponse(Response.FORBIDDEN, request);
                    response.setReasonPhrase("wrong password");
                    log.info("[注册请求] 密码/SIP服务器ID错误, 回复403 sipId：" + sipId);
                    sipSender.transmitRequest(Sipreq.getLocalAddress().getHostAddress(), response);
                    return;
                }
                response = getMessageFactory().createResponse(Response.OK, request);
                // 添加date头
                SIPDateHeader dateHeader = new SIPDateHeader();
                // 使用自己修改的
                SipDate sipDate = new SipDate(Calendar.getInstance(Locale.ENGLISH).getTimeInMillis());
                dateHeader.setDate(sipDate);
                response.addHeader(dateHeader);

                ExpiresHeader expiresHeader = (ExpiresHeader) request.getHeader(Expires.NAME);
                // 添加Contact头
                response.addHeader(request.getHeader(ContactHeader.NAME));
                // 添加Expires头
                response.addHeader(request.getExpires());
                ViaHeader viaHeader = (ViaHeader) request.getHeader(ViaHeader.NAME);
                String received = viaHeader.getReceived();
                int rPort = viaHeader.getRPort();
                // 本地模拟设备 received 为空 rPort 为 -1
                // 解析本地地址替代
                if (StringUtils.isEmpty(received) || rPort == -1) {
                    log.warn("本地地址替代! received:{},rPort:{} [{}:{}]", received, rPort, viaHeader.getHost(), viaHeader.getPort());
                    received = viaHeader.getHost();
                    rPort = viaHeader.getPort();
                }
                SipDevice device = new SipDevice();
                ;
                device.setStreamMode("UDP");
                device.setDeviceSipId(sipId);
                device.setIp(received);
                device.setPort(rPort);
                device.setHostAddress(received.concat(":").concat(String.valueOf(rPort)));
                // 注销成功
                if (expiresHeader != null && expiresHeader.getExpires() == 0) {
                    registerFlag = 2;
                } else {
                    registerFlag = 1;
                    ViaHeader reqViaHeader = (ViaHeader) request.getHeader(ViaHeader.NAME);
                    String transport = reqViaHeader.getTransport();
                    device.setTransport("TCP".equalsIgnoreCase(transport) ? "TCP" : "UDP");
                }
                sipSender.transmitRequest(Sipreq.getLocalAddress().getHostAddress(), response);
                // 注册成功
                if (registerFlag == 1) {
                    log.info("注册成功! sipId:" + device.getDeviceSipId());
                    device.setRegisterTime(DateUtils.getNowDate());
                    sipDeviceService.updateDevice(device);
                    List<SipDeviceChannel> channels = sipDeviceChannelService.selectSipDeviceChannelByDeviceSipId(device.getDeviceSipId());
                    if (channels.size() > 0) {
                        Device iotdev = deviceService.selectDeviceBySerialNumber(device.getDeviceSipId());
                        if (iotdev == null) {
                            //添加到统一设备管理 默认添加到admin账号下
                            int result = deviceService.insertDeviceAuto(device.getDeviceSipId(), 1L, sipConfig.getProductId(), 3);
                            if (result == 1) {
                                log.info("-----------sip设备认证成功,并自动添加设备到系统，SipId:" + device.getDeviceSipId() + "---------------");
                            }
                            iotdev = new Device();
                        }
                        if (iotdev.getStatus() != 3 && iotdev.getStatus() != 2) {
                            iotdev.setStatus(3);
                            deviceService.updateDeviceStatusAndLocation(iotdev,device.getIp());
                        }
                        // 重新注册更新设备和通道，以免设备替换或更新后信息无法更新
                        onRegister(device);
                    } else {
                        log.info("未找到改设备! deviceId:" + device.getDeviceId());
                    }
                } else if (registerFlag == 2) {
                    log.info("注销成功! deviceId:" + device.getDeviceId());
                    mqttService.publishStatus(device, 4);
                }

            }
        } catch (SipException | NoSuchAlgorithmException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void onRegister(SipDevice device) {
        // TODO 查询设备信息和下挂设备信息 自动拉流
        messageInvoker.deviceInfoQuery(device);
        messageInvoker.catalogQuery(device);
        messageInvoker.subscribe(device,0,0,3600,"0",null,null);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String method = "REGISTER";
        sipListener.addRequestProcessor(method, this);
    }
}
