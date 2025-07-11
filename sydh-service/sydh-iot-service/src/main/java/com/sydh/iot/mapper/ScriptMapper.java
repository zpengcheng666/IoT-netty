package com.sydh.iot.mapper;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.iot.domain.Script;
import com.sydh.iot.model.ScriptCondition;
import com.sydh.iot.model.vo.ScriptVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 规则引擎脚本Mapper接口
 *
 * @author lizhuangpeng
 * @date 2023-07-01
 */
@Repository
public interface ScriptMapper extends BaseMapperX<Script>
{
    /**
     * 查询规则引擎脚本
     *
     * @param scriptId 规则引擎脚本主键
     * @return 规则引擎脚本
     */
    public ScriptVO selectRuleScriptById(String scriptId);

    /**
     * 查询规则引擎脚本列表
     *
     * @param ruleScript 规则引擎脚本
     * @return 规则引擎脚本集合
     */
    public List<ScriptVO> selectRuleScriptList(@Param("ruleScript")  Script ruleScript);

    /**
     * 查询规则引擎脚本列表
     *
     * @param ruleScript 规则引擎脚本
     * @return 规则引擎脚本集合
     */
    public Page<ScriptVO> selectRuleScriptList(Page<ScriptVO> page, @Param("ruleScript") Script ruleScript);
    public List<Script> selectExecRuleScriptList(ScriptCondition ruleScript);

    /**
     * 查询规则引擎脚本标识数组
     *
     * @return 规则引擎脚本数组
     */
    public String[] selectRuleScriptIdArray(ScriptCondition scriptCondition);

    /***
     * 查询scriptId的数量
     * @param scriptId
     * @return
     */
    public int selectRuleScriptIdCount(String scriptId);


    public int insertScriptBridge(@Param("scriptId")String scriptId, @Param("bridgeId")Long bridgeId);
    public int updateScriptBridge(@Param("scriptId")String scriptId, @Param("bridgeId")Long bridgeId);
    public int deleteScriptBridge(String scriptId);
}
