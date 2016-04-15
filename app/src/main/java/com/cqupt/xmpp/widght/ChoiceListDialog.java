package com.cqupt.xmpp.widght;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cqupt.xmpp.R;
import com.cqupt.xmpp.utils.DensityUtil;
import com.cqupt.xmpp.utils.ScreenUtils;
import com.cqupt.xmpp.widght.RevealView.RelativeRevealView;

import java.util.List;

/**
 * Created by tiandawu on 2015/10/9.
 */
public class ChoiceListDialog extends Dialog implements View.OnClickListener {

    private TextView cancle;
    private LinearLayout layout;
    private RelativeRevealView revealView;
    private RelativeLayout parentLayout;

    private Context context;
    private List<String> list = null;
    private boolean hasMeasured = false;
    private int duration = 500;//动画时间

    public ChoiceListDialog(Context context, List<String> list) {
        super(context, R.style.normal_dialog);
        this.context = context;
        this.list = list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choice_list);
        initWidget();
        init();
    }

    private void initWidget() {
        cancle = (TextView) findViewById(R.id.dialog_choice_photo_cancle);
        layout = (LinearLayout) findViewById(R.id.dialog_choice_photo_layout);
        revealView = (RelativeRevealView) findViewById(R.id.dialog_choice_list_revealview);
        parentLayout = (RelativeLayout) findViewById(R.id.dialog_choice_list_parent_layout);
    }

    private void init() {
        cancle.setOnClickListener(this);
        initList();
    }

    /**
     * 初始化列表
     */
    public void initList() {
        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i);
            final int index = i;
            TextView textView = new TextView(getContext());
            textView.setHeight(DensityUtil.dip2px(getContext(), 48));
            textView.setGravity(Gravity.CENTER);
            textView.setText(str);
            textView.setTextSize(14);
            textView.setBackgroundResource(R.drawable.btn_trans_select);
            textView.setTextColor(getContext().getResources().getColor(R.color.gray_600));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnimExit(new RelativeRevealView.AnimaFinshListener() {
                        @Override
                        public void onAnimFinish(Animator animation) {
                            dismiss();
                            if (onChoiceListItemClickListener != null) {
                                onChoiceListItemClickListener.onListItemClick(index);
                            }
                        }
                    });

                }
            });
            layout.addView(textView, i);
        }
    }

    @Override
    public void show() {
        super.show();
        ViewTreeObserver vto = revealView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                if (hasMeasured == false) {
                    int height = revealView.getMeasuredHeight();
                    int width = revealView.getMeasuredWidth();
                    //获取到宽度和高度后，可用于计算
                    hasMeasured = true;
                    revealView.show(duration, null);
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == cancle.getId()) {
            AnimExit(new RelativeRevealView.AnimaFinshListener() {
                @Override
                public void onAnimFinish(Animator animation) {
                    dismiss();
                }
            });
        }
    }

    private void AnimExit(RelativeRevealView.AnimaFinshListener animaFinshListener) {
        int x = (int) revealView.getEvent().getX() - (int) revealView.getX();
        int y = (int) revealView.getEvent().getY() - (int) revealView.getY() - ScreenUtils.getStatusHeight(getContext());
        revealView.hide(x, y, duration, animaFinshListener);
    }


    private OnChoiceListItemClickListener onChoiceListItemClickListener = null;

    public void setOnChoiceListItemClickListener(OnChoiceListItemClickListener onChoiceListItemClickListener) {
        this.onChoiceListItemClickListener = onChoiceListItemClickListener;
    }

    public interface OnChoiceListItemClickListener {
        void onListItemClick(int index);
    }

}
