package com.sydh.iot.convert;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.iot.domain.SceneModelData;
import com.sydh.iot.model.vo.SceneModelDataVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * sceneModelDataConvert转换类
 *
 * @author sydh
 * @date 2024-12-27
 */
@Mapper
public interface SceneModelDataConvert
{
    /** 代码生成区域 可直接覆盖**/
    SceneModelDataConvert INSTANCE = Mappers.getMapper(SceneModelDataConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param sceneModelData
     * @return sceneModelData集合
     */
    SceneModelDataVO convertSceneModelDataVO(SceneModelData sceneModelData);

    /**
     * VO类转换为实体类集合
     *
     * @param sceneModelDataVO
     * @return sceneModelData集合
     */
    SceneModelData convertSceneModelData(SceneModelDataVO sceneModelDataVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param sceneModelDataList
     * @return sceneModelData集合
     */
    List<SceneModelDataVO> convertSceneModelDataVOList(List<SceneModelData> sceneModelDataList);

    /**
     * VO类转换为实体类
     *
     * @param sceneModelDataVOList
     * @return sceneModelData集合
     */
    List<SceneModelData> convertSceneModelDataList(List<SceneModelDataVO> sceneModelDataVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param sceneModelDataPage
     * @return sceneModelData分页
     */
    Page<SceneModelDataVO> convertSceneModelDataVOPage(Page<SceneModelData> sceneModelDataPage);

    /**
     * VO类转换为实体类
     *
     * @param sceneModelDataVOPage
     * @return sceneModelData分页
     */
    Page<SceneModelData> convertSceneModelDataPage(Page<SceneModelDataVO> sceneModelDataVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
