package com.sydh.iot.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sydh.common.annotation.DataScope;
import com.sydh.common.exception.ServiceException;
import com.sydh.common.extend.aspectj.DataScopeAspect;
import com.sydh.common.extend.core.domin.entity.SysUser;
import com.sydh.common.utils.DateUtils;
import com.sydh.common.utils.MessageUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.iot.domain.Scene;
import com.sydh.iot.service.IRuleElService;
import com.sydh.iot.service.ISceneService;
import com.sydh.rule.convert.RuleElConvert;
import com.sydh.rule.domain.RuleEl;
import com.sydh.rule.domain.vo.RuleElVO;
import com.sydh.rule.mapper.RuleElMapper;
import com.sydh.rule.parser.entity.node.Node;
import com.sydh.rule.parser.enums.RuleEnums;
import com.sydh.rule.parser.execption.LiteFlowELException;
import com.sydh.rule.parser.graph.Graph;
import com.sydh.rule.parser.graph.GraphInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.sydh.common.extend.utils.SecurityUtils.getLoginUser;

/**
 * 规则elService业务层处理
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */
@Slf4j
@Service
public class RuleElServiceImpl extends ServiceImpl<RuleElMapper, RuleEl> implements IRuleElService {

    @Resource
    private ISceneService sceneService;

    /** 代码生成区域 可直接覆盖**/
    /**
     * 查询规则el
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     *
     * @param id 主键
     * @return 规则el
     */
    @Override
    @Cacheable(cacheNames = "RuleEl", key = "#id")
    public RuleEl queryByIdWithCache(Long id) {
        return this.getById(id);
    }

    /**
     * 查询规则el
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     *
     * @param id 主键
     * @return 规则el
     */
    @Override
    @Cacheable(cacheNames = "RuleEl", key = "#id")
    public RuleEl selectRuleElById(Long id) {
        return this.getById(id);
    }

    /**
     * 查询规则el分页列表
     *
     * @param ruleEl 规则el
     * @return 规则el
     */
    @Override
    @DataScope()
    public Page<RuleElVO> pageRuleElVO(RuleEl ruleEl) {
        LambdaQueryWrapper<RuleEl> lqw = buildQueryWrapper(ruleEl);
        Page<RuleEl> ruleElPage = baseMapper.selectPage(new Page<>(ruleEl.getPageNum(), ruleEl.getPageSize()), lqw);
        return RuleElConvert.INSTANCE.convertRuleElVOPage(ruleElPage);
    }

    /**
     * 查询规则el列表
     *
     * @param ruleEl 规则el
     * @return 规则el
     */
    @Override
    public List<RuleElVO> listRuleElVO(RuleEl ruleEl) {
        LambdaQueryWrapper<RuleEl> lqw = buildQueryWrapper(ruleEl);
        List<RuleEl> ruleElList = baseMapper.selectList(lqw);
        return RuleElConvert.INSTANCE.convertRuleElVOList(ruleElList);
    }

    private LambdaQueryWrapper<RuleEl> buildQueryWrapper(RuleEl query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<RuleEl> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getTenantId() != null, RuleEl::getTenantId, query.getTenantId());
        lqw.eq(query.getId() != null, RuleEl::getId, query.getId());
        lqw.eq(StringUtils.isNotBlank(query.getElId()), RuleEl::getElId, query.getElId());
        lqw.like(StringUtils.isNotBlank(query.getElName()), RuleEl::getElName, query.getElName());
        lqw.eq(StringUtils.isNotBlank(query.getEl()), RuleEl::getEl, query.getEl());
        lqw.eq(StringUtils.isNotBlank(query.getFlowJson()), RuleEl::getFlowJson, query.getFlowJson());
        lqw.eq(StringUtils.isNotBlank(query.getSourceJson()), RuleEl::getSourceJson, query.getSourceJson());
        lqw.eq(query.getExecutorId() != null, RuleEl::getExecutorId, query.getExecutorId());
        lqw.eq(query.getSceneId() != null, RuleEl::getSceneId, query.getSceneId());
        lqw.eq(query.getEnable() != null, RuleEl::getEnable, query.getEnable());
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), RuleEl::getCreateBy, query.getCreateBy());
        lqw.eq(query.getCreateTime() != null, RuleEl::getCreateTime, query.getCreateTime());
        lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), RuleEl::getUpdateBy, query.getUpdateBy());
        lqw.eq(query.getUpdateTime() != null, RuleEl::getUpdateTime, query.getUpdateTime());
        lqw.eq(query.getDelFlag() != null, RuleEl::getDelFlag, query.getDelFlag());
        lqw.eq(StringUtils.isNotBlank(query.getRemark()), RuleEl::getRemark, query.getRemark());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(RuleEl::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        // 数据范围过滤
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))) {
            lqw.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE));
        }
        return lqw;
    }

    /**
     * 新增规则el
     *
     * @param add 规则el
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(RuleEl add) {
        SysUser user = getLoginUser().getUser();
        if (null == user.getDeptId()) {
            throw new ServiceException(MessageUtils.message("only.allow.tenant.config"));
        }
        validEntityBeforeSave(add);
        if (add.getEl() == null && add.getSourceJson() != null) {
            try {
                Graph graph = new Graph(add.getSourceJson());
                GraphInfo graphInfo = graph.toELInfo();
                add.setEl(graphInfo.toString());
            } catch (LiteFlowELException e) {
                throw new RuntimeException(e);
            }
        }
        add.setTenantId(user.getDept().getDeptUserId());
        add.setCreateTime(DateUtils.getNowDate());
        add.setCreateBy(user.getUserName());
        return this.save(add);
    }

    /**
     * 修改规则el
     *
     * @param update 规则el
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "RuleEl", key = "#update.id")
    public Boolean updateWithCache(RuleEl update) {
        SysUser user = getLoginUser().getUser();
        if (null == user.getDeptId()) {
            throw new ServiceException(MessageUtils.message("only.allow.tenant.config"));
        }
        validEntityBeforeSave(update);
        try {
            Graph graph = new Graph(update.getSourceJson());
            GraphInfo graphInfo = graph.toELInfo();
            update.setEl(graphInfo.toString());
        } catch (LiteFlowELException e) {
            throw new RuntimeException(e);
        }
        update.setUpdateTime(DateUtils.getNowDate());
        update.setUpdateBy(user.getUserName());
        return this.updateById(update);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(RuleEl entity) {
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除规则el信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "RuleEl", keyGenerator = "deleteKeyGenerator")
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if (isValid) {
            // 做一些业务上的校验,判断是否需要校验
        }
        for (Long id : ids) {
            RuleEl ruleEl = this.getById(id);
            if (null != ruleEl.getSceneId() && ruleEl.getSceneId() > 0) {
                // 删除场景联动规则
                sceneService.deleteSceneBySceneId(ruleEl.getSceneId());
            }
        }
        return this.removeByIds(Arrays.asList(ids));
    }


    /** 代码生成区域 可直接覆盖END**/

    /**
     * 自定义代码区域
     **/
    @Override
    public Boolean exec(RuleEl ruleEl) {
        return null;
    }

    @Override
    public Boolean publish(RuleEl ruleEl) {
        try {
            SysUser user = getLoginUser().getUser();
            // 创建场景联动规则
            Graph graph = new Graph(ruleEl.getSourceJson());
            log.debug("publish:{}", ruleEl.getSourceJson());
            GraphInfo graphInfo = graph.toELInfo();
            ruleEl.setEl(graphInfo.toString());
            // 获取触发节点
            List<Node> triggerList = graph.getNodes(RuleEnums.NodeEnum.DEV_TRIGGER.getType());
            // 同步到规则引擎规则
            if (ruleEl.getSceneId() == null) {
                Scene scene = new Scene();
                scene.setUserId(user.getDept().getDeptUserId());
                scene.setUserName(user.getUserName());
                scene.setSceneName(ruleEl.getElName());
                scene.setElData(ruleEl.getEl());
                scene.setEnable(1);
                scene.setRemark(ruleEl.getRemark());
                sceneService.insertSceneByView(scene, triggerList);
                ruleEl.setSceneId(scene.getSceneId());
            } else {
                Scene scene = sceneService.selectScene(ruleEl.getSceneId());
                scene.setSceneName(ruleEl.getElName());
                scene.setElData(ruleEl.getEl());
                scene.setRemark(ruleEl.getRemark());
                sceneService.updateSceneByView(scene, triggerList);
            }
            // 更新el状态
            ruleEl.setEnable(1L);
            return this.updateById(ruleEl);
        } catch (LiteFlowELException e) {
            throw new RuntimeException(e);
        }
    }

    /** 自定义代码区域 END**/
}
