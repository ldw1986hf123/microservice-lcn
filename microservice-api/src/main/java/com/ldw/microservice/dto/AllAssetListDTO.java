package com.ldw.microservice.dto;

import lombok.Data;

/**
 *
 * @description: 全部资产列表返回类
 * @author: ludanwen
 */
@Data
public class AllAssetListDTO     {
    /**
     * @desc 业务板块ID
     */
    private Long businessSegmentsId;
    /**
     * @desc 数据域ID
     */
    private String dataFieldId;
    /**
     * @desc 项目ID
     */
    private String projectIds;
    /**
     * @desc 表类型编号
     */
    private String tableType;
    /**
     * @desc 数据分层代码
     */
    private String dataLayerType;
    /**
     * @desc 搜索条件
     */
    private String searchValue;
    /**
     * @desc 我的资产id列表，多个id以英文逗号分隔
     */
    private String ids;
    /**
     * @desc 数据源Id
     */
    private Long dataSourceId;
    /**
     * @desc 数据源类型：hive-hive；impala-impala；oracle-oracle
     */
    private String dataSourceType;

}
