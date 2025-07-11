package com.sydh.controller.device;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.annotation.Log;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.enums.BusinessType;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.core.domin.entity.SysUser;
import com.sydh.iot.domain.DeviceAlertUser;
import com.sydh.iot.model.vo.DeviceAlertUserVO;
import com.sydh.iot.service.IDeviceAlertUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 设备告警用户Controller
 *
 * @author kerwincui
 * @date 2024-05-15
 */
@RestController
@RequestMapping("/iot/deviceAlertUser")
public class DeviceAlertUserController extends BaseController
{
    @Resource
    private IDeviceAlertUserService deviceAlertUserService;

    /**
     * 查询设备告警用户列表
     */
    @PreAuthorize("@ss.hasPermi('iot:device:alert:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(DeviceAlertUser deviceAlertUser)
    {
        Page<DeviceAlertUserVO> list = deviceAlertUserService.selectDeviceAlertUserList(deviceAlertUser);
        return getDataTable(list.getRecords(), list.getTotal());
    }

    /**
     * 获取设备告警用户详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:device:alert:user:query')")
    @GetMapping("/query")
    public TableDataInfo getUser(SysUser sysUser)
    {
        Page<SysUser> list = deviceAlertUserService.selectUserList(sysUser);
        return getDataTable(list.getRecords(), list.getTotal());
    }

    /**
     * 新增设备告警用户
     */
    @PreAuthorize("@ss.hasPermi('iot:device:alert:user:add')")
    @Log(title = "设备告警用户", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DeviceAlertUserVO deviceAlertUserVO)
    {

        return toAjax(deviceAlertUserService.insertDeviceAlertUser(deviceAlertUserVO));
    }

    /**
     * 删除设备告警用户
     */
    @PreAuthorize("@ss.hasPermi('iot:device:alert:user:remove')")
    @Log(title = "设备告警用户", businessType = BusinessType.DELETE)
	@DeleteMapping
    public AjaxResult remove(@RequestParam Long deviceId, @RequestParam Long userId)
    {
        return toAjax(deviceAlertUserService.deleteByDeviceIdAndUserId(deviceId, userId));
    }
}
