package com.cqupt.xmpp.utils;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by tiandawu on 2016/3/2.
 */
public class ActivityUtils {
    /**
     * 打开Activity
     * @param activity
     * @param clazz
     */
    public static void startActivity(Activity activity, Class clazz) {
        startActivity(activity,clazz,false);
    }

    /**
     * 打开Activity，并关闭上一个Activity
     * @param activity
     * @param clazz
     * @param isFinish
     */
    public static void startActivity(Activity activity, Class clazz, boolean isFinish) {
        Intent intent = new Intent(activity, clazz);
        activity.startActivity(intent);
        if (isFinish) {
            activity.finish();
        }
    }
}
