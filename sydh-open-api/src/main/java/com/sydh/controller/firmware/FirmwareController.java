package com.sydh.controller.firmware;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.annotation.Log;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.enums.BusinessType;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.utils.poi.ExcelUtil;
import com.sydh.iot.domain.Firmware;
import com.sydh.iot.service.IFirmwareService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * 产品固件Controller
 *
 * @author kerwincui
 * @date 2021-12-16
 */
@Api(tags = "产品固件")
@RestController
@RequestMapping("/iot/firmware")
public class FirmwareController extends BaseController
{

    @Resource
    private IFirmwareService firmwareService;

    /**
     * 查询产品固件列表
     */
    @PreAuthorize("@ss.hasPermi('iot:firmware:list')")
    @GetMapping("/list")
    @ApiOperation("产品固件分页列表")
    public TableDataInfo list(Firmware firmware)
    {
        Page<Firmware> voPage = firmwareService.selectFirmwareList(firmware);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }


    /**
     * 查询待升级固件版本列表
     */
    @PreAuthorize("@ss.hasPermi('iot:firmware:list')")
    @GetMapping("/upGradeVersionList")
    @ApiOperation("查询待升级固件版本列表")
    public AjaxResult upGradeVersionList(Firmware firmware) {
        return AjaxResult.success(firmwareService.selectUpGradeVersionList(firmware));
    }

    /**
     * 导出产品固件列表
     */
    @PreAuthorize("@ss.hasPermi('iot:firmware:export')")
    @Log(title = "产品固件", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation("导出固件")
    public void export(HttpServletResponse response, Firmware firmware)
    {
        Page<Firmware> list = firmwareService.selectFirmwareList(firmware);
        ExcelUtil<Firmware> util = new ExcelUtil<Firmware>(Firmware.class);
        util.exportExcel(response, list.getRecords(), "产品固件数据");
    }

    /**
     * 获取产品固件详细信息
     */
    @ApiOperation("获取固件详情")
    @PreAuthorize("@ss.hasPermi('iot:firmware:query')")
    @GetMapping(value = "/{firmwareId}")
    public AjaxResult getInfo(@PathVariable("firmwareId") Long firmwareId)
    {
        return AjaxResult.success(firmwareService.getById(firmwareId));
    }

    /**
     * 获取设备最新固件
     */
    @ApiOperation("获取设备最新固件")
    @PreAuthorize("@ss.hasPermi('iot:firmware:query')")
    @GetMapping(value = "/getLatest")
    public AjaxResult getLatest(Long deviceId,Long firmwareType)
    {
        return AjaxResult.success(firmwareService.selectLatestFirmware(deviceId,firmwareType));
    }

    /**
     * 新增产品固件
     */
    @ApiOperation("添加产品固件")
    @PreAuthorize("@ss.hasPermi('iot:firmware:add')")
    @Log(title = "产品固件", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Firmware firmware)
    {
        return toAjax(firmwareService.insertFirmware(firmware));
    }

    /**
     * 修改产品固件
     */
    @ApiOperation("修改产品固件")
    @PreAuthorize("@ss.hasPermi('iot:firmware:edit')")
    @Log(title = "产品固件", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Firmware firmware)
    {
        firmware.setUpdateBy(getUsername());
        return toAjax(firmwareService.update(firmware));
    }

    /**
     * 删除产品固件
     */
    @PreAuthorize("@ss.hasPermi('iot:firmware:remove')")
    @DeleteMapping("/{firmwareIds}")
    @ApiOperation("删除产品固件")
    public AjaxResult remove(@PathVariable Long[] firmwareIds) {
        return toAjax(firmwareService.deleteBatchByIds(Arrays.asList(firmwareIds)));
    }

}
