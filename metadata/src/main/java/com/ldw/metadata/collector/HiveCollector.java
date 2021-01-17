package com.ldw.metadata.collector;

import cn.hutool.core.util.StrUtil;
import com.ldw.metadata.dbUtil.DBConfig;
import com.ldw.metadata.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeys;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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
    private
    Connection connection;
    QueryRunner query = null;

    /**
     * 计算某个表的分区存储信息
     *
     * @return
     * @throws IOException
     */

    @Override
    public List<TableMetadataVO> collect(JdbcDatasourceVO jdbcDatasourceVO) throws Exception {
        List<TableMetadataVO> tableMetadataVOList = new ArrayList<>();
        try {
            String dbName = jdbcDatasourceVO.getDatabaseName();
            String userName = jdbcDatasourceVO.getUsername();

              connection = dbConfig.getConnection(jdbcDatasourceVO);
            //查询表
            tableMetadataVOList = getTableMetadata(connection, dbName);
            for (TableMetadataVO tableMetadataVO : tableMetadataVOList) {

                collectPartition(connection,dbName,tableMetadataVO.getTableName(),jdbcDatasourceVO);
                System.out.println(tableMetadataVO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbConfig.release();
        }
        return null;
    }

    @Override
    public List<PartitionMetadataVO> collectPartition(Connection connection, String dbName, String tableName,JdbcDatasourceVO jdbcDatasourceVO) {
        String sql = "desc formatted " + dbName + "." + tableName;
        String hdfsAddress="";
        List<PartitionMetadataVO> partitionMetadataVOList=new ArrayList<>();
        try {
            ResultSetHandler<List<TablesDescVO>> handler = new BeanListHandler(TablesDescVO.class);
            List<TablesDescVO> tablesDescList = query(connection,sql, handler);
            //为了获取location,通常是这样的一个字符串hdfs://bd2:8020/user/hive/warehouse/test_data_quality_01.db/cdw_area_day_partition
            String location="";
            for (TablesDescVO tablesDesc : tablesDescList) {
                if (tablesDesc.getCol_name().contains("Location:")) {
                    location= tablesDesc.getData_type();
                }
            }
            hdfsAddress=getIP(location);
            //获取hive表的文件路径
            String path= StrUtil.subAfter(location,hdfsAddress,false);
            Path tablePath=new Path(path);
            FileSystem fileSystem=getFileSystem(hdfsAddress,jdbcDatasourceVO);
            FileStatus[] fileStatuses = fileSystem.listStatus(tablePath);
            for (FileStatus fileStatus : fileStatuses) {
                PartitionMetadataVO partitionMetadataVO =new PartitionMetadataVO();
                partitionMetadataVO.setName(fileStatus.getPath().getName());
                partitionMetadataVO.setMaxDataLength(fileSystem.getContentSummary(fileStatus.getPath()).getLength());
                partitionMetadataVOList.add(partitionMetadataVO);

            }
        }  catch (IOException e) {
            log.error("queryTablesDesc 异常:", e);
        }
        return partitionMetadataVOList;
    }


    @Override
    public List<ColumnMetadataVO> getColumnMetadata(Connection connection, List<String> tableNameList) {
        List<ColumnMetadataVO> columnMetadataVOS = new ArrayList<>();
        try {
            for (String tableName : tableNameList) {
                String sql = String.format(SELECT_ALL_COLUMN, tableName);
                ResultSetHandler<List<ColumnMetadataVO>> handler = new BeanListHandler(TablesDescVO.class);
                queryRunner = new QueryRunner(true);
                ColumnMetadataVO columnMetadataVO = new ColumnMetadataVO();
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
            List<String> tableNames = queryRunner.query(connection, sql, new ColumnListHandler<>());
            for (String tableName : tableNames) {
                TableMetadataVO tableMetadataVO = new TableMetadataVO();
                tableMetadataVO.setDatabaseName(dbName);
                tableMetadataVO.setTableName(tableName);
                tableList.add(tableMetadataVO);
            }
        } catch (SQLException e) {
            log.error("查询hive table " + dbName + "异常：", e);
        }
        return tableList;
    }

    @Override
    public List<PartitionMetadataVO> collectPartition(String tableName, Path tablePath) {
        return null;
    }

    public <T> T query(Connection connection,String sql, ResultSetHandler<T> rsh) {
        T t=null;
        query = new QueryRunner(true);
        try {
            t= query.query(connection, sql, rsh);
        } catch (SQLException e) {
            log.error("hive query exception",e);
        }
        return t;
    }

    /**
     * 获取 hdfs 客户端，开启kerberos的
     *
     * @return hdfs 客户端
     * @throws Exception
     */
    private   FileSystem getFileSystem(String hdfsAddress,JdbcDatasourceVO jdbcDatasourceVO) {
        FileSystem fileSystem = null;
        Configuration conf = new Configuration();
        if (null!=jdbcDatasourceVO.getAuthenticationType()&&1==jdbcDatasourceVO.getAuthenticationType()) {
            conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
            conf.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER");
            System.setProperty("java.security.krb5.conf", jdbcDatasourceVO.getKrb5ConfPath());
            conf.setBoolean(CommonConfigurationKeys.IPC_CLIENT_FALLBACK_TO_SIMPLE_AUTH_ALLOWED_KEY, true);
            conf.setBoolean("hadoop.security.authorization", true);
            conf.set("hadoop.security.authentication", "kerberos");
            conf.set("dfs.namenode.kerberos.principal.pattern", "*");
            try {
                UserGroupInformation.setConfiguration(conf);
                UserGroupInformation.loginUserFromKeytab(jdbcDatasourceVO.getPrincipal(), jdbcDatasourceVO.getKeytabPath());
            } catch (IOException ioException) {
                log.error("loginUserFromKeytab error", ioException);
            } catch (IllegalArgumentException e) {
                log.error("loginUserFromKeytab error", hdfsAddress.toString());
            }

        } else {
            // 如果其他数据源开启过kerberos，会对没开启kerberos的hdfs产生影响
            UserGroupInformation.reset();
            System.clearProperty("java.security.krb5.conf");
        }

        try {
            conf.set("fs.defaultFS", hdfsAddress);
            fileSystem = FileSystem.get(conf);
//			fileSystem = FileSystem.get(new URI(hdfsAddress.getDefaultFS()), conf, hdfsAddress.getUsername());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return fileSystem;
    }

    private String getIP(String location) {
        URI uri= null;
        try {
            uri = new URI(location);
        } catch (URISyntaxException uriSyntaxException) {
            uriSyntaxException.printStackTrace();
        }
        URI effectiveURI = null;
        try {
            effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), null, null, null);
        } catch (Throwable var4) {
            effectiveURI = null;
        }
        return effectiveURI.toString();
    }

}
