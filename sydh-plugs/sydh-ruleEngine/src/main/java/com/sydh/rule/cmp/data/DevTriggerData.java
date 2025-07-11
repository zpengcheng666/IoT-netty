package com.sydh.rule.cmp.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DevTriggerData {
    private Long productId;
    private String deviceId;
    private int type;
    private String modelId;
}
