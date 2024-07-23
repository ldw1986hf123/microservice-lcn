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
 * @Description 查找数据库列表简化类
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DatasourceOptionalDTO implements Serializable {

    /**
     * 数据源下资源id
     */
    @ApiModelProperty(value = "数据源下资源id", example = "0")
    private Long id;

    /**
     * 数据源下的资源名称
     */
    @ApiModelProperty(value = "数据源下的资源名称", example = "hive_test")
    private String name;

    /**
     * 数据源类型
     */
    @ApiModelProperty(value = "数据源类型", example = "hive")
    private String type;
}
