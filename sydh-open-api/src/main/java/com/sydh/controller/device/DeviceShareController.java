package com.sydh.controller.device;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.annotation.Log;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.enums.BusinessType;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.utils.poi.ExcelUtil;
import com.sydh.iot.domain.DeviceShare;
import com.sydh.iot.model.vo.DeviceShareVO;
import com.sydh.iot.service.IDeviceShareService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 设备分享Controller
 *
 * @author kerwincui
 * @date 2024-04-03
 */
@RestController
@RequestMapping("/iot/share")
public class DeviceShareController extends BaseController
{
    @Resource
    private IDeviceShareService deviceShareService;

    /**
     * 查询设备分享列表
     */
    @PreAuthorize("@ss.hasPermi('iot:share:list')")
    @GetMapping("/list")
    public TableDataInfo list(DeviceShare deviceShare)
    {
        Page<DeviceShareVO> list = deviceShareService.selectDeviceShareVOList(deviceShare);
        return getDataTable(list.getRecords(), list.getTotal());
    }

    /**
     * 导出设备分享列表
     */
    @PreAuthorize("@ss.hasPermi('iot:share:export')")
    @Log(title = "设备分享", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DeviceShare deviceShare)
    {
        Page<DeviceShareVO> list = deviceShareService.selectDeviceShareVOList(deviceShare);
        ExcelUtil<DeviceShareVO> util = new ExcelUtil<DeviceShareVO>(DeviceShareVO.class);
        util.exportExcel(response, list.getRecords(), "设备分享数据");
    }

    /**
     * 获取设备分享详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:share:query')")
    @GetMapping(value = "/detail")
    public AjaxResult getInfo(Long deviceId,Long userId)
    {
        return success(deviceShareService.selectDeviceShareByDeviceIdAndUserId(deviceId,userId));
    }

    /**
     * 新增设备分享
     */
    @PreAuthorize("@ss.hasPermi('iot:share:add')")
    @Log(title = "设备分享", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DeviceShare deviceShare)
    {
        return toAjax(deviceShareService.insertDeviceShare(deviceShare));
    }

    /**
     * 修改设备分享
     */
    @PreAuthorize("@ss.hasPermi('iot:share:edit')")
    @Log(title = "设备分享", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DeviceShare deviceShare)
    {
        return toAjax(deviceShareService.updateDeviceShare(deviceShare));
    }

    /**
     * 删除设备分享
     */
    @PreAuthorize("@ss.hasPermi('iot:share:remove')")
    @Log(title = "设备分享", businessType = BusinessType.DELETE)
	@DeleteMapping("/{deviceIds}")
    public AjaxResult remove(@PathVariable Long[] deviceIds)
    {
        return toAjax(deviceShareService.deleteDeviceShareByDeviceIds(deviceIds));
    }

    /**
     * 删除设备分享
     */
    @PreAuthorize("@ss.hasPermi('iot:share:remove')")
    @Log(title = "设备分享", businessType = BusinessType.DELETE)
    @DeleteMapping()
    public AjaxResult delete(@RequestBody DeviceShare deviceShare)
    {
        return toAjax(deviceShareService.deleteDeviceShareByDeviceIdAndUserId(deviceShare));
    }


    /**
     * 获取设备分享用户信息
     */
    @GetMapping("/shareUser")
    @PreAuthorize("@ss.hasPermi('iot:share:user')")
    public AjaxResult userList(DeviceShare share)
    {
        return AjaxResult.success(deviceShareService.selectShareUser(share));
    }
}
