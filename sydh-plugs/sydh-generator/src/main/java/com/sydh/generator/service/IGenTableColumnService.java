package com.sydh.generator.service;

import com.sydh.generator.domain.GenTableColumn;

import java.util.List;

/**
 * 业务字段 服务层
 *
 * @author ruoyi
 */
public interface IGenTableColumnService {
    /**
     * 查询业务字段列表
     *
     * @param tableId 业务字段编号
     * @return 业务字段集合
     */
    public List<GenTableColumn> selectGenTableColumnListByTableId(Long tableId);

}
