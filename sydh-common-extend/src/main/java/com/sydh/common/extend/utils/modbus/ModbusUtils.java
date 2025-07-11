package com.sydh.common.extend.utils.modbus;

import com.sydh.common.extend.core.protocol.modbus.ModbusCode;
import com.sydh.common.utils.modbus.Mparams;

import java.util.List;

/**
 * @author gsb
 * @date 2024/6/15 11:21
 */
public class ModbusUtils {

    /**
     * 获取modbus功能码
     * isReadOnly: 0-读写 1-只读
     * type: 1-IO寄存器 2-数据寄存器
     * IO寄存器读写 05功能码 数据寄存器只读 06功能码
     * @param type modbus数据类型
     * @return modbus功能码
     */
    public static ModbusCode getModbusCode(int type){
            if (type == 1){
                return ModbusCode.Write05;
            }else {
                return ModbusCode.Write06;
            }
    }

    public static ModbusCode getReadModbusCode(int type,int isReadOnly){
        if (type == 1){
            return isReadOnly == 1 ? ModbusCode.Read02 : ModbusCode.Read01;
        }else {
            return isReadOnly == 1 ? ModbusCode.Read04 : ModbusCode.Read03;
        }
    }

    /**
     * 获取modbus-hex字符串寄存器地址
     * @param hexString hex字符串
     * @return 寄存器地址-10进制
     */
    public static int getModbusAddress(String hexString){
        return Integer.parseInt(hexString.substring(4, 8),16);
    }

    /**
     * 获取从机地址
     * @param hexString
     * @return
     */
    public static int getModbusSlaveId(String hexString){
        return Integer.parseInt(hexString.substring(0,2),16);
    }

    /**
     * 获取功能码
     * @param hexString
     * @return
     */
    public static int getModbusCode(String hexString){
        return Integer.parseInt(hexString.substring(2,4),16);
    }

    public static Mparams getModbusParams(String hexString){
        Mparams mparams = new Mparams();
        mparams.setSlaveId(Integer.parseInt(hexString.substring(0,2),16));
        mparams.setCode(Integer.parseInt(hexString.substring(2,4),16));
        mparams.setAddress(Integer.parseInt(hexString.substring(4, 8),16));
        return mparams;
    }


    public static void main(String[] args) {
        int modbusAddress = getModbusAddress("0101000A0001FDCA");
        System.out.println(modbusAddress);
    }

    public static byte[] getModbusCommandData(List<Integer> values, int functionCode) {
        if (values != null) {
            switch (functionCode) {
                case 5:
                case 1:
                    return ListToByteArrayConverter.convertValuesToByteArray(values, values.size());
                case 6:
                case 3:
                    return ListToByteArrayConverter.convertToByteArray(values);
                default:
                    return new byte[0];
            }
        }
        return new byte[0];
    }
}
