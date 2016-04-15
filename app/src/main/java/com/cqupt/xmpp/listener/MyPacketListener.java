package com.cqupt.xmpp.listener;

import android.util.Log;

import com.cqupt.xmpp.service.IotXmppService;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

public class MyPacketListener implements PacketListener {
    IotXmppService context;

    public MyPacketListener(IotXmppService context) {
        this.context = context;
    }

    @Override
    public void processPacket(Packet packet) {
        Log.e("tt", "收到包！");

//        Log.e("tt", "packet=" + packet.toXML());

        if (packet.getFrom().equals(packet.getTo())) {
            return;
        }

        if (packet instanceof IQ) {
            IQ iq = (IQ) packet;
            Log.e("tt", "iq=" + iq.toXML());
        }

        if (packet instanceof Presence) {
            Presence presence = (Presence) packet;

            Log.e("tt", "presence=" + presence.toXML());
            Log.e("tt", "type=" + presence.getType());
            String type = presence.getType() + "";
//            if (type.equals("available")) {
//                Log.e("tt", "online");
//
//                Intent intent = new Intent();
//                intent.setAction(ContactFragment.FRIENDS_STATUS_CHANGED);
//                context.sendBroadcast(intent);
//            }

//            String from = presence.getFrom();// 发送方
//            String to = presence.getTo();// 接收方
//            if (presence.getType().equals(Presence.Type.subscribe)) {// 好友申请
//                Log.e("tt", "好友申请");
//            } else if (presence.getType().equals(Presence.Type.subscribed)) {// 同意添加好友
//                Log.e("tt", "同意添加好友");
//            } else if (presence.getType().equals(Presence.Type.unsubscribe)) {// 拒绝添加好友
//                // 和
//                // 删除好友
//                Log.e("tt", "拒绝添加好友");
//            } else if (presence.getType().equals(Presence.Type.unsubscribed)) {
//
//            } else if (presence.getType().equals(Presence.Type.unavailable)) {// 好友下线
//                // 要更新好友列表，可以在这收到包后，发广播到指定页面
//                // 更新列表
//                Log.e("tt", from);
//                Log.e("tt", "好友下线");
//                Intent intent = new Intent();
//                intent.setAction(ContactFragment.FRIENDS_STATUS_CHANGED);
//                context.sendBroadcast(intent);
//            } else if (presence.getType().equals(Presence.Type.available)) {// 好友上线
//
//                Presence.Mode mode = presence.getMode();
//
//                if (mode.equals(Presence.Mode.chat)) {
//                    Log.e("tt", "chat");
//                } else if (mode.equals(Presence.Mode.away)) {
//                    Log.e("tt", "away");
//                } else if (mode.equals(Presence.Mode.dnd)) {
//                    Log.e("tt", "dnd");
//                } else if (mode.equals(Presence.Mode.xa)) {
//                    Log.e("tt", "xa");
//                } else if (mode.equals(Presence.Mode.available)) {
//                    Log.e("tt", "available");
//                }
//                Log.e("tt", "好友上线");
//                Intent intent = new Intent();
//                intent.setAction(ContactFragment.FRIENDS_STATUS_CHANGED);
//                context.sendBroadcast(intent);
//            } else {
//                Log.e("tt", "error");
//            }
        }
    }

}
