package com.ldw.microservice.docker.util;

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:DBUtils <br/>
 * Function: 数据库工具类. <br/>
 * Reason: . <br/>
 * Date: 2020年9月16日 上午11:14:46 <br/>
 *
 * @author WangXf
 * @see
 * @since JDK 1.8
 */
@Slf4j
public class DBUtils {


    /**
     * 直接从resultSet 的 getMetaData  方法  元数据中获取数据
     * 只要查询元数据时设置的别名和metadatavo的属性名对应上，就可以直接把属性值设置进去
     *
     * @param rs
     * @param clazz
     * @param <T>
     * @return
     * @throws SQLException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static <T> List<T> convertList(ResultSet rs, Class<T> clazz) {
        List<T> list = new ArrayList();
        String columnLabel = "";
        Object filedValue = null;
        try {
            //获取键名
            ResultSetMetaData md = rs.getMetaData();
            //获取行的数量
            int columnCount = md.getColumnCount();
            while (rs.next()) {
                T vo = clazz.newInstance();
                for (int i = 1; i <= columnCount; i++) {
                    //获取键名及值
                    columnLabel = md.getColumnLabel(i);
                    filedValue = rs.getObject(i);
                    BeanUtil.setFieldValue(vo, columnLabel, filedValue);
                }
                list.add(vo);
            }
        } catch (Exception e) {
            log.error(" 设置属性值异常 columnLabel:{}。 filedValue:{}  ", columnLabel, filedValue);
            log.error(" 设置属性值异常   ", e);
        }
        return list;
    }


    public static void addParams(PreparedStatement preparedStatement, List<String> params) {
        try {
            for (int i = 0; i < params.size(); i++) {
                String param = params.get(i);
                preparedStatement.setString(i + 1, param);
            }
        } catch (SQLException e) {
            log.error("addParams ", e);
        }
    }


    /**
     * 获取resultRet对应的值，用key value返回
     *
     * @param rs
     * @return
     * @throws SQLException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static Map<String, String> getMapByRs(ResultSet rs, String columnKey, String columnValue) {
        String columnLabel = "";
        Map singleMap = new HashMap();
        try {
            //获取键名
            ResultSetMetaData md = rs.getMetaData();
            //获取行的数量
            int columnCount = md.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    //获取键名及值
                    String key = "";
                    String value = "";
                    columnLabel = md.getColumnLabel(i);
                    if (columnLabel.equals(columnKey)) {
                        key = rs.getString(i);
                    } else if (columnLabel.equals(columnValue)) {
                        value = rs.getString(i);
                    }
                    singleMap.put(key, value);
                }
            }
        } catch (Exception e) {
            log.error(" 获取resultRet对应的值 异常   ", e);
        }
        return singleMap;
    }


}
