package com.sydh.controller.deviceConfig;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.annotation.Log;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.enums.BusinessType;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.utils.poi.ExcelUtil;
import com.sydh.common.utils.MessageUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.iot.cache.ITSLCache;
import com.sydh.iot.domain.ThingsModel;
import com.sydh.iot.model.ImportThingsModelInput;
import com.sydh.iot.model.ThingsModelPerm;
import com.sydh.iot.model.modbus.ModbusAndThingsVO;
import com.sydh.iot.model.vo.ThingsModelVO;
import com.sydh.iot.service.IThingsModelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 物模型Controller
 *
 * @author kerwincui
 * @date 2021-12-16
 */
@RestController
@RequestMapping("/iot/model")
@Api(tags="产品物模型")
public class ThingsModelController extends BaseController
{
    @Resource
    private IThingsModelService thingsModelService;
    @Resource
    private ITSLCache itslCache;

    /**
     * 查询物模型列表
     */
    @PreAuthorize("@ss.hasPermi('iot:model:list')")
    @GetMapping("/list")
    @ApiOperation("产品物模型分页列表")
    public TableDataInfo list(ThingsModelVO thingsModelVO)
    {
        Integer action = thingsModelVO.getAction();
        if (action != null) {
            switch (action) {
                case 0:
                    thingsModelService.topThingsModel(thingsModelVO);
                    break;
                case 1:
                    thingsModelService.moveUpThingsModel(thingsModelVO);
                    break;
                case 2:
                    thingsModelService.moveDownThingsModel(thingsModelVO);
                    break;
                default:
                    // 无效操作，忽略
                    break;
            }
        }
        Page<ThingsModelVO> voPage = thingsModelService.pageThingsModelVO(thingsModelVO);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 查询物模型对应设备分享权限
     */
    @PreAuthorize("@ss.hasPermi('iot:device:list')")
    @GetMapping("/permList/{productId}/{deviceId}/{userId}")
    @ApiOperation("查询物模型对应设备分享权限")
    public AjaxResult permList(@PathVariable Long productId, @PathVariable Long deviceId, @PathVariable Long userId)
    {
        List<ThingsModelPerm> list = thingsModelService.selectThingsModelPermList(productId,deviceId,userId);
        return AjaxResult.success(list);
    }

    /**
     * 获取物模型详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:model:query')")
    @GetMapping(value = "/{modelId}")
    @ApiOperation("获取产品物模型详情")
    public AjaxResult getInfo(@PathVariable("modelId") Long modelId)
    {
        return AjaxResult.success(thingsModelService.selectThingsModelVoByModelId(modelId));
    }

    /**
     * 新增物模型
     */
    @PreAuthorize("@ss.hasPermi('iot:model:add')")
    @Log(title = "物模型", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("添加产品物模型")
    public AjaxResult add(@RequestBody ThingsModelVO thingsModelVO)
    {
        int result=thingsModelService.insertThingsModel(thingsModelVO);
        if(result==1){
            return AjaxResult.success();
        }else if(result==2){
            return AjaxResult.error(MessageUtils.message("things.model.identifier.repeat"));
        }else{
            return AjaxResult.error();
        }
    }

    @PreAuthorize("@ss.hasPermi('iot:model:import')")
    @Log(title = "导入物模型",businessType = BusinessType.INSERT)
    @PostMapping("/import")
    @ApiOperation("导入通用物模型")
    public AjaxResult ImportByTemplateIds(@RequestBody ImportThingsModelInput input){
        int repeatCount=thingsModelService.importByTemplateIds(input);
        if(repeatCount==0){
            return AjaxResult.success(MessageUtils.message("import.success"));
        }else{
            return AjaxResult.success(StringUtils.format(MessageUtils.message("things.model.import.failed.identifier.repeat"), repeatCount));
        }
    }

    /**
     * 修改物模型
     */
    @PreAuthorize("@ss.hasPermi('iot:model:edit')")
    @Log(title = "物模型", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改产品物模型")
    public AjaxResult edit(@RequestBody ThingsModel thingsModel)
    {
        int result=thingsModelService.updateThingsModel(thingsModel);
        if(result==1){
            return AjaxResult.success();
        }else if(result==2){
            return AjaxResult.error(MessageUtils.message("things.model.identifier.repeat"));
        }else{
            return AjaxResult.error();
        }
    }

    /**
     * 删除物模型
     */
    @PreAuthorize("@ss.hasPermi('iot:model:remove')")
    @Log(title = "物模型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{modelIds}")
    @ApiOperation("批量删除产品物模型")
    public AjaxResult remove(@PathVariable Long[] modelIds)
    {
        return toAjax(thingsModelService.deleteThingsModelByModelIds(modelIds));
    }

    /**
     * 获取缓存的JSON物模型
     */
    @PreAuthorize("@ss.hasPermi('iot:model:query')")
    @GetMapping(value = "/cache/{productId}")
    @ApiOperation("获取缓存的JSON物模型")
    public AjaxResult getCacheThingsModelByProductId(@PathVariable("productId") Long productId)
    {
        return AjaxResult.success(MessageUtils.message("operate.success"),itslCache.getCacheThingsModelByProductId(productId));
    }

    @ApiOperation(value = "物模型导入模板")
    @PostMapping("/temp")
    public void temp(HttpServletResponse response){
        ExcelUtil<ThingsModelVO> excelUtil = new ExcelUtil<>(ThingsModelVO.class);
        excelUtil.importTemplateExcel(response,"采集点");
    }


    /**
     * 导入采集点
     */
    @PreAuthorize("@ss.hasPermi('iot:point:import')")
    @ApiOperation(value = "采集点导入")
    @PostMapping(value = "/importData")
    public AjaxResult importData(MultipartFile file,Long productId) throws Exception{
        ExcelUtil<ThingsModelVO> excelUtil = new ExcelUtil<>(ThingsModelVO.class);
        List<ThingsModelVO> list = excelUtil.importExcel(file.getInputStream());
        String result = thingsModelService.importData(list, productId);
        return AjaxResult.success(result);
    }


    /**
     * 获取modbus配置可选择物模型
     */
    @PreAuthorize("@ss.hasPermi('iot:model:list')")
    @GetMapping("/listModbus")
    @ApiOperation("获取modbus配置可选择物模型")
    public TableDataInfo listModbus(Long productId)
    {
        Page<ModbusAndThingsVO> list = thingsModelService.getModbusConfigUnSelectThingsModel(productId);
        return getDataTable(list.getRecords(), list.getTotal());
    }


    /**
     * 根据设备获取可读写物模型
     */
    @PreAuthorize("@ss.hasPermi('iot:model:list')")
    @GetMapping("/write")
    @ApiOperation("根据设备获取可读写物模型")
    public TableDataInfo write(Long deviceId)
    {
        Page<ThingsModel> thingsModelList = thingsModelService.selectThingsModelBySerialNumber(deviceId);
        return getDataTable(thingsModelList.getRecords(), thingsModelList.getTotal());
    }

    /**
     * 处理老物模型结构
     */
    @PreAuthorize("@ss.hasPermi('iot:model:refresh')")
    @ApiOperation(value = " 处理老物模型结构")
    @PostMapping(value = "/refresh")
    public AjaxResult refresh(Long productId) {
        return thingsModelService.refresh(productId);
    }

}
