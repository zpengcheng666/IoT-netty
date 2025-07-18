package com.sydh.pay.core.controller.admin.refund;

import cn.hutool.core.collection.CollectionUtil;
import com.sydh.common.core.domain.CommonResult;
import com.sydh.common.core.domain.PageResult;
import com.sydh.pay.core.controller.admin.refund.vo.*;
import com.sydh.pay.core.convert.refund.PayRefundConvert;
import com.sydh.pay.core.domain.dataobject.app.PayApp;
import com.sydh.pay.core.domain.dataobject.refund.PayRefund;
import com.sydh.pay.core.service.app.PayAppService;
import com.sydh.pay.core.service.refund.PayRefundService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.sydh.common.core.domain.CommonResult.success;
import static com.sydh.common.utils.collection.CollectionUtils.convertList;


@Api(tags = "管理后台 - 退款订单")
@RestController
@RequestMapping("/pay/refund")
@Validated
public class PayRefundController {

    @Resource
    private PayRefundService refundService;
    @Resource
    private PayAppService appService;

    @GetMapping("/get")
    @ApiOperation("获得退款订单")
    @ApiParam(name = "id", value = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('pay:refund:query')")
    public CommonResult<PayRefundDetailsRespVO> getRefund(@RequestParam("id") Long id) {
        PayRefund refund = refundService.getRefund(id);
        if (refund == null) {
            return success(new PayRefundDetailsRespVO());
        }

        // 拼接数据
        PayApp app = appService.getApp(refund.getAppId());
        return success(PayRefundConvert.INSTANCE.convert(refund, app));
    }

    @GetMapping("/page")
    @ApiModelProperty("获得退款订单分页")
    @PreAuthorize("@ss.hasPermission('pay:refund:query')")
    public CommonResult<PageResult<PayRefundPageItemRespVO>> getRefundPage(@Valid PayRefundPageReqVO pageVO) {
        PageResult<PayRefund> pageResult = refundService.getRefundPage(pageVO);
        if (CollectionUtil.isEmpty(pageResult.getList())) {
            return success(new PageResult<>(pageResult.getTotal()));
        }

        // 处理应用ID数据
        Map<Long, PayApp> appMap = appService.getAppMap(convertList(pageResult.getList(), PayRefund::getAppId));
        return success(PayRefundConvert.INSTANCE.convertPage(pageResult, appMap));
    }

    @GetMapping("/export-excel")
    @ApiModelProperty("导出退款订单 Excel")
    @PreAuthorize("@ss.hasPermission('pay:refund:export')")
//    @OperateLog(type = EXPORT)
    public void exportRefundExcel(@Valid PayRefundExportReqVO exportReqVO,
            HttpServletResponse response) throws IOException {
        List<PayRefund> list = refundService.getRefundList(exportReqVO);
        if (CollectionUtil.isEmpty(list)) {
//            ExcelUtils.write(response, "退款订单.xls", "数据",
//                    PayRefundExcelVO.class, new ArrayList<>());
            return;
        }

        // 拼接返回
        Map<Long, PayApp> appMap = appService.getAppMap(convertList(list, PayRefund::getAppId));
        List<PayRefundExcelVO> excelList = PayRefundConvert.INSTANCE.convertList(list, appMap);
        // 导出 Excel
//        ExcelUtils.write(response, "退款订单.xls", "数据", PayRefundExcelVO.class, excelList);
    }

}
