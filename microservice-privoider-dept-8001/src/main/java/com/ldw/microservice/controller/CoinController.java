package com.ldw.microservice.controller;

import com.ldw.microservice.entity.Coin;
import com.ldw.microservice.entity.Dept;
import com.ldw.microservice.service.CoinSercice;
import com.ldw.microservice.service.DeptSercice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("coin")
public class CoinController {

    private @Autowired
    CoinSercice coinSercice;

    @RequestMapping(value = "get/{id}", method = RequestMethod.GET)
    public Coin get(@PathVariable("id") Integer id) {
        System.out.print("get");
        Coin coin = coinSercice.findById(1);
        return coin;
    }


}
