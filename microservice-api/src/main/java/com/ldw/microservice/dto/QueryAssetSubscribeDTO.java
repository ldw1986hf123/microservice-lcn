package com.ldw.microservice.dto;

import com.deepexi.data.metadata.domain.eo.SuperEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: ludanwen
 * @time: 2020/11/3 10:40
 */
@Data
public class QueryAssetSubscribeDTO extends SuperEntity implements Serializable {

    private Long projectId;
    private Long tableId;
    private String tableName ;
    private Long datasourceId ;
    private String datasourceType ;
    private String datasourceName ;
    private String keyword ;

}
