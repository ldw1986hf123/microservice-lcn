//package com.microservice.es.controller;
//
//import com.microservice.es.entity.User;
//import org.elasticsearch.action.get.GetRequest;
//import org.elasticsearch.action.get.GetResponse;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//
//@Controller
//public class EsController {
//
//    @Autowired
//    private RestHighLevelClient client;
//
//    @GetMapping("/order/getById/{id}")
//    public Map<String, Object> getOrder(@PathVariable("id") String id) {
//        GetRequest getRequest = new GetRequest("order", "_doc", id);
//        Map map = new HashMap();
//        GetResponse response = null;
//        try {
//            response = client.get(getRequest, RequestOptions.DEFAULT);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (response.isExists()) {
//            map.put("success", true);
//            map.put("data", response.getSource());
//        } else {
//            map.put("success", false);
//        }
//        return map;
//    }
//
//    @GetMapping("/createUser")
//    public Iterable<User> createUser(@RequestParam("from")Integer from, @RequestParam("to")Integer to) {
//        String[] country = {"uk", "china", "japan"};
//        String[] city = {"london", "gz", "tokyo"};
//        String[] street = {"a", "b", "config"};
//        List<User> users = IntStream.rangeClosed(from, to).mapToObj(e -> {
//                    User u = new User();
//                    u.setName("No" + e);
//                    u.setUserId(e);
//                    return u;
//                }
//        ).collect(Collectors.toList());
//
//        return userRepository.saveAll(users);
//    }
//}
