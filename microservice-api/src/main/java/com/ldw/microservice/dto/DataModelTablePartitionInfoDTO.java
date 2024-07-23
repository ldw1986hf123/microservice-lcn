package com.ldw.microservice.dto;

import com.deepexi.data.metadata.domain.eo.SuperEntity;
import lombok.Data;

import java.util.Date;


/**
 * 分区明细表
 *
 * @author ludanwen
 */
@Data
public class DataModelTablePartitionInfoDTO extends SuperEntity {
    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 数据模型ID
     */
    private Long dataModelTableId;

    /**
     * 分区表ID
     */
    private Long partitionId;

    /**
     * 分区名称、值
     */
    private String name;

    /**
     * 分区大小
     */
    private Long size;

    /**
     * 修改时间
     */
    private Date lastModifiedTime;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 更新时间
     */
    private Date updatedTime;

    /**
     * 更新人
     */
    private String updatedBy;
}