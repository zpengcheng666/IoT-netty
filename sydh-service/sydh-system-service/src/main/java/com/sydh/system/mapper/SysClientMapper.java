package com.sydh.system.mapper;

import java.util.List;

import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.system.domain.SysClient;

/**
 * 系统授权Mapper接口
 *
 * @author zhuangpeng.li
 * @date 2024-07-26
 */
public interface SysClientMapper extends BaseMapperX<SysClient>
{
    /**
     * 查询系统授权
     *
     * @param id 系统授权主键
     * @return 系统授权
     */
    public SysClient selectSysClientById(Long id);

    /**
     * 查询系统授权列表
     *
     * @param sysClient 系统授权
     * @return 系统授权集合
     */
    public List<SysClient> selectSysClientList(SysClient sysClient);

    /**
     * 新增系统授权
     *
     * @param sysClient 系统授权
     * @return 结果
     */
    public int insertSysClient(SysClient sysClient);

    /**
     * 修改系统授权
     *
     * @param sysClient 系统授权
     * @return 结果
     */
    public int updateSysClient(SysClient sysClient);

}
