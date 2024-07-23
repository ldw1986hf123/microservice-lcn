package com.ldw.microservice.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/6 11:00
 * @Description 表血缘VO
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class TableBloodAllDTO {

    /**
     * @desc 顶点
     */
    @ApiModelProperty(value = "表顶点列表")
    private List<TableNodeDTO> tableNodes;
    /**
     * @desc 关系
     */
    @ApiModelProperty(value = "表关系列表")
    private List<TableEdgeDTO> tableEdges;
}
