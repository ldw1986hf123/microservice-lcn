package com.ldw.microservice.service;

import com.ldw.microservice.entity.Coin;
import com.ldw.microservice.entity.Dept;

import java.util.List;

public interface CoinSercice {
	 boolean Insert(Coin dept);

	 Coin findById(Integer id);

	@SuppressWarnings("rawtypes")
	 List findAll();



}
