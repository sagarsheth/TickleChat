package com.techpro.chat.ticklechat.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techpro.chat.ticklechat.R;
import com.techpro.chat.ticklechat.adapters.TickleFriendAdapter;
import com.techpro.chat.ticklechat.adapters.UserListAdapter;
import com.techpro.chat.ticklechat.models.DataStorage;
import com.techpro.chat.ticklechat.models.TickleFriend;
import com.techpro.chat.ticklechat.models.user.UserGroupBotModel;

import java.util.ArrayList;
import java.util.List;

public class TickleFriendFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private TickleFriendAdapter mAdapter;
    private List<TickleFriend> movieList = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_tickle_friend,
                container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);



        mAdapter = new TickleFriendAdapter(DataStorage.chatUserList,getContext(),false,true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        prepareMovieData();
        return view;
    }

    private void prepareMovieData() {
        movieList.clear();
        TickleFriend movie = new TickleFriend("Mad Max: Fury Road", "Action & Adventure", "2015",null);
        movieList.add(movie);

        movie = new TickleFriend("Inside Out", "Animation, Kids & Family", "2015",null);
        movieList.add(movie);

        movie = new TickleFriend("Star Wars: Episode VII - The Force Awakens", "Action", "2015",null);
        movieList.add(movie);

        movie = new TickleFriend("Shaun the Sheep", "Animation", "2015",null);
        movieList.add(movie);

        movie = new TickleFriend("The Martian", "Science Fiction & Fantasy", "2015",null);
        movieList.add(movie);

        movie = new TickleFriend("Mission: Impossible Rogue Nation", "Action", "2015",null);
        movieList.add(movie);

        movie = new TickleFriend("Up", "Animation", "2009",null);
        movieList.add(movie);

        mAdapter.notifyDataSetChanged();
    }
}
