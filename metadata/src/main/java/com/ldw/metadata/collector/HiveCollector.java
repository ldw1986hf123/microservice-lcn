package com.ldw.metadata.collector;

import cn.hutool.core.date.DateUtil;
import com.ldw.metadata.dbUtil.DBConfig;
import com.ldw.metadata.dbUtil.HdfsUtils;
import com.ldw.metadata.vo.JdbcDatasourceVO;
import com.ldw.metadata.vo.PartitionMetadataVO;
import com.ldw.metadata.vo.TableMetadataVO;
import com.ldw.metadata.vo.TablesDesc;
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

    private FileSystem fileSystem;
    @Autowired
    DBConfig dbConfig;
    private
    QueryRunner queryRunner = null;

    @Override
    public List<TableMetadataVO> collect(JdbcDatasourceVO jdbcDatasourceVO) {
        List<TableMetadataVO> tableMetadataVOList=new ArrayList<>();
      try {
          String dbName = jdbcDatasourceVO.getDatabaseName();
          String userName=jdbcDatasourceVO.getUsername();

          queryRunner = new QueryRunner(true);
          Connection connection = dbConfig.getConnection(jdbcDatasourceVO);
          tableMetadataVOList   = collectTableMetadata(connection, dbName);
          fileSystem = HdfsUtils.getFileSystemByKerberos(hdfsAddress);

          for (TableMetadataVO tableMetadataVO : tableMetadataVOList) {
              String tableName = tableMetadataVO.getTableName();
              tableMetadataVO.setTableName(tableName);
              List<TablesDesc> tablesDescList = queryTablesDesc(connection,dbName, tableName);
              String location = TablesDesc.getLocation(tablesDescList);
              Path tablePath = new Path(location);
              if (TablesDesc.isExsitPartition(tablesDescList)) {
                  List<PartitionMetadataVO> partitionMetadataVOList = collectPartition(tableName, tablePath);
                  tableMetadataVO.setPartitionMetadataVOS(partitionMetadataVOList);
              }
          }
      }catch (Exception e){
          e.printStackTrace();
      }
      finally {
          dbConfig.release();
      }
        return tableMetadataVOList;
    }



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


    private List<TablesDesc> queryTablesDesc(Connection connection, String dbName, String tableName) {
        String sql = "desc formatted " + dbName + "." + tableName;
        try {
            ResultSetHandler<List<TablesDesc>> handler = new BeanListHandler(TablesDesc.class);
            List<TablesDesc> tablesDescList = queryRunner.query(connection, sql, handler);
            return tablesDescList;
        } catch (SQLException e) {
            log.error("queryTablesDesc 异常:", e);
        }
        return null;
    }

    private List<TableMetadataVO> collectTableMetadata(Connection connection, String dbName) {
        List<TableMetadataVO> tableMetadataVOList = new ArrayList<>();
        List<String> list = new ArrayList<>();
        String sql = "show tables in " + dbName;
        try {
            list = queryRunner.query(connection, sql, new ColumnListHandler<String>());
        } catch (SQLException e) {
            log.error("show tables in " + dbName + "异常：", e);
        }
        list.forEach(single ->
        {
            TableMetadataVO tableMetadataVO = new TableMetadataVO();
            tableMetadataVO.setTableName(single);
            tableMetadataVO.setDatabaseName(dbName);
            tableMetadataVOList.add(tableMetadataVO);
        });
        return tableMetadataVOList;
    }
}
