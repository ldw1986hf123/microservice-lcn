package com.ldw.microservice.docker.collector;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ldw.microservice.docker.config.DBConfig;
import com.ldw.microservice.docker.dto.*;
import com.ldw.microservice.docker.enums.CommonConstants;
import com.ldw.microservice.docker.enums.ConstraintTypeEnum;
import com.ldw.microservice.docker.util.DBUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
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
public class AutoCollectorMetadataPostgresql implements AutoCollectorMetadata {
    //mysql in语句中参数个数是不限制的。不过对整段sql语句的长度有了限制（max_allowed_packet）。最大4M
 /*   private final String SELECT_ALL_TABLES = " SELECT TABLE_NAME as code ,TABLE_COMMENT name,TABLE_SCHEMA as  databaseName " +
            ",create_time as createdTime ,update_time as updatedTime  FROM  information_schema.TABLES   " +
            "where TABLE_TYPE='BASE TABLE' and TABLE_SCHEMA= ? ";

    private final String SELECT_ALL_COLUMN_NAME = "  SELECT COLUMN_NAME as code,TABLE_NAME AS tableCode, COLUMN_TYPE as dataType" +
            ", CHARACTER_MAXIMUM_LENGTH as length, IS_NULLABLE as isNull, COLUMN_COMMENT  as name  " +
            "FROM   information_schema.COLUMNS WHERE TABLE_NAME IN (  {0}  )  and TABLE_SCHEMA= {1}  ";

    private final String SELECT_ALL_INDEX = "  SELECT table_name as tableCode,index_name as name,column_name as columnCode" +
            ",index_type as type, index_comment as remark FROM information_schema.statistics " +
            "WHERE table_name in (    {0}  )  and TABLE_SCHEMA= {1} ";*/

    private final String SELECT_ALL_PARTITION = " SELECT  parent.relname AS \"tableCode\", child.relname AS \"name\" , " +
            "  pg_relation_size(child.oid) AS size,  child.relhassubclass as \"hasChild\"  " +
            " FROM pg_inherits JOIN pg_class parent ON pg_inherits.inhparent = parent.oid " +
            " JOIN pg_class child ON pg_inherits.inhrelid = child.oid " +
            " JOIN pg_namespace nmsp_parent ON nmsp_parent.oid = parent.relnamespace " +
            " JOIN pg_namespace nmsp_child ON nmsp_child.oid = child.relnamespace  " +
            "WHERE parent.relname in ( %s ) ";

    private final String SELECT_ALL_CONSTRAINTS = "SELECT tc.CONSTRAINT_NAME as name, tc.TABLE_NAME as \"tablecode\", kcu.COLUMN_NAME as \"columnCode\", constraint_type as type  " +
            "FROM information_schema.table_constraints AS tc JOIN information_schema.key_column_usage AS kcu ON tc.CONSTRAINT_NAME = kcu.CONSTRAINT_NAME " +
            " JOIN information_schema.constraint_column_usage AS ccu ON ccu.CONSTRAINT_NAME = tc.CONSTRAINT_NAME" +
            "WHERE  tc.TABLE_NAME IN ( %s  );";


    @Autowired
    private DBConfig dBConfig;

    @Override
    public List<MetadataTableDTO> collect(JdbcDatasourceDTO datasourceDTO) throws Exception {
        List<MetadataTableDTO> metadataTableDTOS = new ArrayList<>();
        List<String> tableCodeList = Arrays.asList("order_list", "s");

        String url = "jdbc:postgresql://192.168.171.134:55433/postgres";
        Connection connection = dBConfig.getSimpleConnection(url, "postgres", "abc123");

        //查询分区
        List<MetadataPartitionDTO> partitionMetadataVOS = getPartitionMetadata(connection, tableCodeList);
        Map<String, List<MetadataPartitionDTO>> tableNameToPartition = partitionMetadataVOS.stream().collect(Collectors.groupingBy(MetadataPartitionDTO::getTableCode));

        //查询 约束
        List<MetadataConstraintDTO> constraintMetadataVOS = getConstraintMetadata(connection, tableCodeList);
        Map<String, List<MetadataConstraintDTO>> tableNameToConstraint = constraintMetadataVOS.stream().collect(Collectors.groupingBy(MetadataConstraintDTO::getTableCode));


        for (String tableCode : tableCodeList) {
            MetadataTableDTO metadataTableDTO = new MetadataTableDTO();
            /*取出一張表下的所有約束  ，然每個字段设置约束*/
            List<MetadataConstraintDTO> tableNameToCostraints = tableNameToConstraint.get(tableCode);
            if (CollectionUtils.isNotEmpty(tableNameToCostraints)) {
            /*    for (MetadataColumnDTO columnMetadataVO : singleColumns) {
                    String columnCode = columnMetadataVO.getCode();
                    Map<String, List<MetadataConstraintDTO>> columnNameToConstraints = tableNameToCostraints.stream().collect(Collectors.groupingBy(MetadataConstraintDTO::getColumnCode));
                    if (CollectionUtils.isNotEmpty(columnNameToConstraints.get(columnCode))) {
                        setColumnProperties(columnNameToConstraints.get(columnCode), columnMetadataVO);
                    }
                }*/
            }

            List<MetadataPartitionDTO> singlePartitions = tableNameToPartition.get(tableCode);

            metadataTableDTO.setPartitionDTOS(singlePartitions);
            metadataTableDTO.setOwner("sys");
        }
        return metadataTableDTOS;
    }

    /**
     * 获取 分区 的元数据
     */
    @Override
    public List<MetadataPartitionDTO> getPartitionMetadata(Connection connection, List tableNameList) throws
            SQLException {
        List<MetadataPartitionDTO> partitionMetadataVOS = new ArrayList<>();
        PreparedStatement stm = null;
        ResultSet rs = null;
        //查询数据源下，所有的表信息
        try {
            List<String> parameters = new ArrayList<>();
            tableNameList.forEach(empNo -> parameters.add("?"));
            String commaSepParameters = String.join(",", parameters);

            String selectQuery = String.format(SELECT_ALL_PARTITION, commaSepParameters);
            stm = connection.prepareStatement(selectQuery);
            addParams(stm, tableNameList);

            log.info("sql:   {}", selectQuery);
            rs = stm.executeQuery();
            partitionMetadataVOS = DBUtils.convertList(rs, MetadataPartitionDTO.class);
        } finally {
            dBConfig.realeaseResources(stm, rs);
        }

        List<MetadataPartitionDTO> firstPartitionListHasSub = new ArrayList();
        List<MetadataPartitionDTO> secondPartitionList = new ArrayList();
        //有子分区的分区
        firstPartitionListHasSub = partitionMetadataVOS.stream().filter(partitionMetadataVO -> partitionMetadataVO.getHasChild().equals("true")).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(firstPartitionListHasSub)) {
            List<String> firstPartitionNameList = firstPartitionListHasSub.stream().map(MetadataPartitionDTO::getName).collect(Collectors.toList());
            Map<String, MetadataPartitionDTO> partitonnameToPartiton = firstPartitionListHasSub.stream().collect(Collectors.toMap(MetadataPartitionDTO::getName, a -> a, (k1, k2) -> k1));

            secondPartitionList = getPartitionMetadata(connection, firstPartitionNameList);


            for (MetadataPartitionDTO secondPartition : secondPartitionList) {
                String secondPartitionName = secondPartition.getName();
                String parentPartitonName = secondPartition.getTableCode();
                Long size = secondPartition.getSize();
                secondPartition.setName(partitonnameToPartiton.get(parentPartitonName).getName() + "/" + secondPartitionName);
            }
            partitionMetadataVOS.addAll(secondPartitionList);
        }


        return partitionMetadataVOS;
    }

    /**
     * 获取 约束  的元数据
     * constraint_type有四种：UNIQUE、PRIMARY KEY、CHECK、FOREIGN KEY
     */
    public List<MetadataConstraintDTO> getConstraintMetadata(Connection connection, List tableNameList) {
        List<MetadataConstraintDTO> constraintMetadataVOS = new ArrayList<>();
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            List<String> parameters = new ArrayList<>();
            tableNameList.forEach(empNo -> parameters.add("?"));
            String commaSepParameters = String.join(",", parameters);

            String selectQuery = String.format(SELECT_ALL_CONSTRAINTS, commaSepParameters);

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


    /**
     * 为字段设置各种信息  索引，主外键约束等
     *
     * @return
     */
     void setColumnProperties(List<MetadataConstraintDTO> metadataConstraintDTOS, MetadataColumnDTO metadataColumnDTO) {
        StringBuffer result = new StringBuffer();
        StringBuffer resultStr = new StringBuffer();

        for (MetadataConstraintDTO metadataConstraintDTO : metadataConstraintDTOS) {
            String tableName = metadataConstraintDTO.getTableCode();
            String columnTableName = metadataColumnDTO.getTableCode();
            String constraintType = metadataConstraintDTO.getType();

            ConstraintTypeEnum constraintTypeEnum = ConstraintTypeEnum.getEnumByType(constraintType);
            if (columnTableName.equals(tableName) && null != constraintTypeEnum) {
                result.append(constraintTypeEnum.getCode()).append(",");
                resultStr.append(constraintTypeEnum.getName()).append(" ");
                if (ConstraintTypeEnum.PRIMARY_KEY.getType().equals(constraintType)) {
                    metadataColumnDTO.setIsPrimaryKey(CommonConstants.IsPrimaryKey.YES);
                } else if (ConstraintTypeEnum.FOREIGN_KEY.getType().equals(constraintType)) {
                    metadataColumnDTO.setIsForeignKey(CommonConstants.IsForeignKey.YES);
                }
            }

        }
        metadataColumnDTO.setColumnConstraint(StringUtils.isBlank(result) ? result.toString() : result.deleteCharAt(result.length() - 1).toString());
        metadataColumnDTO.setConstraintsStr(resultStr.toString());
    }
}
