package com.ldw.microservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.deepexi.data.metadata.biz.MetadataBizService;
import com.deepexi.data.metadata.biz.strategy.AlterationStrategy;
import com.deepexi.data.metadata.biz.strategy.AlterationStrategyContext;
import com.deepexi.data.metadata.config.properties.MetadataProperties;
import com.deepexi.data.metadata.constant.CommonConstants;
import com.deepexi.data.metadata.constant.CommonConstants.MetadataOperationType;
import com.deepexi.data.metadata.constant.CommonConstants.TableMetadataHistoryRecordStatus;
import com.deepexi.data.metadata.domain.dto.metadata.*;
import com.deepexi.data.metadata.domain.eo.*;
import com.deepexi.data.metadata.enums.ConstraintTypeEnum;
import com.deepexi.data.metadata.service.*;
import com.deepexi.data.metadata.util.BeanUtil;
import com.deepexi.data.metadata.util.DateUtil;
import com.deepexi.data.metadata.util.DownloadExcelUtils;
import com.deepexi.data.metadata.util.ImportExcelUtils;
import com.deepexi.util.extension.ApplicationException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2020. All rights reserved.
 *
 * @author wengguifang
 * @date 2021/3/19
 * @description: TODO
 **/
@Slf4j
@Service
public class MetadataBizServiceImpl implements MetadataBizService {

    private Map<String, Map<String, String>> columnTypes = new HashMap<>();

    @Autowired
    private MetadataProperties metadataProperties;
    @Autowired
    private AlterationStrategyContext alterationStrategyContext;
    @Autowired
    private MetadataColumnService columnService;
    @Autowired
    private MetadataTableService tableService;
    @Autowired
    private MetadataTableHistoryService tableHistoryService;
    @Autowired
    private MetadataIndexService indexService;
    @Autowired
    private MetadataPartitionService partitionService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private UserSubscribeService userSubscribeService;

    @Override
    public Boolean updateMetadaColumn(List<MetadataColumnDTO> data, Long metadataTableId, String tenantId) {
        try {
            if (!isChanged(data, metadataTableId)) {
                return Boolean.TRUE;
            }
            Thread thread = new Thread(() -> {
                try {
                    MetadataTableDO metadataTableDO = tableService.getOne(new QueryWrapper<MetadataTableDO>()
                            .lambda()
                            .eq(MetadataTableDO::getTenantId, tenantId)
                            .eq(MetadataTableDO::getId, metadataTableId));
                    MetadataTableDTO metadataTableDTO = BeanUtil.deepClone(metadataTableDO, MetadataTableDTO.class);
                    metadataTableDTO.setColumnDTOS(data);

                    AlterationStrategy alterationStrategy = alterationStrategyContext.getAlterationStrategy(metadataTableDTO.getDatasourceType());
                    if (null != alterationStrategy) {
                        if (alterationStrategy.alter(metadataTableDTO, MetadataOperationType.UPDATE)) {
                            List<MetadataColumnDO> metadataColumnDOS = BeanUtil.deepCloneList(data, MetadataColumnDO.class);
                            columnService.updateBatchById(metadataColumnDOS);
                            addChangeRecord(metadataTableId, tenantId, metadataTableDTO.getDatasourceName());
                        }
                    }
                } catch (Exception e) {
                    log.error("failed to updated metadata !", e);
                }
            });
            thread.start();
        } catch (Exception e) {
            log.error("failed to updated metadata !", e);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    public String uploadMetadata(HttpServletRequest request, Long metadataTableId, String tenantId) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        InputStream in = null;
        List<List<Object>> metadataList = null;
        MultipartFile file = multipartRequest.getFile("file");
        log.info("Excel文件名称：{}", file.getOriginalFilename());
        StringBuilder message = new StringBuilder();
        List<MetadataColumnDO> metadataColumnDOList = new LinkedList<>();
        try {
            in = file.getInputStream();
            metadataList = ImportExcelUtils.getListByExcel(in, file.getOriginalFilename());
            log.info("元数据信息集合信息：{}", JSON.toJSONString(metadataList));
            if (CollectionUtils.isNotEmpty(metadataList)) {
                List<MetadataColumnDO> metadataColumnDOS = columnService.list(new QueryWrapper<MetadataColumnDO>()
                        .lambda()
                        .eq(MetadataColumnDO::getTableId, metadataTableId)
                        .eq(MetadataColumnDO::getTenantId, tenantId)
                        .eq(MetadataColumnDO::getIsDeleted, CommonConstants.IsDeleted.NO));
                Map<String, String> columnNameListMap = metadataColumnDOS.stream()
                        .collect(Collectors.toMap(MetadataColumnDO::getCode, MetadataColumnDO::getCode));

                MetadataTableDO metadataTableDO = tableService.getOne(new QueryWrapper<MetadataTableDO>()
                        .lambda()
                        .eq(MetadataTableDO::getId, metadataTableId)
                        .eq(MetadataTableDO::getTenantId, tenantId)
                        .eq(MetadataTableDO::getIsDeleted, CommonConstants.IsDeleted.NO));
                MetadataTableDTO metadataTableDTO = BeanUtil.deepClone(metadataTableDO, MetadataTableDTO.class);

                int i = 2;
                boolean skip;
                boolean isError = false;
                if (0 == columnTypes.size()) {
                    String databaseColumnTypesKey = metadataProperties.getDatabaseColumnTypesKey();
                    if (StringUtils.isNotEmpty(databaseColumnTypesKey)) {
                        Map<String, String> databaseColumnTypes = redisService.hgetAll(databaseColumnTypesKey);
                        databaseColumnTypes.forEach((k, v) -> {
                            columnTypes.put(k, JSON.parseObject(v, HashMap.class));
                        });
                    }
                }
                Map<String, String> columnTypesMap = columnTypes.get(metadataTableDO.getDatasourceType());
                for (List<Object> item : metadataList) {
                    skip = true;
                    for (Object column : item) {
                        if (null != column && StringUtils.isNotEmpty(column.toString())) {
                            skip = false;
                        }
                    }
                    if (skip) {
                        continue;
                    }
                    if (null != columnNameListMap.get(item.get(0))) {
                        message.append("第").append(i).append("行字段[").append(item.get(0)).append("]已经存在了！\r\n");
                        isError = true;
                    }
                    if (null == item.get(0) || StringUtils.isEmpty(item.get(0).toString()) || null == item.get(1)
                            || StringUtils.isEmpty(item.get(1).toString()) || null == item.get(2)
                            || StringUtils.isEmpty(item.get(2).toString())) {
                        message.append("第").append(i).append("行字段名称、字段类型、字段中文名信息不能为空！\r\n");
                        isError = true;
                    }
                    if (isContainChinese(item.get(0).toString())
                            || null == columnTypesMap.get(item.get(1).toString().toUpperCase())) {
                        message.append("第").append(i).append("行字段名称、字段类型信息格式不正确！\r\n");
                        isError = true;
                    }
                    metadataColumnDOList.add(newColumnMetadataDO(item, metadataTableId));
                    i++;
                }
                if (isError) {
                    return message.toString();
                }
                metadataTableDTO.setColumnDTOS(BeanUtil.deepCloneList(metadataColumnDOList, MetadataColumnDTO.class));
                Thread thread = new Thread(() -> {
                    try {
                        AlterationStrategy alterationStrategy = alterationStrategyContext.getAlterationStrategy(metadataTableDTO.getDatasourceType());
                        if (null != alterationStrategy) {
                            if (alterationStrategy.alter(metadataTableDTO, MetadataOperationType.ADD)) {
                                columnService.saveBatch(metadataColumnDOList);
                                addChangeRecord(metadataTableId, tenantId, metadataTableDTO.getDatasourceName());
                            }
                        }
                    } catch (Exception e) {
                        log.error("上传元数据失败！", e);
                    }
                });
                thread.start();
            } else {
                throw new ApplicationException("上传元数据不能为空！");
            }
            message.append("上传元数据成功！");
        } catch (Exception e) {
            log.error("上传元数据失败！", e);
            message.append("上传元数据失败！");
        }
        return message.toString();
    }

    @Override
    public void downloadMetadata(HttpServletResponse response, String metadataTableId, String tenantId) {
        List<MetadataColumnDO> metadataColumnDOS = columnService.list(new QueryWrapper<MetadataColumnDO>()
                .lambda()
                .eq(MetadataColumnDO::getIsDeleted, CommonConstants.IsDeleted.NO)
                .eq(MetadataColumnDO::getTenantId, tenantId)
                .eq(MetadataColumnDO::getTableId, metadataTableId));

        List<MetadataColumnDownloadDTO> downloadVOList = metadataColumnDOS.stream().map(item -> {
            MetadataColumnDownloadDTO downloadVO = new MetadataColumnDownloadDTO();
            downloadVO.setName(item.getName());
            downloadVO.setCode(item.getCode());
            downloadVO.setDataType(item.getDataType());
            downloadVO.setColumnConstraint(ConstraintTypeEnum.getNamesByCodes(item.getColumnConstraint()));
            if (null == item.getIsPrimaryKey() || item.getIsPrimaryKey().intValue() == CommonConstants.IsPrimaryKey.NO) {
                downloadVO.setIsPrimaryKey("否");
            } else {
                downloadVO.setIsPrimaryKey("是");
            }
            if (null == item.getIsForeignKey() || item.getIsForeignKey().intValue() == CommonConstants.IsForeignKey.NO) {
                downloadVO.setIsForeignKey("否");
            } else {
                downloadVO.setIsForeignKey("是");
            }
            return downloadVO;
        }).collect(Collectors.toList());
        String[] headers = {"字段名称", "字段中文名", "数据类型", "主键", "外键（可选）", "约束"};

        MetadataTableDO metadataTableDO = tableService.getOne(new QueryWrapper<MetadataTableDO>().lambda()
                .eq(MetadataTableDO::getId, metadataTableId)
                .eq(MetadataTableDO::getTenantId, tenantId)
                .eq(MetadataTableDO::getIsDeleted, CommonConstants.IsDeleted.NO));

        DownloadExcelUtils.downloadExcel(response, downloadVOList, headers, metadataTableDO.getCode());
    }

    @Override
    public void downloadExcelTemplate(HttpServletResponse response, String tenantId) {
        InputStream input = null;
        OutputStream output = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", "attachment;filename=excelTemplateUploadingMetadata.xlsx");
            //input = MetadataBizServiceImpl.class.getResourceAsStream(metadataProperties.getExcelTemplateUploadingMetadataPath());
            ClassPathResource classPathResource = new ClassPathResource(metadataProperties.getExcelTemplateUploadingMetadataPath());
            input = classPathResource.getInputStream();
            output = response.getOutputStream();
            byte[] bytes = new byte[1024];
            int i = 0;
            while ((i = input.read(bytes)) > -1) {
                output.write(bytes, 0, i);
            }
        } catch (IOException e) {
            log.error("failed to downloaded excel template uploading metadata !", e);
        } finally {
            try {
                if (null != output) {
                    output.flush();
                    output.close();
                }
                if (null != input) {
                    input.close();
                }
            } catch (IOException e) {
                log.error("failed to downloaded excel template uploading metadata !", e);
            }
        }
    }

    @Override
    public Boolean save(List<MetadataTableDTO> metadataTableDTOS) {
        Map<String, MetadataColumnDO> columnMap = Maps.newHashMap();
        Map<Long, List<MetadataColumnDO>> allOldColumnsMap = Maps.newHashMap();
        Set<Long> dataSourceIds = new HashSet<>();
        List<MetadataColumnDO> columnDOList = Lists.newArrayList();

        metadataTableDTOS.stream().forEach(tableDTO -> {
            Long tableId = null;
            dataSourceIds.add(tableDTO.getDatasourceId());
            columnMap.clear();

            MetadataTableDO metadataTableDO = tableService.getOne(new QueryWrapper<MetadataTableDO>().lambda()
                    .eq(MetadataTableDO::getIsDeleted, CommonConstants.IsDeleted.NO)
                    .eq(MetadataTableDO::getDatasourceId, tableDTO.getDatasourceId())
                    .eq(MetadataTableDO::getCode, tableDTO.getCode()));

            if (null != metadataTableDO) {
                tableId = metadataTableDO.getId();

                //再查出原来的表的字段，放到columnMap 和allOldColumnsMap 中  allOldColumnsMap是原来的tableId 和原来的字段的对应
                List<MetadataColumnDO> data = columnService.list(new QueryWrapper<MetadataColumnDO>().lambda()
                        .eq(MetadataColumnDO::getIsDeleted, CommonConstants.IsDeleted.NO)
                        .eq(MetadataColumnDO::getIsPartitionKey, CommonConstants.IsPartitionKey.NO)
                        .eq(MetadataColumnDO::getTableId, tableId));

                if (!CollectionUtils.isEmpty(data)) {
                    data.stream().forEach(item -> {
                        columnMap.put(item.getCode(), item);
                    });
                    allOldColumnsMap.put(tableId, JSON.parseArray(JSON.toJSONString(data), MetadataColumnDO.class));
                }
            }
            boolean isNew = null == metadataTableDO;
            if (null == tableId) {
                tableDTO.setVersionNumber(1);
                tableDTO.setCreatedBy("sys");
                tableDTO.setUpdatedBy("sys");
            } else {
                tableDTO.setVersionNumber(metadataTableDO.getVersionNumber() == null ? 1 : metadataTableDO.getVersionNumber());
                tableDTO.setId(tableId);
                tableDTO.setCreatedBy(metadataTableDO.getCreatedBy());
                tableDTO.setCreatedTime(metadataTableDO.getCreatedTime());
                tableDTO.setUpdatedBy("sys");
            }
            MetadataTableDO tableDO = BeanUtil.deepClone(tableDTO, MetadataTableDO.class);
            tableService.saveOrUpdate(tableDO);
            tableDTO.setId(tableDO.getId());

            int orderNo = 0;
            for (MetadataColumnDTO columnDTO : tableDTO.getColumnDTOS()) {
                if (null == columnMap.get(columnDTO.getCode())) {
                    columnDTO.setCreatedBy("sys");
                    columnDTO.setUpdatedBy("sys");
                } else {
                    columnDTO.setId(columnMap.get(columnDTO.getCode()).getId());
                    columnDTO.setCreatedBy(columnMap.get(columnDTO.getCode()).getCreatedBy());
                    columnDTO.setCreatedTime(columnMap.get(columnDTO.getCode()).getCreatedTime());
                    columnDTO.setUpdatedBy("sys");
                }
                columnDTO.setTableId(tableDTO.getId());
                columnDTO.setSortNo(orderNo);

                MetadataColumnDO metadataColumnDO = BeanUtil.deepClone(columnDTO, MetadataColumnDO.class);
                columnService.saveOrUpdate(metadataColumnDO);
                columnDOList.add(metadataColumnDO);
                orderNo++;
            }
            if (isNew) {
                addChangeRecord(tableDTO, 1);
            }
        });

        if (CollectionUtils.isNotEmpty(metadataTableDTOS)) {
            deleteTablesAndColumns(metadataTableDTOS, allOldColumnsMap,
                    dataSourceIds.toArray(new Long[dataSourceIds.size()]));

            /** ------------------ 首先把就的索引和分区数据都删除------------------------------------- */
            List<Long> tableIdList = metadataTableDTOS.stream().map(MetadataTableDTO::getId)
                    .collect(Collectors.toList());
            indexService.removeByTableIds(tableIdList);
            partitionService.removeByTableIds(tableIdList);
            columnService.removePartitionColumnByTableIds(tableIdList);
            /** ---------------------------------添加索引和分区信息------------------------------- */
            addIndexMetadata(metadataTableDTOS);
            addPartitionMetadata(metadataTableDTOS);
            addPartitionColumnMetadata(metadataTableDTOS);
        }
        return true;
    }

    /**
     * 更新索引元数据
     *
     * @param metadataTableDTOS
     */
    private void addIndexMetadata(List<MetadataTableDTO> metadataTableDTOS) {
        log.info("{}--------  开始 收集 索引数据", DateUtil.now());
        for (MetadataTableDTO tableMetadataVO : metadataTableDTOS) {
            List<MetadataIndexDTO> metadataIndexDTOList = tableMetadataVO.getIndexDTOS();
            String tenantId = tableMetadataVO.getTenantId();
            Long tableId = tableMetadataVO.getId();
            if (CollectionUtils.isNotEmpty(metadataIndexDTOList)) {
                List<MetadataIndexDO> indexDOS = Lists.newArrayList();
                for (MetadataIndexDTO metadataIndexDTO : metadataIndexDTOList) {
                    metadataIndexDTO.setTableId(tableId);
                    metadataIndexDTO.setTenantId(tenantId);
                    MetadataIndexDO metadataIndexDO = BeanUtil.deepClone(metadataIndexDTO, MetadataIndexDO.class);
                    indexDOS.add(metadataIndexDO);
                }
                indexService.saveBatch(indexDOS);
            }
        }
        log.info("{}--------  结束收集索引数据", DateUtil.now());
    }

    /**
     * 更新 分区 元数据
     */
    private void addPartitionMetadata(List<MetadataTableDTO> metadataTableDTOS) {
        log.info("{}--------  开始收集 分区 数据  tableName---------------", DateUtil.now());
        for (MetadataTableDTO metadataTableDTO : metadataTableDTOS) {
            List<MetadataPartitionDTO> metadataPartitionDTOS = metadataTableDTO.getPartitionDTOS();
            String tenantId = metadataTableDTO.getTenantId();
            Long tableId = metadataTableDTO.getId();
            if (CollectionUtils.isNotEmpty(metadataPartitionDTOS)) {
                List<MetadataPartitionDO> partitionDOS = Lists.newArrayList();
                for (MetadataPartitionDTO metadataPartitionDTO : metadataPartitionDTOS) {
                    metadataPartitionDTO.setTableId(tableId);
                    metadataPartitionDTO.setIsDeleted(CommonConstants.IsDeleted.NO);
                    metadataPartitionDTO.setTenantId(tenantId);
                    MetadataPartitionDO metadataPartitionDO = BeanUtil.deepClone(metadataPartitionDTO, MetadataPartitionDO.class);
                    partitionDOS.add(metadataPartitionDO);
                }
                partitionService.saveBatch(partitionDOS);
            }
        }
        log.info("{}--------  结束收集   分区  数据", DateUtil.now());
    }

    /**
     * 更新 分区字段 元数据  只有hive才会用到
     */
    private void addPartitionColumnMetadata(List<MetadataTableDTO> metadataTableDTOS) {
        log.info("{}--------  开始  添加 分区字段 数据", new Date());
        for (MetadataTableDTO metadataTableDTO : metadataTableDTOS) {
            List<TablesDescDTO> tablesDescDTOS = metadataTableDTO.getTablesDescDTOS();
            String tenantId = metadataTableDTO.getTenantId();
            Long tableId = metadataTableDTO.getId();
            if (CollectionUtils.isNotEmpty(tablesDescDTOS)) {
                List<MetadataColumnDO> columnDOS = Lists.newArrayList();
                for (TablesDescDTO tablesDescDTO : tablesDescDTOS) {
                    MetadataColumnDO columnDO = new MetadataColumnDO();
                    columnDO.setCode(tablesDescDTO.getCol_name());
                    columnDO.setName(StringUtils.isNotBlank(tablesDescDTO.getComment()) ? tablesDescDTO.getComment() : tablesDescDTO.getCol_name());
                    columnDO.setDataType(tablesDescDTO.getData_type());
                    columnDO.setTableId(tableId);
                    columnDO.setTenantId(tenantId);
                    columnDO.setIsPartitionKey(CommonConstants.IsPartitionKey.YES);
                    columnDOS.add(columnDO);
                }
                columnService.saveBatch(columnDOS);
            }
        }

        log.info("{}--------  结束  添加    分区字段  数据", DateUtil.now());
    }

    /**
     * @param newMetadataTableDTOS 新的表元数据集合
     * @param allOldColumnsMap     所有老的字段集合
     * @param dataSourceIds        数据源Id
     * @desc 删除数据源中不存在的表和字段元数据
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void deleteTablesAndColumns(List<MetadataTableDTO> newMetadataTableDTOS,
                                        Map<Long, List<MetadataColumnDO>> allOldColumnsMap, Long... dataSourceIds) {
        LambdaQueryWrapper<MetadataTableDO> queryWrapper = new QueryWrapper<MetadataTableDO>().lambda();
        queryWrapper.eq(MetadataTableDO::getIsDeleted, CommonConstants.IsDeleted.NO);
        queryWrapper.in(MetadataTableDO::getDatasourceId, dataSourceIds);
        if (newMetadataTableDTOS.size() == 1) {
            queryWrapper.eq(MetadataTableDO::getCode, newMetadataTableDTOS.get(0).getCode());
        }
        List<MetadataTableDO> metadataTableDOS = tableService.list(queryWrapper);
        if (!CollectionUtils.isEmpty(metadataTableDOS)) {
            List<MetadataTableDTO> data = BeanUtil.deepCloneList(metadataTableDOS, MetadataTableDTO.class);

            Map<Long, MetadataTableDTO> newTablesMap = Maps.newHashMap();
            newMetadataTableDTOS.stream().forEach(item -> {
                newTablesMap.put(item.getId(), item);
            });
            data.stream().forEach(item -> {
                Long tableId = item.getId();
                //原来有这张表，但是现在没有了的，就删除，并且把这张表的字段也全部删除
                MetadataTableDTO newTable = newTablesMap.get(tableId);
                if (null == newTable) {
                    tableService.removeById(tableId);

                    columnService.remove(new QueryWrapper<MetadataColumnDO>().lambda()
                            .eq(MetadataColumnDO::getIsDeleted, CommonConstants.IsDeleted.NO)
                            .eq(MetadataColumnDO::getTableId, tableId));
                } else {
                    //表没有变动，检查字段有没有变动，主键，字段长度，类型等
                    boolean isChanged = false;
                    if (null == allOldColumnsMap.get(tableId)) {
                        return;
                    }
                    List<MetadataColumnDTO> newColumnDTOS = newTable.getColumnDTOS();
                    Map<Long, MetadataColumnDTO> newColumnsMap = Maps.newHashMap();
                    newColumnDTOS.stream().forEach(columnVO -> {
                        newColumnsMap.put(columnVO.getId(), columnVO);
                    });
                    Map<Long, Long> oldColumnsMap = Maps.newHashMap();
                    for (MetadataColumnDO oldColumn : allOldColumnsMap.get(tableId)) {
                        oldColumnsMap.put(oldColumn.getId(), oldColumn.getId());
                        if (null == newColumnsMap.get(oldColumn.getId())) {
                            //原来有某个字段，但是现在没有了，删除该字段
                            isChanged = true;
                            columnService.removeById(oldColumn.getId());
                        } else {
                            // 字段个数没有变动， 就检查字段属性是不是有变动
                            MetadataColumnDTO newColumn = newColumnsMap.get(oldColumn.getId());
                            if (!isChanged) {
                                isChanged = wasChanged(newColumn, oldColumn);
                            }
                        }
                    }
                    for (MetadataColumnDTO newColumn : newColumnDTOS) {
                        if (null == oldColumnsMap.get(newColumn.getId())) {
                            isChanged = true;
                            break;
                        }
                    }
                    if (isChanged) {
                        //变更表元数据的版本号
                        int versionNumber = newTable.getVersionNumber() + 1;
                        addChangeRecord(newTable, versionNumber);
                        MetadataTableDO tableDO = new MetadataTableDO();
                        tableDO.setVersionNumber(versionNumber);
                        tableService.update(tableDO, new QueryWrapper<MetadataTableDO>().lambda()
                                .eq(MetadataTableDO::getId, tableId)
                                .eq(MetadataTableDO::getIsDeleted, CommonConstants.IsDeleted.NO));
                    }
                    newColumnDTOS = null;
                }
            });
        }
    }

    /**
     * @param newColumn
     * @param oldColumn
     * @return
     * @desc 检查同一张表，同一个一个字段，属性是不是有变动
     */
    private boolean wasChanged(MetadataColumnDTO newColumn, MetadataColumnDO oldColumn) {
        boolean wasChanged = false;
        if (StringUtils.isNotEmpty(newColumn.getDataType()) != StringUtils.isNotEmpty(oldColumn.getDataType())
                || StringUtils.isNotEmpty(newColumn.getDataType())
                && StringUtils.isNotEmpty(oldColumn.getDataType())
                && !newColumn.getDataType().equals(oldColumn.getDataType())
                || StringUtils.isNotEmpty(newColumn.getName()) != StringUtils
                .isNotEmpty(oldColumn.getName())
                || StringUtils.isNotEmpty(newColumn.getName())
                && StringUtils.isNotEmpty(oldColumn.getName())
                && !newColumn.getName().equals(oldColumn.getName())
                || (null != newColumn.getIsRequired()) != (null != oldColumn.getIsRequired())
                || null != newColumn.getIsRequired() && null != oldColumn.getIsRequired()
                && newColumn.getIsRequired().intValue() != oldColumn.getIsRequired().intValue()
                || (null != newColumn.getIsPrimaryKey()) != (null != oldColumn.getIsPrimaryKey())
                || null != newColumn.getIsPrimaryKey() && null != oldColumn.getIsPrimaryKey()
                && newColumn.getIsPrimaryKey().intValue() != oldColumn.getIsPrimaryKey().intValue()
                || (null != newColumn.getLength()) != (null != oldColumn.getLength())
                || null != newColumn.getLength() && null != oldColumn.getLength()
                && newColumn.getLength().intValue() != oldColumn.getLength().intValue()
                || (null != newColumn.getPrecision()) != (null != oldColumn.getPrecision())
                || null != newColumn.getPrecision() && null != oldColumn.getPrecision()
                && newColumn.getPrecision().intValue() != oldColumn.getPrecision().intValue()) {
            wasChanged = true;
        }
        return wasChanged;
    }

    /**
     * @param newTable
     * @param versionNumber
     * @desc 添加表元数据变更记录
     */
    private void addChangeRecord(MetadataTableDTO newTable, int versionNumber) {
        MetadataTableHistoryDO metadataTableHistoryDO = new MetadataTableHistoryDO();
        metadataTableHistoryDO.setStatus(TableMetadataHistoryRecordStatus.OFFLINE);
        tableHistoryService.update(metadataTableHistoryDO, new UpdateWrapper<MetadataTableHistoryDO>()
                .lambda()
                .eq(MetadataTableHistoryDO::getTableId, newTable.getId())
                .eq(MetadataTableHistoryDO::getStatus, TableMetadataHistoryRecordStatus.SERVICING)
                .eq(MetadataTableHistoryDO::getIsDeleted, CommonConstants.IsDeleted.NO));

        MetadataTableHistoryDO historyRecord = new MetadataTableHistoryDO();
        historyRecord.setStatus(CommonConstants.TableMetadataHistoryRecordStatus.SERVICING);
        historyRecord.setVersionNumber(versionNumber);
        historyRecord.setTableId(newTable.getId());
        historyRecord.setTableMetadataJson(JSONObject.toJSONString(newTable));
        historyRecord.setTenantId(newTable.getTenantId());
        historyRecord.setIsDeleted(CommonConstants.IsDeleted.NO);
        historyRecord.setCreatedBy(newTable.getUpdatedBy());
        historyRecord.setCreatedTime(newTable.getUpdatedTime());
        tableHistoryService.save(historyRecord);

        /**
         * 表有变动，发送订阅通知
         */
        log.info("{}-addChangeRecord--------表有变动，发送订阅通知", new Date());
        SendInformDTO sendInformDTO = new SendInformDTO(newTable.getTenantId(), newTable.getId());
        sendInformDTO.setDatasourceId(newTable.getDatasourceId());
        sendInformDTO.setDatasourceName(newTable.getDatasourceName());
        sendInformDTO.setTableName(newTable.getCode());
        userSubscribeService.inform(sendInformDTO);
    }

    @Override
    public Boolean delete(MetadataTableDTO metadataTableDTO) {
        MetadataTableDO metadataTableDO = tableService.getOne(new QueryWrapper<MetadataTableDO>()
                .lambda()
                .eq(MetadataTableDO::getIsDeleted, CommonConstants.IsDeleted.NO)
                .eq(MetadataTableDO::getDatasourceId, metadataTableDTO.getDatasourceId())
                .eq(MetadataTableDO::getCode, metadataTableDTO.getCode()));

        metadataTableDTO = BeanUtil.deepClone(metadataTableDO, MetadataTableDTO.class);
        if (null != metadataTableDTO.getId()) {
            return Boolean.TRUE;
        }

        tableService.removeById(metadataTableDTO.getId());

        columnService.remove(new QueryWrapper<MetadataColumnDO>()
                .lambda()
                .eq(MetadataColumnDO::getIsDeleted, CommonConstants.IsDeleted.NO)
                .eq(MetadataColumnDO::getTableId, metadataTableDTO.getId()));
        return Boolean.TRUE;
    }

    @Override
    public Boolean wasChangedPrimaryKey(List<MetadataColumnDTO> newColumnDTO, Long tableMetadataId) {
        List<MetadataColumnDO> oldColumnDOS = columnService.list(new QueryWrapper<MetadataColumnDO>()
                .lambda()
                .eq(MetadataColumnDO::getIsDeleted, CommonConstants.IsDeleted.NO)
                .eq(MetadataColumnDO::getTableId, tableMetadataId));

        if (CollectionUtils.isNotEmpty(oldColumnDOS)) {
            List<MetadataColumnDTO> metadataColumnDTOS = BeanUtil.deepCloneList(oldColumnDOS, MetadataColumnDTO.class);
            Map<Long, MetadataColumnDTO> oldColumnsMap = Maps.newHashMap();
            metadataColumnDTOS.stream().forEach(item -> {
                oldColumnsMap.put(item.getId(), item);
            });
            for (MetadataColumnDTO newColumn : newColumnDTO) {
                MetadataColumnDTO oldColumn = oldColumnsMap.get(newColumn.getId());
                if ((null != newColumn.getIsPrimaryKey()) != (null != oldColumn.getIsPrimaryKey())
                        || null != newColumn.getIsPrimaryKey() && null != oldColumn.getIsPrimaryKey() && !newColumn
                        .getIsPrimaryKey().equals(oldColumn.getIsPrimaryKey())) {
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }


    /**
     * @param metadataTableId
     * @return
     * @desc 添加表元数据的变更记录
     */
    private Boolean isChanged(List<MetadataColumnDTO> metadataColumnDTOS, Long metadataTableId) {
        boolean isChanged = false;
        List<MetadataColumnDO> metadataColumnDOList = columnService.list(new QueryWrapper<MetadataColumnDO>()
                .lambda()
                .eq(MetadataColumnDO::getIsDeleted, CommonConstants.IsDeleted.NO)
                .eq(MetadataColumnDO::getTableId, metadataTableId));

        Map<Long, MetadataColumnDO> oldColumnsMap = Maps.newHashMap();
        metadataColumnDOList.stream().forEach(item -> {
            oldColumnsMap.put(item.getId(), item);
        });
        for (MetadataColumnDTO metadataColumnDTO : metadataColumnDTOS) {
            MetadataColumnDO oldColumn = oldColumnsMap.get(metadataColumnDTO.getId());
            metadataColumnDTO.setIsRequired(oldColumn.getIsRequired());
            if (StringUtils.isNotEmpty(metadataColumnDTO.getName()) != StringUtils
                    .isNotEmpty(oldColumn.getName())
                    || StringUtils.isNotEmpty(metadataColumnDTO.getName())
                    && StringUtils.isNotEmpty(oldColumn.getName())
                    && !metadataColumnDTO.getName().equals(oldColumn.getName())
                    || (null != metadataColumnDTO.getIsPrimaryKey()) != (null != oldColumn.getIsPrimaryKey())
                    || null != metadataColumnDTO.getIsPrimaryKey() && null != oldColumn.getIsPrimaryKey()
                    && !metadataColumnDTO.getIsPrimaryKey().equals(oldColumn.getIsPrimaryKey())) {
                isChanged = true;
            }
        }
        return isChanged;
    }

    /**
     * @param metadataTableId
     * @param datasourceName
     * @throws InterruptedException
     * @desc 添加变更记录
     */
    private void addChangeRecord(Long metadataTableId, String tenantId, String datasourceName) {
        MetadataTableDO metadataTableDO = tableService.getOne(new QueryWrapper<MetadataTableDO>()
                .lambda()
                .eq(MetadataTableDO::getTenantId, tenantId)
                .eq(MetadataTableDO::getId, metadataTableId));
        MetadataTableDTO metadataTableDTO = BeanUtil.deepClone(metadataTableDO, MetadataTableDTO.class);

        List<MetadataColumnDO> metadataColumnDOS = columnService.list(new QueryWrapper<MetadataColumnDO>()
                .lambda()
                .eq(MetadataColumnDO::getTableId, metadataTableId)
                .orderByAsc(MetadataColumnDO::getSortNo));
        List<MetadataColumnDTO> metadataColumnDTOS = BeanUtil.deepCloneList(metadataColumnDOS, MetadataColumnDTO.class);
        metadataTableDTO.setColumnDTOS(metadataColumnDTOS);

        List<MetadataIndexDO> indexDOS = indexService.list(new QueryWrapper<MetadataIndexDO>()
                .lambda()
                .eq(MetadataIndexDO::getTableId, metadataTableId));
        List<MetadataIndexDTO> indexDTOS = BeanUtil.deepCloneList(indexDOS, MetadataIndexDTO.class);
        metadataTableDTO.setIndexDTOS(indexDTOS);

        List<MetadataPartitionDO> partitionDOS = partitionService.list(new QueryWrapper<MetadataPartitionDO>()
                .lambda()
                .eq(MetadataPartitionDO::getTableId, metadataTableId));
        List<MetadataPartitionDTO> partitionDTOS = BeanUtil.deepCloneList(partitionDOS, MetadataPartitionDTO.class);
        metadataTableDTO.setPartitionDTOS(partitionDTOS);

        Integer versionNumber = metadataTableDTO.getVersionNumber() == null ? 1 : metadataTableDTO.getVersionNumber() + 1;
        MetadataTableHistoryDO metadataTableHistoryDO = new MetadataTableHistoryDO();
        metadataTableHistoryDO.setStatus(TableMetadataHistoryRecordStatus.OFFLINE);
        tableHistoryService.update(metadataTableHistoryDO, new UpdateWrapper<MetadataTableHistoryDO>()
                .lambda()
                .eq(MetadataTableHistoryDO::getTableId, metadataTableId)
                .eq(MetadataTableHistoryDO::getStatus, TableMetadataHistoryRecordStatus.SERVICING));

        MetadataTableHistoryDO historyDO = new MetadataTableHistoryDO();
        historyDO.setTableId(metadataTableId);
        historyDO.setStatus(CommonConstants.TableMetadataHistoryRecordStatus.SERVICING);
        historyDO.setVersionNumber(versionNumber);
        historyDO.setTableMetadataJson(JSONObject.toJSONString(metadataTableDTO));
        historyDO.setTenantId(tenantId);
        tableHistoryService.save(historyDO);

        metadataTableDO.setVersionNumber(versionNumber);
        tableService.updateById(metadataTableDO);

        /**
         * 表有变动，发送订阅通知
         */
        log.info("{}-addChangeRecord--------表有变动，发送订阅通知", DateUtil.now());
        SendInformDTO sendInformDTO = new SendInformDTO(tenantId, metadataTableDO.getId());
        sendInformDTO.setTableId(metadataTableDO.getId());
        sendInformDTO.setDatasourceId(metadataTableDO.getDatasourceId());
        sendInformDTO.setDatasourceName(datasourceName);
        sendInformDTO.setTableName(metadataTableDO.getCode());
        userSubscribeService.inform(sendInformDTO);
    }

    /**
     * @param data
     * @return
     * @desc 创建ColumnMetadataVO对象
     */
    private MetadataColumnDO newColumnMetadataDO(List<Object> data, Long metadataTableId) {
        MetadataColumnDO columnVO = new MetadataColumnDO();
        columnVO.setCode(data.get(0).toString());
        columnVO.setName(data.get(2).toString());
        columnVO.setDataType(data.get(1).toString());
        columnVO.setLength(data.size() >= 4 && null != data.get(3) && StringUtils.isNotEmpty(data.get(3).toString())
                ? Integer.valueOf(data.get(3).toString().trim())
                : null);
        columnVO.setPrecision(data.size() >= 5 && null != data.get(4) && StringUtils.isNotEmpty(data.get(4).toString())
                ? Integer.valueOf(data.get(4).toString().trim())
                : null);
        columnVO.setTableId(metadataTableId);
        return columnVO;
    }

    public static final Boolean isContainChinese(String value) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(value);
        if (m.find()) {
            return true;
        }
        return false;
    }
}
