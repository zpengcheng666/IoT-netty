package com.sydh.oss.mapper;

import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.oss.domain.OssDetail;

import java.util.List;

/**
 * 文件记录Mapper接口
 *
 * @author zhuangpeng.li
 * @date 2024-04-19
 */
public interface OssDetailMapper extends BaseMapperX<OssDetail>
{
    /**
     * 查询文件记录列表
     *
     * @param ossDetail 文件记录
     * @return 文件记录集合
     */
    public List<OssDetail> selectOssDetailList(OssDetail ossDetail);
}
