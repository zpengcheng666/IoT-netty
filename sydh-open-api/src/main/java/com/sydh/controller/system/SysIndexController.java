package com.sydh.controller.system;

import com.sydh.common.config.RuoYiConfig;
import com.sydh.common.utils.MessageUtils;
import com.sydh.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页
 *
 * @author ruoyi
 */
@RestController
public class SysIndexController
{
    /** 系统基础配置 */
    @Autowired
    private RuoYiConfig ruoyiConfig;

    /**
     * 访问首页，提示语
     */
    @RequestMapping("/")
    public String index()
    {
        return StringUtils.format(MessageUtils.message("index.welcome.message"), ruoyiConfig.getName(), ruoyiConfig.getVersion());
    }
}
