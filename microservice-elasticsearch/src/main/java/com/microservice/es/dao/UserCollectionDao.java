package com.microservice.es.dao;

import com.microservice.es.entity.UserCollection;
import org.springframework.stereotype.Service;

@Service
public class UserCollectionDao extends EsCommonDao<UserCollection> {

    public UserCollection getById(Long id)
    {

        return null;
    }
}
