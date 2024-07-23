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
 * @Date 2021/3/3 11:00
 * @Description
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class ColumnBloodDTO {

    /**
     * @desc 顶点
     */
    @ApiModelProperty(value = "字段顶点")
    private List<ColumnNodeDTO> columnNodes;
    /**
     * @desc 关系
     */
    @ApiModelProperty(value = "字段顶点之间的关系")
    private List<ColumnEdgeDTO> columnEdges;
}
