package com.sydh.controller.ruleview;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.utils.poi.ExcelUtil;
import com.sydh.iot.service.IRuleCmpService;
import com.sydh.rule.domain.RuleCmp;
import com.sydh.rule.domain.vo.RuleCmpVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 规则组件Controller
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */
@RestController
@RequestMapping("/rule/cmp")
@Api(tags = "规则组件")
public class RuleCmpController extends BaseController {
    /** 代码生成区域 可直接覆盖**/
    @Resource
    private IRuleCmpService ruleCmpService;

    /**
     * 查询规则组件列表
     */
    @PreAuthorize("@ss.hasPermi('rule:cmp:list')")
    @GetMapping("/list")
    @ApiOperation("查询规则组件列表")
    public TableDataInfo list(RuleCmp ruleCmp) {
        Page<RuleCmpVO> voPage = ruleCmpService.pageRuleCmpVO(ruleCmp);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出规则组件列表
     */
    @ApiOperation("导出规则组件列表")
    @PreAuthorize("@ss.hasPermi('rule:cmp:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, RuleCmp ruleCmp) {
        Page<RuleCmpVO> voPage = ruleCmpService.pageRuleCmpVO(ruleCmp);
        ExcelUtil<RuleCmpVO> util = new ExcelUtil<RuleCmpVO>(RuleCmpVO.class);
        util.exportExcel(response, voPage.getRecords(), "规则组件数据");
    }

    /**
     * 获取规则组件详细信息
     */
    @PreAuthorize("@ss.hasPermi('rule:cmp:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取规则组件详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(ruleCmpService.queryByIdWithCache(id));
    }

    /**
     * 新增规则组件
     */
    @PreAuthorize("@ss.hasPermi('rule:cmp:add')")
    @PostMapping
    @ApiOperation("新增规则组件")
    public AjaxResult add(@RequestBody RuleCmp ruleCmp) {
        return toAjax(ruleCmpService.insertWithCache(ruleCmp));
    }

    /**
     * 修改规则组件
     */
    @PreAuthorize("@ss.hasPermi('rule:cmp:edit')")
    @PutMapping
    @ApiOperation("修改规则组件")
    public AjaxResult edit(@RequestBody RuleCmp ruleCmp) {
        return toAjax(ruleCmpService.updateWithCache(ruleCmp));
    }

    /**
     * 删除规则组件
     */
    @PreAuthorize("@ss.hasPermi('rule:cmp:remove')")
    @DeleteMapping("/{ids}")
    @ApiOperation("删除规则组件")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(ruleCmpService.deleteWithCacheByIds(ids, true));
    }
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/
    @PostMapping("/exec")
    @PreAuthorize("@ss.hasPermi('rule:cmp:edit')")
    public AjaxResult exec(@RequestBody RuleCmp ruleCmp){
        return toAjax(ruleCmpService.exec(ruleCmp));
    }

    @PostMapping("/options")
    @PreAuthorize("@ss.hasPermi('rule:cmp:edit')")
    public AjaxResult options(@RequestBody Map<String,Object> map){
        return toAjax(ruleCmpService.options(map));
    }

    @PostMapping("/cmpOptions")
    @PreAuthorize("@ss.hasPermi('rule:cmp:edit')")
    public AjaxResult cmpOptions(@RequestBody Map<String,Object> map){
        return toAjax(ruleCmpService.cmpOptions(map));
    }
    @PostMapping("/typeOptions")
    @PreAuthorize("@ss.hasPermi('rule:cmp:edit')")
    public AjaxResult typeOptions(@RequestBody Map<String,Object> map){
        return toAjax(ruleCmpService.typeOptions(map));
    }
    @PostMapping("/script/options")
    @PreAuthorize("@ss.hasPermi('rule:cmp:edit')")
    public AjaxResult scriptOptions(@RequestBody Map<String,Object> map){
        return toAjax(ruleCmpService.scriptOptions(map));
    }

    /** 自定义代码区域 END**/
}
