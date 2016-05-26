package com.cqupt.xmpp.listener;

import android.content.Intent;

import com.cqupt.xmpp.activity.ChatWithNodeActivity;
import com.cqupt.xmpp.bean.ChatMessage;
import com.cqupt.xmpp.bean.ChatSession;
import com.cqupt.xmpp.db.ChatMsgDao;
import com.cqupt.xmpp.db.ChatSesionDao;
import com.cqupt.xmpp.db.NodeStatusDao;
import com.cqupt.xmpp.fragment.MessageFragment;
import com.cqupt.xmpp.packet.GetDataResp;
import com.cqupt.xmpp.packet.SubscribResp;
import com.cqupt.xmpp.packet.UnsubNodeReq;
import com.cqupt.xmpp.packet.WriteNodeResp;
import com.cqupt.xmpp.service.IotXmppService;
import com.cqupt.xmpp.utils.DateUtils;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;

public class MyPacketListener implements PacketListener {
    private IotXmppService context;
    private ChatMsgDao mChatMsgDao;
    private ChatSesionDao mChatSesionDao;
    private NodeStatusDao mNodeStatusDao;

    public MyPacketListener(IotXmppService context) {
        this.context = context;
        mChatMsgDao = new ChatMsgDao(context);
        mChatSesionDao = new ChatSesionDao(context);
        mNodeStatusDao = new NodeStatusDao(context);
    }

    @Override
    public void processPacket(Packet packet) {


        if (packet.getFrom().equals(packet.getTo())) {
            return;
        }

//        if (packet instanceof Presence) {
//            Log.e("tt", "packet == " + packet.toXML());
//            Intent intent = new Intent();
//            intent.setAction(ContactFragment.FRIENDS_STATUS_CHANGED);
//            context.sendBroadcast(intent);
//        }
//        Log.e("tt", "packet == " + packet.toXML());

        //获取到数据
        if (packet instanceof GetDataResp) {
            GetDataResp getDataResp = (GetDataResp) packet;

//            Log.e("tt", "packet = " + packet.toXML());


            String from = getDataResp.getFrom();
            String groupName = from.substring(from.lastIndexOf("/") + 1, from.length());

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setFrom(from);
            chatMessage.setTo(getDataResp.getTo());
            chatMessage.setTime(DateUtils.getNowDateTime());
            chatMessage.setOwner(getDataResp.getTo());


            String var = getDataResp.getVar();
            String value = getDataResp.getValue();

            if (var.equals("samplePeri")) {
                chatMessage.setBody("当前周期为：" + value + " 秒");
            } else if (var.equals("highLimit")) {
                if ("temprature".equals(groupName)) {
                    chatMessage.setBody("当前温度上限值为：" + value + " ℃");
                } else if ("smoke".equals(groupName)) {
                    chatMessage.setBody("当前烟雾浓度上限为：" + value + " ppm");
                } else if ("light".equals(groupName)) {
                    chatMessage.setBody("当前光照强度上限为：" + value + " lx");
                }
            } else if (var.equals("lowLimit")) {

                if ("temprature".equals(groupName)) {
                    chatMessage.setBody("当前温度下限值为：" + value + " ℃");
                } else if ("light".equals(groupName)) {
                    chatMessage.setBody("当前光照强度下限为：" + value + " lx");
                }

            } else {
                chatMessage.setBody(value);
            }

            mChatMsgDao.insert(chatMessage);

            Intent intent = new Intent();
            intent.setAction(ChatWithNodeActivity.RECEIVED_MESSAGE);
            context.sendBroadcast(intent);


            ChatSession session = new ChatSession();

            session.setFrom(from);
            session.setTo(getDataResp.getTo());
//            session.setBody(getDataResp.getValue());

            if ("temprature".equals(groupName)) {
                session.setBody("当前温度值为：" + getDataResp.getValue() + " ℃");
            } else if ("smoke".equals(groupName)) {
                session.setBody("当前烟雾浓度为：" + getDataResp.getValue() + " ppm");
            } else if ("light".equals(groupName)) {
                session.setBody("当前光照强度为：" + value + " lx");
            }
            session.setOwner(getDataResp.getTo());
            session.setTime(DateUtils.getNowDateTime());
            if (mChatSesionDao.isExistTheSession(getDataResp.getFrom(), getDataResp.getTo())) {
                mChatSesionDao.updateSession(session);
            } else {
                mChatSesionDao.insert(session);
            }

            Intent sessionIntent = new Intent();
            sessionIntent.setAction(MessageFragment.RECEIVED_NEW_SESSION);
            context.sendBroadcast(sessionIntent);
        } else if (packet instanceof SubscribResp) {
            //订阅成功的响应
            Intent subIntent = new Intent();
            subIntent.putExtra("subType", SubscribResp.getPubType());
            subIntent.setAction(ChatWithNodeActivity.SUBSCRIBE_SUCCESS);
            context.sendBroadcast(subIntent);
        } else if (packet instanceof WriteNodeResp) {
            //写入数据成功的响应
            Intent writeIntent = new Intent();
            writeIntent.setAction(ChatWithNodeActivity.WRITE_SUCCESS);
            context.sendBroadcast(writeIntent);
        } else if (packet instanceof UnsubNodeReq) {
            UnsubNodeReq unsubNodeReq = (UnsubNodeReq) packet;
            Intent unsubIntent = new Intent();
            unsubIntent.putExtra("unsubType", unsubNodeReq.getPubType());
            unsubIntent.setAction(ChatWithNodeActivity.UNSUBD_SUCCESS);
            context.sendBroadcast(unsubIntent);
        }

    }

}
