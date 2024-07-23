package com.ldw.microservice.dto;

import com.deepexi.data.metadata.domain.eo.SuperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2020. All rights reserved.
 *
 * @Author liangchaohua
 * @Date 2020/11/5 10:21
 * @Description 元数据类目信息
 */
@Data
@EqualsAndHashCode
public class CategoryTreeDTO extends SuperEntity {
    /**
     * 类目名称
     */
    private String name;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 父ID
     */
    private Long parentId;

    /**
     * 全路径，格式为：“/”+类目ID
     */
    private String fullPath;

    /**
     * 租户的id
     */
    private String tenantId;

    /**
     * 子类目列表
     */
    private List<CategoryTreeDTO> childNodes;
}
