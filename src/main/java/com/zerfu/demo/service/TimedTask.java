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
        String createTime = "18:00:0";
        String msg = "";
        boolean flag = true;
        while (flag) { //不确定是否每秒都能取到:so不精确到秒
            String time = HH_MM_SS.format(new Date());
            if(null!=testTime){
                createTime = testTime;
            }
            if ((time.indexOf("08:40:0") != -1 || time.indexOf(createTime) != -1) && num1 == -1) {//每天08:40,18:00生成一个打卡时间
                num1 = r.nextInt(10);
                System.out.println("num1:" + num1);
                String today = yyyyddmm.format(new Date());
                qd_time = today + " 08:4" + num1;//签到时间
                qc_time = today + " 18:2" + num1;//签出时间
                if(null!=testTime){
                    qc_time = today +" "+ testTime;
                }
                System.out.println("时间1:" + qd_time);
                System.out.println("时间2:" + qc_time);
            }

            if (num1 != -1) {//有随机数后才打卡
                int week = Util.getWeekOfToday();
                if (user.getClockWeek().contains(week)) {//排除不打卡的时间
                    System.out.println("非工作日,不打卡");
                    num1 = -1;
                    continue;
                }
                String now = yyyyddmmHHMMSS.format(new Date());
                int type = 0;//签到
                if (!now.equals(qd_time) && !now.equals(qc_time)) {
                    continue;
                }
                if (now.equals(qc_time)) {
                    type = 1;//签出
                }
                msg = now;
                try {
                    num1 = -1;//只打卡一次
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
