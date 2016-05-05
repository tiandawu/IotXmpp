package com.cqupt.xmpp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by tiandawu on 2016/3/29.
 */
public class DateUtils {
    public static String getNowDateTime() {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(new Date());
    }
}
