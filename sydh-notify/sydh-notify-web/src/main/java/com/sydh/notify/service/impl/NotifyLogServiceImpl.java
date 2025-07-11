package com.sydh.notify.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sydh.common.annotation.DataScope;
import com.sydh.common.extend.aspectj.DataScopeAspect;
import com.sydh.common.utils.DateUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.notify.convert.NotifyLogConvert;
import com.sydh.notify.domain.NotifyChannel;
import com.sydh.notify.domain.NotifyLog;
import com.sydh.notify.domain.NotifyTemplate;
import com.sydh.notify.mapper.NotifyChannelMapper;
import com.sydh.notify.mapper.NotifyLogMapper;
import com.sydh.notify.mapper.NotifyTemplateMapper;
import com.sydh.notify.service.INotifyLogService;
import com.sydh.notify.vo.NotifyLogVO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 通知日志Service业务层处理
 *
 * @author sydh
 * @date 2023-12-16
 */
@Service
public class NotifyLogServiceImpl extends ServiceImpl<NotifyLogMapper,NotifyLog> implements INotifyLogService {
    @Resource
    private NotifyLogMapper notifyLogMapper;
    @Resource
    private NotifyChannelMapper notifyChannelMapper;
    @Resource
    private NotifyTemplateMapper notifyTemplateMapper;

    /**
     * 查询通知日志
     *
     * @param id 通知日志主键
     * @return 通知日志
     */
    @Override
    public NotifyLog selectNotifyLogById(Long id)
    {
        return notifyLogMapper.selectById(id);
    }

    /**
     * 查询通知日志分页列表
     *
     * @param notifyLog 通知日志
     * @return 通知日志
     */
    @Override
    @DataScope()
    public Page<NotifyLogVO> pageNotifyLogVO(NotifyLog notifyLog) {
//        SysUser user = getLoginUser().getUser();
        // 查询所属机构
//        if (null != user.getDeptId()) {
//            notifyLog.setTenantId(user.getDept().getDeptUserId());
//        } else {
//            notifyLog.setTenantId(user.getUserId());
//        }
        LambdaQueryWrapper<NotifyLog> lqw = buildQueryWrapper(notifyLog);
        lqw.orderByDesc(NotifyLog::getCreateTime);
        Page<NotifyLog> notifyLogPage = baseMapper.selectPage(new Page<>(notifyLog.getPageNum(), notifyLog.getPageSize()), lqw);
        Page<NotifyLogVO> notifyLogVoPage = NotifyLogConvert.INSTANCE.convertNotifyLogVOPage(notifyLogPage);
        List<NotifyLogVO> notifyLogs = notifyLogVoPage.getRecords();
        if (CollectionUtils.isEmpty(notifyLogs)) {
            return notifyLogVoPage;
        }
        List<Long> channelIdList = notifyLogs.stream().map(NotifyLogVO::getChannelId).collect(Collectors.toList());
        List<NotifyChannel> notifyChannelList = notifyChannelMapper.selectBatchIds(channelIdList);
        Map<Long, NotifyChannel> notifyChannelMap = notifyChannelList.stream().collect(Collectors.toMap(NotifyChannel::getId, Function.identity()));
        List<Long> templateIdList = notifyLogs.stream().map(NotifyLogVO::getNotifyTemplateId).collect(Collectors.toList());
        List<NotifyTemplate> notifyTemplateList = notifyTemplateMapper.selectBatchIds(templateIdList);
        Map<Long, NotifyTemplate> notifyTemplateMap = notifyTemplateList.stream().collect(Collectors.toMap(NotifyTemplate::getId, Function.identity()));
        for (NotifyLogVO log : notifyLogs) {
            if (notifyChannelMap.containsKey(log.getChannelId())) {
                log.setChannelName(notifyChannelMap.get(log.getChannelId()).getName());
            }
            if (notifyTemplateMap.containsKey(log.getNotifyTemplateId())) {
                log.setTemplateName(notifyTemplateMap.get(log.getNotifyTemplateId()).getName());
            }
        }
        return notifyLogVoPage;
    }

    private LambdaQueryWrapper<NotifyLog> buildQueryWrapper(NotifyLog query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<NotifyLog> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getChannelId() != null, NotifyLog::getChannelId, query.getChannelId());
        lqw.eq(query.getNotifyTemplateId() != null, NotifyLog::getNotifyTemplateId, query.getNotifyTemplateId());
        lqw.eq(StringUtils.isNotBlank(query.getMsgContent()), NotifyLog::getMsgContent, query.getMsgContent());
        lqw.eq(StringUtils.isNotBlank(query.getSendAccount()), NotifyLog::getSendAccount, query.getSendAccount());
        lqw.eq(query.getSendStatus() != null, NotifyLog::getSendStatus, query.getSendStatus());
        lqw.eq(StringUtils.isNotBlank(query.getResultContent()), NotifyLog::getResultContent, query.getResultContent());
        lqw.eq(StringUtils.isNotBlank(query.getServiceCode()), NotifyLog::getServiceCode, query.getServiceCode());
        lqw.eq(query.getTenantId() != null, NotifyLog::getTenantId, query.getTenantId());
        lqw.like(StringUtils.isNotBlank(query.getTenantName()), NotifyLog::getTenantName, query.getTenantName());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(NotifyLog::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }

        // 数据范围过滤
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))){
            lqw.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE));
        }

        return lqw;
    }

    /**
     * 新增通知日志
     *
     * @param notifyLog 通知日志
     * @return 结果
     */
    @Override
    public int insertNotifyLog(NotifyLog notifyLog)
    {
        notifyLog.setCreateTime(DateUtils.getNowDate());
        return notifyLogMapper.insert(notifyLog);
    }

    /**
     * 修改通知日志
     *
     * @param notifyLog 通知日志
     * @return 结果
     */
    @Override
    public int updateNotifyLog(NotifyLog notifyLog)
    {
        notifyLog.setUpdateTime(DateUtils.getNowDate());
        return notifyLogMapper.updateById(notifyLog);
    }

    /**
     * 批量删除通知日志
     *
     * @param ids 需要删除的通知日志主键
     * @return 结果
     */
    @Override
    public int deleteNotifyLogByIds(Long[] ids)
    {
        return notifyLogMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /**
     * 删除通知日志信息
     *
     * @param id 通知日志主键
     * @return 结果
     */
    @Override
    public int deleteNotifyLogById(Long id)
    {
        return notifyLogMapper.deleteById(id);
    }
}
