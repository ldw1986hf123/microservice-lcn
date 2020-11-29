package com.microservice.es.repository;

import com.microservice.es.entity.User;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface UserRepository extends ElasticsearchRepository<User, Integer> {
    /**
     * 根据用户名模糊查询
     * @param name
     * @return
     */
    List<User> findAllByNameLike(String name);

    /**
     * 按用户名模糊查询and根据Address属性下的city进行查询，并且按照id排序
     * @param userName
     * @param city
     * @return
     */
    List<User> EsCommonDao(
            String userName,
            String city
    );
}
