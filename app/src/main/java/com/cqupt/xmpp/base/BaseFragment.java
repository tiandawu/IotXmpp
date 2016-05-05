package com.cqupt.xmpp.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by tiandawu on 2016/3/31.
 */
public abstract class BaseFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = initView(inflater,container);
        initData();
        return view;
    }

    protected abstract View initView(LayoutInflater inflater, ViewGroup container);

    protected void initData() {

    }
}
