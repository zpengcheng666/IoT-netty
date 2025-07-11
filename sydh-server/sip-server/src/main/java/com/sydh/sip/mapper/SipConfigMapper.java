package com.sydh.sip.mapper;

import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.sip.domain.SipConfig;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * sip系统配置Mapper接口
 *
 * @author zhuangpeng.li
 * @date 2022-11-30
 */
@Repository
public interface SipConfigMapper extends BaseMapperX<SipConfig>
{
    /**
     * 查询产品下第一条sip系统配置
     *
     * @return sip系统配置
     */
    public SipConfig selectSipConfigByProductId(Long productId);
    /**
     * 查询sip系统配置列表
     *
     * @param sipConfig sip系统配置
     * @return sip系统配置集合
     */
    public List<SipConfig> selectSipConfigList(SipConfig sipConfig);

    public int resetDefaultSipConfig();
}
