package com.sydh.pay.core.controller.admin.order.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

@ApiModel(description = "管理后台 - 支付订单提交 Request VO")
@Data
public class PayOrderSubmitReqVO {

    @ApiModelProperty(value = "支付单编号", required = true, example = "1024")
    @NotNull(message = "支付单编号不能为空")
    private Long id;

    @ApiModelProperty(value = "支付渠道", required = true, example = "wx_pub")
    @NotEmpty(message = "支付渠道不能为空")
    private String channelCode;

    @ApiModelProperty(value = "支付渠道的额外参数，例如说，微信公众号需要传递 openid 参数")
    private Map<String, String> channelExtras;

    @ApiModelProperty(value = "展示模式", example = "url") // 参见 {@link PayDisplayModeEnum} 枚举。如果不传递，则每个支付渠道使用默认的方式
    private String displayMode;

    @ApiModelProperty(value = "回跳地址")
    @URL(message = "回跳地址的格式必须是 URL")
    private String returnUrl;

}
