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

        //获取到数据
        if (packet instanceof GetDataResp) {
            GetDataResp getDataResp = (GetDataResp) packet;
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setFrom(getDataResp.getFrom());
            chatMessage.setTo(getDataResp.getTo());
            chatMessage.setTime(DateUtils.getNowDateTime());
            chatMessage.setOwner(getDataResp.getTo());

            String var = getDataResp.getVar();
            String value = getDataResp.getValue();

            if (var.equals("samplePeri")) {
                chatMessage.setBody("当前周期为：" + value);
            } else if (var.equals("highLimit")) {
                chatMessage.setBody("当前上限为：" + value);
            } else if (var.equals("lowLimit")) {
                chatMessage.setBody("当前下限为：" + value);
            } else {
                chatMessage.setBody(value);
            }
            mChatMsgDao.insert(chatMessage);

            Intent intent = new Intent();
            intent.setAction(ChatWithNodeActivity.RECEIVED_MESSAGE);
            context.sendBroadcast(intent);


            ChatSession session = new ChatSession();
            session.setFrom(getDataResp.getFrom());
            session.setTo(getDataResp.getTo());
            session.setBody(getDataResp.getValue());
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
