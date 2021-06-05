package com.ldw.microservice.docker.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2020. All rights reserved.
 *
 * @description: 索引  元数据信息
 * @author: wenggufiang
 * @time: 2021年3月18日11:30:51
 */
@ToString
@Data
public class MetadataIndexDTO  implements Serializable {

    /**
     * 表元数据ID
     */
    private Long tableId;

    /**
     * 字段ID
     */
    private Long columnId;

    /**
     * 索引类型
     */
    private String type;

    /**
     * 索引名
     */
    private String name;

    /**
     * 备注
     */
    private String remark;

    /**
     * 排序
     */
    private Integer sortNo;

    /**
     * 表名
     */
    private String tableCode;
    /**
     * 字段名
     */
    private String columnCode;

    /**
     * 作用区域（Oracle） LOCAL；GLOBAL
     */
    private String locality;


}
