package com.cqupt.xmpp.widght;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import com.cqupt.xmpp.R;

/**
 * Created by tiandawu on 2016/4/2.
 */
public class LoadingProgressDialog extends ProgressDialog {

    private TextView tv_message;
    private Context mContext;
    private String contentString = "加载中...";

    public LoadingProgressDialog(Context context) {
        this(context, R.style.MyLoadingDialog);
    }

    public LoadingProgressDialog(Context context, int theme) {
        super(context, theme);
        this.getWindow().getAttributes().gravity = Gravity.CENTER;
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_progress_dialog);
        init();
    }

    private void init() {
        tv_message = (TextView) findViewById(R.id.id_tv_loadingmsg);
        setCancelable(false);
    }

    public void setContentString(String contentString) {
        this.contentString = contentString;
    }

    @Override
    public void show() {
        super.show();
        tv_message.setText(contentString);
    }
}
