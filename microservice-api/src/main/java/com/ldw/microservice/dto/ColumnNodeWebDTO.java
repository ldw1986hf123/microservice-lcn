package com.ldw.microservice.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/6 11:00
 * @Description 字段血缘节点VO
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class ColumnNodeWebDTO {

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
     * 是否当前节点
     */
    @ApiModelProperty(value = "字段位置, 0-当前字段,1-上游字段,2-下游字段,3-既是上游又是下游", example = "0")
    private Integer nodeType;

    /**
     * 拥有者
     */
    @ApiModelProperty(value = "拥有人", example = "拥有者")
    private String owner;

    /**
     * @desc 上层表数
     */
    @ApiModelProperty(value = "直接上层表数", example = "0")
    private Integer upstreamTablesNumber;

    /**
     * @desc 下层表数
     */
    @ApiModelProperty(value = "直接下层表数", example = "0")
    private Integer downstreamTablesNumber;
}
