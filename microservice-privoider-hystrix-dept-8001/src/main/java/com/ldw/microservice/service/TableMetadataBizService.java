package com.ldw.microservice.service;

import com.deepexi.data.metadata.domain.dto.TableMetadataDTO;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/3 11:00
 * @Description
 */
public interface TableMetadataBizService {

    /**
     * 获取元数据表
     * @param tableId
     * @param tenantId
     * @param includeFields
     * @param timeOut
     * @return
     */
    TableMetadataDTO getTableMetaDate(Long tableId, String tenantId, String[] includeFields, Long timeOut);
}
