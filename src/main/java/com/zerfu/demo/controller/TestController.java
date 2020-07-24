package com.zerfu.demo.controller;

import com.zerfu.demo.entity.UserInfo;
import com.zerfu.demo.service.TimedTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

@Api(tags = "测试接口")
@RestController
public class TestController {
    @Autowired
    private TimedTask tt ;
    @ApiOperation(value = "测试能否正常打卡",notes = "备注：无")
    @ApiImplicitParams({
            @ApiImplicitParam(name="account",value="账号",required=true,paramType="query",dataType="String"),
            @ApiImplicitParam(name="password",value="密码",required=true,paramType="query",dataType="String"),
            @ApiImplicitParam(name="email",value="邮箱",paramType="query",dataType="String"),
    })
    @RequestMapping(value = "/test",method = RequestMethod.POST)
    public String testTask(String account,String password,String email){
        SimpleDateFormat HH_MM = new SimpleDateFormat("HH:mm");
        String testTime = HH_MM.format(new Date());
        if(null==account||"".equals(account)){
            return "账号不能为空！";
        }
        if(null==password||"".equals(password)){
            return "密码不能为空！";
        }
        UserInfo userInfo = new UserInfo(account,password,email,null,null);
        tt.setUser(userInfo);
        tt.setTestTime(testTime);
        return tt.punchClock();
    }
}
