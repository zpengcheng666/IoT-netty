package com.sydh.controller.sceneModel;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.annotation.Log;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.enums.BusinessType;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.utils.poi.ExcelUtil;
import com.sydh.iot.domain.SceneModel;
import com.sydh.iot.model.vo.SceneModelVO;
import com.sydh.iot.service.ISceneModelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 场景管理Controller
 *
 * @author kerwincui
 * @date 2024-05-20
 */
@RestController
@RequestMapping("/scene/model")
@Api(tags = "场景管理")
public class SceneModelController extends BaseController
{
    @Resource
    private ISceneModelService sceneModelService;

    /**
     * 查询场景管理列表
     */
    @PreAuthorize("@ss.hasPermi('scene:model:list')")
    @GetMapping("/list")
    @ApiOperation("查询场景管理列表")
    public TableDataInfo list(SceneModelVO sceneModel)
    {
        Page<SceneModelVO> voPage = sceneModelService.pageSceneModelVO(sceneModel);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出场景管理列表
     */
    @PreAuthorize("@ss.hasPermi('scene:model:export')")
    @Log(title = "场景管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation("导出场景管理列表")
    public void export(HttpServletResponse response, SceneModelVO sceneModel)
    {
        Page<SceneModelVO> voPage = sceneModelService.pageSceneModelVO(sceneModel);
        ExcelUtil<SceneModelVO> util = new ExcelUtil<SceneModelVO>(SceneModelVO.class);
        util.exportExcel(response, voPage.getRecords(), "场景管理数据");
    }

    /**
     * 获取场景管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('scene:model:query')")
    @GetMapping(value = "/{sceneModelId}")
    @ApiOperation("获取场景管理详细信息")
    public AjaxResult getInfo(@PathVariable("sceneModelId") Long sceneModelId)
    {
        return success(sceneModelService.selectSceneModelBySceneModelId(sceneModelId));
    }

    /**
     * 新增场景管理
     */
    @PreAuthorize("@ss.hasPermi('scene:model:add')")
    @Log(title = "场景管理", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("新增场景管理")
    public AjaxResult add(@RequestBody SceneModel sceneModel)
    {
        return toAjax(sceneModelService.insertSceneModel(sceneModel));
    }

    /**
     * 修改场景管理
     */
    @PreAuthorize("@ss.hasPermi('scene:model:edit')")
    @Log(title = "场景管理", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改场景管理")
    public AjaxResult edit(@RequestBody SceneModelVO sceneModel)
    {
        return toAjax(sceneModelService.updateSceneModel(sceneModel));
    }

    /**
     * 删除场景管理
     */
    @PreAuthorize("@ss.hasPermi('scene:model:remove')")
    @Log(title = "场景管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{sceneModelIds}")
    @ApiOperation("删除场景管理")
    public AjaxResult remove(@PathVariable Long[] sceneModelIds)
    {
        return toAjax(sceneModelService.deleteSceneModelBySceneModelIds(sceneModelIds));
    }
}
