package com.cqupt.xmpp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cqupt.xmpp.R;
import com.cqupt.xmpp.manager.XmppConnectionManager;
import com.cqupt.xmpp.packet.FanDCMessage;
import com.cqupt.xmpp.utils.ConstUtil;
import com.cqupt.xmpp.widght.SwipeBackActivity;
import com.cqupt.xmpp.widght.SwipeBackLayout;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

/**
 * Created by tiandawu on 2016/5/21.
 */
public class KGNodeActivity extends SwipeBackActivity implements View.OnClickListener {

    /**
     * 交互格式
     * C->N:
     * <message from='XX@xmpp/XX' to='A1@xmpp/A'>1/0</message>
     * <p/>
     * N->C:
     * <message from='A1@xmpp/A' to='XX@xmpp/XX'>1/0</message>
     */

    private String FROM = "";
    private final String TO_FAN = "A1@xmpp/A";
    private final String TO_DC = "A2@xmpp/A";

    public static final String CHILD_NAME = "childName";
    public static final String FROM_EXTRA = "fromExtra";
    public static final String ON = "on";
    public static final String OFF = "off";
    private String childName = "childName";
    private static boolean IS_OFF = true;
    private final int MSG = 1;
    private final int MSG_ERROR = 0;
    private final int FAN_IS_OPEND = 2;
    private final int DC_IS_OPEND = 3;
    private final int FAN_OPEN_FAILURE = 4;
    private final int DC_OPEN_FAILURE = 5;

    private static int count = 3;
    /**
     * 超时重发时间
     */
    private final int TIME_OUT = 2000;


    private TextView nodeName;
    private LinearLayout backBtn;
    private ImageView fan, zldj;//风扇,直流电机
    //    private SwitchButton mSwitchButton;
    private Button openBtn, closeBtn;
    //    private RotateAnimation mRotateAnimation;
    private XMPPConnection mXMPPConnection = XmppConnectionManager.getXmppconnectionManager().getXmppConnection();
    private ChatManager mChatManager;
    private Chat mChat;
    private FanDCMessage mFanDCMessage;
    private BroadcastReceiver mReceiver;
    private AnimationDrawable mAnimationDrawable;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MSG_ERROR:
//                    ToastUtils.showShortToastInCenter(KGNodeActivity.this, "打开失败");
                    break;
                case MSG:
                    if (!IS_OFF) {
                        Log.e("tt", "kai1");
//                        fan.startAnimation(mRotateAnimation);
                        mAnimationDrawable.start();
                        mHandler.sendEmptyMessageDelayed(MSG, 1000);
                    } else {
                        Log.e("tt", "guan1");
                    }
                    break;
                case FAN_IS_OPEND:
                    if (IS_OFF) {
                        IS_OFF = false;
                    }
                    mHandler.sendEmptyMessage(MSG);
                    break;
                case FAN_OPEN_FAILURE:
//                    ToastUtils.showShortToastInCenter(KGNodeActivity.this, "风扇打开失败");
                    break;
                case DC_IS_OPEND:
                    if (IS_OFF) {
                        IS_OFF = false;
                    }
                    mHandler.sendEmptyMessage(MSG);
                    break;
                case DC_OPEN_FAILURE:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_kg_node);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_ALL);

        initView();
        initData();
    }

    private void initView() {
        nodeName = (TextView) findViewById(R.id.titlebar_navigation_title);
        backBtn = (LinearLayout) findViewById(R.id.titlebar_navigation);
//        mRotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        mRotateAnimation.setDuration(1000);
//        mRotateAnimation.setFillAfter(true);
//        mRotateAnimation.setInterpolator(new LinearInterpolator());
//        mRotateAnimation.setRepeatCount(1);
        fan = (ImageView) findViewById(R.id.fan);
//        mSwitchButton = (SwitchButton) findViewById(R.id.switch_button);
        openBtn = (Button) findViewById(R.id.open_button);
        closeBtn = (Button) findViewById(R.id.close_button);
        zldj = (ImageView) findViewById(R.id.zldj);
    }

    private void initData() {
        FROM = ConstUtil.getOwnerJid(KGNodeActivity.this);

        childName = getIntent().getStringExtra(CHILD_NAME);

        if ("A1".equals(childName)) {
            nodeName.setText("风扇");
        } else if ("A2".equals(childName)) {
            nodeName.setText("直流电机");
        }

        backBtn.setOnClickListener(this);

//        mSwitchButton.setOnClickListener(this);
        openBtn.setOnClickListener(this);
        closeBtn.setOnClickListener(this);

        mChatManager = mXMPPConnection.getChatManager();
        if (childName.equals("A1")) {
            mChat = mChatManager.createChat(TO_FAN, null);
            mFanDCMessage = new FanDCMessage();
            mFanDCMessage.setFrom(FROM);
            mFanDCMessage.setTo(TO_FAN);
        } else if (childName.equals("A2")) {
            mChat = mChatManager.createChat(TO_DC, null);
            mFanDCMessage = new FanDCMessage();
            mFanDCMessage.setFrom(FROM);
            mFanDCMessage.setTo(TO_DC);
        }


        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                count = 0;
                String action = intent.getAction();
                String fromExtra = intent.getStringExtra(FROM_EXTRA);

//                Log.e("tt", "action = " + action);
//                Log.e("tt", "fromExtra = " + fromExtra);
                if (ON.equals(action)) {
                    if (TO_FAN.equals(fromExtra)) {
                        mHandler.sendEmptyMessage(FAN_IS_OPEND);
                    } else if (TO_DC.equals(fromExtra)) {
                        mHandler.sendEmptyMessage(DC_IS_OPEND);
                    }
                } else if (OFF.equals(action)) {
                    if (TO_FAN.equals(fromExtra)) {
                        mHandler.sendEmptyMessage(FAN_OPEN_FAILURE);
                    } else if (TO_DC.equals(fromExtra)) {
                        mHandler.sendEmptyMessage(DC_OPEN_FAILURE);
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(ON);
        filter.addAction(OFF);
        registerReceiver(mReceiver, filter);

        if ("A1".equals(childName)) {
            zldj.setVisibility(View.GONE);
            fan.setVisibility(View.VISIBLE);
            fan.setImageResource(R.drawable.fan_animation);
            mAnimationDrawable = (AnimationDrawable) fan.getDrawable();
        } else if ("A2".equals(childName)) {
            zldj.setVisibility(View.VISIBLE);
            fan.setVisibility(View.GONE);
            zldj.setImageResource(R.drawable.zldj_animation);
            mAnimationDrawable = (AnimationDrawable) zldj.getDrawable();
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlebar_navigation:
                finish();
                break;
            case R.id.open_button:
                mFanDCMessage.setFlag("1");
//                mAnimationDrawable.start();
                resendMessage(mFanDCMessage);
                break;
            case R.id.close_button:
                mFanDCMessage.setFlag("0");
                resendMessage(mFanDCMessage);
                mHandler.removeCallbacksAndMessages(null);
                mAnimationDrawable.stop();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        mReceiver = null;
        mXMPPConnection = null;
        mChatManager = null;
        mChat = null;
        super.onDestroy();
    }


    private void resendMessage(final org.jivesoftware.smack.packet.Message message) {
        count = 3;
        new Thread() {
            @Override
            public void run() {
                while (count > 0) {
                    count--;
                    Log.e("tt", "发送时间： " + System.currentTimeMillis() + "");
                    try {
                        if (mChat != null) {
                            mChat.sendMessage(message);
                        }
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(TIME_OUT);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
