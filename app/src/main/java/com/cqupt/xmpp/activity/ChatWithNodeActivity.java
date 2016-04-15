package com.cqupt.xmpp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cqupt.xmpp.R;
import com.cqupt.xmpp.adapter.ChatWithNodeAdapter;
import com.cqupt.xmpp.bean.ChatMessage;
import com.cqupt.xmpp.db.ChatMsgDao;
import com.cqupt.xmpp.fragment.ContactFragment;
import com.cqupt.xmpp.manager.XmppConnectionManager;
import com.cqupt.xmpp.packet.GetNodeData;
import com.cqupt.xmpp.packet.SubscribNode;
import com.cqupt.xmpp.widght.ChoiceListDialog;
import com.cqupt.xmpp.widght.DropdownListView;
import com.cqupt.xmpp.widght.SwipeBackActivity;
import com.cqupt.xmpp.widght.SwipeBackLayout;

import org.jivesoftware.smack.packet.IQ;

import java.util.ArrayList;

/**
 * Created by tiandawu on 2016/4/9.
 */
public class ChatWithNodeActivity extends SwipeBackActivity implements View.OnClickListener, DropdownListView.OnRefreshListenerHeader {

    private LinearLayout titlebarBackBtn, moreOption;
    private TextView title, subscribeNode, readData, settingNode;
    private DropdownListView dropdownListView;
    private ChatWithNodeAdapter adapter;
    private String groupName, childName, childJid;
    private BroadcastReceiver receiver;
    private XmppConnectionManager manager = XmppConnectionManager.getXmppconnectionManager();
    private ChatMsgDao chatMsgDao;
    private ArrayList<ChatMessage> chatMessages;
    private int offset;
    public static final String RECEIVED_MESSAGE = "received_message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_chat_with_node);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_ALL);
        initView();
        initData();
    }

    private void initView() {
        chatMsgDao = new ChatMsgDao(this);
        chatMessages = new ArrayList<>();
        titlebarBackBtn = (LinearLayout) findViewById(R.id.titlebar_navigation);
        moreOption = (LinearLayout) findViewById(R.id.linear_menu_container);
        title = (TextView) findViewById(R.id.titlebar_navigation_title);
        subscribeNode = (TextView) findViewById(R.id.subscribe_node);
        readData = (TextView) findViewById(R.id.read_data);
        settingNode = (TextView) findViewById(R.id.setting_node);
        dropdownListView = (DropdownListView) findViewById(R.id.message_chat_listview);
        dropdownListView.setOnRefreshListenerHead(this);
    }

    private void initData() {
        groupName = getIntent().getStringExtra(ContactFragment.GROUP_NAME);
        childName = getIntent().getStringExtra(ContactFragment.CHILD_NAME);
        childJid = getIntent().getStringExtra(ContactFragment.CHILD_JID);
        title.setText(childName);

        titlebarBackBtn.setOnClickListener(this);
        moreOption.setOnClickListener(this);
        subscribeNode.setOnClickListener(this);
        readData.setOnClickListener(this);
        settingNode.setOnClickListener(this);

        offset = 0;
        chatMessages = chatMsgDao.queryMsg("temprature1@xmpp/temprature", "client@xmpp/Smack", offset);
        offset = chatMessages.size();
        adapter = new ChatWithNodeAdapter(this, chatMessages);
        dropdownListView.setAdapter(adapter);
        dropdownListView.setSelection(chatMessages.size());
    }

    @Override
    protected void onResume() {
        initBroadcastReceiver();
        super.onResume();
    }

    private void initBroadcastReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                ChatMessage chatMessage = chatMsgDao.queryTheLastMsg();
                chatMessages.add(chatMessages.size(), chatMessage);
                adapter.notifyDataSetChanged();
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(RECEIVED_MESSAGE);
        registerReceiver(receiver, filter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlebar_navigation:
                finish();
                break;
            case R.id.linear_menu_container:
                break;
            case R.id.subscribe_node:
                ArrayList<String> list = new ArrayList<>();
                list.add("周期订阅");
                list.add("上订阅");
                list.add("下线订阅");
                ChoiceListDialog dialog = new ChoiceListDialog(this, list);
                dialog.setOnChoiceListItemClickListener(new ChoiceListDialog.OnChoiceListItemClickListener() {
                    @Override
                    public void onListItemClick(int index) {
                        switch (index) {
                            case 0:
                                Log.e("tt", "index=" + index);
                                SubscribNode packet = new SubscribNode("temprature1@xmpp/temprature", "period");
                                packet.setType(IQ.Type.SET);
                                packet.setFrom("client@xmpp/Smack");
                                manager.getXmppConnection().sendPacket(packet);
                                break;
                            case 1:
                                Log.e("tt", "index=" + index);
                                break;
                            case 2:
                                Log.e("tt", "index=" + index);
                                break;
                        }
                    }
                });
                dialog.show();
                break;
            case R.id.read_data:
                GetNodeData packet = new GetNodeData();
                packet.setType(IQ.Type.GET);
                packet.setFrom("client@xmpp/Smack");
                packet.setTo("temprature1@xmpp/temprature");
                packet.setDataType("temprature");
                manager.getXmppConnection().sendPacket(packet);
                break;
            case R.id.setting_node:
                break;
        }
    }

    /**
     * 下拉加载更多
     */
    @Override
    public void onRefresh() {
        ArrayList<ChatMessage> list = chatMsgDao.queryMsg("temprature1@xmpp/temprature", "client@xmpp/Smack", offset);
        if (list.size() <= 0) {
            dropdownListView.setSelection(0);
            dropdownListView.onRefreshCompleteHeader();
            return;
        }
        chatMessages.addAll(0, list);
        offset = chatMessages.size();
        dropdownListView.onRefreshCompleteHeader();
        adapter.notifyDataSetChanged();
        dropdownListView.setSelection(list.size());
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

}
