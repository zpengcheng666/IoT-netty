package com.sydh.web.controller.common;

import com.sydh.common.core.redis.RedisCache;
import com.sydh.system.service.ISysConfigService;
import com.google.code.kaptcha.Producer;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 验证码操作处理
 *
 * @author ruoyi
 */
@Api(tags = "验证码操作")
@RestController
public class CaptchaController
{
    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ISysConfigService configService;
    /**
     * 生成验证码
     */
//    @ApiOperation("获取验证码")
//    @GetMapping("/captchaImage")
//    public AjaxResult getCode(HttpServletResponse response,Boolean showCode) throws IOException
//    {
//        AjaxResult ajax = AjaxResult.success();
//        boolean captchaEnabled = configService.selectCaptchaEnabled();
//        ajax.put("captchaEnabled", captchaEnabled);
//        if (!captchaEnabled)
//        {
//            return ajax;
//        }
//
//        // 保存验证码信息
//        String uuid = IdUtils.simpleUUID();
//        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + uuid;
//
//        String capStr = null, code = null;
//        BufferedImage image = null;
//
//        // 生成验证码
//        String captchaType = RuoYiConfig.getCaptchaType();
//        if ("math".equals(captchaType))
//        {
//            String capText = captchaProducerMath.createText();
//            capStr = capText.substring(0, capText.lastIndexOf("@"));
//            code = capText.substring(capText.lastIndexOf("@") + 1);
//            image = captchaProducerMath.createImage(capStr);
//        }
//        else if ("char".equals(captchaType))
//        {
//            capStr = code = captchaProducer.createText();
//            image = captchaProducer.createImage(capStr);
//        }
//
//        redisCache.setCacheObject(verifyKey, code, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
//        // 转换流信息写出
//        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
//        try
//        {
//            ImageIO.write(image, "jpg", os);
//        }
//        catch (IOException e)
//        {
//            return AjaxResult.error(e.getMessage());
//        }
//        if (Boolean.TRUE.equals(showCode)) {
//            ajax.put("resultCode", code);
//        }
//        ajax.put("uuid", uuid);
//        ajax.put("img", Base64.encode(os.toByteArray()));
//        return ajax;
//    }
}
