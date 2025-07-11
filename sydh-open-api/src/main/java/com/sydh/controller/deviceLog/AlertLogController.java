package com.sydh.controller.deviceLog;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.annotation.Log;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.enums.BusinessType;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.utils.poi.ExcelUtil;
import com.sydh.iot.domain.AlertLog;
import com.sydh.iot.model.vo.AlertLogVO;
import com.sydh.iot.service.IAlertLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * 设备告警Controller
 *
 * @author kerwincui
 * @date 2022-01-13
 */
@Api(tags = "设备告警alertLog模块")
@RestController
@RequestMapping("/iot/alertLog")
public class AlertLogController extends BaseController
{
    @Resource
    private IAlertLogService alertLogService;

    /**
     * 查询设备告警列表
     */
    @ApiOperation("查询设备告警列表")
    @PreAuthorize("@ss.hasPermi('iot:alertLog:list')")
    @GetMapping("/list")
    public TableDataInfo list(AlertLogVO alertLogVO)
    {
        Page<AlertLogVO> voPage = alertLogService.pageAlertLogVO(alertLogVO);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出设备告警列表
     */
    @ApiOperation("导出设备告警列表")
    @PreAuthorize("@ss.hasPermi('iot:alertLog:export')")
    @Log(title = "设备告警", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AlertLogVO alertLogVO)
    {
        Page<AlertLogVO> voPage = alertLogService.pageAlertLogVO(alertLogVO);
        ExcelUtil<AlertLogVO> util = new ExcelUtil<AlertLogVO>(AlertLogVO.class);
        util.exportExcel(response, voPage.getRecords(), "设备告警日志数据");
    }

    /**
     * 获取设备告警详细信息
     */
    @ApiOperation("获取设备告警详细信息")
    @PreAuthorize("@ss.hasPermi('iot:alertLog:query')")
    @GetMapping(value = "/{alertLogId}")
    public AjaxResult getInfo(@PathVariable("alertLogId") Long alertLogId)
    {
        return AjaxResult.success(alertLogService.selectAlertLogByAlertLogId(alertLogId));
    }

    /**
     * 新增设备告警
     */
    @ApiOperation("新增设备告警")
    @PreAuthorize("@ss.hasPermi('iot:alertLog:add')")
    @Log(title = "设备告警", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AlertLog alertLog)
    {
        return toAjax(alertLogService.insertAlertLog(alertLog));
    }

    /**
     * 修改设备告警
     */
    @ApiOperation("修改设备告警")
    @PreAuthorize("@ss.hasPermi('iot:alertLog:edit')")
    @Log(title = "设备告警", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AlertLog alertLog)
    {
        alertLog.setUpdateBy(getUsername());
        return toAjax(alertLogService.updateAlertLog(alertLog));
    }

    /**
     * 修改设备告警
     */
    @ApiOperation("修改设备告警")
    @PreAuthorize("@ss.hasPermi('iot:alertLog:remove')")
    @Log(title = "设备告警", businessType = BusinessType.DELETE)
	@DeleteMapping("/{alertLogIds}")
    public AjaxResult remove(@PathVariable Long[] alertLogIds)
    {
        return toAjax(alertLogService.removeBatchByIds(Arrays.asList(alertLogIds)));
    }
}
