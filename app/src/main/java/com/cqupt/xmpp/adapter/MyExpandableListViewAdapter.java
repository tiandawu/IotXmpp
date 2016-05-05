package com.cqupt.xmpp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cqupt.xmpp.R;
import com.cqupt.xmpp.manager.XmppConnectionManager;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;

import java.util.ArrayList;

/**
 * Created by tiandawu on 2016/4/8.
 */
public class MyExpandableListViewAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<RosterGroup> mGroups;
    private Roster roster;
    private XmppConnectionManager xmppConnectionManager = XmppConnectionManager.getXmppconnectionManager();


    public MyExpandableListViewAdapter(Context context, ArrayList<RosterGroup> mGroups) {
        this.context = context;
        this.mGroups = mGroups;
        roster = xmppConnectionManager.getRoster();
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
        groupViewholder.groupName.setText(getRosterGroup(groupPosition).getName());
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
            childViewholder.userStatusImg = (ImageView) convertView.findViewById(R.id.contact_user_status_img);
            childViewholder.userGroup = (TextView) convertView.findViewById(R.id.contact_user_group);
            convertView.setTag(childViewholder);
        }

        childViewholder = (ChildViewHolder) convertView.getTag();
        RosterEntry rosterEntry = getRosterEntries(groupPosition).get(childPosition);
        childViewholder.userName.setText(rosterEntry.getName());
        childViewholder.userGroup.setText(getRosterGroup(groupPosition).getName());
        if ((roster.getPresence(rosterEntry.getUser()).getType() + "").equals("available")) {
            childViewholder.userStatus.setText(context.getResources().getText(R.string.on_line));
            childViewholder.userStatusImg.setVisibility(View.VISIBLE);
        } else {
            childViewholder.userStatus.setText(context.getResources().getText(R.string.off_line));
            childViewholder.userStatusImg.setVisibility(View.GONE);
        }
        return convertView;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class GroupViewHolder {
        ImageView groupIndicator;
        TextView groupName, onlineCount;
    }

    private class ChildViewHolder {
        ImageView userHeadImg, userStatusImg;
        TextView userName, userStatus, userGroup;
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
