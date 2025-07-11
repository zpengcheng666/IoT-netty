package com.sydh.controller.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.core.domin.entity.SysUser;
import com.sydh.common.extend.utils.poi.ExcelUtil;
import com.sydh.system.domain.SysClient;
import com.sydh.system.domain.vo.SysClientVO;
import com.sydh.system.service.ISysClientService;
import com.sydh.system.service.sys.TokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;


/**
 * 系统授权Controller
 *
 * @author zhuangpeng.li
 * @date 2024-07-26
 */
@RestController
@RequestMapping("/system/sysclient")
@Api(tags = "系统授权")
public class SysClientController extends BaseController {
    @Resource
    private ISysClientService sysClientService;
    @Resource
    private TokenService tokenService;

    /**
     * 查询系统授权列表
     */
    @PreAuthorize("@ss.hasPermi('system:sysclient:list')")
    @GetMapping("/list")
    @ApiOperation("查询系统授权列表")
    public TableDataInfo list(SysClient sysClient) {
        Page<SysClientVO> voPage = sysClientService.pageSysClientVO(sysClient);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出系统授权列表
     */
    @ApiOperation("导出系统授权列表")
    @PreAuthorize("@ss.hasPermi('system:sysclient:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysClient sysClient) {
        Page<SysClientVO> voPage = sysClientService.pageSysClientVO(sysClient);
        ExcelUtil<SysClientVO> util = new ExcelUtil<SysClientVO>(SysClientVO.class);
        util.exportExcel(response, voPage.getRecords(), "系统授权数据");
    }

    /**
     * 获取系统授权详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:sysclient:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取系统授权详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(sysClientService.selectSysClientById(id));
    }

    /**
     * 新增系统授权
     */
    @PreAuthorize("@ss.hasPermi('system:sysclient:add')")
    @PostMapping
    @ApiOperation("新增系统授权")
    public AjaxResult add(@RequestBody SysClient sysClient) {
        SysUser user = getLoginUser().getUser();
        sysClient.setCreateBy(user.getCreateBy());
        return toAjax(tokenService.addToken(user, sysClient));
    }

    /**
     * 修改系统授权
     */
    @PreAuthorize("@ss.hasPermi('system:sysclient:edit')")
    @PutMapping
    @ApiOperation("修改系统授权")
    public AjaxResult edit(@RequestBody SysClient sysClient) {
        SysUser user = getLoginUser().getUser();
        sysClient.setUpdateBy(user.getUserName());
        return toAjax(tokenService.updateToken(user, sysClient));
    }

    /**
     * 删除系统授权
     */
    @PreAuthorize("@ss.hasPermi('system:sysclient:remove')")
    @DeleteMapping("/{ids}")
    @ApiOperation("删除系统授权")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(sysClientService.deleteSysClientByIds(ids));
    }
}
