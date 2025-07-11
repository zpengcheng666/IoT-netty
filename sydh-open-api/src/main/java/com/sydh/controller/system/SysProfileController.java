package com.sydh.controller.system;

import com.sydh.common.annotation.Log;
import com.sydh.common.config.RuoYiConfig;
import com.sydh.common.constant.UserConstants;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.enums.BusinessType;
import com.sydh.common.enums.SocialPlatformType;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.core.domin.model.LoginUser;
import com.sydh.common.extend.core.domin.entity.SysUser;
import com.sydh.common.extend.utils.SecurityUtils;
import com.sydh.common.utils.MessageUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.common.utils.file.FileUploadUtils;
import com.sydh.common.utils.file.MimeTypeUtils;
import com.sydh.iot.domain.UserSocialProfile;
import com.sydh.iot.service.IUserSocialProfileService;
import com.sydh.system.service.ISysUserService;
import com.sydh.system.service.sys.TokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 个人信息 业务处理
 *
 * @author ruoyi
 */
@Api(tags = "个人中心")
@RestController
@RequestMapping("/system/user/profile")
public class SysProfileController extends BaseController
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private IUserSocialProfileService iUserSocialProfileService;

    /**
     * 个人信息
     */
    @ApiOperation("获取个人信息")
    @GetMapping
    public AjaxResult profile()
    {
        LoginUser loginUser = getLoginUser();
        SysUser user = loginUser.getUser();
        AjaxResult ajax = AjaxResult.success(user);
        ajax.put("roleGroup", userService.selectUserRoleGroup(loginUser.getUsername()));
        ajax.put("postGroup", userService.selectUserPostGroup(loginUser.getUsername()));
        List<UserSocialProfile> socialProfileList = iUserSocialProfileService.selectUserSocialProfile(loginUser.getUserId());
        UserSocialProfile userSocialProfile = socialProfileList.stream().filter(s -> SocialPlatformType.listWechatPlatform.contains(s.getSourceClient()) && 1 == s.getStatus()).findFirst().orElse(null);
        ajax.put("socialGroup", socialProfileList);
        ajax.put("wxBind", userSocialProfile != null);
        return ajax;
    }

    /**
     * 修改用户
     */
    @ApiOperation("修改个人信息")
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult updateProfile(@RequestBody SysUser user)
    {
        LoginUser loginUser = getLoginUser();
        SysUser sysUser = loginUser.getUser();
        user.setUserName(sysUser.getUserName());
        if (StringUtils.isNotEmpty(user.getPhonenumber())
                && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user)))
        {
            return error(StringUtils.format(MessageUtils.message("user.update.failed.phone.exists"), user.getUserName()));
        }
        if (StringUtils.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user)))
        {
            return error(StringUtils.format(MessageUtils.message("user.update.failed.email.exists"), user.getUserName()));
        }
        user.setUserId(sysUser.getUserId());
        user.setPassword(null);
        user.setAvatar(null);
        user.setDeptId(sysUser.getDeptId());
        if (userService.updateUserProfile(user) > 0)
        {
            // 更新缓存用户信息
            sysUser.setNickName(user.getNickName());
            sysUser.setPhonenumber(user.getPhonenumber());
            sysUser.setEmail(user.getEmail());
            sysUser.setSex(user.getSex());
            tokenService.setLoginUser(loginUser);
            return success();
        }
        return error(MessageUtils.message("user.update.failed"));
    }

    /**
     * 重置密码
     */
    @ApiOperation("重置密码")
    @PreAuthorize("@ss.hasPermi('systerm:user:updatePwd')")
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updatePwd")
    public AjaxResult updatePwd(String oldPassword, String newPassword)
    {
        LoginUser loginUser = getLoginUser();
        String userName = loginUser.getUsername();
        String password = loginUser.getPassword();
        if (!SecurityUtils.matchesPassword(oldPassword, password))
        {
            return error(MessageUtils.message("user.update.failed.password.wrong"));
        }
        if (SecurityUtils.matchesPassword(newPassword, password))
        {
            return error(MessageUtils.message("user.update.failed.password.same"));
        }
        if (userService.resetUserPwd(userName, SecurityUtils.encryptPassword(newPassword)) > 0)
        {
            // 更新缓存用户密码
            loginUser.getUser().setPassword(SecurityUtils.encryptPassword(newPassword));
            tokenService.setLoginUser(loginUser);
            return success();
        }
        return error(MessageUtils.message("user.update.password.failed"));
    }

    /**
     * 头像上传
     */
    @ApiOperation("头像上传")
    @Log(title = "用户头像", businessType = BusinessType.UPDATE)
    @PostMapping("/avatar")
    public AjaxResult avatar(@RequestParam("avatarfile") MultipartFile file) throws Exception
    {
        if (!file.isEmpty())
        {
            LoginUser loginUser = getLoginUser();
            String avatar = FileUploadUtils.upload(RuoYiConfig.getAvatarPath(), file, MimeTypeUtils.IMAGE_EXTENSION);
            if (userService.updateUserAvatar(loginUser.getUsername(), avatar))
            {
                AjaxResult ajax = AjaxResult.success();
                ajax.put("imgUrl", avatar);
                // 更新缓存用户头像
                loginUser.getUser().setAvatar(avatar);
                tokenService.setLoginUser(loginUser);
                return ajax;
            }
        }
        return error(MessageUtils.message("user.upload.avatar.failed"));
    }
}
