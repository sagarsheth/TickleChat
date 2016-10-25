package com.techpro.chat.ticklechat.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.techpro.chat.ticklechat.R;
import com.techpro.chat.ticklechat.activity.ChatScreen;
import com.techpro.chat.ticklechat.models.message.Tickles;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder>{

    private List<Tickles.MessageList.ChatMessagesTicklesList> moviesList;
    private boolean showCheckbox = false;
    private boolean showBelowDesc = false;
    private Context mContext = null;

    public ChatAdapter(List<Tickles.MessageList.ChatMessagesTicklesList> moviesList, boolean showCheckbox, boolean showBelowDesc) {
        this.moviesList = moviesList;
        this.showCheckbox = showCheckbox;
        this.showBelowDesc = showBelowDesc;
    }

    public ChatAdapter(List<Tickles.MessageList.ChatMessagesTicklesList> moviesList, Context context, boolean showCheckbox, boolean showBelowDesc) {
        this.moviesList = moviesList;
        this.showCheckbox = showCheckbox;
        this.showBelowDesc = showBelowDesc;
        this.mContext = context;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tickle_friend_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Tickles.MessageList.ChatMessagesTicklesList movie = moviesList.get(position);
        holder.friendName.setText(movie.getMessage());
        holder.friendImage.setVisibility(View.GONE);
        if (showCheckbox) {
            holder.addfriends.setVisibility(View.VISIBLE);
        } else {
            holder.addfriends.setVisibility(View.INVISIBLE);
        }
        if (movie.getName() != null)
            holder.friendNumber.setText(movie.getName());
        if (showBelowDesc) {
            holder.friendNumber.setVisibility(View.VISIBLE);
        } else {
            holder.friendNumber.setVisibility(View.INVISIBLE);
        }
        Log.e("(position % 2) => "+position,"(position % 2)=>"+(position % 2));
        if ((position % 2) == 0) {
            holder.backgroundlayout.setBackgroundColor(Color.parseColor("#f1f1f1"));
        }
    }

    @Override
    public int getItemCount() {
        if (moviesList == null)
            return 0;

        return moviesList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView friendName,  friendNumber;
        public ImageView friendImage;
        public CheckBox addfriends;
        public LinearLayout backgroundlayout;

        public MyViewHolder(View view) {
            super(view);
            backgroundlayout = (LinearLayout) view.findViewById(R.id.backgroundlayout);
            friendName = (TextView) view.findViewById(R.id.friendName);
            friendNumber = (TextView) view.findViewById(R.id.friendNumber);
            friendImage = (ImageView) view.findViewById(R.id.friendImage);
            addfriends = (CheckBox) view.findViewById(R.id.checkBox);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("RecyclerView", "getPosition：" + getPosition());
                    Log.d("RecyclerView", "getAdapterPosition：" + getAdapterPosition());
                    Log.d("RecyclerView", "getLayoutPosition：" + getLayoutPosition());
//                    if (mContext != null){
//                        Intent intent = new Intent(mContext, ChatScreen.class);
//                        mContext.startActivity(intent);
//                    }
                }
            });
        }
    }
}