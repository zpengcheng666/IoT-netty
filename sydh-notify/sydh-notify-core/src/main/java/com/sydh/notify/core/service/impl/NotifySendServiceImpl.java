package com.sydh.notify.core.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.sydh.common.constant.HttpStatus;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.redis.RedisCache;
import com.sydh.common.exception.ServiceException;
import com.sydh.common.extend.core.domin.notify.AlertPushParams;
import com.sydh.common.extend.core.domin.notify.NotifySendResponse;
import com.sydh.common.extend.enums.NotifyChannelEnum;
import com.sydh.common.extend.enums.NotifyChannelProviderEnum;
import com.sydh.common.extend.enums.NotifyServiceCodeEnum;
import com.sydh.common.utils.MessageUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.common.utils.ValidationUtils;
import com.sydh.common.utils.VerifyCodeUtils;
import com.sydh.common.utils.wechat.AesException;
import com.sydh.common.utils.wechat.WXBizMsgCrypt;
import com.sydh.notify.core.dingtalk.service.DingTalkService;
import com.sydh.notify.core.email.service.EmailService;
import com.sydh.notify.core.service.NotifySendService;
import com.sydh.notify.core.sms.service.ISmsService;
import com.sydh.notify.core.vo.SendParams;
import com.sydh.notify.core.voice.service.VoiceService;
import com.sydh.notify.core.wechat.service.WeChatPushService;
import com.sydh.notify.domain.NotifyChannel;
import com.sydh.notify.domain.NotifyLog;
import com.sydh.notify.domain.NotifyTemplate;
import com.sydh.notify.service.INotifyChannelService;
import com.sydh.notify.service.INotifyLogService;
import com.sydh.notify.service.INotifyTemplateService;
import com.sydh.notify.vo.NotifyVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author fastb
 * @version 1.0
 * @description: 通知业务类
 * @date 2023-12-26 10:38
 */
@Slf4j
@Service
public class NotifySendServiceImpl implements NotifySendService {

    @Resource
    private INotifyChannelService notifyChannelService;
    @Resource
    private INotifyTemplateService notifyTemplateService;
    @Resource
    private INotifyLogService notifyLogService;
    @Resource
    private ISmsService smsService;
    @Resource
    private VoiceService voiceService;
    @Resource
    private EmailService emailService;
    @Resource
    private WeChatPushService weChatPushService;
    @Resource
    private RedisCache redisCache;
    @Resource
    private DingTalkService dingTalkService;

    @Override
    public AjaxResult send(SendParams sendParams) {
        // 获取配置参数
        NotifyTemplate notifyTemplate = notifyTemplateService.selectNotifyTemplateById(sendParams.getId());
        NotifyChannel notifyChannel = notifyChannelService.selectNotifyChannelById(notifyTemplate.getChannelId());
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        if (StringUtils.isNotEmpty(sendParams.getVariables())) {
            map = JSONObject.parseObject(sendParams.getVariables(), LinkedHashMap.class);
        }
        NotifyChannelProviderEnum notifyChannelProviderEnum = NotifyChannelProviderEnum.getByChannelTypeAndProvider(notifyChannel.getChannelType(), notifyChannel.getProvider());
        NotifyVO notifyVO = new NotifyVO();
        notifyVO.setNotifyChannel(notifyChannel).setNotifyTemplate(notifyTemplate)
                .setSendAccount(sendParams.getSendAccount()).setMap(map).setNotifyChannelProviderEnum(notifyChannelProviderEnum);
        return this.notifySend(notifyVO);
    }

    @Override
    public AjaxResult notifySend(NotifyVO notifyVO) {
        // 获取发送参数
        NotifyChannel notifyChannel = notifyVO.getNotifyChannel();
        NotifyTemplate notifyTemplate = notifyVO.getNotifyTemplate();
        String sendAccount = notifyVO.getSendAccount();
        if (StringUtils.isNotEmpty(notifyVO.getSendAccount())) {
            String s = notifyVO.getSendAccount().replaceAll("，", ",");
            sendAccount = s;
            notifyVO.setSendAccount(s);
        }
        NotifyChannelEnum notifyChannelEnum = NotifyChannelEnum.getNotifyChannelEnum(notifyChannel.getChannelType());
        // 组装模板内容参数，发送通知
        NotifySendResponse notifySendResponse = new NotifySendResponse();
        switch (Objects.requireNonNull(notifyChannelEnum)) {
            case SMS:
                notifySendResponse = smsService.send(notifyVO);
                break;
            case EMAIL:
                notifySendResponse = emailService.send(notifyVO);
                break;
            case VOICE:
                notifySendResponse = voiceService.send(notifyVO);
                break;
            case WECHAT:
                notifySendResponse = weChatPushService.send(notifyVO);
                break;
            case DING_TALK:
                notifySendResponse = dingTalkService.send(notifyVO);
                break;
            default:
                break;
        }
        // 保存日志
        NotifyLog notifyLog = new NotifyLog();
        notifyLog.setChannelId(notifyChannel.getId()).setNotifyTemplateId(notifyTemplate.getId())
                .setSendAccount(StringUtils.isNotEmpty(notifySendResponse.getOtherSendAccount()) ? notifySendResponse.getOtherSendAccount() :  sendAccount)
                .setServiceCode(notifyTemplate.getServiceCode())
                .setMsgContent(notifySendResponse.getSendContent())
                .setSendStatus(Long.valueOf(notifySendResponse.getStatus())).setResultContent(notifySendResponse.getResultContent())
                .setTenantId(notifyTemplate.getTenantId()).setTenantName(notifyTemplate.getTenantName()).setCreateBy(notifyTemplate.getCreateBy());
        notifyLogService.insertNotifyLog(notifyLog);
        return 1 == notifySendResponse.getStatus() ? AjaxResult.success() : AjaxResult.error(notifySendResponse.getResultContent());
    }

    @Override
    public String alertSend(AlertPushParams alertPushParams) {
        // 获取发送模版
        NotifyTemplate notifyTemplate = notifyTemplateService.selectNotifyTemplateById(alertPushParams.getNotifyTemplateId());
        if (Objects.isNull(notifyTemplate) || 0 == notifyTemplate.getStatus()) {
            log.info("告警关联通知模版未启用，模版编号：{}", alertPushParams.getNotifyTemplateId());
            return "";
        }
        NotifyChannel notifyChannel = notifyChannelService.selectNotifyChannelById(notifyTemplate.getChannelId());
        NotifyChannelProviderEnum notifyChannelProviderEnum = NotifyChannelProviderEnum.getByChannelTypeAndProvider(notifyChannel.getChannelType(), notifyChannel.getProvider());
        NotifyVO notifyVO = new NotifyVO();
        notifyVO.setNotifyChannel(notifyChannel).setNotifyTemplate(notifyTemplate).setNotifyChannelProviderEnum(notifyChannelProviderEnum);
        // 获取模版参数
        JSONObject jsonMsgParams = JSONObject.parseObject(notifyVO.getNotifyTemplate().getMsgParams());
        String content = jsonMsgParams.get("content").toString();
        List<String> variables = notifyTemplateService.listVariables(content, notifyChannelProviderEnum);
        // 获取模版变量
        assert notifyChannelProviderEnum != null;
        NotifyChannelEnum notifyChannelEnum = NotifyChannelEnum.getNotifyChannelEnum(notifyChannelProviderEnum.getChannelType());
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        switch (Objects.requireNonNull(notifyChannelEnum)) {
            // 示例内容变量顺序：您的设备:${name},设备编号:${serialnumber},在${address}发生${alert}告警; 可自行修改
            case SMS:
            case EMAIL:
            case WECHAT:
            case DING_TALK:
                // 按顺序依次替换变量信息
                for (int i = 0; i < variables.size(); i++) {
                    if (i == 0) {
                        map.put(variables.get(i), alertPushParams.getDeviceName());
                    } else if (i == 1) {
                        map.put(variables.get(i), alertPushParams.getSerialNumber());
                    } else if (i == 2) {
                        map.put(variables.get(i), alertPushParams.getAddress());
                    } else {
                        map.put(variables.get(i), alertPushParams.getAlertName());
                    }
                }
                break;
            // 示例内容变量顺序：您的设备:${name}，在${address}发生告警，请尽快处理;
            // 阿里云语音模版只支持两个变量，所有语音统一使用两个变量，可自行修改
            case VOICE:
                // 按顺序依次替换变量信息
                for (int i = 0; i < variables.size(); i++) {
                    if (i == 0) {
                        map.put(variables.get(i), alertPushParams.getDeviceName());
                    } else {
                        map.put(variables.get(i), alertPushParams.getAddress());
                    }
                }
                break;
            case MQTT:
                for (int i = 0; i < variables.size(); i++) {
                    if (i == 0) {
                        map.put(variables.get(i), alertPushParams.getSerialNumber());
                    } else if (i == 1) {
                        map.put(variables.get(i), alertPushParams.getAddress());
                    } else if (i == 2) {
                        map.put(variables.get(i), alertPushParams.getValue());
                    } else if (i == 3) {
                        map.put(variables.get(i), alertPushParams.getMatchValue());
                    } else {
                        map.put(variables.get(i), alertPushParams.getAlertTime());
                    }
                }
                // 是否弹窗显示
                jsonMsgParams.put("showDialog", 1);
                jsonMsgParams.put("title", alertPushParams.getDeviceName());
                jsonMsgParams.put("content", StringUtils.strReplaceVariable("${", "}", content, map));
                return jsonMsgParams.toString();
            default:
                break;
        }
        // 获取发送账号
        Object sendAccountObject = jsonMsgParams.get("sendAccount");
        Set<String> sendAccountSet = new HashSet<>();
        if (ObjectUtil.isNotEmpty(sendAccountObject)) {
            Collections.addAll(sendAccountSet, sendAccountObject.toString());
        }
        // 短信、语音、微信小程序需要取设备所属及分享用户信息+模版配置账号，其余使用模版配置的账号
        if (NotifyChannelEnum.SMS.equals(notifyChannelEnum) || NotifyChannelEnum.VOICE.equals(notifyChannelEnum)) {
            if (CollectionUtils.isNotEmpty(alertPushParams.getUserPhoneSet())) {
                sendAccountSet.addAll(alertPushParams.getUserPhoneSet());
            }
        }
        if (NotifyChannelProviderEnum.WECHAT_MINI_PROGRAM.equals(notifyChannelProviderEnum)
                || NotifyChannelProviderEnum.WECHAT_PUBLIC_ACCOUNT.equals(notifyChannelProviderEnum)) {
            if (CollectionUtils.isNotEmpty(alertPushParams.getUserIdSet())) {
                for (Long userId : alertPushParams.getUserIdSet()) {
                    sendAccountSet.add(userId.toString());
                }
            }
        }
        notifyVO.setSendAccount(StringUtils.join(sendAccountSet, ","));
        // 发送
        notifyVO.setMap(map);
        this.notifySend(notifyVO);
        return "";
    }

    @Override
    public AjaxResult sendCaptchaSms(String phone, String captcha) {
        NotifyVO notifyVO = this.selectOnlyEnable(NotifyServiceCodeEnum.CAPTCHA.getServiceCode(), NotifyChannelEnum.SMS.getType(), null, 1L);
        NotifyChannelProviderEnum notifyChannelProviderEnum = notifyVO.getNotifyChannelProviderEnum();
        // 获取模板参数
        JSONObject jsonMsgParams = JSONObject.parseObject(notifyVO.getNotifyTemplate().getMsgParams());
        String content = jsonMsgParams.get("content").toString();
        // 从模板内容中获取 占位符 关键字
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        List<String> variables = new ArrayList<>();
        if (NotifyChannelProviderEnum.SMS_ALIBABA.equals(notifyChannelProviderEnum)) {
            variables = StringUtils.getVariables("${}", content);
        } else if (NotifyChannelProviderEnum.SMS_TENCENT.equals(notifyChannelProviderEnum)) {
            variables = StringUtils.getVariables("{}", content);
        }
        map.put(variables.get(0), captcha);
        notifyVO.setSendAccount(phone);
        notifyVO.setMap(map);
        return this.notifySend(notifyVO);
    }

    @Override
    public NotifyVO selectOnlyEnable(String serviceCode, String channelType, String provider, Long tenantId) {
        // 获取查询条件
        NotifyTemplate enableQueryCondition = notifyTemplateService.getEnableQueryCondition(serviceCode, channelType, provider, tenantId);
        NotifyTemplate notifyTemplate = notifyTemplateService.selectOnlyEnable(enableQueryCondition);
        if (Objects.isNull(notifyTemplate)) {
            throw new ServiceException(MessageUtils.message("not.find.enable.notify.template"));
        }
        NotifyChannel notifyChannel = notifyChannelService.selectNotifyChannelById(notifyTemplate.getChannelId());
        if (Objects.isNull(notifyChannel)) {
            throw new ServiceException(MessageUtils.message("not.find.notify.channel"));
        }
        NotifyVO notifyVO = new NotifyVO();
        notifyVO.setNotifyChannel(notifyChannel);
        notifyVO.setNotifyTemplate(notifyTemplate);
        NotifyChannelProviderEnum notifyChannelProviderEnum = NotifyChannelProviderEnum.getByChannelTypeAndProvider(notifyVO.getNotifyChannel().getChannelType(), notifyVO.getNotifyChannel().getProvider());
        notifyVO.setNotifyChannelProviderEnum(notifyChannelProviderEnum);
        return notifyVO;
    }

    @Override
    public AjaxResult sendSmsCaptcha(String phoneNumber, String redisKey) {
        String userIdKey = redisKey + phoneNumber;
        String captcha = VerifyCodeUtils.generateVerifyCode(6, "0123456789");
        AjaxResult ajaxResult = this.sendCaptchaSms(phoneNumber, captcha);
        if (HttpStatus.ERROR == Integer.parseInt(ajaxResult.get("code").toString())) {
            return AjaxResult.error(MessageUtils.message("sms.send.fail.contact.admin"));
        }
        redisCache.setCacheObject(userIdKey, captcha, 2, TimeUnit.MINUTES);
        return AjaxResult.success();
    }

    @Override
    public String weComVerifyUrl(String msgSignature, String timestamp, String nonce, String echostr) {
        // 因为只用验证一次，下面三个参数就不写在配置文件里了，需要验证的可以把下面的验证参数改为自己公司的，然后部署到服务器验证就行
        //token
        String token = "pr77kdcA5mzJwNeAwV86UcIS";
        // encodingAESKey
        String encodingAesKey = "efNILsQxM6wOCsrNPiBeuLOBDgDSnNtOVFBbtf6jwTe";
        //企业ID
        String corpId = "ww4761023a5d81550f";
        // 通过检验msg_signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
        String result = null;
        try {
            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token, encodingAesKey, corpId);
            result = wxcpt.VerifyURL(msgSignature, timestamp, nonce, echostr);
        } catch (AesException e) {
            log.error("企业微信验证url错误,error:{}", e.getMessage());
        }
        return result;
    }

    /**
     * 校验发送账号格式
     * @param sendAccount 发送账号
     * @param: notifyChannelEnum 通知枚举
     * @return java.lang.String
     */
    private String checkSendAccountMsg(String sendAccount, NotifyChannelProviderEnum notifyChannelProviderEnum) {
        boolean matches;
        switch (Objects.requireNonNull(notifyChannelProviderEnum)) {
            case SMS_ALIBABA:
            case SMS_TENCENT:
            case VOICE_ALIBABA:
            case DING_TALK_WORK:
            case WECHAT_WECOM_APPLY:
                matches  = ValidationUtils.isMobile(sendAccount);
                if (!matches) {
                    return "请输入正确的电话号码！";
                }
                break;
            case EMAIL_QQ:
            case EMAIL_163:
                matches = ValidationUtils.isEmail(sendAccount);
                if (!matches) {
                    return "请输入正确的邮箱地址！";
                }
                break;
            case WECHAT_MINI_PROGRAM:
                if (!StringUtils.isNumeric(sendAccount)) {
                    return "请输入正确的用户id";
                }
                break;
            default:
                return "";
        }
        return "";
    }

}
