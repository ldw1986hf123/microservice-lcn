package ldw.microservice.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ldw.microservice.DeptProvider8002_App;
import com.ldw.microservice.entity.OperLog;
import com.ldw.microservice.service.impl.OperLogServiceImpl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = DeptProvider8002_App.class)
@RunWith(SpringRunner.class)
/**  指定当前生效的配置文件( active profile)，如果是 appplication-dev.yml 则 dev   **/
@ActiveProfiles("test")
/** 指定  @SpringBootApplication  启动类 和 端口  **/
public class OperLogServiceImplTest {

    @Autowired
    OperLogServiceImpl operLogService;

    @Test
    public void insert() {
        Map para = new HashMap();
        para.put("tableName", "tableName");
        para.put("createTime", new Date());
        para.put("noted", "1");
        operLogService.insert(null);
    }

    @Test
    public void insertSelective() {
        OperLog operLog = new OperLog();
        operLog.setNoted("3");
        operLog.setCreateTime(new Date());
        for (int i = 0; i < 50; i++) {
            operLogService.insertSelective(operLog);
        }

    }

    @Test
    public void getPage() {
        PageHelper.startPage(1, 10);
        OperLog param = new OperLog();
        param.setNoted("3");
        List<OperLog> list = operLogService.getPage(param);//查询
        // 取商品列表
        for (OperLog item : list) {
            System.out.println(item);
        }
        // 取分页信息
        PageInfo<OperLog> pageInfo = new PageInfo<OperLog>(list);
        long total = pageInfo.getTotal(); //获取总记录数
        System.out.println("共有商品信息：" + total);
    }

    @Test
    public void getPageWithAnnotation() {
        OperLog operLog = new OperLog();
        operLog.setNoted("3");
        operLogService.getPageWithAnnotation(operLog);
    }


}