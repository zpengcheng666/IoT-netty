package com.sydh.iot.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.iot.domain.ThingsModelTemplate;
import com.sydh.iot.model.vo.ThingsModelTemplateVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 通用物模型Mapper接口
 *
 * @author kerwincui
 * @date 2021-12-16
 */
@Repository
public interface ThingsModelTemplateMapper extends BaseMapperX<ThingsModelTemplate>
{
    /**
     * 查询通用物模型
     *
     * @param templateId 通用物模型主键
     * @param language 语言
     * @return 通用物模型
     */
    public ThingsModelTemplate selectThingsModelTemplateByTemplateId(@Param("templateId") Long templateId, @Param("language") String language);

    /**
     * 根据id数组查询通用物模型集合
     * @param templateIds
     * @param language 语言
     * @return
     */
    public List<ThingsModelTemplate> selectThingsModelTemplateByTemplateIds (@Param("templateIds") Long[] templateIds, @Param("language") String language);

    /**
     * 查询通用物模型列表
     *
     * @param thingsModelTemplateVO 通用物模型
     * @return 通用物模型集合
     */
    public List<ThingsModelTemplate> selectThingsModelTemplateList(@Param("thingsModelTemplateVO") ThingsModelTemplateVO thingsModelTemplateVO);

    /**
     * 查询通用物模型列表
     *
     * @param thingsModelTemplateVO 通用物模型
     * @return 通用物模型集合
     */
    public Page<ThingsModelTemplate> selectThingsModelTemplateList(Page<ThingsModelTemplate> page, @Param("thingsModelTemplateVO") ThingsModelTemplateVO thingsModelTemplateVO);

}
