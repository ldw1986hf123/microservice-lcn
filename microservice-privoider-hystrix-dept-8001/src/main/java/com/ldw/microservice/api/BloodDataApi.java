package com.ldw.microservice.api;

import com.ldw.microservice.config.InternalPayload;
import com.ldw.microservice.dto.ColumnBloodDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @Author 卢丹文
 * @Description 数据域API接口
 */
@FeignClient(name = "${deepexi.daas.metadata.service.application.name:deepexi-daas-metadata2}", path = "/_internal/v1/data/blood")
public interface BloodDataApi {

    /**
     * 获取字段血缘
     * @param tenantId
     * @param jobId
     * @return
     */
    @GetMapping(value = "getColumnBlood")
    InternalPayload<ColumnBloodDTO> getColumnBloodInternal(@RequestParam(value = "tenantId", required = true) String tenantId,
                                                           @RequestParam(value = "jobId", required = true) Long jobId);

}
