package com.ldw.microservice.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/6 11:00
 * @Description 表血缘层数VO
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class TableBloodLevelNumDTO {

    /**
     * @desc 表id
     */
    private Long tableId;
    /**
     * @desc 上游层数
     */
    private Integer upStreamLevelNum;
    /**
     * @desc 下游层数
     */
    private Integer downStreamLevelNum;
}
