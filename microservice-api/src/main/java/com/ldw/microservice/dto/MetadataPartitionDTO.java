package com.ldw.microservice.dto;

import com.deepexi.data.metadata.domain.eo.SuperEntity;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 *   
 *
 * @description: 分区元数据
 * @author: wenggufiang
 * @time: 2021年3月18日11:30:51
 */
@ToString
@Data
public class MetadataPartitionDTO extends SuperEntity implements Serializable {
    /**
     * 表ID
     */
    private Long tableId;

    /**
     * 字段ID，存放分区字段id,
     */
    private Long columnId;

    /**
     * 分区名称
     */
    private String name;

    /**
     * 数据长度
     */
    private Integer dataLength;

    /**
     * 最大数据长度
     */
    private Integer maxDataLength;

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
     * 分区大小
     */
    private Long size;

}
