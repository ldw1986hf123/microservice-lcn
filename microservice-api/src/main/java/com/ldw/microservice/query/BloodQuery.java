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
 * @Date 2021/3/3 11:00
 * @Description 血缘查询类
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BloodQuery extends TenantIdQuery {

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
     * 0-node,1-edge,不传自动匹配
     */
    @ApiModelProperty(value = "动作类型", example = "0")
    private Integer operationType;

    /**
     * 表节点列表
     */
    @Valid
    @ApiModelProperty(value = "节点列表")
    private List<NodeQuery> fieldNodes;
    /**
     * 表节点关系列表
     */
    @Valid
    @ApiModelProperty(value = "表节点关系列表")
    private List<EdgeQuery> edges;
    /**
     * job列表
     */
    @Valid
    @ApiModelProperty(value = "job列表")
    private List<DetailQuery> taskDetails;
}
