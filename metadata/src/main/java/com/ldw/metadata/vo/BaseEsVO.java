package com.ldw.metadata.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * ClassName:BaseEsVO <br/>
 * Function: 基础的EsVO类. <br/>
 * Reason: . <br/>
 * Date: 2020年10月24日 下午6:01:47 <br/>
 * 
 * @author WangXf
 * @version
 * @since JDK 1.8
 * @see
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class BaseEsVO implements Serializable {

	/**
	 * serialVersionUID:(序列号).
	 * 
	 * @since JDK 1.8
	 */
	private static final long serialVersionUID = -7370528720111762761L;
	private String index;
	private String id;
	private String jsonEntity;
	private Long timeout;

	public BaseEsVO() {

	}

	public BaseEsVO(String index, String id, String jsonEntity, Long timeout) {
		this.index = index;
		this.id = id;
		this.jsonEntity = jsonEntity;
		this.timeout = timeout;
	}

}
