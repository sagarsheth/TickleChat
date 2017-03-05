package com.techpro.chat.ticklechat.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.techpro.chat.ticklechat.R;
import com.techpro.chat.ticklechat.Utils;
import com.techpro.chat.ticklechat.activity.home.HomeActivity;
import com.techpro.chat.ticklechat.models.DataStorage;
import com.techpro.chat.ticklechat.models.user.UserDetailsModel;
import com.techpro.chat.ticklechat.models.user.UserModel;
import com.techpro.chat.ticklechat.registration.RegistrationActivity;
import com.techpro.chat.ticklechat.rest.ApiClient;
import com.techpro.chat.ticklechat.rest.ApiInterface;
import com.techpro.chat.ticklechat.utils.AppUtils;
import com.techpro.chat.ticklechat.utils.SharedPreferenceUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Sagar on 06/04/16.
 */
public class LoginActivity extends Activity{

    private static final String TAG = LoginActivity.class.getSimpleName();

    Button btnLogin,btn_signup;
    EditText et_username,et_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);

        btnLogin= (Button) findViewById(R.id.btnLogin);
        btn_signup= (Button) findViewById(R.id.btn_signup);
        et_username= (EditText) findViewById(R.id.et_username);
        et_pass= (EditText) findViewById(R.id.et_pass);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=et_username.getText().toString();
                String password=et_pass.getText().toString();
                if(username.trim().length()==0 || password.trim().length()==0){
                    Toast.makeText(getApplicationContext(),"Please enter all fields",Toast.LENGTH_LONG).show();
                }
                else {
                    callLoginService(username, password);
                }
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
            }
        });
    }

    private void showMsg(){

    }
    private void callLoginService(String username, String pass) {
        //Getting webservice instance which we need to call
        Call<UserModel> callForUserDetailsFromID = ApiClient.getClient().create(ApiInterface.class).loginUser(username, pass);
        //Calling Webservice by enqueue
        callForUserDetailsFromID.enqueue(new Callback<UserModel>() {

            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response != null && response.body() != null && response.body().getBody() != null && response.body().getMessage().equals("")) {
                    UserDetailsModel getUserDetails = response.body().getBody();
                    DataStorage.UserDetails = getUserDetails;
//                    if (DataStorage.UserDetails.getProfile_image()!=null) {
//                        byte[] decodedString = Base64.decode(DataStorage.UserDetails.getProfile_image(), Base64.DEFAULT);
//                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//                        if (decodedByte != null) {
//                            DataStorage.UserDetails.setProfile_image_bitmap(decodedByte);
//                        }
//                    }
                    Gson gson = new Gson();
                    String json = gson.toJson(getUserDetails);
                    Log.e(TAG, "json ==> " + json);

                    DataStorage.myAllUserlist = null;
                    DataStorage.chatUserList = null;
                    DataStorage.mygrouplist = null;
                    SharedPreferenceUtils.setValue(getApplicationContext(), SharedPreferenceUtils.LoginuserDetailsPreference, json);
                    SharedPreferenceUtils.setColleactionObject(getApplicationContext(), SharedPreferenceUtils.myuserlist,
                            DataStorage.myAllUserlist);
                    SharedPreferenceUtils.setColleactionObject(getApplicationContext(), SharedPreferenceUtils.mygrouplist,
                            DataStorage.mygrouplist);
                    SharedPreferenceUtils.setColleactionObject(getApplicationContext(), SharedPreferenceUtils.chatUserID,
                            DataStorage.chatUserList);
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                } else {
                    if (response != null && response.body() != null && response.body().getMessage() != null) {
                        Log.e(TAG, "response.body().getMessage() ==> " + response.body().toString());
                        Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                    } else if (response != null && response.message() != null) {
                        Log.e(TAG, "response.body().getMessage() ==> " + response.message());
                        Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.failmessage, Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Success but null response");
                    }
                }

            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                // Log error here since request failed
                Toast.makeText(getApplicationContext(), R.string.failmessage, Toast.LENGTH_LONG).show();
                Log.e(TAG, t.toString());
            }
        });
    }


}
