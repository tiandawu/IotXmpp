package com.cqupt.xmpp.activity;

import android.app.Activity;
import android.os.Bundle;
<<<<<<< HEAD
=======
import android.text.TextUtils;
>>>>>>> origin/master
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
<<<<<<< HEAD
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cqupt.xmpp.R;
import com.cqupt.xmpp.fragment.ContactFragment;
import com.cqupt.xmpp.listener.MsgListener;
import com.cqupt.xmpp.manager.XmppConnectionManager;
import com.cqupt.xmpp.service.IotXmppService;
import com.cqupt.xmpp.utils.ConstUtil;
=======
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cqupt.xmpp.R;
import com.cqupt.xmpp.utils.ConstUtil;
import com.cqupt.xmpp.utils.PreferencesUtils;
>>>>>>> origin/master
import com.cqupt.xmpp.widght.DropdownListView;
import com.cqupt.xmpp.widght.SwipeBackActivity;
import com.cqupt.xmpp.widght.SwipeBackLayout;

<<<<<<< HEAD
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPException;

=======
>>>>>>> origin/master
/**
 * Created by tiandawu on 2016/4/6.
 */
public class ChatActivity extends SwipeBackActivity implements View.OnClickListener, DropdownListView.OnRefreshListenerHeader {
    private SwipeBackLayout mSwipeBackLayout;
    private DropdownListView dropDownListView;
<<<<<<< HEAD
    private String I, YOU, groupName, childName, childJid;//为了好区分，I就是自己，YOU就是对方
    private LinearLayout btnBack, btnMore;
    private TextView title, btnSendMsg;
    private EditText inputMsg;
    private XmppConnectionManager manager;
    private Chat chat;
=======
    private String I, YOU;//为了好区分，I就是自己，YOU就是对方
    private LinearLayout btnBack, btnMore;
    private TextView title, btnSendMsg;
    private ImageView imageAdd, imageEmoji;
    private EditText inputMsg;
>>>>>>> origin/master

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_chat);
        mSwipeBackLayout = getSwipeBackLayout();
        //设置滑动方向，可设置EDGE_LEFT, EDGE_RIGHT, EDGE_ALL, EDGE_BOTTOM
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_ALL);
        initView();
        initData();
    }

    private void initView() {
<<<<<<< HEAD
        manager = XmppConnectionManager.getXmppconnectionManager();
=======
>>>>>>> origin/master
        btnBack = (LinearLayout) findViewById(R.id.titlebar_navigation);
        btnMore = (LinearLayout) findViewById(R.id.linear_menu_container);
        btnSendMsg = (TextView) findViewById(R.id.send_sms);
        title = (TextView) findViewById(R.id.titlebar_navigation_title);
<<<<<<< HEAD
=======
        imageAdd = (ImageView) findViewById(R.id.image_add);
        imageEmoji = (ImageView) findViewById(R.id.image_face);
>>>>>>> origin/master
        inputMsg = (EditText) findViewById(R.id.input_sms);
        dropDownListView = (DropdownListView) findViewById(R.id.message_chat_listview);

        btnBack.setOnClickListener(this);
        btnMore.setOnClickListener(this);
        btnSendMsg.setOnClickListener(this);
<<<<<<< HEAD
    }

    private void initData() {
        groupName = getIntent().getStringExtra(ContactFragment.GROUP_NAME);
        childName = getIntent().getStringExtra(ContactFragment.CHILD_NAME);
        childJid = getIntent().getStringExtra(ContactFragment.CHILD_JID);
        I = ConstUtil.getOwnerJid(this);
        YOU = childJid + "/" + groupName;
        title.setText(YOU.substring(0, YOU.lastIndexOf("@")));

        chat = manager.getXmppConnection().getChatManager().createChat(YOU, new MsgListener(IotXmppService.getInstance(), null));
=======
        imageAdd.setOnClickListener(this);
        imageEmoji.setOnClickListener(this);
    }

    private void initData() {
        I = PreferencesUtils.getSharePreStr(this, ConstUtil.SP_KEY_NAME);
        YOU = getIntent().getStringExtra("from");
        if (TextUtils.isEmpty(YOU)) {
            Toast.makeText(this, "没有获取到聊天对象", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        title.setText(YOU.substring(0, YOU.lastIndexOf("@")));
>>>>>>> origin/master
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //标题栏返回按钮
            case R.id.titlebar_navigation:
                finish();
                break;
            //标题栏更多按钮
            case R.id.linear_menu_container:
                break;
            //发送消息按钮
            case R.id.send_sms:
<<<<<<< HEAD
                String text = inputMsg.getText().toString().trim();
                try {
                    chat.sendMessage(text);
                } catch (XMPPException e) {
                    e.printStackTrace();
                    Log.e("tt", "Eror");
                }
                inputMsg.setText("");
                break;
=======
                break;
            //更多功能按钮
            case R.id.image_add:
                break;
            //更多表情按钮
            case R.id.image_face:
                break;

>>>>>>> origin/master
        }

    }

    @Override
    public void onRefresh() {

    }


    /**
     * 隐藏软键盘
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
