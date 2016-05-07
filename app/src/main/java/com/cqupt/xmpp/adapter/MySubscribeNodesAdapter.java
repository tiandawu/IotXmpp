package com.cqupt.xmpp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cqupt.xmpp.R;
import com.cqupt.xmpp.bean.NodeSubStatus;
import com.cqupt.xmpp.manager.XmppConnectionManager;
import com.cqupt.xmpp.widght.CircleImageView;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;

import java.util.ArrayList;

import static com.cqupt.xmpp.R.id.session_user_status;

/**
 * Created by tiandawu on 2016/4/20.
 */
public class MySubscribeNodesAdapter extends RecyclerView.Adapter<MySubscribeNodesAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<NodeSubStatus> mList;
    private Roster mRoster;
    private OnClickListener mListener;


    public MySubscribeNodesAdapter(Context context, ArrayList<NodeSubStatus> mList) {
        this.mContext = context;
        this.mList = mList;
        mRoster = XmppConnectionManager.getXmppconnectionManager().getRoster();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(View.inflate(mContext, R.layout.item_session_child, null));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        NodeSubStatus nodeSubStatus = mList.get(position);
        String nodeName = nodeSubStatus.getNodeName();
        String name = nodeName.substring(0, nodeName.lastIndexOf("@"));
        String period = nodeSubStatus.getPeriod();
        String highLimit = nodeSubStatus.getHighLimit();
        String lowLimit = nodeSubStatus.getLowLimit();
        String subSttus = "";
        if (!period.equals("false")) {
            subSttus += period;
        }

        if (!highLimit.equals("false")) {

            if (subSttus != null) {
                subSttus += "/" + highLimit;
            } else {
                subSttus += highLimit;
            }

        }

        if (!lowLimit.equals("false")) {

            if (subSttus != null) {
                subSttus += "/" + lowLimit;
            } else {
                subSttus += lowLimit;
            }

        }
        RosterEntry entry = mRoster.getEntry(nodeName.substring(0, nodeName.lastIndexOf("/")));
        String type = mRoster.getPresence(entry.getUser()) + "";
        holder.userName.setText(name);
        holder.sessionContent.setText(subSttus);

        if (type.equals("available (online)")) {
            holder.userStatus.setText(mContext.getResources().getText(R.string.on_line));
            holder.nodeSleep.setVisibility(View.GONE);
            holder.nodeOffline.setVisibility(View.GONE);
            holder.nodeBusy.setVisibility(View.GONE);
            holder.userStatusImage.setVisibility(View.VISIBLE);
        } else if (type.equals("available (sleep)")) {
            holder.userStatus.setText(mContext.getResources().getText(R.string.on_sleep));
            holder.userStatusImage.setVisibility(View.GONE);
            holder.nodeBusy.setVisibility(View.GONE);
            holder.nodeOffline.setVisibility(View.GONE);
            holder.nodeSleep.setVisibility(View.VISIBLE);
        } else if (type.equals("available (busy)")) {
            holder.userStatus.setText(mContext.getResources().getText(R.string.on_busy));
            holder.userStatusImage.setVisibility(View.GONE);
            holder.nodeOffline.setVisibility(View.GONE);
            holder.nodeSleep.setVisibility(View.GONE);
            holder.nodeBusy.setVisibility(View.VISIBLE);
        } else {
            holder.userStatus.setText(mContext.getResources().getText(R.string.off_line));
            holder.userStatusImage.setVisibility(View.GONE);
            holder.nodeSleep.setVisibility(View.GONE);
            holder.nodeBusy.setVisibility(View.GONE);
            holder.nodeOffline.setVisibility(View.VISIBLE);
        }

        if (mListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    mListener.onItemClick(holder.itemView, position);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getLayoutPosition();
                    mListener.onItemLongClick(holder.itemView, position);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView userImage;
        private TextView userName, sessionContent, userStatus, nodeSleep, nodeBusy, nodeOffline;
        private ImageView userStatusImage;

        public MyViewHolder(View itemView) {
            super(itemView);
            userImage = (CircleImageView) itemView.findViewById(R.id.session_user_img);
            userStatusImage = (ImageView) itemView.findViewById(R.id.session_user_status_img);
            userStatus = (TextView) itemView.findViewById(session_user_status);
            userName = (TextView) itemView.findViewById(R.id.session_user_name);
            sessionContent = (TextView) itemView.findViewById(R.id.session_content);
            nodeSleep = (TextView) itemView.findViewById(R.id.contact_user_status_sleep);
            nodeBusy = (TextView) itemView.findViewById(R.id.contact_user_status_busy);
            nodeOffline = (TextView) itemView.findViewById(R.id.contact_user_status_offline);
        }
    }


    public interface OnClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public void setOnClickListener(OnClickListener listener) {
        this.mListener = listener;
    }
}
