package com.ldw.metadata.vo;

import lombok.Data;

@Data
public class ColumnMetadataVO extends MetadataVO {
    private String columnName;
    private String columnType;
    private String tableName;
    private Boolean isNullAble;
    private String comment;

    /**
     * 约束  字符串
     */
    private String constraintsStr;

    /**
     * 是否外键
     */
    private Boolean isForeignKey;
}
