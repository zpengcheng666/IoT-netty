package com.sydh.iot.convert;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.iot.domain.Group;
import com.sydh.iot.model.vo.GroupVO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 设备分组Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, builder = @Builder(disableBuilder = true))
public interface GroupConvert
{

    GroupConvert INSTANCE = Mappers.getMapper(GroupConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param group
     * @return 设备分组集合
     */
    GroupVO convertGroupVO(Group group);

    /**
     * VO类转换为实体类集合
     *
     * @param groupVO
     * @return 设备分组集合
     */
    Group convertGroup(GroupVO groupVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param groupList
     * @return 设备分组集合
     */
    List<GroupVO> convertGroupVOList(List<Group> groupList);

    /**
     * VO类转换为实体类
     *
     * @param groupVOList
     * @return 设备分组集合
     */
    List<Group> convertGroupList(List<GroupVO> groupVOList);

    /**
     * 实体类转VO类分页
     * @param page
     * @return
     */
    Page<GroupVO> convertGroupVOPage(Page<Group> page);
}
