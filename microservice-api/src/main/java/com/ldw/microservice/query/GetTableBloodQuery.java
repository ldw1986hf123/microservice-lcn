package com.ldw.microservice.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/5 11:00
 * @Description 获取表血缘query
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GetTableBloodQuery extends TenantIdQuery {

    /**
     * 租户id
     */
    @NotBlank(message = "tenantId不能为空")
    @ApiModelProperty(value = "租户Id", example = "1", required = true)
    private String tenantId;

    /**
     * 表id
     */
    @NotNull(message = "表id不能为空")
    @ApiModelProperty(value = "表id", example = "0", required = true)
    private Long tableId;

    @ApiModelProperty(value = "上游层级", example = "3")
    private Integer upstreamLevel;

    @ApiModelProperty(value = "下游层级", example = "3")
    private Integer downstreamLevel;

}
