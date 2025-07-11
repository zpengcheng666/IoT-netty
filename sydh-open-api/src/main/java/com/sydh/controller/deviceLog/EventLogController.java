package com.sydh.controller.deviceLog;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.annotation.Log;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.enums.BusinessType;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.utils.poi.ExcelUtil;
import com.sydh.iot.domain.DeviceLog;
import com.sydh.iot.domain.EventLog;
import com.sydh.iot.service.IEventLogService;
import com.sydh.iot.tsdb.service.ILogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 事件日志Controller
 *
 * @author kerwincui
 * @date 2023-03-28
 */
@Api(tags = "事件日志")
@RestController
@RequestMapping("/iot/event")
public class EventLogController extends BaseController
{
    @Autowired
    private IEventLogService eventLogService;

    @Resource
    private ILogService logService;

    /**
     * 查询事件日志列表
     */
    @ApiOperation("查询事件日志列表")
    @PreAuthorize("@ss.hasPermi('iot:event:list')")
    @GetMapping("/list")
    public TableDataInfo list(DeviceLog eventLog)
    {
        Page<DeviceLog> logPage = logService.selectEventLogList(eventLog);
        return getDataTable(logPage.getRecords(), logPage.getTotal());
    }

    /**
     * 导出事件日志列表
     */
    @ApiOperation("导出事件日志列表")
    @PreAuthorize("@ss.hasPermi('iot:event:export')")
    @Log(title = "事件日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EventLog eventLog)
    {
        List<EventLog> list = eventLogService.selectEventLogList(eventLog).getRecords();
        ExcelUtil<EventLog> util = new ExcelUtil<EventLog>(EventLog.class);
        util.exportExcel(response, list, "事件日志数据");
    }

    /**
     * 获取事件日志详细信息
     */
    @ApiOperation("获取事件日志详细信息")
    @PreAuthorize("@ss.hasPermi('iot:event:query')")
    @GetMapping(value = "/{logId}")
    public AjaxResult getInfo(@PathVariable("logId") Long logId)
    {
        return success(eventLogService.getById(logId));
    }

    /**
     * 新增事件日志
     */
    @ApiOperation("新增事件日志")
    @PreAuthorize("@ss.hasPermi('iot:event:add')")
    @Log(title = "事件日志", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EventLog eventLog)
    {
        return toAjax(eventLogService.save(eventLog));
    }

    /**
     * 修改事件日志
     */
    @ApiOperation("修改事件日志")
    @PreAuthorize("@ss.hasPermi('iot:event:edit')")
    @Log(title = "事件日志", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EventLog eventLog)
    {
        return toAjax(eventLogService.updateById(eventLog));
    }

    /**
     * 删除事件日志
     */
    @ApiOperation("删除事件日志")
    @PreAuthorize("@ss.hasPermi('iot:event:remove')")
    @Log(title = "事件日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{logIds}")
    public AjaxResult remove(@PathVariable Long[] logIds)
    {
        return toAjax(eventLogService.removeById(logIds));
    }
}
