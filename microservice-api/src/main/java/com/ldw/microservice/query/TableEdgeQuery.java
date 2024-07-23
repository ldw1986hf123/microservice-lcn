package com.ldw.microservice.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/5 11:00
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TableEdgeQuery {

    /**
     * 来源表id
     */
    @ApiModelProperty(value = "来源表id", example = "0")
    private Long sourceTableId;

    /**
     * 目标表id
     */
    @ApiModelProperty(value = "目标表id", example = "0")
    private Long targetTableId;

    /**
     * jobId
     */
    @ApiModelProperty(value = "jobId", example = "0")
    private Long jobId;

}
