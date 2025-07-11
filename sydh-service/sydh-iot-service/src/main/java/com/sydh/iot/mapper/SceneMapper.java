package com.sydh.iot.mapper;

import java.util.List;
import java.util.Set;

import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.iot.domain.Scene;
import com.sydh.iot.model.vo.SceneTerminalUserVO;
import org.springframework.stereotype.Repository;

/**
 * 场景联动Mapper接口
 *
 * @author kerwincui
 * @date 2022-01-13
 */
@Repository
public interface SceneMapper extends BaseMapperX<Scene>
{

    /**
     * 查询场景用户信息
     * @param sceneIdSet 场景id
     * @return java.util.List<com.sydh.iot.model.vo.SceneTerminalUserVO>
     */
    List<SceneTerminalUserVO> selectTerminalUserBySceneIds(Set<Long> sceneIdSet);
}
