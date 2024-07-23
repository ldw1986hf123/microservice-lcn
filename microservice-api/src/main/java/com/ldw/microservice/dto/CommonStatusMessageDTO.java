package com.ldw.microservice.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * ClassName:CommonStatusMessageVO <br/>
 * Function: 通用的调度程序的状态信息VO. <br/>
 * Reason: . <br/>
 * Date: 2020年9月27日 上午9:48:52 <br/>
 * 
 * @author WangXf
 * @version
 * @since JDK 1.8
 * @see
 */
public class CommonStatusMessageDTO extends StatusMessageDTO implements Serializable {

	/**
	 * serialVersionUID:(序列号).
	 * 
	 * @since JDK 1.8
	 */
	private static final long serialVersionUID = 7286420331636025769L;
	private Boolean status;
	private String message;
	private Long timeConsuming;
	private Date executedTime;
	/**
	 * @desc 任务执行信息详情
	 */
	private String messageDetail;

	public CommonStatusMessageDTO() {
	}

	public CommonStatusMessageDTO(Boolean status, String message, Long timeConsuming, Date executedTime) {
		this.status = status;
		this.message = message;
		this.timeConsuming = timeConsuming;
		this.executedTime = executedTime;
	}

	public CommonStatusMessageDTO(Boolean status, String message, Long timeConsuming, Date executedTime,
								  String messageDetail) {
		this.status = status;
		this.message = message;
		this.timeConsuming = timeConsuming;
		this.executedTime = executedTime;
		this.messageDetail = messageDetail;
	}

	@Override
	public Boolean getStatus() {
		return this.status;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public Long getTimeConsuming() {
		return this.timeConsuming;
	}

	public void setTimeConsuming(Long timeConsuming) {
		this.timeConsuming = timeConsuming;
	}

	@Override
	public Date getExecutedTime() {

		return this.executedTime;
	}

	public void setExecutedTime(Date executedTime) {
		this.executedTime = executedTime;
	}

	@Override
	public String getMessageDetail() {
		return messageDetail;
	}

	public void setMessageDetail(String messageDetail) {
		this.messageDetail = messageDetail;
	}

}
