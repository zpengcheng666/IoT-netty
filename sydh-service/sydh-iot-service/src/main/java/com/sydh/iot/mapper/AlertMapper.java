package com.sydh.iot.mapper;

import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.iot.domain.Alert;
import com.sydh.iot.domain.AlertNotifyTemplate;
import com.sydh.iot.domain.AlertScene;
import com.sydh.iot.domain.Scene;
import com.sydh.iot.model.vo.AlertSceneSendVO;
import com.sydh.notify.domain.NotifyTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 设备告警Mapper接口
 *
 * @author kerwincui
 * @date 2022-01-13
 */
@Repository
public interface AlertMapper extends BaseMapperX<Alert>
{

    /**
     * 获取告警关联的场景列表
     * @param alertId
     * @return
     */
    public List<Scene> selectScenesByAlertId(Long alertId);

    /**
     * 批量插入告警场景
     * @param alerts
     * @return
     */
    public int insertAlertSceneList(List<AlertScene> alerts);

    /**
     * 根据告警ID批量删除告警场景
     * @param alertIds
     * @return
     */
    public int deleteAlertSceneByAlertIds(Long[]  alertIds);

    /**
     * 根据场景ID批量删除告警场景
     * @param sceneIds
     * @return
     */
    public int deleteAlertSceneBySceneIds(Long[] sceneIds);

    /**
     * 批量插入告警通知模版
     * @param alertNotifyTemplateList
     * @return
     */
    public int insertAlertNotifyTemplateList(List<AlertNotifyTemplate> alertNotifyTemplateList);

    /**
     * 根据告警ID批量删除告警场景
     * @param alertIds
     * @return
     */
    public int deleteAlertNotifyTemplateByAlertIds(Long[]  alertIds);

    /**
     * 查询告警通知模版
     * @param alertId 告警id
     * @return java.util.List<com.sydh.iot.domain.AlertNotifyTemplate>
     */
    List<AlertNotifyTemplate> selectAlertNotifyTemplateList(Long alertId);

    /**
     * 获取告警关联的通知模版列表
     * @param alertId 告警id
     * @return
     */
    public List<NotifyTemplate> selectNotifyTemplateListByAlertId(Long alertId);

    /**
     * 获取场景关联的告警列表
     * @param sceneId 场景id
     * @return java.util.List<com.sydh.iot.domain.AlertScene>
     */
    List<AlertScene> selectAlertSceneListBySceneId(Long sceneId);

    /**
     * 获取场景告警通知参数
     *
     * @param sceneId 场景id
     * @return 设备告警集合
     */
    List<AlertSceneSendVO> listByAlertIds(Long sceneId);
}
