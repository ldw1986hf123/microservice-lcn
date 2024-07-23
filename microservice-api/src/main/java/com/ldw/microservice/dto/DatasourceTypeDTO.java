package com.ldw.microservice.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/2 14:34
 * @Description 查找数据源类型简化类
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DatasourceTypeDTO implements Serializable {

    /**
     * 数据源code
     */
    @ApiModelProperty(value = "数据源code", example = "hive")
    private String code;

    /**
     * 数据源类型名称
     */
    @ApiModelProperty(value = "数据源名称", example = "hive")
    private String name;
}
