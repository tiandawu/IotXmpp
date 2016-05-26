package com.cqupt.xmpp.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cqupt.xmpp.R;
import com.cqupt.xmpp.adapter.MyFragmentPagerAdapter;
import com.cqupt.xmpp.base.BaseActivity;
import com.cqupt.xmpp.fragment.ContactFragment;
import com.cqupt.xmpp.fragment.DiscoverFragment;
import com.cqupt.xmpp.fragment.MessageFragment;
import com.cqupt.xmpp.service.IotXmppService;
import com.cqupt.xmpp.utils.ActivityUtils;
import com.cqupt.xmpp.utils.ToastUtils;
import com.cqupt.xmpp.widght.CircleImageView;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements View.OnClickListener {


    private ImageView toolbarUserImg, tabMessageImg, tabContactImg, tabDiscoverImg;
    private LinearLayout tabMessage, tabContact, tabDiscover, drawerSub, drawerCollect, drawerMsg, drawerSetting, drawerLogOut;
    private TextView toolbarTitle, tabMessageText, tabContactText, tabDiscoverText;
    private CircleImageView drawerUserImg;

    private Fragment messageFragment, contactFragment, discoverFragment;
    private DrawerLayout drawerLayout;
    private ViewPager mViewPager;
    private ArrayList<Fragment> fragments;

    private static final int CONTACT_FRAG_ID = 0;
    private static final int MESSAGE_FRAG_ID = 1;
    private static final int DISCOVER_FRAG_ID = 2;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_main);
        toolbarUserImg = (ImageView) findViewById(R.id.toobar_user_img);
//        toolbarAddImg = (ImageView) findViewById(R.id.toolbar_add_btn);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);

        tabMessageText = (TextView) findViewById(R.id.tab_message_text);
        tabContactText = (TextView) findViewById(R.id.tab_contact_text);
        tabDiscoverText = (TextView) findViewById(R.id.tab_discover_text);
        tabMessageImg = (ImageView) findViewById(R.id.tab_message_icon);
        tabContactImg = (ImageView) findViewById(R.id.tab_contact_icon);
        tabDiscoverImg = (ImageView) findViewById(R.id.tab_discover_icon);
        tabMessage = (LinearLayout) findViewById(R.id.tab_message);
        tabContact = (LinearLayout) findViewById(R.id.tab_contact);
        tabDiscover = (LinearLayout) findViewById(R.id.tab_discover);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mViewPager = (ViewPager) findViewById(R.id.mainViewPager);
        drawerUserImg = (CircleImageView) findViewById(R.id.drawer_user_img);
        drawerSub = (LinearLayout) findViewById(R.id.drawer_item_subscribe);
        drawerCollect = (LinearLayout) findViewById(R.id.drawer_item_collect);
        drawerMsg = (LinearLayout) findViewById(R.id.drawer_item_msg);
        drawerSetting = (LinearLayout) findViewById(R.id.drawer_item_settings);
        drawerLogOut = (LinearLayout) findViewById(R.id.drawer_item_logOut);
    }

    @Override
    protected void initData() {
        super.initData();
        fragments = new ArrayList<>();
        messageFragment = new MessageFragment();
        contactFragment = new ContactFragment();
        discoverFragment = new DiscoverFragment();
        fragments.add(contactFragment);
        fragments.add(messageFragment);
        fragments.add(discoverFragment);

        mViewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments));
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == MESSAGE_FRAG_ID) {
                    setTabImagAndTitle(MESSAGE_FRAG_ID);
                } else if (position == CONTACT_FRAG_ID) {
                    setTabImagAndTitle(CONTACT_FRAG_ID);
                } else if (position == DISCOVER_FRAG_ID) {
                    setTabImagAndTitle(DISCOVER_FRAG_ID);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewPager.setCurrentItem(0);
        toolbarTitle.setText(R.string.tab_friend_str);
        tabContactImg.setSelected(true);
        tabContactText.setTextColor(getResources().getColor(R.color.text_color));
        tabContact.setOnClickListener(this);
        tabMessage.setOnClickListener(this);
        tabDiscover.setOnClickListener(this);
        toolbarUserImg.setOnClickListener(this);
//        toolbarAddImg.setOnClickListener(this);

        drawerUserImg.setOnClickListener(this);
        drawerLogOut.setOnClickListener(this);
        drawerSub.setOnClickListener(this);
        drawerCollect.setOnClickListener(this);
        drawerMsg.setOnClickListener(this);
        drawerSetting.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.tab_message:
                setTabImagAndTitle(MESSAGE_FRAG_ID);
                break;
            case R.id.tab_contact:
                setTabImagAndTitle(CONTACT_FRAG_ID);
                break;
            case R.id.tab_discover:
                setTabImagAndTitle(DISCOVER_FRAG_ID);
                break;

            case R.id.toobar_user_img:
                if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
                break;
//            case R.id.toolbar_add_btn:
//                drawerLayout.closeDrawer(Gravity.LEFT);
//                ToastUtils.showShortToastInCenter(this, "目前不支持该功能");
//                break;
            case R.id.drawer_item_subscribe:
                ActivityUtils.startActivity(this, SubscribedNodeActivity.class);
                break;
            case R.id.drawer_item_collect:
                drawerLayout.closeDrawer(Gravity.LEFT);
                ToastUtils.showShortToastInCenter(this, "目前不支持该功能");
                break;
            case R.id.drawer_item_msg:
                drawerLayout.closeDrawer(Gravity.LEFT);
                ToastUtils.showShortToastInCenter(this, "目前不支持该功能");
                break;
            case R.id.drawer_item_settings:
                ToastUtils.showShortToastInCenter(this, "目前不支持该功能");
                break;
            case R.id.drawer_item_logOut:
                drawerLayout.closeDrawer(Gravity.LEFT);
                IotXmppService.getInstance().stopSelf();
                ActivityUtils.startActivity(MainActivity.this, LoginActivity.class, true);
                break;
            case R.id.drawer_user_img:
                drawerLayout.closeDrawer(Gravity.LEFT);
                ToastUtils.showShortToastInCenter(this, "目前不支持该功能");
                break;
        }
    }

    private void resetTabImge() {
        tabMessageImg.setSelected(false);
        tabContactImg.setSelected(false);
        tabDiscoverImg.setSelected(false);
    }

    private void resetTabText() {
        tabMessageText.setTextColor(getResources().getColor(R.color.gray_600));
        tabContactText.setTextColor(getResources().getColor(R.color.gray_600));
        tabDiscoverText.setTextColor(getResources().getColor(R.color.gray_600));
    }

    private void setTabImagAndTitle(int id) {
        switch (id) {
            case CONTACT_FRAG_ID:
                resetTabImge();
                resetTabText();
                tabContactImg.setSelected(true);
                tabContactText.setTextColor(getResources().getColor(R.color.text_color));
                toolbarTitle.setText(getResources().getText(R.string.tab_friend_str));
                mViewPager.setCurrentItem(0);
                break;
            case MESSAGE_FRAG_ID:
                resetTabImge();
                resetTabText();
                tabMessageImg.setSelected(true);
                tabMessageText.setTextColor(getResources().getColor(R.color.text_color));
                toolbarTitle.setText(getResources().getText(R.string.tab_msg_str));
                mViewPager.setCurrentItem(1);
                break;
            case DISCOVER_FRAG_ID:
                resetTabImge();
                resetTabText();
                tabDiscoverImg.setSelected(true);
                tabDiscoverText.setTextColor(getResources().getColor(R.color.text_color));
                toolbarTitle.setText(getResources().getText(R.string.tab_discover_str));
                mViewPager.setCurrentItem(2);
                break;
        }
    }

}
