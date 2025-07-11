package com.sydh.controller.deviceConfig;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.utils.poi.ExcelUtil;
import com.sydh.iot.domain.OrderControl;
import com.sydh.iot.model.vo.OrderControlVO;
import com.sydh.iot.service.IOrderControlService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 指令权限控制Controller
 *
 * @author kerwincui
 * @date 2024-07-01
 */
@RestController
@RequestMapping("/order/control")
@Api(tags = "指令权限控制")
public class OrderControlController extends BaseController
{

    @Resource
    private IOrderControlService orderControlService;

    /**
     * 查询指令权限控制列表
     */
    @PreAuthorize("@ss.hasPermi('order:control:list')")
    @GetMapping("/list")
    @ApiOperation("查询指令权限控制列表")
    public TableDataInfo list(OrderControlVO orderControlVO)
    {
        Page<OrderControlVO> voPage = orderControlService.pageOrderControlVO(orderControlVO);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出指令权限控制列表
     */
    @ApiOperation("导出指令权限控制列表")
    @PreAuthorize("@ss.hasPermi('order:control:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, OrderControlVO orderControlVO)
    {
        Page<OrderControlVO> voPage = orderControlService.pageOrderControlVO(orderControlVO);
        ExcelUtil<OrderControlVO> util = new ExcelUtil<OrderControlVO>(OrderControlVO.class);
        util.exportExcel(response, voPage.getRecords(), "指令权限控制数据");
    }

    /**
     * 获取指令权限控制详细信息
     */
    @PreAuthorize("@ss.hasPermi('order:control:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取指令权限控制详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(orderControlService.selectOrderControlById(id));
    }

    /**
     * 新增指令权限控制
     */
    @PreAuthorize("@ss.hasPermi('order:control:add')")
    @PostMapping
    @ApiOperation("新增指令权限控制")
    public AjaxResult add(@RequestBody OrderControl orderControl)
    {
        return toAjax(orderControlService.insertOrderControl(orderControl));
    }

    /**
     * 修改指令权限控制
     */
    @PreAuthorize("@ss.hasPermi('order:control:edit')")
    @PutMapping
    @ApiOperation("修改指令权限控制")
    public AjaxResult edit(@RequestBody OrderControl orderControl)
    {
        return toAjax(orderControlService.updateOrderControl(orderControl));
    }

    /**
     * 删除指令权限控制
     */
    @PreAuthorize("@ss.hasPermi('order:control:remove')")
    @DeleteMapping("/{ids}")
    @ApiOperation("删除指令权限控制")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(orderControlService.deleteOrderControlByIds(ids));
    }

    @GetMapping(value = "/get")
    @ApiOperation("获取指令权限")
    public AjaxResult getControl(Long deviceId,Long modelId)
    {
        return orderControlService.judgeThingsModel(deviceId, modelId);
    }
}
