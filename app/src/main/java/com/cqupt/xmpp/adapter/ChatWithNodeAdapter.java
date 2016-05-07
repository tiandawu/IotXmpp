package com.cqupt.xmpp.adapter;

import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cqupt.xmpp.R;
import com.cqupt.xmpp.bean.ChatMessage;
import com.cqupt.xmpp.db.ChatMsgDao;
import com.cqupt.xmpp.utils.ToastUtils;
import com.cqupt.xmpp.widght.ChoiceListDialog;

import java.util.ArrayList;

/**
 * Created by tiandawu on 2016/4/12.
 */
public class ChatWithNodeAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ChatMessage> chatMessages;
    private ChatMsgDao mChatMsgDao;

    public ChatWithNodeAdapter(Context mContext, ArrayList<ChatMessage> chatMessages) {
        this.mContext = mContext;
        this.chatMessages = chatMessages;
        mChatMsgDao = new ChatMsgDao(mContext);
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
    public View getView(final int position, View convertView, ViewGroup parent) {
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

        if (chatMessage.getBody().contains("当前")) {
            holder.content.setText(chatMessage.getBody());
        } else {
            holder.content.setText("测到的值为：" + chatMessage.getBody());
        }


        holder.content.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ArrayList<String> list = new ArrayList<>();
                list.add("删除本条消息");
                ChoiceListDialog dialog = new ChoiceListDialog(mContext, list);
                dialog.setOnChoiceListItemClickListener(new ChoiceListDialog.OnChoiceListItemClickListener() {
                    @Override
                    public void onListItemClick(int index) {
                        long result = mChatMsgDao.deleteMsgByTime(chatMessages.get(position).getTime());
                        if (result > 0) {
                            ToastUtils.showShortToastInCenter(mContext, "删除成功");
                            chatMessages.remove(position);
                            notifyDataSetChanged();
                        } else {
                            ToastUtils.showShortToastInCenter(mContext, "删除失败");
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });


        final MyViewHolder finalHolder = holder;
        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> list = new ArrayList<String>();
                list.add("复制文本");
                ChoiceListDialog dialog = new ChoiceListDialog(mContext, list);
                dialog.setOnChoiceListItemClickListener(new ChoiceListDialog.OnChoiceListItemClickListener() {
                    @Override
                    public void onListItemClick(int index) {
                        ClipboardManager clipboardManager = (ClipboardManager) mContext.getSystemService(mContext.CLIPBOARD_SERVICE);
                        clipboardManager.setText(finalHolder.content.getText());
//                        clipboardManager.setPrimaryClip(ClipData.newPlainText("data", finalHolder.content.getText()));
                        ToastUtils.showShortToastInCenter(mContext, "复制成功");
                    }
                });
                dialog.show();
            }
        });

        return convertView;
    }


    private class MyViewHolder {
        private TextView date, content;
    }
}

