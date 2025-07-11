package com.sydh.controller.ruleEngine;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.utils.poi.ExcelUtil;
import com.sydh.iot.domain.Bridge;
import com.sydh.iot.model.vo.BridgeVO;
import com.sydh.iot.service.IBridgeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 数据桥接Controller
 *
 * @author zhuangpeng.li
 * @date 2024-11-18
 */
@RestController
@RequestMapping("/iot/bridge")
@Api(tags = "数据桥接")
public class BridgeController extends BaseController {
    @Resource
    private IBridgeService bridgeService;

    /**
     * 查询数据桥接列表
     */
    @PreAuthorize("@ss.hasPermi('iot:bridge:list')")
    @GetMapping("/list")
    @ApiOperation("查询数据桥接列表")
    public TableDataInfo list(Bridge bridge) {
        Page<BridgeVO> voPage = bridgeService.pageBridgeVO(bridge);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出数据桥接列表
     */
    @ApiOperation("导出数据桥接列表")
    @PreAuthorize("@ss.hasPermi('iot:bridge:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, Bridge bridge) {
        Page<BridgeVO> voPage = bridgeService.pageBridgeVO(bridge);
        ExcelUtil<BridgeVO> util = new ExcelUtil<BridgeVO>(BridgeVO.class);
        util.exportExcel(response, voPage.getRecords(), "数据桥接数据");
    }

    /**
     * 获取数据桥接详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:bridge:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取数据桥接详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(bridgeService.queryByIdWithCache(id));
    }

    /**
     * 新增数据桥接
     */
    @PreAuthorize("@ss.hasPermi('iot:bridge:add')")
    @PostMapping
    @ApiOperation("新增数据桥接")
    public AjaxResult add(@RequestBody Bridge bridge) {
        return toAjax(bridgeService.insertWithCache(bridge));
    }

    /**
     * 修改数据桥接
     */
    @PreAuthorize("@ss.hasPermi('iot:bridge:edit')")
    @PutMapping
    @ApiOperation("修改数据桥接")
    public AjaxResult edit(@RequestBody Bridge bridge) {
        bridge.setUpdateBy(getUsername());
        return toAjax(bridgeService.updateWithCache(bridge));
    }

    /**
     * 删除数据桥接
     */
    @PreAuthorize("@ss.hasPermi('iot:bridge:remove')")
    @DeleteMapping("/{ids}")
    @ApiOperation("删除数据桥接")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(bridgeService.deleteWithCacheByIds(ids, true));
    }

    @PreAuthorize("@ss.hasPermi('iot:bridge:edit')")
    @PostMapping("/connect")
    @ApiOperation("连接数据桥接")
    public AjaxResult connect(@RequestBody Bridge bridge)
    {
        int result = bridgeService.connect(bridge);
        if (result == -1) {
            return AjaxResult.error("当前配置未启用，请先启用");
        }
        return toAjax(result);

    }
}
