package com.microservice.es.repository;


import org.elasticsearch.action.get.MultiGetRequest;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ItemRepository extends ElasticsearchRepository<MultiGetRequest.Item,Long> {
}
