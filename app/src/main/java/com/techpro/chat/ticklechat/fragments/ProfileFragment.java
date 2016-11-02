package com.techpro.chat.ticklechat.fragments;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.techpro.chat.ticklechat.R;
import com.techpro.chat.ticklechat.models.DataStorage;
import com.techpro.chat.ticklechat.models.GetGroupDetails;
import com.techpro.chat.ticklechat.models.Group;
import com.techpro.chat.ticklechat.models.message.AllMessages;
import com.techpro.chat.ticklechat.models.user.UserDetailsModel;
import com.techpro.chat.ticklechat.models.user.UserModel;
import com.techpro.chat.ticklechat.rest.ApiClient;
import com.techpro.chat.ticklechat.rest.ApiInterface;
import com.techpro.chat.ticklechat.utils.SharedPreferenceUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment implements View.OnClickListener{
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ImageView ivProfileIcon;
    private EditText profilename;
    private EditText profileemail;
    private EditText profilephone;
    private EditText profile_date;
    private TextView tvBtnMale;
    private TextView tvBtnFemale;
    private TextView submit;
    private ProgressDialog dialog;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view == null)
        {
            view = inflater.inflate(R.layout.activity_profile, container, false);
        }
        initUi();
        return view;
    }
    private void initUi()
    {
        ivProfileIcon = (ImageView) view.findViewById(R.id.iv_profile_icon);
        profilename = (EditText) view.findViewById(R.id.profilename);
        profileemail = (EditText) view.findViewById(R.id.profileemail);
        profilephone = (EditText) view.findViewById(R.id.profilephone);
        profile_date = (EditText) view.findViewById(R.id.profile_date);
        tvBtnMale = (TextView) view.findViewById(R.id.tv_btn_male);
        tvBtnFemale = (TextView) view.findViewById(R.id.tv_btn_female);
        submit = (TextView) view.findViewById(R.id.submit);

        tvBtnMale.setSelected(true);
        tvBtnMale.setOnClickListener(this);
        tvBtnFemale.setOnClickListener(this);
        submit.setOnClickListener(this);
        ivProfileIcon.setOnClickListener(this);
        if (DataStorage.UserDetails.getProfile_image() != null) {
            byte[] decodedString = Base64.decode(DataStorage.UserDetails.getProfile_image().getBytes(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ivProfileIcon.setImageBitmap(decodedByte);
        }
        profilename.setText(DataStorage.UserDetails.getName());
        profileemail.setText(DataStorage.UserDetails.getEmail());
        profilephone.setText(DataStorage.UserDetails.getPhone());
        profile_date.setText(DataStorage.UserDetails.getBirthday());
        if (DataStorage.UserDetails.getGender().equals("m")) {
            tvBtnFemale.setSelected(false);
            tvBtnMale.setSelected(true);
        } else {

            tvBtnMale.setSelected(false);
            tvBtnFemale.setSelected(true);
        }
    }

    @Override
    public void onClick (View v)
    {
        switch (v.getId())
        {
            case R.id.submit:
                dialog = ProgressDialog.show(ProfileFragment.this.getActivity(), "Loading", "Please wait...", true);
                callUpdateUserDataService(Integer.parseInt(DataStorage.UserDetails.getId()),profilename.getText().toString(), "m" ,profile_date.getText().toString(),
                        profilephone.getText().toString(),profileemail.getText().toString(),"");
                break;

            case R.id.iv_profile_icon:
                // TODO: 30/10/16
                break;

            case R.id.tv_btn_male:
                // TODO: 30/10/16
                tvBtnFemale.setSelected(false);
                tvBtnMale.setSelected(true);
                break;

            case R.id.tv_btn_female:
                // TODO: 30/10/16
                tvBtnMale.setSelected(false);
                tvBtnFemale.setSelected(true);
                break;
        }
    }

    /*
* Get - User details by user chatUserList
* @param userId - user chatUserList
* */
    private synchronized void callUpdateUserDataService(int userid, String name,String gender,String dob,String phone,String email,String profile_image) {
        //Getting webservice instance which we need to call
        Call<UserModel> callForUserDetailsFromID = (ApiClient.createServiceWithAuth(DataStorage.UserDetails.getId())
                .create(ApiInterface.class)).callUpdateUserDataService(userid,name,gender,dob,phone,email,profile_image);
        //Calling Webservice by enqueue
        callForUserDetailsFromID.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response != null) {
                    UserDetailsModel getUserDetails = response.body().getBody();
                    DataStorage.UserDetails = getUserDetails;
                    Log.e("profile", "Success  callLoginService : " + getUserDetails);
                    Log.e("profile", "Success  getUserDetails.getId() : " + getUserDetails.getId());

                } else {
                    Log.e("profile", "Success callTickles_Service but null response");
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                // Log error here since request failed
                Log.e("profile", t.toString());
                dialog.dismiss();
            }
        });

    }
}
