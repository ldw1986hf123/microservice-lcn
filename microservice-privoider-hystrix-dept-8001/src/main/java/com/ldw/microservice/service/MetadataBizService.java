package com.ldw.microservice.service;


import com.ldw.microservice.dto.MetadataColumnDTO;
import com.ldw.microservice.dto.MetadataTableDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *
 * @author 卢丹文
 * @description: TODO
 **/
public interface MetadataBizService {
    /**
     * 编辑元数据字段
     * @param data
     * @param metadataTableId
     * @param tenantId
     * @param userId
     * @return
     */
    Boolean updateMetadaColumn(List<MetadataColumnDTO> data, Long metadataTableId, String tenantId);

    /**
     *
     * @desc 批量保存表元数据
     *
     * @param metadataTableDTOS
     * @return 结果状态信息
     */
    Boolean save(List<MetadataTableDTO> metadataTableDTOS);

    /**
     *
     * @desc 删除表元数据
     *
     * @param metadataTableDTO
     * @return
     */
    Boolean delete(MetadataTableDTO metadataTableDTO);

    /**
     *
     * @desc 主键是否被变更
     *
     * @param newColumnDTO
     * @param tableMetadataId
     * @return
     */
    Boolean wasChangedPrimaryKey(List<MetadataColumnDTO> newColumnDTO, Long tableMetadataId);

    /**
     * 上传元数据
     *
     * @param request
     * @param metadataTableId
     * @param tenantId
     * @param userId
     * @return
     */
    String uploadMetadata(HttpServletRequest request, Long metadataTableId, String tenantId);

    /**
     * 下载元数据
     * @param response
     * @param metadataTableId
     * @param tenantId
     */
    void downloadMetadata(HttpServletResponse response, String metadataTableId, String tenantId);

    /**
     * 元数据Excel导入模板下载
     * @param response
     * @param tenantId
     */
    void downloadExcelTemplate(HttpServletResponse response, String tenantId);
}
