package com.cqupt.xmpp.utils;

import android.content.Context;

/**
 * Created by tiandawu on 2016/3/31.
 */
public class ConstUtil {
    /**
     * 服务器ip
     */
    public static final String XMPP_IP = "ip";
    /**
     * 服务器端口
     */
    public static final String XMPP_PORT = "port";
    /**
     * SharedPreferences 的名字
     */
    public static final String SP_NAME = "iotXmppConfig";
    /**
     * 登陆用户名
     */
    public static final String SP_KEY_NAME = "username";
    /**
     * 登陆密码
     */
    public static final String SP_KEY_PWD = "password";

    /**
     * 登录状态广播过滤器
     */
    public static final String LOGIN_STATUS = "com.cqupt.xmpp.login_is_success";

    /**
     * 在线
     */
    public static final String ON_LINE = "available";
    /**
     * 在线
     */
    public static final String OFF_LINE = "available";

    /**
     * 获取所有者的jid
     *
     * @return
     */
    public static String getOwnerJid(Context context) {
        return PreferencesUtils.getSharePreStr(context, SP_KEY_NAME) + "@xmpp/Smack";
    }

}
