package com.sydh.controller.sceneModel;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.annotation.Log;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.enums.BusinessType;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.enums.scenemodel.SceneModelVariableTypeEnum;
import com.sydh.common.extend.utils.poi.ExcelUtil;
import com.sydh.common.utils.MessageUtils;
import com.sydh.iot.domain.SceneModelDevice;
import com.sydh.iot.model.vo.SceneModelDeviceVO;
import com.sydh.iot.service.ISceneModelDeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 场景管理关联设备Controller
 *
 * @author kerwincui
 * @date 2024-05-21
 */
@RestController
@RequestMapping("/scene/modelDevice")
@Api(tags = "场景关联设备管理")
public class SceneModelDeviceController extends BaseController
{
    @Resource
    private ISceneModelDeviceService sceneModelDeviceService;

    /**
     * 查询场景管理关联设备列表
     */
    @PreAuthorize("@ss.hasPermi('scene:modelDevice:list')")
    @GetMapping("/list")
    @ApiOperation("查询场景关联设备列表")
    public TableDataInfo list(SceneModelDevice sceneModelDevice)
    {
        Page<SceneModelDeviceVO> voPage = sceneModelDeviceService.pageSceneModelDeviceVO(sceneModelDevice);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出场景管理关联设备列表
     */
    @PreAuthorize("@ss.hasPermi('scene:modelDevice:export')")
    @Log(title = "场景管理关联设备", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation("导出场景关联设备列表")
    public void export(HttpServletResponse response, SceneModelDevice sceneModelDevice)
    {
        Page<SceneModelDeviceVO> voPage = sceneModelDeviceService.pageSceneModelDeviceVO(sceneModelDevice);
        ExcelUtil<SceneModelDeviceVO> util = new ExcelUtil<SceneModelDeviceVO>(SceneModelDeviceVO.class);
        util.exportExcel(response, voPage.getRecords(), "场景管理关联设备数据");
    }

    /**
     * 获取场景管理关联设备详细信息
     */
    @PreAuthorize("@ss.hasPermi('scene:modelDevice:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("查询场景关联设备详情")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(sceneModelDeviceService.selectSceneModelDeviceById(id));
    }

    /**
     * 新增场景管理关联设备
     */
    @PreAuthorize("@ss.hasPermi('scene:modelDevice:add')")
    @Log(title = "场景管理关联设备", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("新增场景关联设备")
    public AjaxResult add(@RequestBody SceneModelDevice sceneModelDevice)
    {
        if (null == sceneModelDevice.getSceneModelId()) {
            return AjaxResult.error(MessageUtils.message("please.incoming.scene.id"));
        }
        return toAjax(sceneModelDeviceService.insertSceneModelDevice(sceneModelDevice));
    }

    /**
     * 修改场景管理关联设备
     */
    @PreAuthorize("@ss.hasPermi('scene:modelDevice:edit')")
    @Log(title = "场景管理关联设备", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("编辑场景关联设备")
    public AjaxResult edit(@RequestBody SceneModelDevice sceneModelDevice)
    {
        if (null == sceneModelDevice.getSceneModelId()) {
            return AjaxResult.error(MessageUtils.message("please.incoming.scene.id"));
        }
        return toAjax(sceneModelDeviceService.updateSceneModelDevice(sceneModelDevice));
    }

    /**
     * 修改场景管理关联设备
     */
    @PreAuthorize("@ss.hasPermi('scene:modelDevice:edit')")
    @Log(title = "场景管理关联设备", businessType = BusinessType.UPDATE)
    @PostMapping("/editEnable")
    @ApiOperation("编辑场景关联设备")
    public AjaxResult editEnable(@RequestBody SceneModelDevice sceneModelDevice)
    {
        if (SceneModelVariableTypeEnum.THINGS_MODEL.getType().equals(sceneModelDevice.getVariableType())
                && null == sceneModelDevice.getId()) {
            return AjaxResult.error(MessageUtils.message("please.incoming.device.config.number"));
        }
        return toAjax(sceneModelDeviceService.editEnable(sceneModelDevice));
    }

    /**
     * 删除场景管理关联设备
     */
    @PreAuthorize("@ss.hasPermi('scene:modelDevice:remove')")
    @Log(title = "场景管理关联设备", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    @ApiOperation("删除场景关联设备")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sceneModelDeviceService.deleteSceneModelDeviceByIds(ids));
    }
}
