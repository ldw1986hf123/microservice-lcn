package com.ldw.microservice.comsumer.dao;

import com.ldw.microservice.entity.Dept;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DeptDao {
	public boolean addDpet(Dept dept);

	public Dept findById(Long id);

	public List findAll();
}
