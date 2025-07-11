package com.sydh.iot.mapper;

import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.iot.domain.GoviewProjectData;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 项目数据关联Mapper接口
 *
 * @author kami
 * @date 2022-10-27
 */
@Repository
public interface GoviewProjectDataMapper extends BaseMapperX<GoviewProjectData>
{

    /**
     * 查询项目数据关联列表
     *
     * @param goviewProjectData 项目数据关联
     * @return 项目数据关联集合
     */
    public List<GoviewProjectData> selectGoviewProjectDataList(GoviewProjectData goviewProjectData);

    /**
     * 根据sql获取组件数据接口
     * @param sql sql
     * @return 组件数据
     */
    List<LinkedHashMap> executeSql(String sql);
}
