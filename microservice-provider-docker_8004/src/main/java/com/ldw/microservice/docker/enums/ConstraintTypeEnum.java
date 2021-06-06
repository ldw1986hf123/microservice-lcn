package com.ldw.microservice.docker.enums;


import org.apache.commons.lang3.StringUtils;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2020. All rights reserved.
 *
 * @author wengguifang
 * @date 2021/3/18
 * @description: 字段约束
 **/
public enum ConstraintTypeEnum {
    // mysql start
    UNIQUE("UNIQUE", "0", "唯一约束"),
    DEFAULT("DEFAULT", "1", "默认约束"),
    PRIMARY_KEY("PRIMARY KEY", "2", "主键约束"),
    FOREIGN_KEY("FOREIGN KEY", "3", "外键约束"),
    // mysql end
    // oracle start
    CHECK_ORACLE("C","4","CHECK约束"),
    UNIQUE_ORACLE("U","5","唯一约束"),
    PRIMARY_KEY_ORACLE("P","6","主键约束"),
    FOREIGN_KEY_ORACLE("R","7","外键约束"),
    VIEW_KEY_ORACLE("V","8","视图约束"),
    READ_ONLY_ORACLE("O","9","只读约束"),
    // oracle end
    // sqlserver start
    CHECK_SQLSERVER("CHECK", "10","CHECK约束"),
    NOT_NULL("NOT NULL", "11","值不为空"),
    // sqlserver end
    ;

    private String type;

    private String code;

    private String name;

    ConstraintTypeEnum(String type, String code, String name) {
        this.type = type;
        this.code = code;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public static ConstraintTypeEnum getEnumByType(String type) {
        for (ConstraintTypeEnum value : values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }

    public static String getNameByCode(String code) {
        for (ConstraintTypeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value.getName();
            }
        }
        return null;
    }

    public static String getNamesByCodes(String codes) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(codes)) {
            String[] split = codes.split(",");
            for (String id : split) {
                String tempName = getNameByCode(id);
                if (StringUtils.isNotBlank(tempName)) {
                    sb.append(tempName).append(" ");
                }
            }
        }
        return sb.toString();
    }
}
