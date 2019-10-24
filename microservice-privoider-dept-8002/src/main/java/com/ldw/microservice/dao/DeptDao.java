package com.ldw.microservice.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ldw.microservice.entity.Dept;

@SuppressWarnings("rawtypes")
@Mapper
public interface DeptDao {
	 boolean addDpet(Dept dept);

	 Dept findById(Long id);

	 List findAll();
}
