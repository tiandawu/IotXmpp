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
import com.cqupt.xmpp.widght.CircleImageView;

import java.util.ArrayList;

/**
 * Created by tiandawu on 2016/4/12.
 */
public class ChatWithNodeAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ChatMessage> chatMessages;
    private ChatMsgDao mChatMsgDao;
    private final int VIEW_TYPE_COUNT = 2;

    public ChatWithNodeAdapter(Context mContext, ArrayList<ChatMessage> chatMessages) {
        this.mContext = mContext;
        this.chatMessages = chatMessages;
        mChatMsgDao = new ChatMsgDao(mContext);
    }

    @Override
    public int getItemViewType(int position) {

        ChatMessage chatMessage = chatMessages.get(position);
        String flag = chatMessage.getFlag();
        if ("true".equals(flag)) {
            //返回0表示显示我的对话
            return 0;
        }
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
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
        MyViewHolder holder = null;
        int type = getItemViewType(position);
        if (type == 0) {
            if (convertView == null) {
                holder = new MyViewHolder();
                convertView = View.inflate(mContext, R.layout.item_my_response, null);
                holder.myDate = (TextView) convertView.findViewById(R.id.chat_item_my_date);
                holder.myText = (TextView) convertView.findViewById(R.id.chat_item_my_text);
                convertView.setTag(holder);
            } else {
                holder = (MyViewHolder) convertView.getTag();
            }
            ChatMessage chatMessage = chatMessages.get(position);
            holder.myDate.setText(chatMessage.getTime());
            holder.myText.setText(chatMessage.getBody());
            holder.myText.setOnLongClickListener(new View.OnLongClickListener() {
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
            holder.myText.setOnClickListener(new View.OnClickListener() {
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

        } else if (type == 1) {
            if (convertView == null) {
                holder = new MyViewHolder();
                convertView = View.inflate(mContext, R.layout.item_server_response, null);
                holder.date = (TextView) convertView.findViewById(R.id.chat_item_node_date);
                holder.content = (TextView) convertView.findViewById(R.id.chat_item_node_text);
                holder.nodeHeadImg = (CircleImageView) convertView.findViewById(R.id.chat_node_head_img);
                convertView.setTag(holder);
            } else {
                holder = (MyViewHolder) convertView.getTag();
            }

            ChatMessage chatMessage = chatMessages.get(position);
            holder.date.setText(chatMessage.getTime());
            String from = chatMessages.get(position).getFrom();
            String groupName = from.substring(from.indexOf("/") + 1, from.length());
//            if (chatMessage.getBody().contains("当前")) {
//                holder.content.setText(chatMessage.getBody());
//            } else
            if ("订阅成功".equals(chatMessage.getBody()) || "设置成功".equals(chatMessage.getBody())) {
                if ("temprature".equals(groupName)) {
                    holder.nodeHeadImg.setImageResource(R.mipmap.node_head_img);
                } else if ("smoke".equals(groupName)) {
                    holder.nodeHeadImg.setImageResource(R.mipmap.smoke);
                } else if ("light".equals(groupName)){
                    holder.nodeHeadImg.setImageResource(R.mipmap.light);
                }
                holder.content.setText(chatMessage.getBody());
            } else if ("取消订阅成功".equals(chatMessage.getBody())) {
                holder.content.setText(chatMessage.getBody());
            } else {


//                Log.e("tt", "body = " + chatMessage.getBody());

                if (!chatMessage.getBody().contains("当前")) {
                    if ("temprature".equals(groupName)) {
                        holder.content.setText("当前温度为：" + chatMessage.getBody() + " ℃");
                        holder.nodeHeadImg.setImageResource(R.mipmap.node_head_img);
                    } else if ("smoke".equals(groupName)) {
                        holder.content.setText("当前浓度为：" + chatMessage.getBody() + " ppm");
                        holder.nodeHeadImg.setImageResource(R.mipmap.smoke);
                    } else if ("light".equals(groupName)) {
                        holder.content.setText("当前光照强度为：" + chatMessage.getBody()+" lx");
                        holder.nodeHeadImg.setImageResource(R.mipmap.light);
                    }
                } else {
                    if ("temprature".equals(groupName)) {
                        holder.content.setText(chatMessage.getBody());
                        holder.nodeHeadImg.setImageResource(R.mipmap.node_head_img);
                    } else if ("smoke".equals(groupName)) {
                        holder.content.setText(chatMessage.getBody());
                        holder.nodeHeadImg.setImageResource(R.mipmap.smoke);
                    } else if ("light".equals(groupName)) {
                        holder.content.setText(chatMessage.getBody());
                        holder.nodeHeadImg.setImageResource(R.mipmap.light);
                    }
                }


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

        }


        return convertView;
    }


    private class MyViewHolder {
        private TextView date, content, myText, myDate;
        private CircleImageView nodeHeadImg;
    }
}

