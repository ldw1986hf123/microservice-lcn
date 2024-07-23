package com.ldw.microservice.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/4 15:06
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TableBloodJobQuery extends BaseQuery {

    @NotBlank(message = "tenantId不能为空")
    @ApiModelProperty(value = "租户Id", example = "1")
    private String tenantId;

    @NotNull(message = "表id不能为空")
    @ApiModelProperty(value = "表id", example = "0")
    private Long tableId;

    @ApiModelProperty(value = "是否该表的上游，true-是，false-否", example = "true")
    private Boolean isUpstream;

    @ApiModelProperty(value = "job状态，0-下线，1-在线，null-全部", example = "1")
    private Integer jobStatus;

    @ApiModelProperty(value = "所在层", example = "1")
    private Integer level;

    @ApiModelProperty(value = "基准表Id", example = "1")
    private Long baseTableId;

    public Boolean getIsUpstream(){
        if (null == isUpstream) {
            return true;
        }
        return isUpstream;
    }
}
