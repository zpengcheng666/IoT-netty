package com.sydh.notify.mapper;

import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.notify.domain.NotifyTemplate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知模版Mapper接口
 *
 * @author kerwincui
 * @date 2023-12-01
 */
public interface NotifyTemplateMapper extends BaseMapperX<NotifyTemplate>
{

    /**
     * 查询同一业务已启用的模板
     * @param notifyTemplate
     * @return
     */
    public Integer selectEnableNotifyTemplateCount(NotifyTemplate notifyTemplate);

    /**
     * 批量更新渠道状态
     * @param ids ids
     * @return
     */
    public int updateNotifyBatch(@Param("ids") List<Long> ids, @Param("status") Integer status);

    /**
     * 根据业务编码查询启用模板
     * @param notifyTemplate 通知模板
     * @return com.sydh.notify.domain.NotifyTemplate
     */
    NotifyTemplate selectOnlyEnable(NotifyTemplate notifyTemplate);

    /**
     * 根据场景ID批量删除告警场景
     * @param notifyTemplateIds
     * @return
     */
    public int deleteAlertNotifyTemplateByNotifyTemplateIds(Long[] notifyTemplateIds);
}
