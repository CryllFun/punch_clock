package com.zerfu.demo.service;

import com.zerfu.demo.entity.UserInfo;
import com.zerfu.demo.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Service
public class TimedTask implements Runnable {

    private SimpleDateFormat yyyyddmmHHMMSS = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private SimpleDateFormat HH_MM_SS = new SimpleDateFormat("HH:mm:ss");

    private SimpleDateFormat yyyyddmm = new SimpleDateFormat("yyyy-MM-dd");

    private UserInfo user;
    @Autowired
    private SendMail sendMail;
    private String testTime=null;

    public TimedTask() {
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    @Override
    public void run() {
        punchClock();
    }

    public synchronized String punchClock() {
        int num1 = -1;
        String qd_time = "";
        String qc_time = "";
        Random r = new Random();
        System.out.println("--------------------定时打卡任务开始----------------------------------------");
        String createTime1 = "08:1";
        String createTime2 = "17:3";
        String qtsj = user.getQtsj();
        if(qtsj!=null&&!"".equals(qtsj)){
            createTime2 = qtsj;
        }
        String qdsj = user.getQdsj();
        if(qdsj!=null&&!"".equals(qdsj)){
            createTime1 = qdsj;
        }
        String msg = "";
        boolean flag = true;
        //不确定是否每秒都能取到:so不精确到秒
        while (flag) {
            String time = HH_MM_SS.format(new Date());
            if(null!=testTime){
                createTime2 = testTime;
            }
            //每天08:10,17:30生成一个打卡时间
            if ((time.indexOf(createTime1+"0:0") != -1 || time.indexOf(createTime2+"0:0") != -1) && num1 == -1) {
                num1 = r.nextInt(9)+1;
                System.out.println("num1:" + num1);
                String today = yyyyddmm.format(new Date());
                //签到时间
                qd_time = today +" "+createTime1+ num1;
                //签出时间
                qc_time = today +" "+createTime2+ num1;
                if(null!=testTime){
                    qc_time = today +" "+ testTime;
                }
                System.out.println("时间1:" + qd_time);
                System.out.println("时间2:" + qc_time);
            }
            //有随机数后才打卡
            if (num1 != -1) {
                int week = Util.getWeekOfToday();
                //排除不打卡的时间
                if (user.getClockWeek().contains(week)) {
                    System.out.println("非工作日,不打卡");
                    num1 = -1;
                    continue;
                }
                String now = yyyyddmmHHMMSS.format(new Date());
                //签到
                int type = 0;
                if (!now.equals(qd_time) && !now.equals(qc_time)) {
                    continue;
                }
                //签出
                if (now.equals(qc_time)) {
                    type = 1;
                }
                msg = now;
                //只打卡一次
                try {
                    num1 = -1;
                    SignInOrCheckOut inout = new SignInOrCheckOut();
                    msg += inout.qdqc(type, user);
                    System.out.println(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    msg = e.toString();
                }
                String mail = user.getEmail();
                if (null != mail && !mail.equals("")) {
                    boolean res = sendMail.sendSimpleMail(mail, "打卡状态提醒", msg);
                    if (res) {
                        System.out.println("邮件发送成功！"+msg);
                    } else {
                        System.out.println("邮件发送失败！");
                    }
                }
//                String mobil = user.getMobile();
//                if (null != mobil && !mobil.equals("")) {
//                    boolean res = sendMail.sendSimpleMail(mail, "打卡状态提醒", msg);
//                    if (res) {
//                        System.out.println("短信发送成功！");
//                    } else {
//                        System.out.println("短信发送失败！");
//                    }
//                }
            }
            if(null!=testTime){
                flag = false;
            }
        }
        System.out.println("打卡任务结束！");
        return msg;
    }

    public String getTestTime() {
        return testTime;
    }

    public void setTestTime(String testTime) {
        this.testTime = testTime;
    }
}
