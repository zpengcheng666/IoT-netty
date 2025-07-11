package com.sydh.pay.core.controller.admin.order;

import cn.hutool.core.collection.CollectionUtil;
import com.sydh.common.core.domain.CommonResult;
import com.sydh.common.core.domain.PageResult;
import com.sydh.pay.core.controller.admin.order.vo.*;
import com.sydh.pay.core.convert.order.PayOrderConvert;
import com.sydh.pay.core.domain.dataobject.app.PayApp;
import com.sydh.pay.core.domain.dataobject.order.PayOrder;
import com.sydh.pay.core.domain.dataobject.order.PayOrderExtension;
import com.sydh.pay.core.service.app.PayAppService;
import com.sydh.pay.core.service.order.PayOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.sydh.common.core.domain.CommonResult.success;
import static com.sydh.common.utils.ServletUtils.getClientIP;
import static com.sydh.common.utils.collection.CollectionUtils.convertList;


@Api(tags = "管理后台 - 支付订单")
@RestController
@RequestMapping("/pay/order")
@Validated
public class PayOrderController {

    @Resource
    private PayOrderService orderService;
    @Resource
    private PayAppService appService;

    @GetMapping("/get")
    @ApiOperation("获得支付订单")
    @ApiParam(name = "id", value = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('pay:order:query')")
    public CommonResult<PayOrderRespVO> getOrder(@RequestParam("id") Long id) {
        return success(PayOrderConvert.INSTANCE.convert(orderService.getOrder(id)));
    }

    @GetMapping("/get-detail")
    @ApiOperation("获得支付订单详情")
    @ApiParam(name = "id", value = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('pay:order:query')")
    public CommonResult<PayOrderDetailsRespVO> getOrderDetail(@RequestParam("id") Long id) {
        PayOrder order = orderService.getOrder(id);
        if (order == null) {
            return success(null);
        }

        // 拼接返回
        PayApp app = appService.getApp(order.getAppId());
        PayOrderExtension orderExtension = orderService.getOrderExtension(order.getExtensionId());
        return success(PayOrderConvert.INSTANCE.convert(order, orderExtension, app));
    }

    @PostMapping("/submit")
    @ApiOperation("提交支付订单")
    public CommonResult<PayOrderSubmitRespVO> submitPayOrder(@RequestBody PayOrderSubmitReqVO reqVO) {
        PayOrderSubmitRespVO respVO = orderService.submitOrder(reqVO, getClientIP());
        return success(respVO);
    }

    @GetMapping("/page")
    @ApiOperation("获得支付订单分页")
    @PreAuthorize("@ss.hasPermission('pay:order:query')")
    public CommonResult<PageResult<PayOrderPageItemRespVO>> getOrderPage(@Valid PayOrderPageReqVO pageVO) {
        PageResult<PayOrder> pageResult = orderService.getOrderPage(pageVO);
        if (CollectionUtil.isEmpty(pageResult.getList())) {
            return success(new PageResult<>(pageResult.getTotal()));
        }

        // 拼接返回
        Map<Long, PayApp> appMap = appService.getAppMap(convertList(pageResult.getList(), PayOrder::getAppId));
        return success(PayOrderConvert.INSTANCE.convertPage(pageResult, appMap));
    }

    @GetMapping("/export-excel")
    @ApiOperation("导出支付订单 Excel")
    @PreAuthorize("@ss.hasPermission('pay:order:export')")
//    @OperateLog(type = EXPORT)
    public void exportOrderExcel(@Valid PayOrderExportReqVO exportReqVO,
            HttpServletResponse response) throws IOException {
        List<PayOrder> list = orderService.getOrderList(exportReqVO);
        if (CollectionUtil.isEmpty(list)) {
//            ExcelUtils.write(response, "支付订单.xls", "数据",
//                    PayOrderExcelVO.class, new ArrayList<>());
            return;
        }

        // 拼接返回
        Map<Long, PayApp> appMap = appService.getAppMap(convertList(list, PayOrder::getAppId));
        List<PayOrderExcelVO> excelList = PayOrderConvert.INSTANCE.convertList(list, appMap);
        // 导出 Excel
//        ExcelUtils.write(response, "支付订单.xls", "数据", PayOrderExcelVO.class, excelList);
    }

}
