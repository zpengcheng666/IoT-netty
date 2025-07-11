package com.sydh.iot.convert;

import com.sydh.iot.domain.SocialUser;
import com.sydh.iot.model.vo.SocialUserVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 第三方登录用户Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-14
 */
@Mapper
public interface SocialUserConvert
{

    SocialUserConvert INSTANCE = Mappers.getMapper(SocialUserConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param socialUser
     * @return 第三方登录用户集合
     */
    SocialUserVO convertSocialUserVO(SocialUser socialUser);

    /**
     * VO类转换为实体类集合
     *
     * @param socialUserVO
     * @return 第三方登录用户集合
     */
    SocialUser convertSocialUser(SocialUserVO socialUserVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param socialUserList
     * @return 第三方登录用户集合
     */
    List<SocialUserVO> convertSocialUserVOList(List<SocialUser> socialUserList);

    /**
     * VO类转换为实体类
     *
     * @param socialUserVOList
     * @return 第三方登录用户集合
     */
    List<SocialUser> convertSocialUserList(List<SocialUserVO> socialUserVOList);
}
