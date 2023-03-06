package cn.nzcer.odapi.util;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @project: od-api
 * @ClassName: DateUtil
 * @author: nzcer
 * @creat: 2022/12/22 18:49
 * @description: 日期工具类
 */
@Slf4j
public class DateUtil {
    public static final String format(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public static Date parse(String strTime) {
        strTime = filterKey(strTime);
        if (strTime == null) {
            // key 解析有误
            return null;
        }
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
        }
        return date;
    }

    public static String filterKey(String content) {
        String pattern = "^\\d{4}-\\d{2}$";
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(content);
        if (matcher.find()) {
            return matcher.group(0).toString();
        }
        return null;
    }
}
