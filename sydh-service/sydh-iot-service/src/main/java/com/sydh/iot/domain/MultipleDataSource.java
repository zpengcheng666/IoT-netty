package com.sydh.iot.domain;

import com.sydh.common.annotation.Excel;
import lombok.Data;
import com.sydh.common.core.domain.BaseEntity;
import lombok.EqualsAndHashCode;

/**
 * 数据源配置对象 multiple_data_source
 *
 * @author gx_ma
 * @date 2024-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MultipleDataSource extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /**
     * 连接池名称
     */
    private String poolName;

    /**
     * JDBC driver org.h2.Driver
     */
    private String driverClassName;

    /** 数据源 */
    @Excel(name = "数据源")
    private String databaseSource;

    /** 类型 */
    @Excel(name = "类型")
    private String type;

    /** 数据库名称 */
    @Excel(name = "数据库名称")
    private String dataBaseName;

    /** 连接地址 */
    private String url;

    /** 连接地址 */
    @Excel(name = "连接地址")
    private String host;

    /** 用户名 */
    @Excel(name = "用户名")
    private String username;

    /** 密码 */
    @Excel(name = "密码")
    private String password;

    /** sql语句 */
    @Excel(name = "sql语句")
    private String sql;
}
