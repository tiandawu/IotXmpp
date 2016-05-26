package com.cqupt.xmpp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cqupt.xmpp.R;
import com.cqupt.xmpp.manager.XmppConnectionManager;
import com.cqupt.xmpp.packet.LedMessage;
import com.cqupt.xmpp.utils.ConstUtil;
import com.cqupt.xmpp.widght.SwipeBackActivity;
import com.cqupt.xmpp.widght.SwipeBackLayout;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

/**
 * Created by tiandawu on 2016/5/22.
 */
public class LEDActivity extends SwipeBackActivity implements View.OnClickListener {


    private TextView openRedLed, openYellowLed, openGreenLed, openBlueLed,
            closeRedLed, closeYellowLed, closeGreenLed, closeBlueLed, title;
    private ImageView redLed, yellowLed, greenLed, blueLed;
    private LinearLayout backBtn;
    private String childName;
    private XMPPConnection connection = XmppConnectionManager.getXmppconnectionManager().getXmppConnection();
    private ChatManager chatManager;
    private Chat mChat;
    private final int TIME_OUT = 1200;

    public static final String CHILD_NAME = "childName";
    public static final String LED_STATE = "ledState";
    private static int count = 3;
    private final String TO = "A3@xmpp/A";
    private String FROM;
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_led);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_ALL);
        initView();
        initData();
    }

    private void initView() {
        backBtn = (LinearLayout) findViewById(R.id.titlebar_navigation);
        title = (TextView) findViewById(R.id.titlebar_navigation_title);

        redLed = (ImageView) findViewById(R.id.red_led);
        yellowLed = (ImageView) findViewById(R.id.yellow_led);
        greenLed = (ImageView) findViewById(R.id.green_led);
        blueLed = (ImageView) findViewById(R.id.blue_led);

        openRedLed = (TextView) findViewById(R.id.open_red_led);
        openYellowLed = (TextView) findViewById(R.id.open_yellow_led);
        openGreenLed = (TextView) findViewById(R.id.open_green_led);
        openBlueLed = (TextView) findViewById(R.id.open_blue_led);

        closeRedLed = (TextView) findViewById(R.id.close_red_led);
        closeYellowLed = (TextView) findViewById(R.id.close_yellow_led);
        closeGreenLed = (TextView) findViewById(R.id.close_green_led);
        closeBlueLed = (TextView) findViewById(R.id.close_blue_led);

        FROM = ConstUtil.getOwnerJid(this);
        chatManager = connection.getChatManager();
        mChat = chatManager.createChat(TO, null);

    }

    private void initData() {

        childName = getIntent().getStringExtra(CHILD_NAME);
        title.setText("LED灯");
        backBtn.setOnClickListener(this);

        openRedLed.setOnClickListener(this);
        openYellowLed.setOnClickListener(this);
        openGreenLed.setOnClickListener(this);
        openBlueLed.setOnClickListener(this);

        closeRedLed.setOnClickListener(this);
        closeYellowLed.setOnClickListener(this);
        closeGreenLed.setOnClickListener(this);
        closeBlueLed.setOnClickListener(this);


        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String ledState = intent.getStringExtra(LED_STATE);
                if ("10".equals(ledState)) {
                    redLed.setSelected(false);
                    count = 0;
                } else if ("20".equals(ledState)) {
                    yellowLed.setSelected(false);
                } else if ("30".equals(ledState)) {
                    greenLed.setSelected(false);
                    count = 0;
                } else if ("40".equals(ledState)) {
                    blueLed.setSelected(false);
                    count = 0;
                } else if ("11".equals(ledState)) {
                    redLed.setSelected(true);
                    count = 0;
                } else if ("21".equals(ledState)) {
                    yellowLed.setSelected(true);
                    count = 0;
                } else if ("31".equals(ledState)) {
                    greenLed.setSelected(true);
                    count = 0;
                } else if ("41".equals(ledState)) {
                    blueLed.setSelected(true);
                    count = 0;
                }

            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(LED_STATE);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.titlebar_navigation:
                finish();
                break;
            case R.id.close_red_led:
                LedMessage closeRedMessage = new LedMessage();
                closeRedMessage.setFrom(FROM);
                closeRedMessage.setTo(TO);
                closeRedMessage.setLedState("10");

                sendMessage(closeRedMessage);

                break;
            case R.id.close_yellow_led:
                LedMessage closeYelMessage = new LedMessage();
                closeYelMessage.setFrom(FROM);
                closeYelMessage.setTo(TO);
                closeYelMessage.setLedState("20");

                sendMessage(closeYelMessage);

                break;
            case R.id.close_green_led:
                LedMessage closeGreenMessage = new LedMessage();
                closeGreenMessage.setFrom(FROM);
                closeGreenMessage.setTo(TO);
                closeGreenMessage.setLedState("30");
                sendMessage(closeGreenMessage);

                break;
            case R.id.close_blue_led:
                LedMessage closeBlueMessage = new LedMessage();
                closeBlueMessage.setFrom(FROM);
                closeBlueMessage.setTo(TO);
                closeBlueMessage.setLedState("40");
                sendMessage(closeBlueMessage);
                break;
            case R.id.open_red_led:
                LedMessage openRedMessage = new LedMessage();
                openRedMessage.setFrom(FROM);
                openRedMessage.setTo(TO);
                openRedMessage.setLedState("11");
                sendMessage(openRedMessage);
                break;
            case R.id.open_yellow_led:
                LedMessage openYelMessage = new LedMessage();
                openYelMessage.setFrom(FROM);
                openYelMessage.setTo(TO);
                openYelMessage.setLedState("21");
                sendMessage(openYelMessage);
                break;
            case R.id.open_green_led:
                LedMessage openGreenMessage = new LedMessage();
                openGreenMessage.setFrom(FROM);
                openGreenMessage.setTo(TO);
                openGreenMessage.setLedState("31");
                sendMessage(openGreenMessage);
                break;
            case R.id.open_blue_led:
                LedMessage openBlueMessage = new LedMessage();
                openBlueMessage.setFrom(FROM);
                openBlueMessage.setTo(TO);
                openBlueMessage.setLedState("41");
                sendMessage(openBlueMessage);
                break;
        }
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        mReceiver = null;
        chatManager = null;
        mChat = null;
        connection = null;
        super.onDestroy();
    }


    private void sendMessage(final org.jivesoftware.smack.packet.Message message) {
        count = 3;
        new Thread() {
            @Override
            public void run() {
                while (count > 0) {
                    count--;
                    Log.e("tt", "发送时间： "+System.currentTimeMillis()+"");
                    try {
                        mChat.sendMessage(message);
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
