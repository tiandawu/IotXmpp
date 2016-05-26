package com.cqupt.xmpp.activity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cqupt.xmpp.R;
import com.cqupt.xmpp.manager.XmppConnectionManager;
import com.cqupt.xmpp.packet.BjMessage;
import com.cqupt.xmpp.utils.ConstUtil;
import com.cqupt.xmpp.utils.ToastUtils;
import com.cqupt.xmpp.widght.SwipeBackActivity;
import com.cqupt.xmpp.widght.SwipeBackLayout;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

/**
 * Created by tiandawu on 2016/5/22.
 */

/**
 * 步进电机页面
 */
public class BJActivity extends SwipeBackActivity implements View.OnClickListener {

    public static final String CHILD_NAME = "childName";
    public static final String CHILD_STATUS = "childStatus";
    /**
     * 转速，方向，圈数
     */
    private Spinner mSpeed, mDriction, mNumber;
    private LinearLayout backBtn;
    private TextView title;
    private final String TO = "A4@xmpp/A";
    private String FROM = "";
    private Button mButton;
    private String dirction, speed, number;
    private XMPPConnection mConnection = XmppConnectionManager.getXmppconnectionManager().getXmppConnection();
    private ChatManager mChatManager;
    private Chat mChat;
    private ImageView bjdjImg;
    private AnimationDrawable mAnimationDrawable;
    private final int STOP_ANIMATION = 1;
    private String[] numberData = new String[]{
            "20", "40", "60", "80", "100"
    };

    private String[] dirictionData = new String[]{
            "正向", "反向"
    };

    private String[] speedData = new String[]{
            "一档", "二档", "三档", "四档"
    };

    private String[] getSpeedData = new String[]{
            "50", "40", "30", "20"
    };


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case STOP_ANIMATION:
                    mAnimationDrawable.stop();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_bj);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_ALL);

        initView();
        initData();
    }

    private void initView() {
        FROM = ConstUtil.getOwnerJid(this);
        mSpeed = (Spinner) findViewById(R.id.sp_speed);
        mDriction = (Spinner) findViewById(R.id.sp_diriction);
        mNumber = (Spinner) findViewById(R.id.sp_number);
        mButton = (Button) findViewById(R.id.btn_start);
        mChatManager = mConnection.getChatManager();
        mChat = mChatManager.createChat(TO, null);
        backBtn = (LinearLayout) findViewById(R.id.titlebar_navigation);
        title = (TextView) findViewById(R.id.titlebar_navigation_title);
        bjdjImg = (ImageView) findViewById(R.id.bjdj_img);
    }

    private void initData() {
        ArrayAdapter<String> speedAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, speedData);
        ArrayAdapter<String> dircAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dirictionData);
        ArrayAdapter<String> numAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, numberData);
        mSpeed.setAdapter(speedAdapter);
        mDriction.setAdapter(dircAdapter);
        mNumber.setAdapter(numAdapter);

        mButton.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        title.setText("步进电机");

        bjdjImg.setImageResource(R.drawable.bjdj_animation);
        mAnimationDrawable = (AnimationDrawable) bjdjImg.getDrawable();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:

                String staus = getIntent().getStringExtra(CHILD_STATUS);
                Log.e("tt", "status = " + staus);
                if (!staus.equals("available (online)")) {
                    ToastUtils.showShortToastInCenter(BJActivity.this, "节点不在线");
                    return;
                }

                speed = getSpeedData[(int) mSpeed.getSelectedItemId()];

                if (dirictionData[0].equals(mDriction.getSelectedItem().toString())) {
                    dirction = "1";
                } else if (dirictionData[1].equals(mDriction.getSelectedItem().toString())) {
                    dirction = "0";
                }

                number = mNumber.getSelectedItem().toString();


                BjMessage bjMessage = new BjMessage();
                bjMessage.setFrom(FROM);
                bjMessage.setTo(TO);
                bjMessage.setDirction(dirction);
                bjMessage.setNumber(number);
                bjMessage.setSpeed(speed);


                try {
                    mChat.sendMessage(bjMessage);

                } catch (XMPPException e) {
                    e.printStackTrace();
                }

                int num = Integer.parseInt(number);//全数
                int spe = Integer.parseInt(speed);//速度
                int bl = num %spe;//时间比例

                Log.e("tt", "bl = " + bl);

                mAnimationDrawable.start();

                new Thread() {
                    @Override
                    public void run() {

                        try {
                            Thread.sleep(1000);
                            mHandler.sendEmptyMessage(STOP_ANIMATION);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                break;

            case R.id.titlebar_navigation:
                finish();
                break;
        }
    }
}
