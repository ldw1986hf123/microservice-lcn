package com.ldw.microservice.docker.eo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2020. All rights reserved.
 *
 * @Author Fucy
 * @Date 2021/1/6 11:00
 * @Description 基础类
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SuperEntity implements Serializable {
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

}
