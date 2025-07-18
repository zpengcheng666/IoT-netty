package com.sydh.controller.tool;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.annotation.Log;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.enums.BusinessType;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.utils.poi.ExcelUtil;
import com.sydh.iot.domain.News;
import com.sydh.iot.model.CategoryNews;
import com.sydh.iot.model.vo.NewsVO;
import com.sydh.iot.service.INewsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 新闻资讯Controller
 *
 * @author kerwincui
 * @date 2022-04-09
 */
@Api(tags = "新闻资讯")
@RestController
@RequestMapping("/iot/news")
public class NewsController extends BaseController
{
    @Resource
    private INewsService newsService;

    /**
     * 查询新闻资讯列表
     */
    @PreAuthorize("@ss.hasPermi('iot:news:list')")
    @GetMapping("/list")
    @ApiOperation("新闻分页列表")
    public TableDataInfo list(News news)
    {
        Page<NewsVO> voPage = newsService.pageNewsVO(news);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 查询轮播的新闻资讯
     */
   // @PreAuthorize("@ss.hasPermi('iot:news:list')")
    @GetMapping("/bannerList")
    @ApiOperation("轮播新闻列表")
    public AjaxResult bannerList()
    {
        News news=new News();
        news.setIsBanner(1L);
        news.setStatus(1L);
        List<NewsVO> list = newsService.selectNewsList(news);
        return AjaxResult.success(list);
    }

    /**
     * 查询置顶的新闻资讯
     */
   // @PreAuthorize("@ss.hasPermi('iot:news:list')")
    @GetMapping("/topList")
    @ApiOperation("置顶新闻列表")
    public AjaxResult topList()
    {
        List<CategoryNews> list = newsService.selectTopNewsList();
        return AjaxResult.success(list);
    }

    /**
     * 导出新闻资讯列表
     */
    @PreAuthorize("@ss.hasPermi('iot:news:export')")
    @Log(title = "新闻资讯", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, News news)
    {
        Page<NewsVO> voPage = newsService.pageNewsVO(news);
        ExcelUtil<NewsVO> util = new ExcelUtil<NewsVO>(NewsVO.class);
        util.exportExcel(response, voPage.getRecords(), "新闻资讯数据");
    }

    /**
     * 获取新闻资讯详细信息
     */
//    @PreAuthorize("@ss.hasPermi('iot:news:query')")
    @GetMapping(value = "/{newsId}")
    @ApiOperation("新闻详情")
    public AjaxResult getInfo(@PathVariable("newsId") Long newsId)
    {
        return AjaxResult.success(newsService.selectNewsByNewsId(newsId));
    }

    /**
     * 获取新闻资讯详细信息
     */
    @GetMapping("/getDetail")
    @ApiOperation("新闻详情")
    public AjaxResult getDetail(Long newsId)
    {
        return AjaxResult.success(newsService.selectNewsByNewsId(newsId));
    }

    /**
     * 新增新闻资讯
     */
    @PreAuthorize("@ss.hasPermi('iot:news:add')")
    @Log(title = "新闻资讯", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("添加新闻资讯")
    public AjaxResult add(@RequestBody News news)
    {
        return toAjax(newsService.insertNews(news));
    }

    /**
     * 修改新闻资讯
     */
    @PreAuthorize("@ss.hasPermi('iot:news:edit')")
    @Log(title = "新闻资讯", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改新闻资讯")
    public AjaxResult edit(@RequestBody News news)
    {
        return toAjax(newsService.updateNews(news));
    }

    /**
     * 删除新闻资讯
     */
    @PreAuthorize("@ss.hasPermi('iot:news:remove')")
    @Log(title = "新闻资讯", businessType = BusinessType.DELETE)
	@DeleteMapping("/{newsIds}")
    @ApiOperation("删除新闻资讯")
    public AjaxResult remove(@PathVariable Long[] newsIds)
    {
        return toAjax(newsService.deleteNewsByNewsIds(newsIds));
    }
}
