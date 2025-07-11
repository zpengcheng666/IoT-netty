package com.sydh.controller.tool;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.annotation.Log;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.enums.BusinessType;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.utils.poi.ExcelUtil;
import com.sydh.iot.domain.NewsCategory;
import com.sydh.iot.model.IdAndName;
import com.sydh.iot.model.vo.NewsCategoryVO;
import com.sydh.iot.service.INewsCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 新闻分类Controller
 *
 * @author kerwincui
 * @date 2022-04-09
 */
@Api(tags = "新闻分类")
@RestController
@RequestMapping("/iot/newsCategory")
public class NewsCategoryController extends BaseController
{
    @Resource
    private INewsCategoryService newsCategoryService;

    /**
     * 查询新闻分类列表
     */
    @PreAuthorize("@ss.hasPermi('iot:newsCategory:list')")
    @GetMapping("/list")
    @ApiOperation("新闻分类分页列表")
    public TableDataInfo list(NewsCategory newsCategory)
    {
        Page<NewsCategoryVO> voPage = newsCategoryService.pageNewsCategoryVO(newsCategory);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 查询新闻分类简短列表
     */
    @PreAuthorize("@ss.hasPermi('iot:news:list')")
    @GetMapping("/newsCategoryShortList")
    @ApiOperation("分类简短列表")
    public AjaxResult newsCategoryShortList()
    {
        List<IdAndName> list = newsCategoryService.selectNewsCategoryShortList();
        return AjaxResult.success(list);
    }

    /**
     * 导出新闻分类列表
     */
    @PreAuthorize("@ss.hasPermi('iot:newsCategory:export')")
    @Log(title = "新闻分类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, NewsCategory newsCategory)
    {
        Page<NewsCategoryVO> voPage = newsCategoryService.pageNewsCategoryVO(newsCategory);
        ExcelUtil<NewsCategoryVO> util = new ExcelUtil<NewsCategoryVO>(NewsCategoryVO.class);
        util.exportExcel(response, voPage.getRecords(), "新闻分类数据");
    }

    /**
     * 获取新闻分类详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:newsCategory:query')")
    @GetMapping(value = "/{categoryId}")
    @ApiOperation("新闻分类详情")
    public AjaxResult getInfo(@PathVariable("categoryId") Long categoryId)
    {
        return AjaxResult.success(newsCategoryService.selectNewsCategoryByCategoryId(categoryId));
    }

    /**
     * 新增新闻分类
     */
    @PreAuthorize("@ss.hasPermi('iot:newsCategory:add')")
    @Log(title = "新闻分类", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("添加新闻分类")
    public AjaxResult add(@RequestBody NewsCategory newsCategory)
    {
        return toAjax(newsCategoryService.insertNewsCategory(newsCategory));
    }

    /**
     * 修改新闻分类
     */
    @PreAuthorize("@ss.hasPermi('iot:newsCategory:edit')")
    @Log(title = "新闻分类", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改新闻分类")
    public AjaxResult edit(@RequestBody NewsCategory newsCategory)
    {
        return toAjax(newsCategoryService.updateNewsCategory(newsCategory));
    }

    /**
     * 删除新闻分类
     */
    @PreAuthorize("@ss.hasPermi('iot:newsCategory:remove')")
    @Log(title = "新闻分类", businessType = BusinessType.DELETE)
	@DeleteMapping("/{categoryIds}")
    @ApiOperation("删除新闻分类")
    public AjaxResult remove(@PathVariable Long[] categoryIds)
    {
        return newsCategoryService.deleteNewsCategoryByCategoryIds(categoryIds);
    }
}
