package com.techpro.chat.ticklechat.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.techpro.chat.ticklechat.R;
import com.techpro.chat.ticklechat.activity.ChatScreen;
import com.techpro.chat.ticklechat.models.Group;
import com.techpro.chat.ticklechat.models.user.User;
import com.techpro.chat.ticklechat.models.user.UserGroupBotModel;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder>{

    private List<UserGroupBotModel> UserList;
    private boolean showCheckbox = false;
    private boolean showBelowDesc = false;
    private Context mContext = null;

    public UserListAdapter(List<UserGroupBotModel> UserList, boolean showCheckbox, boolean showBelowDesc) {
        this.UserList = UserList;
        this.showCheckbox = showCheckbox;
        this.showBelowDesc = showBelowDesc;
    }

    public UserListAdapter(List<UserGroupBotModel> UserList, Context context, boolean showCheckbox, boolean showBelowDesc) {
        this.UserList = UserList;
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

        if (UserList.get(position) instanceof User){
            User user = (User) UserList.get(position);
            holder.friendName.setText(user.getName());
            Log.e("(position % 2) => "+position,"(User.getName()=>"+user.getName());
            if (user.getProfile_image() != null) {
                byte[] decodedString = Base64.decode(user.getProfile_image().getBytes(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.friendImage.setImageBitmap(decodedByte);
            }
            if (showCheckbox) {
                holder.addfriends.setVisibility(View.VISIBLE);
            } else {
                holder.addfriends.setVisibility(View.INVISIBLE);
            }
            if (user.getStatus() != null)
                holder.friendNumber.setText(user.getStatus());
            if (showBelowDesc) {
                holder.friendNumber.setVisibility(View.VISIBLE);
            } else {
                holder.friendNumber.setVisibility(View.INVISIBLE);
            }
//            Log.e("(position % 2) => "+position,"(position % 2)=>"+(position % 2));
//            if ((position % 2) == 0) {
//                holder.backgroundlayout.setBackgroundColor(Color.parseColor("#f1f1f1"));
//            }
        } else {
            Group user = (Group) UserList.get(position);
            holder.friendName.setText(user.getName());
            Log.e("(position % 2) => "+position,"(Group.getName()=>"+user.getName());
            if (user.getGroup_image() != null) {
                byte[] decodedString = Base64.decode(user.getGroup_image().getBytes(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.friendImage.setImageBitmap(decodedByte);
            }
            if (showCheckbox) {
                holder.addfriends.setVisibility(View.VISIBLE);
            } else {
                holder.addfriends.setVisibility(View.INVISIBLE);
            }
            if (user.getName() != null)
                holder.friendNumber.setText(user.getName());
            if (showBelowDesc) {
                holder.friendNumber.setVisibility(View.VISIBLE);
            } else {
                holder.friendNumber.setVisibility(View.INVISIBLE);
            }
//            Log.e("(position % 2) => "+position,"(position % 2)=>"+(position % 2));
//            if ((position % 2) == 0) {
//                holder.backgroundlayout.setBackgroundColor(Color.parseColor("#f1f1f1"));
//            }
        }

    }

    @Override
    public int getItemCount() {
        return UserList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView friendName,  friendNumber;
        public CircularImageView friendImage;
        public CheckBox addfriends;
        public LinearLayout backgroundlayout;

        public MyViewHolder(View view) {
            super(view);
            backgroundlayout = (LinearLayout) view.findViewById(R.id.backgroundlayout);
            friendName = (TextView) view.findViewById(R.id.friendName);
            friendNumber = (TextView) view.findViewById(R.id.friendNumber);
            friendImage = (CircularImageView) view.findViewById(R.id.friendImage);
            addfriends = (CheckBox) view.findViewById(R.id.checkBox);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("RecyclerView", "getPosition：" + getPosition());
//                    Log.d("RecyclerView", "getAdapterPosition：" + getAdapterPosition());
//                    Log.d("RecyclerView", "getLayoutPosition：" + getLayoutPosition());
                    if (mContext != null){
                        Intent intent = new Intent(mContext, ChatScreen.class);
                        if (UserList.get(getPosition()) instanceof User) {
                            User user = (User) UserList.get(getPosition());
                            intent.putExtra("userid",user.getId());
                            Log.d("RecyclerView", "user.getId()：" + user.getId());

                        } else {
                            Group grp = (Group) UserList.get(getPosition());
                            intent.putExtra("groupid",grp.getId());
                            Log.d("RecyclerView", "grp.getId()：" + grp.getId());
                        }
                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }
}
