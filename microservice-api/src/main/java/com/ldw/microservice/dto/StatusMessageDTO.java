package com.ldw.microservice.dto;

import java.util.Date;

/**
 * ClassName:StatusMessageVO <br/>
 * Function: 调度程序的状态信息VO基类. <br/>
 * Reason: . <br/>
 * Date: 2020年9月27日 上午9:44:52 <br/>
 * 
 * @author WangXf
 * @version
 * @since JDK 1.8
 * @see
 */
public abstract class StatusMessageDTO {

	/**
	 * 
	 * @desc 获得状态信息
	 * 
	 * @return
	 */
	public abstract Boolean getStatus();

	/**
	 * 
	 * @desc 获得结果信息
	 * 
	 * @return
	 */
	public abstract String getMessage();

	/**
	 * 
	 * @desc 耗时（单位：毫秒ms）
	 * 
	 * @return 耗时长度
	 */
	public abstract Long getTimeConsuming();

	/**
	 * 
	 * @desc 任务执行时间
	 * 
	 * @return
	 */
	public abstract Date getExecutedTime();

	/**
	 * 
	 * @desc 任务执行信息详情
	 * 
	 * @return
	 */
	public abstract String getMessageDetail();

}
