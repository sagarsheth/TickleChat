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
import com.techpro.chat.ticklechat.adapters.UserListAdapter;
import com.techpro.chat.ticklechat.models.DataStorage;
import com.techpro.chat.ticklechat.models.TickleFriend;
import com.techpro.chat.ticklechat.models.user.User;
import com.techpro.chat.ticklechat.models.user.UserGroupBotModel;

import java.util.ArrayList;
import java.util.List;

public class HomeScreenFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private UserListAdapter mAdapter;
    private List<TickleFriend> movieList = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_tickle_friend,
                container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        List<UserGroupBotModel> list = new ArrayList<>();
        list.addAll(DataStorage.myuserlist);
        list.addAll(DataStorage.mygrouplist);
        mAdapter = new UserListAdapter(list,getContext(),false,true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("RecyclerView", "getPosition：" );
//                Intent intent = new Intent(getContext(), ChatScreen.class);
//                startActivity(intent);

            }
        });
        prepareMovieData();
        return view;
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
