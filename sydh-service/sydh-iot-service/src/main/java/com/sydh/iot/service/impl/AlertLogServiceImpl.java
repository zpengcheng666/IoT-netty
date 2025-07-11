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
import com.sydh.iot.convert.AlertLogConvert;
import com.sydh.iot.domain.AlertLog;
import com.sydh.iot.mapper.AlertLogMapper;
import com.sydh.iot.model.DeviceAlertCount;
import com.sydh.iot.model.param.DataCenterParam;
import com.sydh.iot.model.vo.AlertCountVO;
import com.sydh.iot.model.vo.AlertLogVO;
import com.sydh.iot.service.IAlertLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.sydh.common.extend.utils.SecurityUtils.getLoginUser;


/**
 * 设备告警Service业务层处理
 *
 * @author kerwincui
 * @date 2022-01-13
 */
@Service
public class AlertLogServiceImpl extends ServiceImpl<AlertLogMapper,AlertLog> implements IAlertLogService {

    @Resource
    private AlertLogMapper alertLogMapper;

    /**
     * 查询设备告警
     *
     * @param alertLogId 设备告警主键
     * @return 设备告警
     */
    @Override
    public AlertLog selectAlertLogByAlertLogId(Long alertLogId) {
        return alertLogMapper.selectById(alertLogId);
    }

    /**
     * 查询设备告警日志列表
     *
     * @param alertLogVO 设备告警日志
     * @return 设备告警日志
     */
    @Override
    @DataScope(fieldAlias = DataScopeAspect.DATA_SCOPE_FILED_ALIAS_USER_ID)
    public Page<AlertLogVO> pageAlertLogVO(AlertLogVO alertLogVO) {
        AlertLog alertLog = AlertLogConvert.INSTANCE.convertAlertLog(alertLogVO);
        SysUser user = getLoginUser().getUser();
        LambdaQueryWrapper<AlertLog> lqw = buildQueryWrapper(alertLog);
        // 查询所属机构
        // 兼容组态多租户分享
        if (null != alertLogVO.getDeptUserId()) {
            lqw.eq(AlertLog::getUserId, alertLogVO.getDeptUserId());
        } else {
            if (null != user.getDeptId()) {
                // 数据范围过滤
                if (ObjectUtil.isNotEmpty(alertLogVO.getParams().get(DataScopeAspect.DATA_SCOPE))){
                    lqw.apply((String) alertLogVO.getParams().get(DataScopeAspect.DATA_SCOPE));
                }
            } else {
                lqw.eq(AlertLog::getUserId, alertLogVO.getDeptUserId());
            }
        }
        lqw.orderByDesc(AlertLog::getCreateTime);
        Page<AlertLog> alertLogPage = baseMapper.selectPage(new Page<>(alertLogVO.getPageNum(), alertLogVO.getPageSize()), lqw);
        return AlertLogConvert.INSTANCE.convertAlertLogVOPage(alertLogPage);
    }

    @Override
    public List<AlertLog> selectAlertLogListByCreateBy(String createBy, String remark, Integer status) {
        AlertLog alertLog = new AlertLog();
        alertLog.setCreateBy(createBy);
        alertLog.setStatus(status);
        alertLog.setRemark(remark);
        LambdaQueryWrapper<AlertLog> queryWrapper = this.buildQueryWrapper(alertLog);
        queryWrapper.orderByDesc(AlertLog::getCreateTime);
        return alertLogMapper.selectList(queryWrapper);
    }

    /**
     * 查询设备告警列表
     *
     * @param alertLog 设备告警
     * @return 设备告警
     */
    @Override
    public Long selectAlertLogListCount(AlertLog alertLog) {
        return alertLogMapper.selectAlertLogListCount(alertLog);
    }

    @Override
    public List<DeviceAlertCount> selectDeviceAlertCount(List<String> serialNumberList) {
        return alertLogMapper.selectDeviceAlertCount(serialNumberList);
    }

    @Override
    public DeviceAlertCount selectDeviceAlertCountBySN(String serialNumber) {
        return alertLogMapper.selectDeviceAlertCountBySN(serialNumber);
    }

    @Override
    public List<DeviceAlertCount> selectSceneAlertCount() {
        return alertLogMapper.selectSceneAlertCount();
    }

    @Override
    public DeviceAlertCount selectSceneAlertCountBySceneId(String sceneId) {
        return alertLogMapper.selectSceneAlertCountBySceneId(sceneId);
    }

    /**
     * 新增设备告警
     *
     * @param alertLog 设备告警
     * @return 结果
     */
    @Override
    public int insertAlertLog(AlertLog alertLog) {
        alertLog.setCreateTime(DateUtils.getNowDate());
        return alertLogMapper.insert(alertLog);
    }

    @Override
    public int insertAlertLogBatch(List<AlertLog> alertLogList) {
        return alertLogMapper.insertBatch(alertLogList) ? 1 : 0;
    }

    /**
     * 修改设备告警
     *
     * @param alertLog 设备告警
     * @return 结果
     */
    @Override
    public int updateAlertLog(AlertLog alertLog) {
        alertLog.setUpdateTime(DateUtils.getNowDate());
        if (alertLog.getRemark().length() > 0) {
            // 1=不需要处理,2=未处理,3=已处理
            alertLog.setStatus(3);
        }
        return alertLogMapper.updateById(alertLog);
    }

    @Override
    public int updateAlertLogStatus(AlertLog alertLog) {
        LambdaUpdateWrapper<AlertLog> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(AlertLog::getStatus, alertLog.getStatus());
        updateWrapper.set(AlertLog::getUpdateTime, DateUtils.getNowDate());
        updateWrapper.eq(AlertLog::getSerialNumber, alertLog.getSerialNumber());
        updateWrapper.eq(AlertLog::getSceneId, alertLog.getSceneId());
        return this.update(updateWrapper) ? 1 : 0;
    }

    /**
     * 通过设备编号删除设备告警信息
     *
     * @param SerialNumber 设备告警主键
     * @return 结果
     */
    @Override
    public int deleteAlertLogBySerialNumber(String SerialNumber) {
        LambdaQueryWrapper<AlertLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AlertLog::getSerialNumber, SerialNumber);
        return alertLogMapper.delete(queryWrapper);
    }
    @Override
    public List<AlertCountVO> countAlertProcess(DataCenterParam dataCenterParam) {
        Date beginTime = null;
        Date endTime = null;
        if (dataCenterParam.getBeginTime() != null && dataCenterParam.getBeginTime() != "" && dataCenterParam.getEndTime() != null && dataCenterParam.getEndTime() != "") {
            beginTime = parseTime(dataCenterParam.getBeginTime());
            endTime = parseTime(dataCenterParam.getEndTime());
        }
        return alertLogMapper.countAlertProcess(dataCenterParam, beginTime, endTime);
    }
    private Date parseTime(String time) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return format.parse(time);
        } catch (ParseException e) {
            throw new IllegalArgumentException("时间格式错误: " + time, e);
        }
    }

    @Override
    public List<AlertCountVO> countAlertLevel(DataCenterParam dataCenterParam) {
        Date beginTime = null;
        Date endTime = null;
        if (dataCenterParam.getBeginTime() != null && dataCenterParam.getBeginTime() != "" && dataCenterParam.getEndTime() != null && dataCenterParam.getEndTime() != "") {
            beginTime = parseTime(dataCenterParam.getBeginTime());
            endTime = parseTime(dataCenterParam.getEndTime());
        }
        return alertLogMapper.countAlertLevel(dataCenterParam, beginTime, endTime);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(AlertLog entity){
        //TODO 做一些数据校验,如唯一约束
    }

    private LambdaQueryWrapper<AlertLog> buildQueryWrapper(AlertLog query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<AlertLog> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(query.getAlertName()), AlertLog::getAlertName, query.getAlertName());
        lqw.eq(query.getAlertLevel() != null, AlertLog::getAlertLevel, query.getAlertLevel());
        lqw.eq(query.getStatus() != null, AlertLog::getStatus, query.getStatus());
        lqw.eq(StringUtils.isNotBlank(query.getSerialNumber()), AlertLog::getSerialNumber, query.getSerialNumber());
        lqw.eq(query.getProductId() != null, AlertLog::getProductId, query.getProductId());
        lqw.eq(StringUtils.isNotBlank(query.getDetail()), AlertLog::getDetail, query.getDetail());
        lqw.eq(query.getUserId() != null, AlertLog::getUserId, query.getUserId());
        lqw.like(StringUtils.isNotBlank(query.getDeviceName()), AlertLog::getDeviceName, query.getDeviceName());
        lqw.eq(StringUtils.isNotBlank(query.getRemark()), AlertLog::getRemark, query.getRemark());
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), AlertLog::getCreateBy, query.getCreateBy());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(AlertLog::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }
}
