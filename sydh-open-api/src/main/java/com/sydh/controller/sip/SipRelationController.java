package com.sydh.controller.sip;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.utils.poi.ExcelUtil;
import com.sydh.iot.domain.SipRelation;
import com.sydh.iot.model.vo.SipRelationVO;
import com.sydh.iot.service.ISipRelationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * 监控设备关联Controller
 *
 * @author kerwincui
 * @date 2024-06-06
 */
@RestController
@RequestMapping("/iot/relation")
@Api(tags = "监控设备关联")
public class SipRelationController extends BaseController {
    @Autowired
    private ISipRelationService sipRelationService;

    /**
     * 查询监控设备关联列表
     */
    @PreAuthorize("@ss.hasPermi('iot:relation:list')")
    @GetMapping("/list")
    @ApiOperation("查询监控设备关联列表")
    public TableDataInfo list(SipRelation sipRelation) {
        Page<SipRelationVO> list = sipRelationService.selectSipRelationList(sipRelation);
        return getDataTable(list.getRecords(), list.getTotal());
    }

    /**
     * 导出监控设备关联列表
     */
    @ApiOperation("导出监控设备关联列表")
    @PreAuthorize("@ss.hasPermi('iot:relation:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SipRelation sipRelation) {
        Page<SipRelationVO> list = sipRelationService.selectSipRelationList(sipRelation);
        ExcelUtil<SipRelationVO> util = new ExcelUtil<>(SipRelationVO.class);
        util.exportExcel(response, list.getRecords(), "监控设备关联数据");
    }

    /**
     * 获取监控设备关联详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:relation:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取监控设备关联详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(sipRelationService.selectSipRelationById(id));
    }

    @PreAuthorize("@ss.hasPermi('iot:relation:query')")
    @GetMapping(value = "/dev/{deviceId}")
    @ApiOperation("根据设备id获取关联通道详细信息")
    public AjaxResult getInfoByDeviceId(@PathVariable("deviceId") Long deviceId) {
        return success(sipRelationService.selectSipRelationByDeviceId(deviceId));
    }

    /**
     * 新增或更新监控设备关联
     */
    @PreAuthorize("@ss.hasPermi('iot:relation:add')")
    @PostMapping("/addOrUp")
    @ApiOperation("新增或更新监控设备关联")
    public AjaxResult addOrUp(@RequestBody SipRelation sipRelation) {
        return toAjax(sipRelationService.addOrUpdateSipRelation(sipRelation));
    }

    /**
     * 修改监控设备关联
     */
    @PreAuthorize("@ss.hasPermi('iot:relation:edit')")
    @PutMapping
    @ApiOperation("修改监控设备关联")
    public AjaxResult edit(@RequestBody SipRelation sipRelation) {
        return toAjax(sipRelationService.updateSipRelation(sipRelation));
    }

    /**
     * 删除监控设备关联
     */
    @PreAuthorize("@ss.hasPermi('iot:relation:remove')")
    @DeleteMapping("/{ids}")
    @ApiOperation("删除监控设备关联")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(sipRelationService.deleteSipRelationByIds(ids));
    }
}
