package com.ldw.microservice.docker.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(value="基础返回类",description="基础返回类")
public class FFResponseModel<T> implements Serializable {

    private static final long serialVersionUID = -2215304260629038881L;
    // 状态码
    @ApiModelProperty(example="成功")
    private String code;
    // 业务提示语
    @ApiModelProperty(example="000000")
    private String msg;
    // 数据对象
    private T data;
}