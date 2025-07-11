package com.sydh.system.mapper;

import java.util.List;

import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.system.domain.SysRoleDept;
import com.sydh.system.domain.vo.SysRoleDeptVO;
import org.apache.ibatis.annotations.Param;

/**
 * 角色与部门关联表 数据层
 *
 * @author ruoyi
 */
public interface SysRoleDeptMapper extends BaseMapperX<SysRoleDept>
{

    /**
     * 查询部门使用数量
     *
     * @param deptId 部门ID
     * @return 结果
     */
    public int selectCountRoleDeptByDeptId(Long deptId);

    /**
     * 查询部门角色
     * @param deptIdList 部门id
     * @return java.util.List<com.sydh.system.domain.SysRoleDept>
     */
    List<SysRoleDeptVO> selectRoleDeptByDeptIds(@Param("deptIdList") List<Long> deptIdList);

    /**
     * 查询部门角色
     * @param deptId 部门id
     * @return java.util.List<com.sydh.common.core.domain.entity.SysRole>
     */
    List<Long> selectByDeptId(Long deptId);
}
