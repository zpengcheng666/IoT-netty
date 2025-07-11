package com.sydh.iot.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sydh.common.annotation.DataScope;
import com.sydh.common.extend.aspectj.DataScopeAspect;
import com.sydh.common.extend.core.domin.entity.SysUser;
import com.sydh.common.utils.DateUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.iot.convert.GroupConvert;
import com.sydh.iot.domain.Group;
import com.sydh.iot.mapper.GroupMapper;
import com.sydh.iot.model.DeviceGroupInput;
import com.sydh.iot.model.IdOutput;
import com.sydh.iot.model.vo.DeviceShortV0;
import com.sydh.iot.model.vo.GroupVO;
import com.sydh.iot.service.IGroupService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.sydh.common.extend.utils.SecurityUtils.*;


/**
 * 设备分组Service业务层处理
 *
 * @author kerwincui
 * @date 2021-12-16
 */
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper,Group> implements IGroupService
{
    @Resource
    private GroupMapper groupMapper;

    /**
     * 查询设备分组
     *
     * @param groupId 设备分组主键
     * @return 设备分组
     */
    @Override
    public Group selectGroupByGroupId(Long groupId)
    {
        return groupMapper.selectById(groupId);
    }

    /**
     * 通过分组ID查询关联的设备ID数组
     * @param groupId
     * @return
     */
    @Override
    public Long[]  selectDeviceIdsByGroupId(Long groupId){
        List<IdOutput> list=groupMapper.selectDeviceIdsByGroupId(groupId);
        Long[] ids=new Long[list.size()];
        for(int i=0;i<list.size();i++){
            ids[i]=list.get(i).getId();
        }
        return ids;
    }

    /**
     * 查询设备分组列表
     *
     * @param groupVO 设备分组
     * @return 设备分组
     */
    @Override
    @DataScope(fieldAlias = DataScopeAspect.DATA_SCOPE_FILED_ALIAS_USER_ID)
    public Page<GroupVO> selectGroupList(GroupVO groupVO)
    {
        Group group = GroupConvert.INSTANCE.convertGroup(groupVO);
        LambdaQueryWrapper<Group> queryWrapper = this.buildQueryWrapper(group);
        queryWrapper.orderByAsc(Group::getGroupOrder).orderByDesc(Group::getCreateTime);
        Page<Group> groupPage = groupMapper.selectPage(new Page<>(groupVO.getPageNum(), groupVO.getPageSize()), queryWrapper);
        if (0 == groupPage.getTotal()) {
            return new Page<>();
        }
        Page<GroupVO> groupVoPage = GroupConvert.INSTANCE.convertGroupVOPage(groupPage);
        List<GroupVO> groupVOList = groupVoPage.getRecords();
        if (Boolean.TRUE.equals(groupVO.getShowDevice())) {
            List<Long> groupIdList = groupVOList.stream().map(GroupVO::getGroupId).collect(Collectors.toList());
            List<DeviceShortV0> deviceShortList = groupMapper.selectGroupDeviceShortList(groupIdList);
            Map<Long, List<DeviceShortV0>> listMap = deviceShortList.stream().collect(Collectors.groupingBy(DeviceShortV0::getGroupId));
            for (GroupVO vo : groupVOList) {
                vo.setDeviceShortV0List(listMap.get(vo.getGroupId()));
            }
        }
        return groupVoPage;
    }

    /**
     * 新增设备分组
     *
     * @param group 设备分组
     * @return 结果
     */
    @Override
    public int insertGroup(Group group)
    {
        //分组，如果是租户按照机构管理员来存储，终端用户按照用户id来
        Long deptId = getDeptId();
        if (Objects.isNull(deptId)){
            SysUser user = getLoginUser().getUser();
            group.setUserId(user.getUserId());
            group.setUserName(user.getUserName());
        }else {
            Long deptUserId = getLoginUser().getUser().getDept().getDeptUserId();
            String userName = getLoginUser().getUser().getDept().getDeptName();
            group.setUserId(deptUserId);
            group.setUserName(userName);
        }
        group.setCreateBy(getUsername());
        group.setCreateTime(DateUtils.getNowDate());
        return groupMapper.insert(group);
    }

    /**
     * 修改设备分组
     *
     * @param group 设备分组
     * @return 结果
     */
    @Override
    public int updateGroup(Group group)
    {
        group.setUpdateTime(DateUtils.getNowDate());
        return groupMapper.updateById(group);
    }

    /**
     * 分组下批量添加设备分组
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateDeviceGroups(DeviceGroupInput input){
        //删除分组下的所有关联设备
        groupMapper.deleteDeviceGroupByGroupIds(new Long[]{input.getGroupId()});
        // 分组下添加关联设备
        if(input.getDeviceIds().length>0){
            groupMapper.insertDeviceGroups(input);
        }
        return 1;
    }

    /**
     * 批量删除分组和设备分组
     *
     * @param groupIds 需要删除的设备分组主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteGroupByGroupIds(Long[] groupIds)
    {
        // 删除设备分组
        groupMapper.deleteDeviceGroupByGroupIds(groupIds);
        // 删除分组
        return groupMapper.deleteBatchIds(Arrays.asList(groupIds));
    }

    /**
     * 删除分组信息
     *
     * @param groupId 设备分组主键
     * @return 结果
     */
    @Override
    public int deleteGroupByGroupId(Long groupId)
    {

        return groupMapper.deleteById(groupId);
    }

    /**
     * 根据用户Id查询分组列表
     * @param userId 用户id
     * @return
     */
    @Override
    public List<Group> getGroupListByUserId(Long userId) {
        LambdaQueryWrapper<Group> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Group::getUserId, userId);
        queryWrapper.orderByAsc(Group::getGroupOrder);
        return groupMapper.selectList(queryWrapper);
    }

    @Override
    public List<Group> listAllByIdList(Set<Long> groupIds) {
        List<Group> groupList = null ;
        if (CollectionUtils.isNotEmpty(groupIds)){
            LambdaQueryWrapper<Group> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(Group::getGroupId, groupIds);
            queryWrapper.orderByAsc(Group::getGroupOrder);
            groupList = groupMapper.selectList(queryWrapper);
        }
        return groupList;
    }

    private LambdaQueryWrapper<Group> buildQueryWrapper(Group query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<Group> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(query.getGroupName()), Group::getGroupName, query.getGroupName());
        lqw.eq(query.getGroupOrder() != null, Group::getGroupOrder, query.getGroupOrder());
        lqw.eq(query.getUserId() != null, Group::getUserId, query.getUserId());
        lqw.eq(StringUtils.isNotBlank(query.getUserName()), Group::getUserName, query.getUserName());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(Group::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }

        // 数据范围过滤
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))){
            lqw.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE));
        }

        return lqw;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Group entity){
        //TODO 做一些数据校验,如唯一约束
    }


}
