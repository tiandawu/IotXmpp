package com.cqupt.xmpp.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cqupt.xmpp.R;
import com.cqupt.xmpp.base.BaseFragment;
import com.cqupt.xmpp.bean.ChatMessage;
import com.cqupt.xmpp.bean.ChatSession;
import com.cqupt.xmpp.db.ChatMsgDao;
import com.cqupt.xmpp.db.ChatSesionDao;
import com.cqupt.xmpp.utils.DensityUtil;
import com.cqupt.xmpp.widght.MyMarkerView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tiandawu on 2016/3/31.
 */
public class DiscoverFragment extends BaseFragment implements View.OnClickListener {
    private LineChart mLineChart;
    private ChatMsgDao mChatMsgDao;
    private ChatSesionDao mSesionDao;
    private LinearLayout lookMoreNodeLine;
    private ArrayList<ChatMessage> list;
    private ArrayList<ChatSession> mSessions;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.frag_discover, container, false);
        lookMoreNodeLine = (LinearLayout) view.findViewById(R.id.look_more);
        lookMoreNodeLine.setOnClickListener(this);
        mChatMsgDao = new ChatMsgDao(getActivity());
        mSesionDao = new ChatSesionDao(getActivity());
        mLineChart = (LineChart) view.findViewById(R.id.spread_line_chart);
        list = new ArrayList<>();
        mSessions = mSesionDao.queryMsg();
        if (mSessions.size() > 0) {
            ChatSession session = mSessions.get(mSessions.size() - 1);

//            ArrayList<ChatMessage> result = mChatMsgDao.queryMsgs(session.getFrom(), session.getTo());
//            for (ChatMessage chatMessage : result) {
//
//
//                if ("true".equals(chatMessage.getFlag())) {
//                    continue;
//                }
//                if ("设置成功".equals(chatMessage.getBody())||"订阅成功".equals(chatMessage.getBody()) || "取消订阅成功".equals(chatMessage.getBody())) {
//                    continue;
//                }
//
//                Log.e("tt", chatMessage.getBody());
//                list.add(chatMessage);
//            }

            getData(session);

//            list.addAll(mChatMsgDao.queryMsgs(session.getFrom(), session.getTo()));
        }
        LineData mLineData = getLineData(list);
        showChart(mLineChart, mLineData, getResources().getColor(R.color.gray_100));
        return view;
    }


    /**
     * 生成一个数据
     *
     * @return
     */
    private LineData getLineData(ArrayList<ChatMessage> list) {
//        ArrayList<ChatMessage> list = mChatMsgDao.queryMsg("temprature1@xmpp/temprature", "client@xmpp/Smack", 0);
//        Log.e("tt", "size = " + list.size());
        ArrayList<String> xValues = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            // x轴显示的数据，这里默认使用数字下标显示
            xValues.add("" + i);
        }

        // y轴的数据
        ArrayList<Entry> yValues = new ArrayList<Entry>();

        for (int i = 0; i < list.size(); i++) {
            String valueBody = list.get(i).getBody();
            if (valueBody.contains("当前")) {
                continue;
            }

            float value = Float.parseFloat(valueBody);
            yValues.add(new Entry(value, i));
        }

        // create a dataset and give it a type
        // y轴的数据集合
        LineDataSet lineDataSet = new LineDataSet(yValues, "数据折线图" /*显示在比例图上*/);
        // mLineDataSet.setFillAlpha(110);
        // mLineDataSet.setFillColor(Color.RED);

        //用y轴的集合来设置参数
        lineDataSet.setLineWidth(1.75f); // 线宽
        lineDataSet.setCircleSize(3f);// 显示的圆形大小
        lineDataSet.setColor(Color.WHITE);// 显示颜色
        lineDataSet.setCircleColor(Color.WHITE);// 圆形的颜色
        lineDataSet.setHighLightColor(Color.WHITE); // 高亮的线的颜色

        List<ILineDataSet> lineDataSets = new ArrayList<>();
        lineDataSets.add(lineDataSet); // add the datasets

        // create a data object with the datasets
        LineData lineData = new LineData(xValues, lineDataSets);

        return lineData;
    }


    // 设置显示的样式
    private void showChart(LineChart lineChart, LineData lineData, int color) {
        lineChart.setDrawBorders(false);  //是否在折线图上添加边框

        // no description text
        lineChart.setDescription("横轴为周期\r\n纵轴为测量值");// 数据描述
        // 如果没有数据的时候，会显示这个，类似listview的emtpyview
        lineChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable / disable grid background
        lineChart.setDrawGridBackground(false); // 是否显示表格颜色
        lineChart.setGridBackgroundColor(Color.BLACK & 0x70FFFFFF); // 表格的的颜色，在这里是是给颜色设置一个透明度
//        lineChart.setGridBackgroundColor(getResources().getColor(R.color.gray_800));
        // enable touch gestures
        lineChart.setTouchEnabled(true); // 设置是否可以触摸

        // enable scaling and dragging
        lineChart.setDragEnabled(true);// 是否可以拖拽
        lineChart.setScaleEnabled(true);// 是否可以缩放

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true);//

        MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.custom_marker_view);

        // set the marker to the chart
        lineChart.setMarkerView(mv);

        lineChart.setBackgroundColor(color);// 设置背景

        // add data
        lineChart.setData(lineData); // 设置数据

        // get the legend (only possible after setting data)
        Legend mLegend = lineChart.getLegend(); // 设置比例图标示，就是那个一组y的value的

        // modify the legend ...
        // mLegend.setPosition(LegendPosition.LEFT_OF_CHART);
        mLegend.setForm(Legend.LegendForm.SQUARE);// 样式
        mLegend.setFormSize(6f);// 字体
        mLegend.setTextColor(Color.WHITE);// 颜色
//      mLegend.setTypeface(mTf);// 字体

        lineChart.animateX(2000); // 立即执行的动画,x轴
    }

    @Override
    public void onClick(View v) {
        final PopupWindow popupWindow = new PopupWindow(getActivity());
        View view = View.inflate(getActivity(), R.layout.more_node_lines, null);
        popupWindow.setContentView(view);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(DensityUtil.dip2px(getActivity(), 220));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setFocusable(true);
        final ListView listView = (ListView) view.findViewById(R.id.pop_list_view);
        TextView cancle = (TextView) view.findViewById(R.id.pop_cancle);
        TextView emptyShow = (TextView) view.findViewById(R.id.pop_empty_show);
//        mSessions = mSesionDao.queryMsg();
        mSessions.clear();
        mSessions.addAll(mSesionDao.queryMsg());
        final ArrayList<String> datas = new ArrayList<>();
        for (ChatSession session : mSessions) {
            String from = session.getFrom();
            String name = from.substring(0, from.lastIndexOf("@"));

            if ("temprature1".equals(name)) {
                datas.add("温度传感器1");
            } else if ("temprature2".equals(name)) {
                datas.add("温度传感器2");
            } else if ("A1".equals(name)) {
                datas.add("风扇");
            } else if ("A2".equals(name)) {
                datas.add("直流电机");
            } else if ("A3".equals(name)) {
                datas.add("LED灯");
            } else if ("A4".equals(name)) {
                datas.add("步进电机");
            } else if ("B1".equals(name)) {
                datas.add("门磁");
            } else if ("B2".equals(name)) {
                datas.add("光电接近传感器");
            } else if ("light1".equals(name)) {
                datas.add("光照传感器1");
            } else if ("light2".equals(name)) {
                datas.add("光照传感器2");
            } else if ("smoke1".equals(name)) {
                datas.add("烟雾传感器1");
            } else if ("smoke2".equals(name)) {
                datas.add("烟雾传感器2");
            }

//            datas.add(name);
        }
        listView.setEmptyView(emptyShow);
        listView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, datas));
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                list.clear();
                ChatSession session = mSessions.get(position);
                String from = session.getFrom();
                String to = session.getTo();
//                list.addAll(mChatMsgDao.queryMsgs(from, to));
                getData(session);
                LineData data = getLineData(list);
                mLineChart.setData(data);
                mLineChart.notifyDataSetChanged();
                popupWindow.dismiss();
                mLineChart.invalidate();
            }
        });
    }


    private void getData(ChatSession session) {
        ArrayList<ChatMessage> result = mChatMsgDao.queryMsgs(session.getFrom(), session.getTo());
        for (ChatMessage chatMessage : result) {


            if ("true".equals(chatMessage.getFlag())) {
                continue;
            }
            if ("设置成功".equals(chatMessage.getBody()) || "订阅成功".equals(chatMessage.getBody()) || "取消订阅成功".equals(chatMessage.getBody())) {
                continue;
            }

            list.add(chatMessage);
        }
    }

}
