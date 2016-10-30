package com.techpro.chat.ticklechat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techpro.chat.ticklechat.R;
import com.techpro.chat.ticklechat.models.Messages;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder>{

    private ArrayList<Messages> moviesList;
    private boolean showCheckbox = false;
    private boolean showBelowDesc = false;
    private Context mContext = null;

    public MessageAdapter( ArrayList<Messages> moviesList, boolean showCheckbox, boolean showBelowDesc) {
        this.moviesList = moviesList;
        this.showCheckbox = showCheckbox;
        this.showBelowDesc = showBelowDesc;
    }

    public MessageAdapter( ArrayList<Messages> moviesList, Context context, boolean showCheckbox, boolean showBelowDesc) {
        this.moviesList = moviesList;
        this.showCheckbox = showCheckbox;
        this.showBelowDesc = showBelowDesc;
        this.mContext = context;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_chat_text_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Messages movie = moviesList.get(position);
        holder.tvChatMessage.setText(movie.getMessage());
//        Log.e("(position % 2) => "+position,"(position % 2)=>"+(position % 2));
//        if ((position % 2) == 0) {
//            holder.backgroundlayout.setBackgroundColor(Color.parseColor("#f1f1f1"));
//        }
    }

    @Override
    public int getItemCount() {
        if (moviesList == null)
            return 0;
        return moviesList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvChatMessage;
//        public ImageView friendImage;
//        public CheckBox addfriends;
//        public LinearLayout backgroundlayout;

        public MyViewHolder(View view) {
            super(view);
//            backgroundlayout = (LinearLayout) view.findViewById(R.id.backgroundlayout);
            tvChatMessage = (TextView) view.findViewById(R.id.tv_chat_message);
//            friendNumber = (TextView) view.findViewById(R.id.friendNumber);
//            friendImage = (ImageView) view.findViewById(R.id.friendImage);
//            addfriends = (CheckBox) view.findViewById(R.id.checkBox);
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
