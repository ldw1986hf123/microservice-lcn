package com.ldw.microservice.docker.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ldw.microservice.docker.eo.TaskInfoDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author AutoGenerator
 * @since 2021-03-18
 */
@Mapper
public interface TaskInfoMapper extends BaseMapper<TaskInfoDO> {


    /**
     * 根据datasourceId 和tenantId 查找，只会返回一条记录
     * @param datasourceId
     * @param tenantId
     * @return
     */
    TaskInfoDO getByDatasourceIdAndTenantId(@Param("datasourceId") Long datasourceId, @Param("tenantId") String tenantId);

}
