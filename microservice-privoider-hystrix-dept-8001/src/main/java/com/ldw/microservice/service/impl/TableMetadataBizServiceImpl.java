package com.ldw.microservice.service.impl;

import com.ldw.microservice.dto.TableMetadataDTO;
import com.ldw.microservice.query.CommonSearchQuery;
import com.ldw.microservice.service.CommonEsBizService;
import com.ldw.microservice.service.TableMetadataBizService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @Author 卢丹文
 * @Description
 */
@Slf4j
@Service
public class TableMetadataBizServiceImpl implements TableMetadataBizService {

    @Autowired
    private CommonEsBizService commonEsBizService;

    /**
     * 获取表信息
     * @param tableId
     * @param tenantId
     * @param includeFields
     * @param timeOut
     * @return
     */
    @Override
    public TableMetadataDTO getTableMetaDate(Long tableId, String tenantId, String[] includeFields, Long timeOut){
        Map<String, Object> mustWhere = new HashMap<String, Object>(4);
        mustWhere.put("id", tableId);
        mustWhere.put("tenantId", tenantId);
        mustWhere.put("isDeleted", 0);
        TableMetadataDTO tableMetadata = commonEsBizService.findOne(new CommonSearchQuery<TableMetadataDTO>(
                "table_metadata_index", null, null, mustWhere, null, null, includeFields,
                null, timeOut, false, TableMetadataDTO.class));
        return tableMetadata;
    }


}
