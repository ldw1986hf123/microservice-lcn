package com.ldw.metadata.vo;

import lombok.Data;

import java.util.List;

@Data
public class TableMetadataVO extends MetaDataVO {
    private String tableId;
    private String tableName;
    private String tableComment;
    private String dataBaseName;
    private List<ColumnMetadataVO> columnMetadataVOList;

}
