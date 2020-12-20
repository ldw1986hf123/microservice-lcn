package com.ldw.metadata.vo;

import lombok.Data;
import lombok.ToString;
import org.apache.hadoop.fs.Path;

import java.util.Date;


@Data
@ToString
public class HiveTableStorageInfo {

    /**
     * 库名
     */
    private String dbName;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 路径名
     */
    private Path path;
    /**
     * 表的大小
     */
    private Long size;
    /**
     * 表的需改时间
     */
    private Date lastModifiedTime;

}
