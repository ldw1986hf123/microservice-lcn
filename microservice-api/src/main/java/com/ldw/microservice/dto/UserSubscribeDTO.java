package com.ldw.microservice.dto;

import com.deepexi.data.metadata.domain.eo.SuperEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author AutoGenerator
 * @since 2021-03-18
 */
@Data
public class UserSubscribeDTO extends SuperEntity  implements Serializable {


    /**
     * 用户id
     */
    private Long userId;

    /**
     * 表id
     */
    private Long tableId;

    /**
     * 表名
     */
    private String tableCode;

    /**
     * 项目Id
     */
    private Long projectId;

    /**
     * 订阅状态，1: 已订阅  0: 取消订阅
     */
    private Integer isSubscribed;

    /**
     * 数据源id
     */
    private Long datasourceId;

    /**
     * 数据源类型
     */
    private String datasourceType;

    /**
     * 通知方式。1: mail,2: phone,多个值的话，用逗号隔开，如（1,2）
     */
    private String messageType;

    /**
     * 邮件
     */
    private String mail;

    /**
     * 手机
     */
    private String phone;


}
