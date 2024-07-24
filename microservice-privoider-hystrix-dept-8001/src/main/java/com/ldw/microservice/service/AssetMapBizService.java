package com.ldw.microservice.service;


import com.ldw.microservice.dto.MetadataColumnDTO;
import com.ldw.microservice.dto.MetadataTableHistoryDTO;

import java.util.List;

/**
 *
 * @author 卢丹文
 * @date 2021/3/25
 * @description: TODO
 **/
public interface AssetMapBizService {
    /**
     * 表详情变更记录查询
     * @param id
     * @param tenantId
     * @param page
     * @param size
     * @return
     */
    List<MetadataTableHistoryDTO> searchChangeRecord(Long id, String tenantId, Integer page, Integer size);

    /**
     * 表详情变更记录明细查询
     * @param tableId
     * @param version
     * @param tenantId
     * @return
     */
    List<MetadataColumnDTO> changeRecordDetail(String tableId, Integer version, String tenantId);
}
