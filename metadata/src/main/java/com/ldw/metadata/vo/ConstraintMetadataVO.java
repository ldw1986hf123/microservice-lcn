
package com.ldw.metadata.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * ClassName:TableVO <br/>
 * Function: 约束   元  模型VO. <br/>
 * Reason: . <br/>
 * Date: 2020年8月28日 下午8:17:51 <br/>
 * 
 * @author ludanwen
 * @version
 * @since JDK 1.8
 * @see
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class ConstraintMetadataVO extends MetadataVO implements Serializable {
	/**
	 * @desc 主键id
	 */
	private String id;
	private String name;
	private String columnName;
	private String tableName;
	private String type;
	private String createdTime;
	private String updatedTime;
}
