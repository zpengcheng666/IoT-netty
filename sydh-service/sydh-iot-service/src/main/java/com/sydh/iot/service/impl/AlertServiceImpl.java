package com.sydh.iot.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sydh.common.annotation.DataScope;
import com.sydh.common.extend.aspectj.DataScopeAspect;
import com.sydh.common.extend.core.domin.entity.SysUser;
import com.sydh.common.utils.DateUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.iot.convert.AlertConvert;
import com.sydh.iot.domain.Alert;
import com.sydh.iot.domain.AlertNotifyTemplate;
import com.sydh.iot.domain.AlertScene;
import com.sydh.iot.domain.Scene;
import com.sydh.iot.mapper.AlertMapper;
import com.sydh.iot.model.vo.AlertSceneSendVO;
import com.sydh.iot.model.vo.AlertVO;
import com.sydh.iot.service.IAlertService;
import com.sydh.notify.domain.NotifyTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

import static com.sydh.common.extend.utils.SecurityUtils.getLoginUser;


/**
 * 设备告警Service业务层处理
 *
 * @author kerwincui
 * @date 2022-01-13
 */
@Slf4j
@Service
public class AlertServiceImpl extends ServiceImpl<AlertMapper,Alert> implements IAlertService {

    @Resource
    private AlertMapper alertMapper;

    /**
     * 查询设备告警分页列表
     *
     * @param alert 设备告警
     * @return 设备告警
     */
    @Override
    @DataScope()
    public Page<AlertVO> pageAlertVO(Alert alert) {
        SysUser user = getLoginUser().getUser();
        LambdaQueryWrapper<Alert> lqw = buildQueryWrapper(alert);
        if (null != user.getDeptId()) {
            // 数据范围过滤
            if (ObjectUtil.isNotEmpty(alert.getParams().get(DataScopeAspect.DATA_SCOPE))){
                lqw.apply((String) alert.getParams().get(DataScopeAspect.DATA_SCOPE));
            }
        } else {
            lqw.eq(Alert::getTenantId, user.getUserId());
        }
        lqw.orderByDesc(Alert::getCreateTime);
        Page<Alert> alertPage = baseMapper.selectPage(new Page<>(alert.getPageNum(), alert.getPageSize()), lqw);
        return AlertConvert.INSTANCE.convertAlertVOPage(alertPage);
    }

    /**
     * 查询设备告警
     *
     * @param alertId 设备告警主键
     * @return 设备告警
     */
    @Override
    public AlertVO selectAlertByAlertId(Long alertId) {
        Alert alert = alertMapper.selectById(alertId);
        AlertVO alertVO = AlertConvert.INSTANCE.convertAlertVO(alert);
        alertVO.setScenes(alertMapper.selectScenesByAlertId(alert.getAlertId()));
        alertVO.setNotifyTemplateList(alertMapper.selectNotifyTemplateListByAlertId(alert.getAlertId()));
        return alertVO;
    }

    /**
     * 获取告警关联的场景列表
     *
     * @param alertId
     * @return
     */
    @Override
    public List<Scene> selectScenesByAlertId(Long alertId) {
        return alertMapper.selectScenesByAlertId(alertId);
    }

    /**
     * 新增设备告警
     *
     * @param alertVO 设备告警
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertAlert(AlertVO alertVO) {
        // 新增告警配置
        alertVO.setCreateTime(DateUtils.getNowDate());
        Alert alert = AlertConvert.INSTANCE.convertAlert(alertVO);
        int result = alertMapper.insert(alert);
        // 批量新增告警关联场景
        if (alertVO.getScenes() != null && !alertVO.getScenes().isEmpty()) {
            List<AlertScene> alertScenes = new ArrayList<>();
            for (Scene scene : alertVO.getScenes()) {
                AlertScene alertScene = new AlertScene();
                alertScene.setAlertId(alert.getAlertId());
                alertScene.setSceneId(scene.getSceneId());
                alertScenes.add(alertScene);
            }
            alertMapper.insertAlertSceneList(alertScenes);
        }
        // 批量新增通知模版
        if (CollectionUtils.isNotEmpty(alertVO.getNotifyTemplateList())) {
            List<AlertNotifyTemplate> alertNotifyTemplateList = new ArrayList<>();
            for (NotifyTemplate notifyTemplate : alertVO.getNotifyTemplateList()) {
                AlertNotifyTemplate alertNotifyTemplate = new AlertNotifyTemplate();
                alertNotifyTemplate.setAlertId(alert.getAlertId());
                alertNotifyTemplate.setNotifyTemplateId(notifyTemplate.getId());
                alertNotifyTemplateList.add(alertNotifyTemplate);
            }
            alertMapper.insertAlertNotifyTemplateList(alertNotifyTemplateList);
        }
        return result;
    }

    /**
     * 修改设备告警
     *
     * @param alertVO 设备告警
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateAlert(AlertVO alertVO) {
        Alert alert = AlertConvert.INSTANCE.convertAlert(alertVO);
        // 批量删除告警场景
        alertMapper.deleteAlertSceneByAlertIds(new Long[]{alert.getAlertId()});
        // 批量删除告警通知
        alertMapper.deleteAlertNotifyTemplateByAlertIds(new Long[]{alert.getAlertId()});
        // 更新告警配置
        alert.setUpdateTime(DateUtils.getNowDate());
        int result = alertMapper.updateById(alert);
        // 批量新增告警关联场景
        if (alertVO.getScenes() != null && !alertVO.getScenes().isEmpty()) {
            List<AlertScene> alertScenes = new ArrayList<>();
            for (Scene scene : alertVO.getScenes()) {
                AlertScene alertScene = new AlertScene();
                alertScene.setAlertId(alert.getAlertId());
                alertScene.setSceneId(scene.getSceneId());
                alertScenes.add(alertScene);
            }
            alertMapper.insertAlertSceneList(alertScenes);
        }
        // 批量新增通知模版
        if (CollectionUtils.isNotEmpty(alertVO.getNotifyTemplateList())) {
            List<AlertNotifyTemplate> alertNotifyTemplateList = new ArrayList<>();
            for (NotifyTemplate notifyTemplate : alertVO.getNotifyTemplateList()) {
                AlertNotifyTemplate alertNotifyTemplate = new AlertNotifyTemplate();
                alertNotifyTemplate.setAlertId(alert.getAlertId());
                alertNotifyTemplate.setNotifyTemplateId(notifyTemplate.getId());
                alertNotifyTemplateList.add(alertNotifyTemplate);
            }
            alertMapper.insertAlertNotifyTemplateList(alertNotifyTemplateList);
        }
        return result;
    }

    /**
     * 批量删除设备告警
     *
     * @param alertIds 需要删除的设备告警主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteAlertByAlertIds(Long[] alertIds) {
        // 批量删除告警场景
        alertMapper.deleteAlertSceneByAlertIds(alertIds);
        // 批量删除告警通知模版配置
        alertMapper.deleteAlertNotifyTemplateByAlertIds(alertIds);
        // 删除告警配置
        return alertMapper.deleteBatchIds(Arrays.asList(alertIds));
    }

    /**
     * 删除设备告警信息
     *
     * @param alertId 设备告警主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteAlertByAlertId(Long alertId) {
        // 批量删除告警场景
        alertMapper.deleteAlertSceneByAlertIds(new Long[]{alertId});
        // 批量删除告警通知模版配置
        alertMapper.deleteAlertNotifyTemplateByAlertIds(new Long[]{alertId});
        return alertMapper.deleteById(alertId);
    }

    /**
     * 修改设备告警状态
     *
     * @param alertId 告警id
     * @param status  状态
     * @return 结果
     */
    @Override
    public int editStatus(Long alertId, Integer status) {
        LambdaUpdateWrapper<Alert> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Alert::getAlertId, alertId);
        updateWrapper.set(Alert::getStatus, status);
        return this.update(updateWrapper) ? 1 : 0;
    }

    @Override
    public List<NotifyTemplate> listNotifyTemplateByAlertId(Long alertId) {
        return alertMapper.selectNotifyTemplateListByAlertId(alertId);
    }

    @Override
    public List<AlertNotifyTemplate> listAlertNotifyTemplate(Long alertId) {
        return alertMapper.selectAlertNotifyTemplateList(alertId);
    }

    @Override
    public List<AlertScene> listAlertScene(Long sceneId) {
        return alertMapper.selectAlertSceneListBySceneId(sceneId);
    }

    @Override
    public List<AlertSceneSendVO> listByAlertIds(Long sceneId) {
        return alertMapper.listByAlertIds(sceneId);
    }

    @Override
    public int deleteAlertSceneBySceneIds(Long[] sceneIds) {
        return alertMapper.deleteAlertSceneBySceneIds(sceneIds);
    }

    private LambdaQueryWrapper<Alert> buildQueryWrapper(Alert query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<Alert> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(query.getAlertName()), Alert::getAlertName, query.getAlertName());
        lqw.eq(query.getAlertLevel() != null, Alert::getAlertLevel, query.getAlertLevel());
        lqw.eq(query.getStatus() != null, Alert::getStatus, query.getStatus());
        lqw.eq(StringUtils.isNotBlank(query.getNotify()), Alert::getNotify, query.getNotify());
        lqw.eq(query.getTenantId() != null, Alert::getTenantId, query.getTenantId());
        lqw.like(StringUtils.isNotBlank(query.getTenantName()), Alert::getTenantName, query.getTenantName());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(Alert::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }


}
