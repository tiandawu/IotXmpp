package com.cqupt.xmpp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cqupt.xmpp.R;
import com.cqupt.xmpp.bean.ChatMessage;

import java.util.ArrayList;

/**
 * Created by tiandawu on 2016/4/12.
 */
public class ChatWithNodeAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ChatMessage> chatMessages;

    public ChatWithNodeAdapter(Context mContext, ArrayList<ChatMessage> chatMessages) {
        this.mContext = mContext;
        this.chatMessages = chatMessages;
    }


    @Override
    public int getCount() {
        return chatMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return chatMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder holder;
        if (convertView == null) {
            holder = new MyViewHolder();
            convertView = View.inflate(mContext, R.layout.item_server_response, null);
            holder.date = (TextView) convertView.findViewById(R.id.chat_item_node_date);
            holder.content = (TextView) convertView.findViewById(R.id.chat_item_node_text);
            convertView.setTag(holder);
        }

        holder = (MyViewHolder) convertView.getTag();
        ChatMessage chatMessage = chatMessages.get(position);
        holder.date.setText(chatMessage.getTime());
        holder.content.setText("测到的温度为：" + chatMessage.getBody());
        return convertView;
    }


    private class MyViewHolder {
        private TextView date, content;
    }
}

