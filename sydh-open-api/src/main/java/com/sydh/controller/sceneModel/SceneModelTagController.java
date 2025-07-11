package com.sydh.controller.sceneModel;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.annotation.Log;
import com.sydh.common.constant.HttpStatus;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataExtendInfo;
import com.sydh.common.enums.BusinessType;
import com.sydh.common.exception.ServiceException;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.utils.poi.ExcelUtil;
import com.sydh.common.utils.MessageUtils;
import com.sydh.iot.domain.SceneModelDevice;
import com.sydh.iot.domain.SceneModelTag;
import com.sydh.iot.mapper.SceneModelDataMapper;
import com.sydh.iot.model.vo.SceneModelTagVO;
import com.sydh.iot.service.ISceneModelDeviceService;
import com.sydh.iot.service.ISceneModelTagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;


/**
 * 场景录入型变量Controller
 *
 * @author kerwincui
 * @date 2024-05-21
 */
@RestController
@RequestMapping("/scene/modelTag")
@Api(tags = "场景录入运算变量管理")
public class SceneModelTagController extends BaseController
{
    @Resource
    private ISceneModelTagService sceneModelTagService;
    @Resource
    private ISceneModelDeviceService sceneModelDeviceService;
    @Resource
    private SceneModelDataMapper sceneModelDataMapper;

    /**
     * 查询场景录入型变量列表
     */
    @PreAuthorize("@ss.hasPermi('scene:modelTag:list')")
    @GetMapping("/list")
    @ApiOperation("查询场景录入运算变量列表")
    public TableDataExtendInfo list(SceneModelTag sceneModelTag)
    {
        Page<SceneModelTagVO> voPage = sceneModelTagService.pageSceneModelTagVO(sceneModelTag);
        SceneModelDevice sceneModelDevice = sceneModelDeviceService.selectOneBySceneModelIdAndVariableType(sceneModelTag.getSceneModelId(), sceneModelTag.getVariableType());
        TableDataExtendInfo rspData = new TableDataExtendInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(voPage.getRecords());
        rspData.setTotal(voPage.getTotal());
        rspData.setAllEnable(null != sceneModelDevice ? sceneModelDevice.getAllEnable() : 0);
        return rspData;
    }

    /**
     * 导出场景录入型变量列表
     */
    @PreAuthorize("@ss.hasPermi('scene:modelTag:export')")
    @Log(title = "场景录入型变量", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation("导出场景录入运算变量列表")
    public void export(HttpServletResponse response, SceneModelTag sceneModelTag)
    {
        Page<SceneModelTagVO> voPage = sceneModelTagService.pageSceneModelTagVO(sceneModelTag);
        ExcelUtil<SceneModelTagVO> util = new ExcelUtil<SceneModelTagVO>(SceneModelTagVO.class);
        util.exportExcel(response, voPage.getRecords(), "场景录入型变量数据");
    }

    /**
     * 获取场景录入型变量详细信息
     */
    @PreAuthorize("@ss.hasPermi('scene:modelTag:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取场景录入运算变量详情")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(sceneModelTagService.selectSceneModelTagById(id));
    }

    /**
     * 新增场景录入型变量
     */
    @PreAuthorize("@ss.hasPermi('scene:modelTag:add')")
    @Log(title = "场景录入型变量", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("新增场景录入运算变量")
    public AjaxResult add(@RequestBody SceneModelTagVO sceneModelTagVO)
    {
        sceneModelTagVO.setCreateBy(getUsername());
        return toAjax(sceneModelTagService.insertSceneModelTag(sceneModelTagVO));
    }

    /**
     * 修改场景录入型变量
     */
    @PreAuthorize("@ss.hasPermi('scene:modelTag:edit')")
    @Log(title = "场景录入型变量", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("编辑场景录入运算变量")
    public AjaxResult edit(@RequestBody SceneModelTagVO sceneModelTagVO)
    {
        sceneModelTagVO.setCreateBy(getUsername());
        return toAjax(sceneModelTagService.updateSceneModelTag(sceneModelTagVO));
    }

    /**
     * 删除场景录入型变量
     */
    @PreAuthorize("@ss.hasPermi('scene:modelTag:remove')")
    @Log(title = "场景录入型变量", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    @ApiOperation("删除场景录入运算变量")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        // 校验场景是否被应用到计算公式
        int count = sceneModelDataMapper.checkIsApplyAliasFormule(Arrays.asList(ids), null);
        if (count > 0) {
            throw new ServiceException(MessageUtils.message("sceneModel.current.variable.quote.operate.variable.formula.please.delete"));
        }
        return toAjax(sceneModelTagService.deleteSceneModelTagByIds(ids));
    }
}
