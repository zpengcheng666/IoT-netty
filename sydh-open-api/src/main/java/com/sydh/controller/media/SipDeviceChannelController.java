package com.sydh.controller.media;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.annotation.Log;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.enums.BusinessType;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.utils.poi.ExcelUtil;
import com.sydh.common.utils.MessageUtils;
import com.sydh.sip.domain.SipDeviceChannel;
import com.sydh.sip.model.vo.SipDeviceChannelVO;
import com.sydh.sip.service.ISipDeviceChannelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

/**
 * 监控设备通道信息Controller
 *
 * @author zhuangpeng.li
 * @date 2022-10-07
 */
@Api(tags = "监控设备通道信息")
@RestController
@RequestMapping("/sip/channel")
public class SipDeviceChannelController extends BaseController {
    @Resource
    private ISipDeviceChannelService sipDeviceChannelService;

    /**
     * 查询监控设备通道信息列表
     */
    @ApiOperation("查询监控设备通道信息列表")
    @PreAuthorize("@ss.hasPermi('iot:video:list')")
    @GetMapping("/list")
    public TableDataInfo list(SipDeviceChannel sipDeviceChannel) {
        Page<SipDeviceChannelVO> voPage = sipDeviceChannelService.pageSipDeviceChannelVO(sipDeviceChannel);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出监控设备通道信息列表
     */
    @ApiOperation("导出监控设备通道信息列表")
    @PreAuthorize("@ss.hasPermi('iot:video:list')")
    @Log(title = "监控设备通道信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SipDeviceChannel sipDeviceChannel) {
        Page<SipDeviceChannelVO> voPage = sipDeviceChannelService.pageSipDeviceChannelVO(sipDeviceChannel);
        ExcelUtil<SipDeviceChannelVO> util = new ExcelUtil<SipDeviceChannelVO>(SipDeviceChannelVO.class);
        util.exportExcel(response, voPage.getRecords(), "监控设备通道信息数据");
    }

    /**
     * 获取监控设备通道信息详细信息
     */
    @ApiOperation("获取监控设备通道信息详细信息")
    @PreAuthorize("@ss.hasPermi('iot:video:query')")
    @GetMapping(value = "/{channelId}")
    public AjaxResult getInfo(@PathVariable("channelId") Long channelId) {
        return AjaxResult.success(sipDeviceChannelService.selectSipDeviceChannelByChannelId(channelId));
    }

    /**
     * 新增监控设备通道信息
     */
    @ApiOperation("新增监控设备通道信息")
    @PreAuthorize("@ss.hasPermi('iot:video:add')")
    @Log(title = "监控设备通道信息", businessType = BusinessType.INSERT)
    @PostMapping(value = "/{createNum}")
    public AjaxResult add(@PathVariable("createNum") Long createNum, @RequestBody SipDeviceChannel sipDeviceChannel) {
        String devstr = sipDeviceChannelService.insertSipDeviceChannelGen(createNum, sipDeviceChannel);
        if (!Objects.equals(devstr, "")) {
            return AjaxResult.success(MessageUtils.message("operate.success"), devstr);
        } else {
            return AjaxResult.error(MessageUtils.message("operate.fail"));
        }
    }

    /**
     * 修改监控设备通道信息
     */
    @ApiOperation("修改监控设备通道信息")
    @PreAuthorize("@ss.hasPermi('iot:video:edit')")
    @Log(title = "监控设备通道信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SipDeviceChannel sipDeviceChannel) {
        return toAjax(sipDeviceChannelService.updateSipDeviceChannel(sipDeviceChannel));
    }

    /**
     * 删除监控设备通道信息
     */
    @ApiOperation("删除监控设备通道信息")
    @PreAuthorize("@ss.hasPermi('iot:video:remove')")
    @Log(title = "监控设备通道信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{channelIds}")
    public AjaxResult remove(@PathVariable Long[] channelIds) {
        return toAjax(sipDeviceChannelService.deleteSipDeviceChannelByChannelIds(channelIds));
    }

    /**
     * 查询监控设备通道信息列表
     */
    @ApiOperation("查询监控设备通道信息列表")
    @PreAuthorize("@ss.hasPermi('iot:video:list')")
    @GetMapping("/listRelDeviceOrScene")
    public AjaxResult listRelDeviceOrScene(String serialNumber, Long sceneModelId) {
        List<SipDeviceChannelVO> list = sipDeviceChannelService.listRelDeviceOrScene(serialNumber, sceneModelId);
        return AjaxResult.success(list);
    }
}
