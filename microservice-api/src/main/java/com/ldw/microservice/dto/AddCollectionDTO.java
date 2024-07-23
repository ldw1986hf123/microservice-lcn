package com.ldw.microservice.dto;

import com.deepexi.data.metadata.domain.eo.SuperEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: ludanwen
 * @time: 2020/11/16 10:14
 */
@Data
public class AddCollectionDTO extends SuperEntity implements Serializable {
    private Long userId;
    private Long tableId;
    private Long projectId;
    private String code;
    private String name;
    private String projectName;
    private Long size;
}
