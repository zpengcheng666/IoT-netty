package com.sydh.common.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeptInfoRespDTO {

    private Long id;

    private String name;

    private Long parentId;

    private Integer sort;

}
