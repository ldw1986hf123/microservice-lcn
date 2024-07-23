package com.ldw.microservice.dto;

import com.deepexi.data.metadata.domain.eo.SuperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author AutoGenerator
 * @since 2021-03-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MetadataTableHistoryDTO extends SuperEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 表ID
     */
    private Long tableId;

    /**
     * 表元数据状态：0-已下架；1-服务中
     */
    private Integer status;

    /**
     * 版本号
     */
    private Integer versionNumber;

    /**
     * 元数据JSON字符串内容
     */
    private String tableMetadataJson;

    /**
     * 备注
     */
    private String remark;


}
