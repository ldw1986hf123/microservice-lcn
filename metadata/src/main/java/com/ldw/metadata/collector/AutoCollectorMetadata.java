package com.ldw.metadata.collector;
/**  
 * ClassName:AutoCollectorMetadata <br/>  
 * Function: 元数据自动采集器接口类. <br/>  
 * Reason:   . <br/>  
 * Date:     2020年9月17日 下午4:57:22 <br/>  
 * @author
 * @version    
 * @since    JDK 1.8  
 * @see        
 */


import com.ldw.metadata.vo.JdbcDatasourceVO;
import com.ldw.metadata.vo.TableMetadataVO;

import java.sql.SQLException;
import java.util.List;

public interface AutoCollectorMetadata {

	/**
	 * 
	 * @desc 自动收集元数据
	 * 
	 * @param datasourceVO
	 * @return 表集合
	 * @throws SQLException 
	 */
	List<TableMetadataVO> collect(JdbcDatasourceVO datasourceVO) throws Exception;
}
