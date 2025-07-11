package com.sydh.web.controller.monitor;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.annotation.Log;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.enums.BusinessType;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.utils.poi.ExcelUtil;
import com.sydh.system.domain.SysLogininfor;
import com.sydh.system.domain.vo.SysLogininforVO;
import com.sydh.system.service.ISysLogininforService;
import com.sydh.system.service.sys.SysPasswordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 系统访问记录
 *
 * @author ruoyi
 */
@Api(tags = "日志管理：登录日志")
@RestController
@RequestMapping("/monitor/logininfor")
public class SysLogininforController extends BaseController
{
    @Resource
    private ISysLogininforService logininforService;

    @Resource
    private SysPasswordService passwordService;

    @ApiOperation("获取列表登录信息")
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysLogininfor logininfor)
    {
        Page<SysLogininforVO> voPage = logininforService.pageSysLogininforVO(logininfor);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    @ApiOperation("导出登录日志列表")
    @Log(title = "登录日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysLogininfor logininfor)
    {
        Page<SysLogininforVO> voPage = logininforService.pageSysLogininforVO(logininfor);
        ExcelUtil<SysLogininforVO> util = new ExcelUtil<SysLogininforVO>(SysLogininforVO.class);
        util.exportExcel(response, voPage.getRecords(), "系统访问记录数据");
    }

    @ApiOperation("批量删除登录日志")
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{infoIds}")
    public AjaxResult remove(@PathVariable Long[] infoIds)
    {
        return toAjax(logininforService.deleteLogininforByIds(infoIds));
    }

    @ApiOperation("清空登录日志信息")
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')")
    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public AjaxResult clean()
    {
        logininforService.cleanLogininfor();
        return success();
    }

    @ApiOperation("账户解锁")
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:unlock')")
    @Log(title = "账户解锁", businessType = BusinessType.OTHER)
    @GetMapping("/unlock/{userName}")
    public AjaxResult unlock(@PathVariable("userName") String userName)
    {
        passwordService.clearLoginRecordCache(userName);
        return success();
    }

    @ApiOperation("获取一周内每天用户统计")
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:list')")
    @GetMapping("/userCount")
    public AjaxResult userCount()
    {
        return toAjax(logininforService.getUserCount());
    }
}
