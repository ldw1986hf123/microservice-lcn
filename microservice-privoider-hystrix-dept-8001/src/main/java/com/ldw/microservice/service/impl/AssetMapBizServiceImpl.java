package com.ldw.microservice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.deepexi.data.metadata.biz.AssetMapBizService;
import com.deepexi.data.metadata.constant.CommonConstants;
import com.deepexi.data.metadata.domain.dto.metadata.MetadataColumnDTO;
import com.deepexi.data.metadata.domain.dto.metadata.MetadataTableDTO;
import com.deepexi.data.metadata.domain.dto.metadata.MetadataTableHistoryDTO;
import com.deepexi.data.metadata.domain.eo.MetadataColumnDO;
import com.deepexi.data.metadata.domain.eo.MetadataTableHistoryDO;
import com.deepexi.data.metadata.service.*;
import com.deepexi.data.metadata.util.BeanUtil;
import com.deepexi.data.metadata.util.PageUtil;
import com.deepexi.util.CollectionUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2020. All rights reserved.
 *
 * @author wengguifang
 * @date 2021/3/25
 * @description: TODO
 **/
@Service
public class AssetMapBizServiceImpl implements AssetMapBizService {

    @Autowired
    private MetadataColumnService columnService;
    @Autowired
    private MetadataTableHistoryService tableHistoryService;


    @Override
    public List<MetadataTableHistoryDTO> searchChangeRecord(Long tableId, String tenantId, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<MetadataTableHistoryDO> historyDOList = tableHistoryService.list(new QueryWrapper<MetadataTableHistoryDO>()
                .select("id", "table_id", "status", "version_number", "created_time")
                .lambda()
                .eq(MetadataTableHistoryDO::getTableId, tableId)
                .eq(MetadataTableHistoryDO::getTenantId, tenantId)
                .eq(MetadataTableHistoryDO::getIsDeleted, CommonConstants.IsDeleted.NO)
                .orderByDesc(MetadataTableHistoryDO::getVersionNumber));

        if (CollectionUtil.isEmpty(historyDOList)) {
            if (null  == historyDOList) {
                historyDOList = new Page<>();
            }
            MetadataTableHistoryDO historyDO = new MetadataTableHistoryDO();
            historyDO.setVersionNumber(1);
            historyDO.setStatus(CommonConstants.TableMetadataHistoryRecordStatus.SERVICING);
            historyDO.setCreatedTime(new Date());
            historyDOList.add(historyDO);
        }
        return PageUtil.getResult(historyDOList, MetadataTableHistoryDTO.class);
    }

    @Override
    public List<MetadataColumnDTO> changeRecordDetail(String tableId, Integer version, String tenantId) {
        MetadataTableHistoryDO historyDO = tableHistoryService.getOne(new QueryWrapper<MetadataTableHistoryDO>()
                .lambda()
                .eq(MetadataTableHistoryDO::getTableId, tableId)
                .eq(MetadataTableHistoryDO::getTenantId, tenantId)
                .eq(MetadataTableHistoryDO::getIsDeleted, CommonConstants.IsDeleted.NO)
                .eq(MetadataTableHistoryDO::getVersionNumber, version));


        List<MetadataColumnDTO> columnDTOS = null;
        if (null != historyDO) {
            String tableMetadataJson = historyDO.getTableMetadataJson();
            MetadataTableDTO metadataTableDTO = JSONObject.parseObject(tableMetadataJson, MetadataTableDTO.class);
            columnDTOS = metadataTableDTO.getColumnDTOS();
        } else {
            List<MetadataColumnDO> columnDOS = columnService.list(new QueryWrapper<MetadataColumnDO>().lambda()
                    .eq(MetadataColumnDO::getTableId, tableId)
                    .eq(MetadataColumnDO::getTenantId, tenantId)
                    .eq(MetadataColumnDO::getIsDeleted, CommonConstants.IsDeleted.NO)
                    .orderByAsc(MetadataColumnDO::getSortNo));
            columnDTOS = BeanUtil.deepCloneList(columnDOS, MetadataColumnDTO.class);
        }
        return columnDTOS;
    }
}
