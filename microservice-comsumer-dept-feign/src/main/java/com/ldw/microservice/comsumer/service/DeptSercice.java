package com.ldw.microservice.comsumer.service;

import com.ldw.microservice.entity.Dept;

import java.util.List;

public interface DeptSercice {
	public boolean addDpet(Dept dept);

	public Dept findById(Long id);

	@SuppressWarnings("rawtypes")
	public List findAll();

	void lcnTxTestAdd(Dept dept);
}
