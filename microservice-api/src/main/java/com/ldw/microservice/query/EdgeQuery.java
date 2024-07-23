package com.ldw.microservice.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/3 11:00
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EdgeQuery {

    /**
     * 表来源表id
     */
    @ApiModelProperty(value = "来源表id", example = "0")
    private Long sourceTableId;

    /**
     * 表目标表id
     */
    @ApiModelProperty(value = "目标表id", example = "0")
    private Long targetTableId;
    /**
     * 来源表id
     */
    @ApiModelProperty(value = "来源字段id", example = "0")
    private Long sourceFieldId;

    /**
     * 目标表id
     */
    @ApiModelProperty(value = "目标字段id", example = "0")
    private Long targetFieldId;

    /**
     * jobId
     */
    @ApiModelProperty(value = "jobId", example = "0")
    private Long jobId;

}
