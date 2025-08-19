package com.sydh.controller.device;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.annotation.Log;
import com.sydh.common.constant.HttpStatus;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.enums.BusinessType;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.core.domin.entity.SysRole;
import com.sydh.common.extend.core.domin.model.LoginUser;
import com.sydh.common.extend.utils.SecurityUtils;
import com.sydh.common.extend.utils.poi.ExcelUtil;
import com.sydh.common.utils.MessageUtils;
import com.sydh.common.exception.ServiceException;
import com.sydh.common.utils.StringUtils;
import com.sydh.iot.domain.Device;
import com.sydh.iot.model.DeviceShortOutput;
import com.sydh.iot.model.vo.DeviceAssignmentVO;
import com.sydh.iot.model.vo.DeviceImportVO;
import com.sydh.iot.model.DeviceRelateUserInput;
import com.sydh.iot.model.vo.DeviceVO;
import com.sydh.iot.model.vo.SerialNumberVO;
import com.sydh.iot.service.IDeviceService;
import com.sydh.iot.model.dto.ThingsModelDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.quartz.SchedulerException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 设备Controller
 *
 * @author kerwincui
 * @date 2021-12-16
 */
@Api(tags = "设备管理")
@RestController
@RequestMapping("/iot/device")
public class DeviceController extends BaseController {

    @Resource
    private IDeviceService deviceService;

    /**
     * 查询设备列表
     */
    @PreAuthorize("@ss.hasPermi('iot:device:list')")
    @GetMapping("/list")
    @ApiOperation("设备分页列表")
    public TableDataInfo list(DeviceVO deviceVO) {
        Page<DeviceVO> voPage = deviceService.pageDeviceVO(deviceVO);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 查询未分配授权码设备列表
     */
    @PreAuthorize("@ss.hasPermi('iot:device:list')")
    @GetMapping("/unAuthlist")
    @ApiOperation("设备分页列表")
    public TableDataInfo unAuthlist(DeviceVO deviceVO) {
        Page<Device> devicePage = deviceService.selectUnAuthDeviceList(deviceVO);
        return getDataTable(devicePage.getRecords(), devicePage.getTotal());
    }

    /**
     * 查询分组可添加设备
     */
    @PreAuthorize("@ss.hasPermi('iot:device:list')")
    @GetMapping("/listByGroup")
    @ApiOperation("查询分组可添加设备分页列表")
    public TableDataInfo listByGroup(DeviceVO deviceVO) {
        LoginUser loginUser = getLoginUser();
        if (null == loginUser.getDeptId()) {
            deviceVO.setTenantId(loginUser.getUserId());
            return getDataTable(deviceService.listTerminalUserByGroup(deviceVO));
        }
        Page<Device> devicePage = deviceService.selectDeviceListByGroup(deviceVO);
        return getDataTable(devicePage.getRecords(), devicePage.getTotal());
    }

    /**
     * 查询设备简短列表，主页列表数据
     */
    @PreAuthorize("@ss.hasPermi('iot:device:list')")
    @GetMapping("/shortList")
    @ApiOperation("设备分页简短列表")
    public TableDataInfo shortList(DeviceVO deviceVO) {
//        LoginUser loginUser = getLoginUser();
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser.getDeptId()) {
            // 终端用户查询设备
            deviceVO.setTenantId(loginUser.getUserId());
            Page<DeviceShortOutput> deviceShortOutputPage = deviceService.listTerminalUser(deviceVO);
            return getDataTable(deviceShortOutputPage.getRecords(), deviceShortOutputPage.getTotal());
        }
        if (Objects.isNull(deviceVO.getTenantId())) {
//            deviceVO.setTenantId(getLoginUser().getUserId());
            deviceVO.setTenantId(loginUser.getUserId());
        }
        Page<DeviceShortOutput> deviceShortOutputPage = deviceService.selectDeviceShortList(deviceVO);
        return getDataTable(deviceShortOutputPage.getRecords(), deviceShortOutputPage.getTotal());
    }

    /**
     * 查询所有设备简短列表
     */
    @PreAuthorize("@ss.hasPermi('iot:device:list')")
    @GetMapping("/all")
    @ApiOperation("查询所有设备简短列表")
    public TableDataInfo allShortList(DeviceVO deviceVO) {
        DeviceVO queryDevice = new DeviceVO();
        return getDataTable(deviceService.selectAllDeviceShortList(queryDevice));
    }

    /**
     * 导出设备列表
     */
    @PreAuthorize("@ss.hasPermi('iot:device:export')")
    @Log(title = "设备", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation("导出设备")
    public void export(HttpServletResponse response, DeviceVO deviceVO) {
        Page<DeviceVO> voPage = deviceService.pageDeviceVO(deviceVO);
        ExcelUtil<DeviceVO> util = new ExcelUtil<DeviceVO>(DeviceVO.class);
        util.exportExcel(response, voPage.getRecords(), "设备数据");
    }

    /**
     * 获取设备详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:device:query')")
    @GetMapping(value = "/{deviceId}")
    @ApiOperation("获取设备详情")
    public AjaxResult getInfo(@PathVariable("deviceId") Long deviceId) {
        DeviceVO deviceVO = deviceService.selectDeviceByDeviceId(deviceId);
        // 判断当前用户是否有设备分享权限 （设备所属机构管理员和设备所属用户有权限）
//        LoginUser loginUser = getLoginUser();
        LoginUser loginUser = SecurityUtils.getLoginUser();
        List<SysRole> roles = loginUser.getUser().getRoles();
        //判断当前用户是否为设备所属机构管理员
        if (roles.stream().anyMatch(a -> "admin".equals(a.getRoleKey()))) {
            deviceVO.setIsOwner(1);
        } else {
            //判断当前用户是否是设备所属用户
            if (Objects.equals(deviceVO.getTenantId(), loginUser.getUserId())) {
                deviceVO.setIsOwner(1);
            } else {
                deviceVO.setIsOwner(deviceVO.getCreateBy().equals(loginUser.getUsername()) ? 1 : 0);
            }
        }
        return AjaxResult.success(deviceVO);
    }

    /**
     * 根据设备编号详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:device:query')")
    @GetMapping(value = "/getDeviceBySerialNumber/{serialNumber}")
    @ApiOperation("根据设备编号获取设备详情")
    public AjaxResult getInfoBySerialNumber(@PathVariable("serialNumber") String serialNumber) {
        return AjaxResult.success(deviceService.selectDeviceBySerialNumber(serialNumber));
    }

    /**
     * 获取设备统计信息
     */
    @PreAuthorize("@ss.hasPermi('iot:device:query')")
    @GetMapping(value = "/statistic")
    @ApiOperation("获取设备统计信息")
    public AjaxResult getDeviceStatistic() {
        return AjaxResult.success(deviceService.selectDeviceStatistic());
    }

    /**
     * 获取设备详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:device:query')")
    @GetMapping(value = "/runningStatus")
    @ApiOperation("获取设备详情和运行状态")
    public AjaxResult getRunningStatusInfo(Long deviceId) {
        return AjaxResult.success(deviceService.selectDeviceRunningStatusByDeviceId(deviceId));
    }

    /**
     * 新增设备
     */
    @PreAuthorize("@ss.hasPermi('iot:device:add')")
    @Log(title = "添加设备", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("添加设备")
    public AjaxResult add(@RequestBody DeviceVO deviceVO) {
        return AjaxResult.success(deviceService.insertDevice(deviceVO));
    }

    /**
     * TODO --APP
     * 终端用户绑定设备
     */
    @PreAuthorize("@ss.hasPermi('iot:device:add')")
    @Log(title = "设备关联用户", businessType = BusinessType.UPDATE)
    @PostMapping("/relateUser")
    @ApiOperation("终端-设备关联用户")
    public AjaxResult relateUser(@RequestBody DeviceRelateUserInput deviceRelateUserInput) {
        if (deviceRelateUserInput.getUserId() == 0 || deviceRelateUserInput.getUserId() == null) {
            return AjaxResult.error(MessageUtils.message("device.user.id.null"));
        }
        if (deviceRelateUserInput.getDeviceNumberAndProductIds() == null || deviceRelateUserInput.getDeviceNumberAndProductIds().size() == 0) {
            return AjaxResult.error(MessageUtils.message("device.product.id.null"));
        }
        return deviceService.deviceRelateUser(deviceRelateUserInput);
    }

    /**
     * 修改设备
     */
    @PreAuthorize("@ss.hasPermi('iot:device:edit')")
    @Log(title = "修改设备", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改设备")
    public AjaxResult edit(@RequestBody Device device) {
        device.setUpdateBy(getUsername());
        return deviceService.updateDevice(device);
    }

    /**
     * 重置设备状态
     */
    @PreAuthorize("@ss.hasPermi('iot:device:edit')")
    @Log(title = "重置设备状态", businessType = BusinessType.UPDATE)
    @PutMapping("/reset/{serialNumber}")
    @ApiOperation("重置设备状态")
    public AjaxResult resetDeviceStatus(@PathVariable String serialNumber) {
        return toAjax(deviceService.resetDeviceStatus(serialNumber));
    }

    /**
     * 删除设备
     */
    @PreAuthorize("@ss.hasPermi('iot:device:remove')")
    @Log(title = "删除设备", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deviceIds}")
    @ApiOperation("批量删除设备")
    public AjaxResult remove(@PathVariable Long[] deviceIds) throws SchedulerException {
        List<Long> ids = new ArrayList<>();
        ArrayList<Long> sceneModelIds = new ArrayList<>();
        for (Long deviceId : deviceIds) {
            int i = deviceService.deleteDeviceByDeviceId(deviceId);
            if (1 == i) {
                ids.add(deviceId);
            }
            if (2 == i) {
                sceneModelIds.add(deviceId);
            }
        }
        if (CollectionUtils.isNotEmpty(ids)) {
            return AjaxResult.error(StringUtils.format(MessageUtils.message("device.delete.fail.please.delete.device.scene"), JSONObject.toJSONString(ids)));
        }
        if (CollectionUtils.isNotEmpty(sceneModelIds)) {
            return AjaxResult.error(StringUtils.format(MessageUtils.message("delete.fail.please.delete.scene.model"), JSONObject.toJSONString(sceneModelIds)));
        }
        return AjaxResult.success();
    }

    /**
     * 生成设备编号
     */
    @PreAuthorize("@ss.hasPermi('iot:device:add')")
    @GetMapping("/generator")
    @ApiOperation("生成设备编号")
    public AjaxResult generatorDeviceNum(Integer type) {
        return AjaxResult.success(MessageUtils.message("operate.success"), deviceService.generationDeviceNum(type));
    }

    /**
     * 获取设备MQTT连接参数
     *
     * @param deviceId 设备主键id
     * @return
     */
    @PreAuthorize("@ss.hasPermi('iot:device:query')")
    @GetMapping("/getMqttConnectData")
    @ApiOperation("获取设备MQTT连接参数")
    public AjaxResult getMqttConnectData(Long deviceId) {
        return AjaxResult.success(deviceService.getMqttConnectData(deviceId));
    }

    @PreAuthorize("@ss.hasPermi('iot:device:query')")
    @GetMapping("/getHttpAuthData")
    @ApiOperation("获取设备HTTP认证参数")
    public AjaxResult getHttpAuthData(Long deviceId) {
        return AjaxResult.success(deviceService.getHttpAuthData(deviceId));
    }

    @PreAuthorize("@ss.hasPermi('iot:device:add')")
    @ApiOperation("下载设备导入模板")
    @PostMapping("/uploadTemplate")
    public void uploadTemplate(HttpServletResponse response, @RequestParam(name = "type") Integer type) {
        // 1-设备导入；2-设备分配
        if (1 == type) {
            ExcelUtil<DeviceImportVO> util = new ExcelUtil<>(DeviceImportVO.class);
            util.importTemplateExcel(response, "设备导入");
        } else if (2 == type) {
            ExcelUtil<DeviceAssignmentVO> util = new ExcelUtil<>(DeviceAssignmentVO.class);
            util.importTemplateExcel(response, "设备分配");
        }
    }

    @PreAuthorize("@ss.hasPermi('iot:device:add')")
    @ApiOperation("批量导入设备")
    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(@RequestParam("file") MultipartFile file, @RequestParam("productId") Long productId) throws Exception {
        if (null == file) {
            return error(MessageUtils.message("import.failed.file.null"));
        }
        ExcelUtil<DeviceImportVO> util = new ExcelUtil<>(DeviceImportVO.class);
        List<DeviceImportVO> deviceImportVOList = util.importExcel(file.getInputStream());
        if (CollectionUtils.isEmpty(deviceImportVOList)) {
            return error(MessageUtils.message("import.failed.data.null"));
        }
        DeviceImportVO deviceImportVO = deviceImportVOList.stream().filter(d -> StringUtils.isEmpty(d.getDeviceName())).findAny().orElse(null);
        if (null != deviceImportVO) {
            return error(MessageUtils.message("import.failed.device.name.null"));
        }
        String message = deviceService.importDevice(deviceImportVOList, productId);
        return StringUtils.isEmpty(message) ? success(MessageUtils.message("import.success")) : error(message);
    }

    @PreAuthorize("@ss.hasPermi('iot:device:assignment')")
    @ApiOperation("批量导入分配设备")
    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @PostMapping("/importAssignmentData")
    public AjaxResult importAssignmentData(@RequestParam("file") MultipartFile file,
                                           @RequestParam("productId") Long productId,
                                           @RequestParam("deptId") Long deptId) throws Exception {
        if (null == file) {
            return error(MessageUtils.message("import.failed.file.null"));
        }
        ExcelUtil<DeviceAssignmentVO> util = new ExcelUtil<>(DeviceAssignmentVO.class);
        List<DeviceAssignmentVO> deviceAssignmentVOS = util.importExcel(file.getInputStream());
        if (CollectionUtils.isEmpty(deviceAssignmentVOS)) {
            return error(MessageUtils.message("import.failed.device.name.null"));
        }
        DeviceAssignmentVO deviceAssignmentVO = deviceAssignmentVOS.stream().filter(d -> StringUtils.isEmpty(d.getDeviceName())).findAny().orElse(null);
        if (null != deviceAssignmentVO) {
            return error(MessageUtils.message("import.failed.device.name.null"));
        }
        String message = deviceService.importAssignmentDevice(deviceAssignmentVOS, productId, deptId);
        return StringUtils.isEmpty(message) ? success(MessageUtils.message("import.success")) : error(message);
    }

    /**
     * 分配设备
     *
     * @param deptId 机构id
     * @return com.sydh.common.core.domain.AjaxResult
     * @param: deviceIds 设备id字符串
     */
    @PreAuthorize("@ss.hasPermi('iot:device:assignment')")
    @ApiOperation("分配设备")
    @PostMapping("/assignment")
    public AjaxResult assignment(@RequestParam("deptId") Long deptId,
                                 @RequestParam("deviceIds") String deviceIds) {
        if (null == deptId) {
            return error(MessageUtils.message("device.dept.id.null"));
        }
        if (StringUtils.isEmpty(deviceIds)) {
            return error(MessageUtils.message("device.id.null"));
        }
        return deviceService.assignment(deptId, deviceIds);
    }

    /**
     * 回收设备
     *
     * @return com.sydh.common.core.domain.AjaxResult
     * @param: deviceIds 设备id字符串
     */
    @PreAuthorize("@ss.hasPermi('iot:device:recovery')")
    @ApiOperation("回收设备")
    @PostMapping("/recovery")
    public AjaxResult recovery(@RequestParam("deviceIds") String deviceIds,
                               @RequestParam("recoveryDeptId") Long recoveryDeptId) {
        if (StringUtils.isEmpty(deviceIds)) {
            return error(MessageUtils.message("device.not.select"));
        }
        return deviceService.recovery(deviceIds, recoveryDeptId);
    }

    /**
     * 批量生成设备编号
     */
    @PreAuthorize("@ss.hasPermi('iot:device:batchGenerator')")
    @PostMapping("/batchGenerator")
    @ApiOperation("批量生成设备编号")
    public void batchGeneratorDeviceNum(HttpServletResponse response,
                                        @RequestParam("count") Integer count) {
        if (count > 200) {
            throw new ServiceException(MessageUtils.message("device.serialNumber.allow.generate.max.number"));
        }
        List<SerialNumberVO> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            SerialNumberVO serialNumberVO = new SerialNumberVO();
            String serialNumber = deviceService.generationDeviceNum(1);
            serialNumberVO.setSerialNumber(serialNumber);
            list.add(serialNumberVO);
        }
        ExcelUtil<SerialNumberVO> util = new ExcelUtil<>(SerialNumberVO.class);
        util.exportExcel(response, list, "设备编号");
    }

    /**
     * 查询变量概况
     */
    @PreAuthorize("@ss.hasPermi('iot:device:query')")
    @GetMapping("/listThingsModel")
    @ApiOperation("查询变量概况")
    public TableDataInfo listThingsModel(Integer pageNum, Integer pageSize, Long deviceId, String modelName, Integer type, Integer isMonitor, Integer isReadonly) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        List<ThingsModelDTO> thingsModelDTOList = deviceService.listThingsModel(deviceId);
        if (CollectionUtils.isEmpty(thingsModelDTOList)) {
            rspData.setRows(thingsModelDTOList);
            rspData.setTotal(thingsModelDTOList.size());
            return rspData;
        }
        List<Predicate<ThingsModelDTO>> predicateList = new ArrayList<>();
        if (StringUtils.isNotEmpty(modelName)) {
            predicateList.add(o -> o.getModelName().contains(modelName));
        }
        if (null != type) {
            predicateList.add(o -> Objects.equals(o.getType(), type));
        }
        if (null != isMonitor) {
            predicateList.add(o -> Objects.equals(isMonitor, o.getIsMonitor()));
        }
        if (null != isReadonly) {
            predicateList.add(o -> Objects.equals(isReadonly, o.getIsReadonly()));
            predicateList.add(o -> !Objects.equals(3, o.getType()));
        }
        Stream<ThingsModelDTO> stream = thingsModelDTOList.stream();
        for (Predicate<ThingsModelDTO> predicate : predicateList) {
            stream = stream.filter(predicate);
        }
        List<ThingsModelDTO> filterList = stream.collect(Collectors.toList());
        filterList.sort(Comparator.comparing(ThingsModelDTO::getModelOrder).reversed().thenComparing(ThingsModelDTO::getModelId));
        if (CollectionUtils.isNotEmpty(filterList)) {
            List resultList = com.sydh.common.utils.collection.CollectionUtils.startPage(filterList, pageNum, pageSize);
            rspData.setRows(resultList);
        } else {
            rspData.setRows(new ArrayList<>());
        }
        rspData.setTotal(filterList.size());
        return rspData;
    }

    /**
     * 查询ModbusTcp设备主站列表
     */
    @PreAuthorize("@ss.hasPermi('iot:device:list')")
    @GetMapping("/pageModbusTcpHost")
    @ApiOperation("查询ModbusTcp设备主站列表")
    public TableDataInfo pageModbusTcpHost(DeviceVO deviceVO) {
        // 限制当前用户机构
        if (null == deviceVO.getTenantId()) {
            deviceVO.setTenantId(getLoginUser().getUser().getDept().getDeptUserId());
        }
        Page<DeviceVO> voPage = deviceService.pageModbusTcpHost(deviceVO);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 查询已删除设备列表（逻辑删除：del_flag = NULL）
     */
    @PreAuthorize("@ss.hasPermi('iot:recovery:device')")
    @GetMapping("/list/deleted")
    @ApiOperation("查询已删除设备列表")
    public TableDataInfo listDeletedDevices(DeviceVO device) {
        Page<Device> voPage = deviceService.pageDeleteDeviceVO(device);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 还原已删除设备（del_flag = NULL -> 0）
     */
    @PreAuthorize("@ss.hasPermi('iot:recovery:restore')")
    @Log(title = "还原设备", businessType = BusinessType.UPDATE)
    @PutMapping("/restore")
    @ApiOperation("还原逻辑删除设备")
    public AjaxResult restoreDevice(Long deviceId) {
        return deviceService.restoreDeviceByDeviceId(deviceId);
    }

    /**
     * 物理删除逻辑删除的设备（del_flag = NULL 的设备）
     */
    @PreAuthorize("@ss.hasPermi('iot:recovery:delete')")
    @Log(title = "物理删除设备", businessType = BusinessType.DELETE)
    @DeleteMapping("/physical/delete/{deviceIds}")
    @ApiOperation("物理删除逻辑删除的设备")
    public AjaxResult deleteDeviceBySerialNumber(@PathVariable Long[] deviceIds) {
        return deviceService.deleteDeviceByIds(deviceIds);
    }

}
