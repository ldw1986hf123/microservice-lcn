package com.ldw.microservice.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/3 11:00
 * @Description
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class ColumnNodeDTO {

    /**
     * @desc 所属表id
     */
    @ApiModelProperty(value = "所属表id", example = "0")
    private Long tableId;

    /**
     * @desc 所属表code  eg: user
     */
    @ApiModelProperty(value = "所属表名", example = "user")
    private String tableName;
    /**
     * @desc 字段id
     */
    @ApiModelProperty(value = "字段id", example = "0")
    private Long columnId;

    /**
     * @desc 字段名
     */
    @ApiModelProperty(value = "字段名", example = "username")
    private String columnName;

    /**
     * @desc 字段类型
     */
    @ApiModelProperty(value = "字段类型", example = "string")
    private String columnType;
    /**
     * @desc 字段约束
     */
    @ApiModelProperty(value = "字段约束", example = "unique")
    private String columnConstraint;

    /**
     * 数据源id
     */
    @ApiModelProperty(value = "数据源id", example = "0")
    private Long dataSourceId;

    /**
     * @desc 数据源名称
     */
    @ApiModelProperty(value = "数据源名称", example = "测试数据源")
    private String dataSourceName;

    /**
     * 数据源类型 kudu, mysql, hive, oracle, sqlserver...
     */
    @ApiModelProperty(value = "数据源类型 kudu, mysql, hive, oracle, sqlserver...", example = "mysql")
    private String dataSourceType;

    /**
     * @desc 数据库code,可能为空
     */
    @ApiModelProperty(value = "数据库code", example = "user_hb")
    private String databaseCode;
}
