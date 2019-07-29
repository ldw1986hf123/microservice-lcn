//package com.ldw.microservice.service;
//
//import java.util.List;
//
//import org.springframework.stereotype.Component;
//
//import com.ldw.microservice.entity.Dept;
//
//import feign.hystrix.FallbackFactory;
//
//@Component
//public class DeptFeignClient implements FallbackFactory<DeptFeignService> {
//
//	public DeptFeignService create(Throwable arg0) {
//		return new DeptFeignService() {
//
//			public List list() {
//				// TODO Auto-generated method stub
//				return null;
//			}
//
//			public Dept get(Long id) {
//				return new Dept().setDName("该ID：" + id
//						+ "没有对于的信息，consumer客户端提供对于的降级信息,");
//			}
//
//			public boolean addDept(Dept dept) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//		};
//	}
//}
