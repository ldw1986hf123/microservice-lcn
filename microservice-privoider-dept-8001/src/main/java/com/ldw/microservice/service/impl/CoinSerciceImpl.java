package com.ldw.microservice.service.impl;

import com.ldw.microservice.dao.CoinDao;
import com.ldw.microservice.entity.Coin;
import com.ldw.microservice.entity.Dept;
import com.ldw.microservice.service.CoinSercice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoinSerciceImpl implements CoinSercice {
    @Autowired
    CoinDao coinDao;

    @Override
    public boolean Insert(Coin coin) {
        return coinDao.Insert(coin);
    }

    @Override
    public Coin findById(Integer id) {
        return coinDao.findById(id);
    }

    @Override
    public List findAll() {
        return coinDao.findAll();
    }
}
