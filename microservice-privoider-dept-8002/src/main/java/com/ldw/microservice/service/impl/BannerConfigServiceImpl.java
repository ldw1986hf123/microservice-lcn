package com.ldw.microservice.service.impl;

import com.ldw.microservice.dao.BannerConfigDao;
import com.ldw.microservice.entity.BannerConfig;
import com.ldw.microservice.entity.OperLog;
import com.ldw.microservice.service.BannerConfigService;
import com.ldw.microservice.service.vo.BannerClickVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BannerConfigServiceImpl implements BannerConfigService {

    @Autowired
    private BannerConfigDao bannerConfigDao;

    public BannerConfig findById(String id) {
        return bannerConfigDao.findById(id);
    }

    public int addBannerConfig(BannerConfig bannerConfig) {
        return bannerConfigDao.addBannerConfig(bannerConfig);
    }

    public List staticBannerConfigClick() {
        List<String> tableNameList = new ArrayList();

        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        for (int i = 0; ; i--) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MONTH, i);
            String time = format.format(c.getTime());
            tableNameList.add("oper_log_" + time);
            if ("201908".equals(time)) {
                break;
            }
        }

        List bannerClickPerMonth = new ArrayList();
        Map mapParam = new HashMap();
        for (String tableName : tableNameList) {
            mapParam.put("tableName", tableName);
            List list = bannerConfigDao.staticBannerClick(mapParam);
            bannerClickPerMonth.addAll(list);
        }

        List resultList = new ArrayList();
        //分组：按照分数（返回的map的key是根据分组的条件来决定的，score是int，那么key就是Integer）
        Map<Integer, List<BannerClickVo>> scoreUsers = (Map<Integer, List<BannerClickVo>>) bannerClickPerMonth.stream().collect(Collectors.groupingBy(BannerClickVo::getNoted));
        scoreUsers.forEach((key, value) -> {
            Map notedToClickCnt = new HashMap();
            int summingInt = value.stream().collect(Collectors.summingInt(BannerClickVo::getClickCnt));
            notedToClickCnt.put(key, summingInt);
            resultList.add(notedToClickCnt);
        });
        return resultList;
    }

    public OperLog modifyCurrency(String currency) throws Exception {
        System.out.println("modifyCurrency ");
        OperLog operLog=new OperLog();
        operLog.setId("2");
        return operLog;
//        String s=null;
//        System.out.println(s.getBytes());
    }


}
