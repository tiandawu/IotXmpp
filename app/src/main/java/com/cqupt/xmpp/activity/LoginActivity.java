package com.cqupt.xmpp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cqupt.xmpp.R;
import com.cqupt.xmpp.base.BaseActivity;
import com.cqupt.xmpp.service.IotXmppService;
import com.cqupt.xmpp.utils.ActivityUtils;
import com.cqupt.xmpp.utils.ConstUtil;
import com.cqupt.xmpp.utils.PreferencesUtils;
import com.cqupt.xmpp.utils.ToastUtils;
import com.cqupt.xmpp.widght.CatLoadingView;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by tiandawu on 2016/3/31.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout loginButton;
    private TextView loginSettings;
    private MaterialEditText inputUsername, inputPassword;
    private ImageView showPasswordImg;
    private CatLoadingView catLoadingView;
    private BroadcastReceiver receiver;


    @Override
    protected void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.act_login);

        inputUsername = (MaterialEditText) findViewById(R.id.login_username);
        inputPassword = (MaterialEditText) findViewById(R.id.login_password);
        loginButton = (LinearLayout) findViewById(R.id.login_button);
        loginSettings = (TextView) findViewById(R.id.login_setting);
        showPasswordImg = (ImageView) findViewById(R.id.show_password_img);
//        showPasswordImg.setSelected(false);

        catLoadingView = new CatLoadingView();
        catLoadingView.setCancelable(false);

        loginButton.setOnClickListener(this);
        loginSettings.setOnClickListener(this);
        showPasswordImg.setOnClickListener(this);
        initLonginReceiver();
    }

    @Override
    protected void initData() {
        super.initData();
        String name = PreferencesUtils.getSharePreStr(this, ConstUtil.SP_KEY_NAME);
        String pwd = PreferencesUtils.getSharePreStr(this, ConstUtil.SP_KEY_PWD);
        inputUsername.setText(TextUtils.isEmpty(name) ? "" : name);
        inputPassword.setText(TextUtils.isEmpty(pwd) ? "" : pwd);

    }

    private boolean submitForm() {
        if (!validateName()) {
            return false;
        }
        if (!validatePassword()) {
            return false;
        }

        return true;
    }

    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            requestFocus(inputPassword);
            return false;
        }
        return true;
    }

    private boolean validateName() {
        if (inputUsername.getText().toString().trim().isEmpty()) {
            requestFocus(inputUsername);
            return false;
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.login_button:
                if (!submitForm()) {
                    return;
                }
//                final LoadingProgressDialog dialog = new LoadingProgressDialog(LoginActivity.this);
//                dialog.setContentString(getResources().getString(R.string.loging));
//                dialog.show();
                final String username = inputUsername.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();
                PreferencesUtils.putSharePre(this, ConstUtil.SP_KEY_NAME, username);
                PreferencesUtils.putSharePre(this, ConstUtil.SP_KEY_PWD, password);
                catLoadingView.show(getSupportFragmentManager(), "登录中...");
                Intent intent = new Intent(this, IotXmppService.class);
                startService(intent);
                break;
            case R.id.login_setting:
                showLoginSettingPop();
                break;
            case R.id.show_password_img:
                showPasswordImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (showPasswordImg.isSelected()) {
                            showPasswordImg.setSelected(false);
                        } else {
                            showPasswordImg.setSelected(true);
                        }

                        if (showPasswordImg.isSelected()) {
                            //设置EditText文本为可见的
                            inputPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        } else {
                            //设置EditText文本为隐藏的
                            inputPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        }
                        inputPassword.postInvalidate();

                        //切换后将EditText光标置于末尾
                        CharSequence charSequence = inputPassword.getText();
                        if (charSequence instanceof Spannable) {
                            Spannable spanText = (Spannable) charSequence;
                            Selection.setSelection(spanText, charSequence.length());
                        }
                    }
                });
                break;
        }
    }

    /**
     * 登陆设置popWindow
     */
    private void showLoginSettingPop() {
        final PopupWindow pop = new PopupWindow(LoginActivity.this);
        View view = View.inflate(LoginActivity.this, R.layout.pop_login_settings, null);
        pop.setContentView(view);
        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new ColorDrawable());
        pop.setFocusable(true);
        final EditText inputIp = (EditText) view.findViewById(R.id.et_input_ip);
        final EditText inputPort = (EditText) view.findViewById(R.id.et_inpu_port);

        inputIp.setText(PreferencesUtils.getSharePreStr(LoginActivity.this, ConstUtil.XMPP_IP));
        inputPort.setText(PreferencesUtils.getSharePreStr(LoginActivity.this, ConstUtil.XMPP_PORT));

        TextView cancle = (TextView) view.findViewById(R.id.tv_cancle);
        TextView okBtn = (TextView) view.findViewById(R.id.tv_ok);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = inputIp.getText().toString().trim();
                String port = inputPort.getText().toString().trim();
                PreferencesUtils.putSharePre(LoginActivity.this, ConstUtil.XMPP_IP, ip);
                PreferencesUtils.putSharePre(LoginActivity.this, ConstUtil.XMPP_PORT, port);
                pop.dismiss();
            }
        });

        pop.showAtLocation(view, Gravity.CENTER, 0, 0);

    }


    /**
     * 初始化登录的广播
     */
    private void initLonginReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(ConstUtil.LOGIN_STATUS)) {
                    if (catLoadingView.isResumed()) {
                        catLoadingView.dismiss();
                    }

                    boolean isLoginSuccess = intent.getBooleanExtra("isLoginSuccess", false);
                    if (isLoginSuccess) {//登录成功
                        ActivityUtils.startActivity(LoginActivity.this, MainActivity.class, true);
                    } else {
                        ToastUtils.showShortToast(context, "登录失败，请检您的网络是否正常以及用户名和密码是否正确");
                    }
                }
            }
        };

        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConstUtil.LOGIN_STATUS);
        registerReceiver(receiver, mFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
