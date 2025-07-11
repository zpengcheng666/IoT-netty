package com.sydh.common.extend.core.controller;

import com.sydh.common.core.domain.AjaxResult;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhuangpengli
 */
@Api(tags = "公共模块")
@RestController
@RequestMapping("/commons")
public class CommonsController extends BaseController {
    
    /**
     * 获取系统信息
     * 开源版本：移除了license验证，直接返回成功状态
     */
    @GetMapping(value = "/getInfo")
    public AjaxResult getInfo() {
        // 开源版本不需要license验证，直接返回成功
        return AjaxResult.success("开源版本，无需license验证");
    }
}
