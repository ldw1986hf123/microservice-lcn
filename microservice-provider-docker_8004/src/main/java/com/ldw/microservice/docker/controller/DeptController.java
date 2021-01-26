package com.ldw.microservice.docker.controller;


import com.ldw.microservice.docker.dto.DemoDto;
import com.ldw.microservice.docker.dto.DemoOutputDto;
import com.ldw.microservice.docker.dto.FFResponseModel;
import com.ldw.microservice.entity.Dept;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = "抢红包")
@Controller
@RequestMapping("/redPacket")
@Slf4j
public class DeptController {

    @ApiOperation(value = "抢红包一", nickname = "爪哇笔记")
    @RequestMapping(path = "get", method = RequestMethod.GET)
    public String get() {
        System.out.println("get");
        return "get";
    }

    @ApiOperation(value = "post请求调用示例", notes = "invokePost说明", httpMethod = "POST")
    @RequestMapping(path = "invokePost", method = RequestMethod.GET)
    public FFResponseModel<DemoOutputDto> invokePost(@ApiParam(name = "传入对象", value = "传入json格式", required = true)
                                                     @RequestBody @Valid DemoDto input) {
        log.info("/testPost is called. input=" + input.toString());
        return new FFResponseModel();
    }


}
