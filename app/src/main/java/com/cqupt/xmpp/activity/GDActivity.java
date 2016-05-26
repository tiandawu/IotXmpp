package com.cqupt.xmpp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cqupt.xmpp.R;
import com.cqupt.xmpp.fragment.ContactFragment;
import com.cqupt.xmpp.manager.XmppConnectionManager;
import com.cqupt.xmpp.packet.GdMessage;
import com.cqupt.xmpp.service.PlayAlarmSoundService;
import com.cqupt.xmpp.utils.ConstUtil;
import com.cqupt.xmpp.utils.PreferencesUtils;
import com.cqupt.xmpp.widght.SwipeBackActivity;
import com.cqupt.xmpp.widght.SwipeBackLayout;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

/**
 * Created by tiandawu on 2016/5/22.
 */
public class GDActivity extends SwipeBackActivity implements View.OnClickListener {


    private LinearLayout backBtn;
    private TextView nodeName, bjState;
    private ImageView bjImg;
    public static final String CHILD_NAME = "childName";
    public static final String GD_STATE = "gdState";
    private BroadcastReceiver mReceiver;
    private XMPPConnection connection = XmppConnectionManager.getXmppconnectionManager().getXmppConnection();
    private ChatManager chatManager;
    private Chat mChat;
    private String FROM = "";
    private String TO = "B2@xmpp/B";
    private AnimationDrawable mAnimationDrawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_gd);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_ALL);
        initView();
        initData();
    }

    private void initView() {
        backBtn = (LinearLayout) findViewById(R.id.titlebar_navigation);
        nodeName = (TextView) findViewById(R.id.titlebar_navigation_title);
        bjImg = (ImageView) findViewById(R.id.bj_img);
        bjState = (TextView) findViewById(R.id.bj_state);
        chatManager = connection.getChatManager();
        mChat = chatManager.createChat(TO, null);

    }

    private void initData() {

        FROM = ConstUtil.getOwnerJid(this);
        nodeName.setText("光电接近传感器");
        backBtn.setOnClickListener(this);

        /**
         * 保存被点击的是谁，便于清楚报警标记
         */
        PreferencesUtils.putSharePre(this, "clickedItemName", getIntent().getStringExtra(CHILD_NAME));

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String doorState = intent.getStringExtra(GD_STATE);
                if ("1".equals(doorState)) {
                    bjImg.setImageResource(R.drawable.bj_animation);
                    mAnimationDrawable = (AnimationDrawable) bjImg.getDrawable();
                    Intent gdAlarmIntent = new Intent(context, PlayAlarmSoundService.class);
                    PlayAlarmSoundService.isLoop = true;
                    context.startService(gdAlarmIntent);
                    mAnimationDrawable.start();
                    bjState.setText("有警报");
                    GdMessage message = new GdMessage();
                    message.setTo(TO);
                    message.setFrom(FROM);
                    message.setGdState("1");

                    try {
                        mChat.sendMessage(message);
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
                } else if ("0".equals(doorState)) {
                    PlayAlarmSoundService.getInstance().stopSelf();
                    if (mAnimationDrawable != null) {
                        mAnimationDrawable.stop();
                        PlayAlarmSoundService.getInstance().stopSelf();
                    } else {
                        mAnimationDrawable = (AnimationDrawable) bjImg.getDrawable();
                        mAnimationDrawable.stop();
                        PlayAlarmSoundService.getInstance().stopSelf();
                    }
                    bjState.setText("无警报");
                    bjImg.setImageResource(R.mipmap.bj_normal);
                    GdMessage message = new GdMessage();
                    message.setTo(TO);
                    message.setFrom(FROM);
                    message.setGdState("0");
                    try {
                        mChat.sendMessage(message);
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
                }

            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(GD_STATE);
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

        Intent clearAlarmIntent = new Intent();
        clearAlarmIntent.setAction(ContactFragment.FRIENDS_STATUS_CHANGED);
        sendBroadcast(clearAlarmIntent);

        unregisterReceiver(mReceiver);
        mReceiver = null;
        connection = null;
        chatManager = null;
        mChat = null;
        PlayAlarmSoundService.isLoop = false;
        super.onDestroy();
    }
}
