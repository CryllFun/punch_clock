package com.zerfu.demo.util;

import java.util.Calendar;
import java.util.Date;

public class Util {
    /**
     * 获取当前日期的周几
     *
     * @return
     */
    public static int getWeekOfToday(){
        Date tmpDate = new Date();
        Calendar cal = Calendar.getInstance();
        int[] weekDays = {7, 1, 2, 3, 4, 5, 6};
        try {
            cal.setTime(tmpDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
        if (w < 0)
            w = 0;
        return weekDays[w];
    }
}
