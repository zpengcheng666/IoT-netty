package com.sydh.controller.deviceConfig;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.annotation.Log;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.enums.BusinessType;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.utils.poi.ExcelUtil;
import com.sydh.iot.domain.ThingsModelJsonTemplate;
import com.sydh.iot.domain.ThingsModelTemplate;
import com.sydh.iot.model.vo.ThingsModelTemplateVO;
import com.sydh.iot.service.IThingsModelTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 通用物模型Controller
 *
 * @author kerwincui
 * @date 2021-12-16
 */
@RestController
@RequestMapping("/iot/template")
@Api(tags = "通用物模型")
public class ThingsModelTemplateController extends BaseController {

    @Resource
    private IThingsModelTemplateService thingsModelTemplateService;

    /**
     * 查询通用物模型列表
     */
    @PreAuthorize("@ss.hasPermi('iot:template:list')")
    @GetMapping("/list")
    @ApiOperation("通用物模型分页列表")
    public TableDataInfo list(ThingsModelTemplateVO thingsModelTemplateVO) {
        Page<ThingsModelTemplateVO> voPage = thingsModelTemplateService.pageThingsModelTemplateVO(thingsModelTemplateVO);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出通用物模型列表
     */
    @PreAuthorize("@ss.hasPermi('iot:template:export')")
    @Log(title = "通用物模型", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation("导出通用物模型")
    public void export(HttpServletResponse response, ThingsModelTemplateVO thingsModelTemplateVO) {
        Page<ThingsModelTemplateVO> voPage = thingsModelTemplateService.pageThingsModelTemplateVO(thingsModelTemplateVO);
        ExcelUtil<ThingsModelTemplateVO> util = new ExcelUtil<>(ThingsModelTemplateVO.class);
        util.exportExcel(response, voPage.getRecords(), "通用物模型数据");
    }

    /**
     * 获取通用物模型详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:template:query')")
    @GetMapping(value = "/{templateId}")
    @ApiOperation("获取通用物模型详情")
    public AjaxResult getInfo(@PathVariable("templateId") Long templateId) {
        return AjaxResult.success(thingsModelTemplateService.selectThingsModelTemplateByTemplateId(templateId));
    }

    /**
     * 新增通用物模型
     */
    @PreAuthorize("@ss.hasPermi('iot:template:add')")
    @Log(title = "通用物模型", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("添加通用物模型")
    public AjaxResult add(@RequestBody ThingsModelTemplate thingsModelTemplate) {
        return toAjax(thingsModelTemplateService.insertThingsModelTemplate(thingsModelTemplate));
    }

    /**
     * 修改通用物模型
     */
    @PreAuthorize("@ss.hasPermi('iot:template:edit')")
    @Log(title = "通用物模型", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改通用物模型")
    public AjaxResult edit(@RequestBody ThingsModelTemplate thingsModelTemplate) {
        return toAjax(thingsModelTemplateService.updateThingsModelTemplate(thingsModelTemplate));
    }

    /**
     * 删除通用物模型
     */
    @PreAuthorize("@ss.hasPermi('iot:template:remove')")
    @Log(title = "通用物模型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{templateIds}")
    @ApiOperation("批量删除通用物模型")
    public AjaxResult remove(@PathVariable Long[] templateIds) {
        return toAjax(thingsModelTemplateService.deleteThingsModelTemplateByTemplateIds(templateIds));
    }

    @ApiOperation(value = "物模型导入模板")
    @PostMapping("/temp")
    public void temp(HttpServletResponse response) {
        ExcelUtil<ThingsModelTemplate> excelUtil = new ExcelUtil<>(ThingsModelTemplate.class);
        excelUtil.importTemplateExcel(response, "采集点");
    }

    @ApiOperation(value = "物模型导入模板")
    @RequestMapping(value = "/temp-json",method = RequestMethod.POST)
    public void tempJson(HttpServletResponse response) {
        ExcelUtil<ThingsModelJsonTemplate> excelUtil = new ExcelUtil<>(ThingsModelJsonTemplate.class);
        excelUtil.importTemplateExcel(response, "JSON采集点模板");
    }


    /**
     * 导入采集点
     */
    @PreAuthorize("@ss.hasPermi('iot:template:add')")
    @ApiOperation(value = "采集点导入")
    @PostMapping(value = "/importData")
    public AjaxResult importData(MultipartFile file, String tempSlaveId) throws Exception {
        ExcelUtil<ThingsModelTemplateVO> excelUtil = new ExcelUtil<>(ThingsModelTemplateVO.class);
        List<ThingsModelTemplateVO> list = excelUtil.importExcel(file.getInputStream());
        String result = thingsModelTemplateService.importData(list, tempSlaveId);
        return AjaxResult.success(result);
    }

    @ApiOperation("导出采集点")
    @PreAuthorize("@ss.hasPermi('iot:template:query')")
    @PostMapping("/exportJson")
    public void exportJson(HttpServletResponse response, ThingsModelTemplateVO thingsModelTemplateVO)
    {
        List<ThingsModelTemplateVO> thingsModelTemplates = thingsModelTemplateService.selectThingsModelTemplateExport(thingsModelTemplateVO);
        ExcelUtil<ThingsModelTemplateVO> util = new ExcelUtil<>(ThingsModelTemplateVO.class);
        util.exportExcel(response, thingsModelTemplates, "采集点");
    }

}
