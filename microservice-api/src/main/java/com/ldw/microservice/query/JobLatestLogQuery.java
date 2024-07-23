package com.ldw.microservice.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/8 19:47
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class JobLatestLogQuery extends TenantIdQuery implements Serializable {
    private static final long serialVersionUID = 1723087026999136081L;

    @NotNull
    @ApiModelProperty(value = "jobId", example = "1", required = true)
    private Long jobId;

    @ApiModelProperty(value = "业务实例ID", example = "1", required = true)
    private Long processInstanceId;

    @ApiModelProperty(value = "实例ID", example = "1", required = true)
    private Long instanceId;

    @NotNull
    @ApiModelProperty(value = "项目id", example = "1", required = true)
    private Long projectId;

    @ApiModelProperty(value = "项目code", example = "code", required = true)
    private String projectCode;

}
