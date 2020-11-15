package com.microservice.es.entity;

import lombok.Data;

import java.util.Date;

@Data
public class UserCollection {
    public static final String INDEX = "user_collection";
    private Long id;
    private String docId;
    private Long browserCount;
    private Long tenantId;
    private Long userId;
    private Long projectId;
    private Long tableId;
    private String tableName;
    private Integer version;
    private String createdTime;
    private Date updatedTime;


    private Integer isCollected;

    public static String getINDEX() {
        return INDEX;
    }


}
