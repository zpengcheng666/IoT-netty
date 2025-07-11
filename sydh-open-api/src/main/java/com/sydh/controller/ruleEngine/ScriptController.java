package com.sydh.controller.ruleEngine;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.annotation.Log;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.enums.BusinessType;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.utils.poi.ExcelUtil;
import com.sydh.iot.domain.Script;
import com.sydh.iot.model.vo.ScriptVO;
import com.sydh.iot.service.IScriptService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 规则引擎脚本Controller
 *
 * @author lizhuangpeng
 * @date 2023-07-01
 */
@RestController
@RequestMapping("/iot/script")
public class ScriptController extends BaseController {
    @Resource
    private IScriptService scriptService;

    /**
     * 查询规则引擎脚本列表
     */
    @PreAuthorize("@ss.hasPermi('iot:script:list')")
    @GetMapping("/list")
    public TableDataInfo list(Script ruleScript) {
        Page<ScriptVO> voPage = scriptService.selectRuleScriptList(ruleScript);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出规则引擎脚本列表
     */
    @PreAuthorize("@ss.hasPermi('iot:script:export')")
    @Log(title = "规则引擎脚本", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Script ruleScript) {
        Page<ScriptVO> voPage = scriptService.selectRuleScriptList(ruleScript);
        ExcelUtil<ScriptVO> util = new ExcelUtil<>(ScriptVO.class);
        util.exportExcel(response, voPage.getRecords(), "规则引擎脚本数据");
    }

    /**
     * 获取规则引擎脚本详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:script:query')")
    @GetMapping(value = "/{scriptId}")
    public AjaxResult getInfo(@PathVariable("scriptId") String scriptId) {
        return success(scriptService.selectRuleScriptById(scriptId));
    }

    /**
     * 新增规则引擎脚本
     */
    @PreAuthorize("@ss.hasPermi('iot:script:add')")
    @Log(title = "规则引擎脚本", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ScriptVO scriptVO) {
        return toAjax(scriptService.insertRuleScript(scriptVO));
    }

    /**
     * 修改规则引擎脚本
     */
    @PreAuthorize("@ss.hasPermi('iot:script:edit')")
    @Log(title = "规则引擎脚本", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ScriptVO scriptVO) {
        return toAjax(scriptService.updateRuleScript(scriptVO));
    }

    /**
     * 获取规则引擎脚本日志
     */
    @PreAuthorize("@ss.hasPermi('iot:script:query')")
    @GetMapping(value = "/log/{scriptId}")
    public AjaxResult getScriptLog(@PathVariable("scriptId") String scriptId) {
        return success(scriptService.selectRuleScriptLog("script", scriptId));
    }


    /**
     * 删除规则引擎脚本
     */
    @PreAuthorize("@ss.hasPermi('iot:script:remove')")
    @Log(title = "规则引擎脚本", businessType = BusinessType.DELETE)
    @DeleteMapping("/{scriptIds}")
    public AjaxResult remove(@PathVariable String[] scriptIds) {
        return toAjax(scriptService.deleteRuleScriptByIds(scriptIds));
    }

    /**
     * 删除规则引擎脚本
     */
    @PostMapping("/validate")
    public AjaxResult validateScript(@RequestBody Script ruleScript) {
        return scriptService.validateScript(ruleScript);
    }
}
