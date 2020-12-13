package com.ldw.metadata.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2020. All rights reserved.
 *
 * @description: 分区  元数据信息
 * @author: ludanwen
 * @time: 2020/12/7 17:45
 */
@ToString
@Data
public class PartitionMetadataVO  extends MetadataVO implements Serializable {
    private String name;
    private String tableName;
    private Long dataLength;
    private  Long maxDataLength;
    private String createdTime;
    private String updatedTime;

}
