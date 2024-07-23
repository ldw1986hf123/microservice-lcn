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
 * @Description 获取字段血缘query
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GetFieldBloodQuery extends TenantIdQuery {

    /**
     * 租户id
     */
    @NotBlank(message = "tenantId不能为空")
    @ApiModelProperty(value = "租户Id", example = "1")
    private String tenantId;

    /**
     * 表id
     */
    @NotNull(message = "字段id不能为空")
    @ApiModelProperty(value = "字段id", example = "0")
    private Long fieldId;

    @ApiModelProperty(value = "上游层级,默认1", example = "0")
    private Integer upstreamLevel;

    @ApiModelProperty(value = "下游层级,默认1", example = "0")
    private Integer downstreamLevel;

    public Integer getUpstreamLevel(){
        if (upstreamLevel == null) {
            return 1;
        }
        return upstreamLevel;
    }

    public Integer getDownstreamLevel() {
        if (downstreamLevel == null) {
            return 1;
        }
        return downstreamLevel;
    }

}
