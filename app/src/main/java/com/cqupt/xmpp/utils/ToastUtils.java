package com.cqupt.xmpp.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by tiandawu on 2016/4/2.
 */
public class ToastUtils {

    /**
     * 显示较短的土司
     *
     * @param context
     * @param message
     */
    public static void showShortToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示较长的土司
     *
     * @param context
     * @param message
     */
    public static void showLongToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 显示较长的土司在正中间
     *
     * @param context
     * @param message
     */
    public static void showLongToastInCenter(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    /**
     * 显示较短的土司在正中间
     *
     * @param context
     * @param message
     */
    public static void showShortToastInCenter(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}
