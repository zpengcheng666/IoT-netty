package com.sydh.iot.convert;

import com.sydh.iot.domain.GoviewProject;
import com.sydh.iot.model.vo.GoviewProjectVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 项目Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */
@Mapper
public interface GoviewProjectConvert
{

    GoviewProjectConvert INSTANCE = Mappers.getMapper(GoviewProjectConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param goviewProject
     * @return 项目集合
     */
    GoviewProjectVO convertGoviewProjectVO(GoviewProject goviewProject);

    /**
     * VO类转换为实体类集合
     *
     * @param goviewProjectVO
     * @return 项目集合
     */
    GoviewProject convertGoviewProject(GoviewProjectVO goviewProjectVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param goviewProjectList
     * @return 项目集合
     */
    List<GoviewProjectVO> convertGoviewProjectVOList(List<GoviewProject> goviewProjectList);

    /**
     * VO类转换为实体类
     *
     * @param goviewProjectVOList
     * @return 项目集合
     */
    List<GoviewProject> convertGoviewProjectList(List<GoviewProjectVO> goviewProjectVOList);
}
