package com.ldw.microservice.dao;

import com.ldw.microservice.entity.AnchorOrderSummary;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AnchorOrderSummaryDao {
    AnchorOrderSummary findById(Integer id);

    int addRecord(AnchorOrderSummary anchorOrderSummary);
}
