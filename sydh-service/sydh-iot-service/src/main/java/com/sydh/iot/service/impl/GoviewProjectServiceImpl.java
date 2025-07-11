package com.sydh.iot.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sydh.common.annotation.DataScope;
import com.sydh.common.extend.aspectj.DataScopeAspect;
import com.sydh.common.utils.DateUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.common.utils.uuid.IdUtils;
import com.sydh.iot.domain.GoviewProject;
import com.sydh.iot.mapper.GoviewProjectMapper;
import com.sydh.iot.service.IGoviewProjectService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * 项目Service业务层处理
 *
 * @author kami
 * @date 2022-10-27
 */
@Service
public class GoviewProjectServiceImpl extends ServiceImpl<GoviewProjectMapper,GoviewProject> implements IGoviewProjectService
{
    @Resource
    private GoviewProjectMapper goviewProjectMapper;

    /**
     * 查询项目
     *
     * @param id 项目主键
     * @return 项目
     */
    @Override
    public GoviewProject selectGoviewProjectById(String id)
    {
        return goviewProjectMapper.selectById(id);
    }

    /**
     * 查询项目列表
     *
     * @param goviewProject 项目
     * @return 项目
     */
    @Override
    @DataScope()
    public Page<GoviewProject> selectGoviewProjectList(GoviewProject goviewProject)
    {
        LambdaQueryWrapper<GoviewProject> queryWrapper = this.buildQueryWrapper(goviewProject);
        return goviewProjectMapper.selectPage(new Page<>(goviewProject.getPageNum(), goviewProject.getPageSize()), queryWrapper);
    }

    /**
     * 新增项目
     *
     * @param goviewProject 项目
     * @return 结果
     */
    @Override
    public String insertGoviewProject(GoviewProject goviewProject)
    {
        goviewProject.setId(IdUtils.simpleUUID());
        goviewProject.setCreateTime(DateUtils.getNowDate());
        goviewProject.setUpdateTime(DateUtils.getNowDate());
        int i = goviewProjectMapper.insert(goviewProject);
        if(i > 0){
            return goviewProject.getId();
        }
        return null;
    }

    /**
     * 修改项目
     *
     * @param goviewProject 项目
     * @return 结果
     */
    @Override
    public int updateGoviewProject(GoviewProject goviewProject)
    {
        goviewProject.setUpdateTime(DateUtils.getNowDate());
        return goviewProjectMapper.updateById(goviewProject);
    }

    /**
     * 批量删除项目
     *
     * @param ids 需要删除的项目主键
     * @return 结果
     */
    @Override
    public int deleteGoviewProjectByIds(String[] ids)
    {
        return goviewProjectMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /**
     * 删除项目信息
     *
     * @param id 项目主键
     * @return 结果
     */
    @Override
    public int deleteGoviewProjectById(String id)
    {
        return goviewProjectMapper.deleteById(id);
    }

    private LambdaQueryWrapper<GoviewProject> buildQueryWrapper(GoviewProject query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<GoviewProject> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(query.getProjectName()), GoviewProject::getProjectName, query.getProjectName());
        lqw.eq(query.getState() != null, GoviewProject::getState, query.getState());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(GoviewProject::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        // 数据范围过滤
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))){
            lqw.apply((String) query.getParams().get("dataScope"));
        }
        return lqw;
    }
}
