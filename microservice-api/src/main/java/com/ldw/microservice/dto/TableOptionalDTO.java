package com.ldw.microservice.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/2 14:34
 * @Description 查找表简化类
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TableOptionalDTO implements Serializable {

    @ApiModelProperty(value = "表id", example = "0")
    private Long id;
    /**
     * @desc 表名称
     */
    @ApiModelProperty(value = "表名称", example = "用户表")
    private String tableName;
    /**
     * @desc 数据库名称
     */
    @ApiModelProperty(value = "表code", example = "user")
    private String tableCode;
    /**
     * @desc 项目id
     */
    @ApiModelProperty(value = "项目id", example = "0")
    private Long projectId;
    /**
     * @desc 项目名
     */
    @ApiModelProperty(value = "项目名", example = "project")
    private String projectName;

}
