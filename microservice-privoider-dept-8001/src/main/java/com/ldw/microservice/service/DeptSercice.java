package com.ldw.microservice.service;

import java.util.List;

import com.ldw.microservice.entity.Dept;

public interface DeptSercice {
	public boolean addDpet(Dept dept);

	public Dept findById(Long id);

	@SuppressWarnings("rawtypes")
	public List findAll();



}
