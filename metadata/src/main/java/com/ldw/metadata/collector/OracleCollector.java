package com.ldw.metadata.collector;

import com.ldw.metadata.dbUtil.ConnectUtil;
import com.ldw.metadata.dbUtil.DBUtils;
import com.ldw.metadata.vo.DatasourceVO;
import com.ldw.metadata.vo.IndexMetadataVO;
import com.ldw.metadata.vo.JdbcDatasourceVO;
import com.ldw.metadata.vo.PartitionMetadataVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OracleCollector {

    String SELECT_ALL_INDEX="select  index_name as \"name\",Index_type as \"type\" ,table_name as \"tableName\"   from user_indexes";
    String SELECT_ALL_PARTITION="select partition_name as  \"name\" ,table_name  as \"tableName\", max_size as \"maxDataLength\"  from DBA_TAB_PARTITIONS";

    //查询创建时间

//    select * from user_objects where object_type='INDEX' ORDER BY CREATED DESC;

    /**
     * 获取索引的元数据
     */
    public List<IndexMetadataVO> getIndexMetadata(Connection connection) {
        List<IndexMetadataVO> indexMetadataVOS = new ArrayList<>();
        PreparedStatement stm = null;
        ResultSet rs = null;
        //查询数据源下，所有的表信息
        try {
            stm = connection.prepareStatement(SELECT_ALL_INDEX);
            rs = stm.executeQuery();
            indexMetadataVOS = DBUtils.convertList(rs, IndexMetadataVO.class);
        } catch (SQLException e) {
            log.error("先获取索引的元数据 异常", e);
        } catch (IllegalAccessException e) {
            log.error("先获取索引的元数据 异常", e);
        } catch (InstantiationException e) {
            log.error("先获取索引的元数据 异常", e);
        }
        finally {
            DBUtils.closeResources(stm,rs);
        }
        return indexMetadataVOS;
    }


    /**
     * 获取 分区 的元数据
     */
    private List<PartitionMetadataVO> getPartitionMetadata(Connection connection) {
        List<PartitionMetadataVO> partitionMetadataVOS = new ArrayList<>();
        PreparedStatement stm = null;
        ResultSet rs = null;
        //查询数据源下，所有的表信息
        try {
            stm = connection.prepareStatement(SELECT_ALL_PARTITION);
            rs = stm.executeQuery();
            partitionMetadataVOS = DBUtils.convertList(rs, PartitionMetadataVO.class);
        } catch (SQLException e) {
            log.error("先获取 分区 的元数据 异常", e);
        } catch (IllegalAccessException e) {
            log.error("先获取分区的元数据 异常", e);
        } catch (InstantiationException e) {
            log.error("先获取分区的元数据 异常", e);
        }
        finally {
           DBUtils.closeResources(stm,rs);
        }
        return partitionMetadataVOS;
    }


    /**
     * 获取索引的元数据
     */
    public List<IndexMetadataVO> collect(JdbcDatasourceVO datasourceVO) {
        Connection connection= ConnectUtil.getConnection(datasourceVO   );
        List<IndexMetadataVO> indexMetadataVOS = new ArrayList<>();
        PreparedStatement stm = null;
        ResultSet rs = null;
        //查询数据源下，所有的表信息
        try {
            stm = connection.prepareStatement(SELECT_ALL_INDEX);
            rs = stm.executeQuery();
            indexMetadataVOS = DBUtils.convertList(rs, IndexMetadataVO.class);
        } catch (SQLException e) {
            log.error("先获取索引的元数据 异常", e);
        } catch (IllegalAccessException e) {
            log.error("先获取索引的元数据 异常", e);
        } catch (InstantiationException e) {
            log.error("先获取索引的元数据 异常", e);
        }
        finally {
            DBUtils.closeResources(stm,rs);
        }
        return indexMetadataVOS;
    }

}
