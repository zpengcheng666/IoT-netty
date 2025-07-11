package com.sydh.rule.cmp.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DevExecuteCData {
    private int cond;
    private String deviceId;
    private int type;
    private String modelId;
    private String value;
}
