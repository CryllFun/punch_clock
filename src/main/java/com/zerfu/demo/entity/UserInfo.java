package com.zerfu.demo.entity;

import com.zerfu.demo.controller.CasController;
import com.zerfu.demo.util.MD5;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class UserInfo implements Serializable {
    private  String account = "";
    private  String password = "";
    private  String email = "";
    private  String mobile = "";
    private  String times = "";
    //不打卡时间
    private  List<Integer> clockWeek = new ArrayList<>();
    private String qdsj;
    private String qtsj;

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
        setClockWeek();
    }

    public UserInfo() {
    }

    public UserInfo(String account, String password, String email, String mobile, String times) {
        this.account = account;
        this.password = MD5.getMd5(password.trim());
        this.email = email==null?"":email;
        this.mobile = mobile==null?"":mobile;
        this.times = times==null?"":times;
        setClockWeek();
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = MD5.getMd5(password);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public List<Integer> getClockWeek() {
        return clockWeek;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfo userInfo = (UserInfo) o;
        return account.equals(userInfo.account) &&
                password.equals(userInfo.password) &&
                email.equals(userInfo.email) &&
                mobile.equals(userInfo.mobile) &&
                times.equals(userInfo.times) &&
                clockWeek.equals(userInfo.clockWeek) &&
                qdsj.equals(userInfo.qdsj) &&
                qtsj.equals(userInfo.qtsj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account, password, email, mobile, times, clockWeek);
    }
    private void setClockWeek(){
        if(null==times){
            return;
        }
        String[] i = times.split(",");
        for (String s :i) {
            try {
                this.clockWeek.add(Integer.valueOf(s));
            } catch (NumberFormatException e) {
                continue;
            }
        }
    }

    public String getQdsj() {
        return qdsj;
    }

    public void setQdsj(String qdsj) {
        this.qdsj = qdsj;
    }

    public String getQtsj() {
        return qtsj;
    }

    public void setQtsj(String qtsj) {
        this.qtsj = qtsj;
    }
}
