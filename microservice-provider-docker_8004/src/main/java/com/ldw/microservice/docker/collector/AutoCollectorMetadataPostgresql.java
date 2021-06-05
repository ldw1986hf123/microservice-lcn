package com.ldw.microservice.docker.collector;

import com.ldw.microservice.docker.config.DBConfig;
import com.ldw.microservice.docker.dto.JdbcDatasourceDTO;
import com.ldw.microservice.docker.dto.MetadataConstraintDTO;
import com.ldw.microservice.docker.dto.MetadataPartitionDTO;
import com.ldw.microservice.docker.dto.MetadataTableDTO;
import com.ldw.microservice.docker.util.DBUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2020. All rights reserved.
 *
 * @description: mysql 元数据采集器
 * @author: wengguifang
 * @time: 2021年3月18日11:50:39
 */
@Slf4j
@Component
public class AutoCollectorMetadataPostgresql implements AutoCollectorMetadata{
    //mysql in语句中参数个数是不限制的。不过对整段sql语句的长度有了限制（max_allowed_packet）。最大4M
    private final String SELECT_ALL_TABLES = " SELECT TABLE_NAME as code ,TABLE_COMMENT name,TABLE_SCHEMA as  databaseName " +
                                            ",create_time as createdTime ,update_time as updatedTime  FROM  information_schema.TABLES   " +
                                            "where TABLE_TYPE='BASE TABLE' and TABLE_SCHEMA= ? ";

    private final String SELECT_ALL_COLUMN_NAME = "  SELECT COLUMN_NAME as code,TABLE_NAME AS tableCode, COLUMN_TYPE as dataType" +
                                            ", CHARACTER_MAXIMUM_LENGTH as length, IS_NULLABLE as isNull, COLUMN_COMMENT  as name  " +
                                            "FROM   information_schema.COLUMNS WHERE TABLE_NAME IN (  {0}  )  and TABLE_SCHEMA= {1}  ";

    private final String SELECT_ALL_INDEX = "  SELECT table_name as tableCode,index_name as name,column_name as columnCode" +
                                            ",index_type as type, index_comment as remark FROM information_schema.statistics " +
                                            "WHERE table_name in (    {0}  )  and TABLE_SCHEMA= {1} ";

    private final String SELECT_ALL_PARTITION = " SELECT  parent.relname AS name, child.relname AS \"subPartitionName\"  " +
                                        "FROM pg_inherits JOIN pg_class parent ON pg_inherits.inhparent = parent.oid " +
                                        " JOIN pg_class child ON pg_inherits.inhrelid = child.oid " +
                                        " JOIN pg_namespace nmsp_parent ON nmsp_parent.oid = parent.relnamespace " +
                                        " JOIN pg_namespace nmsp_child ON nmsp_child.oid = child.relnamespace  " +
                                        "WHERE parent.relname in ( %s ) " +
            "\t ";

    private final String SELECT_ALL_CONSTRAINTS = " SELECT isc.constraint_name AS name,  isc.table_name AS tableCode, isc.CONSTRAINT_type AS type " +
                                                ", ik.COLUMN_NAME as columnCode FROM  information_schema.table_constraints AS isc " +
                                                "INNER JOIN information_schema.KEY_COLUMN_USAGE AS ik ON ( isc.TABLE_NAME = ik.TABLE_NAME " +
                                                "AND isc.CONSTRAINT_NAME = ik.CONSTRAINT_NAME  and isc.table_schema = ik.table_schema)  " +
                                                "WHERE  isc.table_name in (   {0}   )  and isc.TABLE_SCHEMA= {1}  ";


    @Autowired
    private DBConfig dBConfig;

    @Override
    public List<MetadataTableDTO> collect(JdbcDatasourceDTO datasourceDTO) throws Exception {
        return null;
    }

    /**
     * 获取 分区 的元数据
     */
    @Override
    public List<MetadataPartitionDTO> getPartitionMetadata(Connection connection, List tableNameList) throws SQLException {
        List<MetadataPartitionDTO> partitionMetadataVOS = new ArrayList<>();
        PreparedStatement stm = null;
        ResultSet rs = null;
        //查询数据源下，所有的表信息
        try {
            List<String> parameters = new ArrayList<>();
            tableNameList.forEach(empNo -> parameters.add("?"));
            String commaSepParameters = String.join(",", parameters);

            String   selectQuery = String.format(SELECT_ALL_PARTITION, commaSepParameters );
            stm = connection.prepareStatement(selectQuery);
            addParams(stm, tableNameList);

            log.info("sql:   {}", selectQuery);
            rs = stm.executeQuery();
            partitionMetadataVOS = DBUtils.convertList(rs, MetadataPartitionDTO.class);
        }  finally {
            dBConfig.realeaseResources(stm, rs);
        }
        return partitionMetadataVOS;
    }

    /**
     * 获取 约束  的元数据
     * 一个字段可能会有多个元数据
     */
    public List<MetadataConstraintDTO> getConstraintMetadata(Connection connection, List tableNameList, String databaseName) {
        List<MetadataConstraintDTO> constraintMetadataVOS = new ArrayList<>();
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            List<String> parameters = new ArrayList<>();
            tableNameList.forEach(empNo -> parameters.add("?"));
            String commaSepParameters = String.join(",", parameters);

            String selectQuery = MessageFormat.format(SELECT_ALL_CONSTRAINTS, "%s", "'" + databaseName + "'");
            selectQuery = String.format(selectQuery, commaSepParameters);

            stm = connection.prepareStatement(selectQuery);
            addParams(stm, tableNameList);
            rs = stm.executeQuery();
            constraintMetadataVOS = DBUtils.convertList(rs, MetadataConstraintDTO.class);
        } catch (SQLException e) {
            log.error("获取 constraint 的元数据 异常", e);
        } finally {
            dBConfig.realeaseResources(stm, rs);
        }
        return constraintMetadataVOS;
    }

    public static void addParams(PreparedStatement preparedStatement, List<String> params) {
        try {
            for (int i = 0; i < params.size(); i++) {
                String param = params.get(i);
                preparedStatement.setString(i + 1, param);
            }
        } catch (SQLException e) {
            log.error("addParams ", e);
        }
    }

}
