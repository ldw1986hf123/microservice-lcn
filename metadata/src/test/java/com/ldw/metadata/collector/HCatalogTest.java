//package com.ldw.metadata.collector;
//
//
//import org.apache.hadoop.hive.conf.HiveConf;
//import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
//import org.apache.hadoop.io.IntWritable;
//import org.apache.hive.hcatalog.api.HCatClient;
//import org.apache.hive.hcatalog.api.HCatCreateTableDesc;
//import org.apache.hive.hcatalog.api.HCatTable;
//import org.apache.hive.hcatalog.cli.SemanticAnalysis.HCatSemanticAnalyzer;
//import org.apache.hive.hcatalog.common.HCatException;
//import org.apache.hive.hcatalog.data.DefaultHCatRecord;
//import org.apache.hive.hcatalog.data.HCatRecord;
//import org.apache.hive.hcatalog.data.schema.HCatFieldSchema;
//import org.apache.hive.hcatalog.data.schema.HCatSchema;
//import org.junit.Before;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//
//public class HCatalogTest extends BaseTest {
//    @Autowired
//    HiveCollector hiveCollector;
//
//    String tableName = "  student  ";
//    private static Statement statement;
//    private static ResultSet resultSet;
//
//    private static String JDBC_DRIVER = "org.apache.hive.jdbc.HiveDriver";
//    private static String CONNECTION_URL = "jdbc:hive2://192.168.171.134:10000/";
//
//    private final static String localFilePath = "/home/hadoop/test/hive/test.txt";
//    private final static String hdfsFilePath = "hdfs://192.168.171.134:9000/user/hadoop/";
//
//    HCatClient client = null;
//
//    @Before
//    public void init() {
//        HiveConf hcatConf = new HiveConf();
//
//        hcatConf.setVar(HiveConf.ConfVars.METASTOREURIS, "thrift://192.168.171.134:9083");
//        hcatConf.set("hive.metastore.local", "false");
//        hcatConf.setIntVar(HiveConf.ConfVars.METASTORETHRIFTCONNECTIONRETRIES, 10);
//        hcatConf.set(HiveConf.ConfVars.HIVE_SUPPORT_CONCURRENCY.varname, "true");
//        hcatConf.set(HiveConf.ConfVars.SEMANTIC_ANALYZER_HOOK.varname, HCatSemanticAnalyzer.class.getName());
//        hcatConf.set(HiveConf.ConfVars.PREEXECHOOKS.varname, "");
//        hcatConf.set(HiveConf.ConfVars.POSTEXECHOOKS.varname, "");
//
//        hcatConf.setTimeVar(HiveConf.ConfVars.METASTORE_CLIENT_SOCKET_TIMEOUT, 1000, TimeUnit.MILLISECONDS);
//
//        HCatTable hTable = null;
//
//        try {
//            client = HCatClient.create(hcatConf);
//        } catch (HCatException hCatEx) {
//            hCatEx.printStackTrace();
//        }
//    }
//
//
//    @Test
//    public void connectHiveWithKerberos() throws SQLException {
//        //登录Kerberos账号
//     /*   System.setProperty("java.security.krb5.conf", "/Volumes/Transcend/keytab/krb5.conf");
//        Configuration configuration = new Configuration();
//        configuration.set("hadoop.security.authentication" , "Kerberos" );
//        UserGroupInformation. setConfiguration(configuration);
//        UserGroupInformation.loginUserFromKeytab("fayson@CLOUDERA.COM", "/Volumes/Transcend/keytab/fayson.keytab");
//
//        Connection connection = null;
//        ResultSet rs = null;
//        PreparedStatement ps = null;
//        try {
//            connection = DriverManager.getConnection(CONNECTION_URL);
//            ps = connection.prepareStatement("select * from test_table");
//            rs = ps.executeQuery();
//            while (rs.next()) {
//                System.out.println(rs.getInt(1));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            JDBCUtils.disconnect(connection, rs, ps);
//        }*/
//    }
//
//
//    @Test
//    public void createTable() throws SQLException, HCatException {
//        HCatTable test2 = new HCatTable("default", "hcatalog_test_table");
//
//        HCatFieldSchema prefixFieldSchema = new HCatFieldSchema("prefix",
//                TypeInfoFactory.stringTypeInfo, "注释1");
//        HCatFieldSchema idFieldSchema = new HCatFieldSchema("id",
//                TypeInfoFactory.stringTypeInfo, "注释2");
//        HCatFieldSchema xxFieldSchema = new HCatFieldSchema("xx",
//                TypeInfoFactory.intTypeInfo, "注释3");
//
//        test2.cols(Arrays.asList(prefixFieldSchema, idFieldSchema, xxFieldSchema));
//        test2.fieldsTerminatedBy('\t')
//                .linesTerminatedBy('\n')
//                .fileFormat("textfile");
//
//        client.createTable(HCatCreateTableDesc.create(test2, true).build());
//    }
//
//    @Test
//    public void describeTable() throws SQLException {
//        String sql = "describe " + tableName;
//        System.out.println("describe table:" + tableName);
//        resultSet = statement.executeQuery(sql);
//        while (resultSet.next()) {
//            System.out.println(resultSet.getString(1) + "\t" + resultSet.getString(2));
//        }
//    }
//
//    @Test
//    public void showTable() throws SQLException {
//        String sql = "show tables " + tableName;
//        System.out.println("show table:" + tableName);
//        resultSet = statement.executeQuery(sql);
//        while (resultSet.next()) {
//            System.out.println(resultSet.getString(1));
//        }
//    }
//
//    @Test
//    public void loadDataToTable() throws SQLException {
//    /*    String sql = isLocal ? "load data local inpath '" + localFilePath + "' overwrite into table " + tableName :
//                "load data inpath '" + hdfsFilePath + "' overwrite into table " + tableName;
//        System.out.println("load data into table:" + tableName);
//        statement.executeQuery(sql);*/
//    }
//
//    @Test
//    public void collectTable() {
//        try {
//            List<String> tableNames = client.listTableNamesByPattern("default", "*");
//            printResult(tableNames);
//        } catch (HCatException hCatEx) {
//            hCatEx.printStackTrace();
//        }
//
//
//    }
//
//    @Test
//    public void collectColumn() {
//        try {
//            HCatTable tableNames = client.getTable("default", "student");
//
//            List<HCatFieldSchema> listSchemas = tableNames.getCols();
//            listSchemas.forEach(item->{
//                System.out.println(item.getName()+"--"+item.getTypeString()+"--"+item.getComment());
//            });
//
//        } catch (HCatException hCatEx) {
//            hCatEx.printStackTrace();
//        }
//    }
//
//    @Test
//    public void collectConstraint() throws HCatException {
//        int count = 0; // records counter for particular year-month
//        for (IntWritable s:value) {
//            count++;
//        }
//
//        // define output record schema
//        List columns = new ArrayList(3);
//        columns.add(new HCatFieldSchema("year", HCatFieldSchema.Type.INT, ""));
//        columns.add(new HCatFieldSchema("month", HCatFieldSchema.Type.INT, ""));
//        columns.add(new HCatFieldSchema("flightCount", HCatFieldSchema.Type.INT,""));
//        HCatSchema schema = new HCatSchema(columns);
//        HCatRecord record = new DefaultHCatRecord(3);
//
//        record.setInteger("year", schema, key.getFirstInt());
//        record.set("month", schema, key.getSecondInt());
//        record.set("flightCount", schema, count);
//        context.write(null, record);
//    }
//
//
//
//  /*  public void insertDataToPartition() throws SQLException {
//        String insterSQL = "LOAD DATA LOCAL INPATH '/ldw/add.txt' OVERWRITE INTO TABLE javabloger";
//        Statement stmt = connection.createStatement();
//        stmt.executeQuery(insterSQL);  // 执行插入语句
//    }*/
//
//
//}
