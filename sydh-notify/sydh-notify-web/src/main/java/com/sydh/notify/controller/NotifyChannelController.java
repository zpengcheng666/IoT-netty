package com.sydh.notify.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.annotation.Log;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.enums.BusinessType;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.utils.poi.ExcelUtil;
import com.sydh.notify.domain.NotifyChannel;
import com.sydh.notify.service.INotifyChannelService;
import com.sydh.notify.vo.NotifyChannelVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 通知渠道Controller
 *
 * @author kerwincui
 * @date 2023-12-01
 */
@RestController
@RequestMapping("/notify/channel")
@Api(tags = "通知渠道")
public class NotifyChannelController extends BaseController
{
    @Resource
    private INotifyChannelService notifyChannelService;

    /**
     * 查询通知渠道列表
     */
    @PreAuthorize("@ss.hasPermi('notify:channel:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询通知渠道列表")
    public TableDataInfo list(NotifyChannel notifyChannel)
    {
        Page<NotifyChannelVO> voPage = notifyChannelService.pageNotifyChannelVO(notifyChannel);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出通知渠道列表
     */
    @ApiOperation(value = "导出通知渠道列表")
    @PreAuthorize("@ss.hasPermi('notify:channel:export')")
    @Log(title = "通知渠道", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, NotifyChannel notifyChannel)
    {
        Page<NotifyChannelVO> voPage = notifyChannelService.pageNotifyChannelVO(notifyChannel);
        ExcelUtil<NotifyChannelVO> util = new ExcelUtil<NotifyChannelVO>(NotifyChannelVO.class);
        util.exportExcel(response, voPage.getRecords(), "通知渠道数据");
    }

    /**
     * 获取通知渠道详细信息
     */
    @PreAuthorize("@ss.hasPermi('notify:channel:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation(value = "获取通知渠道详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(notifyChannelService.selectNotifyChannelById(id));
    }

    /**
     * 新增通知渠道
     */
    @PreAuthorize("@ss.hasPermi('notify:channel:add')")
    @Log(title = "通知渠道", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation(value = "新增通知渠道")
    public AjaxResult add(@RequestBody NotifyChannel notifyChannel)
    {
        return toAjax(notifyChannelService.insertNotifyChannel(notifyChannel));
    }

    /**
     * 修改通知渠道
     */
    @PreAuthorize("@ss.hasPermi('notify:channel:edit')")
    @Log(title = "通知渠道", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation(value = "修改通知渠道")
    public AjaxResult edit(@RequestBody NotifyChannel notifyChannel)
    {
        return toAjax(notifyChannelService.updateNotifyChannel(notifyChannel));
    }

    /**
     * 删除通知渠道
     */
    @PreAuthorize("@ss.hasPermi('notify:channel:remove')")
    @Log(title = "通知渠道", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    @ApiOperation(value = "删除通知渠道")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(notifyChannelService.deleteNotifyChannelByIds(ids));
    }

    /**
     * 查询通知渠道和服务商
     * @return 结果
     */
    @GetMapping("/listChannel")
    @ApiOperation(value = "查询通知渠道和服务商")
    public AjaxResult listChannel() {
        return AjaxResult.success(notifyChannelService.listChannel());
    }

    /**
     * 获取消息通知渠道参数信息
     * @param channelType 渠道类型
     * @param: provider 服务商
     * @return com.sydh.common.core.domain.AjaxResult
     */
    @GetMapping(value = "/getConfigContent")
    @ApiOperation("获取渠道参数配置")
    public AjaxResult msgParams(String channelType, String provider) {
        return success(notifyChannelService.getConfigContent(channelType, provider));
    }
}
