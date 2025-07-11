package com.sydh.iot.service.impl;

import ch.qos.logback.classic.LoggerContext;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sydh.common.annotation.DataScope;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.extend.aspectj.DataScopeAspect;
import com.sydh.common.extend.core.domin.entity.SysUser;
import com.sydh.common.utils.DateUtils;
import com.sydh.iot.convert.ScriptConvert;
import com.sydh.iot.domain.Script;
import com.sydh.iot.mapper.ScriptMapper;
import com.sydh.iot.model.ScriptCondition;
import com.sydh.iot.model.vo.ScriptVO;
import com.sydh.iot.service.IScriptService;
import com.sydh.rule.context.MsgContext;
import com.sydh.rule.core.FlowLogExecutor;
import com.yomahub.liteflow.builder.LiteFlowNodeBuilder;
import com.yomahub.liteflow.builder.el.LiteFlowChainELBuilder;
import com.yomahub.liteflow.flow.FlowBus;
import com.yomahub.liteflow.flow.LiteflowResponse;
import com.yomahub.liteflow.script.ScriptExecutorFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sydh.common.core.domain.AjaxResult.success;
import static com.sydh.common.extend.utils.SecurityUtils.getLoginUser;
import static com.sydh.common.extend.utils.SecurityUtils.getUsername;

/**
 * 规则引擎脚本Service业务层处理
 *
 * @author lizhuangpeng
 * @date 2023-07-01
 */
@Slf4j
@Service
public class ScriptServiceImpl extends ServiceImpl<ScriptMapper, Script> implements IScriptService {

    @Resource
    private ScriptMapper ruleScriptMapper;

    @Resource
    private FlowLogExecutor flowLogExecutor;

    /**
     * 查询规则引擎脚本
     *
     * @param scriptId 规则引擎脚本主键
     * @return 规则引擎脚本
     */
    @Override
    public ScriptVO selectRuleScriptById(String scriptId) {
        return ruleScriptMapper.selectRuleScriptById(scriptId);
    }

    /**
     * 查询规则引擎脚本日志
     *
     * @param id 规则引擎脚本主键
     * @return 规则引擎脚本
     */
    @Override
    public String selectRuleScriptLog(String type, String id) {
        // 获取日志存储路径
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        String path = loggerContext.getProperty("log.path");

        // 倒叙读取500条日志
        try {
            List<String> lines = new ArrayList<>();
            ReversedLinesFileReader reader = new ReversedLinesFileReader(new File(path + "/rule/" + type + ".log"));
            String line = "";
            while ((line = reader.readLine()) != null && lines.size() < 500) {
                String requestId = type + "/" + id;
                if (line.contains(requestId)) {
                    lines.add(line);
                }
            }
            Collections.reverse(lines);
            return String.join("\n", lines);
        } catch (IOException e) {
            return "暂无日志,详情如下：\n" + e.toString();
        }
    }

    /**
     * 查询规则引擎脚本列表
     *
     * @param ruleScript 规则引擎脚本
     * @return 规则引擎脚本
     */
    @Override
    @DataScope(deptAlias = "s", userAlias = "s", fieldAlias = DataScopeAspect.DATA_SCOPE_FILED_ALIAS_USER_ID)
    public Page<ScriptVO> selectRuleScriptList(Script ruleScript) {
        return ruleScriptMapper.selectRuleScriptList(new Page<>(ruleScript.getPageNum(), ruleScript.getPageSize()), ruleScript);
    }

    @Override
    public List<Script> selectExecRuleScriptList(ScriptCondition ruleScript) {
        return ruleScriptMapper.selectExecRuleScriptList(ruleScript);
    }

    /**
     * 查询规则引擎脚本标识数组（设备用户和租户的脚本）
     *
     * @return 规则引擎脚本
     */
    @Override
    public String[] selectRuleScriptIdArray(ScriptCondition scriptCondition) {
        return ruleScriptMapper.selectRuleScriptIdArray(scriptCondition);
    }

    /**
     * 新增规则引擎脚本
     *
     * @param scriptVO 规则引擎脚本
     * @return 结果
     */
    @Override
    public int insertRuleScript(ScriptVO scriptVO) {
        // 脚本中引用包替换为许可的包
        scriptVO.setScriptData(replaceAllowPackage(scriptVO.getScriptData()));
        // 设置脚本标识,D=数据流，A=执行动作，T=触发器,雪花算法生成唯一数
        Snowflake snowflake = IdUtil.createSnowflake(1, 1);
        scriptVO.setScriptId("D" + snowflake.nextId());
        SysUser sysUser = getLoginUser().getUser();
        // 归属为机构管理员
        if (null != sysUser.getDeptId()) {
            scriptVO.setUserId(sysUser.getDept().getDeptUserId());
            scriptVO.setUserName(sysUser.getDept().getDeptName());
        } else {
            scriptVO.setUserId(sysUser.getUserId());
            scriptVO.setUserName(sysUser.getUserName());
        }
        scriptVO.setCreateBy(sysUser.getUserName());
        scriptVO.setCreateTime(DateUtils.getNowDate());
        Script script = ScriptConvert.INSTANCE.convertScript(scriptVO);
        int result = ruleScriptMapper.insert(script);
        // 动态刷新脚本
        if (result == 1) {
            LiteFlowNodeBuilder builder = null;
            if (scriptVO.getScriptType().equals("script")) {
                builder = LiteFlowNodeBuilder.createScriptNode();
            } else if (scriptVO.getScriptType().equals("switch_script")) {
                builder = LiteFlowNodeBuilder.createScriptSwitchNode();
            } else if (scriptVO.getScriptType().equals("boolean_script")) {
                builder = LiteFlowNodeBuilder.createScriptBooleanNode();
            } else if (scriptVO.getScriptType().equals("for_script")) {
                builder = LiteFlowNodeBuilder.createScriptForNode();
            }
            if (builder != null) {
                builder.setId(scriptVO.getScriptId())
                        .setName(scriptVO.getScriptName())
                        .setScript(scriptVO.getScriptData())
                        .build();
            }
        }
        if ((scriptVO.getScriptEvent() == 5 || scriptVO.getScriptEvent() == 6) && scriptVO.getBridgeId() != 0) {
            ruleScriptMapper.insertScriptBridge(scriptVO.getScriptId(), scriptVO.getBridgeId());
        }
        return result;
    }

    /**
     * 脚本中引用包替换为许可的包
     *
     * @return
     */
    private String replaceAllowPackage(String scriptData) {
        String header = "import cn.hutool.json.JSONArray;\n" +
                "import cn.hutool.json.JSONObject;\n" +
                "import cn.hutool.json.JSONUtil;\n" +
                "import cn.hutool.core.util.NumberUtil;\n";
        // 正则替换import为许可的引用包
        String pattern = "import.*[;\\n\\s]";
        String data = scriptData.replaceAll(pattern, "");
        return header + data;
    }

    /**
     * 修改规则引擎脚本
     *
     * @param scriptVO 规则引擎脚本
     * @return 结果
     */
    @Override
    public int updateRuleScript(ScriptVO scriptVO) {
        // 脚本中引用包替换为许可的包
        scriptVO.setScriptData(replaceAllowPackage(scriptVO.getScriptData()));
        scriptVO.setUpdateTime(DateUtils.getNowDate());
        if ((scriptVO.getScriptEvent() == 5 || scriptVO.getScriptEvent() == 6) && scriptVO.getBridgeId() != 0) {
            ruleScriptMapper.updateScriptBridge(scriptVO.getScriptId(), scriptVO.getBridgeId());
        }
        Script script = ScriptConvert.INSTANCE.convertScript(scriptVO);
        script.setUpdateBy(getUsername());
        int result = ruleScriptMapper.updateById(script);
        // 动态刷新脚本
        if (result == 1) {
            FlowBus.reloadScript(scriptVO.getScriptId(), scriptVO.getScriptData());
        }
        return result;
    }

    /**
     * 批量删除规则引擎脚本
     *
     * @param ids 需要删除的规则引擎脚本主键
     * @return 结果
     */
    @Override
    public int deleteRuleScriptByIds(String[] ids) {
        for (String id : ids) {
            FlowBus.unloadScriptNode(id);
            ruleScriptMapper.deleteScriptBridge(id);
        }
        return ruleScriptMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /**
     * 删除规则引擎脚本信息
     *
     * @param id 规则引擎脚本主键
     * @return 结果
     */
    @Override
    public int deleteRuleScriptById(String id) {
        FlowBus.unloadScriptNode(id);
        ruleScriptMapper.deleteScriptBridge(id);
        return ruleScriptMapper.deleteById(id);
    }

    /**
     * 验证脚本
     * ruleScript.scriptData 脚本数据
     *
     * @return
     */
    @Override
    public AjaxResult validateScript(Script ruleScript) {
        // 检查安全性检查
        String pattern = ".*while|for\\s*\\(|InputStream|OutputStream|Reader|Writer|File|Socket.*";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(ruleScript.getScriptData());
        if (m.find()) {
            return success("验证失败，错误信息：" + "不能包含关键词for、while、Reader、Write、File、Socket等", false);
        }
        // 热刷新脚本
        try {
            ScriptExecutorFactory.loadInstance().getScriptExecutor("groovy").load("validateScript", ruleScript.getScriptData());
        } catch (Exception e) {
            return success("验证失败，错误信息：" + e.getMessage(), false);
        }
        return success("验证成功，脚本的实际执行情况可以查看后端日志文件", true);
    }

    @Override
    public int deleteRuleScriptBySceneIds(Long[] sceneIds) {
        for (Long id : sceneIds) {
            Script script = new Script();
            script.setSceneId(id);
            List<ScriptVO> list = ruleScriptMapper.selectRuleScriptList(script);
            list.forEach(item -> {
                FlowBus.unloadScriptNode(item.getScriptId());
            });
        }
        LambdaQueryWrapper<Script> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Script::getSceneId, Arrays.asList(sceneIds));
        return ruleScriptMapper.delete(queryWrapper);
    }

    @Override
    public int insertRuleScriptList(List<Script> ruleScriptList) {
        return ruleScriptMapper.insertBatch(ruleScriptList) ? 1 : 0;
    }

    @Override
    public MsgContext execRuleScript(ScriptCondition scriptCondition, Object... contextBeanArray) {
        List<Script> scripts = selectExecRuleScriptList(scriptCondition);
        //如果查询不到脚本，则认为是不用处理
        if (Objects.isNull(scripts) || scripts.isEmpty()) {
            return null;
        }
        LiteflowResponse response = null;
        for (Script script : scripts) {
            String el;
            String eChainName = "dataChain_" + script.getScriptId();
            String requestId = "script/" + script.getScriptId();
            if (script.getScriptAction() == 3) {
                el = "THEN(" + script.getScriptId() + ",httpBridge" + ")";
            } else if (script.getScriptAction() == 4) {
                el = "THEN(" + script.getScriptId() + ",mqttBridge" + ")";
            } else if (script.getScriptAction() == 5) {
                el = "THEN(" + script.getScriptId() + ",databaseBridge" + ")";
            } else {
                el = "THEN(" + script.getScriptId() + ")";
            }
            LiteFlowChainELBuilder.createChain().setChainName(eChainName).setEL(el).build();
            // 执行规则脚本
            response = flowLogExecutor.execute2RespWithRid(eChainName, null, requestId, contextBeanArray);

//            if (response.isSuccess() && script.getScriptAction() == 1) {
//                MsgContext cxt = response.getContextBean(MsgContext.class);
//                mqttClient.publish(0, false, cxt.getTopic(), cxt.getPayload());
//            }
        }
        return response.getContextBean(MsgContext.class);
    }
}
