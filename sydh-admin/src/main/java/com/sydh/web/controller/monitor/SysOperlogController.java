package com.sydh.web.controller.monitor;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.annotation.Log;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.enums.BusinessType;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.utils.poi.ExcelUtil;
import com.sydh.system.domain.vo.SysOperLogVO;
import com.sydh.system.service.ISysOperLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 操作日志记录
 *
 * @author ruoyi
 */
@Api(tags = "日志管理：操作日志")
@RestController
@RequestMapping("/monitor/operlog")
public class SysOperlogController extends BaseController
{
    @Resource
    private ISysOperLogService operLogService;

    @ApiOperation("获取操作日志列表")
    @PreAuthorize("@ss.hasPermi('monitor:operlog:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysOperLogVO operLog)
    {
        Page<SysOperLogVO> voPage = operLogService.pageSysOperLogVO(operLog);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    @ApiOperation("导出操作日志")
    @Log(title = "操作日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('monitor:operlog:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysOperLogVO operLog)
    {
        Page<SysOperLogVO> voPage = operLogService.pageSysOperLogVO(operLog);
        ExcelUtil<SysOperLogVO> util = new ExcelUtil<SysOperLogVO>(SysOperLogVO.class);
        util.exportExcel(response, voPage.getRecords(), "操作日志记录数据");
    }

    @ApiOperation("批量删除操作日志")
    @Log(title = "操作日志", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('monitor:operlog:remove')")
    @DeleteMapping("/{operIds}")
    public AjaxResult remove(@PathVariable Long[] operIds)
    {
        return toAjax(operLogService.deleteOperLogByIds(operIds));
    }

    @ApiOperation("清空操作日志")
    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    @PreAuthorize("@ss.hasPermi('monitor:operlog:remove')")
    @DeleteMapping("/clean")
    public AjaxResult clean()
    {
        operLogService.cleanOperLog();
        return success();
    }
}
