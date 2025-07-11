package com.sydh.iot.data.service;

import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.extend.core.domin.mq.InvokeReqDto;

/**
 * 设备指令下发接口
 * @author gsb
 * @date 2022/12/5 11:03
 */
public interface IFunctionInvoke {

    /**
     * 服务调用，等待设备响应
     * @param reqDto 服务下发对象
     * @return 数据结果
     */
    public AjaxResult invokeReply(InvokeReqDto reqDto);

    /**
     * 服务调用,设备不响应
     * @param reqDto 服务下发对象
     * @return 消息id messageId
     */
    public AjaxResult invokeNoReply(InvokeReqDto reqDto);
}
