package com.ldw.microservice.entity;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Dept implements Serializable {

	private Long deptNo;
	private String dName;
	private String db_source;

}
