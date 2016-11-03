package com.techpro.chat.ticklechat.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.techpro.chat.ticklechat.R;
import com.techpro.chat.ticklechat.models.CustomModel;
import com.techpro.chat.ticklechat.models.DataStorage;
import com.techpro.chat.ticklechat.models.message.Tickles;
import com.techpro.chat.ticklechat.rest.ApiClient;
import com.techpro.chat.ticklechat.rest.ApiInterface;
import com.techpro.chat.ticklechat.utils.SharedPreferenceUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SentenceFragment extends Fragment implements View.OnClickListener{

    private View mView;
    private TextView mTvAddNewTickle;
    private TextView mTvBtnSave;
    private ProgressDialog dialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(mView ==null)
        {
            mView = inflater.inflate(R.layout.fragment_sentence_fragment, container, false);
        }
        initUi();
        return mView;
    }

    private void initUi()
    {
        dialog = ProgressDialog.show(SentenceFragment.this.getActivity(), "Loading", "Please wait...", true);
        getTicklesService(Integer.parseInt(DataStorage.UserDetails.getId()));
        mTvAddNewTickle = (TextView) mView.findViewById(R.id.tv_add_new_tickle);
        mTvBtnSave = (TextView) mView.findViewById(R.id.tv_btn_save);
        mTvBtnSave.setOnClickListener(this);
    }

    @Override
    public void onClick (View v)
    {
        switch (v.getId())
        {
            case R.id.tv_btn_save:
                dialog = ProgressDialog.show(SentenceFragment.this.getActivity(), "Loading", "Please wait...", true);
                callAddSentenceService(DataStorage.UserDetails.getId(),mTvAddNewTickle.getText().toString());
                break;

        }

    }


    /*
* Get - User details by user chatUserList
* @param userId - user chatUserList
* */
    private synchronized void callAddSentenceService(String userid, final String status) {
        //Getting webservice instance which we need to call
        Call<CustomModel> callForUserDetailsFromID = (ApiClient.createServiceWithAuth(DataStorage.UserDetails.getId())
                .create(ApiInterface.class)).callAddSentenceService(status,userid);
        //Calling Webservice by enqueue
        callForUserDetailsFromID.enqueue(new Callback<CustomModel>() {
            @Override
            public void onResponse(Call<CustomModel> call, Response<CustomModel> response) {
                if (response != null) {
                    if (response.body() != null && response.body().getStatus().equals("success")) {
                        mTvAddNewTickle.setText("");
                    }

                } else {
                    Log.e("profile", "Success callTickles_Service but null response");
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<CustomModel> call, Throwable t) {
                // Log error here since request failed
                Log.e("profile", t.toString());
                dialog.dismiss();
            }
        });

    }


    /*
* Get - User details by user chatUserList
* @param userId - user chatUserList
* */
    private synchronized void getTicklesService(int userid) {
        //Getting webservice instance which we need to call
        Call<Tickles> callForUserDetailsFromID = (ApiClient.createServiceWithAuth(DataStorage.UserDetails.getId())
                .create(ApiInterface.class)).getTickles(userid);
        //Calling Webservice by enqueue
        callForUserDetailsFromID.enqueue(new Callback<Tickles>() {
            @Override
            public void onResponse(Call<Tickles> call, Response<Tickles> response) {
                if (response != null) {
                    if (response.body() != null && response.body().getStatus().equals("success")) {

                    }

                } else {
                    Log.e("profile", "Success callTickles_Service but null response");
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<Tickles> call, Throwable t) {
                // Log error here since request failed
                Log.e("profile", t.toString());
                dialog.dismiss();
            }
        });

    }
}
