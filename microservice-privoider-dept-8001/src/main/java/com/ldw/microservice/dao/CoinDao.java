package com.ldw.microservice.dao;

import com.ldw.microservice.entity.Coin;
import com.ldw.microservice.entity.Dept;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CoinDao {

     boolean Insert(Coin dept);

     Coin findById(Integer id);

     List findAll();
}
