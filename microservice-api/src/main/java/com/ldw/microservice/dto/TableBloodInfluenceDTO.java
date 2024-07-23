package com.ldw.microservice.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/6 11:00
 * @Description 影响分析VO
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class TableBloodInfluenceDTO implements Serializable {

    /**
     * @desc 层级
     */
    private Integer level;
    /**
     * @desc 表名
     */
    private String tableName;
    /**
     * @desc owner
     */
    private String owner;
    /**
     * @desc 表id
     */
    private Long tableId;
    /**
     * @desc 最新产出任务id jobId
     */
    private Long jobId;
    /**
     * @desc 最新产出任务id jobId
     */
    private String jobName;

    /**
     * @desc 基准表Id
     */
    private Long baseTableId;
    /**
     * @desc job状态
     */
    private Integer jobStatus;
}
