package com.ldw.microservice.entity;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class EmployeeInfo {
    private String employeeId;
    private String employeeName;
    private String employeeIdCardNo;
    private String sex;
    private Long deptNo;

}
