package com.sydh.system.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sydh.system.domain.SysLogininfor;
import com.sydh.system.domain.vo.SysLogininforVO;

/**
 * 系统访问日志情况信息 服务层
 *
 * @author ruoyi
 */
public interface ISysLogininforService extends IService<SysLogininfor>
{
    /**
     * 新增系统登录日志
     *
     * @param logininfor 访问日志对象
     */
    public void insertLogininfor(SysLogininfor logininfor);

    /**
     * 查询系统登录日志集合
     *
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    public List<SysLogininfor> selectLogininforList(SysLogininfor logininfor);

    /**
     * 查询系统访问记录列表
     *
     * @param sysLogininfor 系统访问记录
     * @return 系统访问记录分页集合
     */
    Page<SysLogininforVO> pageSysLogininforVO(SysLogininfor sysLogininfor);

    /**
     * 批量删除系统登录日志
     *
     * @param infoIds 需要删除的登录日志ID
     * @return 结果
     */
    public int deleteLogininforByIds(Long[] infoIds);

    /**
     * 清空系统登录日志
     */
    public void cleanLogininfor();

    /**
     * 统计一周内的登陆数据
     */
    public List<SysLogininfor> getUserCount();
}
