package com.sydh.controller.device;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.annotation.Log;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.enums.BusinessType;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.core.domin.entity.SysUser;
import com.sydh.common.extend.utils.poi.ExcelUtil;
import com.sydh.iot.domain.DeviceRecord;
import com.sydh.iot.model.vo.DeviceRecordVO;
import com.sydh.iot.service.IDeviceRecordService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 设备记录Controller
 *
 * @author zhangzhiyi
 * @date 2024-07-16
 */
@RestController
@RequestMapping("/iot/record")
public class DeviceRecordController extends BaseController
{
    @Resource
    private IDeviceRecordService deviceRecordService;

    /**
     * 查询设备记录列表
     */
    @PreAuthorize("@ss.hasPermi('iot:device:record:list')")
    @GetMapping("/list")
    public TableDataInfo list(DeviceRecord deviceRecord)
    {
        if (null == deviceRecord.getOperateDeptId()) {
            SysUser user = getLoginUser().getUser();
            deviceRecord.setTenantId(user.getDept().getDeptUserId());
        }
        Page<DeviceRecordVO> voPage = deviceRecordService.pageDeviceRecordVO(deviceRecord);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出设备记录列表
     */
    @PreAuthorize("@ss.hasPermi('iot:device:record:export')")
    @Log(title = "设备记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DeviceRecord deviceRecord)
    {
        Page<DeviceRecordVO> voPage = deviceRecordService.pageDeviceRecordVO(deviceRecord);
        ExcelUtil<DeviceRecordVO> util = new ExcelUtil<>(DeviceRecordVO.class);
        util.exportExcel(response, voPage.getRecords(), "设备记录数据");
    }

    /**
     * 获取设备记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:device:record:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(deviceRecordService.selectDeviceRecordById(id));
    }

    /**
     * 删除设备记录
     */
    @PreAuthorize("@ss.hasPermi('iot:device:record:remove')")
    @Log(title = "设备记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(deviceRecordService.deleteDeviceRecordByIds(ids));
    }
}
