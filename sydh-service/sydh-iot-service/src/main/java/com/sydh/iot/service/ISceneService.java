package com.sydh.iot.service;

import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sydh.iot.domain.Scene;
import com.sydh.iot.model.vo.SceneTerminalUserVO;
import com.sydh.iot.model.vo.SceneVO;
import com.sydh.rule.parser.entity.node.Node;

/**
 * 场景联动Service接口
 *
 * @author kerwincui
 * @date 2022-01-13
 */
public interface ISceneService extends IService<Scene> {
    /**
     * 查询场景联动
     *
     * @param sceneId 场景联动主键
     * @return 场景联动
     */
    public SceneVO selectSceneBySceneId(Long sceneId);

    public Scene selectScene(Long sceneId);

    /**
     * 分页查询
     *
     * @param scene 场景实体类
     * @return
     */
    Page<SceneVO> pageSceneVO(Scene scene);

    /**
     * 查询场景联动列表
     *
     * @param scene 场景联动
     * @return 场景联动集合
     */
    public List<SceneVO> selectSceneVOList(Scene scene);

    /**
     * 新增场景联动
     *
     * @param sceneVO 场景联动
     * @return 结果
     */
    public int insertScene(SceneVO sceneVO);

    public int insertSceneByView(Scene scene, List<Node> triggerList);


    /**
     * 修改场景联动
     *
     * @param sceneVO 场景联动
     * @return 结果
     */
    public int updateScene(SceneVO sceneVO);

    public int updateSceneByView(Scene scene, List<Node> triggerList);

    /**
     * 批量删除场景联动
     *
     * @param sceneIds 需要删除的场景联动主键集合
     * @return 结果
     */
    public int deleteSceneBySceneIds(Long[] sceneIds);

    /**
     * 删除场景联动信息
     *
     * @param sceneId 场景联动主键
     * @return 结果
     */
    public int deleteSceneBySceneId(Long sceneId);

    /**
     * 修改场景联动状态
     *
     * @param scene 场景联动
     * @return int
     */
    int updateStatus(Scene scene);


    /**
     * 查询场景用户信息
     *
     * @param sceneIdSet 场景id
     * @return java.util.List<com.sydh.iot.model.vo.SceneTerminalUserVO>
     */
    List<SceneTerminalUserVO> selectTerminalUserBySceneIds(Set<Long> sceneIdSet);
}
