package com.sydh.controller.media;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.annotation.Log;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.enums.BusinessType;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.utils.poi.ExcelUtil;
import com.sydh.common.utils.file.FileUploadUtils;
import com.sydh.sip.domain.SipDevice;
import com.sydh.sip.model.vo.SipDeviceVO;
import com.sydh.sip.service.ISipDeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * 监控设备Controller
 *
 * @author zhuangpeng.li
 * @date 2022-10-07
 */
@Api(tags = "监控设备")
@RestController
@RequestMapping("/sip/device")
public class SipDeviceController extends BaseController
{
    @Autowired
    private ISipDeviceService sipDeviceService;

    /**
     * 查询监控设备列表
     */
    @ApiOperation("查询监控设备列表")
    @PreAuthorize("@ss.hasPermi('iot:video:list')")
    @GetMapping("/list")
    public TableDataInfo list(SipDevice sipDevice)
    {
        Page<SipDeviceVO> voPage = sipDeviceService.pageSipDeviceVO(sipDevice);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    @ApiOperation("查询监控设备树结构")
    @PreAuthorize("@ss.hasPermi('iot:video:list')")
    @GetMapping("/listchannel/{deviceId}")
    public AjaxResult listchannel(@PathVariable("deviceId") String deviceId)
    {
        return AjaxResult.success(sipDeviceService.selectSipDeviceChannelList(deviceId));
    }

    /**
     * 导出监控设备列表
     */
    @ApiOperation("导出监控设备列表")
    @PreAuthorize("@ss.hasPermi('iot:video:list')")
    @Log(title = "监控设备", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SipDevice sipDevice)
    {
        Page<SipDeviceVO> voPage = sipDeviceService.pageSipDeviceVO(sipDevice);
        ExcelUtil<SipDeviceVO> util = new ExcelUtil<SipDeviceVO>(SipDeviceVO.class);
        util.exportExcel(response, voPage.getRecords(), "监控设备数据");
    }

    /**
     * 获取监控设备详细信息
     */
    @ApiOperation("获取监控设备详细信息")
    @PreAuthorize("@ss.hasPermi('iot:video:query')")
    @GetMapping(value = "/{deviceId}")
    public AjaxResult getInfo(@PathVariable("deviceId") String deviceId)
    {
        return AjaxResult.success(sipDeviceService.selectSipDeviceBySipId(deviceId));
    }

    /**
     * 新增监控设备
     */
    @ApiOperation("新增监控设备")
    @PreAuthorize("@ss.hasPermi('iot:video:add')")
    @Log(title = "监控设备", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SipDevice sipDevice)
    {
        return toAjax(sipDeviceService.insertSipDevice(sipDevice));
    }

    /**
     * 修改监控设备
     */
    @ApiOperation("修改监控设备")
    @PreAuthorize("@ss.hasPermi('iot:video:edit')")
    @Log(title = "监控设备", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SipDevice sipDevice)
    {
        return toAjax(sipDeviceService.updateSipDevice(sipDevice));
    }

    /**
     * 删除监控设备
     */
    @ApiOperation("根据设备id批量删除监控设备")
    @PreAuthorize("@ss.hasPermi('iot:video:remove')")
    @Log(title = "监控设备", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deviceIds}")
    public AjaxResult remove(@PathVariable Long[] deviceIds)
    {
        return toAjax(sipDeviceService.deleteSipDeviceByDeviceIds(deviceIds));
    }

    @ApiOperation("根据sipId删除")
    @PreAuthorize("@ss.hasPermi('iot:video:remove')")
    @Log(title = "监控设备", businessType = BusinessType.DELETE)
    @DeleteMapping("/sipid/{sipId}")
    public AjaxResult remove(@PathVariable String sipId)
    {
        return toAjax(sipDeviceService.deleteSipDeviceBySipId(sipId));
    }

    @ApiOperation("根据设备id捕捉")
    @PreAuthorize("@ss.hasPermi('iot:video:query')")
    @GetMapping("/snap/{deviceId}/{channelId}")
    public void getSnap(HttpServletResponse resp, @PathVariable String deviceId, @PathVariable String channelId) {
        try {
            final InputStream in = Files.newInputStream(new File(FileUploadUtils.getDefaultBaseDir() + File.separator + "snap" + File.separator + deviceId + "_" + channelId + ".jpg").toPath());
            resp.setContentType(MediaType.IMAGE_PNG_VALUE);
            IOUtils.copy(in, resp.getOutputStream());
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
