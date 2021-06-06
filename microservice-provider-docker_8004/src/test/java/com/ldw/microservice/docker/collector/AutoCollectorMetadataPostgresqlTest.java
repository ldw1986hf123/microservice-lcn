package com.ldw.microservice.docker.collector;

import com.ldw.microservice.docker.config.DBConfig;
import com.ldw.microservice.docker.dto.MetadataPartitionDTO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class AutoCollectorMetadataPostgresqlTest extends  BaseJunit {

    @Autowired
    DBConfig dbConfig;

    @Autowired
    AutoCollectorMetadata autoCollectorMetadata;


    @Test
    public void getPartitionMetadata() throws SQLException {
        String url = "jdbc:postgresql://192.168.171.134:55433/postgres";
        Connection connection = dbConfig.getSimpleConnection(url, "postgres", "abc123");


       List<MetadataPartitionDTO> partitionDTOList= autoCollectorMetadata.getPartitionMetadata(connection, Arrays.asList("order_list","s"));
        printResult(partitionDTOList);
    }
}