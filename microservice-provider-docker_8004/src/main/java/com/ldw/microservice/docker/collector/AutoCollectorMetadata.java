package com.ldw.microservice.docker.collector;

import com.ldw.microservice.docker.dto.JdbcDatasourceDTO;
import com.ldw.microservice.docker.dto.MetadataPartitionDTO;
import com.ldw.microservice.docker.dto.MetadataTableDTO;

import lombok.NonNull;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
@Validated
public interface AutoCollectorMetadata {

    /**
     * @param datasourceDTO
     * @return 表集合
     * @throws SQLException
     * @desc 自动收集元数据
     */
    List<MetadataTableDTO> collect(JdbcDatasourceDTO datasourceDTO) throws Exception;

    List<MetadataPartitionDTO> getPartitionMetadata(Connection connection, @NotNull List tableNameList) throws SQLException;
}
