package com.microservice.es.repository;

import com.microservice.es.entity.Item;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ItemRepository extends ElasticsearchRepository<Item,Long> {
}
