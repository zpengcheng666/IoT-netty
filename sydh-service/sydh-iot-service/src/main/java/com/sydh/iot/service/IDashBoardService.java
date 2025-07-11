package com.sydh.iot.service;

import com.sydh.common.core.domain.AjaxResult;
import com.sydh.iot.model.dashBoard.DeviceMatchBo;

/**
 * 大屏接口
 * @author bill
 */
public interface IDashBoardService {


    public AjaxResult dashCombine(DeviceMatchBo bo);
}
