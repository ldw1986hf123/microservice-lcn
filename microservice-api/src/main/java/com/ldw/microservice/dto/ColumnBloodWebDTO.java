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
 * @Description 字段血缘VO
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class ColumnBloodWebDTO {

    /**
     * @desc 顶点
     */
    @ApiModelProperty(value = "字段顶点")
    private List<ColumnNodeWebDTO> columnNodes;
    /**
     * @desc 关系
     */
    @ApiModelProperty(value = "字段顶点之间的关系")
    private List<ColumnEdgeWebDTO> columnEdges;
}
