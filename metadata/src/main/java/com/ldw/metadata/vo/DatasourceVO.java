package com.ldw.metadata.vo;

import java.io.Serializable;

/**
 * ClassName:DatasourceVO <br/>
 * Function: 数据源VO接口类. <br/>
 * Reason: . <br/>
 * Date: 2020年9月17日 下午5:02:10 <br/>
 * 
 * @author WangXf
 * @version
 * @since JDK 1.8
 * @see
 */
public abstract class DatasourceVO implements Serializable {

	/**  
	 * serialVersionUID:(序列号).  
	 * @since JDK 1.8  
	 */
	private static final long serialVersionUID = -8124980623128763028L;

	/**
	 * @desc 获取数据源Id
	 *
	 * @return 数据源Id
	 */
	public abstract Long getId();

	/**
	 * @desc 获取数据源代码
	 *
	 * @return 数据源代码
	 */
	public abstract String getCode();

	/**
	 * 
	 * @desc 获得数据源类型
	 * 
	 * @return 数据源类型
	 */
	public abstract String getType();

	/**
	 * 
	 * @desc 获得项目Id
	 * 
	 * @return
	 */
	public abstract Long getProjectId();

}
