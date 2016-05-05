package com.cqupt.xmpp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cqupt.xmpp.R;
import com.cqupt.xmpp.fragment.ContactFragment;
import com.cqupt.xmpp.listener.MsgListener;
import com.cqupt.xmpp.manager.XmppConnectionManager;
import com.cqupt.xmpp.service.IotXmppService;
import com.cqupt.xmpp.utils.ConstUtil;
import com.cqupt.xmpp.widght.DropdownListView;
import com.cqupt.xmpp.widght.SwipeBackActivity;
import com.cqupt.xmpp.widght.SwipeBackLayout;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPException;

/**
 * Created by tiandawu on 2016/4/6.
 */
public class ChatActivity extends SwipeBackActivity implements View.OnClickListener, DropdownListView.OnRefreshListenerHeader {
    private SwipeBackLayout mSwipeBackLayout;
    private DropdownListView dropDownListView;
    private String I, YOU, groupName, childName, childJid;//为了好区分，I就是自己，YOU就是对方
    private LinearLayout btnBack, btnMore;
    private TextView title, btnSendMsg;
    private EditText inputMsg;
    private XmppConnectionManager manager;
    private Chat chat;

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
        manager = XmppConnectionManager.getXmppconnectionManager();
        btnBack = (LinearLayout) findViewById(R.id.titlebar_navigation);
        btnMore = (LinearLayout) findViewById(R.id.linear_menu_container);
        btnSendMsg = (TextView) findViewById(R.id.send_sms);
        title = (TextView) findViewById(R.id.titlebar_navigation_title);
        inputMsg = (EditText) findViewById(R.id.input_sms);
        dropDownListView = (DropdownListView) findViewById(R.id.message_chat_listview);

        btnBack.setOnClickListener(this);
        btnMore.setOnClickListener(this);
        btnSendMsg.setOnClickListener(this);
    }

    private void initData() {
        groupName = getIntent().getStringExtra(ContactFragment.GROUP_NAME);
        childName = getIntent().getStringExtra(ContactFragment.CHILD_NAME);
        childJid = getIntent().getStringExtra(ContactFragment.CHILD_JID);
        I = ConstUtil.getOwnerJid(this);
        YOU = childJid + "/" + groupName;
        title.setText(YOU.substring(0, YOU.lastIndexOf("@")));

        chat = manager.getXmppConnection().getChatManager().createChat(YOU, new MsgListener(IotXmppService.getInstance(), null));
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
                String text = inputMsg.getText().toString().trim();
                try {
                    chat.sendMessage(text);
                } catch (XMPPException e) {
                    e.printStackTrace();
                    Log.e("tt", "Eror");
                }
                inputMsg.setText("");
                break;
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
