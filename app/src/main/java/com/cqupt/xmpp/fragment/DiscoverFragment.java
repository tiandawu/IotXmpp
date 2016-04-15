package com.cqupt.xmpp.fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cqupt.xmpp.R;
import com.cqupt.xmpp.base.BaseFragment;
import com.cqupt.xmpp.bean.ChatMessage;
import com.cqupt.xmpp.db.ChatMsgDao;
import com.cqupt.xmpp.utils.DateUtils;

import java.util.ArrayList;

/**
 * Created by tiandawu on 2016/3/31.
 */
public class DiscoverFragment extends BaseFragment {
    private Button insertBtn, queryOneBtn, quweryListBtn;
    private ChatMsgDao chatMsgDao;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.frag_discover, container, false);
        chatMsgDao = new ChatMsgDao(getActivity());

        view.findViewById(R.id.insert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setFrom("devbird");
                chatMessage.setTo("dd");
                chatMessage.setBody("8888888888888");
                chatMessage.setOwner("devbird");
                chatMessage.setTime(DateUtils.getNowDateTime());
                chatMsgDao.insert(chatMessage);
            }
        });

        view.findViewById(R.id.queryOne).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatMessage chatMessage = chatMsgDao.queryTheLastMsg();
                Log.e("tt", "from=" + chatMessage.getFrom());
                Log.e("tt", "to=" + chatMessage.getTo());
                Log.e("tt", "body=" + chatMessage.getBody());
                Log.e("tt", "owner=" + chatMessage.getOwner());
                Log.e("tt", "time=" + chatMessage.getTime());
                Log.e("tt", "*******************************");
            }
        });


        view.findViewById(R.id.queryList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<ChatMessage> chatMessages = chatMsgDao.queryMsg("sensor@xmpp/s1", "client@xmpp/Smack", 0);
                Log.e("tt", "size=" + chatMessages.size());
                for (ChatMessage message : chatMessages) {
                    Log.e("tt", "from=" + message.getFrom());
                    Log.e("tt", "to=" + message.getTo());
                    Log.e("tt", "body=" + message.getBody());
                    Log.e("tt", "owner=" + message.getOwner());
                    Log.e("tt", "time=" + message.getTime());
                    Log.e("tt", "----------------------------");
                }
            }
        });

        return view;
    }
}
