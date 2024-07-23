package com.ldw.microservice.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/3 11:00
 * @Description 血缘顶点阐述
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class NodeQuery {

    /**
     * 数据源id
     */
    @ApiModelProperty(value = "数据源id", example = "0")
    private Long dataSourceId;

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

    /**
     * 项目id
     */
    @ApiModelProperty(value = "项目id", example = "0")
    private Long projectId;

    /**
     * @desc 表ID
     */
    @ApiModelProperty(value = "表ID", example = "0")
    private Long tableId;

    /**
     * @desc 表code
     */
    @ApiModelProperty(value = "表code", example = "user")
    private String tableCode;

    /**
     * @desc 字段ID
     */
    @ApiModelProperty(value = "字段ID", example = "0")
    private Long fieldId;

    /**
     * @desc 字段code
     */
    @ApiModelProperty(value = "字段code", example = "username")
    private String fieldCode;

}
