package com.sydh.oss.mapper;

import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.oss.domain.OssConfig;

import java.util.List;

/**
 * 对象存储配置Mapper接口
 *
 * @author zhuangpeng.li
 * @date 2024-04-19
 */
public interface OssConfigMapper extends BaseMapperX<OssConfig>
{

    /**
     * 查询对象存储配置列表
     *
     * @param ossConfig 对象存储配置
     * @return 对象存储配置集合
     */
    public List<OssConfig> selectOssConfigList(OssConfig ossConfig);

    public int resetConfigStatus();
}
