package cn.nzcer.odapi.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @project: od-api
 * @ClassName: DateUtil
 * @author: nzcer
 * @creat: 2022/12/22 18:49
 * @description: 日期工具类
 */
public class DateUtil {
    public static final String format(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public static Date parse(String strTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMM");
        Date date = null;
        try {
            date = sdf.parse(strTime);
        } catch (Exception e) {
            try {
                date = sdf1.parse(strTime);
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
            //e.printStackTrace();
        }
        return date;
    }
}
