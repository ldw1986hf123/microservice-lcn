package com.ldw.microservice.docker.constnat;


/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2020. All rights reserved.
 *
 * @author ludanwen
 * @date 2021/3/18
 * @description: 数据库类型枚举
 * <p>
 * "hive-Hive,kudu-Kudu,oracle-Oracle,mysql-MySQL,sqlserver-SQL Server";
 **/
public enum DataSourceTypeEnum {
    Hive("hive", "Hive"),
    Kudu("kudu", "Kudu"),
    Oracle("oracle", "Oracle"),
    MySQL("mysql", "MySQL"),
    SQL_Server("sqlserver", "SQL Server");

    private String value;
    private String name;

    DataSourceTypeEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static String getEnumType(String status) {
        DataSourceTypeEnum[] alarmGrades = DataSourceTypeEnum.values();
        for (int i = 0; i < alarmGrades.length; i++) {
            if (alarmGrades[i].getName().equals(status)) {
                return alarmGrades[i].name;
            }
        }
        return "";
    }
}
