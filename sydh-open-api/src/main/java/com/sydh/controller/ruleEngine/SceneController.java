package com.sydh.controller.ruleEngine;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.annotation.Log;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.enums.BusinessType;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.core.domin.model.LoginUser;
import com.sydh.common.extend.utils.poi.ExcelUtil;
import com.sydh.common.utils.MessageUtils;
import com.sydh.iot.domain.Scene;
import com.sydh.iot.model.vo.SceneVO;
import com.sydh.iot.service.ISceneService;
import com.sydh.iot.service.IScriptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 场景联动Controller
 *
 * @author kerwincui
 * @date 2022-01-13
 */
@Api(tags = "场景联动")
@RestController
@RequestMapping("/iot/scene")
public class SceneController extends BaseController {
    @Resource
    private ISceneService sceneService;

    @Resource
    private IScriptService scriptService;

    /**
     * 查询场景联动列表
     */
    @ApiOperation("查询场景联动列表")
    @PreAuthorize("@ss.hasPermi('iot:scene:list')")
    @GetMapping("/list")
    public TableDataInfo list(Scene scene) {
        Page<SceneVO> voPage = sceneService.pageSceneVO(scene);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出场景联动列表
     */
    @ApiOperation("导出场景联动列表")
    @PreAuthorize("@ss.hasPermi('iot:scene:export')")
    @Log(title = "场景联动", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Scene scene) {
        Page<SceneVO> voPage = sceneService.pageSceneVO(scene);
        ExcelUtil<SceneVO> util = new ExcelUtil<>(SceneVO.class);
        util.exportExcel(response, voPage.getRecords(), "场景联动数据");
    }

    /**
     * 获取场景联动详细信息
     */
    @ApiOperation("获取场景联动详细信息")
    @PreAuthorize("@ss.hasPermi('iot:scene:query')")
    @GetMapping(value = "/{sceneId}")
    public AjaxResult getInfo(@PathVariable("sceneId") Long sceneId) {
        return AjaxResult.success(sceneService.selectSceneBySceneId(sceneId));
    }

    /**
     * 新增场景联动
     */
    @ApiOperation("新增场景联动")
    @PreAuthorize("@ss.hasPermi('iot:scene:add')")
    @Log(title = "场景联动", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SceneVO sceneVO) {
        LoginUser loginUser = getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            return error(MessageUtils.message("user.not.login"));
        }
        // 查询归属机构
        if (null != loginUser.getDeptId()) {
            sceneVO.setUserId(loginUser.getUser().getDept().getDeptUserId());
            sceneVO.setUserName(loginUser.getUser().getDept().getDeptName());
            sceneVO.setTerminalUser(0);
        } else {
            sceneVO.setUserId(loginUser.getUser().getUserId());
            sceneVO.setUserName(loginUser.getUser().getUserName());
            sceneVO.setTerminalUser(1);
        }
        sceneVO.setCreateBy(loginUser.getUsername());
        return toAjax(sceneService.insertScene(sceneVO));
    }

    /**
     * 修改场景联动
     */
    @ApiOperation("修改场景联动")
    @PreAuthorize("@ss.hasPermi('iot:scene:edit')")
    @Log(title = "场景联动", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SceneVO sceneVO) {
        sceneVO.setUpdateBy(getUsername());
        return toAjax(sceneService.updateScene(sceneVO));
    }

    /**
     * 获取规则引擎脚本日志
     */
    @PreAuthorize("@ss.hasPermi('iot:scene:query')")
    @GetMapping(value = "/log/{chainName}")
    public AjaxResult getScriptLog(@PathVariable("chainName") String chainName) {
        return success(scriptService.selectRuleScriptLog("scene", chainName));
    }

    /**
     * 删除场景联动
     */
    @ApiOperation("删除场景联动")
    @PreAuthorize("@ss.hasPermi('iot:scene:remove')")
    @Log(title = "场景联动", businessType = BusinessType.DELETE)
    @DeleteMapping("/{sceneIds}")
    public AjaxResult remove(@PathVariable Long[] sceneIds) {
        return toAjax(sceneService.deleteSceneBySceneIds(sceneIds));
    }

    /**
     * 修改场景联动状态
     */
    @ApiOperation("修改场景联动状态")
    @PreAuthorize("@ss.hasPermi('iot:scene:edit')")
    @Log(title = "场景联动", businessType = BusinessType.UPDATE)
    @PutMapping("/updateStatus")
    public AjaxResult updateStatus(@RequestBody Scene scene) {
        return toAjax(sceneService.updateStatus(scene));
    }

}
