package com.ldw.microservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.deepexi.data.metadata.biz.ColumnBloodBizService;
import com.deepexi.data.metadata.biz.ColumnMetadataBizService;
import com.deepexi.data.metadata.biz.DevelopTaskBizService;
import com.deepexi.data.metadata.biz.TableMetadataBizService;
import com.deepexi.data.metadata.constant.BloodConstants;
import com.deepexi.data.metadata.domain.dto.*;
import com.deepexi.data.metadata.domain.dto.blood.*;
import com.deepexi.data.metadata.domain.query.BloodQuery;
import com.deepexi.data.metadata.domain.query.DetailQuery;
import com.deepexi.data.metadata.domain.query.EdgeQuery;
import com.deepexi.data.metadata.domain.query.NodeQuery;
import com.deepexi.data.metadata.domain.query.blood.GetFieldBloodQuery;
import com.deepexi.data.metadata.domain.vo.blood.TaskDetailVO;
import com.deepexi.data.metadata.util.BloodUtil;
import com.deepexi.util.CollectionUtil;
import com.redislabs.redisgraph.ResultSet;
import com.redislabs.redisgraph.graph_entities.Edge;
import com.redislabs.redisgraph.graph_entities.Node;
import com.redislabs.redisgraph.impl.api.RedisGraph;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/3 11:00
 * @Description 字段血缘service
 */
@Slf4j
@Service
public class ColumnBloodBizServiceImpl implements ColumnBloodBizService {

    @Autowired
    private RedisGraph redisGraph;
    @Autowired
    private ColumnMetadataBizService columnMetadataBizService;
    @Autowired
    private TableMetadataBizService tableMetadataBizService;
    @Autowired
    private DevelopTaskBizService developTaskBizService;
    @Value("${es.timeout:30}")
    private Long esTimeout;

    @Override
    public void handleFieldBloodData(BloodQuery param) {
        String operation = param.getOperation();
        Integer operationType = param.getOperationType();
        List<NodeQuery> fieldNodes = param.getFieldNodes();
        List<EdgeQuery> edges = param.getEdges();
        List<DetailQuery> taskDetails = param.getTaskDetails();
        String tenantId = param.getTenantId();
        if (null == operationType && (StringUtils.isBlank(operation) || BloodConstants.BloodOperation.DEFAULT.equals(operation))){
            // 自适应处理
            commonAdapter(fieldNodes, edges, taskDetails, tenantId);
        }

        if (null != operationType) {
            if(0 == operationType) {
                // 顶点操作
                switch (operation) {
                    case BloodConstants.BloodOperation.ADD:
                        fieldNodesAdd(fieldNodes, tenantId);
                        break;
                    case BloodConstants.BloodOperation.PUT:
                        fieldNodesUpdate(fieldNodes, tenantId);
                        break;
                    case BloodConstants.BloodOperation.DEL:
                        fieldNodeDelete(fieldNodes, tenantId);
                        break;
                    default:
                        break;
                }
            } else if (1 == operationType){
                // 关系操作
                switch (operation) {
                    case BloodConstants.BloodOperation.ADD:
                        fieldEdgesAdd(fieldNodes, edges, taskDetails, tenantId);
                        break;
                    case BloodConstants.BloodOperation.PUT:
                        fieldEdgesUpdate(fieldNodes, edges, taskDetails, tenantId);
                        break;
                    case BloodConstants.BloodOperation.DEL:
                        fieldEdgeDelete(taskDetails, tenantId);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 当字段节点不存在时创建节点，存在使用已存在的节点，节点间关系不存在时创建关系，存在则使用当前关系
     * @param fieldNodes
     * @param edges
     * @param tenantId
     */
    void commonAdapter(List<NodeQuery> fieldNodes, List<EdgeQuery> edges, List<DetailQuery> taskDetails, String tenantId) {
        // 删除字段血缘关系
        delEdgeByJobId(taskDetails, tenantId);

        if (CollectionUtils.isEmpty(fieldNodes)) {
            log.info("field node adapter fail, reason: fieldnodes is empty");
            return;
        } else {
            if (CollectionUtils.isEmpty(edges)) {
                mergeFieldNode(fieldNodes, tenantId);
            } else {
                mergeFieldAll(fieldNodes, edges, taskDetails, tenantId);
            }
        }
    }

    /**
     * 删除顶点之间的关系
     * @param jobs
     * @param tenantId
     */
    public void delEdgeByJobId(List<DetailQuery> jobs, String tenantId){
        if(CollectionUtil.isEmpty(jobs)) {
            return;
        }
        jobs.stream().forEach(job->{
            StringBuilder querySql = new StringBuilder();
            querySql.append("MATCH (s:").append(BloodConstants.COLUMN_NODE).append("{tenantId:'").append(tenantId).append("'})-[r:")
                    .append(BloodConstants.COLUMN_REL).append("{jobId:").append(job.getJobId()).append(", tenantId:'").append(tenantId).append("'}]->(t:")
                    .append(BloodConstants.COLUMN_NODE).append("{tenantId:'").append(tenantId).append("'})").append(" DELETE r");
            log.info("delete column blood relation for RedisGraph: {}", querySql.toString());
            redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, querySql.toString());
        });
    }

    /**
     * 匹配字段节点
     * @param fieldId
     * @param tenantId
     */
    public ResultSet matchFieldNode(Long fieldId, String tenantId) {
        StringBuilder cypherSql = new StringBuilder();
        cypherSql.append("MATCH (f:").append(BloodConstants.COLUMN_NODE).append("{id:").append(fieldId).append(", tenantId:'").append(tenantId).append("'}) RETURN f");
        log.info("field blood match field node cypher sql :{}", cypherSql.toString());
        return redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, cypherSql.toString());
    }

    /**
     * 匹配修改或创建字段节点
     * @param fieldNodes
     * @param tenantId
     */
    public void mergeFieldNode(List<NodeQuery> fieldNodes, String tenantId) {
        if (CollectionUtils.isEmpty(fieldNodes)) {
            return;
        }
        fieldNodes.stream().forEach(n->{
            ColumnMetadataDTO column = columnMetadataBizService.getColumnMetadata(n.getFieldId(), tenantId, null);
            if (null == column || StringUtils.isBlank(column.getId())) {
                return;
            }
            StringBuilder cypherSql = new StringBuilder();
            cypherSql.append("MERGE (f:").append(BloodConstants.COLUMN_NODE).append("{id:").append(column.getId()).append(", tenantId:'").append(tenantId).append("'}) ON CREATE SET f.crtTime = ").append(System.currentTimeMillis()).append(", f.updateTime = ").append(System.currentTimeMillis()).append(" ON MATCH SET f.updateTime = ").append(System.currentTimeMillis()).append(" SET ");
            if (null != column.getDataSourceId()) {
                cypherSql.append("f.datasourceId=").append(column.getDataSourceId()).append(",");
            }
            if (StringUtils.isNotBlank(column.getDataSourceType())) {
                cypherSql.append("f.datasourceType='").append(column.getDataSourceType()).append("',");
            }
            if (null != column.getProjectId()) {
                cypherSql.append("f.projectId=").append(column.getProjectId()).append(",");
            }
            if (null != column.getTableMetadataId()) {
                cypherSql.append("f.tableId=").append(column.getTableMetadataId()).append(",");
            }
            cypherSql.append("f.fieldCode='").append(column.getColumnName());
            log.info("field blood merge field node cypher sql :{}", cypherSql.toString());
            redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, cypherSql.toString());
        });
    }
    /**
     * 匹配修改或创建字段节点
     * @param fieldNodes
     * @param tenantId
     */
    public void mergeFieldAll(List<NodeQuery> fieldNodes, List<EdgeQuery> edges, List<DetailQuery> taskDetails, String tenantId) {
        if (CollectionUtils.isEmpty(fieldNodes) || CollectionUtils.isEmpty(edges)) {
            return;
        }
        // 先删除jobId相关的边信息
        fieldEdgeDeleteByJobId(edges, tenantId);
        // 重新加入字段血缘关系
        edges.stream().forEach(e->{
            DetailQuery detailQuery = taskDetails.stream().filter(t -> t.getJobId().equals(e.getJobId())).findAny().orElse(new DetailQuery());

            NodeQuery sourceNodeParam = fieldNodes.stream().filter(n -> e.getSourceFieldId().equals(n.getFieldId())).findAny().orElse(null);
            NodeQuery targetNodeParam = fieldNodes.stream().filter(n -> e.getTargetFieldId().equals(n.getFieldId())).findAny().orElse(null);
            ColumnMetadataDTO columnSource= columnMetadataBizService.getColumnMetadata(sourceNodeParam.getFieldId(), tenantId, null);
            ColumnMetadataDTO columnTarget= columnMetadataBizService.getColumnMetadata(targetNodeParam.getFieldId(), tenantId, null);
            if ((null == columnSource || StringUtils.isBlank(columnSource.getId()) || (null == columnTarget || StringUtils.isBlank(columnTarget.getId())))) {
                log.info("columnSource or columnTarget is empty columnSource={}, columnTarget={}", columnSource, columnTarget);
                return;
            }

            StringBuilder cypherSql = new StringBuilder();
            cypherSql.append("MERGE (s:").append(BloodConstants.COLUMN_NODE).append("{id:").append(columnSource.getId()).append(", tenantId:'").append(tenantId).append("'}) ON CREATE SET s.crtTime = ").append(System.currentTimeMillis()).append(", s.updateTime = ").append(System.currentTimeMillis()).append(" ON MATCH SET s.updateTime = ").append(System.currentTimeMillis()).append(" SET ");
            if (null != columnSource.getDataSourceId()) {
                cypherSql.append("s.datasourceId=").append(columnSource.getDataSourceId()).append(",");
            }
            if (StringUtils.isNotBlank(columnSource.getDataSourceType())) {
                cypherSql.append("s.datasourceType='").append(columnSource.getDataSourceType()).append("',");
            }
            if (null != columnSource.getProjectId()) {
                cypherSql.append("s.projectId=").append(columnSource.getProjectId()).append(",");
            }
            if (null != columnSource.getTableMetadataId()) {
                cypherSql.append("s.tableId=").append(columnSource.getTableMetadataId()).append(",");
            }
            cypherSql.append("s.fieldCode='").append(columnSource.getColumnName()).append("'");

            cypherSql.append(" MERGE (t:").append(BloodConstants.COLUMN_NODE).append("{id:").append(columnTarget.getId()).append(", tenantId:'").append(tenantId).append("'}) ON CREATE SET t.crtTime = ").append(System.currentTimeMillis()).append(", t.updateTime = ").append(System.currentTimeMillis()).append(" ON MATCH SET t.updateTime = ").append(System.currentTimeMillis()).append(" SET ");
            if (null != columnTarget.getDataSourceId()) {
                cypherSql.append("t.datasourceId=").append(columnTarget.getDataSourceId()).append(",");
            }
            if (StringUtils.isNotBlank(columnTarget.getDataSourceType())) {
                cypherSql.append("t.datasourceType='").append(columnTarget.getDataSourceType()).append("',");
            }
            if (null != columnTarget.getProjectId()) {
                cypherSql.append("t.projectId=").append(columnTarget.getProjectId()).append(",");
            }
            if (null != columnTarget.getTableMetadataId()) {
                cypherSql.append("t.tableId=").append(columnTarget.getTableMetadataId()).append(",");
            }
            cypherSql.append("t.fieldCode='").append(columnTarget.getColumnName()).append("'");

            cypherSql.append(" MERGE (s)-[r:").append(BloodConstants.COLUMN_REL).append("{tenantId:'").append(tenantId).append("', jobId:").append(e.getJobId()).append("}]->(t) ON CREATE SET r.crtTime = ").append(System.currentTimeMillis()).append(", r.updateTime = ").append(System.currentTimeMillis()).append(" ON MATCH SET r.updateTime = ").append(System.currentTimeMillis()).append(" SET ");
            if (StringUtils.isNotBlank(detailQuery.getJobName())) {
                cypherSql.append("r.jobName='").append(detailQuery.getJobName()).append("',");
            }
            if (null != detailQuery.getProjectId()) {
                cypherSql.append("r.projectId = ").append(detailQuery.getProjectId()).append(",");
            }
            if (StringUtils.isNotBlank(detailQuery.getProjectCode())) {
                cypherSql.append("r.projectCode='").append(detailQuery.getProjectCode()).append("',");
            }
            Integer status = 1;
            if (null != detailQuery.getStatus()) {
                status = detailQuery.getStatus();
            }
            cypherSql.append("r.status=").append(status);
            log.info("field blood merge field node and relation cypher sql :{}", cypherSql.toString());
            redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, cypherSql.toString());
        });
    }

    /**
     * 添加血缘字段节点
     * @param tenantId
     * @param fieldNodes
     */
    public void fieldNodesAdd(List<NodeQuery> fieldNodes, String tenantId){
        if (CollectionUtils.isEmpty(fieldNodes)) {
            return;
        }

        fieldNodes.stream().forEach(f->{
            fieldNodeAdd(f, tenantId);
        });

    }
    /**
     * 添加血缘字段节点
     * @param tenantId
     * @param fieldNode
     */
    public void fieldNodeAdd(NodeQuery fieldNode, String tenantId){
        StringBuilder cypherSql = new StringBuilder();
        ColumnMetadataDTO columnMetadataDTO = columnMetadataBizService.getColumnMetadata(fieldNode.getFieldId(), tenantId, null);
        if (null == columnMetadataDTO || StringUtils.isBlank(columnMetadataDTO.getId())) {
            log.error("columnBlood add node get column metadata failed. columnId={}", fieldNode.getFieldId());
            return;
        }
        cypherSql.append("CREATE (:").append(BloodConstants.COLUMN_NODE).append("{tenantId:'").append(tenantId).append("', id:").append(columnMetadataDTO.getId()).append(",");
        if (StringUtils.isNotBlank(columnMetadataDTO.getColumnName())) {
            cypherSql.append(", fieldCode:'").append(columnMetadataDTO.getColumnName()).append("',");
        }
        if (null != columnMetadataDTO.getDataSourceId()) {
            cypherSql.append("datasourceId:").append(columnMetadataDTO.getDataSourceId()).append(",");
        }
        if (StringUtils.isNotBlank(columnMetadataDTO.getDataSourceType())) {
            cypherSql.append("datasourceType:'").append(columnMetadataDTO.getDataSourceType()).append("',");
        }
        if (null != columnMetadataDTO.getProjectId()) {
            cypherSql.append("projectId:").append(columnMetadataDTO.getProjectId()).append(",");
        }
        if (null != columnMetadataDTO.getTableMetadataId()) {
            cypherSql.append("tableId:").append(columnMetadataDTO.getTableMetadataId()).append(",");
        }
        cypherSql.append("crtTime:").append(System.currentTimeMillis()).append(", updateTime:").append(System.currentTimeMillis()).append("})");
        log.info("field blood create node cypher sql :{}", cypherSql.toString());
        redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, cypherSql.toString());
    }

    /**
     * 修改字段血缘节点
     * @param tenantId
     * @param fieldNodes
     */
    public void fieldNodesUpdate(List<NodeQuery> fieldNodes, String tenantId){
        if (CollectionUtils.isEmpty(fieldNodes)) {
            return;
        }

        fieldNodes.stream().forEach(f->{
            fieldNodeUpdate(f, tenantId);
        });
    }

    /**
     * 修改字段血缘节点
     * @param tenantId
     * @param fieldNode
     */
    public void fieldNodeUpdate(NodeQuery fieldNode, String tenantId){
        ColumnMetadataDTO columnMetadataDTO = columnMetadataBizService.getColumnMetadata(fieldNode.getFieldId(), tenantId, null);
        if (null == columnMetadataDTO || StringUtils.isBlank(columnMetadataDTO.getId())) {
            log.error("column blood update column node get column metadata failed. columnId={}", fieldNode.getFieldId());
            return;
        }
        StringBuilder cypherSql = new StringBuilder();
        cypherSql.append("MATCH (f:").append(BloodConstants.COLUMN_NODE).append("{tenantId:'").append(tenantId).append("', id:").append(columnMetadataDTO.getId()).append("}) SET ");
        if (StringUtils.isNotBlank(columnMetadataDTO.getColumnName())) {
            cypherSql.append("f.fieldCode='").append(columnMetadataDTO.getColumnName()).append("',");
        }
        if (null != columnMetadataDTO.getDataSourceId()) {
            cypherSql.append("f.datasourceId=").append(columnMetadataDTO.getDataSourceId()).append(",");
        }
        if (StringUtils.isNotBlank(columnMetadataDTO.getDataSourceType())) {
            cypherSql.append("f.datasourceType='").append(columnMetadataDTO.getDataSourceType()).append("',");
        }
        if (null != columnMetadataDTO.getProjectId()) {
            cypherSql.append("f.projectId=").append(columnMetadataDTO.getProjectId()).append(",");
        }
        if (null != columnMetadataDTO.getTableMetadataId()) {
            cypherSql.append("f.tableId=").append(columnMetadataDTO.getTableMetadataId()).append(",");
        }
        cypherSql.append("f.updateTime=").append(System.currentTimeMillis());
        log.info("field blood update node cypher sql :{}", cypherSql.toString());
        redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, cypherSql.toString());
    }


    /**
     * 删除字段血缘节点，该删除会一并删除该节点的联系，redisgraph官网信息
     * @param tenantId
     * @param fieldNodes
     */
    public void fieldNodeDelete(List<NodeQuery> fieldNodes, String tenantId){
        if (CollectionUtils.isEmpty(fieldNodes)) {
            return;
        }
        List<Long> ids = fieldNodes.stream().map(f -> {
            ColumnMetadataDTO columnMetadataDTO = columnMetadataBizService.getColumnMetadata(f.getFieldId(), tenantId, null);
            if (null != columnMetadataDTO && StringUtils.isNotBlank(columnMetadataDTO.getId())) {
                return Long.parseLong(columnMetadataDTO.getId());
            }
            return null;
        }).filter(e -> null != e).collect(Collectors.toList());

        StringBuilder cypherSql = new StringBuilder();
        cypherSql.append("MATCH (f:").append(BloodConstants.COLUMN_NODE).append(") WHERE (f.tenantId = '").append(tenantId).append("' AND f.id IN").append(ids).append(") DELETE f");
        log.info("field blood delete node cypher sql :{}", cypherSql.toString());
        redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, cypherSql.toString());
    }


    public ResultSet matchFieldEdge(Long sourceId, Long targetId, String tenantId, Long jobId){
        StringBuilder cypherSql = new StringBuilder();
        cypherSql.append("MATCH (s:").append(BloodConstants.COLUMN_NODE).append("{tenantId:'").append(tenantId).append("', id:").append(sourceId).append("}) ")
                .append("MATCH (t:").append(BloodConstants.COLUMN_NODE).append("{tenantId:'").append(tenantId).append("', id:").append(targetId).append("}) ")
                .append("MATCH (s)-[r:").append(BloodConstants.COLUMN_REL).append("{tenantId:'").append(tenantId).append("'}]->(t)");
        if (null != jobId) {
            cypherSql.append(" WHERE r.jobId=").append(jobId);
        }
        cypherSql.append(" RETURN r");
        log.info("match blood field relation cypher sql :{}", cypherSql.toString());
        return redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, cypherSql.toString());
    }

    /**
     * 添加字段血缘节点间关系，该接口只建立在节点存在的基础上
     * @param tenantId
     * @param fieldNodes
     * @param edges
     */
    public void fieldEdgesAdd(List<NodeQuery> fieldNodes, List<EdgeQuery> edges, List<DetailQuery> taskDetails, String tenantId){
        if (CollectionUtils.isEmpty(edges)) {
            return;
        }
        edges.stream().forEach(e->{
            NodeQuery sourceNodeParam = fieldNodes.stream().filter(n -> e.getSourceFieldId().equals(n.getFieldId())).findAny().orElse(null);
            NodeQuery targetNodeParam = fieldNodes.stream().filter(n -> e.getTargetFieldId().equals(n.getFieldId())).findAny().orElse(null);
            fieldEdgeAdd(sourceNodeParam, targetNodeParam, e, taskDetails, tenantId);
        });
    }
    /**
     * 添加字段血缘节点间关系，该接口只建立在节点存在的基础上
     * @param tenantId
     * @param sourceNode
     * @param targetNode
     * @param taskDetails
     */
    public void fieldEdgeAdd(NodeQuery sourceNode, NodeQuery targetNode, EdgeQuery edge, List<DetailQuery> taskDetails, String tenantId){
        ColumnMetadataDTO columnSource= columnMetadataBizService.getColumnMetadata(sourceNode.getFieldId(), tenantId, null);
        ColumnMetadataDTO columnTarget= columnMetadataBizService.getColumnMetadata(targetNode.getFieldId(), tenantId, null);
        DetailQuery detailQuery = taskDetails.stream().filter(t -> t.getJobId().equals(edge.getJobId())).findAny().get();
        if (null == columnSource || null == columnTarget) {
            return;
        }
        StringBuilder cypherSql = new StringBuilder();
        cypherSql.append("MATCH (s:").append(BloodConstants.COLUMN_NODE).append("{tenantId:'").append(tenantId).append("', id:").append(columnSource.getId()).append("}) ")
                .append("MATCH (t:").append(BloodConstants.COLUMN_NODE).append("{tenantId:'").append(tenantId).append("', id:").append(columnTarget.getId()).append("}) ")
                .append("CREATE (s)-[r:").append(BloodConstants.COLUMN_REL).append("{tenantId:'").append(tenantId).append("', jobId:").append(edge.getJobId()).append(",");
        if (StringUtils.isNotBlank(detailQuery.getJobName())) {
            cypherSql.append("jobName:'").append(detailQuery.getJobName()).append("',");
        }
        if (null != detailQuery.getProjectId()) {
            cypherSql.append("projectId:").append(detailQuery.getProjectId()).append(",");
        }
        if (StringUtils.isNotBlank(detailQuery.getProjectCode())) {
            cypherSql.append("projectCode:'").append(detailQuery.getProjectCode()).append("',");
        }
        Integer status = 1;
        if (null != detailQuery.getStatus()) {
            status = detailQuery.getStatus();
        }
        cypherSql.append("status:").append(status).append(", crtTime:").append(System.currentTimeMillis()).append(", updateTime:").append(System.currentTimeMillis()).append("}]->(t)");
        log.info("add blood field relation cypher sql :{}", cypherSql.toString());
        redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, cypherSql.toString());
    }
    /**
     * 修改字段血缘节点间关系
     * @param tenantId
     */
    public void fieldEdgesUpdate(List<NodeQuery> fieldNodes, List<EdgeQuery> edges, List<DetailQuery> taskDetails, String tenantId){
        edges.forEach(e->{
            NodeQuery nodeSource = fieldNodes.stream().filter(f -> e.getSourceFieldId().equals(f.getFieldId())).findAny().orElse(null);
            NodeQuery nodeTarget = fieldNodes.stream().filter(f -> e.getTargetFieldId().equals(f.getFieldId())).findAny().orElse(null);
            if (null == nodeSource || null == nodeTarget) {
                log.error("field update edge can not fitch node, sourceFieldId={}, targetFieldId=", nodeSource.getFieldId(), nodeTarget.getFieldId());
                return;
            }
            fieldEdgeUpdate(nodeSource.getFieldId(), nodeTarget.getFieldId(), e.getJobId(), e, taskDetails, tenantId);
        });
    }
    /**
     * 修改字段血缘节点间关系
     * @param tenantId
     */
    public void fieldEdgeUpdate(Long sourceNodeId, Long targetNodeId, Long jobId, EdgeQuery edge, List<DetailQuery> taskDetails, String tenantId){
        DetailQuery detailQuery = taskDetails.stream().filter(t -> t.getJobId().equals(edge.getJobId())).findAny().get();

        StringBuilder cypherSql = new StringBuilder();
        cypherSql.append("MATCH (s:").append(BloodConstants.COLUMN_NODE).append("{tenantId:'").append(tenantId).append("', id:").append(sourceNodeId).append("}) ")
                .append("MATCH (t:").append(BloodConstants.COLUMN_NODE).append("{tenantId:'").append(tenantId).append("', id:").append(targetNodeId).append("}) ")
                .append("MATCH (s)-[r:").append(BloodConstants.COLUMN_REL).append("{tenantId:'").append(tenantId).append("', jobId:").append(jobId).append("}]->(t) SET ");
        if (StringUtils.isNotBlank(detailQuery.getJobName())) {
            cypherSql.append("r.jobName='").append(detailQuery.getJobName()).append("',");
        }
        if (null != detailQuery.getProjectId()) {
            cypherSql.append("r.projectId = ").append(detailQuery.getProjectId()).append(",");
        }
        if (StringUtils.isNotBlank(detailQuery.getProjectCode())) {
            cypherSql.append("r.projectCode='").append(detailQuery.getProjectCode()).append("',");
        }
        Integer status = 1;
        if (null != detailQuery.getStatus()) {
            status = detailQuery.getStatus();
        }
        cypherSql.append("r.status =").append(status).append(", r.updateTime=").append(System.currentTimeMillis());
        log.info("update field relation cypher sql :{}", cypherSql.toString());
        redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, cypherSql.toString());
    }
    /**
     * 根据jobId删除字段血缘节点间关系
     * @param tenantId
     * @param edges
     */
    public void fieldEdgeDeleteByJobId(List<EdgeQuery> edges, String tenantId){
        if (CollectionUtils.isEmpty(edges)) {
            return;
        }
        edges.stream().forEach(e->{
            Long jobId = e.getJobId();
            StringBuilder cypherSql = new StringBuilder();
            cypherSql.append("MATCH (s:").append(BloodConstants.COLUMN_NODE).append("{tenantId:'").append(tenantId).append("'}) ")
                    .append("MATCH (t:").append(BloodConstants.COLUMN_NODE).append("{tenantId:'").append(tenantId).append("'}) ")
                    .append("MATCH (s)-[r:").append(BloodConstants.COLUMN_REL).append("{jobId:").append(jobId).append(", tenantId:'").append(tenantId).append("'}]->(t) ")
                    .append("DELETE r");
            log.info("field blood delete field relation cypher sql :{}", cypherSql.toString());
            redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, cypherSql.toString());
        });
    }
    /**
     * 删除字段血缘节点间关系
     * @param tenantId
     * @param taskDetails
     */
    public void fieldEdgeDelete(List<DetailQuery> taskDetails, String tenantId){
        if (CollectionUtils.isEmpty(taskDetails)) {
            return;
        }
        taskDetails.stream().forEach(e->{
            Long jobId = e.getJobId();
            StringBuilder cypherSql = new StringBuilder();
            cypherSql.append("MATCH (s:").append(BloodConstants.COLUMN_NODE).append("{tenantId:'").append(tenantId).append("'}) ")
                    .append("MATCH (t:").append(BloodConstants.COLUMN_NODE).append("{tenantId:'").append(tenantId).append("'}) ")
                    .append("MATCH (s)-[r:").append(BloodConstants.COLUMN_REL).append("{jobId:").append(jobId).append(", tenantId:'").append(tenantId).append("'}]->(t) ")
                    .append("DELETE r");
            log.info("field blood delete field relation cypher sql :{}", cypherSql.toString());
            redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, cypherSql.toString());
        });
    }

    /**
     * 获得字段的血缘
     * @param param
     * @return
     */
    @Override
    public ColumnBloodWebDTO getFieldBlood(GetFieldBloodQuery param) {
        Long fieldId = param.getFieldId();
        String tenantId = param.getTenantId();
        Integer upstreamLevel = param.getUpstreamLevel();
        Integer downstreamLevel = param.getDownstreamLevel();

        Map<Long, NodeDTO> nodesDTOMap = new HashMap<>();
        Map<Long, Edge> edgesMap = new HashMap<>();
        Map<Long, Node> nodesMap = new HashMap<>();
        if (null != upstreamLevel) {
            ResultSet upResultSet = getFieldBloodVariableLength(fieldId, tenantId, true, null, downstreamLevel);
            BloodUtil.parseResultSet(upResultSet, "p", nodesMap,  edgesMap, 1);
            Map<Long, NodeDTO> upNodes = nodesMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, n -> {
                NodeDTO dto = new NodeDTO();
                // 设置为上游
                dto.setNodeType(1);
                dto.setNode(n.getValue());
                return dto;
            }));
            nodesDTOMap.putAll(upNodes);
        }
        nodesMap.clear();
        if (null != downstreamLevel) {
            ResultSet downResultSet = getFieldBloodVariableLength(fieldId, tenantId, false, null, upstreamLevel);
            BloodUtil.parseResultSet(downResultSet, "p", nodesMap,  edgesMap, 1);
            Map<Long, NodeDTO> downNodes = nodesMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, n -> {
                NodeDTO dto = new NodeDTO();
                // 设置为上游
                if (nodesDTOMap.containsKey(n.getKey())) {
                    // 上游表已经存在，设置为极为上游又为下游
                    dto.setNodeType(3);
                } else {
                    // 设置为下游
                    dto.setNodeType(2);
                }
                dto.setNode(n.getValue());
                return dto;
            }));
            nodesDTOMap.putAll(downNodes);
        }
        ColumnBloodWebDTO vo = collectResult(nodesDTOMap, edgesMap, fieldId, tenantId);
        return vo;
    }

    @Override
    public ColumnBloodDTO getColumnBloodInternal(String tenantId, Long jobId) {
        ResultSet edgeSet = getResultByJobId(tenantId, jobId);
        log.info("getColumnBloodInternal result = {}", edgeSet);
        Map<Long, Edge> edgesMap = new HashMap<>();
        Map<Long, Node> nodesMap = new HashMap<>();
        BloodUtil.parseResultSet(edgeSet, "p", nodesMap, edgesMap, 1);
        if (CollectionUtils.isEmpty(edgesMap) || CollectionUtils.isEmpty(nodesMap)){
            return null;
        }
        ColumnBloodDTO dto = new ColumnBloodDTO();
        List<ColumnNodeDTO> columnNodes = nodesMap.values().stream().map(e -> {
            ColumnNodeDTO nodeDTO = new ColumnNodeDTO();
            Long fieldId = (Long) e.getProperty("id").getValue();
            ColumnMetadataDTO columnMetadata = columnMetadataBizService.getColumnMetadata(fieldId, tenantId, null);
            log.info("get Internal column blood node columnMetadata={}", columnMetadata);
            long tableId = Long.parseLong(columnMetadata.getTableMetadataId());
            String[] includeFields = { "id", "tableName", "tenantId", "dataSourceType", "dataSourceId", "projectId", "databaseName" };
            TableMetadataDTO tableMetadata = tableMetadataBizService.getTableMetaDate(tableId, tenantId, includeFields, esTimeout);
            nodeDTO.setColumnId(fieldId);
            if (null != columnMetadata){
                nodeDTO.setColumnName(columnMetadata.getColumnName());
                nodeDTO.setTableId(tableId);
                nodeDTO.setColumnType(columnMetadata.getColumnType());
                nodeDTO.setColumnConstraint(columnMetadata.getColumnConstraint());
                nodeDTO.setDataSourceType(columnMetadata.getDataSourceType());
                nodeDTO.setDataSourceId(columnMetadata.getDataSourceId());
                nodeDTO.setDataSourceName(columnMetadata.getDataSourceName());
            }
            if(null != tableMetadata) {
                nodeDTO.setTableName(tableMetadata.getTableName());
                nodeDTO.setDatabaseCode(tableMetadata.getDatabaseName());
            }
            return nodeDTO;
        }).collect(Collectors.toList());
        dto.setColumnNodes(columnNodes);
        List<ColumnEdgeDTO> columnEdges = edgesMap.values().stream().map(e -> {
            ColumnEdgeDTO edgeDTO = new ColumnEdgeDTO();
            long source = e.getSource();
            Node sourceNode = nodesMap.get(source);
            if (null != sourceNode) {
                edgeDTO.setSourceColumnId((Long)sourceNode.getProperty("id").getValue());
            }
            long destination = e.getDestination();
            Node targetNode = nodesMap.get(destination);
            if (null != targetNode) {
                edgeDTO.setTargetColumnId((Long)targetNode.getProperty("id").getValue());
            }
            return edgeDTO;
        }).collect(Collectors.toList());
        dto.setColumnEdges(columnEdges);
        return dto;
    }

    public ResultSet getResultByJobId(String tenantId, Long jobId){
        StringBuilder cypherSql = new StringBuilder();
        cypherSql.append("MATCH p=(s:").append(BloodConstants.COLUMN_NODE).append("{tenantId:'").append(tenantId).append("'})")
                .append("-[r:").append(BloodConstants.COLUMN_REL).append("{tenantId:'").append(tenantId).append("', jobId:").append(jobId).append("}]")
                .append("->(t:").append(BloodConstants.COLUMN_NODE).append("{tenantId:'").append(tenantId).append("'})")
                .append("RETURN p");
        log.info("field blood query by jobId cypher sql :{}", cypherSql.toString());
        return redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, cypherSql.toString());
    }

    /**
     * 将数据转化成前端需要的格式
     * @param edgesMap
     * @param nodesMap
     * @return
     */
    private ColumnBloodWebDTO collectResult(Map<Long, NodeDTO> nodesMap, Map<Long, Edge> edgesMap, Long currentFieldId, String currentTenantId){
        ColumnBloodWebDTO fieldBloodDTO = new ColumnBloodWebDTO();
        List<ColumnNodeWebDTO> fieldNodes = new ArrayList<>();
        if (CollectionUtils.isEmpty(nodesMap)) {
            ColumnNodeWebDTO columnNodeDto = new ColumnNodeWebDTO();
            ColumnMetadataDTO columnMetadata = columnMetadataBizService.getColumnMetadata(currentFieldId, currentTenantId, null);
            log.info("get column blood node columnMetadata={}", columnMetadata);
            String[] includeFields = { "id", "tableName", "tenantId", "dataSourceType", "dataSourceId", "projectId", "databaseName" };
            TableMetadataDTO tableMetadata = tableMetadataBizService.getTableMetaDate(Long.parseLong(columnMetadata.getTableMetadataId()), currentTenantId, includeFields, esTimeout);
            log.info("get column blood node tableMetadate={}", tableMetadata);
            if (null != tableMetadata) {
                columnNodeDto.setOwner(tableMetadata.getOwner());
                columnNodeDto.setTableName(tableMetadata.getTableName());
            }
            columnNodeDto.setColumnId(currentFieldId);
            if (null != columnMetadata) {
                columnNodeDto.setColumnName(columnMetadata.getColumnName());
                columnNodeDto.setTableId(Long.parseLong(columnMetadata.getTableMetadataId()));
            }
            columnNodeDto.setNodeType(0);
            fieldBloodDTO.setColumnNodes(Arrays.asList(columnNodeDto));
            return fieldBloodDTO;
        }
        nodesMap.forEach((k, v)->{
            Node node = v.getNode();
            ColumnNodeWebDTO columnNodeDto = new ColumnNodeWebDTO();
            columnNodeDto.setNodeType(v.getNodeType());
            long fieldId = Long.parseLong(String.valueOf(node.getProperty("id").getValue()));
            String tenantId = (String)node.getProperty("tenantId").getValue();
            ColumnMetadataDTO columnMetadata = columnMetadataBizService.getColumnMetadata(fieldId, tenantId, null);
            log.info("get column blood node columnMetadata={}", columnMetadata);
            long tableId = Long.parseLong(columnMetadata.getTableMetadataId());
            String[] includeFields = { "id", "tableName", "tenantId", "dataSourceType", "dataSourceId", "projectId", "databaseName" };
            TableMetadataDTO tableMetadata = tableMetadataBizService.getTableMetaDate(tableId, tenantId, includeFields, esTimeout);
            log.info("get column blood node tableMetadate={}", tableMetadata);
            columnNodeDto.setTableId(tableId);
            if (null != tableMetadata) {
                columnNodeDto.setOwner(tableMetadata.getOwner());
                columnNodeDto.setTableName(tableMetadata.getTableName());
            }
            if (null != columnMetadata) {
                columnNodeDto.setColumnName(columnMetadata.getColumnName());
            }
            ColumnBloodLevelDTO nodeLevelNum = getNodeLevelNum(fieldId, tenantId);
            if (!BeanUtil.isEmpty(nodeLevelNum)) {
                columnNodeDto.setUpstreamTablesNumber(nodeLevelNum.getNextLayerTableNum());
                columnNodeDto.setDownstreamTablesNumber(nodeLevelNum.getUpperStoryNum());
            }
            columnNodeDto.setColumnId(fieldId);

            if (currentFieldId.equals(fieldId)){
                columnNodeDto.setNodeType(0);
            }
            fieldNodes.add(columnNodeDto);
        });

        AtomicReference<Long> preCrtTime = new AtomicReference<>(0L);
        Map<String, ColumnEdgeWebDTO> fieldEdgeMap = new HashMap<String, ColumnEdgeWebDTO>();
        edgesMap.forEach((k, v)->{
            Long crtTime = (Long) v.getProperty("crtTime").getValue();
            // 来源nodeId
            long source = v.getSource();
            // 目标nodeId
            long destination = v.getDestination();

            String key = source + "_" + + destination;
            ColumnEdgeWebDTO columnEdgeWebDto = fieldEdgeMap.get(key);
            long sourceFieldId = Long.parseLong(String.valueOf(nodesMap.get(source).getNode().getProperty("id").getValue()));
            long targetFieldId = Long.parseLong(String.valueOf(nodesMap.get(destination).getNode().getProperty("id").getValue()));

            String projectCode = null == v.getProperty("projectCode") ? null : (String)v.getProperty("projectCode").getValue();
            Long jobId = (Long)v.getProperty("jobId").getValue();
            Long projectId = null == v.getProperty("projectId")? null : (Long)v.getProperty("projectId").getValue();
            String jobName = (String)v.getProperty("jobName").getValue();
            String tenantId = (String)v.getProperty("tenantId").getValue();
            TaskDetailVO taskDetail = developTaskBizService.getTaskDetail(tenantId, projectCode, projectId, jobId, jobName);
            TaskDetailBloodDTO taskDetailBloodDTO = new TaskDetailBloodDTO();
            BeanUtil.copyProperties(taskDetail, taskDetailBloodDTO);
            if (null == columnEdgeWebDto) {
                preCrtTime.set(crtTime);
                columnEdgeWebDto = new ColumnEdgeWebDTO();
                columnEdgeWebDto.setSourceColumnId(sourceFieldId);
                columnEdgeWebDto.setTargetColumnId(targetFieldId);
                if (null != taskDetail) {
                    columnEdgeWebDto.setTasks(Arrays.asList(taskDetailBloodDTO));
                } else {
                    taskDetailBloodDTO = new TaskDetailBloodDTO();
                    taskDetailBloodDTO.setJobId(jobId);
                    taskDetailBloodDTO.setJobName(jobName);
                    taskDetailBloodDTO.setProjectId(projectId);
                    taskDetailBloodDTO.setProjectCode(projectCode);
                    columnEdgeWebDto.setTasks(Arrays.asList(taskDetailBloodDTO));
                }
            } else {
                List<TaskDetailBloodDTO> currentTask = columnEdgeWebDto.getTasks();
                if (!CollectionUtils.isEmpty(currentTask)) {
                    if (null != taskDetail && preCrtTime.get() < crtTime) {
                        columnEdgeWebDto.setTasks(Arrays.asList(taskDetailBloodDTO));
                    }
                } else {
                    columnEdgeWebDto.setTasks(Arrays.asList(taskDetailBloodDTO));
                }
            }
            fieldEdgeMap.put(key, columnEdgeWebDto);
        });
        fieldBloodDTO.setColumnNodes(fieldNodes);
        List<ColumnEdgeWebDTO> columnEdges = fieldEdgeMap.values().stream().collect(Collectors.toList());
        fieldBloodDTO.setColumnEdges(columnEdges);
        return fieldBloodDTO;
    }

    public ColumnBloodLevelDTO getNodeLevelNum(Long columnId, String tenantId) {
        Long downLevel = 0L;
        Long upLevel = 0L;
        StringBuilder querySqlDown = new StringBuilder();
        // 去除自己指向自己
        querySqlDown.append("MATCH (s:").append(BloodConstants.COLUMN_NODE).append("{id:").append(columnId).append(", tenantId:'").append(tenantId).append("'})-[r:").append(BloodConstants.COLUMN_REL).append("{status:1}]->(t:").append(BloodConstants.COLUMN_NODE).append("{tenantId:'").append(tenantId).append("'}) WHERE NOT t.id = ").append(columnId).append(" return count(DISTINCT t.id) AS nextLayerTableNum");
        log.info("query node level down stream num for RedisGraph: {}", querySqlDown.toString());
        ResultSet resultSetDown = redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, querySqlDown.toString());
        ColumnBloodLevelDTO dto = new ColumnBloodLevelDTO();
        while (resultSetDown.hasNext()) {
            Record record = resultSetDown.next();
            Object downStreamNum = record.getValue("nextLayerTableNum");
            downLevel = null == downStreamNum ? 0 : (Long) downStreamNum;
            dto.setColumnId(columnId);
        }
        StringBuilder querySqlUp = new StringBuilder();
        // 去除自己指向自己
        querySqlUp.append("MATCH (s:").append(BloodConstants.COLUMN_NODE).append("{tenantId:'").append(tenantId).append("'})-[r:").append(BloodConstants.COLUMN_REL).append("{status:1}]->(t:").append(BloodConstants.COLUMN_NODE).append("{id:").append(columnId).append(", tenantId:'").append(tenantId).append("'}) WHERE NOT s.id = ").append(columnId).append(" return count(DISTINCT s.id) AS upperStoryTableNum");
        log.info("query node level up stream num for RedisGraph: {}", querySqlUp.toString());
        ResultSet resultSet = redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, querySqlUp.toString());
        log.info("get level up stream num result={}", resultSet);
        while (resultSet.hasNext()) {
            Record record = resultSet.next();
            Object upstreamNum = record.getValue("upperStoryTableNum");
            upLevel = null == upstreamNum ? 0 : (Long) upstreamNum;
            dto.setNextLayerTableNum(Math.toIntExact(upLevel));
            dto.setColumnId(columnId);
        }
        dto.setNextLayerTableNum(Math.toIntExact(upLevel));
        dto.setUpperStoryNum(Math.toIntExact(downLevel));
        return dto;
    }

    /**
     * 查询该节点上游/下游层级
     * @param id
     * @param tenantId
     * @param isUpstream
     * @param upLevel
     * @param downLevel
     * @return
     */
    private ResultSet getFieldBloodVariableLength(Long id, String tenantId, Boolean isUpstream, Integer upLevel, Integer downLevel) {
        StringBuilder querySql = new StringBuilder();
        querySql.append("MATCH p=(s:").append(BloodConstants.COLUMN_NODE).append(")-[").append(BloodConstants.COLUMN_REL).append("*");
        if(null != upLevel && upLevel >= 0) {
            querySql.append(upLevel);
        }
        querySql.append("..");
        if(null != downLevel && downLevel >= 0) {
            querySql.append(downLevel);
        }
        querySql.append("]->(t:").append(BloodConstants.COLUMN_NODE).append(") ");
        if (isUpstream) {
            querySql.append("WHERE t.id=").append(id).append(" AND t.tenantId='").append(tenantId).append("'");
        } else {
            querySql.append("WHERE s.id=").append(id).append(" AND s.tenantId='").append(tenantId).append("'");
        }
        querySql.append(" RETURN p");
        log.info("query field variable length statement input source for RedisGraph: {}", querySql.toString());
        return redisGraph.query(BloodConstants.BLOOD_GRAPH_ID, querySql.toString());
    }

}
