package com.sydh.protocol.util;

import com.sydh.common.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author gsb
 * @date 2022/11/9 16:28
 */
@Getter
@AllArgsConstructor
public class Msg {
    protected int index;
    protected String desc;
    protected Object value;
    protected String raw;


    public static Msg field(int index, String desc, Object value, String raw) {
        return new Msg(index, desc, value, raw);
    }

    public static Msg lengthField(int index, String desc, int value, int lengthUnit) {
        return new Msg(index, desc, value, StringUtils.leftPad(Integer.toHexString(value), 1 << lengthUnit, '0'));
    }


    public void setLength(int length, int lengthUnit) {
        this.value = length;
        this.raw = StringUtils.leftPad(Integer.toHexString(length), 1 << lengthUnit, '0');
    }

    @Override
    public String toString() {
        if (desc == null) {
            return index + "\t[" + raw + "] [" + StringUtils.toString(value) + "]";
        }
        return index + "\t[" + raw + "] [" + StringUtils.toString(value) + "] " + desc;
    }
}
