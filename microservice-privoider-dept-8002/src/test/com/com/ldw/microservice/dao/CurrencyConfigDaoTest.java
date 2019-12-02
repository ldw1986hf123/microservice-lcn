package com.ldw.microservice.dao;

import com.ldw.microservice.DeptProvider8002_App;
import com.ldw.microservice.entity.CurrencyConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

@SpringBootTest(classes = DeptProvider8002_App.class)
@RunWith(SpringRunner.class)
/**  指定当前生效的配置文件( active profile)，如果是 appplication-dev.yml 则 dev   **/
@ActiveProfiles("test")
/** 指定  @SpringBootApplication  启动类 和 端口  **/
public class CurrencyConfigDaoTest {

    @Autowired
    CurrencyConfigDao currencyConfigDao;

    @Test
    public void getByCondition() {
        CurrencyConfig currencyConfig = new CurrencyConfig();
        currencyConfig.setLanguage("zh");
        currencyConfig.setCurrency("eos");
        List existedList = currencyConfigDao.getByCondition(currencyConfig);
        System.out.println(existedList);

        List newList = new ArrayList();

        CurrencyConfig currencyConfig1 = new CurrencyConfig();
        currencyConfig1.setLanguage("zh");
        currencyConfig1.setCurrency("eos");
        currencyConfig1.setKey("deposit");
        currencyConfig1.setValue("入账3");

        CurrencyConfig currencyConfig2 = new CurrencyConfig();
        currencyConfig2.setLanguage("zh");
        currencyConfig2.setCurrency("eos");
        currencyConfig2.setKey("deposit1");
        currencyConfig2.setValue("入账new");

        CurrencyConfig currencyConfig3 = new CurrencyConfig();
        currencyConfig3.setLanguage("zh");
        currencyConfig3.setCurrency("eos");
        currencyConfig3.setKey("deposit5");
        currencyConfig3.setValue("value5");

        newList.add(currencyConfig1);
        newList.add(currencyConfig2);
        newList.add(currencyConfig3);

        System.out.println("new list----------------");
        for (Object o : newList) {
            System.out.println(o);
        }

        List<CurrencyConfig> intersection = (List<CurrencyConfig>) newList.stream().filter(item -> existedList.contains(item)).collect(toList());
        System.out.println("---得到交集 intersection---");
        intersection.parallelStream().forEach(System.out::println);
    }


    @Test
    public void testReataill() {
        List<String> list1 = new ArrayList();
        list1.add("1111");
        list1.add("2222");
        list1.add("3333");

        List<String> list2 = new ArrayList();
        list2.add("3333");
        list2.add("4444");
        list2.add("5555");

        // 交集 	拓展：list2里面如果是对象，则需要提取每个对象的某一属性组成新的list,多个条件则为多个list
        List<String> intersection = list1.stream().filter(item -> list2.contains(item)).collect(toList());
        System.out.println("---得到交集 intersection---");
        intersection.parallelStream().forEach(System.out::println);

        // 差集 (list1 - list2)	同上拓展
        List<String> reduce1 = list1.stream().filter(item -> !list2.contains(item)).collect(toList());
        System.out.println("---得到差集 reduce1 (list1 - list2)---");
        reduce1.parallelStream().forEach(System.out::println);

        // 差集 (list2 - list1)
        List<String> reduce2 = list2.stream().filter(item -> !list1.contains(item)).collect(toList());
        System.out.println("---得到差集 reduce2 (list2 - list1)---");
        reduce2.parallelStream().forEach(System.out::println);

        // 并集
        List<String> listAll = list1.parallelStream().collect(toList());
        List<String> listAll2 = list2.parallelStream().collect(toList());
        listAll.addAll(listAll2);
        System.out.println("---得到并集 listAll---");
        listAll.parallelStream().forEach(System.out::println);

        // 去重并集
        List<String> listAllDistinct = listAll.stream().distinct().collect(toList());
        System.out.println("---得到去重并集 listAllDistinct---");
        listAllDistinct.parallelStream().forEach(System.out::println);

        System.out.println("---原来的List1---");
        list1.parallelStream().forEach(System.out::println);
        System.out.println("---原来的List2---");
        list2.parallelStream().forEach(System.out::println);

        // 一般有filter 操作时，不用并行流parallelStream ,如果用的话可能会导致线程安全问题

    }
}