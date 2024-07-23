package com.ldw.microservice.dto;

import com.deepexi.util.pojo.AbstractObject;
import com.redislabs.redisgraph.graph_entities.Node;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/3 11:00
 * @Description 获取血缘表上下游类型和顶点dto
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class NodeDTO extends AbstractObject implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "顶点相对位置，0-当前表，1-上游表，2-下游表", example = "1")
    private Integer nodeType;

    @ApiModelProperty(value = "redisgraph顶点")
    private Node node;
}