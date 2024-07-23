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
 * @Description 获取血缘表直接上下游表数dto
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class TableBloodLevelDTO {

    /**
     * @desc 表id
     */
    @ApiModelProperty(value = "表id", example = "0")
    private Long tableId;
    /**
     * @desc 直接上游表数
     */
    @ApiModelProperty(value = "直接上游表数", example = "0")
    private Integer nextLayerTableNum;
    /**
     * @desc 直接下游表数
     */
    @ApiModelProperty(value = "直接下游表数", example = "0")
    private Integer upperStoryNum;
}
