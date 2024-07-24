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
public class BaseQuery extends TenantIdQuery{
    /**
     * 分页
     */
    @ApiModelProperty(value = "分页", example = "1")
    private Integer page = 1;
    /**
     * 数目
     */
    @ApiModelProperty(value = "每页数目", example = "10")
    private Integer size = 10;
}
