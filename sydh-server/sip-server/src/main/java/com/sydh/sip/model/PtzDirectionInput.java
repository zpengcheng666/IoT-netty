package com.sydh.sip.model;

import lombok.Data;

@Data
public class PtzDirectionInput {
    int leftRight;
    int upDown;
    int moveSpeed;
}
