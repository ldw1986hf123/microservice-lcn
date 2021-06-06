package com.ldw.microservice.docker.enums;

/**
 * ClassName:CommonContent <br/>
 * Function: 通用的常量类. <br/>
 * Reason: . <br/>
 * Date: 2020年9月23日 上午10:20:38 <br/>
 * 
 * @author WangXf
 * @version
 * @since JDK 1.8
 * @see
 */
public class CommonConstants {

	/**
	 * @desc oracle 字段可为空属性值集合
	 * 
	 * @author WangXf
	 * @date 2020年9月23日 上午10:23:14
	 */
	public class OracleNullable {
		public static final String NO = "N";
		public static final String YES = "Y";
	}

	/**
	 * @desc 是否主键
	 * 
	 * @author WangXf
	 * @date 2020年9月23日 上午10:23:14
	 */
	public class IsPrimaryKey {
		public static final int NO = 0;
		public static final int YES = 1;
	}

	/**
	 * @desc 是否外键
	 *
	 * @author WangXf
	 * @date 2020年9月23日 上午10:23:14
	 */
	public class IsForeignKey {
		public static final int NO = 0;
		public static final int YES = 1;
	}

	/**
	 * 是否必填
	 */
	public class IsRequired {
		public static final int NO = 0;
		public static final int YES = 1;
	}

	/**
	 * 是否分区
	 */
	public class IsPartitionKey {
		public static final int NO = 0;
		public static final int YES = 1;
	}

	/**
	 * @desc DB 字段可为空属性值集合
	 * 
	 * @author WangXf
	 * @date 2020年9月23日 上午10:23:14
	 */
	public class DBIsNull {
		public static final String NO = "not null";
		public static final String YES = "null";
	}

	/**
	 * 
	 * @desc 任务状态：0-未启动；1-运行中；2-启动失败；3-停止了；4-运行成功；5-运行失败；6-已失效；7-启动成功
	 * 
	 * @author WangXf
	 * @date 2020年9月25日 下午8:21:29
	 */
	public class TaskStatus {
		public static final int NOT_STARTED = 0;
		public static final int RUNNING = 1;
		public static final int FAILED_TO_STARTED = 2;
		public static final int STOPPED = 3;
		public static final int RAN_SUCCESSFULLY = 4;
		public static final int FAILED_TO_RAN = 5;
		public static final int INVALID = 6;
		public static final int STARTED_SUCCESSFULLY = 7;

	}

	/**
	 * @desc 任务响应码信息
	 * @author WangXf
	 * @date 2020年9月27日 上午11:17:01
	 */
	public enum ResponseCodeTask {
		FAIL("fail", "执行失败"),
		SUCCESS("success", "执行成功"),
		NOT_FOUND_TASK("not_found_task", "任务不存在"),
		INVALID_TASK("invalid_task", "无效任务"),
		TASK_IS_RUNNING("task_is_running", "任务正在运行中"),
		TASK_IS_ALREADY_STOPPED("task_is_already_stopped", "任务已经停止了");

		private String code;
		private String message;

		private ResponseCodeTask(String code, String message) {
			this.code = code;
			this.message = message;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}

	/**
	 * 
	 * @desc 是否删除：0-否；1-是
	 * 
	 * @author WangXf
	 * @date 2020年9月27日 上午11:35:36
	 */
	public static class IsDeleted {
		/**
		 * @desc 否
		 */
		public static final Boolean NO = Boolean.FALSE;
		/**
		 * @desc 是
		 */
		public static final Boolean YES = Boolean.TRUE;

	}

	/**
	 * 
	 * @desc 任务执行方式：0-自动；1-手动
	 * 
	 * @author WangXf
	 * @date 2020年9月27日 上午11:35:36
	 */
	public class TaskType {
		/**
		 * @desc 自动
		 */
		public static final int AUTOMATIC = 0;
		/**
		 * @desc 手动
		 */
		public static final int MANUAL = 1;
	}

	/**
	 * 
	 * @desc 操作类型：0-启动任务；1-停止任务；2-执行任务；3-删除任务
	 * 
	 * @author WangXf
	 * @date 2020年9月27日 下午4:13:27
	 */
	public class OperationType {
		public static final int START_TASK = 0;
		public static final int STOP_TASK = 1;
		public static final int EXECUTE_TASK = 2;
		public static final int DELETE_TASK = 3;
	}

	/**
	 * 
	 * @desc 数据源类型：mysql-mysql；oracle-oracle；spark-spark；sqlserver-sqlserver；postgresql-postgresql；kylin-kylin；ftp-ftp；hive-hive；hdfs-hdfs；hbase-hbase；es-es；impala-impala；kafka-kafka；kudu-kudu
	 * 
	 * @author WangXf
	 * @date 2020年9月27日 下午9:14:56
	 */
	public class DataSourceType {
		public static final String HIVE = "hive";
		public static final String IMPALA = "impala";
		public static final String ORACLE = "oracle";
		public static final String KUDU = "kudu";
		public static final String MYSQL = "mysql";
		public static final String SQLSERVER = "sqlserver";
	}

	/**
	 * @desc 是否分页标识
	 * 
	 * @author WangXf
	 * @date 2020年10月9日 下午9:23:08
	 */
	public class PagingFlag {
		public static final boolean YES = true;
		public static final boolean NO = false;
	}

	/**
	 * @desc 通用的响应码信息
	 * 
	 * @author WangXf
	 * @date 2020年9月27日 上午11:17:01
	 */
	public enum CommonResponseCode {
		FAIL("fail", "失败"), SUCCESS("success", "成功");

		private String code;
		private String message;

		private CommonResponseCode(String code, String message) {
			this.code = code;
			this.message = message;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}

	/**
	 * @desc 操作类型枚举类
	 *
	 * @author WangXf
	 * @date 2020年7月3日 上午11:17:30
	 */
	public enum OperationTypeEnum {
		ADD("add", "新增"), DEL("del", "删除"), PUT("put", "更新");

		/**
		 * @desc 操作代码
		 */
		private String code;
		/**
		 * @desc 操作名称
		 */
		private String name;

		private OperationTypeEnum(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public String getName() {
			return name;
		}

	}

	/**
	 * @desc 关联类型，1：外部数据源，2：计算资源
	 * 
	 * @author WangXf
	 * @date 2020年10月21日 下午9:51:36
	 */
	public class RelType {
		public static final int EXTERNAL_DATASOURCE = 1;
		public static final int COMPUTING_RESOURCE = 2;
	}

	/**
	 * 
	 * @desc 数据源环境类型（开发0、生产1、基础环境2）
	 * 
	 * @author WangXf
	 * @date 2020年10月22日 上午11:45:08
	 */
	public class DataSourceEnv {
		public static final int DEVELOP = 0;
		public static final int PRODUCT = 1;
		public static final int BASE = 2;
	}

	/**
	 * 
	 * @desc RedisGraph 常量信息
	 * 
	 * @author WangXf
	 * @date 2020年10月24日 下午7:02:12
	 */
	public class RedisGraph {
		/**
		 * @desc 默认的血缘关系层级
		 */
		public static final int DEFAULT_BLOOD_LEVEL = 2;
		/**
		 * @desc 血缘关系图表ID编号
		 */
		public static final String GRAPH_ID = "tableBloodRel";
		/**
		 * @desc 血缘关系标签名称
		 */
		public static final String REL_LABEL = "tableBloodRel";
		/**
		 * @desc 默认的表血缘关系节点标签名称
		 */
		public static final String DEFAULT_NODE_LABEL = "table";
	}

	/**
	 * 
	 * @desc 元数据变更操作类型枚举类
	 * 
	 * @author WangXf
	 * @date 2020年11月5日 上午10:12:13
	 */
	public enum MetadataOperationType {

		ADD("add", "新增"), DELETE("delete", "删除"), UPDATE("update", "更新");

		/**
		 * @desc 操作代码
		 */
		private String code;
		/**
		 * @desc 操作名称
		 */
		private String name;

		private MetadataOperationType(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public String getName() {
			return name;
		}

	}

	/**
	 * 
	 * @desc Hive表结构前缀
	 * 
	 * @author WangXf
	 * @date 2020年11月5日 上午11:35:51
	 */
	public class HiveTableStructurePrefix {
		public static final String PRIMARY_KEY = "# Primary Key";
		public static final String CONSTRAINT_NAME = "Constraint Name:";
		public static final String NOT_NULL_CONSTRAINTS = "# Not Null Constraints";
		public static final String COLUMN_NAME = "Column Name:";

	}

	/**
	 * @desc 是否是数组类型
	 * 
	 * @author WangXf
	 * @date 2020年11月9日 下午9:46:03
	 */
	public class IsArray {
		public static final String YES = "1";
		public static final String NO = "0";
	}

	/**
	 *
	 * @desc jdbc驱动类
	 *
	 * @author WangXf
	 * @date 2020年11月5日 上午10:40:49
	 */
	public class JdbcDriverClass {
		public static final String HIVE = "org.apache.hive.jdbc.HiveDriver";
		public static final String IMPALA = "com.cloudera.impala.jdbc41.Driver";
		public static final String ORACLE = "oracle.jdbc.driver.OracleDriver";
		public static final String MYSQL = "com.mysql.cj.jdbc.Driver";
		public static final String SQLSERVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	}

	/**
	 * 
	 * @desc DDL建表sql脚本
	 * 
	 * @author WangXf
	 * @date 2020年11月25日 下午4:03:00
	 */
	public static class DDLSql {
		public static final String CREATE = "CREATE";
		public static final String TABLE = "TABLE";
		public static final String ALTER = "ALTER";
		public static final String LEFT_BRACKET = "(";
		public static final String[] SYMBOL = { "`", ";" };
		public static final String DROP = "DROP";

	}

	/**
	 * 
	 * @desc 调度程序实例类
	 * 
	 * @author WangXf
	 * @date 2020年11月27日 下午5:59:58
	 */
	public class SchedulerInstanceClass {
		public static final String COMMON_METADATA_AUTO_COLLECTION_SCHEDULER = "com.deepexi.daas.metadata.scheduler.api.impl.CommonMetadataAutoCollectionScheduler";
	}

	/**
	 * 
	 * @desc 表血缘关系边类型，1-表顶点、2-job顶点
	 * 
	 * @author WangXf
	 * @date 2020年12月4日 下午5:53:30
	 */
	public class VertexType {
		public static final int TABLE_VERTEX = 1;
		public static final int JOB_VERTEX = 2;
	}

	/**
	 * @desc 采集周期：0-小时；1-天；2-周；3-月；4-手动采集
	 * 
	 * @author WangXf
	 * @date 2020年12月11日 上午11:43:37

	public class CollectionPeriod {
		public static final int HOUR = 0;
		public static final int DAY = 1;
		public static final int WEEK = 2;
		public static final int MOUTH = 3;
		public static final int MANUAL = 4;
	}*/

	/**
	 * @desc 表元数据历史记录的状态：0-已下架；1-服务中
	 * 
	 * @author WangXf
	 * @date 2020年12月15日 下午3:08:46
	 */
	public class TableMetadataHistoryRecordStatus {
		public static final int OFFLINE = 0;
		public static final int SERVICING = 1;
	}
}
