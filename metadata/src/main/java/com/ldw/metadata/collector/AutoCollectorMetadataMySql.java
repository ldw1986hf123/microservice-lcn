package com.ldw.metadata.collector;

import cn.hutool.core.date.DateUtil;
import com.ldw.metadata.dbUtil.DBConfig;
import com.ldw.metadata.dbUtil.DBUtils;
import com.ldw.metadata.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2020. All rights reserved.
 *
 * @description: mysql 元数据采集器
 * @author: ludanwen
 * @time: 2020/12/3 11:19
 */
@Slf4j
@Component
public class AutoCollectorMetadataMySql implements AutoCollectorMetadata {
    //todo  用户权限有可能灭有权限查这些表

    private final String SELECT_ALL_TABLES = " SELECT TABLE_NAME as tableName ,TABLE_COMMENT tableComment,TABLE_SCHEMA as  databaseName ,create_time as createdTime ,update_time as updatedTime  FROM  information_schema.TABLES   where TABLE_TYPE='BASE TABLE' and TABLE_SCHEMA= ?";
    private final String SELECT_ALL_COLUMN_NAME = "SELECT COLUMN_NAME as columnName,TABLE_NAME AS tableName, COLUMN_TYPE as columnType,  IS_NULLABLE as isNullAble, COLUMN_KEY as isPrimaryKey, COLUMN_COMMENT  as columnComment  FROM   information_schema.COLUMNS WHERE TABLE_NAME IN ( %s ) ";
    private final String SELECT_ALL_INDEX = "  SELECT table_name as tableName,index_name as name,column_name as columnName,index_type as type, index_comment as comment FROM information_schema.statistics  ";
    private final String SELECT_ALL_PARTITION = "  SELECT table_name tableName, partition_name name,data_length dataLength, max_data_length maxDataLength,create_time as createdTime, update_time as updatedTime  FROM information_schema.PARTITIONS ";

    private final String SELECT_ALL_CONSTRAINTS = " SELECT isc.constraint_name AS name,  isc.table_name AS tableName, isc.CONSTRAINT_type AS type , ik.COLUMN_NAME as columnName FROM  information_schema.table_constraints AS isc LEFT JOIN information_schema.KEY_COLUMN_USAGE AS ik ON ( isc.TABLE_NAME = ik.TABLE_NAME AND isc.CONSTRAINT_NAME = ik.CONSTRAINT_NAME )";


    @Autowired
    private DBConfig dBConfig;


    /**
     * @param datasourceVO
     * @return 表集合
     * @desc 自动收集元数据
     */
    @Override
    public List<TableMetadataVO> collect(JdbcDatasourceVO jdbcDatasourceVO) {
        log.info("{}--------------mysql 开始收集  ", DateUtil.now());
        List<TableMetadataVO> tableMetadataVOs = new ArrayList<>();

        Connection connection = null;
        try {
            //获取数据库链接
            connection = dBConfig.getSimpleConnection(jdbcDatasourceVO);
            if (null == connection) {
                return tableMetadataVOs;
            }


            //只查询当前的库
            String databaseName = jdbcDatasourceVO.getDatabaseName();
            //查询数据源下，所有的表信息
            tableMetadataVOs = getTableMetadata(connection, databaseName);


            // 查询字段信息
            List<String> tableNameList = tableMetadataVOs.stream().map(TableMetadataVO::getTableName).collect(Collectors.toList());

            List<ColumnMetadataVO> columnVOs = getColumnMetadata(connection, tableNameList);
            Map<String, List<ColumnMetadataVO>> tableNameToColumns = columnVOs.stream().collect(Collectors.groupingBy(ColumnMetadataVO::getTableName));

            //查询索引
            List<IndexMetadataVO> indexMetadataVOS = getIndexMetadata(connection);
            Map<String, List<IndexMetadataVO>> tableNameToIndex = indexMetadataVOS.stream().collect(Collectors.groupingBy(IndexMetadataVO::getTableName));


            //查询分区
            List<PartitionMetadataVO> partitionMetadataVOS = getPartitionMetadata(connection);
            Map<String, List<PartitionMetadataVO>> tableNameToPartition = partitionMetadataVOS.stream().collect(Collectors.groupingBy(PartitionMetadataVO::getTableName));


            //查询 约束
            List<ConstraintMetadataVO> constraintMetadataVOS = getConstraintMetadata(connection);
            Map<String, List<ConstraintMetadataVO>> columnNameToConstraint = constraintMetadataVOS.stream().collect(Collectors.groupingBy(ConstraintMetadataVO::getColumnName));

            //为字段设置约束
            columnVOs.forEach(columnMetadataVO -> {
                String columnName = columnMetadataVO.getColumnName();
                String tableName = columnMetadataVO.getTableName();
                List<ConstraintMetadataVO> singleConstraints = columnNameToConstraint.get(columnName);
                if (CollectionUtils.isNotEmpty(singleConstraints)) {
                    columnMetadataVO.setConstraintsStr(transformConstraintName(singleConstraints, tableName));
                }
            });

            for (TableMetadataVO tableMetadataVO : tableMetadataVOs) {
                String tableName = tableMetadataVO.getTableName();
                List<ColumnMetadataVO> singleColumns = tableNameToColumns.get(tableName);
                List<IndexMetadataVO> singleIndexes = tableNameToIndex.get(tableName);
                List<PartitionMetadataVO> singlePartitions = tableNameToPartition.get(tableName);

                tableMetadataVO.setColumnVOs(singleColumns);
                tableMetadataVO.setIndexMetadataVOS(singleIndexes);
                tableMetadataVO.setPartitionMetadataVOS(singlePartitions);

                tableMetadataVO.setDataSourceId(jdbcDatasourceVO.getId());
                tableMetadataVO.setProjectId(jdbcDatasourceVO.getProjectId());
            }
        } catch (Exception e) {
            log.error("收集元数据异常", e);
        } finally {
            dBConfig.release();
        }
        log.info("{}--------------mysql 采集完成  ", DateUtil.now());
        return tableMetadataVOs;
    }

    /**
     * 先获取表的元数据
     */
    @Override
    public List<TableMetadataVO> getTableMetadata(Connection connection, String databaseName) {
        List<TableMetadataVO> tableMetadataVOs = new ArrayList<>();
        PreparedStatement stm = null;
        ResultSet rs = null;

        //查询数据源下，所有的表信息
        try {
            stm = connection.prepareStatement(SELECT_ALL_TABLES);
            stm.setString(1, databaseName);
            rs = stm.executeQuery();
            tableMetadataVOs = DBUtils.convertList(rs, TableMetadataVO.class);
        } catch (SQLException e) {
            log.error("先获取表的元数据 异常", e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } finally {
            dBConfig.realeaseResources(stm, rs);
        }
        return tableMetadataVOs;
    }

    @Override
    public List<PartitionMetadataVO> collectPartition(String tableName, Path tablePath) {
        return null;
    }

    @Override
    public List<PartitionMetadataVO> collectPartition(Connection connection, String dbName, String tableName, JdbcDatasourceVO jdbcDatasourceVO) {
        return null;
    }



    /**
     * 获取字段的元数据
     */
    @Override
    public List<ColumnMetadataVO> getColumnMetadata(Connection connection, List tableNameList) {
        List<ColumnMetadataVO> columnMetadataVOS = new ArrayList<>();
        PreparedStatement stm = null;
        ResultSet rs = null;
        //查询数据源下，所有的表信息
        try {
            List<String> parameters = new ArrayList<>();
            tableNameList.forEach(empNo -> parameters.add("?"));   //Use forEach to add required no. of '?'
            String commaSepParameters = String.join(",", parameters); //Use String to join '?' with ','
            String selectQuery =String.format(SELECT_ALL_COLUMN_NAME,commaSepParameters);
            stm = connection.prepareStatement(selectQuery);
            addParams(stm,tableNameList);
            rs = stm.executeQuery();
            columnMetadataVOS = DBUtils.convertList(rs, ColumnMetadataVO.class);
        } catch (SQLException e) {
            log.error("先获取字段的元数据 异常", e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } finally {
            dBConfig.realeaseResources(stm, rs);
        }
        return columnMetadataVOS;
    }


    /**
     * 获取索引的元数据
     */
    private List<IndexMetadataVO> getIndexMetadata(Connection connection) {
        List<IndexMetadataVO> indexMetadataVOS = new ArrayList<>();
        PreparedStatement stm = null;
        ResultSet rs = null;
        //查询数据源下，所有的表信息
        try {
            stm = connection.prepareStatement(SELECT_ALL_INDEX);
            rs = stm.executeQuery();
            indexMetadataVOS = DBUtils.convertList(rs, IndexMetadataVO.class);
        } catch (SQLException e) {
            log.error("先获取索引的元数据 异常", e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } finally {
            dBConfig.realeaseResources(stm, rs);
        }
        return indexMetadataVOS;
    }

    /**
     * 获取 分区 的元数据
     */
    private List<PartitionMetadataVO> getPartitionMetadata(Connection connection) {
        List<PartitionMetadataVO> partitionMetadataVOS = new ArrayList<>();
        PreparedStatement stm = null;
        ResultSet rs = null;
        //查询数据源下，所有的表信息
        try {
            stm = connection.prepareStatement(SELECT_ALL_PARTITION);
            rs = stm.executeQuery();
            partitionMetadataVOS = DBUtils.convertList(rs, PartitionMetadataVO.class);
        } catch (SQLException e) {
            log.error("先获取 分区 的元数据 异常", e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } finally {
            dBConfig.realeaseResources(stm, rs);
        }
        return partitionMetadataVOS;
    }

    /**
     * 获取 约束  的元数据
     * 一个字段可能会有多个元数据
     */
    private List<ConstraintMetadataVO> getConstraintMetadata(Connection connection) {
        List<ConstraintMetadataVO> constraintMetadataVOS = new ArrayList<>();
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            stm = connection.prepareStatement(SELECT_ALL_CONSTRAINTS);
            rs = stm.executeQuery();
            constraintMetadataVOS = DBUtils.convertList(rs, ConstraintMetadataVO.class);
        } catch (SQLException e) {
            log.error("获取 constraint 的元数据 异常", e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } finally {
            dBConfig.realeaseResources(stm, rs);
        }
        return constraintMetadataVOS;
    }

    private String transformConstraintName(List<ConstraintMetadataVO> constraintMetadataVOS, String colunmtableName) {
        StringBuffer result = new StringBuffer();
        constraintMetadataVOS.forEach(constraintMetadataVO -> {
            String tableName = constraintMetadataVO.getTableName();
            String name = MysqlConstraintTypeEnum.getEnumType(constraintMetadataVO.getType());
            if (colunmtableName.equals(tableName)) {
                result.append(name).append(" ");
            }
        });
        return result.toString();
    }


    public static void addParams(PreparedStatement preparedStatement, List<String> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            String param = params.get(i);
            preparedStatement.setString(i + 1, param);
        }
    }
    /**
     *  UNIQUE, PRIMARY KEY, FOREIGN KEY,
     */
    /**
     * mysql 约束
     * UNIQUE = CHECK 约束
     * DEFAULT =  DEFAULT 约束
     * FOREIGN KEY 约束
     * PRIMARY KEY 约束（类型是 K）
     */
    private enum MysqlConstraintTypeEnum {
        UNIQUE("UNIQUE", "唯一约束"),
        CHECK("DEFAULT", "默认约束"),
        PRIMARY_KEY("PRIMARY KEY", "主键约束"),
        FOREIGN_KEY("FOREIGN KEY", "外键约束");
        private String type;

        private String name;

        MysqlConstraintTypeEnum(String type, String name) {
            this.type = type;
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public static String getEnumType(String type) {
            MysqlConstraintTypeEnum[] alarmGrades = MysqlConstraintTypeEnum.values();
            for (int i = 0; i < alarmGrades.length; i++) {
                if (alarmGrades[i].getType().equals(type)) {
                    return alarmGrades[i].name;
                }
            }
            return "";
        }

    }

}
