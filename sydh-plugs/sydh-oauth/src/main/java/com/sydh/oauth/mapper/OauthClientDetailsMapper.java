package com.sydh.oauth.mapper;

import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.oauth.domain.OauthClientDetails;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 云云对接Mapper接口
 *
 * @author kerwincui
 * @date 2022-02-07
 */
@Repository
public interface OauthClientDetailsMapper extends BaseMapperX<OauthClientDetails>
{
    /**
     * 查询云云对接
     *
     * @param id 云云对接主键
     * @return 云云对接
     */
    public OauthClientDetails selectOauthClientDetailsById(Long id);

    /**
     * 查询云云对接列表
     *
     * @param oauthClientDetails 云云对接
     * @return 云云对接集合
     */
    public List<OauthClientDetails> selectOauthClientDetailsList(OauthClientDetails oauthClientDetails);

    /**
     * 新增云云对接
     *
     * @param oauthClientDetails 云云对接
     * @return 结果
     */
    public int insertOauthClientDetails(OauthClientDetails oauthClientDetails);

    /**
     * 修改云云对接
     *
     * @param oauthClientDetails 云云对接
     * @return 结果
     */
    public int updateOauthClientDetails(OauthClientDetails oauthClientDetails);

    /**
     * 删除云云对接
     *
     * @param clientId 云云对接主键
     * @return 结果
     */
    public int deleteOauthClientDetailsByClientId(String clientId);

    /**
     * 批量删除云云对接
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteOauthClientDetailsByIds(Long[] ids);

    /**
     * 通过授权平台查询配置
     * @param type 授权平台类型
     * @return
     */
    OauthClientDetails selectOauthClientDetailsByType(@Param("type") Integer type, @Param("tenantId") Long tenantId);

    OauthClientDetails selectOauthClientDetailsByClientId(String clientId);
}
