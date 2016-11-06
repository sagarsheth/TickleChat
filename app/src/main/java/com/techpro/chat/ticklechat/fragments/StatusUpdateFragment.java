package com.techpro.chat.ticklechat.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.techpro.chat.ticklechat.R;
import com.techpro.chat.ticklechat.StatusUpdateDialog;
import com.techpro.chat.ticklechat.adapters.StatusAdapter;
import com.techpro.chat.ticklechat.listeners.GenericListener;
import com.techpro.chat.ticklechat.models.CustomModel;
import com.techpro.chat.ticklechat.models.DataStorage;
import com.techpro.chat.ticklechat.models.TickleFriend;
import com.techpro.chat.ticklechat.models.user.UserDetailsModel;
import com.techpro.chat.ticklechat.models.user.UserModel;
import com.techpro.chat.ticklechat.rest.ApiClient;
import com.techpro.chat.ticklechat.rest.ApiInterface;
import com.techpro.chat.ticklechat.utils.AppUtils;
import com.techpro.chat.ticklechat.utils.SharedPreferenceUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatusUpdateFragment extends Fragment implements View.OnClickListener {
    private View mView;
    private RecyclerView mRvChangeStatus;
    private ImageView mIvEditStatus;
    private TextView mTvStatus;
    private StatusUpdateDialog mStatusUpdateDialog;
    private StatusAdapter mStatusAdapter;
    private List<TickleFriend> mTempList = new ArrayList<>();
    private ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_status_update, container, false);
        initUi();
        return mView;
    }

    private void initUi() {
        mRvChangeStatus = (RecyclerView) mView.findViewById(R.id.rv_select_sentence);
        mRvChangeStatus.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvChangeStatus.setHasFixedSize(false);
        mRvChangeStatus.setItemAnimator(new DefaultItemAnimator());

        assignTempData();

        mStatusAdapter = new StatusAdapter(mTempList, getContext());
        mRvChangeStatus.setAdapter(mStatusAdapter);

        mTvStatus = (TextView) mView.findViewById(R.id.tv_status);
        mIvEditStatus = (ImageView) mView.findViewById(R.id.iv_edit_status);

        if (DataStorage.UserDetails.getUser_status() != null) {
            mTvStatus.setText(DataStorage.UserDetails.getUser_status());
        } else {
            mTvStatus.setText("none");
        }
        mIvEditStatus.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_edit_status:
                mStatusUpdateDialog = new StatusUpdateDialog(getActivity(), new GenericListener<String>() {
                    @Override
                    public void onResponse(int callerID, String messages) {

                        switch (callerID) {
                            case R.id.tvPositive:
                                if (!AppUtils.isNetworkConnectionAvailable(getContext())) {
                                    Toast.makeText(getContext(),
                                            getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (messages != null && !messages.toString().equals("")) {
                                    dialog = ProgressDialog.show(StatusUpdateFragment.this.getActivity(), "Loading", "Please wait...", true);
                                    callupdateStatusService(Integer.parseInt(DataStorage.UserDetails.getId()), messages.toString());
                                } else {
                                    Toast.makeText(getContext(), "Please enter status.", Toast.LENGTH_LONG).show();
                                }
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

    private void assignTempData() {
        mTempList.add(new TickleFriend("Mad Max: Fury Road", "Action & Adventure", "2015", null));
        mTempList.add(new TickleFriend("Mad Max: Fury Road", "Action & Adventure", "2015", null));
        mTempList.add(new TickleFriend("Mad Max: Fury Road", "Action & Adventure", "2015", null));
        mTempList.add(new TickleFriend("Mad Max: Fury Road", "Action & Adventure", "2015", null));
    }


    /*
* Get - User details by user chatUserList
* @param userId - user chatUserList
* */
    private synchronized void callupdateStatusService(int userid, final String status) {
        //Getting webservice instance which we need to call
        Call<CustomModel> callForUserDetailsFromID = (ApiClient.createServiceWithAuth(DataStorage.UserDetails.getId())
                .create(ApiInterface.class)).callupdateStatusService(userid, status);
        //Calling Webservice by enqueue
        callForUserDetailsFromID.enqueue(new Callback<CustomModel>() {
            @Override
            public void onResponse(Call<CustomModel> call, Response<CustomModel> response) {
                if (response != null) {
                    if (response.body() != null && response.body().getStatus().equals("success")) {
                        DataStorage.UserDetails.setUser_status(status);
                        Log.e("callupdateStatusService", "Success  callLoginService : " + DataStorage.UserDetails);
                        Log.e("callupdateStatusService", "Success  getUserDetails.getId() : " + DataStorage.UserDetails.getId());
                        Gson gson = new Gson();
                        String json = gson.toJson(DataStorage.UserDetails);
                        SharedPreferenceUtils.setValue(StatusUpdateFragment.this.getContext(), SharedPreferenceUtils.LoginuserDetailsPreference, json);
                        Log.e("onResponse", "getUserDetails ==> " + json);
                        Toast.makeText(getContext(), "Status updated Succesfully.", Toast.LENGTH_LONG).show();
                        mTvStatus.setText(status);
                    }
                } else {
                    Toast.makeText(getContext(), R.string.failmessage, Toast.LENGTH_LONG).show();
                    Log.e("profile", "Success callTickles_Service but null response");
                }
                dialog.dismiss();
                mStatusUpdateDialog.dismiss();
            }

            @Override
            public void onFailure(Call<CustomModel> call, Throwable t) {
                // Log error here since request failed
                Toast.makeText(getContext(), R.string.failmessage, Toast.LENGTH_LONG).show();
                Log.e("profile", t.toString());
                dialog.dismiss();
                mStatusUpdateDialog.dismiss();
            }
        });

    }
}
