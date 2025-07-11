package com.sydh.notify.core.sms.config;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson2.JSONObject;
import com.sydh.common.extend.enums.NotifyChannelProviderEnum;
import com.sydh.notify.domain.NotifyChannel;
import com.sydh.notify.domain.NotifyTemplate;
import com.sydh.notify.service.INotifyChannelService;
import com.sydh.notify.service.INotifyTemplateService;
import org.dromara.sms4j.core.datainterface.SmsReadConfig;
import org.dromara.sms4j.provider.config.BaseConfig;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author gsb
 * @date 2023/12/14 17:10
 */
@Component
public class ReadConfig implements SmsReadConfig {

    @Resource
    private INotifyChannelService notifyChannelService;
    @Resource
    private INotifyTemplateService notifyTemplateService;

    @Override
    public BaseConfig getSupplierConfig(String notifyTemplateId) {
        NotifyTemplate notifyTemplate = notifyTemplateService.selectNotifyTemplateById(Long.valueOf(notifyTemplateId));
        NotifyChannel notifyChannel = notifyChannelService.selectNotifyChannelById(notifyTemplate.getChannelId());
        NotifyChannelProviderEnum notifyChannelProviderEnum = NotifyChannelProviderEnum.getByChannelTypeAndProvider(notifyChannel.getChannelType(), notifyChannel.getProvider());
        // 注意：因为配置参数是分开渠道和模版配的，所以这里需要先转换，再copy一下
        BaseConfig baseConfig = (BaseConfig) JSONObject.parseObject(notifyChannel.getConfigContent(), notifyChannelProviderEnum.getConfigContentClass());
        CopyOptions copyOptions = CopyOptions.create(null, true);
        BeanUtil.copyProperties(JSONObject.parseObject(notifyTemplate.getMsgParams(), notifyChannelProviderEnum.getMsgParamsClass()), baseConfig, copyOptions);
        return baseConfig;
    }

    @Override
    public List<BaseConfig> getSupplierConfigList() {
        return null;
    }
}
