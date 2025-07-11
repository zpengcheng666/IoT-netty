package com.sydh.notify.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sydh.common.annotation.DataScope;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.exception.ServiceException;
import com.sydh.common.extend.aspectj.DataScopeAspect;
import com.sydh.common.extend.core.domin.entity.SysUser;
import com.sydh.common.extend.core.domin.model.LoginUser;
import com.sydh.common.extend.core.domin.notify.NotifyConfigVO;
import com.sydh.common.extend.enums.NotifyChannelEnum;
import com.sydh.common.extend.enums.NotifyChannelProviderEnum;
import com.sydh.common.extend.enums.NotifyServiceCodeEnum;
import com.sydh.common.utils.DateUtils;
import com.sydh.common.utils.MessageUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.notify.convert.NotifyTemplateConvert;
import com.sydh.notify.domain.NotifyChannel;
import com.sydh.notify.domain.NotifyTemplate;
import com.sydh.notify.mapper.NotifyChannelMapper;
import com.sydh.notify.mapper.NotifyTemplateMapper;
import com.sydh.notify.service.INotifyTemplateService;
import com.sydh.notify.vo.NotifyTemplateVO;
import lombok.extern.slf4j.Slf4j;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.sydh.common.extend.utils.SecurityUtils.getLoginUser;
import static com.sydh.common.extend.utils.SecurityUtils.getUsername;


/**
 * 通知模版Service业务层处理
 *
 * @author kerwincui
 * @date 2023-12-01
 */
@Service
@Slf4j
public class NotifyTemplateServiceImpl extends ServiceImpl<NotifyTemplateMapper,NotifyTemplate> implements INotifyTemplateService {
    @Resource
    private NotifyTemplateMapper notifyTemplateMapper;
    @Resource
    private NotifyChannelMapper notifyChannelMapper;


    /**
     * 查询通知模版
     *
     * @param id 通知模版主键
     * @return 通知模版
     */
    @Override
    public NotifyTemplate selectNotifyTemplateById(Long id) {
        return notifyTemplateMapper.selectById(id);
    }

    /**
     * 查询通知模版分页列表
     *
     * @param notifyTemplate 通知模版
     * @return 通知模版
     */
    @Override
    @DataScope()
    public Page<NotifyTemplateVO> pageNotifyTemplateVO(NotifyTemplate notifyTemplate) {
        LambdaQueryWrapper<NotifyTemplate> lqw = buildQueryWrapper(notifyTemplate);
        lqw.orderByDesc(NotifyTemplate::getCreateTime);
        Page<NotifyTemplate> notifyTemplatePage = baseMapper.selectPage(new Page<>(notifyTemplate.getPageNum(), notifyTemplate.getPageSize()), lqw);
        Page<NotifyTemplateVO> templateVoPage = NotifyTemplateConvert.INSTANCE.convertNotifyTemplateVOPage(notifyTemplatePage);
        List<NotifyTemplateVO> notifyTemplates = templateVoPage.getRecords();
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(notifyTemplates)) {
            return templateVoPage;
        }
        List<Long> collect = notifyTemplates.stream().map(NotifyTemplateVO::getChannelId).collect(Collectors.toList());
        List<NotifyChannel> notifyChannelList = notifyChannelMapper.selectBatchIds(collect);
        Map<Long, NotifyChannel> notifyChannelMap = notifyChannelList.stream().collect(Collectors.toMap(NotifyChannel::getId, Function.identity()));
        for (NotifyTemplateVO template : notifyTemplates) {
            if (notifyChannelMap.containsKey(template.getChannelId())) {
                NotifyChannel notifyChannel = notifyChannelMap.get(template.getChannelId());
                template.setChannelName(notifyChannel.getName());
            }
        }
        return templateVoPage;
    }

    private LambdaQueryWrapper<NotifyTemplate> buildQueryWrapper(NotifyTemplate query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<NotifyTemplate> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(query.getName()), NotifyTemplate::getName, query.getName());
        lqw.eq(StringUtils.isNotBlank(query.getServiceCode()), NotifyTemplate::getServiceCode, query.getServiceCode());
        lqw.eq(query.getChannelId() != null, NotifyTemplate::getChannelId, query.getChannelId());
        lqw.eq(StringUtils.isNotBlank(query.getChannelType()), NotifyTemplate::getChannelType, query.getChannelType());
        lqw.eq(StringUtils.isNotBlank(query.getProvider()), NotifyTemplate::getProvider, query.getProvider());
        lqw.eq(StringUtils.isNotBlank(query.getMsgParams()), NotifyTemplate::getMsgParams, query.getMsgParams());
        lqw.eq(query.getStatus() != null, NotifyTemplate::getStatus, query.getStatus());
        lqw.eq(query.getTenantId() != null, NotifyTemplate::getTenantId, query.getTenantId());
        lqw.like(StringUtils.isNotBlank(query.getTenantName()), NotifyTemplate::getTenantName, query.getTenantName());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(NotifyTemplate::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }

        // 数据范围过滤
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))){
            lqw.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE));
        }

        return lqw;
    }

    /**
     * 新增通知模版
     *
     * @param notifyTemplate 通知模版
     * @return 结果
     */
    @Override
    public AjaxResult insertNotifyTemplate(NotifyTemplate notifyTemplate) {
        SysUser user = getLoginUser().getUser();
        if (null == user.getDeptId()) {
            throw new ServiceException(MessageUtils.message("only.allow.tenant.config"));
        }
        NotifyChannelProviderEnum providerEnum = NotifyChannelProviderEnum.getByChannelTypeAndProvider(notifyTemplate.getChannelType(), notifyTemplate.getProvider());
        boolean canNotConfig = (!user.getDept().getDeptUserId().equals(user.getUserId())
                && (!NotifyServiceCodeEnum.ALERT.getServiceCode().equals(notifyTemplate.getServiceCode())
                || NotifyChannelProviderEnum.WECHAT_MINI_PROGRAM == providerEnum));
        if (canNotConfig) {
            return AjaxResult.error(MessageUtils.message("only.can.config.alert.and.not.wechat.mini.notify"));
        }
        notifyTemplate.setTenantId(user.getDept().getDeptUserId());
        notifyTemplate.setTenantName(user.getDept().getDeptName());
        notifyTemplate.setCreateTime(DateUtils.getNowDate());
        notifyTemplate.setCreateBy(user.getUserName());
        return notifyTemplateMapper.insert(notifyTemplate) > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 修改通知模版
     *
     * @param notifyTemplate 通知模版
     * @return 结果
     */
    @Override
    public AjaxResult updateNotifyTemplate(NotifyTemplate notifyTemplate) {
        notifyTemplate.setUpdateTime(DateUtils.getNowDate());
        if (NotifyChannelEnum.SMS.getType().equals(notifyTemplate.getChannelType())) {
            SmsFactory.unregister(notifyTemplate.getId().toString());
        }
        notifyTemplate.setUpdateBy(getUsername());
        return notifyTemplateMapper.updateById(notifyTemplate) > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 批量删除通知模版
     *
     * @param ids 需要删除的通知模版主键
     * @return 结果
     */
    @Override
    public int deleteNotifyTemplateByIds(Long[] ids) {
        int i = notifyTemplateMapper.deleteBatchIds(Arrays.asList(ids));
        if (i > 0) {
            notifyTemplateMapper.deleteAlertNotifyTemplateByNotifyTemplateIds(ids);
        }
        return i;
    }

    /**
     * 删除通知模版信息
     *
     * @param id 通知模版主键
     * @return 结果
     */
    @Override
    public int deleteNotifyTemplateById(Long id) {
        int i = notifyTemplateMapper.deleteById(id);
        if (i > 0) {
            notifyTemplateMapper.deleteAlertNotifyTemplateByNotifyTemplateIds(new Long[]{id});
        }
        return i;
    }

    /**
     * 查询某一业务通知通道是否有启动的（业务编码唯一启用一个模板）
     * @param notifyTemplate 通知模板
     */
    @Override
    public Integer countNormalTemplate(NotifyTemplate notifyTemplate){
        NotifyChannelProviderEnum providerEnum = NotifyChannelProviderEnum.getByChannelTypeAndProvider(notifyTemplate.getChannelType(), notifyTemplate.getProvider());
        if (NotifyServiceCodeEnum.ALERT.getServiceCode().equals(notifyTemplate.getServiceCode())
                && NotifyChannelProviderEnum.WECHAT_MINI_PROGRAM != providerEnum) {
            return 0;
        }
        LoginUser loginUser = getLoginUser();
        assert !Objects.isNull(notifyTemplate.getServiceCode()) : "业务编码不能为空";
        NotifyTemplate selectOne = this.getEnableQueryCondition(notifyTemplate.getServiceCode(), notifyTemplate.getChannelType(), notifyTemplate.getProvider(), loginUser.getUser().getDept().getDeptUserId());
        selectOne.setId(notifyTemplate.getId());
        return notifyTemplateMapper.selectEnableNotifyTemplateCount(selectOne);
    }

    /**
     * 获取唯一启用模版查询条件
     * 唯一启用条件：同一业务编码的模板短信、语音、邮箱渠道分别可以启用一个，微信、钉钉渠道下不同服务商分别可以启用一个
     * @param: serviceCode
     * @param: channelType
     * @param: provider
     * @return com.sydh.notify.domain.NotifyTemplate
     */
    @Override
    public NotifyTemplate getEnableQueryCondition(String serviceCode, String channelType, String provider, Long tenantId) {
        NotifyTemplate notifyTemplate = new NotifyTemplate();
        notifyTemplate.setServiceCode(serviceCode);
        notifyTemplate.setStatus(1);
        notifyTemplate.setTenantId(tenantId);
        NotifyChannelEnum notifyChannelEnum = NotifyChannelEnum.getNotifyChannelEnum(channelType);
        switch (Objects.requireNonNull(notifyChannelEnum)) {
            case SMS:
            case VOICE:
            case EMAIL:
                notifyTemplate.setChannelType(channelType);
                break;
            case WECHAT:
            case DING_TALK:
            case MQTT:
                notifyTemplate.setChannelType(channelType);
                notifyTemplate.setProvider(provider);
                break;
            default:
                break;
        }
        return notifyTemplate;
    }

    /**
     * 更新某一类型为不可用状态，选中的为可用状态
     * @param notifyTemplate 通知模板
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTemplateStatus(NotifyTemplate notifyTemplate){
        SysUser user = getLoginUser().getUser();
        NotifyChannelProviderEnum providerEnum = NotifyChannelProviderEnum.getByChannelTypeAndProvider(notifyTemplate.getChannelType(), notifyTemplate.getProvider());
        if (!NotifyServiceCodeEnum.ALERT.getServiceCode().equals(notifyTemplate.getServiceCode())
                || NotifyChannelProviderEnum.WECHAT_MINI_PROGRAM == providerEnum) {
            if (!user.getDept().getDeptUserId().equals(user.getUserId())) {
                throw new ServiceException(MessageUtils.message("notify.can.not.not.alert.and.wechat.mini.status"));
            }
            LoginUser loginUser = getLoginUser();
            // 查询所有统一类型可用的渠道
            NotifyTemplate selectEnable = this.getEnableQueryCondition(notifyTemplate.getServiceCode(), notifyTemplate.getChannelType(), notifyTemplate.getProvider(), loginUser.getUser().getDept().getDeptUserId());
            LambdaQueryWrapper<NotifyTemplate> queryWrapper = this.buildQueryWrapper(selectEnable);
            queryWrapper.ne(NotifyTemplate::getId, notifyTemplate.getId());
            List<NotifyTemplate> notifyTemplateList = notifyTemplateMapper.selectList(queryWrapper);
            if (!CollectionUtils.isEmpty(notifyTemplateList)){
                //如果有同一类型的渠道为可用，要先将更新为不可用
                List<Long> ids = notifyTemplateList.stream().map(NotifyTemplate::getId).filter(id -> !Objects.equals(id, notifyTemplate.getId())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(ids)) {
                    notifyTemplateMapper.updateNotifyBatch(ids, 0);
                }
            }
        }
        //更新选中的为可用状态
        NotifyTemplate updateBo = new NotifyTemplate();
        updateBo.setStatus(1);
        updateBo.setId(notifyTemplate.getId());
        notifyTemplateMapper.updateById(updateBo);
    }

    @Override
    public NotifyTemplate selectOnlyEnable(NotifyTemplate notifyTemplate) {
        return notifyTemplateMapper.selectOnlyEnable(notifyTemplate);
    }

    @Override
    public List<NotifyConfigVO> getNotifyMsgParams(Long channelId, String msgType) {
        NotifyChannel notifyChannel = notifyChannelMapper.selectById(channelId);
        if (Objects.isNull(notifyChannel)) {
            return new ArrayList<>();
        }
        NotifyChannelProviderEnum notifyChannelProviderEnum = NotifyChannelProviderEnum.getByChannelTypeAndProvider(notifyChannel.getChannelType(), notifyChannel.getProvider());
        return NotifyChannelProviderEnum.getMsgParams(notifyChannelProviderEnum, msgType);
    }

    @Override
    public List<String> listVariables(String content, NotifyChannelProviderEnum notifyChannelProviderEnum) {
        List<String> variables;
        switch (Objects.requireNonNull(notifyChannelProviderEnum)) {
            case WECHAT_MINI_PROGRAM:
            case WECHAT_PUBLIC_ACCOUNT:
                variables = StringUtils.getWeChatMiniVariables(content);
                break;
            case SMS_TENCENT:
            case VOICE_TENCENT:
                variables = StringUtils.getVariables("{}", content);
                break;
            case EMAIL_QQ:
            case EMAIL_163:
                variables = StringUtils.getVariables("#{}", content);
                break;
            default:
                variables = StringUtils.getVariables("${}", content);
                break;
        }
        return variables;
    }

    @Override
    public String getAlertWechatMini() {
        NotifyTemplate selectOne = new NotifyTemplate();
        selectOne.setServiceCode(NotifyServiceCodeEnum.ALERT.getServiceCode()).setChannelType(NotifyChannelProviderEnum.WECHAT_MINI_PROGRAM.getChannelType()).setProvider(NotifyChannelProviderEnum.WECHAT_MINI_PROGRAM.getProvider()).setStatus(1);
        SysUser user = getLoginUser().getUser();
        if (null != user.getDeptId()) {
            selectOne.setTenantId(user.getDept().getDeptUserId());
        } else {
            selectOne.setTenantId(1L);
        }
        NotifyTemplate notifyTemplate = notifyTemplateMapper.selectOnlyEnable(selectOne);
        if (notifyTemplate == null || StringUtils.isEmpty(notifyTemplate.getMsgParams())) {
            return "";
        }
        JSONObject jsonObject = JSONObject.parseObject(notifyTemplate.getMsgParams());
        return jsonObject.get("templateId").toString();
    }

}
