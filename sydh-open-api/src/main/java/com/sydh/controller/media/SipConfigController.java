package com.sydh.controller.media;

import com.sydh.common.annotation.Log;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.enums.BusinessType;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.sip.domain.SipConfig;
import com.sydh.sip.service.ISipConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * sip系统配置Controller
 *
 * @author zhuangpeng.li
 * @date 2022-11-30
 */
@Api(tags = "sip系统配置")
@RestController
@RequestMapping("/sip/sipconfig")
public class SipConfigController extends BaseController {
    @Resource
    private ISipConfigService sipConfigService;

    /**
     * 获取产品下第一条sip系统配置详细信息
     */
    @ApiOperation("获取产品下sip系统配置信息")
    @PreAuthorize("@ss.hasPermi('iot:video:query')")
    @GetMapping(value = "/{productId}")
    public AjaxResult getInfo(@PathVariable("productId") Long productId) {
        return AjaxResult.success(sipConfigService.selectSipConfigByProductId(productId));
    }

    @ApiOperation("获取设备下sip认证信息")
    @PreAuthorize("@ss.hasPermi('iot:video:query')")
    @GetMapping(value = "/auth/{deviceSipId}")
    public AjaxResult getDevInfo(@PathVariable("deviceSipId") String deviceSipId) {
        return AjaxResult.success(sipConfigService.selectSipConfigBydeviceSipId(deviceSipId));
    }

    /**
     * 新增sip系统配置
     */
    @ApiOperation("新增sip系统配置")
    @PreAuthorize("@ss.hasPermi('iot:video:add')")
    @Log(title = "sip系统配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SipConfig sipConfig) {
        return AjaxResult.success(sipConfigService.insertSipConfig(sipConfig));
    }

    /**
     * 修改sip系统配置
     */
    @ApiOperation("修改sip系统配置")
    @PreAuthorize("@ss.hasPermi('iot:video:edit')")
    @Log(title = "sip系统配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SipConfig sipConfig) {
        return toAjax(sipConfigService.updateSipConfig(sipConfig));
    }

    /**
     * 删除sip系统配置
     */
    @ApiOperation("删除sip系统配置")
    @PreAuthorize("@ss.hasPermi('iot:video:remove')")
    @Log(title = "sip系统配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(sipConfigService.deleteSipConfigByIds(ids));
    }

    @ApiOperation("批量删除sip系统配置")
    @PreAuthorize("@ss.hasPermi('iot:video:remove')")
    @Log(title = "sip系统配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/product/{productIds}")
    public AjaxResult removeByProductId(@PathVariable Long[] productIds) {
        // 设备可能不存在通道，可以返回0
        int result = sipConfigService.deleteSipConfigByProductIds(productIds);
        return AjaxResult.success(result);
    }
}
