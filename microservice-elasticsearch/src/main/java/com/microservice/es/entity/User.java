package com.microservice.es.entity;


import lombok.Data;
import org.elasticsearch.client.ml.job.config.Job;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

/**
 * @author lulu
 * @Date 2019/10/7 14:19
 */
@Document(indexName = "user_info",shards = 2,type = "_doc")
@Data
public class User {
    @Id
    private Integer userId;

    @Field( type = FieldType.Keyword)
    private String name;


    @Field( type=FieldType.Date,format = DateFormat.date_hour_minute_second )
    private String birthDay;

    @Transient
    private List<Job> jobList;
}