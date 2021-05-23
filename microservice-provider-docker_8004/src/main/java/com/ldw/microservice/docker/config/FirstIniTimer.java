package com.ldw.microservice.docker.config;

import cn.hutool.core.date.DateUtil;
import com.ldw.microservice.docker.schedule.ScannerAutoExecutionTask;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.text.ParseException;

@Configuration
public class FirstIniTimer {


    @Autowired
    ScannerAutoExecutionTask scannerAutoExecutionTask;


    @PostConstruct
    public void execute() throws ParseException, SchedulerException {
        scannerAutoExecutionTask.execute();
    }
}
