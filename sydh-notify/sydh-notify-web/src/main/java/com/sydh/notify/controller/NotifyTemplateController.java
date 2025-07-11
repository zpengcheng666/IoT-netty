package com.sydh.notify.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.annotation.Log;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.enums.BusinessType;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.enums.NotifyChannelProviderEnum;
import com.sydh.common.extend.utils.poi.ExcelUtil;
import com.sydh.common.utils.MessageUtils;
import com.sydh.notify.domain.NotifyTemplate;
import com.sydh.notify.service.INotifyTemplateService;
import com.sydh.notify.vo.NotifyTemplateVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.MapUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * 通知模版Controller
 *
 * @author kerwincui
 * @date 2023-12-01
 */
@RestController
@RequestMapping("/notify/template")
@Api(tags = "通知模板配置")
public class NotifyTemplateController extends BaseController {

    @Resource
    private INotifyTemplateService notifyTemplateService;

    /**
     * 查询通知模版列表
     */
    @PreAuthorize("@ss.hasPermi('notify:template:list')")
    @GetMapping("/list")
    @ApiOperation("查询通知模版列表")
    public TableDataInfo list(NotifyTemplate notifyTemplate) {
        Page<NotifyTemplateVO> voPage = notifyTemplateService.pageNotifyTemplateVO(notifyTemplate);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出通知模版列表
     */
    @PreAuthorize("@ss.hasPermi('notify:template:export')")
    @Log(title = "通知模版", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation("导出通知模版列表")
    public void export(HttpServletResponse response, NotifyTemplate notifyTemplate) {
        Page<NotifyTemplateVO> voPage = notifyTemplateService.pageNotifyTemplateVO(notifyTemplate);
        ExcelUtil<NotifyTemplateVO> util = new ExcelUtil<NotifyTemplateVO>(NotifyTemplateVO.class);
        util.exportExcel(response, voPage.getRecords(), "通知模版数据");
    }

    /**
     * 获取通知模版详细信息
     */
    @PreAuthorize("@ss.hasPermi('notify:template:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取通知模版详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(notifyTemplateService.selectNotifyTemplateById(id));
    }

    /**
     * 新增通知模版
     */
    @PreAuthorize("@ss.hasPermi('notify:template:add')")
    @Log(title = "通知模版", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("新增通知模版")
    public AjaxResult add(@RequestBody NotifyTemplate notifyTemplate) {
        return notifyTemplateService.insertNotifyTemplate(notifyTemplate);
    }

    /**
     * 修改通知模版
     */
    @PreAuthorize("@ss.hasPermi('notify:template:edit')")
    @Log(title = "通知模版", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改通知模版")
    public AjaxResult edit(@RequestBody NotifyTemplate notifyTemplate) {
        return notifyTemplateService.updateNotifyTemplate(notifyTemplate);
    }

    /**
     * 删除通知模版
     */
    @PreAuthorize("@ss.hasPermi('notify:template:remove')")
    @Log(title = "通知模版", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @ApiOperation("删除通知模版")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(notifyTemplateService.deleteNotifyTemplateByIds(ids));
    }

    /**
     * 获取消息通知模版参数信息
     */
    @PreAuthorize("@ss.hasPermi('notify:template:query')")
    @GetMapping(value = "/msgParams")
    @ApiOperation("获取模板参数配置")
    public AjaxResult msgParams(Long channelId, String msgType) {
        return success(notifyTemplateService.getNotifyMsgParams(channelId, msgType));
    }


    /**
     * 获取通知模版详细信息
     */
    @PreAuthorize("@ss.hasPermi('notify:template:query')")
    @GetMapping(value = "/getUsable")
    @ApiOperation("获取同一业务的模板是否有可用的")
    public AjaxResult getUsable(NotifyTemplate notifyTemplate) {
        return success(notifyTemplateService.countNormalTemplate(notifyTemplate));
    }


    /**
     * 修改通知模版-更新选择的为可用，其他为不可用
     */
    @PreAuthorize("@ss.hasPermi('notify:template:edit')")
    @PostMapping("/updateState")
    @ApiOperation("修改模版启用状态")
    public AjaxResult updateState(@RequestBody NotifyTemplate notifyTemplate) {
        notifyTemplateService.updateTemplateStatus(notifyTemplate);
        return AjaxResult.success();
    }

    /**
     * 获取消息通知模版参数变量
     */
    @PreAuthorize("@ss.hasPermi('notify:template:query')")
    @GetMapping(value = "/listVariables")
    @ApiOperation("获取模板内容变量")
    public AjaxResult listVariables(Long id, String channelType, String provider) {
        NotifyTemplate notifyTemplate = notifyTemplateService.selectNotifyTemplateById(id);
        if (Objects.isNull(notifyTemplate)) {
            return success();
        }
        String content = JSONObject.parseObject(notifyTemplate.getMsgParams()).get("content").toString();
        Object account = JSONObject.parseObject(notifyTemplate.getMsgParams()).get("sendAccount");
        NotifyChannelProviderEnum notifyChannelProviderEnum = NotifyChannelProviderEnum.getByChannelTypeAndProvider(channelType, provider);
        List<String> variables = notifyTemplateService.listVariables(content, notifyChannelProviderEnum);
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        for (String variable : variables) {
            map.put(variable, "");
        }
        JSONObject resultData = new JSONObject();
        // 企业微信、钉钉机器人没有发送账号
        if (NotifyChannelProviderEnum.WECHAT_WECOM_ROBOT == notifyChannelProviderEnum ||
                NotifyChannelProviderEnum.DING_TALK_GROUP_ROBOT == notifyChannelProviderEnum) {
            if (MapUtils.isEmpty(map)) {
                return AjaxResult.success(MessageUtils.message("operate.success"), "");
            } else {
                resultData.put("variables", JSON.toJSONString(map));
                return success(resultData);
            }
        }
        resultData.put("sendAccount", Objects.isNull(account) ? "" : account.toString());
        resultData.put("variables", MapUtils.isNotEmpty(map) ? JSON.toJSONString(map) : "");
        return success(resultData);
    }

    /**
     * 获取告警微信小程序模板id
     */
    @GetMapping(value = "/getAlertWechatMini")
    @ApiOperation("获取告警微信小程序模板id")
    public AjaxResult getAlertWechatMini() {
        return success(notifyTemplateService.getAlertWechatMini());
    }

}
