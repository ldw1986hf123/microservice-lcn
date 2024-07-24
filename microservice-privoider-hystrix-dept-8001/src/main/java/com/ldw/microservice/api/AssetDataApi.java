package com.ldw.microservice.api;


import com.ldw.microservice.config.InternalPayload;
import com.ldw.microservice.dto.ColumnOptionalDTO;
import com.ldw.microservice.dto.DatasourceOptionalDTO;
import com.ldw.microservice.dto.DatasourceTypeDTO;
import com.ldw.microservice.dto.TableOptionalDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 *
 * @Author 卢丹文
 * @Description 数据域API接口
 */
@FeignClient(name = "${deepexi.daas.metadata.service.application.name:deepexi-daas-metadata2}", path = "/_internal/v1/data/asset")
public interface AssetDataApi {

    @GetMapping(value = "dataSourceTypes")
    InternalPayload<List<DatasourceTypeDTO>> getDataSourceTypes();

    /**
     * 获取数据源
     * @param tenantId
     * @param projects
     * @param datasourceIds
     * @param environmentType 环境类型 开发0、生产1、基础环境2
     * @param datasourceType
     * @return
     */
    @GetMapping(value = "dataSources")
    InternalPayload<List<DatasourceOptionalDTO>> getDatasources(@RequestParam(value = "tenantId", required = true) String tenantId,
                                                                @RequestParam(value = "projects", required = false) List<Long> projects,
                                                                @RequestParam(value = "datasourceIds", required = false) List<Long> datasourceIds,
                                                                @RequestParam(value = "environmentType", required = true) Integer environmentType,
                                                                @RequestParam(value = "datasourceType", required = false) String datasourceType,
                                                                @RequestParam(value = "userId", required = true) Long userId,
                                                                @RequestParam(value = "userName", required = true) String userName
    );

    /**
     *
     * @param tenantId
     * @param datasourceId
     * @param dataSourceType
     * @param userId
     * @param userName
     * @param type 空-全部，0-元数据，1-授权数据
     * @return
     */
    @GetMapping(value = "tables")
    InternalPayload<List<TableOptionalDTO>> getTables(@RequestParam(value = "tenantId", required = true) String tenantId,
                                                      @RequestParam(value = "datasourceId", required = false) Long datasourceId,
                                                      @RequestParam(value = "dataSourceType", required = false) String dataSourceType,
                                                      @RequestParam(value = "userId", required = true) Long userId,
                                                      @RequestParam(value = "userName", required = true) String userName,
                                                      @RequestParam(value = "type", required = true) Integer type
    );

    @GetMapping(value = "tableColumns")
    InternalPayload<List<ColumnOptionalDTO>> getTableColumns(@RequestParam(value = "tenantId", required = true) String tenantId,
                                                             @RequestParam(value = "tableId", required = true) Long tableId);
}
