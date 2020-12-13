package com.ldw.metadata.dbUtil;

import cn.hutool.core.bean.BeanUtil;
import com.ldw.metadata.vo.Animal;
import com.ldw.metadata.vo.MetadataVO;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.*;

@Slf4j
public class DBUtils {


    public static void printResultSet(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                System.out.print(resultSet.getObject(i + 1));
                System.out.print("\t");
            }
            System.out.println("");
        }
    }

    public static List convertList(ResultSet rs) throws SQLException {
        List list = new ArrayList();
        ResultSetMetaData md = rs.getMetaData();//获取键名
        int columnCount = md.getColumnCount();//获取行的数量
        while (rs.next()) {
            Map rowData = new HashMap();//声明Map
            for (int i = 1; i <= columnCount; i++) {
                rowData.put(md.getColumnName(i), rs.getObject(i));//获取键名及值
            }
            list.add(rowData);
        }
        return list;
    }

    public static <T extends MetadataVO> List<T> convertList(ResultSet rs, Class<T> clazz) throws SQLException, IllegalAccessException, InstantiationException {
        List<T> list = new ArrayList();
        ResultSetMetaData md = rs.getMetaData();//获取键名
        int columnCount = md.getColumnCount();//获取行的数量
        while (rs.next()) {
            T vo = clazz.newInstance();
            for (int i = 1; i <= columnCount; i++) {
                log.info(md.getColumnName(i), rs.getObject(i));//获取键名及值
                String columnLabel=md.getColumnLabel(i);
                Object filedValue=rs.getObject(i);
                BeanUtil.setFieldValue(vo,columnLabel,filedValue);
            }
            list.add(vo);
//            list.add(rowData);
        }
        return list;
    }

    public static <T extends Animal> List<T> cast(List<Animal> animals, Class<T> subclass) {
        List<T> out = new ArrayList<T>();
        for (Animal animal : animals) {
            if (!subclass.isAssignableFrom(animal.getClass())) {
                // the "animal" entry isn't an instance of "subclass"
                // manage this case however you want ;)
            } else {
                out.add((T) animal);
            }
        }
        return out;
    }

    /**
     * 如果在类中定义使用泛型的静态方法，需要添加额外的泛型声明（将这个方法定义成泛型方法）
     * 即使静态方法要使用泛型类中已经声明过的泛型也不可以。
     * 如：public static void show(T t){..},此时编译器会提示错误信息：
     * "StaticGenerator cannot be refrenced from static context"
     */
    public static <T> void show(T t) {

    }
    public static void closeResources(PreparedStatement stm, ResultSet resultSet) {
        if (null != stm) {
            try {
                stm.close();
            } catch (SQLException e) {
                log.error("关闭 stm 出错", e);
            }
        }


        if (null != resultSet) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                log.error("关闭 resultSet 出错", e);
            }
        }
    }

    public static void closeConnection(Connection connection) {
        if (null != connection) {
            try {
                connection.close();
            } catch (SQLException e) {
                log.error("关闭 connection 出错", e);
            }
        }
    }

    public <T> T badCast(T t, Object o) {
        return (T) o; // unchecked warning
    }
}
