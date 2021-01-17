package com.ldw.metadata.collector;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.thrift.TException;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class HiveMetaStoreClientTest extends BaseTest {
    @Override
    public void init() {

    }


    //HiveMetaStore的客户端
    private HiveMetaStoreClient hiveMetaStoreClient;

    /**
     * 功能描述:
     * 设置HiveJDBC的数据源
     */
    @Test
    public void setHiveMetaStoreConf() {
        HiveConf hiveConf = new HiveConf();
        hiveConf.set("hive.metastore.uris", "thrift://192.168.171.134:9083");
        try {
            //设置hiveMetaStore服务的地址
            this.hiveMetaStoreClient = new HiveMetaStoreClient(hiveConf);
            //当前版本2.3.4与集群3.0版本不兼容，加入此设置
//            this.hiveMetaStoreClient.setMetaConf("hive.metastore.client.capability.check", "false");

            // 由数据库的名称获取数据库的对象(一些基本信息)
            Database database = this.hiveMetaStoreClient.getDatabase("default");
//            printResult(database);
            List<String> tablesList = this.hiveMetaStoreClient.getAllTables("default");
//            printResult(tablesList);
            Table table = this.hiveMetaStoreClient.getTable("default", "test_day_partitions_3");
            table.getPartitionKeys().forEach(partitionKey -> {
                System.out.println(partitionKey);
            });
            System.out.println( table.getSd().getLocation());
            String hdfsAddress=table.getSd().getLocation();

            FileSystem fileSystem = null;
            Path tablePath=new Path("/ldw/hive_remote/warehouse/test_day_partitions_3");
            FileStatus[] fileStatuses = fileSystem.listStatus(tablePath);
            for (FileStatus fileStatus : fileStatuses) {
                System.out.println(fileStatus.getPath().getName());
                System.out.println(fileSystem.getContentSummary(fileStatus.getPath()).getLength());
            }

// 关闭当前连接
            this.hiveMetaStoreClient.close();
        } catch (MetaException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
