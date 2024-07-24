package com.ldw.microservice.service;


import com.ldw.microservice.dto.ColumnBloodDTO;
import com.ldw.microservice.dto.ColumnBloodWebDTO;
import com.ldw.microservice.query.BloodQuery;
import com.ldw.microservice.query.GetFieldBloodQuery;

/**
 *
 * @Author 卢丹文
 * @Description
 */
public interface ColumnBloodBizService {
	/**
	 * 处理字段血缘数据
	 * @param param
	 */
	void handleFieldBloodData(BloodQuery param);

	/**
	 * 获取字段血缘
	 * @param param
	 * @return
	 */
	ColumnBloodWebDTO getFieldBlood(GetFieldBloodQuery param);

	/**
	 * 获取字段血缘
	 * @param tenantId
	 * @param jobId
	 * @return
	 */
	ColumnBloodDTO getColumnBloodInternal(String tenantId, Long jobId);

}
