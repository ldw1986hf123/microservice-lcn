package com.ldw.microservice.api.impl;

import com.ldw.microservice.api.BloodDataApi;
import com.ldw.microservice.config.InternalPayload;
import com.ldw.microservice.dto.ColumnBloodDTO;
import com.ldw.microservice.service.ColumnBloodBizService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @Author 卢丹文
 * @Description
 */
@Slf4j
@Api(tags="血缘",value="/_internal/v1/data/blood")
@RestController
@RequestMapping("/_internal/v1/data/blood")
public class BloodDataApiImpl implements BloodDataApi {

    @Autowired
    private ColumnBloodBizService columnBloodBizService;
    @Override
    public InternalPayload<ColumnBloodDTO> getColumnBloodInternal(String tenantId, Long jobId) {
        ColumnBloodDTO columnBloodInternal = columnBloodBizService.getColumnBloodInternal(tenantId, jobId);
        return InternalPayload.of(columnBloodInternal);
    }
}
