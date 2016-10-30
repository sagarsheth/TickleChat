package com.techpro.chat.ticklechat.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.techpro.chat.ticklechat.R;
import com.techpro.chat.ticklechat.StatusUpdateDialog;
import com.techpro.chat.ticklechat.adapters.StatusAdapter;
import com.techpro.chat.ticklechat.listeners.GenericListener;
import com.techpro.chat.ticklechat.models.TickleFriend;

import java.util.ArrayList;
import java.util.List;

public class StatusUpdateFragment extends Fragment implements View.OnClickListener
{
    private View mView;
    private RecyclerView mRvChangeStatus;
    private ImageView mIvEditStatus;
    private TextView mTvStatus;
    private StatusUpdateDialog mStatusUpdateDialog;
    private StatusAdapter mStatusAdapter;
    private List<TickleFriend> mTempList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.fragment_status_update, container, false);
        initUi();
        return mView;
    }

    private void initUi()
    {
        mRvChangeStatus = (RecyclerView) mView.findViewById(R.id.rv_select_sentence);
        mRvChangeStatus.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvChangeStatus.setHasFixedSize(false);
        mRvChangeStatus.setItemAnimator(new DefaultItemAnimator());

        assignTempData();

        mStatusAdapter = new StatusAdapter(mTempList, getContext());
        mRvChangeStatus.setAdapter(mStatusAdapter);

        mTvStatus = (TextView) mView.findViewById(R.id.tv_status);
        mIvEditStatus = (ImageView) mView.findViewById(R.id.iv_edit_status);

    }

    @Override
    public void onClick (View v)
    {
        switch (v.getId())
        {
            case R.id.iv_edit_status:
                mStatusUpdateDialog = new StatusUpdateDialog(getActivity(), new GenericListener<String>() {
                    @Override
                    public void onResponse(int callerID, String messages) {

                        switch (callerID) {
                            case R.id.tvPositive:
                                mTvStatus.setText(messages);
                                break;

                            case R.id.tvNegative:
                                mStatusUpdateDialog.cancel();
                                break;
                        }
                    }
                });

                mStatusUpdateDialog.setTitle("Hey! We do not take any pictures without your permission. Wanna try again ?");
                mStatusUpdateDialog.setPositiveButtonText("OK");
                mStatusUpdateDialog.setNegativeButtonText("CANCEL");
                mStatusUpdateDialog.setCancelable(false);
                mStatusUpdateDialog.show();

                break;
        }
    }
    private void assignTempData()
    {
        mTempList.add(new TickleFriend("Mad Max: Fury Road", "Action & Adventure", "2015", null));
        mTempList.add(new TickleFriend("Mad Max: Fury Road", "Action & Adventure", "2015", null));
        mTempList.add(new TickleFriend("Mad Max: Fury Road", "Action & Adventure", "2015", null));
        mTempList.add(new TickleFriend("Mad Max: Fury Road", "Action & Adventure", "2015", null));
    }
}
