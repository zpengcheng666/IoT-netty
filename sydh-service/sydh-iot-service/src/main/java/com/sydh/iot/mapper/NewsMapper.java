package com.sydh.iot.mapper;

import java.util.List;

import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.iot.domain.News;
import org.springframework.stereotype.Repository;

/**
 * 新闻资讯Mapper接口
 *
 * @author kerwincui
 * @date 2022-04-09
 */
@Repository
public interface NewsMapper extends BaseMapperX<News>
{

    /**
     * 查询置顶新闻资讯列表
     *
     * @return 新闻资讯集合
     */
    public List<News> selectTopNewsList();

}
