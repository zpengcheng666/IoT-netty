package com.sydh.iot.mapper;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.iot.domain.Group;
import com.sydh.iot.model.DeviceGroupInput;
import com.sydh.iot.model.IdOutput;
import com.sydh.iot.model.vo.DeviceShortV0;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 设备分组Mapper接口
 *
 * @author kerwincui
 * @date 2021-12-16
 */
@Repository
public interface GroupMapper extends BaseMapperX<Group>
{

    /**
     * 通过分组ID查询关联的设备ID数组
     * @param groupId
     * @return
     */
    public List<IdOutput> selectDeviceIdsByGroupId(Long groupId);

    /**
     * 查询设备分组列表
     *
     * @param group 设备分组
     * @return 设备分组集合
     */
    public List<Group> selectGroupList(Group group);
    public Page<Group> selectGroupList(Page<Group> page, Group group);

    /**
     * 分组下批量增加设备分组
     * @param input
     * @return
     */
    public int insertDeviceGroups(DeviceGroupInput input);

    /**
     * 批量删除设备分组
     * @param groupIds
     * @return
     */
    public int deleteDeviceGroupByGroupIds(Long[] groupIds);

    /**
     * 分组查询设备列表
     * @param groupIdList
     * @return
     */
    List<DeviceShortV0> selectGroupDeviceShortList(@Param("groupIdList") List<Long> groupIdList);
}
