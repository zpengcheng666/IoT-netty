package com.sydh.controller.datacenter;

import com.alibaba.fastjson2.JSONObject;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.utils.MessageUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.iot.model.vo.AlertCountVO;
import com.sydh.iot.model.DeviceHistoryParam;
import com.sydh.iot.model.vo.ThingsModelLogCountVO;
import com.sydh.iot.model.param.DataCenterParam;
import com.sydh.iot.model.scenemodel.SceneHistoryParam;
import com.sydh.iot.service.DataCenterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static com.sydh.common.extend.utils.SecurityUtils.getLoginUser;


/**
 * @author fastb
 * @version 1.0
 * @description: 数据中心控制器
 * @date 2024-06-13 14:09
 */
@Api(tags = "数据中心管理")
@RestController
@RequestMapping("/data/center")
public class DataCenterController {

    @Resource
    private DataCenterService dataCenterService;

    /**
     * 查询设备物模型的历史数据
     * @param deviceHistoryParam 传参
     * @return com.sydh.common.core.domain.AjaxResult
     */
    @ApiOperation("查询设备的历史数据")
    @PreAuthorize("@ss.hasPermi('dataCenter:history:list')")
    @PostMapping("/deviceHistory")
    public AjaxResult deviceHistory(@RequestBody DeviceHistoryParam deviceHistoryParam)
    {
        if (StringUtils.isEmpty(deviceHistoryParam.getSerialNumber())) {
            return AjaxResult.error(MessageUtils.message("please.select.device"));
        }
        if (StringUtils.isNotEmpty(deviceHistoryParam.getSerialNumber())) {
            deviceHistoryParam.setSerialNumber(deviceHistoryParam.getSerialNumber().toUpperCase());
        }
        List<JSONObject> jsonObject = dataCenterService.deviceHistory(deviceHistoryParam);
        return AjaxResult.success(jsonObject);
    }

    @ApiOperation("查询场景变量历史数据")
    @PreAuthorize("@ss.hasPermi('dataCenter:history:list')")
    @GetMapping("/sceneHistory")
    public AjaxResult sceneHistory(SceneHistoryParam sceneHistoryParam)
    {
        if (StringUtils.isNotEmpty(sceneHistoryParam.getSerialNumber())) {
            sceneHistoryParam.setSerialNumber(sceneHistoryParam.getSerialNumber().toUpperCase());
        }
        List<JSONObject> jsonObject = dataCenterService.sceneHistory(sceneHistoryParam);
        return AjaxResult.success(jsonObject);
    }

    /**
     * 统计告警处理信息
     * @param dataCenterParam 传参
     * @return com.sydh.common.core.domain.AjaxResult
     */
    @ApiOperation("统计告警处理信息")
    @PreAuthorize("@ss.hasPermi('dataCenter:analysis:list')")
    @GetMapping("/countAlertProcess")
    public AjaxResult countAlertProcess(DataCenterParam dataCenterParam)
    {
        Long deptUserId = getLoginUser().getUser().getDept().getDeptUserId();
        dataCenterParam.setTenantId(deptUserId);
        List<AlertCountVO> alertCountVO = dataCenterService.countAlertProcess(dataCenterParam);
        return AjaxResult.success(alertCountVO);
    }

    /**
     * 统计告警级别信息
     * @param dataCenterParam 传参
     * @return com.sydh.common.core.domain.AjaxResult
     */
    @ApiOperation("统计告警级别信息")
    @PreAuthorize("@ss.hasPermi('dataCenter:analysis:list')")
    @GetMapping("/countAlertLevel")
    public AjaxResult countAlertLevel(DataCenterParam dataCenterParam)
    {
        Long deptUserId = getLoginUser().getUser().getDept().getDeptUserId();
        dataCenterParam.setTenantId(deptUserId);
        List<AlertCountVO> alertCountVO = dataCenterService.countAlertLevel(dataCenterParam);
        return AjaxResult.success(alertCountVO);
    }

    /**
     * 统计设备物模型指令下发数量
     * @param dataCenterParam 传参
     * @return com.sydh.common.core.domain.AjaxResult
     */
    @ApiOperation("统计设备物模型指令下发数量")
    @PreAuthorize("@ss.hasPermi('dataCenter:analysis:list')")
    @GetMapping("/countThingsModelInvoke")
    public AjaxResult countThingsModelInvoke(DataCenterParam dataCenterParam)
    {
        if (StringUtils.isEmpty(dataCenterParam.getSerialNumber())) {
            return AjaxResult.error(MessageUtils.message("please.incoming.serialNumber"));
        }
        if (StringUtils.isNotEmpty(dataCenterParam.getSerialNumber())) {
            dataCenterParam.setSerialNumber(dataCenterParam.getSerialNumber().toUpperCase());
        }
        List<ThingsModelLogCountVO> list = dataCenterService.countThingsModelInvoke(dataCenterParam);
        return AjaxResult.success(list);
    }

}
