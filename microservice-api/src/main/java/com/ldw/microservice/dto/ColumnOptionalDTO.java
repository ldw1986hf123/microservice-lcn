
package com.ldw.microservice.dto;

import com.deepexi.util.config.JsonDateSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.Date;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/2 14:34
 * @Description 查找字段列表简化类
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@ApiModel(description = "查找字段列表简化类")
public class ColumnOptionalDTO implements Serializable {

	/**
	 * serialVersionUID:(序列号).
	 * 
	 * @since JDK 1.8
	 */
	private static final long serialVersionUID = -7039620361961514227L;
	/**
	 * @desc 主键id
	 */
	@ApiModelProperty(value = "字段id", example = "0")
	private Long id;
	/**
	 * @desc 字段名称
	 */
	@ApiModelProperty(value = "字段名", example = "username")
	private String columnName;
	/**
	 * @desc 字段类型
	 */
	@ApiModelProperty(value = "字段类型", example = "varchar")
	private String columnType;
	/**
	 * @desc 字段描述
	 */
	@ApiModelProperty(value = "字段描述", example = "用户名")
	private String columnComment;
	/**
	 * @desc 是否为空
	 */
	@ApiModelProperty(value = "是否为空", example = "true")
	private Boolean isNull;
	/**
	 * @desc 是否是主键
	 */
	@ApiModelProperty(value = "是否是主键", example = "false")
	private Boolean isPrimaryKey;
	/**
	 * @desc 项目Id
	 */
	@ApiModelProperty(value = "项目Id", example = "1")
	private Long projectId;

	/**
	 * @desc 创建时间
	 */
	@ApiModelProperty(value = "创建时间")
	@JsonSerialize(using = JsonDateSerializer.class)
	private Date createdTime;

	/**
	 * @desc 创建人
	 */
	@ApiModelProperty(value = "创建人", example = "Susan")
	private String createdBy;

	/**
	 * @desc 更新时间
	 */
	@ApiModelProperty(value = "更新时间")
	@JsonSerialize(using = JsonDateSerializer.class)
	private Date updatedTime;

	/**
	 * @desc 更新人
	 */
	@ApiModelProperty(value = "更新人", example = "bob")
	private String updatedBy;

	/**
	 * @desc 表元数据id
	 */
	@ApiModelProperty(value = "所属表id", example = "0")
	private Long tableId;

}
