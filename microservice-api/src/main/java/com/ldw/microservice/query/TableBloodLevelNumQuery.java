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
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TableBloodLevelNumQuery {

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

    @ApiModelProperty(value = "是否上游, 默认否", example = "false")
    private Boolean isUpStream;
}
