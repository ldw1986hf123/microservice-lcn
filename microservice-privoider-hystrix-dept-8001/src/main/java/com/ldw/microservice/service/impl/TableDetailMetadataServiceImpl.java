package com.ldw.microservice.service.impl;

import com.alibaba.fastjson.JSONObject;

import com.ldw.microservice.config.InternalPayload;
import com.ldw.microservice.dto.DataModelTablePartitionInfoDTO;
import com.ldw.microservice.dto.MetadataColumnDTO;
import com.ldw.microservice.dto.MetadataIndexDTO;
import com.ldw.microservice.dto.MetadataPartitionDTO;
import com.ldw.microservice.service.TableDetailMetadataService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.DateUtil;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author 卢丹文
 * @see
 * @since JDK 1.8
 */
@Slf4j
@Service
public class TableDetailMetadataServiceImpl implements TableDetailMetadataService {

    @Autowired
    private MetadataColumnService columnService;
    @Autowired
    private MetadataIndexService indexService;
    @Autowired
    private MetadataPartitionService partitionService;
    @Autowired
    private MetricsTableInfoFeign metricsTableInfoFeign;

    @Override
    public List<MetadataColumnDTO> findTableColumns(Long tableId, String tenantId, int page, int size) {
        PageHelper.startPage(page, size);
        List<MetadataColumnDO> columnDOList = columnService.list(new QueryWrapper<MetadataColumnDO>().lambda()
                .eq(MetadataColumnDO::getIsDeleted, CommonConstants.IsDeleted.NO)
                .eq(MetadataColumnDO::getTenantId, tenantId)
                .eq(MetadataColumnDO::getIsPartitionKey, CommonConstants.IsPartitionKey.NO)
                .eq(MetadataColumnDO::getTableId, tableId)
                .orderByAsc(MetadataColumnDO::getSortNo));
        List<MetadataColumnDTO> result = PageUtil.getResult(columnDOList, MetadataColumnDTO.class);
        for (MetadataColumnDTO metadataColumnDTO : result) {
            metadataColumnDTO.setColumnConstraint(ConstraintTypeEnum.getNamesByCodes(metadataColumnDTO.getColumnConstraint()));
        }
        return result;
    }

    @Override
    public List<MetadataIndexDTO> findIndexes(Long tableId, String tenantId, int page, int size) {
        PageHelper.startPage(page, size);
        List<MetadataIndexDO> indexDOList = indexService.list(new QueryWrapper<MetadataIndexDO>().lambda()
                .eq(MetadataIndexDO::getIsDeleted, CommonConstants.IsDeleted.NO)
                .eq(MetadataIndexDO::getTenantId, tenantId)
                .eq(MetadataIndexDO::getTableId, tableId));
        return PageUtil.getResult(indexDOList, MetadataIndexDTO.class);
    }

    @Override
    public List<MetadataPartitionDTO> findPartitions(String dataSourceType, Long tableId, String tenantId, int page, int size) {
        PageHelper.startPage(page, size);
        List<MetadataPartitionDO> partitionDOList = partitionService.list(new QueryWrapper<MetadataPartitionDO>().lambda()
                .eq(MetadataPartitionDO::getIsDeleted, CommonConstants.IsDeleted.NO)
                .eq(MetadataPartitionDO::getTenantId, tenantId)
                .eq(MetadataPartitionDO::getTableId, tableId));
        List<MetadataPartitionDTO> partitionDTOList = PageUtil.getResult(partitionDOList, MetadataPartitionDTO.class);

        Map<String, DataModelTablePartitionInfoDTO> partitionInfoDTOMap = getPartitionInfo(dataSourceType, tableId, tenantId, page, size);
        if (CollectionUtils.isNotEmpty(partitionDTOList)) {
            for (MetadataPartitionDTO partitionDTO : partitionDTOList) {
                String partitionName = partitionDTO.getName();
                if ("hive".equals(dataSourceType)) {
                    if (null != partitionInfoDTOMap.get(partitionName)) {
                        partitionDTO.setSize(partitionInfoDTOMap.get(partitionName).getSize());
                    } else {
                        partitionDTO.setSize(0L);
                    }
                } else {
                    if (null != partitionDTO.getMaxDataLength()) {
                        partitionDTO.setSize(Long.valueOf(partitionDTO.getMaxDataLength()));
                    } else {
                        partitionDTO.setSize(0L);
                    }
                }
            }
        }
        return partitionDTOList;
    }

    private Map<String, DataModelTablePartitionInfoDTO> getPartitionInfo(String dataSourceType, Long tableId, String tenantId, int page, int size) {
        Map<String, DataModelTablePartitionInfoDTO> partitionInfoDTOMap = Maps.newHashMap();

        if ("hive".equals(dataSourceType)) {
            InternalPayload<List> internalPayload = metricsTableInfoFeign.partitionInfoForMetadata(tableId, page, size, tenantId);
            if (null != internalPayload.getPayload()) {
                List<DataModelTablePartitionInfoDTO> partitionInfoDTOList = Lists.newArrayList();
                List<Map<String, Object>> internalPayloadList = internalPayload.getPayload();
                for (Map<String, Object> map : internalPayloadList) {
                    JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(map));
                    Date date = jsonObject.getDate("updatedTime");
                    if (DateUtil.isSameDay(date, new Date())) {
                        DataModelTablePartitionInfoDTO partitionInfoDTO = new DataModelTablePartitionInfoDTO();
                        String name = jsonObject.getString("name");
                        Long partitionSize = jsonObject.getLong("size");
                        partitionInfoDTO.setName(name);
                        partitionInfoDTO.setSize(partitionSize);
                        partitionInfoDTOList.add(partitionInfoDTO);
                    }
                }
                if (CollectionUtils.isNotEmpty(partitionInfoDTOList)) {
                    partitionInfoDTOMap = partitionInfoDTOList.stream().collect(Collectors.toMap(DataModelTablePartitionInfoDTO::getName, a -> a, (k1, k2) -> k1));
                }
            }
        }

        return partitionInfoDTOMap;
    }


    @Override
    public List<MetadataColumnDTO> partitionColumn(Long tableId, String tenantId, int page, int size) {
        PageHelper.startPage(page, size);
        List<MetadataColumnDO> columnDOList = columnService.list(new QueryWrapper<MetadataColumnDO>().lambda()
                .eq(MetadataColumnDO::getIsDeleted, CommonConstants.IsDeleted.NO)
                .eq(MetadataColumnDO::getTenantId, tenantId)
                .eq(MetadataColumnDO::getIsPartitionKey, CommonConstants.IsPartitionKey.YES)
                .eq(MetadataColumnDO::getTableId, tableId)
                .orderByAsc(MetadataColumnDO::getSortNo));
        return PageUtil.getResult(columnDOList, MetadataColumnDTO.class);
    }

}
