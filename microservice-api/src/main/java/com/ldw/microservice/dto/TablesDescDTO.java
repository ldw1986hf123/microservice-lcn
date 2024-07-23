package com.ldw.microservice.dto;

import com.deepexi.data.metadata.domain.eo.SuperEntity;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class TablesDescDTO extends SuperEntity implements Serializable {

    private String col_name;
    private String data_type;
    private String comment;
    private Long tableId;
    private String tableCode;

}
