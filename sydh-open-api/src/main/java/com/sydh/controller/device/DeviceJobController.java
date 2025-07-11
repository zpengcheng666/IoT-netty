package com.sydh.controller.device;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.annotation.Log;
import com.sydh.common.constant.SYDHConstant;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.enums.BusinessType;
import com.sydh.common.exception.job.TaskException;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.utils.poi.ExcelUtil;
import com.sydh.common.utils.MessageUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.iot.convert.DeviceJobConvert;
import com.sydh.iot.domain.DeviceJob;
import com.sydh.iot.domain.ModbusConfig;
import com.sydh.iot.domain.ModbusJob;
import com.sydh.iot.mapper.ModbusJobMapper;
import com.sydh.iot.model.Action;
import com.sydh.iot.model.vo.DeviceJobVO;
import com.sydh.iot.service.IDeviceJobService;
import com.sydh.iot.service.IModbusConfigService;
import com.sydh.quartz.util.CronUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.quartz.SchedulerException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 调度任务信息操作处理
 *
 * @author kerwincui
 */
@Api(tags = "调度任务信息操作处理模块")
@RestController
@RequestMapping("/iot/job")
public class DeviceJobController extends BaseController
{
    @Resource
    private IDeviceJobService jobService;

    @Resource
    private IModbusConfigService modbusConfigService;

    @Resource
    private ModbusJobMapper modbusJobMapper;


    /**
     * 查询定时任务列表
     */
    @ApiOperation("查询定时任务列表")
    @PreAuthorize("@ss.hasPermi('iot:device:timer:list')")
    @GetMapping("/list")
    public TableDataInfo list(DeviceJob deviceJob)
    {
        Page<DeviceJobVO> voPage = jobService.pageDeviceJobVO(deviceJob);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出定时任务列表
     */
    @ApiOperation("导出定时任务列表")
    @PreAuthorize("@ss.hasPermi('iot:device:timer:export')")
    @Log(title = "定时任务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DeviceJob deviceJob)
    {
        Page<DeviceJobVO> voPage = jobService.pageDeviceJobVO(deviceJob);
        ExcelUtil<DeviceJobVO> util = new ExcelUtil<DeviceJobVO>(DeviceJobVO.class);
        util.exportExcel(response, voPage.getRecords(), "设备定时数据");
    }

    /**
     * 获取定时任务详细信息
     */
    @ApiOperation("获取定时任务详细信息")
    @PreAuthorize("@ss.hasPermi('iot:device:timer:query')")
    @GetMapping(value = "/{jobId}")
    public AjaxResult getInfo(@PathVariable("jobId") Long jobId)
    {
        return AjaxResult.success(jobService.selectJobById(jobId));
    }

    /**
     * 新增定时任务
     */
    @ApiOperation("新增定时任务")
    @PreAuthorize("@ss.hasPermi('iot:device:timer:add')")
    @Log(title = "定时任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DeviceJobVO jobVO) throws SchedulerException, TaskException
    {
        if (!CronUtils.isValid(jobVO.getCronExpression()))
        {
            return error(StringUtils.format(MessageUtils.message("job.add.failed.cron.not.valid"), jobVO.getJobName()));
        }
        jobVO.setCreateBy(getUsername());
        List<Action> actions = JSON.parseArray(jobVO.getActions(), Action.class);
        List<String> identifiers = actions.stream().map(Action::getId).collect(Collectors.toList());
        if (SYDHConstant.PROTOCOL.ModbusRtu.equals(jobVO.getProtocolCode())
                || SYDHConstant.PROTOCOL.ModbusTcpOverRtu.equals(jobVO.getProtocolCode())) {
            // modbus配置
            List<ModbusConfig> modbusConfigList = modbusConfigService.listByProductIdAndIdentifiers(jobVO.getProductId(), identifiers);
            if (CollectionUtils.isEmpty(modbusConfigList)) {
                return error(StringUtils.format(MessageUtils.message("job.add.failed.product.not.modbus.config"), jobVO.getJobName()));
            }
            jobVO.setJobGroup("MODBUS");
        }
        DeviceJob job = DeviceJobConvert.INSTANCE.convertDeviceJob(jobVO);
        return toAjax(jobService.insertJob(job));
    }

    /**
     * 修改定时任务
     */
    @ApiOperation("修改定时任务")
    @PreAuthorize("@ss.hasPermi('iot:device:timer:edit')")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DeviceJobVO jobVO) throws SchedulerException, TaskException
    {
        if (!CronUtils.isValid(jobVO.getCronExpression()))
        {
            return error(StringUtils.format(MessageUtils.message("job.update.failed.cron.not.valid"), jobVO.getJobName()));
        }
        jobVO.setUpdateBy(getUsername());
        List<Action> actions = JSON.parseArray(jobVO.getActions(), Action.class);
        List<String> identifiers = actions.stream().map(Action::getId).collect(Collectors.toList());
        if (SYDHConstant.PROTOCOL.ModbusRtu.equals(jobVO.getProtocolCode())
                || SYDHConstant.PROTOCOL.ModbusTcpOverRtu.equals(jobVO.getProtocolCode())) {
            // modbus配置
            List<ModbusConfig> modbusConfigList = modbusConfigService.listByProductIdAndIdentifiers(jobVO.getProductId(), identifiers);
            if (CollectionUtils.isEmpty(modbusConfigList)) {
                return error(StringUtils.format(MessageUtils.message("job.update.failed.product.not.modbus.config"), jobVO.getJobName()));
            }
            jobVO.setJobGroup("MODBUS");
        }
        jobVO.setUpdateBy(getUsername());
        DeviceJob job = DeviceJobConvert.INSTANCE.convertDeviceJob(jobVO);
        return toAjax(jobService.updateJob(job));
    }

    /**
     * 定时任务状态修改
     */
    @ApiOperation("定时任务状态修改")
    @PreAuthorize("@ss.hasPermi('iot:device:timer:edit')")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody DeviceJob job) throws SchedulerException
    {
        DeviceJob newJob = jobService.selectJobById(job.getJobId());
        newJob.setStatus(job.getStatus());
        newJob.setUpdateBy(getUsername());
        return toAjax(jobService.changeStatus(newJob));
    }

    /**
     * 定时任务立即执行一次
     */
    @ApiOperation("定时任务立即执行一次")
    @PreAuthorize("@ss.hasPermi('iot:device:timer:execute')")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping("/run")
    public AjaxResult run(@RequestBody DeviceJob job) throws SchedulerException
    {
        jobService.run(job);
        return AjaxResult.success();
    }

    /**
     * 删除定时任务
     */
    @ApiOperation("删除定时任务")
    @PreAuthorize("@ss.hasPermi('iot:device:timer:remove')")
    @Log(title = "定时任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{jobIds}")
    public AjaxResult remove(@PathVariable Long[] jobIds) throws SchedulerException, TaskException
    {
        jobService.deleteJobByIds(jobIds);
        LambdaQueryWrapper<ModbusJob> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ModbusJob::getJobId, Arrays.asList(jobIds));
        modbusJobMapper.delete(queryWrapper);
        return AjaxResult.success();
    }
}
