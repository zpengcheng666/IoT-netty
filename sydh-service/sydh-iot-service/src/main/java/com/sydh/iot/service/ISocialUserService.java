package com.sydh.iot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sydh.iot.domain.SocialUser;

import java.util.List;

/**
 * 用户第三方用户信息Service接口
 *
 * @author json
 * @date 2022-04-18
 */
public interface ISocialUserService extends IService<SocialUser>
{
    /**
     * 查询用户第三方用户信息
     *
     * @param socialUserId 用户第三方用户信息主键
     * @return 用户第三方用户信息
     */
    public SocialUser selectSocialUserBySocialUserId(Long socialUserId);

    /**
     * 查询用户第三方用户信息列表
     *
     * @param socialUser 用户第三方用户信息
     * @return 用户第三方用户信息集合
     */
    public List<SocialUser> selectSocialUserList(SocialUser socialUser);

    /**
     * 新增用户第三方用户信息
     *
     * @param socialUser 用户第三方用户信息
     * @return 结果
     */
    public int insertSocialUser(SocialUser socialUser);

    /**
     * 修改用户第三方用户信息
     *
     * @param socialUser 用户第三方用户信息
     * @return 结果
     */
    public int updateSocialUser(SocialUser socialUser);

    /**
     * 批量删除用户第三方用户信息
     *
     * @param socialUserIds 需要删除的用户第三方用户信息主键集合
     * @return 结果
     */
    public int deleteSocialUserBySocialUserIds(Long[] socialUserIds);

    /**
     * 删除用户第三方用户信息信息
     *
     * @param socialUserId 用户第三方用户信息主键
     * @return 结果
     */
    public int deleteSocialUserBySocialUserId(Long socialUserId);

    /**
     * 根据openId或unionId获取用户第三方信息
     * @param openId
     * @param unionId
     * @return
     */
    public SocialUser selectOneByOpenIdAndUnionId(String openId, String unionId);

    /**
     * 通过unionId查询
     *
     * @param unionId
     * @return
     */
    Long selectSysUserIdByUnionId(String unionId);

    /**
     * 通过系统用户id查询已绑定信息
     * @param sysUserId 系统用户id
     * @return
     */
    List<SocialUser> selectBySysUserId(Long sysUserId);

    /**
     * 取消所有相关微信绑定
     * @param sysUserId 系统用户id
     * @return 结果
     */
    int cancelBind(Long sysUserId, List<String> sourceClientList);

    /**
     * 取消所有相关微信绑定
     * @param sysUserIds 系统用户id集合
     * @return 结果
     */
    int batchCancelBind(Long[] sysUserIds, List<String> sourceClientList);

    /**
     * 根据用户id和来源查询绑定信息
     * @param userId
     * @param: sourceClient
     * @return com.sydh.iot.domain.SocialUser
     */
    SocialUser selectByUserIdAndSourceClient(Long userId, String sourceClient);

    /**
     * 解绑微信公众号
     * @param openId
     * @param: sourceClient
     * @return void
     */
    int deleteByOpenIdAndSourceClient(String openId, String sourceClient);

    /**
     * 查询用户绑定微信公众号openId
     * @param userIdList 用户id集合
     * @return java.util.List<com.sydh.iot.domain.SocialUser>
     */
    List<SocialUser> listWechatPublicAccountOpenId(List<String> userIdList);
}
