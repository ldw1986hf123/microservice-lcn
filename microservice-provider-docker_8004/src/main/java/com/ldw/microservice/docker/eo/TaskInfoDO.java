package com.ldw.microservice.docker.eo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author AutoGenerator
 * @since 2021-03-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("task_info")
public class TaskInfoDO extends SuperEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 任务类型。1手动，0 自动
     */
    private Integer taskType;

    /**
     * 生效时间
     */
    private Date beginEffectiveTime;

    /**
     * 失效时间
     */
    private Date endEffectiveTime;

    /**
     * 关联类型(1: 外部数据源; 2: 计算资源)
     */
    private Integer relType;

    /**
     * 数据源类型：mysql-mysql；oracle-oracle；sqlserver-sqlserver；hive-hive；impala-impala；kafka-kafka；kudu-kudu
     */
    private String datasourceType;

    /**
     * 数据源id
     */
    private Long datasourceId;

    /**
     * 采集周期。0: 小时；1: 天；2: 周；3: 月；4: 手动采集
     */
    private Integer collectPeriod;

    /**
     * 小时开始时间
     */
    private Integer hourBeginTime;

    /**
     * 分钟开始时间
     */
    private Integer minuteBeginTime;

    /**
     * 小时结束时间
     */
    private Integer hourEndTime;

    /**
     * 分钟结束时间
     */
    private Integer minuteEndTime;

    /**
     * 小时采集时间
     */
    private Integer hourCollectTime;

    /**
     * 分钟采集时间
     */
    private Integer minuteCollectTime;

    /**
     * 间隔采集时间。值：1到23
     */
    private Integer intervalCollectTime;

    /**
     * 按周指定日期。值：1到7
     */
    private Integer specifiedDateWeekly;

    /**
     * 按月指定日期。值：1到31
     */
    private Integer specifiedDateMonthly;

    /**
     * cron表达式
     */
    private String cron;

    /**
     * 任务状态。0未启动，1运行中，2启动失败，3已停止，4运行成功，5运行失败，6已过期
     */
    private Integer status;

    /**
     * '任务类型。1手动，0 自动',
     */
    public enum TASK_TYPE {
        AUTO(1, "auto"),
        MANUAL(0, "manual");
        private Integer type;
        private String name;

        TASK_TYPE(Integer type, String name) {
            this.type = type;
            this.name = name;
        }

        public Integer getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public static String getEnumType(String status) {
            TASK_TYPE[] alarmGrades = TASK_TYPE.values();
            for (int i = 0; i < alarmGrades.length; i++) {
                if (alarmGrades[i].getType().equals(status)) {
                    return alarmGrades[i].name;
                }
            }
            return "";
        }
    }

    //'任务状态。0未启动，1运行中，2启动失败，3已停止，4运行成功，5运行失败，6已过期,7已启动',
    public enum STATUS {
        NOT_STARTED(0, "not_started"),
        RUNNING(1, "running"),
        FAILED_TO_STARTED(2, "failed_to_started"),
        STOPPED(3, "stopped"),
        RAN_SUCCESSFULLY(4, "ran_successfully"),
        FAILED_TO_RAN(5, "failed_to_ran"),
        EXPIRED(6, "expired"),
        STARTED(7, "failed_to_ran");

        private Integer status;
        private String name;

        STATUS(Integer status, String name) {
            this.status = status;
            this.name = name;
        }

        public Integer getStatus() {
            return status;
        }

        public String getName() {
            return name;
        }

        public static String getEnumType(String status) {
            STATUS[] alarmGrades = STATUS.values();
            for (int i = 0; i < alarmGrades.length; i++) {
                if (alarmGrades[i].getStatus().equals(status)) {
                    return alarmGrades[i].name;
                }
            }
            return "";
        }
    }


    /**
     * @desc 采集周期：0-小时；1-天；2-周；3-月；4-手动采集
     * public class CollectionPeriod {
     * public static final int HOUR = 0;
     * public static final int DAY = 1;
     * public static final int WEEK = 2;
     * public static final int MOUTH = 3;
     * public static final int MANUAL = 4;
     * }
     */
    public enum COLLECTION_PERIOD {
        HOUR(0, "小时"),
        DAY(1, "天"),
        WEEK(2, "周"),
        MOUTH(3, "月"),
        MANUAL(4, "手动采集");

        private Integer value;
        private String name;

        COLLECTION_PERIOD(Integer value, String name) {
            this.value = value;
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public String getName() {
            return name;
        }

        public static String getEnumType(String status) {
            COLLECTION_PERIOD[] alarmGrades = COLLECTION_PERIOD.values();
            for (int i = 0; i < alarmGrades.length; i++) {
                if (alarmGrades[i].getName().equals(status)) {
                    return alarmGrades[i].name;
                }
            }
            return "";
        }
    }

    public enum SPECIFIED_DATE_WEEKLY_LIST {
        Monday(1, "周一"),
        Tuesday(2, "周二"),
        Wednesday(3, "周三"),
        Thursday(4, "周四"),
        Friday(5, "周五"),
        Saturday(6, "周六"),
        Sunday(7, "周日");

        private Integer value;
        private String name;

        SPECIFIED_DATE_WEEKLY_LIST(Integer value, String name) {
            this.value = value;
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public String getName() {
            return name;
        }

        public static String getEnumType(String status) {
            SPECIFIED_DATE_WEEKLY_LIST[] alarmGrades = SPECIFIED_DATE_WEEKLY_LIST.values();
            for (int i = 0; i < alarmGrades.length; i++) {
                if (alarmGrades[i].getName().equals(status)) {
                    return alarmGrades[i].name;
                }
            }
            return "";
        }
    }

    /**
     * 操作类型：0-启动任务；1-停止任务；2-执行任务；3-删除任务,4过期任务
     */
    public enum OPERATIONS_TYPE {
        START(0, "start"),
        EXECUTE(1, "execute"),
        STOP(2, "stop"),
        DELETE(3, "delete"),
        EXPIRE(4, "expire");

        private Integer value;
        private String name;

        OPERATIONS_TYPE(Integer value, String name) {
            this.value = value;
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public String getName() {
            return name;
        }

        public static String getEnumType(String value) {
            OPERATIONS_TYPE[] alarmGrades = OPERATIONS_TYPE.values();
            for (int i = 0; i < alarmGrades.length; i++) {
                if (alarmGrades[i].getValue().equals(value)) {
                    return alarmGrades[i].name;
                }
            }
            return "";
        }
    }


    /**
     * 关联类型(1: 外部数据源; 2: 计算资源)',
     */
    public enum REL_TYPE {
        EXTERNAL_DATASOURCE(1, "computeResource"),
        COMPUTE_RESOURCE(2, "externalDatasource");

        private Integer value;
        private String name;

        REL_TYPE(Integer value, String name) {
            this.value = value;
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public String getName() {
            return name;
        }

        public static String getEnumType(String value) {
            REL_TYPE[] alarmGrades = REL_TYPE.values();
            for (int i = 0; i < alarmGrades.length; i++) {
                if (alarmGrades[i].getValue().equals(value)) {
                    return alarmGrades[i].name;
                }
            }
            return "";
        }
    }
}
