package com.ldw.microservice.service;

import com.deepexi.data.metadata.domain.dto.metadata.MetadataColumnDTO;
import com.deepexi.data.metadata.domain.dto.metadata.MetadataIndexDTO;
import com.deepexi.data.metadata.domain.dto.metadata.MetadataPartitionDTO;
import com.ldw.microservice.dto.MetadataColumnDTO;
import com.ldw.microservice.dto.MetadataIndexDTO;
import com.ldw.microservice.dto.MetadataPartitionDTO;

import java.util.List;

/**
 * Function:  获取分区元数据接口类. <br/>
 * @author 卢丹文
 * @see
 * @since JDK 1.8
 */
public interface TableDetailMetadataService {

    /**
     * 查询表的 字段信息
     *
     * @return
     */
    List<MetadataColumnDTO> findTableColumns(Long tableId, String tenantId, int page, int size);

    /**
     * 查询表的 index 信息
     *
     * @return
     */
    List<MetadataIndexDTO> findIndexes(Long tableId, String tenantId, int page, int size);

    /**
     * 查询表的 index 信息
     *
     * @return
     */
    List<MetadataPartitionDTO> findPartitions(String dataSourceType, Long tableId, String tenantId, int page, int size);

    /**
     * 查询表的 index 信息
     *
     * @return
     */
    List<MetadataColumnDTO> partitionColumn(Long tableId, String tenantId, int page, int size);


}

