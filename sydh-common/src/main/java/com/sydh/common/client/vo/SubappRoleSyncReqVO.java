package com.sydh.common.client.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubappRoleSyncReqVO {

    private String name;

    private String code;

    private String operation;
}
