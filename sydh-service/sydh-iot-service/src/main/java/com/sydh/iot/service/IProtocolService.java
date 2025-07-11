package com.sydh.iot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sydh.iot.domain.Protocol;
import com.sydh.iot.model.vo.ProtocolVO;

import java.util.List;

/**
 * 协议管理类
 * @author gsb
 * @date 2022/10/19 16:47
 */
public interface IProtocolService extends IService<Protocol> {

    /**
     * 查询协议
     *
     * @param id 协议主键
     * @return 协议
     */
    public Protocol selectProtocolById(Long id);

    /**
     * 查询协议列表
     *
     * @param protocol 协议
     * @return 协议分页集合
     */
    Page<ProtocolVO> pageProtocolVO(Protocol protocol);

    /**
     * 查询协议列表
     *
     * @param protocol 协议
     * @return 协议集合
     */
    List<ProtocolVO> listProtocolVO(Protocol protocol);

    /**
     * 新增协议
     *
     * @param protocol 协议
     * @return 结果
     */
    public int insertProtocol(Protocol protocol);

    /**
     * 修改协议
     *
     * @param protocol 协议
     * @return 结果
     */
    public int updateProtocol(Protocol protocol);

    /**
     * 批量删除协议
     *
     * @param ids 需要删除的协议主键集合
     * @return 结果
     */
    public int deleteProtocolByIds(Long[] ids);

    /**
     * 删除协议信息
     *
     * @param id 协议主键
     * @return 结果
     */
    public int deleteProtocolById(Long id);


    /**
     * 获取所有协议
     * @return
     */
    public List<Protocol> selectAll();

    /**
     *获取所有可用协议
     * @param protocol
     * @return
     */
    public List<Protocol> selectByCondition(Protocol protocol);
}
