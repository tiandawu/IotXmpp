package com.cqupt.xmpp.listener;

import android.app.NotificationManager;
import android.content.Intent;
import android.util.Log;

import com.cqupt.xmpp.activity.ChatWithNodeActivity;
import com.cqupt.xmpp.activity.DMActivity;
import com.cqupt.xmpp.activity.GDActivity;
import com.cqupt.xmpp.activity.KGNodeActivity;
import com.cqupt.xmpp.activity.LEDActivity;
import com.cqupt.xmpp.bean.ChatMessage;
import com.cqupt.xmpp.bean.ChatSession;
import com.cqupt.xmpp.bean.NodeSubStatus;
import com.cqupt.xmpp.db.ChatMsgDao;
import com.cqupt.xmpp.db.ChatSesionDao;
import com.cqupt.xmpp.db.NodeStatusDao;
import com.cqupt.xmpp.fragment.MessageFragment;
import com.cqupt.xmpp.service.IotXmppService;
import com.cqupt.xmpp.service.PlayAlarmSoundService;
import com.cqupt.xmpp.utils.DateUtils;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

/**
 * Created by tiandawu on 2016/4/6.
 */
public class MsgListener implements MessageListener {
    private IotXmppService context;
    private NotificationManager mNotificationManager;
    private ChatMsgDao chatMsgDao;
    private ChatSesionDao mChatSesionDao;
    private NodeStatusDao mNodeStatusDao;
    private static AlarmListener mLisener = null;
    private static SessionAlarmListener mSessionListener = null;


    public MsgListener(IotXmppService context, NotificationManager mNotificationManager) {
        this.context = context;
        this.mNotificationManager = mNotificationManager;
        chatMsgDao = new ChatMsgDao(context);
        mChatSesionDao = new ChatSesionDao(context);
        mNodeStatusDao = new NodeStatusDao(context);
    }

    @Override
    public void processMessage(Chat chat, Message message) {


        String xmlStr = message.toXML();


        Log.e("tt", "tt = " + xmlStr);

        String from = message.getFrom();

        if ("A1@xmpp/A".equals(from) || "A2@xmpp/A".equals(from)) {
            String state = message.getBody();
            if ("1".equals(state)) {
                Intent intent = new Intent();
                intent.setAction(KGNodeActivity.ON);
                intent.putExtra(KGNodeActivity.FROM_EXTRA, message.getFrom());
                context.sendBroadcast(intent);
            } else if ("0".equals(state)) {
                Intent intent = new Intent();
                intent.setAction(KGNodeActivity.OFF);
                intent.putExtra(KGNodeActivity.FROM_EXTRA, message.getFrom());
                context.sendBroadcast(intent);
            }

        } else if ("A3@xmpp/A".equals(from)) {

            String ledState = message.getBody();

//            Log.e("tt", "ledState = " + ledState);
//            Log.e("tt", "收到时间： " + System.currentTimeMillis() + "");
            Intent intent = new Intent();
            intent.setAction(LEDActivity.LED_STATE);
            intent.putExtra(LEDActivity.LED_STATE, ledState);
            context.sendBroadcast(intent);
        } else if ("B1@xmpp/B".equals(from)) {
            Log.e("tt", "doorState = " + message.getBody());
            Intent intent = new Intent();
            intent.setAction(DMActivity.DOOR_STATE);
            intent.putExtra(DMActivity.DOOR_STATE, message.getBody());
            context.sendBroadcast(intent);
        } else if ("B2@xmpp/B".equals(from)) {
            Log.e("tt", "gdState = " + message.getBody());

            Intent intent = new Intent();
            intent.setAction(GDActivity.GD_STATE);
            intent.putExtra(GDActivity.GD_STATE, message.getBody());
            context.sendBroadcast(intent);

            if (mLisener != null) {
                mLisener.showAlarm(from);
            }

            if ("1".equals(message.getBody())) {
                Intent hAlarmIntent = new Intent(context, PlayAlarmSoundService.class);
                context.startService(hAlarmIntent);
            }

        }


        String subType = message.getSubType();

        if (!mNodeStatusDao.isExistTheNode(from)) {
            NodeSubStatus node = new NodeSubStatus();
            node.setNodeName(from);
            if (subType.equals("period")) {
                node.setPeriod(subType);
            } else if (subType.equals("highLimit")) {
                node.setHighLimit(subType);
            } else if (subType.equals("lowLimit")) {
                node.setLowLimit(subType);
            }
            mNodeStatusDao.insert(node);
        }

        if (message.getBody() == null) {

            String result = xmlStr.substring(xmlStr.indexOf("<value>") + 7, xmlStr.indexOf("</value>"));

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setTo(message.getTo());
            chatMessage.setFrom(message.getFrom());
            chatMessage.setBody(result);
            chatMessage.setOwner(message.getTo());
            chatMessage.setTime(DateUtils.getNowDateTime());
            chatMsgDao.insert(chatMessage);
            Intent intent = new Intent();
            intent.setAction(ChatWithNodeActivity.RECEIVED_MESSAGE);
            context.sendBroadcast(intent);

            ChatSession session = new ChatSession();
            session.setFrom(message.getFrom());
            String groupName = from.substring(from.lastIndexOf("/") + 1, from.length());
            session.setTo(message.getTo());
            if ("temprature".equals(groupName)) {
//                Log.e("tt", "当前温度值为：" + result + " ℃");
                session.setBody("当前温度值为：" + result + " ℃");
            } else if ("smoke".equals(groupName)) {
                session.setBody("当前烟雾浓度为：" + result + " ppm");
            } else if ("light".equals(groupName)) {
                session.setBody("当前光照强度为：" + result + " ");
            }
//            session.setBody(result);
            session.setOwner(message.getTo());
            session.setTime(DateUtils.getNowDateTime());
            if (mChatSesionDao.isExistTheSession(message.getFrom(), message.getTo())) {
                mChatSesionDao.updateSession(session);
            } else {
                mChatSesionDao.insert(session);
            }

            Intent sessionIntent = new Intent();
            sessionIntent.setAction(MessageFragment.RECEIVED_NEW_SESSION);
            context.sendBroadcast(sessionIntent);

            if (subType.equals("highLimit")) {
                if (mLisener != null) {
                    mLisener.showAlarm(chatMessage.getFrom());
                }

                if (mSessionListener != null) {
                    mSessionListener.showSessionAlarm(from);
                }

                Intent hAlarmIntent = new Intent(context, PlayAlarmSoundService.class);
                context.startService(hAlarmIntent);
            } else if (subType.equals("lowLimit")) {
                if (mLisener != null) {
                    mLisener.showAlarm(chatMessage.getFrom());
                }
                Intent lAlarmIntent = new Intent(context, PlayAlarmSoundService.class);
                context.startService(lAlarmIntent);
            }

        }

    }


    public static void setAlarmLisener(AlarmListener lisener) {

        MsgListener.mLisener = lisener;
    }

    public static void setSessionAlarmListener(SessionAlarmListener listener) {
        MsgListener.mSessionListener = listener;
    }

}
