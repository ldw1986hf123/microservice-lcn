package com.ldw.microservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.deepexi.bigdata.develop.api.TaskExecuteApi;
import com.deepexi.bigdata.develop.domain.CommonEntity;
import com.deepexi.bigdata.develop.domain.dto.LogPagingDTO;
import com.deepexi.bigdata.develop.enums.ResourceEnvironmentEnum;
import com.deepexi.daas.common.config.InternalPayload;
import com.deepexi.data.metadata.biz.CommonEsBizService;
import com.deepexi.data.metadata.biz.DevelopTaskBizService;
import com.deepexi.data.metadata.biz.TableBloodBizService;
import com.deepexi.data.metadata.biz.TableMetadataBizService;
import com.deepexi.data.metadata.constant.BloodConstants;
import com.deepexi.data.metadata.domain.dto.TableMetadataDTO;
import com.deepexi.data.metadata.domain.dto.blood.*;
import com.deepexi.data.metadata.domain.query.blood.*;
import com.deepexi.data.metadata.domain.vo.blood.TaskDetailVO;
import com.deepexi.data.metadata.util.BloodUtil;
import com.deepexi.data.metadata.util.DownloadExcelUtils;
import com.deepexi.util.CollectionUtil;
import com.deepexi.util.pageHelper.PageBean;
import com.redislabs.redisgraph.ResultSet;
import com.redislabs.redisgraph.graph_entities.Edge;
import com.redislabs.redisgraph.graph_entities.Node;
import com.redislabs.redisgraph.graph_entities.Path;
import com.redislabs.redisgraph.impl.api.RedisGraph;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/3 11:00
 * @Description
 */
@Slf4j
@Service
public class TableBloodBizServiceImpl implements TableBloodBizService {

    @Autowired
    private RedisGraph redisGraph;
    @Autowired
    private TableMetadataBizService tableMetadataBizService;
    @Autowired
    private TaskExecuteApi taskExecuteApi;
    @Autowired
    private DevelopTaskBizService developTaskBizService;
    @Autowired
    private CommonEsBizService commonEsBizService;
    @Value("${es.timeout:30}")
    private Long esTimeout;

    @Override
    public void handleTableBloodOldData() {
        StringBuilder querySql = new StringBuilder();
        // 获取血缘旧表顶点数据，graph_id = tableBloodRel,  vertexType=1
        querySql.append("MATCH (s:table{vertexType:1}) return count(s) AS total");
        long totalNum = 0;
        ResultSet totalSet = redisGraph.query("tableBloodRel", querySql.toString());
        while (totalSet.hasNext()) {
            Record record = totalSet.next();
            Object totalObj = record.getValue("total");
            totalNum = null == totalObj ? 0 : (Long) totalObj;
        }
        // 分页处理
        for(int i=0; i<=totalNum/1000; i++) {
            querySql.delete(0, querySql.length());
            querySql.append("MATCH (s:table{vertexType:1}) return s SKIP ").append(i*1000).append(" LIMIT 1000");
            log.info("handle old data get table node for RedisGraph: {}", querySql.toString());
            ResultSet oldTableBloodNodeResult = redisGraph.query("tableBloodRel", querySql.toString());
            List<Node> oldTableBloodNodeList = BloodUtil.parseResultSetGetNodes(oldTableBloodNodeResult, "s", null);
            // 插入新的格式表顶点数据
            oldTableBloodNodeList.stream().forEach(oldTableNode->{
                Object id = oldTableNode.getProperty("id").getValue();
                if (id instanceof Long) {
                    return;
                }
                Long oldTableId = Long.parseLong((String)oldTableNode.getProperty("id").getValue());

                String oldTenantId = (String) oldTableNode.getProperty("tenantId").getValue();
                String[] includeFields = { "id", "tableName", "tenantId", "dataSourceType", "dataSourceId", "projectId", "databaseName" };
                TableMetadataDTO tableMetadataDto = tableMetadataBizService.getTableMetaDate(oldTableId, oldTenantId, includeFields, esTimeout);
                if (null == tableMetadataDto || StringUtils.isBlank(tableMetadataDto.getId())) {
                    log.info("tableMetadataDto is null tableId={}, tenantId={}", oldTableId, oldTenantId);
                    return;
                }
                // 先把所有的表数据合到新的血缘数据里面取
                StringBuilder query = new StringBuilder();
                query.append("MERGE (s:").append(BloodConstants.TABLE_NODE).append("{tenantId:'").append(tableMetadataDto.getTenantId()).append("', id:").append(tableMetadataDto.getId())
                        .append("}) ON CREATE SET s.crtTime = ").append(System.currentTimeMillis()).append(", s.updateTime =").append(System.currentTimeMillis()).append(" ON MATCH SET s.updateTime = ").append(System.currentTimeMillis())
                        .append(" SET s.datasourceId=").append(tableMetadataDto.getDataSourceId()).append(",")
                        .append("s.datasourceType='").append(tableMetadataDto.getDataSourceType()).append("',")
                        .append("s.databaseCode='").append(tableMetadataDto.getDatabaseName()).append("',")
                        .append("s.projectId= ").append(tableMetadataDto.getProjectId()).append(",")
                        .append("s.tableCode= '").append(tableMetadataDto.getTableName()).append("'");
                log.info("merge table blood node or relation for RedisGraph: {}", query.toString());
                redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, querySql.toString());
            });
        }

        // 获取关系顶点
        querySql.delete(0, querySql.length());
        // 获取血缘旧表顶点数据，graph_id = tableBloodRel,  vertexType=1 表,vertexType=2 job
        // 获取全部关系
        querySql.append("MATCH p = (s:table{vertexType:1})-[r1:tableBloodRel]->(j:table{vertexType:2})-[r2:tableBloodRel]->(t:table{vertexType:1}) return p" );
        ResultSet oldTableBloodRelationResult = redisGraph.query("tableBloodRel", querySql.toString());
        handleOldDataResultSet(oldTableBloodRelationResult);
    }

    /**
     * 将redisgraph查出来的数据进行转换格式
     * @param resultSet
     */
    public void handleOldDataResultSet(ResultSet resultSet) {
        // 1. 获取旧数据的关系
        while (resultSet.hasNext()) {
            Record record = resultSet.next();
            Path p = record.getValue("p");
            Node firstNode = p.firstNode();
            Node lastNode = p.lastNode();
            Node jobNode = p.getNode(1);
            if(null == firstNode || null == lastNode || null == jobNode) {
                return;
            }

            Object sourceIdObj = firstNode.getProperty("id").getValue();
            Object targetIdObj = lastNode.getProperty("id").getValue();
            Object jobIdObj = jobNode.getProperty("id").getValue();
            Long oldTableSourceId = null;
            Long oldTableTargetId = null;
            if (sourceIdObj instanceof Long || targetIdObj instanceof Long || jobIdObj instanceof Long) {
                log.info("the data is useless. node={}", firstNode, targetIdObj, jobIdObj);
                return;
            } else {
                oldTableSourceId = Long.parseLong((String)sourceIdObj);
                oldTableTargetId = Long.parseLong((String)targetIdObj);
            }
            String[] includeFields = { "id", "tableName", "tenantId", "dataSourceType", "dataSourceId", "projectId", "databaseName" };
            TableMetadataDTO tableMetadataInput = tableMetadataBizService.getTableMetaDate(oldTableSourceId, (String)firstNode.getProperty("tenantId").getValue(), includeFields, esTimeout);
            if (null == tableMetadataInput || StringUtils.isBlank(tableMetadataInput.getId())) {
                log.info("create relation between table and relation is failed. can not find table input in metadata tableId = {}", oldTableSourceId);
                return;
            }
            //根据node传过来的数据匹配现有环境的tableMetaData
            TableMetadataDTO tableMetadataOutPut = tableMetadataBizService.getTableMetaDate(oldTableTargetId, (String)lastNode.getProperty("tenantId").getValue(), includeFields, esTimeout);
            if (null == tableMetadataOutPut || StringUtils.isBlank(tableMetadataOutPut.getId())) {
                log.info("create relation between table and relation is failed. can not find table output in metadata tableId = {}", oldTableTargetId);
                return;
            }
            // 此语句会适配库里面的顶点，有的更新，没有的创建，并且适配他们之间的关系，有的更新，没有的创建
            // 日期转为时间戳进行存储
            StringBuilder querySql = new StringBuilder();
            querySql.append("MERGE (s:").append(BloodConstants.TABLE_NODE).append("{tenantId:'").append(tableMetadataInput.getTenantId()).append("', id:").append(tableMetadataInput.getId())
                    .append("}) ON CREATE SET s.crtTime = ").append(System.currentTimeMillis()).append(", s.updateTime =").append(System.currentTimeMillis()).append(" ON MATCH SET s.updateTime = ").append(System.currentTimeMillis()).append(" SET ");
            if (null != tableMetadataInput.getDataSourceId()) {
                querySql.append("s.datasourceId=").append(tableMetadataInput.getDataSourceId()).append(",");
            }
            if (StringUtils.isNotBlank(tableMetadataInput.getDataSourceType())) {
                querySql.append("s.datasourceType='").append(tableMetadataInput.getDataSourceType()).append("',");
            }
            if (StringUtils.isNotBlank(tableMetadataInput.getDataSourceType())) {
                querySql.append("s.databaseCode='").append(tableMetadataInput.getDatabaseName()).append("',");
            }
            if (null != tableMetadataInput.getProjectId()) {
                querySql.append("s.projectId= ").append(tableMetadataInput.getProjectId()).append(",");
            }
            querySql.append("s.tableCode= '").append(tableMetadataInput.getTableName()).append("'");

            querySql.append(" MERGE (t:").append(BloodConstants.TABLE_NODE).append("{tenantId:'").append(tableMetadataOutPut.getTenantId()).append("', id:").append(tableMetadataOutPut.getId())
                    .append("}) ON CREATE SET t.crtTime = ").append(System.currentTimeMillis()).append(", t.updateTime =").append(System.currentTimeMillis()).append(" ON MATCH SET t.updateTime = ").append(System.currentTimeMillis()).append(" SET ");
            if (null != tableMetadataOutPut.getDataSourceId()) {
                querySql.append("s.datasourceId=").append(tableMetadataOutPut.getDataSourceId()).append(",");
            }
            if (StringUtils.isNotBlank(tableMetadataOutPut.getDataSourceType())) {
                querySql.append("s.datasourceType='").append(tableMetadataOutPut.getDataSourceType()).append("',");
            }
            if (StringUtils.isNotBlank(tableMetadataOutPut.getDataSourceType())) {
                querySql.append("s.databaseCode='").append(tableMetadataOutPut.getDatabaseName()).append("',");
            }
            if (null != tableMetadataOutPut.getProjectId()) {
                querySql.append("s.projectId= ").append(tableMetadataOutPut.getProjectId()).append(",");
            }
            querySql.append("s.tableCode= '").append(tableMetadataOutPut.getTableName()).append("'");


            querySql.append(" MERGE (s)-[r:").append(BloodConstants.TABLE_REL).append("{tenantId:'").append(tableMetadataOutPut.getTenantId()).append("', jobId:").append(Long.parseLong((String)jobNode.getProperty("id").getValue()))
                    .append("}]->(t) ON CREATE SET r.crtTime = ").append(System.currentTimeMillis()).append(", r.updateTime =").append(System.currentTimeMillis()).append(" ON MATCH SET r.updateTime = ").append(System.currentTimeMillis()).append(" SET ");
            if (null != jobNode.getProperty("vertexName").getValue()) {
                querySql.append("r.jobName='").append((String)jobNode.getProperty("vertexName").getValue()).append("',");
            }
            if (null != jobNode.getProperty("projectId").getValue()) {
                querySql.append("r.projectId=").append(Long.parseLong((String)jobNode.getProperty("projectId").getValue())).append(",");
            }
            if (null != jobNode.getProperty("projectCode").getValue()) {
                querySql.append("r.projectCode='").append((String)jobNode.getProperty("projectCode").getValue()).append("',");
            }
            querySql.append("r.status = 1");
            log.info("merge old blood data for RedisGraph: {}", querySql.toString());
            redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, querySql.toString());
        }
    }

    @Override
    public void handleTableBloodData(TableBloodQuery param) {
        log.info("------start handle tableBloodData------");
        String tenantId = param.getTenantId();
        String operation = param.getOperation();
        Integer operationType = param.getOperationType();
        List<TableNodeQuery> tableNodes = param.getTableNodes();
        List<TableEdgeQuery> edges = param.getEdges();
        List<TaskDetailQuery> jobs = param.getTaskDetails();
        if (null == operationType && (StringUtils.isBlank(operation) || operation.equals(BloodConstants.BloodOperation.DEFAULT))) {
            mergeTableBlood(tableNodes,  edges, jobs,  tenantId);
        }
        // operationType 0-node,1-edge
        if (null != operationType && 1 == operationType) {
            //进行关系进行操作
            switch (operation){
                case BloodConstants.BloodOperation.ADD:
                    // 创建节点之间的关系,顶点不存在会创建
                    addTableEdge(edges, jobs, tenantId);
                    break;
                case BloodConstants.BloodOperation.PUT:
                    updateTableEdge(jobs, tenantId);
                    break;
                case BloodConstants.BloodOperation.DEL:
                    // 删除两个节点之间的关系
                    delTableEdge(jobs, tenantId);
                    break;
                default:
                    break;
            }
        } else if (null != operationType && 0 == operationType) {
            //进行顶点操作
            switch (operation){
                case BloodConstants.BloodOperation.ADD:
                    // 创建顶点
                    createTableNodes(tableNodes, tenantId);
                    break;
                case BloodConstants.BloodOperation.DEL:
                    // 删除顶点
                    deleteTableNodes(tableNodes, tenantId);
                    break;
                case BloodConstants.BloodOperation.PUT:
                    // 修改顶点
                    updateTableNodes(tableNodes, tenantId);
                    break;
                default:
                    break;
            }
        }
        log.info("------handle tableBloodData end------");
    }
    
    /**
     * 自适应处理顶点和关系，关系不存在会创建，存在会修改
     * 顶点不存在不创建也不修改
     * @param tableNodes
     * @param edges
     */
    public void mergeTableBlood(List<TableNodeQuery> tableNodes, List<TableEdgeQuery> edges, List<TaskDetailQuery> jobs, String tenantId){
        // 先根据jobId 删除边
        delTableEdgeByJobId(jobs, tenantId);
        if (CollectionUtils.isEmpty(edges) || CollectionUtils.isEmpty(tableNodes)) {
            return;
        }
        // 重新导入血缘数据
        edges.stream().forEach(e->{
            Long sourceTableId = e.getSourceTableId();
            Long targetTableId = e.getTargetTableId();
            Long jobId = e.getJobId();
            TaskDetailQuery taskDetail = jobs.stream().filter(j -> jobId.equals(j.getJobId())).findAny().orElse(null);
            if (null == taskDetail) {
                // 任务不存在，不创建关系
                log.info("create relation between table and relation is failed. sourceTableId = {0}, jobId = {1} targetTableId = {2}" , sourceTableId, jobId, targetTableId);
                return;
            }
            TableNodeQuery tableNodeInput = tableNodes.stream().filter(t -> t.getTableId().equals(sourceTableId)).findAny().orElse(null);
            TableNodeQuery tableNodeOutput = tableNodes.stream().filter(t -> t.getTableId().equals(targetTableId)).findAny().orElse(null);

            // 此语句会适配库里面的顶点，有的更新，没有的创建，并且适配他们之间的关系，有的更新，没有的创建
            //根据node传过来的数据匹配现有环境的tableMetaData
            String[] includeFields = { "id", "tableName", "tenantId", "dataSourceType", "dataSourceId", "projectId", "databaseName" };
            TableMetadataDTO tableMetadataInput = tableMetadataBizService.getTableMetaDate(tableNodeInput.getTableId(), tenantId, includeFields, esTimeout);
            if (null == tableMetadataInput || StringUtils.isBlank(tableMetadataInput.getId())) {
				log.info("create relation between table and relation is failed. can not find table input in metadata tableId = {}", tableNodeInput.getTableId());
                return;
            }
            //根据node传过来的数据匹配现有环境的tableMetaData
            TableMetadataDTO tableMetadataOutPut = tableMetadataBizService.getTableMetaDate(tableNodeOutput.getTableId(), tenantId, includeFields, esTimeout);
            if (null == tableMetadataOutPut || StringUtils.isBlank(tableMetadataOutPut.getId())) {
                log.info("create relation between table and relation is failed. can not find table output in metadata tableId = {}", tableNodeOutput.getTableId());
                return;
            }
            // 此语句会适配库里面的顶点，有的更新，没有的创建，并且适配他们之间的关系，有的更新，没有的创建
            // 日期转为时间戳进行存储
            StringBuilder querySql = new StringBuilder();
            querySql.append("MERGE (s:").append(BloodConstants.TABLE_NODE).append("{tenantId:'").append(tableMetadataInput.getTenantId()).append("', id:").append(tableMetadataInput.getId())
                    .append("}) ON CREATE SET s.crtTime = ").append(System.currentTimeMillis()).append(", s.updateTime =").append(System.currentTimeMillis()).append(" ON MATCH SET s.updateTime = ").append(System.currentTimeMillis()).append(" SET ");
            if (null != tableMetadataInput.getDataSourceId()) {
                querySql.append("s.datasourceId=").append(tableMetadataInput.getDataSourceId()).append(",");
            }
            if (StringUtils.isNotBlank(tableMetadataInput.getDataSourceType())) {
                querySql.append("s.datasourceType='").append(tableMetadataInput.getDataSourceType()).append("',");
            }
            if (StringUtils.isNotBlank(tableMetadataInput.getDataSourceType())) {
                querySql.append("s.databaseCode='").append(tableMetadataInput.getDatabaseName()).append("',");
            }
            if (null != tableMetadataInput.getProjectId()) {
                querySql.append("s.projectId= ").append(tableMetadataInput.getProjectId()).append(",");
            }
            querySql.append("s.tableCode= '").append(tableMetadataInput.getTableName()).append("'");

            querySql.append(" MERGE (t:").append(BloodConstants.TABLE_NODE).append("{tenantId:'").append(tableMetadataOutPut.getTenantId()).append("', id:").append(tableMetadataOutPut.getId())
                    .append("}) ON CREATE SET t.crtTime = ").append(System.currentTimeMillis()).append(", t.updateTime =").append(System.currentTimeMillis()).append(" ON MATCH SET t.updateTime = ").append(System.currentTimeMillis()).append(" SET ");
            if (null != tableMetadataOutPut.getDataSourceId()) {
                querySql.append("s.datasourceId=").append(tableMetadataOutPut.getDataSourceId()).append(",");
            }
            if (StringUtils.isNotBlank(tableMetadataOutPut.getDataSourceType())) {
                querySql.append("s.datasourceType='").append(tableMetadataOutPut.getDataSourceType()).append("',");
            }
            if (StringUtils.isNotBlank(tableMetadataOutPut.getDataSourceType())) {
                querySql.append("s.databaseCode='").append(tableMetadataOutPut.getDatabaseName()).append("',");
            }
            if (null != tableMetadataOutPut.getProjectId()) {
                querySql.append("s.projectId= ").append(tableMetadataOutPut.getProjectId()).append(",");
            }
            querySql.append("s.tableCode= '").append(tableMetadataOutPut.getTableName()).append("'");


            querySql.append(" MERGE (s)-[r:").append(BloodConstants.TABLE_REL).append("{tenantId:'").append(tableMetadataOutPut.getTenantId()).append("', jobId:").append(e.getJobId())
                    .append("}]->(t) ON CREATE SET r.crtTime = ").append(System.currentTimeMillis()).append(", r.updateTime =").append(System.currentTimeMillis()).append(" ON MATCH SET r.updateTime = ").append(System.currentTimeMillis()).append(" SET ");
            if (StringUtils.isNotBlank(taskDetail.getJobName())) {
                querySql.append("r.jobName='").append(taskDetail.getJobName()).append("',");
            }
            if (null != taskDetail.getProjectId()) {
                querySql.append("r.projectId=").append(taskDetail.getProjectId()).append(",");
            }
            if (StringUtils.isNotBlank(taskDetail.getProjectCode())) {
                querySql.append("r.projectCode='").append(taskDetail.getProjectCode()).append("',");
            }
            Integer status = 1;
            if (null != taskDetail) {
                status = taskDetail.getStatus();
            }
            querySql.append("r.status = ").append(status);
            log.info("merge table blood node or relation for RedisGraph: {}", querySql.toString());
            redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, querySql.toString());
        });
    }

    public static boolean allFieldIsNotNull(Object o) {
        try {
            for (Field field : o.getClass().getDeclaredFields()) {
                //把私有属性公有化
                field.setAccessible(true);
                Object object = field.get(o);
                if (Objects.isNull(object)) {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 添加顶点之间的关系
     * @param edges
     */
    public void addTableEdge(List<TableEdgeQuery> edges, List<TaskDetailQuery> jobs, String tenantId){
        if (CollectionUtil.isEmpty(edges) || CollectionUtil.isEmpty(jobs)) {
            return;
        }
        edges.stream().forEach(e->{
            Long sourceTableId = e.getSourceTableId();
            Long targetTableId = e.getTargetTableId();
            Long jobId = e.getJobId();
            TaskDetailQuery taskDetail = jobs.stream().filter(j -> jobId.equals(j.getJobId())).findAny().orElse(null);
            StringBuilder querySql = new StringBuilder();
            querySql.append("MATCH (s:").append(BloodConstants.TABLE_NODE).append("{tenantId:'").append(tenantId).append("', id:").append(sourceTableId).append("}) ")
                    .append("MATCH (t:").append(BloodConstants.TABLE_NODE).append("{tenantId:'").append(tenantId).append("', id:").append(targetTableId).append("}) ")
                    .append("CREATE (s)-[r:").append(BloodConstants.TABLE_REL).append("{tenantId:'").append(tenantId).append("', jobId:").append(taskDetail.getJobId()).append(",");
            if (StringUtils.isNotBlank(taskDetail.getJobName())) {
                querySql.append("jobName:'").append(taskDetail.getJobName()).append("',");
            }
            if (null != taskDetail.getProjectId()) {
                querySql.append("projectId:").append(taskDetail.getProjectId()).append(",");
            }
            if (StringUtils.isNotBlank(taskDetail.getProjectCode())) {
                querySql.append("projectCode:'").append(taskDetail.getProjectCode()).append("',");
            }
            Integer status = 1;
            if (null != taskDetail.getStatus()) {
                status = taskDetail.getStatus();
            }
            querySql.append("status:").append(status).append(", crtTime:").append(System.currentTimeMillis()).append(", updateTime:").append(System.currentTimeMillis()).append("}]->(t) ");
            log.info("create table blood relation for RedisGraph: {}", querySql.toString());
            redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, querySql.toString());
        });
    }

    /**
     * 修改顶点间的关系
     * 顶点不存在不创建也不修改
     * @param jobs
     */
    public void updateTableEdge(List<TaskDetailQuery> jobs, String tenantId){
        if (CollectionUtil.isEmpty(jobs)) {
            return;
        }
        jobs.stream().forEach(e->{
            Long jobId = e.getJobId();
            StringBuilder querySql = new StringBuilder();
            querySql.append("MATCH (s:").append(BloodConstants.TABLE_NODE).append("{tenantId:'").append(tenantId).append("'}) ")
                    .append("MATCH (t:").append(BloodConstants.TABLE_NODE).append("{tenantId:'").append(tenantId).append("'}) ")
                    .append("MATCH (s)-[r:").append(BloodConstants.TABLE_REL).append("{tenantId:'").append(tenantId).append("', jobId:").append(jobId).append("}]->(t) SET ");
            Integer status = 1;
            if (null != e.getStatus()) {
                status = e.getStatus();
            }
            querySql.append("r.status=").append(status).append(",");
            if (null != e.getJobName()) {
                querySql.append("r.jobName='").append(e.getJobName()).append("',");
            }
            if (null != e.getProjectId()) {
                querySql.append("r.projectId=").append(e.getProjectId()).append(",");
            }
            if (null != e.getProjectCode()) {
                querySql.append("r.projectCode='").append(e.getProjectCode()).append("',");
            }
            querySql.append("r.updateTime = ").append(System.currentTimeMillis());
            log.info("update table blood relation for RedisGraph: {}", querySql.toString());
            redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, querySql.toString());
        });
    }

    /**
     * 删除顶点之间的关系
     * @param jobs
     * @param tenantId
     */
    public void delTableEdge(List<TaskDetailQuery> jobs, String tenantId){
        if (CollectionUtils.isEmpty(jobs)) {
            return;
        }
        jobs.stream().forEach(e->{
            StringBuilder querySql = new StringBuilder();
            querySql.append("MATCH (s:").append(BloodConstants.TABLE_NODE).append("{tenantId:'").append(tenantId).append("'})-[r:").append(BloodConstants.TABLE_REL).append("]->(t:").append(BloodConstants.TABLE_NODE).append("{tenantId:'").append(tenantId).append("'}) WHERE (r.tenantId = '").append(tenantId)
                    .append("' AND r.jobId =").append(e.getJobId()).append(") DELETE r");
            log.info("delete table blood relation for RedisGraph: {}", querySql.toString());
            redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, querySql.toString());
        });
    }
    /**
     * 删除顶点之间的关系
     * @param jobs
     * @param tenantId
     */
    public void delTableEdgeByJobId(List<TaskDetailQuery> jobs, String tenantId){
        if(CollectionUtil.isEmpty(jobs)) {
            return;
        }
        jobs.stream().forEach(job->{
            StringBuilder querySql = new StringBuilder();
            querySql.append("MATCH (s:").append(BloodConstants.TABLE_NODE).append("{tenantId:'").append(tenantId).append("'})-[r:")
                    .append(BloodConstants.TABLE_REL).append("{jobId:").append(job.getJobId()).append(", tenantId:'").append(tenantId).append("'}]->(t:")
                    .append(BloodConstants.TABLE_NODE).append("{tenantId:'").append(tenantId).append("'})").append(" DELETE r");
            log.info("delete table blood relation for RedisGraph: {}", querySql.toString());
            redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, querySql.toString());
        });
    }

    /**
     * 创建顶点
     * @param tableNodes
     */
    public void createTableNodes(List<TableNodeQuery> tableNodes, String tenantId){
        if (CollectionUtils.isEmpty(tableNodes)) {
            return;
        }
        StringBuilder querySql = new StringBuilder();
        querySql.append("CREATE ");
        tableNodes.stream().forEach(node->{
            //根据node传过来的数据匹配现有环境的tableMetaData
            String[] includeFields = { "id", "tableName", "tenantId", "dataSourceType", "dataSourceId", "projectId", "databaseName" };
            TableMetadataDTO tableMetadataVO = tableMetadataBizService.getTableMetaDate(node.getTableId(), tenantId, includeFields, esTimeout);
            if (null != tableMetadataVO && StringUtils.isNotBlank(tableMetadataVO.getId())) {
                querySql.append("(:").append(BloodConstants.TABLE_NODE).append("{id:").append(tableMetadataVO.getId()).append(",");
                if (StringUtils.isNotBlank(tableMetadataVO.getTenantId())) {
                    querySql.append("tenantId:'").append(tableMetadataVO.getTenantId()).append("',");
                }
                if (null != tableMetadataVO.getDataSourceId()) {
                    querySql.append("datasourceId:").append(tableMetadataVO.getDataSourceId()).append(",");
                }
                if (StringUtils.isNotBlank(tableMetadataVO.getDataSourceType())) {
                    querySql.append("datasourceType:'").append(tableMetadataVO.getDataSourceType()).append("',");
                }
                if (StringUtils.isNotBlank(tableMetadataVO.getTableName())) {
                    querySql.append("tableCode:'").append(tableMetadataVO.getTableName()).append("',");
                }
                if (StringUtils.isNotBlank(tableMetadataVO.getDatabaseName())) {
                    querySql.append("databaseCode:'").append(tableMetadataVO.getDatabaseName()).append("',");
                }
                if (null != tableMetadataVO.getProjectId()) {
                    querySql.append("projectId:").append(tableMetadataVO.getProjectId()).append(",");
                }
                querySql.append("crtTime:").append(System.currentTimeMillis()).append(", updateTime:").append(System.currentTimeMillis()).append("}), ");
            }
        });
        String substring = StringUtils.substring(querySql.toString(), 0, querySql.toString().length() - 2);
        log.info("create table blood node statement for RedisGraph: {}", substring);
        redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, substring);
    }
    /**
     * 修改顶点
     * @param tableNodes
     */
    public void updateTableNodes(List<TableNodeQuery> tableNodes, String tenantId){
        if (CollectionUtils.isEmpty(tableNodes)) {
            return;
        }
        tableNodes.stream().forEach(node->{
            StringBuilder querySql = new StringBuilder();
            //根据node传过来的数据匹配现有环境的tableMetaData
            String[] includeFields = { "id", "tableName", "tenantId", "dataSourceType", "dataSourceId", "projectId", "databaseName" };
            TableMetadataDTO tableMetadataVO = tableMetadataBizService.getTableMetaDate(node.getTableId(), tenantId, includeFields, esTimeout);
            if (null != tableMetadataVO && StringUtils.isNotBlank(tableMetadataVO.getId())) {
                querySql.append("MATCH (t:").append(BloodConstants.TABLE_NODE).append("{tenantId:'").append(tableMetadataVO.getTenantId()).append("', id:").append(tableMetadataVO.getId()).append("}) SET ");
                if (null != tableMetadataVO.getDataSourceId()) {
                    querySql.append(" t.datasourceId =").append(tableMetadataVO.getDataSourceId()).append(",");
                }
                if (StringUtils.isNotBlank(tableMetadataVO.getDataSourceType())) {
                    querySql.append("t.datasourceType ='").append(tableMetadataVO.getDataSourceType()).append("',");
                }
                if (StringUtils.isNotBlank(tableMetadataVO.getTableName())) {
                    querySql.append("t.tableCode ='").append(tableMetadataVO.getTableName()).append("',");
                }
                if (StringUtils.isNotBlank(tableMetadataVO.getDatabaseName())) {
                    querySql.append("t.databaseCode ='").append(tableMetadataVO.getDatabaseName()).append("',");
                }
                if (null != tableMetadataVO.getProjectId()) {
                    querySql.append(" t.projectId =").append(tableMetadataVO.getProjectId()).append(",");
                }
                querySql.append("t.updateTime = ").append(System.currentTimeMillis());
                log.info("update table blood node statement for RedisGraph: {}", querySql.toString());
                redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, querySql.toString());
            }
        });
    }

    /**
     * 删除表血缘多个顶点
     * @param tableNodes
     * @return
     */
    public boolean deleteTableNodes(List<TableNodeQuery> tableNodes, String tenantId) {
        if (CollectionUtils.isEmpty(tableNodes)) {
            return false;
        }
        List<Long> ids = tableNodes.stream().map(n->{return n.getTableId();}).collect(Collectors.toList());
        return deleteNodes(ids, tenantId);
    }

    /**
     * 删除表血缘多个顶点
     * @param ids
     * @return
     */
    @Override
    public boolean deleteNodes(List<Long> ids, String tenantId) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }
        StringBuilder querySql = new StringBuilder();
        querySql.append("MATCH (t:").append(BloodConstants.TABLE_NODE).append(") WHERE (t.tenantId = '").append(tenantId).append("' AND t.id IN ").append(ids).append(") DELETE t");
        log.info("delete table blood node statement for RedisGraph: {}", querySql.toString());
        redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, querySql.toString());
        return true;
    }

    @Override
    public TableBloodAllDTO getTableBloodRelationship(GetTableBloodQuery param) {
        Long id = param.getTableId();
        String tenantId = param.getTenantId();
        Integer upstreamLevel = param.getUpstreamLevel();
        Integer downstreamLevel = param.getDownstreamLevel();

        Map<Long, NodeDTO> nodesDtoMap = new HashMap<>();
        Map<Long, Edge> edgesMap = new HashMap<Long, Edge>();
        Map<Long, Node> nodesMap = new HashMap<Long, Node>();
        if (null != upstreamLevel) {
            // 查询上游表和关系
            getTableBloodRelation(id, tenantId, true, upstreamLevel, nodesMap, edgesMap);

            // 设置为上游的顶点
            Map<Long, NodeDTO> upNodes = nodesMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, n -> {
                NodeDTO dto = new NodeDTO();
                // 设置为上游
                dto.setNodeType(1);
                dto.setNode(n.getValue());
                return dto;
            }));
            nodesDtoMap.putAll(upNodes);
        }

        nodesMap.clear();
        if (null != downstreamLevel) {
            // 查询下游表和关系
            getTableBloodRelation(id, tenantId, false, downstreamLevel, nodesMap, edgesMap);

            Map<Long, NodeDTO> downNodes = nodesMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, n -> {
                NodeDTO dto = new NodeDTO();
                if (nodesDtoMap.containsKey(n.getKey())) {
                    // 上游表已经存在，设置为极为上游又为下游
                    dto.setNodeType(3);
                } else {
                    // 设置为下游
                    dto.setNodeType(2);
                }
                dto.setNode(n.getValue());
                return dto;
            }));
            nodesDtoMap.putAll(downNodes);
        }
        TableBloodAllDTO tableBloodDTO = collectResult(nodesDtoMap, edgesMap, id, tenantId);
        return tableBloodDTO;
    }

    private void getTableBloodRelation(Long id, String tenantId, Boolean isUpstream, Integer level, Map<Long, Node> nodesMap, Map<Long, Edge> edgesMap){
        Map<Long, Edge> edgesMapTemp = new HashMap<Long, Edge>();
        Map<Long, Node> nodesMapTemp = new HashMap<Long, Node>();
        String recordKey = "p";
        // 先获取符合条件的下级节点id ---tableIds
        ResultSet resultSet = getTableBloodVariableLength(id, tenantId, isUpstream, 1, level);
        log.info("getTableBloodVariableLength result={}", resultSet);
        BloodUtil.parseResultSet(resultSet, recordKey, nodesMapTemp, edgesMapTemp, 1);
        nodesMap.putAll(nodesMapTemp);
        edgesMap.putAll(edgesMapTemp);
    }

    /**
     * 查询跳层级结果集
     * @param id
     * @param tenantId
     * @param isUpstream
     * @param upLevel
     * @param downLevel
     * @return
     */
    private ResultSet getTableBloodVariableLength(Long id, String tenantId, Boolean isUpstream, Integer upLevel, Integer downLevel) {
        StringBuilder querySql = new StringBuilder();
        querySql.append("MATCH p=(s:").append(BloodConstants.TABLE_NODE).append(")-[r:")
                .append(BloodConstants.TABLE_REL).append("*");
        if(null != upLevel && upLevel >= 0) {
            querySql.append(upLevel);
        }
        querySql.append("..");
        if(null != downLevel && downLevel >= 0) {
            querySql.append(downLevel);
        }
        querySql.append("]->(t:").append(BloodConstants.TABLE_NODE).append(") ");
        if (isUpstream) {
            // 查询上游
            querySql.append("WHERE t.id=").append(id).append(" AND t.tenantId='").append(tenantId).append("'");
        } else {
            // 查询下游
            querySql.append("WHERE s.id=").append(id).append(" AND s.tenantId='").append(tenantId).append("'");
        }
        querySql.append(" RETURN p");
        log.info("query variable length statement input source for RedisGraph: {}", querySql.toString());
        return redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, querySql.toString());
    }

    /**
     * 将数据转化成前端需要的格式
     * @param edgesMap
     * @param nodesMap
     * @return
     */
    private TableBloodAllDTO collectResult(Map<Long, NodeDTO> nodesMap, Map<Long, Edge> edgesMap, Long currentTableId, String tenantId){
        TableBloodAllDTO tableBloodDto = new TableBloodAllDTO();
        List<TableNodeDTO> tableNodes = new ArrayList<>();
        if (CollectionUtils.isEmpty(nodesMap)) {
            String[] includeFields = { "id", "tableName", "tableComment", "dataSourceType", "tenantId", "dataSourceId", "owner" };
            TableMetadataDTO tableMetaDate = tableMetadataBizService.getTableMetaDate(currentTableId, tenantId, includeFields, esTimeout);
            log.info("get table relationship node get metadata table ={}", tableMetaDate);
            TableNodeDTO tableNodeDto = new TableNodeDTO();
            if(null != tableMetaDate) {
                tableNodeDto.setTableName(tableMetaDate.getTableName());
                tableNodeDto.setTableComment(tableMetaDate.getTableComment());
            }
            tableNodeDto.setTableId(currentTableId);
            tableNodeDto.setNodeType(0);
            tableNodeDto.setUpstreamTablesNumber(0);
            tableNodeDto.setDownstreamTablesNumber(0);
            // todo 待处理
            tableBloodDto.setTableNodes(Arrays.asList(tableNodeDto));
            return tableBloodDto;
        }
        nodesMap.forEach((k, v)->{
            TableNodeDTO tableNode = new TableNodeDTO();
            Integer nodeType = v.getNodeType();
            tableNode.setNodeType(nodeType);
            Node node = v.getNode();
            long tableId = Long.parseLong(String.valueOf(node.getProperty("id").getValue()));
            String[] includeFields = { "id", "tableName", "tableComment", "dataSourceType", "tenantId", "dataSourceId", "owner" };
            TableMetadataDTO tableMetaDate = tableMetadataBizService.getTableMetaDate(tableId, tenantId, includeFields, esTimeout);
            log.info("get table relationship node get metadata table ={}", tableMetaDate);
            if(null != tableMetaDate) {
                tableNode.setTableName(tableMetaDate.getTableName());
                tableNode.setTableComment(tableMetaDate.getTableComment());
            }
            tableNode.setTableId(tableId);
            if (currentTableId.equals(tableId)){
                // 设置为当前表
                tableNode.setNodeType(0);
            }
            //获取上下游节点数
            TableBloodLevelDTO nodeLevelNum = getNodeLevelNum(tableId, tenantId);
            if (!BeanUtil.isEmpty(nodeLevelNum)) {
                tableNode.setUpstreamTablesNumber(nodeLevelNum.getNextLayerTableNum());
                tableNode.setDownstreamTablesNumber(nodeLevelNum.getUpperStoryNum());
            }
            tableNodes.add(tableNode);
        });

        Map<String, TableEdgeDTO> tableEdges = new HashedMap();
        AtomicReference<Long> crtTime = new AtomicReference<>(0L);
        edgesMap.forEach((k, v)->{
            // 来源nodeId
            long source = v.getSource();
            // 目标nodeId
            long destination = v.getDestination();
            String key = source + "_" + destination;

            TableEdgeDTO tableEdge = tableEdges.get(key);

            long sourceTableId = Long.parseLong(String.valueOf(nodesMap.get(source).getNode().getProperty("id").getValue()));
            TableNodeDTO tableNodeSource = tableNodes.stream().filter(t -> t.getTableId() == sourceTableId).findAny().get();
            long targetTableId = Long.parseLong(String.valueOf(nodesMap.get(destination).getNode().getProperty("id").getValue()));
            TableNodeDTO tableNodeTarget = tableNodes.stream().filter(t -> t.getTableId() == targetTableId).findAny().get();

            String projectCode = null == v.getProperty("projectCode") ? null : (String)v.getProperty("projectCode").getValue();
            Long jobId = (Long)v.getProperty("jobId").getValue();
            Long projectId = null == v.getProperty("projectId")? null : (Long)v.getProperty("projectId").getValue();
            String jobName = (String)v.getProperty("jobName").getValue();
            TaskDetailVO taskDetail = developTaskBizService.getTaskDetail(tenantId, projectCode, projectId, jobId, jobName);
            TaskDetailBloodDTO taskDetailBloodDTO = new TaskDetailBloodDTO();
            // 获取不到实例信息，job信息还是要
            if (null == taskDetail) {
                taskDetailBloodDTO.setJobId(jobId);
                taskDetailBloodDTO.setJobName(jobName);
            } else {
                BeanUtil.copyProperties(taskDetail, taskDetailBloodDTO);
            }
            if (null == tableEdge) {
                crtTime.set((Long) v.getProperty("crtTime").getValue());
                tableEdge = getTableEdge(sourceTableId, targetTableId, tableNodeSource.getTableName(), tableNodeTarget.getTableName());
                // 取最新的实例信息, 没有接口，暂时获取不到
                List<TaskDetailBloodDTO> tasks = new ArrayList<>(1);
                tasks.add(taskDetailBloodDTO);
                tableEdge.setTasks(tasks);
                tableEdges.put(key, tableEdge);
            } else {
                // 当前版本只需要最新job,因此这里需要比较crtTime,以获取最新的job
                List<TaskDetailBloodDTO> tasksCurrent = tableEdge.getTasks();
                if (!CollectionUtils.isEmpty(tasksCurrent)) {
                    if (crtTime.get().longValue() < (Long)v.getProperty("crtTime").getValue()) {
                        List<TaskDetailBloodDTO> tasksNew = new ArrayList<>();
                        tasksNew.add(taskDetailBloodDTO);
                        tableEdge.setTasks(tasksNew);
                        tableEdges.put(key, tableEdge);
                    }
                } else {
                    tasksCurrent = new ArrayList<>();
                    tasksCurrent.add(taskDetailBloodDTO);
                    tableEdge.setTasks(tasksCurrent);
                    tableEdges.put(key, tableEdge);
                }
            }
        });
        tableBloodDto.setTableNodes(tableNodes);
        List<TableEdgeDTO> values = tableEdges.values().stream().collect(Collectors.toList());
        tableBloodDto.setTableEdges(values);
        return tableBloodDto;
    }

    /**
     * 获取表的连接信息
     * @param sourceTableId
     * @param targetTableId
     * @param sourceTableName
     * @param targetTableName
     * @return
     */
    private TableEdgeDTO getTableEdge(Long sourceTableId, Long targetTableId, String sourceTableName, String targetTableName){
        TableEdgeDTO tableEdge = new TableEdgeDTO();
        tableEdge.setSourceTableId(sourceTableId);
        tableEdge.setSourceTableName(sourceTableName);
        tableEdge.setTargetTableId(targetTableId);
        tableEdge.setTargetTableName(targetTableName);
        return tableEdge;
    }

    @Override
    public TableBloodLevelDTO getNodeLevelNum(Long tableId, String tenantId) {
        Long downLevel = 0L;
        Long upLevel = 0L;
        StringBuilder querySqlDown = new StringBuilder();
        // 去除自己指向自己
        querySqlDown.append("MATCH (s:").append(BloodConstants.TABLE_NODE).append("{id:").append(tableId).append(", tenantId:'").append(tenantId).append("'})-[r:").append(BloodConstants.TABLE_REL).append("{status:1}]->(t:").append(BloodConstants.TABLE_NODE).append("{tenantId:'").append(tenantId).append("'}) WHERE NOT t.id = ").append(tableId).append(" return count(DISTINCT t.id) AS nextLayerTableNum");
        log.info("query node level down stream num for RedisGraph: {}", querySqlDown.toString());
        ResultSet resultSetDown = redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, querySqlDown.toString());
        TableBloodLevelDTO dto = new TableBloodLevelDTO();
        while (resultSetDown.hasNext()) {
            Record record = resultSetDown.next();
            Object downStreamNum = record.getValue("nextLayerTableNum");
            downLevel = null == downStreamNum ? 0 : (Long) downStreamNum;
            dto.setTableId(tableId);
        }
        StringBuilder querySqlUp = new StringBuilder();
        // 去除自己指向自己
        querySqlUp.append("MATCH (s:").append(BloodConstants.TABLE_NODE).append("{tenantId:'").append(tenantId).append("'})-[r:").append(BloodConstants.TABLE_REL).append("{status:1}]->(t:").append(BloodConstants.TABLE_NODE).append("{id:").append(tableId).append(", tenantId:'").append(tenantId).append("'}) WHERE NOT s.id = ").append(tableId).append(" return count(DISTINCT s.id) AS upperStoryTableNum");
        log.info("query node level up stream num for RedisGraph: {}", querySqlUp.toString());
        ResultSet resultSet = redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, querySqlUp.toString());
        log.info("get level up stream num result={}", resultSet);
        while (resultSet.hasNext()) {
            Record record = resultSet.next();
            Object upstreamNum = record.getValue("upperStoryTableNum");
            upLevel = null == upstreamNum ? 0 : (Long) upstreamNum;
            dto.setNextLayerTableNum(Math.toIntExact(upLevel));
            dto.setTableId(tableId);
        }
        dto.setNextLayerTableNum(Math.toIntExact(upLevel));
        dto.setUpperStoryNum(Math.toIntExact(downLevel));
        return dto;
    }

    @Override
    public PageBean<TableBloodInfluenceDTO> getSpecifiedLevelTables(TableBloodSpecifiedLevelQuery param) {
        String tableName = param.getTableName();
        Integer level = param.getLevel();
        Long tableId = param.getTableId();
        String tenantId = param.getTenantId();
        Boolean isUpStream = param.getIsUpStream();
        Integer jobStatus = param.getJobStatus();
        Integer page = param.getPage();
        Integer size = param.getSize();
        PageBean<TableBloodInfluenceDTO> pageVo = new PageBean<>();
        pageVo.setSize(size);
        pageVo.setNumber(page);
        pageVo.setTotalElements(0);
        pageVo.setNumberOfElements(0);
        pageVo.setTotalPages(0);
        // 判断是否模糊查询
        boolean tableNameFlag = false;
        if (StringUtils.isNotBlank(tableName)) {
            tableNameFlag = true;
        }
        Map<Long, Edge> totalEdgesMap = new HashMap<>();
        Map<Long, Node> totalNodesMap = new HashMap<>();
        List<NodeLevelDTO> nodeLevelDtos = null;
        if (tableNameFlag) {
            // 获取元数据符合要求的所有表Id
            List<Long> totalTableIds = getMetadataTableIds(tenantId, tableName);
            log.info("name-getSpecifiedLevelTables by name getTotalTableIds = ", totalTableIds);
            if(CollectionUtil.isEmpty(totalTableIds)) {
                return pageVo;
            }
            // 根据元数据表Id,从redisgraph获取所有符合要求的顶点
            ResultSet allMetaNodeSet = getNodes(tenantId, totalTableIds, null, null);
            List<Node> metaNodeList = BloodUtil.parseResultSetGetNodes(allMetaNodeSet, "s", jobStatus);
            if (CollectionUtils.isEmpty(metaNodeList)) {
                return pageVo;
            }
            // 查询全部关系和顶点，这一步主要是获取他们之间的关系
            ResultSet allNodeRelGraphSet = getTableBloodVariableLength(tableId, tenantId, isUpStream, 1, level);
            log.info("getSpecifiedLevelTables get relation result={}", allNodeRelGraphSet);
            // 根据job状态筛选符合的关系和顶点
            BloodUtil.parseResultSet(allNodeRelGraphSet, "p", totalNodesMap, totalEdgesMap, jobStatus);
            if (CollectionUtils.isEmpty(totalNodesMap)) {
                return pageVo;
            }
            // 从redisgraph中获取所有job状态符合的表的id
            List<Long> allGraphNodeIds = totalNodesMap.values().stream().map(t -> {
                return (Long) t.getProperty("id").getValue();
            }).collect(Collectors.toList());

            // 从元数据相关获取全部可用的顶点的tableId, 两者取交集
            List<Long> ensureIds = metaNodeList.stream().map(n -> {
                return (Long) n.getProperty("id").getValue();
            }).filter(eId -> allGraphNodeIds.contains(eId)).collect(Collectors.toList());

            // 根据符合要求的tableId 进行分页
            ResultSet pageNodeSet = getNodes(tenantId, ensureIds, size*(page-1), size);
            List<Node> pageNodeList = BloodUtil.parseResultSetGetNodes(pageNodeSet, "s", jobStatus);
            if (CollectionUtils.isEmpty(metaNodeList)) {
                return pageVo;
            }
            int totalCount = ensureIds.size();
            pageVo.setTotalElements(totalCount);
            pageVo.setNumberOfElements(pageNodeList.size());
            int totalPages = totalCount % size == 0 ? totalCount / size : totalCount / size + 1;
            pageVo.setTotalPages(totalPages);

            // 获取顶点之间的层级关系
            // 解析获取到层级数
            Map<Long, NodeLevelDTO> bloodLevelMap = BloodUtil.parseResultSetGetNodesAndLevel(allNodeRelGraphSet, "p", 1);
            List<Long> nodeIds = pageNodeList.stream().map(n -> {
                return n.getId();
            }).collect(Collectors.toList());
            // 匹配符合要求的层级， bloodLevelMap.values()里面包含nodeList所有顶点
            nodeLevelDtos = bloodLevelMap.values().stream().filter(b->nodeIds.contains(b.getNode().getId())).collect(Collectors.toList());
        } else {
            // 获取level跳内的所有顶点
            Integer upLevel = null;
            Integer downLevel = null;
            if (null != level) {
                if (level < 1) {
                    return pageVo;
                } else if(level == 1){
                    upLevel = downLevel = 1;
                } else if(level >1) {
                    upLevel = level - 1;
                    downLevel = level;
                }
            }
            // 这一步主要是获取最长链路
            ResultSet upResultSet = getTableBloodVariableLength(tableId, tenantId, isUpStream, upLevel, downLevel);
            log.info("level-getSpecifiedLevelTables get relation result={}", upResultSet);
            BloodUtil.parseResultSet(upResultSet, "p", totalNodesMap, totalEdgesMap, jobStatus);
            // 解析出层级
            Map<Long, NodeLevelDTO> bloodLevelMap = BloodUtil.parseResultSetGetNodesAndLevel(upResultSet, "p", jobStatus);
            if (CollectionUtils.isEmpty(bloodLevelMap)) {
                return pageVo;
            }
            List<Long> allNodeIds = new ArrayList<>();
            bloodLevelMap.forEach((k,v)->{
                Set<Integer> levels = v.getLevels();
                if (level != null) {
                    // 根据层级查询，层级符合就添加
                    if (levels.contains(level)){
                        allNodeIds.add((Long)v.getNode().getProperty("id").getValue());
                    }
                } else{
                    // 不根据层级查询，所有的都符合
                    allNodeIds.add((Long)v.getNode().getProperty("id").getValue());
                }
            });

            // 此处分页
            Integer totalCount = allNodeIds.size();
            ResultSet tableVariableLength = getNodes(tenantId, allNodeIds, size * (page - 1), size);
            List<Node> levelNodeList = BloodUtil.parseResultSetGetNodes(tableVariableLength, "s", null);
            Integer numOfElements = 0;
            if (!CollectionUtils.isEmpty(levelNodeList)) {
                // 带层级查询，所有数据属于同一层
                if (null != level) {
                    nodeLevelDtos = levelNodeList.stream().map(n->{
                        NodeLevelDTO nodeLevelDTO = new NodeLevelDTO();
                        Set set = new HashSet<>(1);
                        set.add(level);
                        nodeLevelDTO.setLevels(set);
                        nodeLevelDTO.setNode(n);
                        return nodeLevelDTO;
                    }).collect(Collectors.toList());
                    numOfElements = levelNodeList.size();
                } else {
                    // 不带层级查询
                    List<Long> levelNodeIds = levelNodeList.stream().map(lNode -> {
                        return lNode.getId();
                    }).collect(Collectors.toList());
                    nodeLevelDtos = bloodLevelMap.values().stream().filter(b->levelNodeIds.contains(b.getNode().getId())).collect(Collectors.toList());
                    numOfElements = nodeLevelDtos.size();
                }
            }
            pageVo.setNumberOfElements(numOfElements);
            pageVo.setTotalElements(totalCount);
            int totalPages = totalCount % size == 0 ? totalCount / size : totalCount / size + 1;
            pageVo.setTotalPages(totalPages);
        }

        if (!CollectionUtils.isEmpty(nodeLevelDtos)) {
            List<TableBloodInfluenceDTO> influenceDtos = nodeLevelDtos.stream().map(n->{
                Node node = n.getNode();
                Edge nearJob = getNearJob(totalEdgesMap, node.getId(), true);
                TableBloodInfluenceDTO dto = new TableBloodInfluenceDTO();
                if (null != nearJob) {
                    dto.setJobId((Long)nearJob.getProperty("jobId").getValue());
                    String projectCode = null == nearJob.getProperty("projectCode") ? null : (String)nearJob.getProperty("projectCode").getValue();
                    Long jobId = (Long)nearJob.getProperty("jobId").getValue();
                    Long projectId = null == nearJob.getProperty("projectId")? null : (Long)nearJob.getProperty("projectId").getValue();
                    String jobName = (String)nearJob.getProperty("jobName").getValue();
                    String queryTenantId = (String)nearJob.getProperty("tenantId").getValue();
                    TaskDetailVO taskDetail = developTaskBizService.getTaskDetail(queryTenantId, projectCode, projectId, jobId, jobName);
                    dto.setJobName(null == taskDetail ? (String)nearJob.getProperty("jobName").getValue() : taskDetail.getJobName());
                }
                dto.setLevel(n.getLevels().iterator().next());
                Long id = (Long) node.getProperty("id").getValue();
                String[] includeFields = { "id", "tableName", "owner" };
                TableMetadataDTO tableMetaDate = tableMetadataBizService.getTableMetaDate(id, tenantId, includeFields, esTimeout);
                dto.setTableName(tableMetaDate.getTableName());
                dto.setOwner(tableMetaDate.getOwner());
                dto.setTableId(id);
                dto.setBaseTableId(tableId);
                dto.setJobStatus(jobStatus);
                return dto;
            }).collect(Collectors.toList());
            pageVo.setContent(influenceDtos);
        }
        return pageVo;
    }

    /**
     * 顶点分页
     * @param tenantId
     * @param tableIds
     * @param startIndex
     * @param size
     * @return
     */
    private ResultSet getNodes(String tenantId, List<Long> tableIds, Integer startIndex, Integer size) {
        StringBuilder querySql = new StringBuilder();
        querySql.append("MATCH (s:").append(BloodConstants.TABLE_NODE).append(")").append(" WHERE s.tenantId='").append(tenantId).append("'");
        if (!CollectionUtils.isEmpty(tableIds)) {
            querySql.append(" AND s.id IN ").append(tableIds);
        }
        querySql.append(" RETURN s");
        if (null != startIndex && null != size) {
           querySql.append(" ORDER BY s.crtTime DESC").append(" SKIP ").append(startIndex).append(" LIMIT ").append(size);
        }
        log.info("query variable length statement input source for RedisGraph: {}", querySql.toString());
        return redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, querySql.toString());
    }

    /**
     * 获取关系中最近的一个关系
     * @param edgesMap
     * @param nodeId
     * @param isUpstream null -上下游，true-上游，false-下游
     * @return
     */
    private Edge getNearJob(Map<Long, Edge> edgesMap, Long nodeId, Boolean isUpstream){
        List<Edge> edges = new ArrayList<>();
        if (null == isUpstream) {
            edges = edgesMap.values().stream().filter(v -> nodeId == v.getSource() || nodeId == v.getDestination()).collect(Collectors.toList());
        } else if (isUpstream) {
            edges = edgesMap.values().stream().filter(v -> nodeId == v.getDestination()).collect(Collectors.toList());
        } else {
            edges = edgesMap.values().stream().filter(v -> nodeId == v.getSource()).collect(Collectors.toList());
        }
        // 获取最近完成的job
        Edge nearEdge = null;
        Long currentCrtTime = 0L;
        for(Edge e : edges) {
            Long crtTime = (Long) e.getProperty("crtTime").getValue();
            if (crtTime > currentCrtTime) {
                currentCrtTime = crtTime;
                nearEdge = e;
            }
        }
        return nearEdge;
    }
    /**
     * 获取关系中所有关系
     * @param edgesMap
     * @param nodeId
     * @param isUpstream null -上下游，true-上游，false-下游
     * @return
     */
    private List<Edge> getJobs(Map<Long, Edge> edgesMap, Long nodeId, Boolean isUpstream){
        List<Edge> edges = new ArrayList<>();
        if (null == isUpstream) {
            edges = edgesMap.values().stream().filter(v -> nodeId == v.getSource() || nodeId == v.getDestination()).collect(Collectors.toList());
        } else if (isUpstream) {
            edges = edgesMap.values().stream().filter(v -> nodeId == v.getDestination()).collect(Collectors.toList());
        } else {
            edges = edgesMap.values().stream().filter(v -> nodeId == v.getSource()).collect(Collectors.toList());
        }
        return edges;
    }

    @Override
    public TableBloodLevelNumDTO getTableLevelNum(TableBloodLevelNumQuery param) {
        Long tableId = param.getTableId();
        String tenantId = param.getTenantId();
        TableBloodLevelNumDTO dto = new TableBloodLevelNumDTO();
        Integer upLevelNum = null;
        Integer downLevelNum = null;
        if (null == param.getIsUpStream()) {
            ResultSet upResultSet = getTableBloodVariableLength(tableId, tenantId, true, null, null);
            upLevelNum = BloodUtil.parseResultSetGetLevel(upResultSet, "p", 1);
            ResultSet downResultSet = getTableBloodVariableLength(tableId, tenantId, false, null, null);
            downLevelNum = BloodUtil.parseResultSetGetLevel(downResultSet, "p", 1);
        }else if (param.getIsUpStream()) {
            ResultSet upResultSet = getTableBloodVariableLength(tableId, tenantId, true, null, null);
            upLevelNum = BloodUtil.parseResultSetGetLevel(upResultSet, "p", 1);
        } else  {
            ResultSet downResultSet = getTableBloodVariableLength(tableId, tenantId, false, null, null);
            downLevelNum = BloodUtil.parseResultSetGetLevel(downResultSet, "p", 1);
        }
        dto.setTableId(tableId);
        dto.setUpStreamLevelNum(upLevelNum);
        dto.setDownStreamLevelNum(downLevelNum);
        return dto;
    }

    @Override
    public PageBean<TableBloodJobDTO> getTableBloodJobs(TableBloodJobQuery param) {
        Long tableId = param.getTableId();
        Boolean isUpstream = param.getIsUpstream();
        String tenantId = param.getTenantId();
        Integer jobStatus = param.getJobStatus();
        Integer page = param.getPage();
        Integer size = param.getSize();
        PageBean<TableBloodJobDTO> pageJob = new PageBean<>();
        pageJob.setNumber(page);
        pageJob.setSize(size);
        pageJob.setTotalPages(0);
        pageJob.setContent(new ArrayList<>());
        pageJob.setNumberOfElements(0);

        ResultSet allResult = getTableBloodVariableLength(param.getBaseTableId(), tenantId, !isUpstream, null, null);
        Map<Long, Node> nodesMap = new HashMap<Long, Node>();
        Map<Long, Edge> edgesMap = new HashMap<Long, Edge>();
        BloodUtil.parseResultSet(allResult, "p", nodesMap, edgesMap,jobStatus);
        if (CollectionUtil.isEmpty(edgesMap) || CollectionUtil.isEmpty(nodesMap)) {
            return pageJob;
        }
        Node currentNode = nodesMap.values().stream().filter(n -> tableId.equals((Long) n.getProperty("id").getValue())).findFirst().orElse(null);
        if (null == currentNode) {
            return pageJob;
        }
        List<Edge> edges = edgesMap.values().stream().filter(e -> e.getDestination() == currentNode.getId()).collect(Collectors.toList());
        Set<Long> allJobIds = edges.stream().map(e -> {
            return (Long) e.getProperty("jobId").getValue();
        }).collect(Collectors.toSet());
        // 先根据jobId分组查询
        ResultSet tableJobIdResult = getTableJobIds(tableId, tenantId, new ArrayList<>(allJobIds), isUpstream, jobStatus,(page-1)*size, param.getSize(), false);
        if (null == tableJobIdResult) {
            return pageJob;
        }
        List<Long> jobIds = new ArrayList<>();
        while(tableJobIdResult.hasNext()){
            Record record = tableJobIdResult.next();
            Long jobId = (Long)record.getValue("jobId");
            jobIds.add(jobId);
        }
        // 查出所有符合要求的
        ResultSet tableJobs = getTableJobs(tableId, tenantId, isUpstream, jobStatus,null, null, null);
        log.info("getTableBloodJobs result={}", tableJobs);
        if (null == tableJobs) {
            return pageJob;
        }
        List<Edge> sureEdges = BloodUtil.parseResultSetGetEdges(tableJobs, "r");
        if (CollectionUtils.isEmpty(edges)) {
            return pageJob;
        }
        ArrayList<Edge> sureJobs = sureEdges.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(e -> (Long) e.getProperty("jobId").getValue()))), ArrayList::new));
        List<Edge> jobResultList = sureJobs.stream().filter(a -> jobIds.contains((Long) a.getProperty("jobId").getValue())).collect(Collectors.toList());
        List<TableBloodJobDTO> collect = jobResultList.stream().map(e -> {
            String projectCode = (String) e.getProperty("projectCode").getValue();
            Long projectId = (Long) e.getProperty("projectId").getValue();
            String jobName = (String) e.getProperty("jobName").getValue();
            Long jobId = (Long)e.getProperty("jobId").getValue();
            TableBloodJobDTO vo = new TableBloodJobDTO();
            vo.setJobId(jobId);
            vo.setJobName(jobName);
            vo.setJobStatus(Math.toIntExact((Long)e.getProperty("status").getValue()));
            vo.setProjectId(projectId);
            vo.setProjectCode(projectCode);
            // 获取最新实例
            TaskDetailVO taskDetail = developTaskBizService.getTaskDetail(tenantId, projectCode, projectId, jobId, jobName);
            if (null != taskDetail) {
                vo.setProcessInstanceId(taskDetail.getProcessInstanceId());
                vo.setInstanceId(taskDetail.getInstanceId());
                Date startTime = taskDetail.getStartTime();
                Date endTime = taskDetail.getEndTime();
                vo.setStartTime(startTime);
                vo.setEndTime(endTime);
                if (null != startTime && null != endTime) {
                    vo.setTimeConsuming((endTime.getTime() - startTime.getTime())/1000);
                }
            }
            return vo;
        }).collect(Collectors.toList());
        pageJob.setContent(collect);
        int totalElements = allJobIds.size();
        pageJob.setTotalElements(totalElements);
        int totalPages = totalElements % size == 0 ? totalElements / size : totalElements / size + 1;
        pageJob.setTotalPages(totalPages);

        pageJob.setNumberOfElements(collect.size());
        return pageJob;
    }

    @Override
    public PageBean<TableBloodJobDTO> getTableBloodOutput(TableBloodOutputQuery param) {
        Long tableId = param.getTableId();
        String tenantId = param.getTenantId();
        Integer jobStatus = param.getJobStatus();
        Integer page = param.getPage();
        Integer size = param.getSize();
        PageBean<TableBloodJobDTO> pageJob = new PageBean<>();
        pageJob.setNumber(page);
        pageJob.setSize(size);
        pageJob.setTotalPages(0);
        pageJob.setContent(new ArrayList<>());
        pageJob.setNumberOfElements(0);

        // 查出所有符合要求的
        ResultSet tableJobs = getTableJobs(tableId, tenantId, true, jobStatus,null, null, null);
        log.info("getTableBloodJobs result={}", tableJobs);
        if (null == tableJobs) {
            return pageJob;
        }
        List<Edge> edges = BloodUtil.parseResultSetGetEdges(tableJobs, "r");
        if (CollectionUtils.isEmpty(edges)) {
            return pageJob;
        }
        ArrayList<Edge> allJobs = edges.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(e -> (Long) e.getProperty("jobId").getValue()))), ArrayList::new));
        List<Long> jobIds = allJobs.stream().map(a -> {
            return (Long)a.getProperty("jobId").getValue();
        }).collect(Collectors.toList());

        Edge edge = edges.get(0);
        String projectCode = (String) edge.getProperty("projectCode").getValue();
        Long projectId = (Long) edge.getProperty("projectId").getValue();
        // 获取最新实例
        PageBean<TaskDetailDTO> taskDetails = developTaskBizService.getTaskDetails(tenantId, projectCode, projectId, jobIds, param.getPage(), param.getSize());
        if (!BeanUtil.isEmpty(taskDetails)) {
            List<TableBloodJobDTO> collect = taskDetails.getContent().stream().map(t -> {
                TableBloodJobDTO vo1 = new TableBloodJobDTO();
                BeanUtil.copyProperties(t, vo1);
                Edge currentJob = edges.stream().filter(e -> t.getJobId().equals((Long) e.getProperty("jobId").getValue())).findAny().orElseGet(null);
                if(null != currentJob) {
                    vo1.setJobName((String)currentJob.getProperty("jobName").getValue());
                    vo1.setProjectId((Long)currentJob.getProperty("projectId").getValue());
                    vo1.setProjectCode((String)currentJob.getProperty("projectCode").getValue());
                }
                return vo1;
            }).collect(Collectors.toList());
            pageJob.setContent(collect);
            pageJob.setTotalPages(taskDetails.getTotalPages());
            pageJob.setTotalElements(taskDetails.getTotalElements());
            pageJob.setNumberOfElements(taskDetails.getNumberOfElements());
        }
        return pageJob;
    }

    @Override
    public void downloadTableBloodInfluence(HttpServletResponse response, TableBloodInfluenceDownloadQuery param) {
        String tableName = param.getTableName();
        Integer level = param.getLevel();
        Long tableId = param.getTableId();
        String tenantId = param.getTenantId();
        Integer jobStatus = param.getJobStatus();
        List<TableBloodInfluenceDownLoadDTO> influenceDtos = new ArrayList<>();
        // 判断是否模糊查询
        boolean tableNameFind = false;
        if (StringUtils.isNotBlank(tableName)) {
            tableNameFind = true;
        }
        Map<Long, Node> totalNodesMap = new HashMap<>();
        Map<Long, Edge> edgesMap = new HashMap<>();
        List<NodeLevelDTO> nodeLevelDtos = new ArrayList<>();
        if (tableNameFind) {
            List<Long> totalTableIds = getMetadataTableIds(tenantId, tableName);
            // 获取所有符合要求的顶点
            ResultSet ensureNodeSet = getNodes(tenantId, totalTableIds, null, null);
            List<Node> ensureNodeList = BloodUtil.parseResultSetGetNodes(ensureNodeSet, "s", jobStatus);
            if (!CollectionUtils.isEmpty(ensureNodeList)) {
                // 找到顶点间关系
                ResultSet allNodeRelSet = getTableBloodVariableLength(tableId, tenantId, false, null, null);

                // 根据job状态筛选符合的关系和顶点
                // 获取job status下所有顶点
                BloodUtil.parseResultSet(allNodeRelSet, "p", totalNodesMap, edgesMap, jobStatus);
                List<Node> nodeList = totalNodesMap.values().stream().collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(nodeList)) {
                    Map<Long, NodeLevelDTO> bloodLevelMap = BloodUtil.parseResultSetGetNodesAndLevel(allNodeRelSet, "p", 1);
                    List<Long> ensureIds = ensureNodeList.stream().map(n -> {
                        return n.getId();
                    }).collect(Collectors.toList());
                    List<Long> nodeListIds = nodeList.stream().map(n -> {
                        return n.getId();
                    }).collect(Collectors.toList());
                    List<Long> nodeIds = nodeListIds.stream().filter(n->ensureIds.contains(n)).collect(Collectors.toList());
                    // 匹配符合要求的层级
                    nodeLevelDtos = bloodLevelMap.values().stream().filter(b->nodeIds.contains(b.getNode().getId())).collect(Collectors.toList());
                }
            }
        } else {
            // 获取level跳内的所有顶点
            Integer upLevel = null;
            Integer downLevel = null;
            if (null != level) {
                if (level < 1) {
                    return ;
                } else if(level == 1){
                    upLevel = downLevel = 1;
                } else if(level >1) {
                    upLevel = level - 1;
                    downLevel = level;
                }
            }
            ResultSet upResultSet = getTableBloodVariableLength(tableId, tenantId, false, upLevel, downLevel);
            BloodUtil.parseResultSet(upResultSet, "p", totalNodesMap, edgesMap, jobStatus);
            // 解析出层级
            Map<Long, NodeLevelDTO> bloodLevelMap = BloodUtil.parseResultSetGetNodesAndLevel(upResultSet, "p", jobStatus);
            List<NodeLevelDTO> finalNodeLevelDtos = nodeLevelDtos;
            if (CollectionUtil.isNotEmpty(bloodLevelMap)) {
                bloodLevelMap.forEach((k, v)->{
                    Set<Integer> levels = v.getLevels();
                    if (level != null) {
                        // 根据层级查询，层级符合就添加
                        if (levels.contains(level)){
                            finalNodeLevelDtos.add(v);
                        }
                    } else{
                        // 不根据层级查询，所有的都符合
                        finalNodeLevelDtos.add(v);
                    }
                });
            }
        }

        if (!CollectionUtils.isEmpty(nodeLevelDtos)) {
            List<TableBloodInfluenceDownLoadDTO> influenceDtosTemp = new ArrayList<>();
            nodeLevelDtos.forEach(n->{
                Node node = n.getNode();
                List<Edge> jobs = getJobs(edgesMap, node.getId(), true);
                if(CollectionUtils.isEmpty(jobs)) {
                    Set<Integer> levels = n.getLevels();
                    levels.forEach(lev->{
                        TableBloodInfluenceDownLoadDTO dto = new TableBloodInfluenceDownLoadDTO();
                        dto.setLevel(lev);
                        Long id = (Long) node.getProperty("id").getValue();
                        String[] includeFields = { "id", "tableName", "owner" };
                        TableMetadataDTO tableMetaDate = tableMetadataBizService.getTableMetaDate(id, tenantId, includeFields, esTimeout);
                        dto.setTableName(tableMetaDate.getTableName());
                        dto.setOwner(tableMetaDate.getOwner());
                        influenceDtosTemp.add(dto);
                    });
                } else {
                    ArrayList<Edge> disJobs = jobs.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(e -> (Long) e.getProperty("jobId").getValue()))), ArrayList::new));
                    Set<Integer> levels = n.getLevels();
                    disJobs.forEach(j->{
                        Long id = (Long) node.getProperty("id").getValue();
                        String[] includeFields = { "id", "tableName", "owner" };
                        TableMetadataDTO tableMetaDate = tableMetadataBizService.getTableMetaDate(id, tenantId, includeFields, esTimeout);
                        levels.forEach(lev->{
                            TableBloodInfluenceDownLoadDTO dto = new TableBloodInfluenceDownLoadDTO();
                            dto.setLevel(lev);
                            dto.setTableName(tableMetaDate.getTableName());
                            dto.setOwner(tableMetaDate.getOwner());
                            Long jobId = (Long) j.getProperty("jobId").getValue();
                            dto.setJobId(jobId);
                            Long jobStatusCurrent = (Long) j.getProperty("status").getValue();
                            if (0 == jobStatusCurrent) {
                                dto.setJobStatusStr("下线");
                            } else if(1 == jobStatusCurrent){
                                dto.setJobStatusStr("在线");
                            }
                            String projectCode = null == j.getProperty("projectCode") ? null : (String)j.getProperty("projectCode").getValue();
                            Long projectId = null == j.getProperty("projectId")? null : (Long)j.getProperty("projectId").getValue();
                            String jobName = (String)j.getProperty("jobName").getValue();
                            String queryTenantId = (String)j.getProperty("tenantId").getValue();
                            TaskDetailVO taskDetail = developTaskBizService.getTaskDetail(queryTenantId, projectCode, projectId, jobId, jobName);
                            dto.setJobName(null == taskDetail ? (String)j.getProperty("jobName").getValue() : taskDetail.getJobName());
                            influenceDtosTemp.add(dto);
                        });
                    });
                }
            });
            // 按照层级排序
            influenceDtos = influenceDtosTemp.stream().sorted(Comparator.comparing(TableBloodInfluenceDownLoadDTO::getLevel)).collect(Collectors.toList());
        }
        String[] headers = { "血缘层级", "表名", "owner", "产出任务名称", "产出任务ID" , "任务状态"};
        DownloadExcelUtils.downloadExcel(response, influenceDtos, headers, "血缘影响分析");
    }

    @Override
    public JobInstanceLogDTO getInstanceLog(JobLatestLogQuery query) {
        JobInstanceLogDTO vo = null;
        CommonEntity commonEntity = new CommonEntity<CommonEntity>(query.getTenantId(),
                query.getProjectId().toString());
        commonEntity.setSkipLineNum(0);
        commonEntity.setPageSize(10000);
        commonEntity.setProjectId(query.getProjectId().toString());
        commonEntity.setProjectCode(query.getProjectCode());
        commonEntity.setTaskInstanceId(String.valueOf(query.getInstanceId()));
        commonEntity.setTenantId(query.getTenantId());
        if (null != query.getProcessInstanceId()) {
            commonEntity.setProcessInstanceId(String.valueOf(query.getProcessInstanceId()));
        }
        commonEntity.setResourceEnvironmentEnum(ResourceEnvironmentEnum.PRD);
        InternalPayload<LogPagingDTO> taskLog = null;
        try {
            taskLog = taskExecuteApi.getTaskLog(commonEntity);
            log.info("getTaskLog result={}", taskLog);
        } catch (Exception e) {
            log.error("get taskLog failed. message={}", e.getMessage());
        }
        if (null != taskLog) {
            LogPagingDTO logPagingDTO = taskLog.getPayload();
            vo = new JobInstanceLogDTO();
            vo.setLogData(null != logPagingDTO ? logPagingDTO.getData() : null);
        }
        return vo;
    }

    /**
     * 获取表相关的job
     * @param id 表id
     * @param tenantId 租户id
     * @param isUpstream 是否上游，true-是， false-否
     * @param startIndex 开始位置
     * @param size 查询数量
     * @param crtTimeAscFlag 根据crtTime true-正序，false-逆序
     * @return
     */
    private ResultSet getTableJobs(Long id, String tenantId, Boolean isUpstream, Integer jobStatus, Integer startIndex, Integer size, Boolean crtTimeAscFlag) {
        StringBuilder querySql = new StringBuilder();
        querySql.append("MATCH (s:").append(BloodConstants.TABLE_NODE).append("{tenantId:'").append(tenantId).append("'})-[r:").append(BloodConstants.TABLE_REL)
                .append("{tenantId:'").append(tenantId).append("'");
        if (null != jobStatus) {
            querySql.append(", status:").append(jobStatus);
        }
        querySql.append("}]->(t:").append(BloodConstants.TABLE_NODE).append("{tenantId:'").append(tenantId).append("'}) ");
        if (isUpstream) {
            // 查询上游
            querySql.append("WHERE t.id=").append(id);
        } else {
            // 查询下游
            querySql.append("WHERE s.id=").append(id);
        }
        querySql.append(" RETURN r");
        if (null != crtTimeAscFlag) {
            querySql.append(" ORDER BY r.crtTime ");
            if (crtTimeAscFlag) {
                querySql.append("ASC");
            }else {
                querySql.append("DESC");
            }
        }
        if (null != startIndex && null != size) {
            querySql.append(" SKIP ").append(startIndex).append(" LIMIT ").append(size);
        }
        log.info("query table blood job for RedisGraph: {}", querySql.toString());
        return redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, querySql.toString());
    }
    /**
     * 获取表相关的jobId,主要是用于分组
     * @param id 表id
     * @param tenantId 租户id
     * @param isUpstream 是否上游，true-是， false-否
     * @param startIndex 开始位置
     * @param size 查询数量
     * @param crtTimeAscFlag 根据crtTime true-正序，false-逆序
     * @return
     */
    private ResultSet getTableJobIds(Long id, String tenantId, List<Long> jobIds, Boolean isUpstream, Integer jobStatus, Integer startIndex, Integer size, Boolean crtTimeAscFlag) {
        StringBuilder querySql = new StringBuilder();
        querySql.append("MATCH (s:").append(BloodConstants.TABLE_NODE).append("{tenantId:'").append(tenantId).append("'})-[r:").append(BloodConstants.TABLE_REL)
                .append("{tenantId:'").append(tenantId).append("'");
        if (null != jobStatus) {
            querySql.append(", status:").append(jobStatus);
        }
        querySql.append("}]->(t:").append(BloodConstants.TABLE_NODE).append("{tenantId:'").append(tenantId).append("'}) ");
        if (isUpstream) {
            // 查询上游
            querySql.append("WHERE t.id=").append(id);
        } else {
            // 查询下游
            querySql.append("WHERE s.id=").append(id);
        }
        if (CollectionUtil.isNotEmpty(jobIds)) {
            querySql.append(" AND r.jobId IN ").append(jobIds);
        }
        querySql.append(" RETURN r.jobId AS jobId, collect(r.jobId) AS jobIds");
        if (null != crtTimeAscFlag) {
            querySql.append(" ORDER BY r.crtTime ");
            if (crtTimeAscFlag) {
                querySql.append("ASC");
            }else {
                querySql.append("DESC");
            }
        }
        if (null != startIndex && null != size) {
            querySql.append(" SKIP ").append(startIndex).append(" LIMIT ").append(size);
        }
        log.info("query table blood job for RedisGraph: {}", querySql.toString());
        return redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, querySql.toString());
    }

    private List<Long> getMetadataTableIds(String tenantId, String tableName){
        String[] includeFields = { "id", "tableName"};
        Map<String, Object> mustWhere = new HashMap<String, Object>(4);
        mustWhere.put("tenantId", tenantId);
        mustWhere.put("isDeleted", 0);
        if (StringUtils.isNotBlank(tableName)) {
            Map<String, Object> shouldWhere = new HashMap<String, Object>();
            shouldWhere.put("tableName", tableName);
            mustWhere.put("should-1", shouldWhere);
        }
        Map<String, Boolean> sortFieldsToAsc = new HashMap<String, Boolean>();
        sortFieldsToAsc.put("createdTime", false);
        sortFieldsToAsc.put("updatedTime", false);
        List<TableMetadataDTO> tables = commonEsBizService.searchIndex("table_metadata_index", mustWhere, null, sortFieldsToAsc,
                includeFields, null, esTimeout, TableMetadataDTO.class);
        if (CollectionUtil.isNotEmpty(tables)){
            List<Long> totalTableIds = tables.stream().map(t -> {
                return Long.parseLong(t.getId());
            }).collect(Collectors.toList());
            return totalTableIds;
        }
        return null;
    }
}
