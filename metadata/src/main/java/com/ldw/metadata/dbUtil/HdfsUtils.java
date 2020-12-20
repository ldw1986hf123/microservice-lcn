package com.ldw.metadata.dbUtil;

import com.ldw.metadata.vo.JdbcDatasourceVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeys;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class HdfsUtils {
        private HdfsUtils() {
        }
        /**
         * 获取 hdfs 文件系统的客户端
         *
         * @param hdfsAddress
         * @return
         * @throws Exception
         */
/*
        public static FileSystem getFileSystem(HdfsAddress hdfsAddress) throws Exception {
            if (hdfsAddress.getDefaultFS() == null && hdfsAddress.getUsername() == null) {
                return null;
            }

            Configuration conf = new Configuration();
            //不用设这两个值
//        conf.addResource(new Path("/etc/hadoop/conf/core-site.xml"));
//        conf.addResource(new Path("/etc/hadoop/conf/hdfs-site.xml"));


            // hadoop非HA
            if (hdfsAddress.getNameservices() == null) {
                try {
                    return FileSystem.get(new URI(hdfsAddress.getDefaultFS()), conf, hdfsAddress.getUsername());
                } catch (Exception e) {
                    log.error("获取hdfs客户端失败：", e);
                }
            }

            // hadoop的HA
            conf.set("fs.defaultFS", hdfsAddress.getDefaultFS());
            conf.set("dfs.nameservices", hdfsAddress.getNameservices());
            conf.set("dfs.ha.namenodes." + hdfsAddress.getNameservices(), hdfsAddress.getNamenodes());
            Map<String, String> rpcAddress = hdfsAddress.getRpcAddress();
            for (String namenode : rpcAddress.keySet()) {
                conf.set("dfs.namenode.rpc-address." + hdfsAddress.getNameservices() + "." + namenode, rpcAddress.get(namenode));
            }
            conf.set("dfs.client.failover.proxy.provider." + hdfsAddress.getNameservices(), "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");

            // 这个解决hdfs问题
            conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
            // 这个解决本地file问题
            conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());

            return FileSystem.get(new URI(hdfsAddress.getDefaultFS()), conf, hdfsAddress.getUsername());
        }
*/


        /**
         * 获取 hdfs 客户端，开启kerberos的
         *
         * @param hdfsAddress hdfs配置
         * @return hdfs 客户端
         * @throws Exception
         */
        public static FileSystem getFileSystemByKerberos(JdbcDatasourceVO jdbcDatasourceVO) {
            FileSystem fileSystem = null;
            Configuration conf = new Configuration();
            // HA
         /*   if (StringUtils.isNotBlank(hdfsAddress.getNameservices())) {
                // hadoop的HA
                conf.set("fs.defaultFS", hdfsAddress.getDefaultFS());
                conf.set("dfs.nameservices", hdfsAddress.getNameservices());
                conf.set("dfs.ha.namenodes." + hdfsAddress.getNameservices(), hdfsAddress.getNamenodes());
                Map<String, String> rpcAddress = hdfsAddress.getRpcAddress();
                for (String namenode : rpcAddress.keySet()) {
                    conf.set("dfs.namenode.rpc-address." + hdfsAddress.getNameservices() + "." + namenode, rpcAddress.get(namenode));
                }
                conf.set("dfs.client.failover.proxy.provider." + hdfsAddress.getNameservices(), "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");

                // 这个解决hdfs问题
                conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
                // 这个解决本地file问题
                conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
            }*/

            if ( 1 == jdbcDatasourceVO.getAuthenticationType()) {
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
                    log.error("loginUserFromKeytab error",jdbcDatasourceVO);
                }

            } else {
                // 如果其他数据源开启过kerberos，会对没开启kerberos的hdfs产生影响
                UserGroupInformation.reset();
                System.clearProperty("java.security.krb5.conf");
            }

            try {
                fileSystem = FileSystem.get(new URI(hdfsAddress.getDefaultFS()), conf, hdfsAddress.getUsername());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (InterruptedException e) {
                log.error("get FileSystem error", e);
            } catch (URISyntaxException uriSyntaxException) {
                log.error("get FileSystem error", uriSyntaxException);
            }
            return fileSystem;
        }
}
