package com.sydh.generator.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.core.page.PageDomain;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.generator.domain.GenTable;
import com.sydh.generator.domain.GenTableColumn;

import java.util.List;
import java.util.Map;

/**
 * 业务 服务层
 *
 * @author ruoyi
 */
public interface IGenTableService {
    /**
     * 查询业务列表
     *
     * @param genTable 业务信息
     * @return 业务集合
     */
    public Page<GenTable> selectGenTableList(GenTable genTable);

    /**
     * 查询据库列表
     *
     * @param genTable 业务信息
     * @return 数据库表集合
     */
    public TableDataInfo selectDbTableList(GenTable genTable, PageDomain page);

    /**
     * 查询据库列表
     *
     * @param tableNames 表名称组
     * @return 数据库表集合
     */
    public List<GenTable> selectDbTableListByNames(String[] tableNames, String dataName);

    /**
     * 查询所有表信息
     *
     * @return 表信息集合
     */
    public List<GenTable> selectGenTableAll();

    /**
     * 查询业务信息
     *
     * @param id 业务ID
     * @return 业务信息
     */
    public GenTable selectGenTableById(Long id);

    /**
     * 修改业务
     *
     * @param genTable 业务信息
     * @return 结果
     */
    public void updateGenTable(GenTable genTable);

    /**
     * 删除业务信息
     *
     * @param tableIds 需要删除的表数据ID
     * @return 结果
     */
    public void deleteGenTableByIds(Long[] tableIds);

    /**
     * 导入表结构
     *
     * @param tableList 导入表列表
     */
    public void importGenTable(List<GenTable> tableList, String dataName);

    /**
     * 根据表名称查询列信息
     *
     * @param tableName 表名称
     * @param dataName  数据源名称
     * @return 列信息
     */
    List<GenTableColumn> selectDbTableColumnsByName(String tableName, String dataName);

    /**
     * 预览代码
     *
     * @param tableId 表编号
     * @return 预览数据列表
     */
    public Map<String, String> previewCode(Long tableId);

    /**
     * 生成代码（下载方式）
     *
     * @param tableName 表名称
     * @return 数据
     */
    public byte[] downloadCode(String tableName);

    /**
     * 生成代码（自定义路径）
     *
     * @param tableName 表名称
     * @return 数据
     */
    public void generatorCode(String tableName);

    /**
     * 同步数据库
     *
     * @param tableName 表名称
     */
    public void synchDb(String tableName);

    /**
     * 批量生成代码（下载方式）
     *
     * @param tableNames 表数组
     * @return 数据
     */
    public byte[] downloadCode(String[] tableNames);

    /**
     * 修改保存参数校验
     *
     * @param genTable 业务信息
     */
    public void validateEdit(GenTable genTable);
}
