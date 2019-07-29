package com.ldw.microservice.service.impl;

import com.ldw.microservice.dao.DeptDao;
import com.ldw.microservice.entity.Dept;
import com.ldw.microservice.service.DeptSercice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeptServiceImpl implements DeptSercice {

	@Autowired
	private DeptDao deptDao;
	public boolean addDpet(Dept dept) {
		return deptDao.addDpet(dept);
	}

	public Dept findById(Long id) {
		return deptDao.findById(id);
	}

	@SuppressWarnings("rawtypes")
	public List findAll() {
		return deptDao.findAll();
	}


}
