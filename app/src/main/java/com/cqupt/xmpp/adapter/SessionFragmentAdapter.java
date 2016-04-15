package com.cqupt.xmpp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cqupt.xmpp.R;
import com.cqupt.xmpp.bean.ChatSession;
import com.cqupt.xmpp.widght.CircleImageView;

import java.util.ArrayList;

import static com.cqupt.xmpp.R.id.session_user_status;

/**
 * Created by tiandawu on 2016/4/13.
 */
public class SessionFragmentAdapter extends RecyclerView.Adapter {


    private Context mContext;
    private ArrayList<ChatSession> mChatSessions;
    private OnClickListener mListener;

    public SessionFragmentAdapter(Context context, ArrayList<ChatSession> sessions) {
        this.mChatSessions = sessions;
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(View.inflate(mContext, R.layout.item_session_child, null));
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            String from = mChatSessions.get(position).getFrom();
            myViewHolder.userName.setText(from.substring(0, from.lastIndexOf("@")));
            myViewHolder.sessionContent.setText("测到的温度为：" + mChatSessions.get(position).getBody());

            if (mListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getLayoutPosition();
                        mListener.onItemClick(holder.itemView, position);
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getLayoutPosition();
                        mListener.onItemLongClick(holder.itemView, position);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mChatSessions.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView userImage;
        private TextView userName, sessionContent, userStatus;
        private ImageView userStatusImage;

        public MyViewHolder(View itemView) {
            super(itemView);
            userImage = (CircleImageView) itemView.findViewById(R.id.session_user_img);
            userStatusImage = (ImageView) itemView.findViewById(R.id.session_user_status_img);
            userStatus = (TextView) itemView.findViewById(session_user_status);
            userName = (TextView) itemView.findViewById(R.id.session_user_name);
            sessionContent = (TextView) itemView.findViewById(R.id.session_content);
        }
    }

    public interface OnClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public void setOnClickListener(OnClickListener listener) {
        this.mListener = listener;
    }

}
