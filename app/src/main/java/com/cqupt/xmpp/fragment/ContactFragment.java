package com.cqupt.xmpp.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.cqupt.xmpp.R;
import com.cqupt.xmpp.activity.BJActivity;
import com.cqupt.xmpp.activity.ChatActivity;
import com.cqupt.xmpp.activity.ChatWithNodeActivity;
import com.cqupt.xmpp.activity.DMActivity;
import com.cqupt.xmpp.activity.GDActivity;
import com.cqupt.xmpp.activity.KGNodeActivity;
import com.cqupt.xmpp.activity.LEDActivity;
import com.cqupt.xmpp.adapter.MyExpandableListViewAdapter;
import com.cqupt.xmpp.base.BaseFragment;
import com.cqupt.xmpp.manager.XmppConnectionManager;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;

import java.util.ArrayList;


/**
 * Created by tiandawu on 2016/3/31.
 */
public class ContactFragment extends BaseFragment {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ExpandableListView expandableListView;
    private MyExpandableListViewAdapter myAdapter;
    private ArrayList<RosterGroup> mGroups = new ArrayList<>();
    private XmppConnectionManager xmppConnectionManager = XmppConnectionManager.getXmppconnectionManager();
    private BroadcastReceiver receiver;
    public static final String FRIENDS_STATUS_CHANGED = "FriendStatusChanged";
    public static final String GROUP_NAME = "GroupName";
    public static final String CHILD_NAME = "ChildName";
    public static final String CHILD_JID = "ChildJID";

    @Override
    protected View initView(LayoutInflater inflater, final ViewGroup container) {
        View view = inflater.inflate(R.layout.frag_contact, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.contact_SwipeRefreshLayout);
        expandableListView = (ExpandableListView) view.findViewById(R.id.contact_ExpandableListView);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.text_color);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorPrimary);
//        mSwipeRefreshLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                mSwipeRefreshLayout.setRefreshing(true);
//            }
//        });
        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        initMyBroadcastReceiver();

        return view;
    }

    @Override
    protected void initData() {
        mGroups = xmppConnectionManager.getGroups();
//        mSwipeRefreshLayout.setRefreshing(false);
        sortGroup(mGroups);
        myAdapter = new MyExpandableListViewAdapter(getActivity(), mGroups);
        expandableListView.setAdapter(myAdapter);
        if (mGroups.size() == 0) {
            refreshData();
        }
        mSwipeRefreshLayout.setRefreshing(false);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                ArrayList<RosterEntry> rosterEntries = new ArrayList<>(mGroups.get(groupPosition).getEntries());
                String groupName = mGroups.get(groupPosition).getName();
                String childName = rosterEntries.get(childPosition).getName();

                if ("people".equals(groupName)) {

                    String childJid = rosterEntries.get(childPosition).getUser();
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra(ContactFragment.GROUP_NAME, groupName);
                    intent.putExtra(ContactFragment.CHILD_NAME, childName);
                    intent.putExtra(ContactFragment.CHILD_JID, childJid);
                    getActivity().startActivity(intent);

                    return true;
                } else if ("A".equals(groupName)) {
                    if ("A1".equals(childName) || "A2".equals(childName)) {
                        Intent intent = new Intent(getActivity(), KGNodeActivity.class);
                        intent.putExtra(KGNodeActivity.CHILD_NAME, childName);
                        startActivity(intent);
                    } else if ("A3".equals(childName)) {
                        Intent intent = new Intent(getActivity(), LEDActivity.class);
                        intent.putExtra(LEDActivity.CHILD_NAME, childName);
                        startActivity(intent);
                    } else if ("A4".equals(childName)) {

                        String status = xmppConnectionManager.getRoster().getPresence(childName + "@xmpp/A") + "";
                        Intent intent = new Intent(getActivity(), BJActivity.class);
                        intent.putExtra(BJActivity.CHILD_STATUS, status);
                        intent.putExtra(BJActivity.CHILD_NAME, childName);
                        startActivity(intent);
                    }

                    return true;
                } else if ("B".equals(groupName)) {

                    if ("B1".equals(childName)) {
                        Intent intent = new Intent(getActivity(), DMActivity.class);
                        intent.putExtra(DMActivity.CHILD_NAME, childName);
                        startActivity(intent);
                    } else if ("B2".equals(childName)) {
                        Intent intent = new Intent(getActivity(), GDActivity.class);
                        intent.putExtra(GDActivity.CHILD_NAME, childName);
                        startActivity(intent);
                    }

                    return true;
                }

                String childJid = rosterEntries.get(childPosition).getUser();
                Intent intent = new Intent(getActivity(), ChatWithNodeActivity.class);
                intent.putExtra(ContactFragment.GROUP_NAME, groupName);
                intent.putExtra(ContactFragment.CHILD_NAME, childName);
                intent.putExtra(ContactFragment.CHILD_JID, childJid);
                getActivity().startActivity(intent);
                return true;
            }
        });
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        mGroups.clear();
        mGroups.addAll(xmppConnectionManager.getGroups());
        sortGroup(mGroups);
        mSwipeRefreshLayout.setRefreshing(false);
        myAdapter.notifyDataSetChanged();
//        myAdapter.notifyDataSetInvalidated();
    }

    /**
     * 初始化广播接收者，主要用于接收好友上下线状态
     */
    private void initMyBroadcastReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (FRIENDS_STATUS_CHANGED.equals(action)) {
                    mGroups.clear();
                    mGroups.addAll(xmppConnectionManager.getGroups());
                    sortGroup(mGroups);
                    myAdapter.notifyDataSetChanged();
                    Log.e("tt", "上线了");
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FRIENDS_STATUS_CHANGED);
        getActivity().registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(receiver);
        receiver = null;
        super.onDestroy();
    }


    /**
     * 排序分组
     * @param groups
     */
    private void sortGroup(ArrayList<RosterGroup> groups) {
        for (int i = 0; i < groups.size(); i++) {
            if (i == 1 || i == 2) {
                RosterGroup group = groups.get(i + 2);
                groups.add(i+2,groups.get(i));
                groups.remove(i+3);
                groups.add(i,group);
                groups.remove(i + 1);
            }
        }
    }
}




