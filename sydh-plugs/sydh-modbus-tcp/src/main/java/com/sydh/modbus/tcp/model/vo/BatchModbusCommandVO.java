package com.sydh.modbus.tcp.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author bill
 */
@Data
public class BatchModbusCommandVO {
    private String clientId;
    private List<ModbusSingleCommand> commands;

    @Data
    public static class ModbusSingleCommand {
        private int code;
        private int register;
        private int quantity;
        private int address;
        private List<Integer> values;

    }
}
