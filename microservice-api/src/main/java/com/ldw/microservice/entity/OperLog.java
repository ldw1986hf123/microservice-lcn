package com.ldw.microservice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class OperLog {
    private String id;

    private Date createTime;

    private String noted;


}