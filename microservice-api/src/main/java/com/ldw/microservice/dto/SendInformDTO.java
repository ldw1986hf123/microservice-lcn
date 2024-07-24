package com.ldw.microservice.dto;

import com.deepexi.data.metadata.domain.eo.SuperEntity;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 *   
 *
 * @description:
 * @author: ludanwen
 * @time: 2021/1/12 17:57
 */
@Data
@ToString
public class SendInformDTO extends SuperEntity implements Serializable {
    private Long tableId;
    private String tableName;
    private Long datasourceId;
    private String datasourceName;
    private String userId;
    private String tenantId;


    public SendInformDTO(String tenantId, String userId, Long tableId){
        this.tenantId=tenantId;
        this.userId=userId;
        this.tableId=tableId;
    }
    public SendInformDTO(String tenantId , Long tableId){
        this.tenantId=tenantId;
        this.tableId=tableId;
    }
}
