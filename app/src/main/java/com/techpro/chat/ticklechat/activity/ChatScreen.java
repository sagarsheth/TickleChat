package com.techpro.chat.ticklechat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.techpro.chat.ticklechat.R;
import com.techpro.chat.ticklechat.adapters.ChatAdapter;
import com.techpro.chat.ticklechat.adapters.MessageAdapter;
import com.techpro.chat.ticklechat.adapters.TickleFriendAdapter;
import com.techpro.chat.ticklechat.controller.MessageController;
import com.techpro.chat.ticklechat.models.DataStorage;
import com.techpro.chat.ticklechat.models.Messages;
import com.techpro.chat.ticklechat.models.TickleFriend;
import com.techpro.chat.ticklechat.models.message.AllMessages;
import com.techpro.chat.ticklechat.models.message.Tickles;
import com.techpro.chat.ticklechat.models.user.User;
import com.techpro.chat.ticklechat.utils.SharedPreferenceUtils;

import java.util.ArrayList;
import java.util.List;

public class ChatScreen extends Activity {
    protected Toolbar mToolbar;
    protected RecyclerView message_list,text_list;
    private ChatAdapter mAdapter;
    private MessageAdapter mAdapter1;
    private List<AllMessages.MessageList.ChatMessagesList> movieList = new ArrayList<>();
    private List<TickleFriend> movieList1 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("ABC");
        message_list = (RecyclerView) findViewById(R.id.message_list);
        text_list = (RecyclerView) findViewById(R.id.text_list);
        String userid = getIntent().getStringExtra("userid");
        String groupid = getIntent().getStringExtra("groupid");
        Log.e("RecyclerView", "userid：" +userid);
        Log.e("RecyclerView", "groupid：" +groupid);
        if(groupid == null) {
            movieList = (List<AllMessages.MessageList.ChatMessagesList>) SharedPreferenceUtils.
                    getColleactionObject(getApplicationContext(),userid);
        } else {
            movieList = (List<AllMessages.MessageList.ChatMessagesList>) SharedPreferenceUtils.
                    getColleactionObject(getApplicationContext(),groupid);
        }
        mAdapter = new ChatAdapter(movieList,getApplicationContext(),false,true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        message_list.setLayoutManager(mLayoutManager);
        message_list.setItemAnimator(new DefaultItemAnimator());
        message_list.setAdapter(mAdapter);

        ArrayList<Messages> ctrl = new MessageController(getApplicationContext()).getMessages();
//        Log.e("ssssssssssssss","ctrl ==> "+ctrl);
        mAdapter1 = new MessageAdapter(ctrl,getApplicationContext(),false,true);
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getApplicationContext());
        text_list.setLayoutManager(mLayoutManager1);
        text_list.setItemAnimator(new DefaultItemAnimator());
        text_list.setAdapter(mAdapter1);

        final ImageView ivSlideUpDown = (ImageView)findViewById(R.id.iv_slide_up_down);
        ivSlideUpDown.setColorFilter(getResources().getColor(R.color.white));
        ivSlideUpDown.setImageResource(R.drawable.down_white_arrow_icon);
        ivSlideUpDown.setSelected(true);
        ivSlideUpDown.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v)
            {
                if(!ivSlideUpDown.isSelected())
                {
                    ivSlideUpDown.setSelected(true);
                    ivSlideUpDown.setImageResource(R.drawable.down_white_arrow_icon);
                    text_list.setVisibility(View.VISIBLE);

                }
                else
                {
                    ivSlideUpDown.setSelected(false);
                    ivSlideUpDown.setImageResource(R.drawable.up_arrow_icon);
                    text_list.setVisibility(View.GONE);
                }
            }

            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_options, menu);//Menu Resource, Menu
        return true;
    }
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_1:
                Toast.makeText(getApplicationContext(), "Item 1 Selected", Toast.LENGTH_LONG).show();
                return true;
            case R.id.menu_item_2:
                Toast.makeText(getApplicationContext(),"Item 2 Selected",Toast.LENGTH_LONG).show();
                return true;
            case R.id.menu_item_3:
                Toast.makeText(getApplicationContext(),"Item 3 Selected",Toast.LENGTH_LONG).show();
                return true;
            case R.id.menu_item_4:
                Toast.makeText(getApplicationContext(),"Item 4 Selected",Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //    private void prepareMovieData() {
//        movieList.clear();
//        TickleFriend movie = new TickleFriend("KLM", "ABC", "2 new message",null);
//        movieList.add(movie);
//
//        movie = new TickleFriend("KLM","XYZ", "checked",null);
//        movieList.add(movie);
//
//        movie = new TickleFriend("KLM","PQR", "1 new message",null);
//        movieList.add(movie);
//
//        movie = new TickleFriend("KLM","MNO", "checked",null);
//        movieList.add(movie);
//
//        movie = new TickleFriend("KLM","KLM", "checked",null);
//        movieList.add(movie);
//
////        mAdapter.notifyDataSetChanged();
//    }
}