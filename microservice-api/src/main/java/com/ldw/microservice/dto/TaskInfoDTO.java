package com.ldw.microservice.dto;

import com.deepexi.data.metadata.domain.eo.SuperEntity;
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
public class TaskInfoDTO extends SuperEntity {

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
     * 任务配置
     */
    private String config;
    /**
     * @desc jdbc驱动类
     */
    private String jdbcDriveClass;

}
