package com.techpro.chat.ticklechat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.techpro.chat.ticklechat.R;
import com.techpro.chat.ticklechat.controller.MessageController;
import com.techpro.chat.ticklechat.models.DataStorage;
import com.techpro.chat.ticklechat.models.message.AllMessages;
import com.techpro.chat.ticklechat.models.user.UserDetailsModel;
import com.techpro.chat.ticklechat.utils.SharedPreferenceUtils;

import java.net.URLDecoder;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private List<AllMessages.MessageList.ChatMessagesList> moviesList;
    private boolean showCheckbox = false;
    private boolean showBelowDesc = false;
    private Context mContext = null;
    private MessageAdapter mAdapter1 = null;

    public ChatAdapter(List<AllMessages.MessageList.ChatMessagesList> moviesList, boolean showCheckbox, boolean showBelowDesc) {
        this.moviesList = moviesList;
        this.showCheckbox = showCheckbox;
        this.showBelowDesc = showBelowDesc;
    }

    public ChatAdapter(MessageAdapter mAdapter1,List<AllMessages.MessageList.ChatMessagesList> moviesList, Context context, boolean showCheckbox, boolean showBelowDesc) {
        this.moviesList = moviesList;
        this.showCheckbox = showCheckbox;
        this.showBelowDesc = showBelowDesc;
        this.mContext = context;
        this.mAdapter1 = mAdapter1;
        mAdapter1.setDataUpdateListener(new MessageAdapter.DataUpdated() {
            @Override
            public void dataUpdated(int isgroup, String id) {
                ChatAdapter.this.moviesList = (List<AllMessages.MessageList.ChatMessagesList>) SharedPreferenceUtils.
                        getColleactionObject(mContext, id);;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_chat_row_with_image, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AllMessages.MessageList.ChatMessagesList mChatMessageList = moviesList.get(position);

        String json = SharedPreferenceUtils.getValue(mContext, SharedPreferenceUtils.LoginuserDetailsPreference,"");
        Log.e("onCreate", "user ==> "+json);
        if (json.equals("")) {
        } else {
            Gson gson = new Gson();
            UserDetailsModel obj = gson.fromJson(json, UserDetailsModel.class);
            DataStorage.UserDetails = obj;
        }

        if(DataStorage.UserDetails.getId().equalsIgnoreCase(mChatMessageList.getFrom_id()))
        {
            holder.llRightRow.setVisibility(View.VISIBLE);
            holder.llLeftRow.setVisibility(View.GONE);
            try {
                String messages  = mChatMessageList.getMessage().replaceAll("%(?![0-9a-fA-F]{2})", "%25");
                messages = URLDecoder.decode(messages, "UTF-8");
                holder.tvMessageRight.setText(messages);
            } catch (Exception e) {
                holder.tvMessageRight.setText(mChatMessageList.getMessage());
            }

        }
        else
        {
            holder.llRightRow.setVisibility(View.GONE);
            holder.llLeftRow.setVisibility(View.VISIBLE);
            try {
                String messages  = mChatMessageList.getMessage().replaceAll("%(?![0-9a-fA-F]{2})", "%25");
                messages = URLDecoder.decode(messages, "UTF-8");
                holder.tvMessage.setText(messages);
            } catch (Exception e) {
                holder.tvMessage.setText(mChatMessageList.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        if (moviesList == null)
            return 0;
        return moviesList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView tvMessage;
        public ImageView ivProfile;
        public TextView tvMessageRight;
        public ImageView ivProfileRight;

        public LinearLayout llLeftRow;
        public LinearLayout llRightRow;


        public MyViewHolder(View view)
        {
            super(view);
            tvMessage = (TextView) view.findViewById(R.id.tv_message);
            ivProfile = (ImageView) view.findViewById(R.id.iv_profile);
            tvMessageRight = (TextView) view.findViewById(R.id.tv_message_right);
            ivProfileRight = (ImageView) view.findViewById(R.id.iv_profile_right);

            llLeftRow = (LinearLayout) view.findViewById(R.id.ll_left_row);
            llRightRow = (LinearLayout) view.findViewById(R.id.ll_right_row);
        }
    }
}



