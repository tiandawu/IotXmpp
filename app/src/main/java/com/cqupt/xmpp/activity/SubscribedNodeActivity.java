package com.cqupt.xmpp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.cqupt.xmpp.R;
import com.cqupt.xmpp.adapter.MySubscribeNodesAdapter;
import com.cqupt.xmpp.bean.NodeSubStatus;
import com.cqupt.xmpp.db.NodeStatusDao;
import com.cqupt.xmpp.fragment.ContactFragment;
import com.cqupt.xmpp.widght.SwipeBackActivity;
import com.cqupt.xmpp.widght.SwipeBackLayout;

import java.util.ArrayList;

/**
 * Created by tiandawu on 2016/4/20.
 */
public class SubscribedNodeActivity extends SwipeBackActivity implements View.OnClickListener {

    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private LinearLayout backBtn;
    private NodeStatusDao mNodeStatusDao;
    private ArrayList<NodeSubStatus> mLists;
    private MySubscribeNodesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_subscribed_nods);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_ALL);
        initView();
        initData();
    }

    private void initView() {
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.subscribe_SwipeRefreshLayout);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_subscribed_nodes);
        backBtn = (LinearLayout) findViewById(R.id.titlebar_navigation);
        mNodeStatusDao = new NodeStatusDao(this);
        mLists = new ArrayList<>();
    }

    private void initData() {
        backBtn.setOnClickListener(this);
        mRefreshLayout.setColorSchemeResources(R.color.text_color);
        mRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorPrimary);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRefreshLayout.setRefreshing(true);
        mAdapter = new MySubscribeNodesAdapter(this, mLists);
        mRecyclerView.setAdapter(mAdapter);
        getDatas();
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDatas();
            }
        });

        mAdapter.setOnClickListener(new MySubscribeNodesAdapter.OnClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String nodeName = mLists.get(position).getNodeName();
                String childName = nodeName.substring(0, nodeName.lastIndexOf("@"));
                String groupName = nodeName.substring(nodeName.lastIndexOf("/") + 1);
                String childJid = childName + "@xmpp";

                Intent intent = new Intent(SubscribedNodeActivity.this, ChatWithNodeActivity.class);
                intent.putExtra(ContactFragment.GROUP_NAME, groupName);
                intent.putExtra(ContactFragment.CHILD_NAME, childName);
                intent.putExtra(ContactFragment.CHILD_JID, childJid);
                SubscribedNodeActivity.this.startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlebar_navigation:
                finish();
                break;
        }
    }

    private void getDatas() {
        mLists.clear();
        ArrayList<NodeSubStatus> nodes = mNodeStatusDao.querySubNodes();
        for (NodeSubStatus nod : nodes) {
            if (nod.getHighLimit().equals("false") && nod.getLowLimit().equals("false")
                    && nod.getPeriod().equals("false")) {
                //代表什么都没有订阅
                continue;
            }

            mLists.add(nod);
        }
        mRefreshLayout.setRefreshing(false);
        mAdapter.notifyDataSetChanged();
    }
}
