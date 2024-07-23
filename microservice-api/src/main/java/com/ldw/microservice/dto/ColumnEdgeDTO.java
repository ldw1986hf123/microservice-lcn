package com.ldw.microservice.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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
public class ColumnEdgeDTO {

    /**
     * @desc 来源字段id
     */
    @ApiModelProperty(value = "来源字段id", example = "0")
    private Long sourceColumnId;

    /**
     * @desc 目标字段id
     */
    @ApiModelProperty(value = "目标字段id", example = "0")
    private Long targetColumnId;
}
