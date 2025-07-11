package com.sydh.quartz.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.annotation.Log;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.enums.BusinessType;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.utils.poi.ExcelUtil;
import com.sydh.quartz.domain.SysJobLog;
import com.sydh.quartz.service.ISysJobLogService;
import com.sydh.quartz.vo.SysJobLogVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 调度日志操作处理
 *
 * @author ruoyi
 */
@Api(tags = "调度日志操作处理")
@RestController
@RequestMapping("/monitor/jobLog")
public class SysJobLogController extends BaseController
{
    @Resource
    private ISysJobLogService jobLogService;

    /**
     * 查询定时任务调度日志列表
     */
    @ApiOperation("查询定时任务调度日志列表")
    @PreAuthorize("@ss.hasPermi('monitor:job:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysJobLog sysJobLog)
    {
        Page<SysJobLogVO> voPage = jobLogService.pageSysJobLogVO(sysJobLog);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出定时任务调度日志列表
     */
    @ApiOperation("导出定时任务调度日志列表")
    @PreAuthorize("@ss.hasPermi('monitor:job:export')")
    @Log(title = "任务调度日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysJobLog sysJobLog)
    {
        Page<SysJobLogVO> voPage = jobLogService.pageSysJobLogVO(sysJobLog);
        ExcelUtil<SysJobLogVO> util = new ExcelUtil<SysJobLogVO>(SysJobLogVO.class);
        util.exportExcel(response, voPage.getRecords(), "定时任务调度日志数据");
    }

    /**
     * 根据调度编号获取详细信息
     */
    @ApiOperation("根据调度编号获取详细信息")
    @PreAuthorize("@ss.hasPermi('monitor:job:query')")
    @GetMapping(value = "/{jobLogId}")
    public AjaxResult getInfo(@PathVariable Long jobLogId)
    {
        return success(jobLogService.selectJobLogById(jobLogId));
    }


    /**
     * 删除定时任务调度日志
     */
    @ApiOperation("删除定时任务调度日志")
    @PreAuthorize("@ss.hasPermi('monitor:job:remove')")
    @Log(title = "定时任务调度日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{jobLogIds}")
    public AjaxResult remove(@PathVariable Long[] jobLogIds)
    {
        return toAjax(jobLogService.deleteJobLogByIds(jobLogIds));
    }

    /**
     * 清空定时任务调度日志
     */
    @ApiOperation("清空定时任务调度日志")
    @PreAuthorize("@ss.hasPermi('monitor:job:remove')")
    @Log(title = "调度日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public AjaxResult clean()
    {
        jobLogService.cleanJobLog();
        return success();
    }
}
