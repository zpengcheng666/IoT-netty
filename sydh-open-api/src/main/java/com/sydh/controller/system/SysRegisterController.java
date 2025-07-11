package com.sydh.controller.system;

import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.core.domin.model.RegisterBody;
import com.sydh.common.utils.MessageUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.system.service.ISysConfigService;
import com.sydh.system.service.sys.SysRegisterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 注册验证
 *
 * @author ruoyi
 */
@Api(tags = "注册账号")
@RestController
public class SysRegisterController extends BaseController
{
    @Resource
    private SysRegisterService registerService;

    @Resource
    private ISysConfigService configService;

    @ApiOperation("注册账号")
    @PostMapping("/register")
    public AjaxResult register(@RequestBody RegisterBody user)
    {
        if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser"))))
        {
            return error(MessageUtils.message("sysRegister.fail.not.enable.register"));
        }
        String msg = registerService.register(user);
        return StringUtils.isEmpty(msg) ? success() : error(msg);
    }
}
