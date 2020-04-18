package com.ldw.microservice.comsumer.controller;


import com.alibaba.fastjson.JSON;
import com.ldw.microservice.entity.Dept;
import io.swagger.annotations.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.spring.web.json.Json;

@Controller
@RequestMapping("/user")
@Api(value = "UserController  控制用户登录权限")
public class UserController {

    @ResponseBody
    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ApiOperation(value = "登录接口", notes = "登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "username", value = "用户名", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "password", value = "用户密码", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "googlecode", value = "谷歌验证码", required = true, dataType = "String")
    })
    public String login(@RequestParam Integer userNumber) {
//        if (userNumber == 1) {
//            return "张三";
//        } else if (userNumber == 2) {
//            return "李四";
//        } else if (userNumber == 3) {
//            return "王五";
//        }

        Dept dept=new Dept();
        dept.setDName("11  ");
        dept.setDb_source("22");
        String json= JSON.toJSONString(dept );
        return json;
    }

    @ResponseBody
    @RequestMapping(value = "/updatePassword", method = RequestMethod.GET)
    @ApiOperation(value = "修改用户密码", notes = "根据用户id修改密码")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful — 请求已完成"),
            @ApiResponse(code = 400, message = "请求中有语法问题，或不能满足请求")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userId", value = "用户ID", required = true, dataType = "int"),
            @ApiImplicitParam(paramType = "query", name = "password", value = "旧密码", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "newPassword", value = "新密码", required = true, dataType = "String")
    })
    public String updatePassword(@RequestParam(value = "userId", required = true) Integer userId, @RequestParam(value = "password") String password,
                                 @RequestParam(value = "newPassword") String newPassword) {
        if (userId <= 0 || userId > 2) {
            return "未知的用户";
        }
        if (StringUtils.isEmpty(password) || StringUtils.isEmpty(newPassword)) {
            return "密码不能为空";
        }
        if (password.equals(newPassword)) {
            return "新旧密码不能相同";
        }
        return "密码修改成功!";
    }


    @ResponseBody
    @RequestMapping(value = "/updatePassword", method = RequestMethod.GET)
    @ApiOperation(value = "修改用户密码", notes = "根据用户id修改密码")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful — 请求已完成"),
            @ApiResponse(code = 400, message = "请求中有语法问题，或不能满足请求")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userId", value = "用户ID", required = true, dataType = "int"),
            @ApiImplicitParam(paramType = "query", name = "password", value = "旧密码", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "newPassword", value = "新密码", required = true, dataType = "String")
    })
    public String updateUser(@RequestParam(value = "userId", required = true) Integer userId, @RequestParam(value = "password") String password,
                                 @RequestParam(value = "newPassword") String newPassword) {
        if (userId <= 0 || userId > 2) {
            return "未知的用户";
        }
        if (StringUtils.isEmpty(password) || StringUtils.isEmpty(newPassword)) {
            return "密码不能为空";
        }
        if (password.equals(newPassword)) {
            return "新旧密码不能相同";
        }
        return "密码修改成功!";
    }

}
