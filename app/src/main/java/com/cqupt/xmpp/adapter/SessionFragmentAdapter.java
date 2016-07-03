package com.cqupt.xmpp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cqupt.xmpp.R;
import com.cqupt.xmpp.bean.ChatSession;
import com.cqupt.xmpp.fragment.MessageFragment;
import com.cqupt.xmpp.listener.MsgListener;
import com.cqupt.xmpp.listener.SessionAlarmListener;
import com.cqupt.xmpp.manager.XmppConnectionManager;
import com.cqupt.xmpp.utils.PreferencesUtils;
import com.cqupt.xmpp.widght.CircleImageView;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;

import java.util.ArrayList;

import static com.cqupt.xmpp.R.id.session_user_status;

/**
 * Created by tiandawu on 2016/4/13.
 */
public class SessionFragmentAdapter extends RecyclerView.Adapter<SessionFragmentAdapter.MyViewHolder> implements SessionAlarmListener {


    private Context mContext;
    private ArrayList<ChatSession> mChatSessions;
    private OnClickListener mListener;
    private Roster mRoster;

    public SessionFragmentAdapter(Context context, ArrayList<ChatSession> sessions) {
        this.mChatSessions = sessions;
        this.mContext = context;
        mRoster = XmppConnectionManager.getXmppconnectionManager().getRoster();
        MsgListener.setSessionAlarmListener(this);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(View.inflate(mContext, R.layout.item_session_child, null));
        return holder;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        String from = mChatSessions.get(position).getFrom();
        String userName = from.substring(0, from.lastIndexOf("@"));
//        String groupName = from.substring(from.lastIndexOf("/") + 1, from.length());
//        holder.userName.setText(userName);

        if ("temprature1".equals(userName)) {
            holder.userName.setText("温度传感器1");
            holder.userImage.setImageResource(R.mipmap.node_head_img);
        } else if ("temprature2".equals(userName)) {
            holder.userName.setText("温度传感器2");
            holder.userImage.setImageResource(R.mipmap.node_head_img);
        } else if ("A1".equals(userName)) {
            holder.userName.setText("风扇");
            holder.userImage.setImageResource(R.mipmap.node_fan);
        } else if ("A2".equals(userName)) {
            holder.userName.setText("直流电机");
            holder.userImage.setImageResource(R.mipmap.zldj);
        } else if ("A3".equals(userName)) {
            holder.userName.setText("LED灯");
            holder.userImage.setImageResource(R.mipmap.led);
        } else if ("A4".equals(userName)) {
            holder.userName.setText("步进电机");
            holder.userImage.setImageResource(R.mipmap.bjdj);
        } else if ("B1".equals(userName)) {
            holder.userName.setText("门磁");
            holder.userImage.setImageResource(R.mipmap.menci);
        } else if ("B2".equals(userName)) {
            holder.userName.setText("光电接近传感器");
            holder.userImage.setImageResource(R.mipmap.gdcgq);
        } else if ("light1".equals(userName)) {
            holder.userName.setText("光照传感器1");
            holder.userImage.setImageResource(R.mipmap.light);
        } else if ("light2".equals(userName)) {
            holder.userName.setText("光照传感器2");
            holder.userImage.setImageResource(R.mipmap.light);
        } else if ("smoke1".equals(userName)) {
            holder.userName.setText("烟雾传感器1");
            holder.userImage.setImageResource(R.mipmap.smoke);
        } else if ("smoke2".equals(userName)) {
            holder.userName.setText("烟雾传感器2");
            holder.userImage.setImageResource(R.mipmap.smoke);
        }

//        Log.e("tt", "userName == " + userName);
//        Log.e("tt", "ItemName == " + PreferencesUtils.getSharePreStr(mContext, "clickedItemName"));

        if (userName.equals(PreferencesUtils.getSharePreStr(mContext, "sessionItemName"))) {
            holder.sessionAlarm.setVisibility(View.GONE);
            PreferencesUtils.putSharePre(mContext, "sessionWhoAlarm", "");
            PreferencesUtils.putSharePre(mContext, "sessionItemName", "");
            Log.e("tt", "gone alarm");
        }

        if (from.equals(PreferencesUtils.getSharePreStr(mContext, "sessionWhoAlarm"))) {
            holder.sessionAlarm.setVisibility(View.VISIBLE);
            Log.e("tt", "show alarm");
            PreferencesUtils.putSharePre(mContext, "sessionWhoAlarm", "");
        }

        holder.sessionContent.setText(mChatSessions.get(position).getBody());

        Log.e("tt", "from = " + from);
        RosterEntry entry = mRoster.getEntry(from.substring(0, from.lastIndexOf("/")));
        Log.e("tt", "entry = " + entry);
        final String type = mRoster.getPresence(entry.getUser()) + "";

        if (type.equals("available (online)")) {
            holder.userStatus.setText(mContext.getResources().getText(R.string.on_line));
            holder.nodeSleep.setVisibility(View.GONE);
            holder.nodeOffline.setVisibility(View.GONE);
            holder.nodeBusy.setVisibility(View.GONE);
            holder.userStatusImage.setVisibility(View.VISIBLE);
        } else if (type.equals("available (sleep)")) {
            holder.userStatus.setText(mContext.getResources().getText(R.string.on_sleep));
            holder.userStatusImage.setVisibility(View.GONE);
            holder.nodeBusy.setVisibility(View.GONE);
            holder.nodeOffline.setVisibility(View.GONE);
            holder.nodeSleep.setVisibility(View.VISIBLE);
        } else if (type.equals("available (busy)")) {
            holder.userStatus.setText(mContext.getResources().getText(R.string.on_busy));
            holder.userStatusImage.setVisibility(View.GONE);
            holder.nodeOffline.setVisibility(View.GONE);
            holder.nodeSleep.setVisibility(View.GONE);
            holder.nodeBusy.setVisibility(View.VISIBLE);
        } else {
            holder.userStatus.setText(mContext.getResources().getText(R.string.off_line));
            holder.userStatusImage.setVisibility(View.GONE);
            holder.nodeSleep.setVisibility(View.GONE);
            holder.nodeBusy.setVisibility(View.GONE);
            holder.nodeOffline.setVisibility(View.VISIBLE);
        }

        if (mListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    mListener.onItemClick(holder.itemView, position);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getLayoutPosition();
                    mListener.onItemLongClick(holder.itemView, position);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mChatSessions.size();
    }


    @Override
    public void showSessionAlarm(String from) {
//        Log.e("tt", "ttFrom == " + from);
        PreferencesUtils.putSharePre(mContext, "sessionWhoAlarm", from);
        Intent intent = new Intent();
        intent.setAction(MessageFragment.RECEIVED_NEW_SESSION);
        mContext.sendBroadcast(intent);

    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView userImage;
        private TextView userName, sessionContent, userStatus, nodeSleep, nodeBusy, nodeOffline;
        private ImageView userStatusImage;
        private LinearLayout sessionAlarm;

        public MyViewHolder(View itemView) {
            super(itemView);
            userImage = (CircleImageView) itemView.findViewById(R.id.session_user_img);
            userStatusImage = (ImageView) itemView.findViewById(R.id.session_user_status_img);
            userStatus = (TextView) itemView.findViewById(session_user_status);
            userName = (TextView) itemView.findViewById(R.id.session_user_name);
            sessionContent = (TextView) itemView.findViewById(R.id.session_content);
            nodeSleep = (TextView) itemView.findViewById(R.id.session_user_status_sleep);
            nodeBusy = (TextView) itemView.findViewById(R.id.session_user_status_busy);
            nodeOffline = (TextView) itemView.findViewById(R.id.session_user_status_offline);
            sessionAlarm = (LinearLayout) itemView.findViewById(R.id.session_alarm);
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
