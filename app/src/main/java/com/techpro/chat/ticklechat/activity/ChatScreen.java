package com.techpro.chat.ticklechat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.techpro.chat.ticklechat.R;
import com.techpro.chat.ticklechat.adapters.MessageAdapter;
import com.techpro.chat.ticklechat.adapters.TickleFriendAdapter;
import com.techpro.chat.ticklechat.models.TickleFriend;

import java.util.ArrayList;
import java.util.List;

public class ChatScreen extends Activity {
    protected Toolbar mToolbar;
    protected RecyclerView message_list,text_list;
    private MessageAdapter mAdapter;
    private List<TickleFriend> movieList = new ArrayList<>();
    private List<TickleFriend> movieList1 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("ABC");
        message_list = (RecyclerView) findViewById(R.id.message_list);
        text_list = (RecyclerView) findViewById(R.id.text_list);

        mAdapter = new MessageAdapter(movieList,getApplicationContext(),false,true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        message_list.setLayoutManager(mLayoutManager);
        message_list.setItemAnimator(new DefaultItemAnimator());
        message_list.setAdapter(mAdapter);

        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getApplicationContext());
        text_list.setLayoutManager(mLayoutManager1);
        text_list.setItemAnimator(new DefaultItemAnimator());
        text_list.setAdapter(mAdapter);


        prepareMovieData();
    }

    private void prepareMovieData() {
        movieList.clear();
        TickleFriend movie = new TickleFriend(null, "ABC", "2 new message",null);
        movieList.add(movie);

        movie = new TickleFriend(null,"XYZ", "checked",null);
        movieList.add(movie);

        movie = new TickleFriend(null,"PQR", "1 new message",null);
        movieList.add(movie);

        movie = new TickleFriend(null,"MNO", "checked",null);
        movieList.add(movie);

        movie = new TickleFriend(null,"KLM", "checked",null);
        movieList.add(movie);

        mAdapter.notifyDataSetChanged();
    }
}