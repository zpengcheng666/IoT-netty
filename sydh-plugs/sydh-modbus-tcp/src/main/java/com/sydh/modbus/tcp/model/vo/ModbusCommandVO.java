package com.sydh.modbus.tcp.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class ModbusCommandVO {
    private String clientId;
    private int transactionId;
    private int code;
    private int register;
    private int quantity;
    private int address;
    private List<Integer> values;

}
