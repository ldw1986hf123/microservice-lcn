package com.ldw.microservice.docker.controller;


import com.ldw.microservice.service.CommonEsServiceApi;
import com.ldw.microservice.service.vo.CommonSearchVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "抢红包")
@Controller
@RequestMapping("/feignTest")
@Slf4j
public class FeignTestController {
    @Autowired
    private CommonEsServiceApi commonEsService;

    @RequestMapping(path = "get", method = RequestMethod.GET)
    public String get() {
        CommonSearchVO commonSearchVO = new CommonSearchVO();

        Map<String, Object> mustWhere = new HashMap<>();
        mustWhere.put("startEffectiveTime",new Date());
        commonSearchVO.setMustWhere(mustWhere);
        commonSearchVO.setPagingFlag(false);
        commonEsService.searchIndex(commonSearchVO);
        return "get";
    }


}
