package com.cqupt.xmpp.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.cqupt.xmpp.R;
import com.cqupt.xmpp.activity.ChatWithNodeActivity;
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
                if (intent.getAction().equals(FRIENDS_STATUS_CHANGED)) {
//                    refreshData();
                    myAdapter.notifyDataSetChanged();
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
        super.onDestroy();
    }
}

