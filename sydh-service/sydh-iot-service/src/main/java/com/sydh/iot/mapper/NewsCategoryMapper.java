package com.sydh.iot.mapper;

import java.util.List;

import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.iot.domain.NewsCategory;
import com.sydh.iot.model.IdAndName;
import org.springframework.stereotype.Repository;

/**
 * 新闻分类Mapper接口
 *
 * @author kerwincui
 * @date 2022-04-09
 */
@Repository
public interface NewsCategoryMapper extends BaseMapperX<NewsCategory>
{

    /**
     * 查询新闻分类简短列表
     *
     * @return 新闻分类集合
     */
    public List<IdAndName> selectNewsCategoryShortList();

    /**
     * 分类下的新闻数量
     *
     * @param categoryIds 需要删除的数据主键集合
     * @return 结果
     */
    public int newsCountInCategorys(Long[] categoryIds);
}
