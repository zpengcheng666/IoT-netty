package com.sydh.controller.ruleview;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.utils.poi.ExcelUtil;
import com.sydh.iot.service.IRuleElService;
import com.sydh.rule.domain.RuleEl;
import com.sydh.rule.domain.vo.RuleElVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 规则elController
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */
@RestController
@RequestMapping("/rule/el")
@Api(tags = "规则el")
public class RuleElController extends BaseController {
    /** 代码生成区域 可直接覆盖**/
    @Resource
    private IRuleElService ruleElService;

    /**
     * 查询规则el列表
     */
    @PreAuthorize("@ss.hasPermi('rule:el:list')")
    @GetMapping("/list")
    @ApiOperation("查询规则el列表")
    public TableDataInfo list(RuleEl ruleEl) {
        Page<RuleElVO> voPage = ruleElService.pageRuleElVO(ruleEl);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出规则el列表
     */
    @ApiOperation("导出规则el列表")
    @PreAuthorize("@ss.hasPermi('rule:el:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, RuleEl ruleEl) {
        Page<RuleElVO> voPage = ruleElService.pageRuleElVO(ruleEl);
        ExcelUtil<RuleElVO> util = new ExcelUtil<RuleElVO>(RuleElVO.class);
        util.exportExcel(response, voPage.getRecords(), "规则el数据");
    }

    /**
     * 获取规则el详细信息
     */
    @PreAuthorize("@ss.hasPermi('rule:el:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取规则el详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(ruleElService.queryByIdWithCache(id));
    }

    /**
     * 新增规则el
     */
    @PreAuthorize("@ss.hasPermi('rule:el:add')")
    @PostMapping
    @ApiOperation("新增规则el")
    public AjaxResult add(@RequestBody RuleEl ruleEl) {
        return toAjax(ruleElService.insertWithCache(ruleEl));
    }

    /**
     * 修改规则el
     */
    @PreAuthorize("@ss.hasPermi('rule:el:edit')")
    @PutMapping
    @ApiOperation("修改规则el")
    public AjaxResult edit(@RequestBody RuleEl ruleEl) {
        return toAjax(ruleElService.updateWithCache(ruleEl));
    }

    /**
     * 删除规则el
     */
    @PreAuthorize("@ss.hasPermi('rule:el:remove')")
    @DeleteMapping("/{ids}")
    @ApiOperation("删除规则el")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(ruleElService.deleteWithCacheByIds(ids, true));
    }
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/
    @PostMapping("/exec")
    @PreAuthorize("@ss.hasPermi('rule:cmp:edit')")
    public AjaxResult exec(@RequestBody RuleEl ruleEl){
        return toAjax(ruleElService.exec(ruleEl));
    }

    @PostMapping("/publish")
    @PreAuthorize("@ss.hasPermi('rule:cmp:edit')")
    @ApiOperation("规则发布")
    public AjaxResult publish(@RequestBody RuleEl ruleEl){
        return toAjax(ruleElService.publish(ruleEl));
    }

    /** 自定义代码区域 END**/
}
