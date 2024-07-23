package com.ldw.microservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.map.annotate.JsonSerialize;
/**
 *
 * @Author 卢丹文
 * @Description 基础类
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SuperEntity   implements Serializable {
    /**
     * 自增主键
     */
    @TableId(type = IdType.INPUT)
    @TableField(fill = FieldFill.INSERT)
    private Long id;

    @TableField(fill = FieldFill.INSERT)
    private String tenantId;

    /**
     * 版本号，乐观锁
     */
    @Version
    private Integer version;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Boolean isDeleted;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private String createdBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createdTime;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedTime;

}
