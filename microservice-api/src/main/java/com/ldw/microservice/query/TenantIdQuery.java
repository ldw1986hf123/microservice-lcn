package com.ldw.microservice.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 *   
 *
 * @Author Fucy
 * @Date 2021/1/6 10:13
 * @Description Query通用类
 */
@Data
public class TenantIdQuery {
    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户Id", example = "1")
    private String tenantId;
}
