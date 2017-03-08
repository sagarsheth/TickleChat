package com.techpro.chat.ticklechat.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.techpro.chat.ticklechat.R;
import com.techpro.chat.ticklechat.Utils;
import com.techpro.chat.ticklechat.models.user.UserDetailsModel;
import com.techpro.chat.ticklechat.models.user.UserModel;
import com.techpro.chat.ticklechat.registration.RegistrationActivity;
import com.techpro.chat.ticklechat.rest.ApiClient;
import com.techpro.chat.ticklechat.rest.ApiInterface;
import com.techpro.chat.ticklechat.utils.AppUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Sagar on 06/04/16.
 */
public class LoginActivity extends Activity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {


    private EditText et_phone, et_pass;
    private Button btnLogin, btnSignup;
    LinearLayout btn_google_signin, btn_fb_signin;
    private static final String TAG = "LoginActivity";
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);

        et_pass = (EditText) findViewById(R.id.et_username);
        et_phone = (EditText) findViewById(R.id.et_pass);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btn_google_signin = (LinearLayout) findViewById(R.id.btn_google_signin);
        btn_fb_signin = (LinearLayout) findViewById(R.id.btn_fb_signin);

        btnLogin.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
        btn_google_signin.setOnClickListener(this);
        btn_fb_signin.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                if (AppUtils.isNetworkConnectionAvailable(getApplicationContext())) {
                    String phone = et_phone.getText().toString();
                    String pass = Utils.SHA1(et_pass.getText().toString());
                    if (phone != null && !phone.equals("") && pass != null && !pass.equals("")) {
                        showProgressDialog();
                        callLoginService(phone, Utils.SHA1(pass));
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Please enter complete details.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_signup:
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
                break;

            case R.id.btn_google_signin:

                break;

            case R.id.btn_fb_signin:
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void callLoginService(String username, String pass) {
        //Getting webservice instance which we need to call
        Call<UserModel> callForUserDetailsFromID = ApiClient.getClient().create(ApiInterface.class).loginUser(username, pass);
        //Calling Webservice by enqueue
        callForUserDetailsFromID.enqueue(new Callback<UserModel>() {

            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response != null && response.isSuccessful()) {
                    if (response.body().getStatus().equals("success")) {
                        UserDetailsModel getUserDetails = response.body().getBody();
                        Toast.makeText(getApplicationContext(), getUserDetails.getName(), Toast.LENGTH_LONG).show();
//TODO Aparna create table with fields which are declared in UserDetailsModel.java class Table name User Login model
                    } else {
                        Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }


//                    DataStorage.UserDetails = getUserDetails;
//
//                    Gson gson = new Gson();
//                    String json = gson.toJson(getUserDetails);
//                    Log.e(TAG, "json ==> " + json);

//                    DataStorage.myAllUserlist = null;
//                    DataStorage.chatUserList = null;
//                    DataStorage.mygrouplist = null;
//                    SharedPreferenceUtils.setValue(getApplicationContext(), SharedPreferenceUtils.LoginuserDetailsPreference, json);
//                    SharedPreferenceUtils.setColleactionObject(getApplicationContext(), SharedPreferenceUtils.myuserlist,
//                            DataStorage.myAllUserlist);
//                    SharedPreferenceUtils.setColleactionObject(getApplicationContext(), SharedPreferenceUtils.mygrouplist,
//                            DataStorage.mygrouplist);
//                    SharedPreferenceUtils.setColleactionObject(getApplicationContext(), SharedPreferenceUtils.chatUserID,
//                            DataStorage.chatUserList);
//                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
//                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.failmessage, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Success but null response");

                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                // Log error here since request failed
                Toast.makeText(getApplicationContext(), R.string.failmessage, Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();
            }
        });
    }
}
