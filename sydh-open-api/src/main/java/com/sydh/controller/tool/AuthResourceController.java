package com.sydh.controller.tool;

import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.utils.MessageUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 设备告警Controller
 *
 * @author kerwincui
 * @date 2022-01-13
 */
@RestController
@RequestMapping("/oauth/resource")
public class AuthResourceController extends BaseController
{
    /**
     * 查询设备告警列表
     */
    @GetMapping("/product")
    public String findAll() {
        return MessageUtils.message("auth.resource.product.query.success");
    }


}
