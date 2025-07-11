package com.sydh.iot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sydh.common.utils.DateUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.iot.domain.SocialUser;
import com.sydh.iot.mapper.SocialUserMapper;
import com.sydh.iot.service.ISocialUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 用户第三方用户信息Service业务层处理
 *
 * @author json
 * @date 2022-04-18
 */
@Service
public class SocialUserServiceImpl extends ServiceImpl<SocialUserMapper, SocialUser> implements ISocialUserService
{
    @Resource
    private SocialUserMapper socialUserMapper;

    /**
     * 查询用户第三方用户信息
     *
     * @param socialUserId 用户第三方用户信息主键
     * @return 用户第三方用户信息
     */
    @Override
    public SocialUser selectSocialUserBySocialUserId(Long socialUserId)
    {
        return socialUserMapper.selectById(socialUserId);
    }

    /**
     * 查询用户第三方用户信息列表
     *
     * @param socialUser 用户第三方用户信息
     * @return 用户第三方用户信息
     */
    @Override
    public List<SocialUser> selectSocialUserList(SocialUser socialUser)
    {
        return socialUserMapper.selectSocialUserList(socialUser);
    }

    /**
     * 新增用户第三方用户信息
     *
     * @param socialUser 用户第三方用户信息
     * @return 结果
     */
    @Override
    public int insertSocialUser(SocialUser socialUser)
    {
        socialUser.setCreateTime(DateUtils.getNowDate());
        return socialUserMapper.insert(socialUser);
    }

    /**
     * 修改用户第三方用户信息
     *
     * @param socialUser 用户第三方用户信息
     * @return 结果
     */
    @Override
    public int updateSocialUser(SocialUser socialUser)
    {
        socialUser.setUpdateTime(DateUtils.getNowDate());
        return socialUserMapper.updateById(socialUser);
    }

    /**
     * 批量删除用户第三方用户信息
     *
     * @param socialUserIds 需要删除的用户第三方用户信息主键
     * @return 结果
     */
    @Override
    public int deleteSocialUserBySocialUserIds(Long[] socialUserIds)
    {
        return socialUserMapper.deleteBatchIds(Arrays.asList(socialUserIds));
    }

    /**
     * 删除用户第三方用户信息信息
     *
     * @param socialUserId 用户第三方用户信息主键
     * @return 结果
     */
    @Override
    public int deleteSocialUserBySocialUserId(Long socialUserId)
    {
        return socialUserMapper.deleteById(socialUserId);
    }

    /**
     * 根据openId或unionId获取用户第三方信息
     * @param openId
     * @param unionId
     * @return
     */
    @Override
    public SocialUser selectOneByOpenIdAndUnionId(String openId, String unionId) {
        LambdaQueryWrapper<SocialUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SocialUser::getOpenId, openId);
        if (StringUtils.isNotEmpty(unionId)) {
            queryWrapper.eq(SocialUser::getUnionId, unionId);
        }
        return socialUserMapper.selectOne(queryWrapper);
    }

    /**
     * 通过unionId查询
     *
     * @param unionId
     * @return
     */
    @Override
    public Long selectSysUserIdByUnionId(String unionId) {
        if (StringUtils.isEmpty(unionId)) {
            return null;
        }
        IPage<Long> page = new Page<>(1,1);
        IPage<Long> ret = socialUserMapper.selectSysUserIdByUnionId(page,unionId);
        if(ret.getTotal() > 0) {
            return ret.getRecords().get(0);
        } else {
            return null;
        }
    }

    /**
     * 通过系统用户id查询已绑定信息
     * @param sysUserId 系统用户id
     * @return
     */
    @Override
    public List<SocialUser> selectBySysUserId(Long sysUserId) {
        LambdaQueryWrapper<SocialUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SocialUser::getSysUserId, sysUserId);
        return socialUserMapper.selectList(queryWrapper);
    }

    /**
     * 取消三方登录相关信息
     * @param sysUserId 系统用户id
     * @param sourceClientList 来源具体平台
     * @return
     */
    @Override
    public int cancelBind(Long sysUserId, List<String> sourceClientList) {
        LambdaQueryWrapper<SocialUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SocialUser::getSysUserId, sysUserId);
        queryWrapper.in(SocialUser::getSourceClient, sourceClientList);
        return socialUserMapper.delete(queryWrapper);
    }

    /**
     * 取消三方登录相关信息
     * @param sysUserIds 系统用户id集合
     * @param sourceClientList 来源具体平台
     * @return
     */
    @Override
    public int batchCancelBind(Long[] sysUserIds, List<String> sourceClientList) {
        LambdaQueryWrapper<SocialUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SocialUser::getSysUserId, Arrays.asList(sysUserIds));
        queryWrapper.in(SocialUser::getSourceClient, sourceClientList);
        return socialUserMapper.delete(queryWrapper);
    }

    @Override
    public SocialUser selectByUserIdAndSourceClient(Long userId, String sourceClient) {
        LambdaQueryWrapper<SocialUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SocialUser::getSysUserId, userId);
        queryWrapper.eq(SocialUser::getSourceClient, sourceClient);
        return socialUserMapper.selectOne(queryWrapper);
    }

    @Override
    public int deleteByOpenIdAndSourceClient(String openId, String sourceClient) {
        LambdaQueryWrapper<SocialUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SocialUser::getOpenId, openId);
        queryWrapper.eq(SocialUser::getSourceClient, sourceClient);
        return socialUserMapper.delete(queryWrapper);
    }

    @Override
    public List<SocialUser> listWechatPublicAccountOpenId(List<String> userIdList) {
        return socialUserMapper.listWechatPublicAccountOpenId(userIdList);
    }
}
