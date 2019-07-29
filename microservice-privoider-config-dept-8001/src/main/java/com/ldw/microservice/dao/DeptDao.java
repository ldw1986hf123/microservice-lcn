package com.ldw.microservice.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ldw.microservice.entity.Dept;

@SuppressWarnings("rawtypes")
@Mapper
public interface DeptDao {
	public boolean addDpet(Dept dept);

	public Dept findById(Long id);

	public List findAll();
}
