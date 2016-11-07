package com.techpro.chat.ticklechat.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techpro.chat.ticklechat.R;
import com.techpro.chat.ticklechat.adapters.UserListAdapter;
import com.techpro.chat.ticklechat.models.DataStorage;
import com.techpro.chat.ticklechat.models.user.UserGroupBotModel;

import java.util.ArrayList;
import java.util.List;

public class HomeScreenFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private UserListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_tickle_friend,
                container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        List<UserGroupBotModel> list = new ArrayList<>();
        list.addAll(DataStorage.chatUserList);
        list.addAll(DataStorage.mygrouplist);
        mAdapter = new UserListAdapter(list,getContext(),false,true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
}
