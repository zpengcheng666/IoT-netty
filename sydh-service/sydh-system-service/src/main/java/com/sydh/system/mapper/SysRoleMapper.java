package com.sydh.system.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.extend.core.domin.entity.SysRole;
import com.sydh.common.mybatis.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色表 数据层
 *
 * @author ruoyi
 */
public interface SysRoleMapper extends BaseMapperX<SysRole>
{
    /**
     * 根据条件分页查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    public List<SysRole> selectRoleList(SysRole role);

    /**
     * 根据用户ID查询角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    public List<SysRole> selectRolePermissionByUserId(Long userId);

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    public List<SysRole> selectRoleAll();

    /**
     * 根据用户ID获取角色选择框列表
     *
     * @param userId 用户ID
     * @return 选中角色ID列表
     */
    public List<Long> selectRoleListByUserId(Long userId);

    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    public SysRole selectRoleById(Long roleId);

    /**
     * 根据用户ID查询角色
     *
     * @param userName 用户名
     * @return 角色列表
     */
    public List<SysRole> selectRolesByUserName(String userName);

    /**
     * 修改角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    public int updateRole(SysRole role);

    /**
     * 新增角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    public int insertRole(SysRole role);

    /**
     * 查询角色信息
     * @param roleIdList 角色id集合
     * @return java.util.List<com.sydh.common.core.domain.entity.SysRole>
     */
    Page<SysRole> selectRoleByIds(Page<SysRole> page, @Param("roleIdList") List<Long> roleIdList, @Param("roleName") String roleName, @Param("status") Integer status);
}
