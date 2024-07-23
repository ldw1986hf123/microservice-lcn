package com.ldw.microservice.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;


/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/4 14:06
 * @Description
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class BaseEsQuery implements Serializable {

    private static final long serialVersionUID = 955035576665182484L;

    @ApiModelProperty(value = "索引名", example = "table_metadata_index")
    private String index;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "jsonEntity")
    private String jsonEntity;

    @ApiModelProperty(value = "超时时间", example = "30")
    private Long timeout;

    public BaseEsQuery() {

    }

    public BaseEsQuery(String index, String id, String jsonEntity, Long timeout) {
        this.index = index;
        this.id = id;
        this.jsonEntity = jsonEntity;
        this.timeout = timeout;
    }
}
