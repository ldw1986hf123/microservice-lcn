package com.ldw.microservice.dto;

import com.deepexi.data.metadata.domain.eo.SuperEntity;
import lombok.Data;

import java.io.Serializable;

/**
 *   
 *
 * @description:
 * @author: ludanwen
 * @time: 2021/1/12 17:57
 */
@Data
public class UpdateUserSubscriptionDTO extends SuperEntity implements Serializable {
    private Long tableId;
    private String tableCode;
    private Long projectId;
    private String projectName;
    private Long userId;
    /**
     * 通知方式
     */
    private String messageType;
}
