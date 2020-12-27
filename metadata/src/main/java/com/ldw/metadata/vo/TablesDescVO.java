package com.ldw.metadata.vo;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Data
@ToString
@Slf4j
public class TablesDescVO {
    private String col_name;
    private String data_type;
    private String comment;


    public static boolean isExsitPartition(List<TablesDescVO> tablesDescList) {
        for (TablesDescVO tablesDesc : tablesDescList) {
            if ("# Partition Information".equals(tablesDesc.getCol_name())) {
                log.info("查询到分区信息：",tablesDesc.toString());
                return true;
            }
            else
            {
                log.info("没有分区数据。。。。" ,tablesDesc.toString());
            }
        }

        return false;
    }
    public static String getLocation(List<TablesDescVO> tablesDescList) {
        if (tablesDescList == null) {
            return null;
        }

        for (TablesDescVO tablesDesc : tablesDescList) {
            if ("Location:           ".equals(tablesDesc.getCol_name())) {
                return tablesDesc.getData_type();
            }
        }
        return null;
    }
}
