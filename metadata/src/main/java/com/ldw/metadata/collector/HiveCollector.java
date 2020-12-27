package com.ldw.metadata.collector;

import cn.hutool.core.date.DateUtil;
import com.ldw.metadata.dbUtil.DBConfig;
import com.ldw.metadata.dbUtil.HdfsUtils;
import com.ldw.metadata.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class HiveCollector implements AutoCollectorMetadata {

    private final String SELECT_ALL_TABLE = "  show tables in   %s";
    private final String SELECT_ALL_COLUMN = "  desc formatted  %s";


    private FileSystem fileSystem;
    @Autowired
    DBConfig dbConfig;
    private
    QueryRunner queryRunner = null;

    /**
     * 计算某个表的分区存储信息
     *
     * @param tablePath
     * @return
     * @throws IOException
     */


    private List<PartitionMetadataVO> collectPartition(String tableName, Path tablePath) {
        List<PartitionMetadataVO> partitionMetadataVOList = new ArrayList();
        try {
            FileStatus[] fileStatuses = fileSystem.listStatus(tablePath);
            for (FileStatus fileStatus : fileStatuses) {
                PartitionMetadataVO partitionMetadataVO = new PartitionMetadataVO();
                partitionMetadataVO.setName(fileStatus.getPath().getName());

                String updatedTime = DateUtil.formatTime(new Date(fileStatus.getModificationTime()));
                partitionMetadataVO.setTableName(tableName);
                partitionMetadataVO.setUpdatedTime(updatedTime);
                partitionMetadataVO.setMaxDataLength(fileSystem.getContentSummary(fileStatus.getPath()).getLength());
                partitionMetadataVOList.add(partitionMetadataVO);
            }
        } catch (IOException e) {
            log.error("获取 FileStatus 失败：", e);
        }
        return partitionMetadataVOList;
    }


    @Override
    public List<ColumnMetadataVO> getColumnMetadata(Connection connection, List<String> tableNameList) {
        List<ColumnMetadataVO> columnMetadataVOS = new ArrayList<>();
        try {
            for (String tableName : tableNameList) {
                String sql = String.format(SELECT_ALL_COLUMN, tableName);
                ResultSetHandler<List<TablesDescVO>> handler = new BeanListHandler(TablesDescVO.class);
                queryRunner = new QueryRunner(true);
                ColumnMetadataVO columnMetadataVO=new ColumnMetadataVO();
                columnMetadataVOS = queryRunner.query(connection, sql, handler);
            }
        } catch (SQLException e) {
            log.error("queryTablesDesc 异常:", e);
        }
        return columnMetadataVOS;
    }

    @Override
    public List<TableMetadataVO> getTableMetadata(Connection connection, String dbName) {

        List<TableMetadataVO> tableList = new ArrayList<>();
        String sql = String.format(SELECT_ALL_TABLE, dbName);
        queryRunner = new QueryRunner(true);
        try {
            tableList = queryRunner.query(connection, sql, new ColumnListHandler<TableMetadataVO>());
        } catch (SQLException e) {
            log.error("查询hive table " + dbName + "异常：", e);
        }
        return tableList;
    }


    @Override
    public List<TableMetadataVO> collect(JdbcDatasourceVO jdbcDatasourceVO) throws Exception {
        List<TableMetadataVO> tableMetadataVOList = new ArrayList<>();
        try {
            String dbName = jdbcDatasourceVO.getDatabaseName();
            String userName = jdbcDatasourceVO.getUsername();

            Connection connection = dbConfig.getConnection(jdbcDatasourceVO);

            //查询表
            tableMetadataVOList = getTableMetadata(connection, dbName);


            fileSystem = HdfsUtils.getFileSystemByKerberos(jdbcDatasourceVO);

            for (TableMetadataVO tableMetadataVO : tableMetadataVOList) {
                String tableName = tableMetadataVO.getTableName();
                tableMetadataVO.setTableName(tableName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbConfig.release();
        }
        return tableMetadataVOList;
    }

}
