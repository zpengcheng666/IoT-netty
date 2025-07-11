package com.sydh.common.enums;

import java.util.Objects;

/**
 * Modbus数据类型枚举
 * 
 * @author sydh
 */
public enum ModbusDataType {
    
    U_SHORT("ushort", "16位 无符号"),
    SHORT("short", "16位 有符号"),
    LONG_ABCD("long-ABCD", "32位 有符号(ABCD)"),
    LONG_CDAB("long-CDAB", "32位 有符号(CDAB)"),
    U_LONG_ABCD("ulong-ABCD", "32位 无符号(ABCD)"),
    U_LONG_CDAB("ulong-CDAB", "32位 无符号(CDAB)"),
    FLOAT_ABCD("float-ABCD", "32位 浮点数(ABCD)"),
    FLOAT_CDAB("float-CDAB", "32位 浮点数(CDAB)"),
    BIT("bit", "位");

    private final String type;
    private final String msg;

    ModbusDataType(String type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public String getType() {
        return this.type;
    }

    public String getMsg() {
        return this.msg;
    }

    /**
     * 根据类型字符串获取对应的枚举值
     * 
     * @param type 类型字符串
     * @return 对应的枚举值，如果找不到则返回 U_SHORT
     */
    public static ModbusDataType convert(String type) {
        for (ModbusDataType modbusDataType : values()) {
            if (Objects.equals(modbusDataType.type, type)) {
                return modbusDataType;
            }
        }
        return U_SHORT;
    }
}

