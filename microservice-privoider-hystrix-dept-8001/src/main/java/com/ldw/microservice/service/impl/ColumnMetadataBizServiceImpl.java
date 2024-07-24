package com.ldw.microservice.service.impl;

import com.ldw.microservice.dto.ColumnMetadataDTO;
import com.ldw.microservice.query.CommonSearchQuery;
import com.ldw.microservice.query.NodeQuery;
import com.ldw.microservice.service.ColumnMetadataBizService;
import com.ldw.microservice.service.CommonEsBizService;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @Author 卢丹文
 * @Date 2021/3/3 11:00
 * @Description
 */
@Slf4j
@Service
public class ColumnMetadataBizServiceImpl implements ColumnMetadataBizService {

    @Autowired
    private CommonEsBizService commonEsBizService;
    @Value("${es.timeout:30}")
    private Long esTimeout;

    /**
     * 获取字段信息
     * @param fieldId
     * @param tenantId
     * @param includeFields
     * @return
     */
    @Override
    public ColumnMetadataDTO getColumnMetadata(Long fieldId, String tenantId, String[] includeFields){
        if (null == includeFields || includeFields.length <=0) {
            includeFields = new String[]{"id", "columnName", "columnType", "columnConstraint", "columnComment", "isNull", "isPrimaryKey",
                    "length", "dataSourceId", "dataSourceType", "dataSourceName", "dataSourceEnv", "projectId", "tenantId",
                    "tableMetadataId", "relType", "tableName", "orderNo", "compressionAlgorithm", "encoding", "constraintsStr", "isForeignKey", "createdTime", "createdBy", "updatedTime", "updatedBy"};
        }
        Map<String, Object> mustWhere = new HashMap<String, Object>();
        mustWhere.put("id", fieldId);
        if (StringUtils.isNotBlank(tenantId)) {
            mustWhere.put("tenantId", tenantId);
        }
        mustWhere.put("isDeleted", 0);
        ColumnMetadataDTO field = commonEsBizService.findOne(new CommonSearchQuery<ColumnMetadataDTO>("column_metadata_index", null, null,
                mustWhere, null, null, includeFields, null, esTimeout, false, ColumnMetadataDTO.class));
        return field;
//        NodeQuery fieldNodeParam = new NodeQuery();
//        fieldNodeParam.setFieldId(fieldId);
//        return getParam(fieldNodeParam, tenantId);
    }




    /**
     * 用于虚构数据，后期需要删除
     * @param fieldNodeParam
     * @param tenantId
     * @return
     */
    private ColumnMetadataDTO getParam(NodeQuery fieldNodeParam, String tenantId){
        ColumnMetadataDTO columnMetadataDTO = new ColumnMetadataDTO();
        columnMetadataDTO.setId(String.valueOf(fieldNodeParam.getFieldId()));
        columnMetadataDTO.setColumnName(fieldNodeParam.getFieldCode());
        columnMetadataDTO.setDataSourceId(fieldNodeParam.getDataSourceId());
        columnMetadataDTO.setDataSourceType(fieldNodeParam.getDataSourceType());
        columnMetadataDTO.setTableMetadataId(String.valueOf(fieldNodeParam.getTableId()));
        columnMetadataDTO.setProjectId(fieldNodeParam.getProjectId());
        columnMetadataDTO.setTenantId(tenantId);
        return columnMetadataDTO;
    }
}
