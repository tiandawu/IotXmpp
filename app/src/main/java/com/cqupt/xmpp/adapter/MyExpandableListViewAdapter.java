package com.cqupt.xmpp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cqupt.xmpp.R;
import com.cqupt.xmpp.fragment.ContactFragment;
import com.cqupt.xmpp.listener.AlarmListener;
import com.cqupt.xmpp.listener.MsgListener;
import com.cqupt.xmpp.manager.XmppConnectionManager;
import com.cqupt.xmpp.utils.PreferencesUtils;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;

import java.util.ArrayList;

/**
 * Created by tiandawu on 2016/4/8.
 */
public class MyExpandableListViewAdapter extends BaseExpandableListAdapter implements AlarmListener {
    private Context context;
    private ArrayList<RosterGroup> mGroups;
    private Roster roster;
    private XmppConnectionManager xmppConnectionManager = XmppConnectionManager.getXmppconnectionManager();
//    public static String nodeName = "";


    public MyExpandableListViewAdapter(Context context, ArrayList<RosterGroup> mGroups) {
        this.context = context;
        this.mGroups = mGroups;
        roster = xmppConnectionManager.getRoster();

//        /**
//         * 改变组排序
//         */
//
//        sortGroup(mGroups);
        MsgListener.setAlarmLisener(this);
    }

    @Override
    public int getGroupCount() {
        return mGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mGroups.get(groupPosition).getEntries().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return getRosterEntries(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewholder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_contact_group, null);
            groupViewholder = new GroupViewHolder();
            groupViewholder.groupIndicator = (ImageView) convertView.findViewById(R.id.group_indicator);
            groupViewholder.groupName = (TextView) convertView.findViewById(R.id.group_name);
            groupViewholder.onlineCount = (TextView) convertView.findViewById(R.id.online_count);
            convertView.setTag(groupViewholder);

        }
        groupViewholder = (GroupViewHolder) convertView.getTag();
        groupViewholder.groupIndicator.setSelected(isExpanded);
        String groupName = getRosterGroup(groupPosition).getName();

        if ("temprature".equals(groupName)) {
            groupViewholder.groupName.setText("温湿度传感器");
        } else if ("A".equals(groupName)) {
            groupViewholder.groupName.setText("执行器");
        } else if ("B".equals(groupName)) {
            groupViewholder.groupName.setText("感应器");
        } else if ("light".equals(groupName)) {
            groupViewholder.groupName.setText("光线传感器");
        } else if ("smoke".equals(groupName)) {
            groupViewholder.groupName.setText("烟雾传感器");
        }

//        Log.e("tt", "groupName = " + mGroups.get(groupPosition).getName());

        groupViewholder.onlineCount.setText(getGroupOlineNumber(groupPosition) + "/" + getRosterGroup(groupPosition).getEntries().size());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewholder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_contact_child, null);
            childViewholder = new ChildViewHolder();
            childViewholder.userName = (TextView) convertView.findViewById(R.id.contact_user_name);
            childViewholder.userStatus = (TextView) convertView.findViewById(R.id.contact_user_status);
            childViewholder.userHeadImg = (ImageView) convertView.findViewById(R.id.contact_user_img);
            childViewholder.userStatusImg = (ImageView) convertView.findViewById(R.id.contact_user_status_online);
            childViewholder.nodeSleep = (TextView) convertView.findViewById(R.id.contact_user_status_sleep);
            childViewholder.nodeBusy = (TextView) convertView.findViewById(R.id.contact_user_status_busy);
            childViewholder.nodeOffline = (TextView) convertView.findViewById(R.id.contact_user_status_offline);
            childViewholder.childAlarm = (LinearLayout) convertView.findViewById(R.id.contact_alarm);
            convertView.setTag(childViewholder);
        }

        childViewholder = (ChildViewHolder) convertView.getTag();
        RosterEntry rosterEntry = getRosterEntries(groupPosition).get(childPosition);
        String childName = rosterEntry.getName();

        String clickedItemName = PreferencesUtils.getSharePreStr(context, "clickedItemName");


        /**
         * 如果条目被点击过了，则清除报警，并清除被点击过的条目，隐藏报警图标
         */
        if (childName.equals(clickedItemName)) {
            childViewholder.childAlarm.setVisibility(View.GONE);
            PreferencesUtils.putSharePre(context, "whoAlarm", "");
            PreferencesUtils.putSharePre(context, "clickedItemName", "");
        }


        /**
         * 如果报警了，显示报警图标，然后清除报警标记
         */
        if (PreferencesUtils.getSharePreStr(context, "whoAlarm").equals(childName)) {
            childViewholder.childAlarm.setVisibility(View.VISIBLE);
            PreferencesUtils.putSharePre(context, "whoAlarm", "");
        }


        Log.e("tt", "childName = " + childName);

        if ("temprature1".equals(childName)) {
            childViewholder.userName.setText("温度传感器1");
            childViewholder.userHeadImg.setImageResource(R.mipmap.node_head_img);
        } else if ("temprature2".equals(childName)) {
            childViewholder.userName.setText("温度传感器2");
            childViewholder.userHeadImg.setImageResource(R.mipmap.node_head_img);
        } else if ("A1".equals(childName)) {
            childViewholder.userName.setText("风扇");
            childViewholder.userHeadImg.setImageResource(R.mipmap.node_fan);
        } else if ("A2".equals(childName)) {
            childViewholder.userName.setText("直流电机");
            childViewholder.userHeadImg.setImageResource(R.mipmap.zldj);
        } else if ("A3".equals(childName)) {
            childViewholder.userName.setText("LED灯");
            childViewholder.userHeadImg.setImageResource(R.mipmap.led);
        } else if ("A4".equals(childName)) {
            childViewholder.userName.setText("步进电机");
            childViewholder.userHeadImg.setImageResource(R.mipmap.bjdj);
        } else if ("B1".equals(childName)) {
            childViewholder.userName.setText("门磁");
            childViewholder.userHeadImg.setImageResource(R.mipmap.menci);
        } else if ("B2".equals(childName)) {
            childViewholder.userName.setText("光电接近传感器");
            childViewholder.userHeadImg.setImageResource(R.mipmap.gdcgq);
        } else if ("light1".equals(childName)) {
            childViewholder.userName.setText("光照传感器1");
            childViewholder.userHeadImg.setImageResource(R.mipmap.light);
        } else if ("light2".equals(childName)) {
            childViewholder.userName.setText("光照传感器2");
            childViewholder.userHeadImg.setImageResource(R.mipmap.light);
        } else if ("smoke1".equals(childName)) {
            childViewholder.userName.setText("烟雾传感器1");
            childViewholder.userHeadImg.setImageResource(R.mipmap.smoke);
        } else if ("smoke2".equals(childName)) {
            childViewholder.userName.setText("烟雾传感器2");
            childViewholder.userHeadImg.setImageResource(R.mipmap.smoke);
        }


        if ((roster.getPresence(rosterEntry.getUser()) + "").equals("available (online)")) {
            childViewholder.userStatus.setText(context.getResources().getText(R.string.on_line));
            childViewholder.nodeSleep.setVisibility(View.GONE);
            childViewholder.nodeOffline.setVisibility(View.GONE);
            childViewholder.nodeBusy.setVisibility(View.GONE);
            childViewholder.userStatusImg.setVisibility(View.VISIBLE);
        } else if ((roster.getPresence(rosterEntry.getUser()) + "").equals("available (sleep)")) {
            childViewholder.userStatus.setText(context.getResources().getText(R.string.on_sleep));
            childViewholder.userStatusImg.setVisibility(View.GONE);
            childViewholder.nodeBusy.setVisibility(View.GONE);
            childViewholder.nodeOffline.setVisibility(View.GONE);
            childViewholder.nodeSleep.setVisibility(View.VISIBLE);
        } else if ((roster.getPresence(rosterEntry.getUser()) + "").equals("available (busy)")) {
            childViewholder.userStatus.setText(context.getResources().getText(R.string.on_busy));
            childViewholder.userStatusImg.setVisibility(View.GONE);
            childViewholder.nodeOffline.setVisibility(View.GONE);
            childViewholder.nodeSleep.setVisibility(View.GONE);
            childViewholder.nodeBusy.setVisibility(View.VISIBLE);
        } else {
            childViewholder.userStatus.setText(context.getResources().getText(R.string.off_line));
            childViewholder.userStatusImg.setVisibility(View.GONE);
            childViewholder.nodeSleep.setVisibility(View.GONE);
            childViewholder.nodeBusy.setVisibility(View.GONE);
            childViewholder.nodeOffline.setVisibility(View.VISIBLE);
        }
        return convertView;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void showAlarm(String from) {
        String nodeName = from.substring(0, from.indexOf("@"));
//        this.nodeName = nodeName;
//        Log.e("tt", "name = " + nodeName);
//        notifyDataSetChanged();

        PreferencesUtils.putSharePre(context, "whoAlarm", nodeName);

        Intent intent = new Intent();
        intent.setAction(ContactFragment.FRIENDS_STATUS_CHANGED);
        context.sendBroadcast(intent);

    }

    private class GroupViewHolder {
        ImageView groupIndicator;
        TextView groupName, onlineCount;
    }

    private class ChildViewHolder {
        ImageView userHeadImg, userStatusImg;
        TextView userName, userStatus, nodeSleep, nodeBusy, nodeOffline;
        LinearLayout childAlarm;
    }


    /**
     * 获取联系人分组
     *
     * @param groupIndex 组索引
     * @return
     */
    private RosterGroup getRosterGroup(int groupIndex) {
        return mGroups.get(groupIndex);
    }

    /**
     * 获取组联系人
     *
     * @param groupIndex 组索引
     * @return
     */
    private ArrayList<RosterEntry> getRosterEntries(int groupIndex) {
        return new ArrayList<>(getRosterGroup(groupIndex).getEntries());
    }

    /**
     * 获取一个组的在线人数
     *
     * @param groupPosition
     * @return
     */
    private int getGroupOlineNumber(int groupPosition) {
        int onlineCount = 0;


        for (RosterEntry rosterEntry : getRosterEntries(groupPosition)) {
            if ((roster.getPresence(rosterEntry.getUser()).getType() + "").equals("available")) {
                onlineCount++;
            }
        }
        return onlineCount;
    }

}
