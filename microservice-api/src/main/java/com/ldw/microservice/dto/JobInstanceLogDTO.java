package com.ldw.microservice.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/6 11:00
 * @Description 字段血缘VO
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class JobInstanceLogDTO {

    /**
     * @desc 日志数据
     */
    private String logData;
}
