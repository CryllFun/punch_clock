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

@Api(value = "CasController",tags = "任务接口")
@RestController
public class CasController {
    @Autowired
    private TimedTask timedTask ;
    private Thread thread = new Thread(timedTask);

    @ApiOperation(value = "开启定时打卡任务",notes = "备注：无")
    @ApiImplicitParams({
            @ApiImplicitParam(name="account",value="账号",required=true,paramType="query",dataType="String"),
            @ApiImplicitParam(name="password",value="密码",required=true,paramType="query",dataType="String"),
            @ApiImplicitParam(name="email",value="邮箱",paramType="query",dataType="String"),
            @ApiImplicitParam(name="times",value="不打卡时间",required=true,paramType="query",dataType="String")
    })
    @RequestMapping(value = "/start",method = RequestMethod.POST)
    public String taskStart(UserInfo userInfo) {
        try {
            UserInfo user = timedTask.getUser();
            if(null != user && user.equals(userInfo)){
                return "定时任务已开启！不打卡时间：周"+user.getTimes();
            }
            if(null==userInfo.getAccount()||"".equals(userInfo.getAccount())){
                return "账号不能为空！";
            }
            if(null==userInfo.getPassword()||"".equals(userInfo.getPassword())){
                return "密码不能为空！";
            }
            thread.stop();
            timedTask.setUser(userInfo);
            timedTask.setTestTime(null);
            thread = new Thread(timedTask);
            thread.start();
            return "定时打卡任务开始！不打卡时间：周"+userInfo.getTimes();
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }
}