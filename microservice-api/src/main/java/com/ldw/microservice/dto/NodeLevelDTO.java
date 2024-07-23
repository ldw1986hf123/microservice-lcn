package com.ldw.microservice.dto;

import com.redislabs.redisgraph.graph_entities.Node;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/3 11:00
 * @Description 获取血缘表层级和顶点dto
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class NodeLevelDTO {
    /**
     * 顶点
     */
    @ApiModelProperty(value = "顶点")
    private Node node;

    /**
     * 当前顶点下的第几层
     */
    @ApiModelProperty(value = "当前顶点下的第几层", example = "0")
    private Set<Integer> levels;
}
