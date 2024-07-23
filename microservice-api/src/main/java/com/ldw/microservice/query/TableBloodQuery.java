package com.ldw.microservice.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import java.util.List;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/5 11:00
 * @Description 获取字段血缘query
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TableBloodQuery extends TenantIdQuery {

    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户Id", example = "1")
    private String tenantId;

    /**
     * 动作
     * add-新增，del-删除，put-更新, 不传为自动匹配，没有的创建，有的适配，
     */
    @ApiModelProperty(value = "动作", example = "put")
    private String operation;
    /**
     * 动作类型
     * 0-node,1-edge,不传默认为1
     */
    @ApiModelProperty(value = "动作类型", example = "0")
    private Integer operationType;

    /**
     * 表节点列表
     */
    @Valid
    @ApiModelProperty(value = "表节点列表")
    private List<TableNodeQuery> tableNodes;
    /**
     * 表节点关系列表
     */
    @Valid
    @ApiModelProperty(value = "表节点关系列表")
    private List<TableEdgeQuery> edges;
    /**
     * job列表
     */
    @Valid
    @ApiModelProperty(value = "job列表")
    private List<TaskDetailQuery> taskDetails;
}
