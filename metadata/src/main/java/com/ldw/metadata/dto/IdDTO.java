package com.ldw.metadata.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2020. All rights reserved.
 *
 * @Author liangchaohua
 * @Date 2020/11/12 11:08
 * @Description ID和表ID
 */
@Data
@EqualsAndHashCode
public class IdDTO implements Serializable {
    /**
     * ID
     */
    protected Long id;
}
