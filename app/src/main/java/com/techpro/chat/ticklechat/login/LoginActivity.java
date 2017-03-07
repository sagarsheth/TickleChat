package com.techpro.chat.ticklechat.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;
import com.techpro.chat.ticklechat.R;
import com.techpro.chat.ticklechat.Utils;
import com.techpro.chat.ticklechat.activity.home.HomeActivity;
import com.techpro.chat.ticklechat.databinding.ActivitySigninBinding;
import com.techpro.chat.ticklechat.models.DataStorage;
import com.techpro.chat.ticklechat.models.user.UserDetailsModel;
import com.techpro.chat.ticklechat.models.user.UserModel;
import com.techpro.chat.ticklechat.registration.RegistrationActivity;
import com.techpro.chat.ticklechat.rest.ApiClient;
import com.techpro.chat.ticklechat.rest.ApiInterface;
import com.techpro.chat.ticklechat.utils.AppUtils;
import com.techpro.chat.ticklechat.utils.SharedPreferenceUtils;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Sagar on 06/04/16.
 */
public class LoginActivity extends Activity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {


    private EditText et_phone,et_pass;
    private Button btnLogin,btnSignup;
    LinearLayout btn_google_signin,btn_fb_signin;
    private static final String TAG = "LoginActivity";
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);

        et_pass= (EditText) findViewById(R.id.et_username);
        et_phone= (EditText) findViewById(R.id.et_pass);
        btnLogin= (Button) findViewById(R.id.btnLogin);
        btnSignup= (Button) findViewById(R.id.btn_signup);
        btn_google_signin= (LinearLayout) findViewById(R.id.btn_google_signin);
        btn_fb_signin= (LinearLayout) findViewById(R.id.btn_fb_signin);

        btnLogin.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
        btn_google_signin.setOnClickListener(this);
        btn_fb_signin.setOnClickListener(this);


    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLogin:

                break;

            case R.id.btn_signup:
                startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
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
}
