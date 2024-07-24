package com.ldw.microservice.service;

import com.ldw.microservice.dto.ColumnMetadataDTO;

/**
 *
 * @Author 卢丹文
 * @Date 2021/3/3 11:00
 * @Description
 */
public interface ColumnMetadataBizService {

    /**
     * 获取字段信息
     * @param fieldId
     * @param tenantId
     * @param includeFields
     * @return
     */
    ColumnMetadataDTO getColumnMetadata(Long fieldId, String tenantId, String[] includeFields);
}
