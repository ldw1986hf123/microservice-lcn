package com.ldw.metadata.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2020. All rights reserved.
 *
 * @description: 索引  元数据信息
 * @author: ludanwen
 * @time: 2020/12/7 17:45
 */
@ToString
@Data
public class IndexMetadataVO  extends MetadataVO implements Serializable {
    private String name;
    private String tableName;
    private String  columnName;
    private String createdDate;

    @JsonProperty("indexType")
    private String  type;


    @JsonProperty("indexComment")
    private String comment;
}