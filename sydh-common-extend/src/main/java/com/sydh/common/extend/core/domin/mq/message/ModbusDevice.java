package com.sydh.common.extend.core.domin.mq.message;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ModbusDevice {
    @TableId
    private Long deviceId;
    private String serialNumber;
    private String deviceIp;
    private String command;
    private int status;
    private int devicePort;
    private Long productId;
    private List<Command> commands;


}
