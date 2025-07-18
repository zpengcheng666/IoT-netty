package com.sydh.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sydh.iot.domain.News;
import com.sydh.iot.model.CategoryNews;
import com.sydh.iot.model.vo.NewsVO;

/**
 * 新闻资讯Service接口
 *
 * @author kerwincui
 * @date 2022-04-09
 */
public interface INewsService extends IService<News>
{
    /**
     * 查询新闻资讯
     *
     * @param newsId 新闻资讯主键
     * @return 新闻资讯
     */
    public News selectNewsByNewsId(Long newsId);

    /**
     * 查询新闻资讯列表
     *
     * @param news 新闻资讯
     * @return 新闻资讯集合
     */
    public List<NewsVO> selectNewsList(News news);

    /**
     * 查询置顶新闻资讯列表
     *
     * @return 新闻资讯集合
     */
    public List<CategoryNews> selectTopNewsList();

    /**
     * 新增新闻资讯
     *
     * @param news 新闻资讯
     * @return 结果
     */
    public int insertNews(News news);

    /**
     * 修改新闻资讯
     *
     * @param news 新闻资讯
     * @return 结果
     */
    public int updateNews(News news);

    /**
     * 批量删除新闻资讯
     *
     * @param newsIds 需要删除的新闻资讯主键集合
     * @return 结果
     */
    public int deleteNewsByNewsIds(Long[] newsIds);

    /**
     * 删除新闻资讯信息
     *
     * @param newsId 新闻资讯主键
     * @return 结果
     */
    public int deleteNewsByNewsId(Long newsId);

    /**
     * 查询新闻资讯列表
     *
     * @param news 新闻资讯
     * @return 新闻资讯分页集合
     */
    Page<NewsVO> pageNewsVO(News news);
}
