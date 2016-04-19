package com.cqupt.xmpp.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cqupt.xmpp.R;
import com.cqupt.xmpp.activity.ChatWithNodeActivity;
import com.cqupt.xmpp.adapter.SessionFragmentAdapter;
import com.cqupt.xmpp.base.BaseFragment;
import com.cqupt.xmpp.bean.ChatSession;
import com.cqupt.xmpp.db.ChatSesionDao;

import java.util.ArrayList;


/**
 * Created by tiandawu on 2016/3/31.
 */
public class MessageFragment extends BaseFragment {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private ChatSesionDao mChatSesionDao;
    private ArrayList<ChatSession> mChatSessions;
    private SessionFragmentAdapter mAdapter;
    private BroadcastReceiver mReceiver;
    public static final String RECEIVED_NEW_SESSION = "received_new_session";

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.frag_message, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.session_SwipeRefreshLayout);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.session_RecyclerVeiw);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.text_color);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorPrimary);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        return view;
    }

    @Override
    protected void initData() {
        mChatSesionDao = new ChatSesionDao(getActivity());
        mChatSessions = new ArrayList<>();

        mChatSessions = mChatSesionDao.queryMsg();
        mAdapter = new SessionFragmentAdapter(getActivity(), mChatSessions);
        mRecyclerView.setAdapter(mAdapter);
        mSwipeRefreshLayout.setRefreshing(false);

        mAdapter.setOnClickListener(new SessionFragmentAdapter.OnClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String from = mChatSessions.get(position).getFrom();
                String childName = from.substring(0, from.lastIndexOf("@"));
                String groupName = from.substring(from.lastIndexOf("/") + 1);
                String childJid = childName + "@xmpp";

                Intent intent = new Intent(getActivity(), ChatWithNodeActivity.class);
                intent.putExtra(ContactFragment.GROUP_NAME, groupName);
                intent.putExtra(ContactFragment.CHILD_NAME, childName);
                intent.putExtra(ContactFragment.CHILD_JID, childJid);
                getActivity().startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

    }


    /**
     * 刷新数据
     */
    private void refreshData() {
        mChatSessions.clear();
        mChatSessions.addAll(mChatSesionDao.queryMsg());
        mAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void initBroadcastReceiver() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                refreshData();
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(RECEIVED_NEW_SESSION);
        getActivity().registerReceiver(mReceiver, filter);
    }


    @Override
    public void onResume() {
        super.onResume();
        initBroadcastReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mReceiver);
    }
}
