package com.sydh.iot.model.modbus;

import com.sydh.iot.domain.ModbusConfig;
import lombok.Data;

import java.util.List;

/**
 * @author bill
 */
@Data
public class ModbusConfigVO {

    /**产品id*/
    private Long productId;
    /**modbus配置列表*/
    private List<ModbusConfig> configList;
    /**
     * 删除的id集合
     */
    private Long[] delIds;
}
