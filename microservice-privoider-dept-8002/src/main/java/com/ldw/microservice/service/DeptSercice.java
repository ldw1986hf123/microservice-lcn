package com.ldw.microservice.service;

import java.util.List;

import com.ldw.microservice.entity.Dept;

public interface DeptSercice {
	 boolean addDpet(Dept dept);

	 Dept findById(Long id);

	@SuppressWarnings("rawtypes")
	 List findAll();
}
