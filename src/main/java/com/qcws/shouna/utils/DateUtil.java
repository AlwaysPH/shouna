package com.qcws.shouna.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    /**
     * 判断2个时间大小
     * @param addTime
     * @param nowDate
     * @return
     */
    public static boolean compareDate(Date addTime, Date nowDate) {
        Calendar calendar = Calendar.getInstance();//日历对象
        calendar.setTime(addTime);//设置当前日期
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        Date time = calendar.getTime();
        if(time.getTime() - nowDate.getTime() < 1000 * 3600 * 24 * 7){
            return true;
        }
        return false;
    }

}
