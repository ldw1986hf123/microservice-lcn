package com.ldw.microservice.api.impl;


import com.ldw.microservice.api.AssetDataApi;
import com.ldw.microservice.config.InternalPayload;
import com.ldw.microservice.dto.*;
import io.micrometer.core.instrument.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @Author 卢丹文
 * @Date 2020/12/29 11:38
 */
@Slf4j
@Api(tags="数据源相关数据",value="/_internal/v1/data/asset")
@RestController
@RequestMapping("/_internal/v1/data/asset")
public class AssetDataApiImpl implements AssetDataApi {

    @Autowired
    private CommonEsBizService commonEsBizService;
    @Autowired
    private ProjectDatasourceApi projectDatasourceApi;
    @Autowired
    private DatasourceApi datasourceApi;
    @Autowired
    private AssetApi assetApi;
    @Autowired
    private ProjectUserApi projectUserApi;
    @Autowired
    private ProjectServiceApi projectServiceApi;
    @Autowired
    private AssetPermissionApi assetPermissionApi;
    @Autowired
    private UserServiceApi userServiceApi;
    @Value("${es.timeout:30}")
    private Long esTimeout;
    @Value("${metadata.dataSource.types:1}")
    private String datasourceTypesStr;

    @ApiOperation(value="获取数据源类型列表", notes="获取数据源类型列表")
    @Override
    public InternalPayload<List<DatasourceTypeDTO>> getDataSourceTypes() {
        if (StringUtils.isBlank(datasourceTypesStr)) {
            return null;
        }
        List<DatasourceTypeDTO> datasourceTypes = new LinkedList<DatasourceTypeDTO>();
        String[] items = datasourceTypesStr.split(",");
        if (-1 < datasourceTypesStr.indexOf("-")) {
            Arrays.stream(items).forEach(item -> {
                DatasourceTypeDTO datasourceType = new DatasourceTypeDTO();
                String[] keyValue = (item).split("-");
                datasourceType.setCode(keyValue[0]);
                datasourceType.setName(keyValue[1]);
                datasourceTypes.add(datasourceType);
            });
            // 这里可以在项目启动的时候存到redis里面去
        }
        return InternalPayload.of(datasourceTypes);
    }

    @ApiOperation(value="获取数据源列表", notes="获取数据源列表")
    @Override
    public InternalPayload<List<DatasourceOptionalDTO>> getDatasources(@RequestParam(value = "tenantId", required = true) String tenantId,
                                                                       @RequestParam(value = "projects", required = false) List<Long> projects,
                                                                       @RequestParam(value = "datasourceIds", required = false) List<Long> datasourceIds,
                                                                       @RequestParam(value = "environmentType", required = true) Integer environmentType,
                                                                       @RequestParam(value = "datasourceType", required = true) String datasourceType,
                                                                       @RequestParam(value = "userId", required = true) Long userId,
                                                                       @RequestParam(value = "userName", required = true) String userName) {

        List<DatasourceOptionalDTO> dataSourceList = new LinkedList<DatasourceOptionalDTO>();
        Map<Long, Long> dataSourceListMap = new HashMap<>();
        Map<Long, Long> dataSourceListMapHive = new HashMap<>();
        Map<Long, Long> hiveDatasourceExtMap = new HashMap<>();
        // 需要获取成员所在的项目的数据源, 目前至于hive有此数据
        com.deepexi.daas.common.config.InternalPayload<List<Long>> listInternalPayload = projectUserApi.queryProjectIdsByUserId(userId, tenantId);
        log.info("projectUserApi.queryProjectIdsByUserId result={}", listInternalPayload);
        List<Long> payload = listInternalPayload.getPayload();
        ProjectDatasourceQuery mumberQuery = new ProjectDatasourceQuery();
        mumberQuery.setTenantId(tenantId);
        List<Long> finalProjects = projects;
        List<Long> collect = payload.stream().filter(p -> !finalProjects.contains(p.longValue())).collect(Collectors.toList());
        mumberQuery.setProjectIdList(collect);
        mumberQuery.setType(datasourceType);
        // 成员的只获取内部的
        mumberQuery.setRelType(CommonConstants.RelType.COMPUTING_RESOURCE);
        mumberQuery.setEnvironmentType(environmentType);
        List<ProjectDatasourceDTO> memberProjectDatasourceDTOs = projectDatasourceApi.findDataSourceInfoByProjectIdList(mumberQuery).getPayload();
        List<ProjectDatasourceDTO> memberDataSourceListDistinct = memberProjectDatasourceDTOs.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()-> new TreeSet<>(Comparator.comparing(e->e.getDatasourceId()))), ArrayList::new));
        Map<Long, Long> memberDataSourceListMapTemp = memberDataSourceListDistinct.stream().collect(
                Collectors.toMap(ProjectDatasourceDTO::getDatasourceId, ProjectDatasourceDTO::getDatasourceId));
        dataSourceListMap.putAll(memberDataSourceListMapTemp);
        // 获取当前
        ProjectDatasourceQuery query = new ProjectDatasourceQuery();
        query.setTenantId(tenantId);
        query.setProjectIdList(projects);
        query.setType(datasourceType);
        query.setEnvironmentType(environmentType);
        List<ProjectDatasourceDTO> projectDatasourceDTOs = projectDatasourceApi.findDataSourceInfoByProjectIdList(query).getPayload();
        if (CollectionUtils.isEmpty(projectDatasourceDTOs)) {
            log.info("internal get datasource findDataSourceInfoByProjectIdList is empty");
            return InternalPayload.of(new ArrayList());
        }
        Map<Long, Long> dataSourceListMapTemp = projectDatasourceDTOs.stream().collect(
                Collectors.toMap(ProjectDatasourceDTO::getDatasourceId, ProjectDatasourceDTO::getDatasourceId));
        dataSourceListMap.putAll(dataSourceListMapTemp);
        log.info("internal get datasource findDataSourceInfoByProjectIdList projectIds={}", dataSourceListMap.values().toString());
        // 申请权限元数据只在生产环境上有, 此处数据产品约定，获取最高权限
        if (datasourceType.equals(DatasourceTypeEnum.HIVE.getType())) {
            ProjectDatasourceQuery hiveExtProjectQuery = new ProjectDatasourceQuery();
            hiveExtProjectQuery.setTenantId(tenantId);
            hiveExtProjectQuery.setProjectIdList(projects);
            hiveExtProjectQuery.setType(datasourceType);
            hiveExtProjectQuery.setEnvironmentType(CommonConstants.DataSourceEnv.PRODUCT);
            hiveExtProjectQuery.setRelType(CommonConstants.RelType.EXTERNAL_DATASOURCE);
            List<ProjectDatasourceDTO> projectDatasourceExtDtos = null;
            try {
                InternalPayload<List<ProjectDatasourceDTO>> dataSourceInfoByProjectIdList = projectDatasourceApi.findDataSourceInfoByProjectIdList(hiveExtProjectQuery);
                projectDatasourceExtDtos = dataSourceInfoByProjectIdList.getPayload();
            } catch (Exception e) {
                log.error("hive get datasource ,get projectDatasource failed, message = {}", e.getMessage());
            }
            if (CollectionUtil.isNotEmpty(projectDatasourceExtDtos)) {
                hiveDatasourceExtMap = projectDatasourceExtDtos.stream().collect(
                        Collectors.toMap(ProjectDatasourceDTO::getDatasourceId, ProjectDatasourceDTO::getDatasourceId));
            }
            List<ProjectInfoDTO> allProject = null;
            try {
                com.deepexi.daas.common.config.InternalPayload<List<ProjectInfoDTO>> allProjectObj = projectServiceApi.findAllProject(tenantId);
                allProject = allProjectObj.getPayload();
            } catch (Exception e) {
                log.error("get security projects error, message={}",e.getMessage());
            }
            if (CollectionUtil.isNotEmpty(allProject)) {
                List<Long> allProjectIds = allProject.stream().map(a -> {
                    return a.getId();
                }).collect(Collectors.toList());
                ProjectDatasourceQuery allProjectQuery = new ProjectDatasourceQuery();
                allProjectQuery.setTenantId(tenantId);
                allProjectQuery.setProjectIdList(allProjectIds);
                allProjectQuery.setType(datasourceType);
                allProjectQuery.setEnvironmentType(CommonConstants.DataSourceEnv.PRODUCT);
                allProjectQuery.setRelType(CommonConstants.RelType.COMPUTING_RESOURCE);
                List<ProjectDatasourceDTO> projectDatasourceDtos = null;
                try {
                    com.deepexi.daas.common.config.InternalPayload<List<ProjectDatasourceDTO>> dataSourceInfoByProjectIdList = projectDatasourceApi.findDataSourceInfoByProjectIdList(allProjectQuery);
                    projectDatasourceDtos = dataSourceInfoByProjectIdList.getPayload();
                } catch (Exception e) {
                    log.error("hive get datasource ,get projectDatasource failed, message = {}", e.getMessage());
                }
                if (CollectionUtil.isNotEmpty(projectDatasourceDtos)) {
                    Map<Long, Long> dataSourceListMapHiveTemp = projectDatasourceDtos.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(e -> e.getDatasourceId()))), ArrayList::new)).stream().collect(
                            Collectors.toMap(ProjectDatasourceDTO::getDatasourceId, ProjectDatasourceDTO::getDatasourceId));
                    dataSourceListMapHive.putAll(dataSourceListMapHiveTemp);
                }
            }
        }
        List<DatasourceDTO> datasourceDTOs = datasourceApi.listByTenantId(tenantId).getPayload();
        Map<Long, Long> finalDataSourceListMapHive = dataSourceListMapHive;
        Map<Long, Long> finalHiveDatasourceExtMap = hiveDatasourceExtMap;
        datasourceDTOs.stream().forEach(dataSource -> {
            // 经产品确认，hive获取数据源为最高权限
            if (datasourceType.equals(dataSource.getType())) {
                if(datasourceType.equals(DatasourceTypeEnum.HIVE.getType())){
                    if (StringUtils.isNotBlank(dataSource.getType()) && dataSource.getType().equals(DatasourceTypeEnum.HIVE.getType())) {
                        // 获取hive的外部数据源
                        if (null == dataSource.getRelType() || null == dataSource.getEnv() || CommonConstants.DataSourceEnv.DEVELOP==dataSource.getEnv()){
                            return;
                        }
                        if(null != finalDataSourceListMapHive.get(dataSource.getId()) && null != dataSource.getRelType() && dataSource.getRelType().equals(CommonConstants.RelType.COMPUTING_RESOURCE)){
                            DatasourceOptionalDTO hiveDto = new DatasourceOptionalDTO();
                            hiveDto.setId(dataSource.getId());
                            hiveDto.setName(dataSource.getName());
                            hiveDto.setType(dataSource.getType());
                            dataSourceList.add(hiveDto);
                        }
                        if(null != finalHiveDatasourceExtMap.get(dataSource.getId()) && null != dataSource.getRelType() && dataSource.getRelType().equals(CommonConstants.RelType.EXTERNAL_DATASOURCE)){
                            DatasourceOptionalDTO hiveDto = new DatasourceOptionalDTO();
                            hiveDto.setId(dataSource.getId());
                            hiveDto.setName(dataSource.getName());
                            hiveDto.setType(dataSource.getType());
                            dataSourceList.add(hiveDto);
                        }


                    }
                } else {
                    if (null == dataSourceListMap.get(dataSource.getId())) {
                        return;
                    }
                    DatasourceOptionalDTO dto = new DatasourceOptionalDTO();
                    dto.setId(dataSource.getId());
                    dto.setName(dataSource.getName());
                    dto.setType(dataSource.getType());
                    dataSourceList.add(dto);
                }
            }
        });
        ArrayList<DatasourceOptionalDTO> resultList = dataSourceList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(e -> e.getId()))), ArrayList::new));
        return InternalPayload.of(resultList);
    }

    @ApiOperation(value="获取数据源下的表", notes="获取数据源下的表")
    @Override
    public InternalPayload<List<TableOptionalDTO>> getTables(@RequestParam(value = "tenantId", required = true) String tenantId,
                                                             @RequestParam(value = "datasourceId", required = false) Long datasourceId,
                                                             @RequestParam(value = "dataSourceType", required = false) String dataSourceType,
                                                             @RequestParam(value = "userId", required = true) Long userId,
                                                             @RequestParam(value = "userName", required = true) String userName,
                                                             @RequestParam(value = "type", required = false) Integer type) {

        List<TableOptionalDTO> content = new ArrayList<TableOptionalDTO>();
        Map<String, Object> mustWhere = new HashMap<String, Object>();
        Map<String, Boolean> sortFieldsToAsc = new HashMap<String, Boolean>();
        String[] includeFields = { "id", "tableComment", "tableName", "projectId", "dataSourceId" };
        // 初始化全文搜索的查询参数
        initParametersSearch(null, tenantId, mustWhere, sortFieldsToAsc, dataSourceType, Arrays.asList(datasourceId));
        List<TableMetadataDTO> tableMetadataVOS = commonEsBizService.searchIndex("table_metadata_index", mustWhere, null, sortFieldsToAsc,
                includeFields, null, esTimeout, TableMetadataDTO.class);
        if (!CollectionUtils.isEmpty(tableMetadataVOS)) {
            List<TableOptionalDTO> collect = tableMetadataVOS.stream().map(t -> {
                TableOptionalDTO dto = new TableOptionalDTO();
                dto.setId(Long.parseLong(t.getId()));
                dto.setTableCode(t.getTableName());
                dto.setTableName(t.getTableComment());
                dto.setProjectId(t.getProjectId());
                return dto;
            }).collect(Collectors.toList());
            content.addAll(collect);
        }
        // 根据表id去重
        ArrayList<TableOptionalDTO> collect = content.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(e -> e.getId()))), ArrayList::new));
        fillProjectName(collect, tenantId);
        return InternalPayload.of(content);
    }

    @ApiOperation(value="获取表的字段", notes="获取表的字段")
    @Override
    public InternalPayload<List<ColumnOptionalDTO>> getTableColumns(@ApiParam(value="租户ID") @RequestParam(value = "tenantId", required = true) String tenantId,
                                                                    @ApiParam(value="表ID") @RequestParam(value = "tableId", required = true) Long tableId) {
        List<ColumnOptionalDTO> columns = new ArrayList<>();
        Map<String, Object> mustWhere = new HashMap<>(3);
        mustWhere.put("tableMetadataId", tableId.toString());
        mustWhere.put("tenantId", tenantId);
        mustWhere.put("isDeleted", "0");

        String[] includeFields = {"id", "columnName", "columnType", "columnComment", "isNull", "isPrimaryKey",
                     "projectId", "tenantId", "tableMetadataId", "tableName", "orderNo", "isForeignKey", "createdTime", "createdBy", "updatedTime", "updatedBy"};

        Map<String, Boolean> sortFieldsToAsc = new HashMap<String, Boolean>(1);
        sortFieldsToAsc.put("orderNo", true);

        List<ColumnMetadataDTO> columnVOs = commonEsBizService.searchIndex("column_metadata_index", mustWhere, null, sortFieldsToAsc,
                includeFields, null, esTimeout, ColumnMetadataDTO.class);
        columns = columnVOs.stream().map(c->{
            ColumnOptionalDTO dto = new ColumnOptionalDTO();
            dto.setId(Long.parseLong(c.getId()));
            dto.setColumnName(c.getColumnName());
            dto.setColumnComment(c.getColumnComment());
            dto.setColumnType(c.getColumnType());
            dto.setTableId(Long.parseLong(c.getTableMetadataId()));
            dto.setIsNull(c.getIsNull());
            dto.setCreatedBy(c.getCreatedBy());
            dto.setCreatedTime(c.getCreatedTime());
            dto.setIsPrimaryKey(c.getIsPrimaryKey());
            dto.setProjectId(c.getProjectId());
            dto.setUpdatedBy(c.getUpdatedBy());
            dto.setUpdatedTime(c.getUpdatedTime());
            return dto;
        }).collect(Collectors.toList());
        return InternalPayload.of(columns);
    }


    /**
     *
     * @desc 初始化全文搜索的查询参数
     *
     * @param projectIds      项目ID数组
     * @param tenantId        租户ID
     * @param mustWhere       and查询条件
     * @param sortFieldsToAsc 排序字段列表
     */
    private void initParametersSearch(List<Long> projectIds, String tenantId, Map<String, Object> mustWhere,
                                      Map<String, Boolean> sortFieldsToAsc, String dataSourceType, List<Long> dataSourceIds) {
        mustWhere.put("tenantId", tenantId);
        if (CollectionUtil.isNotEmpty(projectIds)) {
            mustWhere.put("projectId", projectIds.toArray());
        }
        if (CollectionUtil.isNotEmpty(dataSourceIds)) {
            mustWhere.put("dataSourceId", dataSourceIds.toArray());
        }
        if (StringUtil.isNotBlank(dataSourceType)) {
            mustWhere.put("dataSourceType", dataSourceType);
        }
        mustWhere.put("isDeleted", 0);
        Integer[] envs = { 1, 2 };
        mustWhere.put("dataSourceEnv", envs);
        sortFieldsToAsc.put("createdTime", false);
        sortFieldsToAsc.put("updatedTime", false);
    }

    /**
     *
     * @desc 填充项目名称字段
     *
     * @param tables     返回结果集
     * @param tenantId 住户Id
     */
    private void fillProjectName(List<TableOptionalDTO> tables, String tenantId) {
        if (!CollectionUtils.isEmpty(tables)) {
            List<Long> projectIds = new ArrayList<Long>();
            tables.stream().forEach(item -> {
                Long projectId = item.getProjectId() != null ? Long.valueOf(item.getProjectId().toString()) : -1;
                projectIds.add(projectId);
            });
            List<ProjectInfoDTO> projectInfoDTOs = null;
            try {
                projectInfoDTOs = projectServiceApi.listByProjectIds(tenantId, projectIds)
                        .getPayload();
            } catch (Exception e) {
                log.error("fill projectName failed. message = {}", e.getMessage());
            }
            if (!CollectionUtils.isEmpty(projectInfoDTOs)) {
                Map<Long, String> projectsNameMap = projectInfoDTOs.stream()
                        .collect(Collectors.toMap(ProjectInfoDTO::getId, ProjectInfoDTO::getName));
                tables.stream().forEach(item -> {
                    item.setProjectName(projectsNameMap.get(item.getProjectId()));
                });
            }
        }
    }
}
