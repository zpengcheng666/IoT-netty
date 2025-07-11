package com.sydh.controller.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.annotation.Log;
import com.sydh.common.constant.UserConstants;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.enums.BusinessType;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.utils.poi.ExcelUtil;
import com.sydh.common.utils.MessageUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.system.domain.SysConfig;
import com.sydh.system.domain.vo.SysConfigVO;
import com.sydh.system.service.ISysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 参数配置 信息操作处理
 *
 * @author ruoyi
 */
@Api(tags = "参数设置")
@RestController
@RequestMapping("/system/config")
public class SysConfigController extends BaseController
{
    @Resource
    private ISysConfigService configService;

    /**
     * 获取参数配置列表
     */
    @ApiOperation("获取参数配置列表")
    @PreAuthorize("@ss.hasPermi('system:config:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysConfig config)
    {
        Page<SysConfigVO> voPage = configService.pageSysConfigVO(config);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    @ApiOperation("导出参数配置列表")
    @Log(title = "参数管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:config:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysConfig config)
    {
        Page<SysConfigVO> voPage = configService.pageSysConfigVO(config);
        ExcelUtil<SysConfigVO> util = new ExcelUtil<SysConfigVO>(SysConfigVO.class);
        util.exportExcel(response, voPage.getRecords(), "参数配置数据");
    }

    /**
     * 根据参数编号获取详细信息
     */
    @ApiOperation("根据参数编号获取详细信息")
    @PreAuthorize("@ss.hasPermi('system:config:query')")
    @GetMapping(value = "/{configId}")
    public AjaxResult getInfo(@PathVariable Long configId)
    {
        return success(configService.selectConfigById(configId));
    }

    /**
     * 根据参数键名查询参数值
     */
    @ApiOperation("根据参数键名查询参数值")
    @GetMapping(value = "/configKey/{configKey}")
    public AjaxResult getConfigKey(@PathVariable String configKey)
    {
        if (!"sys.logo.config".equals(configKey)) {
            getLoginUser();
        }
        return success(configService.selectConfigByKey(configKey));
    }

    /**
     * 新增参数配置
     */
    @ApiOperation("新增参数配置")
    @PreAuthorize("@ss.hasPermi('system:config:add')")
    @Log(title = "参数管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysConfig config)
    {
        if (UserConstants.NOT_UNIQUE.equals(configService.checkConfigKeyUnique(config)))
        {
            return error(StringUtils.format(MessageUtils.message("sysConfig.add.param.fail.name.exist"), config.getConfigName()));
        }
        config.setCreateBy(getUsername());
        return toAjax(configService.insertConfig(config));
    }

    /**
     * 修改参数配置
     */
    @ApiOperation("修改参数配置")
    @PreAuthorize("@ss.hasPermi('system:config:edit')")
    @Log(title = "参数管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysConfig config)
    {
        if (UserConstants.NOT_UNIQUE.equals(configService.checkConfigKeyUnique(config)))
        {
            return error(StringUtils.format(MessageUtils.message("sysConfig.update.param.fail.name.exist"), config.getConfigName()));
        }
        config.setUpdateBy(getUsername());
        return toAjax(configService.updateConfig(config));
    }

    /**
     * 删除参数配置
     */
    @ApiOperation("批量删除参数配置")
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    public AjaxResult remove(@PathVariable Long[] configIds)
    {
        configService.deleteConfigByIds(configIds);
        return success();
    }

    /**
     * 刷新参数缓存
     */
    @ApiOperation("刷新参数缓存")
    @PreAuthorize("@ss.hasPermi('system:config:refresh')")
    @Log(title = "参数管理", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public AjaxResult refreshCache()
    {
        configService.resetConfigCache();
        return success();
    }
}
