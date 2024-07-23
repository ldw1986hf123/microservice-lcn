
package com.ldw.microservice.dto;

import com.deepexi.data.metadata.domain.eo.SuperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2020. All rights reserved.
 *
 * @description: 约束元数据
 * @author: wenggufiang
 * @time: 2021年3月18日11:30:51
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class MetadataConstraintDTO extends SuperEntity implements Serializable {

    private String name;
    private String columnCode;
    private String tableCode;
    private String type;
}
