package com.techpro.chat.ticklechat.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.techpro.chat.ticklechat.R;
import com.techpro.chat.ticklechat.Utils;
import com.techpro.chat.ticklechat.models.user.UserDetailsModel;
import com.techpro.chat.ticklechat.models.user.UserModel;
import com.techpro.chat.ticklechat.registration.RegistrationActivity;
import com.techpro.chat.ticklechat.rest.ApiClient;
import com.techpro.chat.ticklechat.rest.ApiInterface;
import com.techpro.chat.ticklechat.utils.AppUtils;

import org.json.JSONObject;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Sagar on 06/04/16.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "LoginActivity";

    private EditText et_phone, et_pass;
    private Button btnLogin, btnSignup;
    LinearLayout btn_google_signin, btn_fb_signin;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    private static final int RC_SIGN_IN = 9001;

    UserDetailsModel user = new UserDetailsModel();

    private boolean isFbUserLoggedIn;

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

        changeStatusBarColor();

        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
// options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

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
                clickedOnGoogleSignin();
                break;

            case R.id.btn_fb_signin:
                initFb();
                break;
        }
    }

    private void initFb(){
        CallbackManager callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        isFbUserLoggedIn = true;
                        final AccessToken accessToken = loginResult.getAccessToken();
                        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject JSONUser, GraphResponse graphResponse) {
                                        if (graphResponse.getError() != null) {
                                            //showErrorDialog(graphResponse.getError().toString());
                                        } else {
                                            /*Get User Data*/
                                        }
                                    }

                                }

                        );
                        Bundle parameters = new Bundle();
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d("LOG", exception.toString());
                    }
                });
        LoginManager.getInstance().logInWithReadPermissions((this), Arrays.asList("public_profile"));
    }
    // SignIn Method for Google.
    private void clickedOnGoogleSignin() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    //Handle Google SignIn Results Here.
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            user.setName(acct.getDisplayName());
            user.setEmail(acct.getEmail());
            user.setProfile_image((acct.getPhotoUrl() != null) ? acct.getPhotoUrl().getPath() : "");
            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    // [START signOut]
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END signOut]

    //[START update UI]
    private void updateUI(boolean signedIn) {
        // TODO: 09/11/16 RESULT SUCCESS - boolean true Start with home activity

    }
    //[END update UI]
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

    public void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        /**
         * View pager adapter
         */
    }
        public class MyViewPagerAdapter extends PagerAdapter {
            private LayoutInflater layoutInflater;

            public MyViewPagerAdapter() {
            }

            @Override
            public int getCount() {
                return 0;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return false;
            }

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
