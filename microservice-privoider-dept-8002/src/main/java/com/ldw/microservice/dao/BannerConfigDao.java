package com.ldw.microservice.dao;

import com.ldw.microservice.entity.BannerConfig;
import com.ldw.microservice.entity.Dept;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
@Mapper
public interface BannerConfigDao {

	BannerConfig findById(String id);

	int addBannerConfig(BannerConfig bannerConfig);

	/*/*
	 *统计每个banner的点击次数
	 * @author qqg
	 * @date 2019/10/28
	 * @param [mapParam]
	 * @return java.util.List
	 */
	List staticBannerClick(Map mapParam);
}
