package com.cqupt.xmpp.widght;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.cqupt.xmpp.R;
import com.cqupt.xmpp.utils.ConstUtil;
import com.cqupt.xmpp.utils.PreferencesUtils;

import static com.cqupt.xmpp.R.id.et_input_ip;

/**
 * Created by tiandawu on 2016/4/15.
 */
public class LoginSettingDialog extends Dialog implements View.OnClickListener {

    private EditText mInputIp, mInputPort;
    private LinearLayout mOkButton;
    private Context mContext;

    public LoginSettingDialog(Context context) {
        super(context, R.style.normal_dialog);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_login_setting);
        initView();
    }

    private void initView() {
        mInputIp = (EditText) findViewById(et_input_ip);
        mInputPort = (EditText) findViewById(R.id.et_input_port);
        mOkButton = (LinearLayout) findViewById(R.id.ll_ok_button);

        mOkButton.setOnClickListener(this);
    }

    @Override
    public void show() {
        super.show();
        mInputIp.setText(PreferencesUtils.getSharePreStr(mContext, ConstUtil.XMPP_IP));
        mInputPort.setText(PreferencesUtils.getSharePreInt(mContext, ConstUtil.XMPP_PORT) + "");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mOkButton.getId()) {
            String ip = mInputIp.getText().toString().trim();
            String port = mInputPort.getText().toString().trim();
            PreferencesUtils.putSharePre(mContext, ConstUtil.XMPP_IP, ip);
            PreferencesUtils.putSharePre(mContext, ConstUtil.XMPP_PORT, Integer.parseInt(port));
            this.dismiss();
        }
    }
}
