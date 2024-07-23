package com.ldw.microservice.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @Author ludanwen
 * @Description 元数据类目信息
 */
@Data
public class AddCategoryDTO  implements Serializable {
    /**
     * 类目名称
     */
    private String name;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 父ID
     */
    private Long parentId;

    /**
     * 全路径，格式为：“/”+类目ID
     */
    private String fullPath;


}
