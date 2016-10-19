package com.techpro.chat.ticklechat.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.techpro.chat.ticklechat.R;
import com.techpro.chat.ticklechat.activity.home.TicklersAdapter;
import com.techpro.chat.ticklechat.adapters.TickleFriendAdapter;
import com.techpro.chat.ticklechat.models.TickleFriend;

import java.util.ArrayList;
import java.util.List;

public class NewGroupFragment extends Fragment {
    private Button createGroup;
    private Activity mActivity = null;
    private TickleFriendAdapter mAdapter;
    private List<TickleFriend> movieList = new ArrayList<>();
    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_create_group,
                container, false);
        createGroup = (Button) view.findViewById(R.id.creategroup);
        mActivity = getActivity();
        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Create group","ssssssssssssss");
                final Dialog dialog = new Dialog(mActivity);
                // Include dialog.xml file
                dialog.setContentView(R.layout.activity_tickle_friend);
                // Set dialog title
                dialog.setTitle("Select Group Members");

                // set values for custom dialog components - text, image and button
                mRecyclerView = (RecyclerView) dialog.findViewById(R.id.my_recycler_view);



                mAdapter = new TickleFriendAdapter(movieList,true,true);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.setAdapter(mAdapter);
                prepareMovieData();
                dialog.show();
            }
        });
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
