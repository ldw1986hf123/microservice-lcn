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
 * @Description 表血缘顶点VO
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class TableNodeDTO {

    /**
     * @desc 表id
     */
    @ApiModelProperty(value = "表Id", example = "0")
    private Long tableId;

    /**
     * @desc 表code  eg: user
     */
    @ApiModelProperty(value = "表名", example = "user")
    private String tableName;

    /**
     * @desc 表名  eg:用户表
     */
    @ApiModelProperty(value = "表描述", example = "用户表")
    private String tableComment;

    /**
     * @desc 存储大小
     */
    @ApiModelProperty(value = "表存储量", example = "234")
    private String memorySpace;

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

    /**
     * 节点位置, 0-当前表，1-上游表，2-下游表
     */
    @ApiModelProperty(value = "表位置, 0-当前表，1-上游表，2-下游表, 3-即是上游表又是下游表", example = "0")
    private Integer nodeType;
}
