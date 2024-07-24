package com.ldw.microservice.service;


import com.deepexi.data.metadata.domain.dto.blood.*;
import com.deepexi.data.metadata.domain.query.blood.*;
import com.deepexi.util.pageHelper.PageBean;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/3 11:00
 * @Description
 */
public interface TableBloodBizService {
	/**
	 * 处理表血缘旧数据
	 */
	void handleTableBloodOldData();
	/**
	 * 处理表血缘数据
	 * @param param
	 */
	void handleTableBloodData(TableBloodQuery param);

	/**
	 * 根据id获得表血缘关系
	 * @param param
	 * @return
	 */
	TableBloodAllDTO getTableBloodRelationship(GetTableBloodQuery param);

	/**
	 * 删除多个顶点
	 * @param ids
	 * @param tenantId
	 * @return
	 */
	boolean deleteNodes(List<Long> ids, String tenantId);

	/**
	 * 获取单个顶点
	 * @param id
	 * @param tenantId
	 * @return
	 */
//	TableNodeOneVO getTableNode(Long id, String tenantId);

	/**
	 * 获取上下游节点数
	 * @param tableId
	 * @param tenantId
	 * @return
	 */
	TableBloodLevelDTO getNodeLevelNum(Long tableId, String tenantId);


	/**
	 * 获取上游/下游指定层级的表
	 * @param param
	 * @return
	 */
	PageBean<TableBloodInfluenceDTO> getSpecifiedLevelTables(TableBloodSpecifiedLevelQuery param);

    /**
     * 获取表血缘上/下游层数
	 * @param param
     * @return
     */
	TableBloodLevelNumDTO getTableLevelNum(TableBloodLevelNumQuery param);

    /**
     * 获取表血缘上/下游job
	 * @param param
     * @return
     */
	PageBean<TableBloodJobDTO> getTableBloodJobs(TableBloodJobQuery param);
    /**
     * 获取表产出
	 * @param param
     * @return
     */
	PageBean<TableBloodJobDTO> getTableBloodOutput(TableBloodOutputQuery param);

	/**
	 * 影响分析数据下载
	 * @param response
	 * @param param
	 */
	void downloadTableBloodInfluence(HttpServletResponse response, TableBloodInfluenceDownloadQuery param);


	/**
	 * 获取日志
	 * @param query
	 * @return
	 */
	JobInstanceLogDTO getInstanceLog(JobLatestLogQuery query);
}
