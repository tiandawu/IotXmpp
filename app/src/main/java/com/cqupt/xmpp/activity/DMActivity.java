package com.cqupt.xmpp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cqupt.xmpp.R;
import com.cqupt.xmpp.manager.XmppConnectionManager;
import com.cqupt.xmpp.packet.DmMessage;
import com.cqupt.xmpp.utils.ConstUtil;
import com.cqupt.xmpp.widght.SwipeBackActivity;
import com.cqupt.xmpp.widght.SwipeBackLayout;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

/**
 * Created by tiandawu on 2016/5/22.
 */


/**
 * 门磁页面
 */
public class DMActivity extends SwipeBackActivity implements View.OnClickListener {


    private LinearLayout backBtn;
    private TextView nodeName, mDoorState;
    private ImageView doorImg;
    public static final String CHILD_NAME = "childName";
    public static final String DOOR_STATE = "doorState";
    private BroadcastReceiver mReceiver;
    private XMPPConnection connection = XmppConnectionManager.getXmppconnectionManager().getXmppConnection();
    private ChatManager chatManager;
    private Chat mChat;
    private String FROM = "";
    private String TO = "B1@xmpp/B";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_dm);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_ALL);
        initView();
        initData();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void initView() {
        backBtn = (LinearLayout) findViewById(R.id.titlebar_navigation);
        nodeName = (TextView) findViewById(R.id.titlebar_navigation_title);
//        doorImg = (ImageView) findViewById(R.id.door_img);
        doorImg = (ImageView) findViewById(R.id.door_img);
        mDoorState = (TextView) findViewById(R.id.door_state);
        chatManager = connection.getChatManager();
        mChat = chatManager.createChat(TO, null);
        mDoorState.setText("门已开");

    }

    private void initData() {

        FROM = ConstUtil.getOwnerJid(this);
        nodeName.setText("门磁");
        backBtn.setOnClickListener(this);

//
//        doorImg.setImageResource(R.drawable.door_close_animation);
//        mAnimationDrawable = (AnimationDrawable) doorImg.getDrawable();


        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String doorState = intent.getStringExtra(DOOR_STATE);
                if ("1".equals(doorState)) {
                    doorImg.setSelected(true);
                    mDoorState.setText("门已关");
                    DmMessage doorOpendMessage = new DmMessage();
                    doorOpendMessage.setTo(TO);
                    doorOpendMessage.setFrom(FROM);
                    doorOpendMessage.setDoorState("1");

                    try {
                        mChat.sendMessage(doorOpendMessage);
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
                } else if ("0".equals(doorState)) {
                    doorImg.setSelected(false);
                    mDoorState.setText("门已开");
                    DmMessage doorClosedMessage = new DmMessage();
                    doorClosedMessage.setTo(TO);
                    doorClosedMessage.setFrom(FROM);
                    doorClosedMessage.setDoorState("0");
                    try {
                        mChat.sendMessage(doorClosedMessage);
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
                }

            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(DOOR_STATE);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlebar_navigation:
                finish();
                break;
        }
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        mReceiver = null;
        connection = null;
        chatManager = null;
        mChat = null;
        super.onDestroy();
    }

}
