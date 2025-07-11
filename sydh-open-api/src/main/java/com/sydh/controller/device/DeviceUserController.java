package com.sydh.controller.device;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.annotation.Log;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.enums.BusinessType;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.utils.MessageUtils;
import com.sydh.iot.domain.DeviceUser;
import com.sydh.iot.service.IDeviceUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 设备用户Controller
 *
 * @author kerwincui
 * @date 2021-12-16
 */
@Api(tags = "设备用户")
@RestController
@RequestMapping("/iot/deviceUser")
public class DeviceUserController extends BaseController
{
    @Resource
    private IDeviceUserService deviceUserService;

    /**
     * 查询设备用户列表
     */
    @PreAuthorize("@ss.hasPermi('iot:device:user:list')")
    @GetMapping("/list")
    @ApiOperation("设备用户分页列表")
    public TableDataInfo list(DeviceUser deviceUser)
    {
        Page<DeviceUser> deviceUserPage = deviceUserService.selectDeviceUserList(deviceUser);
        return getDataTable(deviceUserPage.getRecords(), deviceUserPage.getTotal());
    }

    /**
     * 获取设备分享用户信息
     */
    @PreAuthorize("@ss.hasPermi('iot:device:user:query')")
    @GetMapping("/shareUser")
    public AjaxResult userList(DeviceUser user)
    {
        return AjaxResult.success(deviceUserService.selectShareUser(user));
    }

    /**
     * 获取设备用户详细信息 根据deviceId 查询的话可能会查出多个
     */
    @PreAuthorize("@ss.hasPermi('iot:device:user:query')")
    @GetMapping(value = "/{deviceId}")
    @ApiOperation("获取设备用户详情")
    public AjaxResult getInfo(@PathVariable("deviceId") Long deviceId)
    {
        return AjaxResult.success(deviceUserService.selectDeviceUserByDeviceId(deviceId));
    }

    /**
     * 获取设备用户详细信息 双主键 device_id 和 user_id
     */
    @PreAuthorize("@ss.hasPermi('iot:device:user:query')")
    @GetMapping(value = "/{deviceId}/{userId}")
    @ApiOperation("获取设备用户详情,根据用户id 和 设备id")
    public AjaxResult getInfo(@PathVariable("deviceId") Long deviceId, @PathVariable("userId") Long userId)
    {
        return AjaxResult.success(deviceUserService.selectDeviceUserByDeviceIdAndUserId(deviceId, userId));
    }

    /**
     * 新增设备用户
     */
    @PreAuthorize("@ss.hasPermi('iot:device:user:share')")
    @Log(title = "设备用户", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("添加设备用户")
    public AjaxResult add(@RequestBody DeviceUser deviceUser)
    {
        return toAjax(deviceUserService.insertDeviceUser(deviceUser));
    }

    /**
     * 新增多个设备用户
     */
    @PreAuthorize("@ss.hasPermi('iot:device:user:share')")
    @Log(title = "设备用户", businessType = BusinessType.INSERT)
    @PostMapping("/addDeviceUsers")
    @ApiOperation("批量添加设备用户")
    public AjaxResult addDeviceUsers(@RequestBody List<DeviceUser> deviceUsers)
    {
        return toAjax(deviceUserService.insertDeviceUserList(deviceUsers));
    }

    /**
     * 修改设备用户
     */
    @ApiOperation("修改设备用户")
    @PreAuthorize("@ss.hasPermi('iot:device:user:edit')")
    @Log(title = "设备用户", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DeviceUser deviceUser)
    {
        return toAjax(deviceUserService.updateDeviceUser(deviceUser));
    }


    /**
     * 删除设备用户
     */
    @ApiOperation("删除设备用户")
    @PreAuthorize("@ss.hasPermi('iot:device:user:remove')")
    @Log(title = "设备用户", businessType = BusinessType.DELETE)
    @DeleteMapping
    public AjaxResult remove(@RequestBody DeviceUser deviceUser)
    {
        int count=deviceUserService.deleteDeviceUser(deviceUser);
        if(count==0){
            return AjaxResult.error(MessageUtils.message("device.user.delete.failed.user.not.valid"));
        }else{
            return AjaxResult.success();
        }
    }
}
