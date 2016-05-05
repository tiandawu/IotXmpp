package com.cqupt.xmpp.listener;

import android.app.NotificationManager;
import android.content.Intent;

import com.cqupt.xmpp.activity.ChatWithNodeActivity;
import com.cqupt.xmpp.bean.ChatMessage;
import com.cqupt.xmpp.bean.ChatSession;
import com.cqupt.xmpp.bean.NodeSubStatus;
import com.cqupt.xmpp.db.ChatMsgDao;
import com.cqupt.xmpp.db.ChatSesionDao;
import com.cqupt.xmpp.db.NodeStatusDao;
import com.cqupt.xmpp.fragment.MessageFragment;
import com.cqupt.xmpp.service.IotXmppService;
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

    public MsgListener(IotXmppService context, NotificationManager mNotificationManager) {
        this.context = context;
        this.mNotificationManager = mNotificationManager;
        chatMsgDao = new ChatMsgDao(context);
        mChatSesionDao = new ChatSesionDao(context);
        mNodeStatusDao = new NodeStatusDao(context);
    }

    @Override
    public void processMessage(Chat chat, Message message) {

<<<<<<< HEAD

        String xmlStr = message.toXML();
=======
        String xmlStr = message.toXML();

>>>>>>> origin/master
        String from = message.getFrom();
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
            session.setTo(message.getTo());
            session.setBody(result);
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

        }

    }

}
