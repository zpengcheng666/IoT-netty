package com.sydh.controller.system;

import com.sydh.common.constant.Constants;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.extend.core.domin.entity.SysUser;
import com.sydh.common.extend.core.domin.model.LoginBody;
import com.sydh.common.extend.utils.SecurityUtils;
import com.sydh.system.domain.AppPreferences;
import com.sydh.system.domain.SysMenu;
import com.sydh.system.service.IAppPreferencesService;
import com.sydh.system.service.ISysMenuService;
import com.sydh.system.service.sys.SysLoginService;
import com.sydh.system.service.sys.SysPermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

import static com.sydh.common.constant.Constants.LANGUAGE;

/**
 * 登录验证
 *
 * @author ruoyi
 */
@Api(tags = "登录验证")
@RestController
public class SysLoginController
{
    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;
    @Value("${server.broker.enabled}")
    private Boolean enabled;
    @Resource
    private IAppPreferencesService appPreferencesService;

    /**
     * 登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public AjaxResult login(HttpServletRequest request, @RequestBody LoginBody loginBody)
    {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid(), loginBody.getSourceType(), request.getHeader(LANGUAGE));
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @ApiOperation("获取用户信息")
    @GetMapping("getInfo")
    public AjaxResult getInfo()
    {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        AppPreferences appPreferences = appPreferencesService.selectAppPreferencesByUserId(user.getUserId());
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        ajax.put("mqtt",enabled);
        ajax.put("language", appPreferences.getLanguage());
        return ajax;
    }


    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @ApiOperation("获取路由信息")
    @GetMapping("getRouters")
    public AjaxResult getRouters(HttpServletRequest request)
    {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId, request.getHeader(LANGUAGE));
        return AjaxResult.success(menuService.buildMenus(menus));
    }
}
