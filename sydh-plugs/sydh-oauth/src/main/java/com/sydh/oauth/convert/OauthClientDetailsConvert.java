package com.sydh.oauth.convert;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.oauth.domain.OauthClientDetails;
import com.sydh.oauth.vo.OauthClientDetailsVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 【请填写功能名称】Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-19
 */
@Mapper
public interface OauthClientDetailsConvert
{

    OauthClientDetailsConvert INSTANCE = Mappers.getMapper(OauthClientDetailsConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param oauthClientDetails
     * @return 【请填写功能名称】集合
     */
    OauthClientDetailsVO convertOauthClientDetailsVO(OauthClientDetails oauthClientDetails);

    /**
     * VO类转换为实体类集合
     *
     * @param oauthClientDetailsVO
     * @return 【请填写功能名称】集合
     */
    OauthClientDetails convertOauthClientDetails(OauthClientDetailsVO oauthClientDetailsVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param oauthClientDetailsList
     * @return 【请填写功能名称】集合
     */
    List<OauthClientDetailsVO> convertOauthClientDetailsVOList(List<OauthClientDetails> oauthClientDetailsList);

    /**
     * VO类转换为实体类
     *
     * @param oauthClientDetailsVOList
     * @return 【请填写功能名称】集合
     */
    List<OauthClientDetails> convertOauthClientDetailsList(List<OauthClientDetailsVO> oauthClientDetailsVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param oauthClientDetailsPage
     * @return 【请填写功能名称】分页
     */
    Page<OauthClientDetailsVO> convertOauthClientDetailsVOPage(Page<OauthClientDetails> oauthClientDetailsPage);

    /**
     * VO类转换为实体类
     *
     * @param oauthClientDetailsVOPage
     * @return 【请填写功能名称】分页
     */
    Page<OauthClientDetails> convertOauthClientDetailsPage(Page<OauthClientDetailsVO> oauthClientDetailsVOPage);
}
