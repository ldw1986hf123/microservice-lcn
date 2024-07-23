package com.ldw.microservice.query;

import com.deepexi.data.metadata.config.valid.BlankOrPattern;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/5 11:00
 * @Description 影响分析下载查询query
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TableBloodInfluenceDownloadQuery {

    /**
     * 租户id
     */
    @NotBlank(message = "tenantId不能为空")
    @ApiModelProperty(value = "租户Id", example = "1", required = true)
    private String tenantId;

    @NotNull(message = "表id不能为空")
    @ApiModelProperty(value = "表id", example = "0", required = true)
    private Long tableId;

    @BlankOrPattern(regexp = "/^[1-9]d*$/", message = "层数必须大于0的正整数")
    @ApiModelProperty(value = "层级", example = "3")
    private Integer level;

    @ApiModelProperty(value = "表名", example = "user")
    private String tableName;

    @ApiModelProperty(value = "job状态，0-下线，1-在线，null-全部", example = "1")
    private Integer jobStatus;
}
