package com.ldw.metadata.dbUtil;

import com.ldw.metadata.constant.CommonConstants;
import com.ldw.metadata.vo.JdbcDatasourceVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ClassName:JdbcConnectionUtils <br/>
 * Function: Jdbc连接工具类. <br/>
 * Reason: . <br/>
 * Date: 2020年10月10日 下午7:28:42 <br/>
 *
 * @author WangXf
 * @see
 * @since JDK 1.8
 */
@Slf4j
public class JdbcConnectionUtils {

    /**
     * @return
     * @desc 测试数据源连通性
     */
    @SuppressWarnings("restriction")
    public static final Boolean canConnect(JdbcDatasourceVO jdbcDatasourceVO) {
        Boolean result = false;
        Connection conn = null;
        try {
            Class.forName(jdbcDatasourceVO.getDriverClass());
            String connectUrl = getUrl(jdbcDatasourceVO);
            if (null != jdbcDatasourceVO.getAuthenticationType() && 1 == jdbcDatasourceVO.getAuthenticationType()) {
                System.setProperty("java.security.krb5.conf", jdbcDatasourceVO.getKrb5ConfPath());
                log.info("日志统一打印 ↓ ↓ ↓ ↓ ↓ ↓ krb5ConfPath = {}", jdbcDatasourceVO.getKrb5ConfPath());
                log.info("日志统一打印 ↓ ↓ ↓ ↓ ↓ ↓ System.getProperty = {}", System.getProperty("java.security.krb5.conf"));
                Configuration configuration = new Configuration();
                configuration.set("hadoop.security.authentication", "Kerberos");
                sun.security.krb5.Config.refresh();
                UserGroupInformation.setConfiguration(configuration);
                UserGroupInformation.loginUserFromKeytab(jdbcDatasourceVO.getPrincipal(),
                        jdbcDatasourceVO.getKeytabPath());
                if (connectUrl != null && !connectUrl.contains("principal")) {
                    connectUrl = connectUrl + ";principal=" + jdbcDatasourceVO.getServicePrincipal();
                }
                conn = DriverManager.getConnection(connectUrl);
            } else {
                System.clearProperty("java.security.krb5.conf");
                conn = DriverManager.getConnection(connectUrl, jdbcDatasourceVO.getUsername(),
                        jdbcDatasourceVO.getPassword());
            }
            log.info("tested connection database successfully !");
            result = true;
        } catch (Exception e) {
            log.error("failed to tested connection database !", e);
            result = false;
        } finally {
            if (null != conn) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.error("failed to tested connection database !", e);
                }
            }
        }
        return result;
    }

    /**
     * @param jdbcDatasourceVO
     * @return
     * @desc 获得url
     */
    public static String getUrl(JdbcDatasourceVO jdbcDatasourceVO) {
        StringBuilder stringBuilder = new StringBuilder();
        if (CommonConstants.DataSourceType.IMPALA.equals(jdbcDatasourceVO.getType())) {
            if (jdbcDatasourceVO.getUrl().endsWith("/")) {
                stringBuilder.append(jdbcDatasourceVO.getUrl()).append(jdbcDatasourceVO.getDatabaseName());
            } else {
                stringBuilder.append(jdbcDatasourceVO.getUrl()).append("/").append(jdbcDatasourceVO.getDatabaseName());
            }
            stringBuilder.append(";UseSasl=0;AuthMech=0;UID=").append(jdbcDatasourceVO.getUsername()).append(";PWD=")
                    .append(jdbcDatasourceVO.getPassword()).append(";");
        }

        else if (CommonConstants.DataSourceType.MYSQL.equals(jdbcDatasourceVO.getType())) {
            //mysql 需要链接 information_schema 数据库
            String schemaDB = "information_schema";
            String originUrl = jdbcDatasourceVO.getUrl();
            if (originUrl.endsWith("/")) {
                stringBuilder.append(originUrl).append(schemaDB).append("?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&serverTimezone=UTC");
            } else {
                stringBuilder.append(originUrl).append("/").append(schemaDB).append("?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&serverTimezone=UTC");
            }
            log.info("mysql url:  {}", stringBuilder.toString());
        }
        else if (CommonConstants.DataSourceType.SQLSERVER.equals(jdbcDatasourceVO.getType())) {
            //SQLSERVER
            String originUrl = jdbcDatasourceVO.getUrl();
            String dataBaseName = jdbcDatasourceVO.getDatabaseName();
            stringBuilder.append(originUrl).append(";databasename=").append(dataBaseName);
            log.info("sqlserver url:  {}", stringBuilder.toString());
        }
        else {
            if (jdbcDatasourceVO.getUrl().endsWith("/")) {
                stringBuilder.append(jdbcDatasourceVO.getUrl()).append(jdbcDatasourceVO.getDatabaseName());
            } else {
                stringBuilder.append(jdbcDatasourceVO.getUrl()).append("/").append(jdbcDatasourceVO.getDatabaseName());
            }
        }
        return stringBuilder.toString();

    }


}
