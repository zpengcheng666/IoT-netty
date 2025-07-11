package com.sydh.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sydh.iot.domain.SipRelation;
import com.sydh.iot.model.vo.SipRelationVO;

/**
 * 监控设备关联Service接口
 *
 * @author kerwincui
 * @date 2024-06-06
 */
public interface ISipRelationService extends IService<SipRelation>
{
    /**
     * 查询监控设备关联
     *
     * @param id 监控设备关联主键
     * @return 监控设备关联
     */
    public SipRelation selectSipRelationById(Long id);

    public List<SipRelationVO> selectSipRelationByDeviceId(Long deviceId);
    /**
     * 查询监控设备关联列表
     *
     * @param sipRelation 监控设备关联
     * @return 监控设备关联集合
     */
    public Page<SipRelationVO> selectSipRelationList(SipRelation sipRelation);

    /**
     * 根据channelId获取关联关系
     * @param channelId
     * @return
     */
    SipRelation selectByChannelId(String channelId);

    /**
     * 新增或者更新监控设备关联
     * @param sipRelation
     * @return
     */
    int addOrUpdateSipRelation(SipRelation sipRelation);

    /**
     * 新增监控设备关联
     *
     * @param sipRelation 监控设备关联
     * @return 结果
     */
    public int insertSipRelation(SipRelation sipRelation);

    /**
     * 修改监控设备关联
     *
     * @param sipRelation 监控设备关联
     * @return 结果
     */
    public int updateSipRelation(SipRelation sipRelation);

    /**
     * 批量删除监控设备关联
     *
     * @param ids 需要删除的监控设备关联主键集合
     * @return 结果
     */
    public int deleteSipRelationByIds(Long[] ids);

    /**
     * 删除监控设备关联信息
     *
     * @param id 监控设备关联主键
     * @return 结果
     */
    public int deleteSipRelationById(Long id);
}
