package com.techpro.chat.ticklechat.activity.registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.techpro.chat.ticklechat.R;
import com.techpro.chat.ticklechat.activity.home.HomeActivity;
import com.techpro.chat.ticklechat.listeners.GenericListener;
import com.techpro.chat.ticklechat.models.LoginModel;
import com.techpro.chat.ticklechat.models.user.GetUserDetails;
import com.techpro.chat.ticklechat.models.user.GetUserDetailsBody;
import com.techpro.chat.ticklechat.rest.ApiClient;
import com.techpro.chat.ticklechat.rest.ApiInterface;
import com.techpro.chat.ticklechat.utils.AppUtils;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vishalrandive on 06/04/16.
 */
public class Login extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    LoginButton fb_button_new;
    CallbackManager callbackManager;
    GoogleApiClient mGoogleApiClient;
    private ProgressDialog dialog;
    private static final String TAG = Login.class.getSimpleName();
    private ApiInterface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login);

        apiService = ApiClient.getClient().create(ApiInterface.class);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager_signup);
        dialog = ProgressDialog.show(Login.this, "Loading", "Please wait...", true);

        callGetUserDetailsService(520);
        // Add code to print out the key hash
        CirclePageIndicator titlePageIndicator = (CirclePageIndicator) findViewById(R.id.pageIndicator);
        viewPager.setAdapter(new SignupPagerAdapter(getSupportFragmentManager()));
        titlePageIndicator.setViewPager(viewPager);


        findViewById(R.id.btnSignup).setOnClickListener(this);


        fb_button_new = (LoginButton) findViewById(R.id.login_button);

        fb_button_new.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));

        callbackManager = CallbackManager.Factory.create();


        fb_button_new.setBackgroundResource(R.drawable.drawable_big_rounded_background);
        fb_button_new.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        fb_button_new.setCompoundDrawablePadding(0);
        fb_button_new.setPadding((int) getResources().getDimension(R.dimen.dimen_10),
                (int) getResources().getDimension(R.dimen.dimen_10),
                (int) getResources().getDimension(R.dimen.dimen_10),
                (int) getResources().getDimension(R.dimen.dimen_10));

        ((GradientDrawable) fb_button_new.getBackground()).setColor(getResources().getColor(R.color.fb_blue));
        fb_button_new.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);


        // Callback registration
        fb_button_new.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                AppUtils.showToast("LOGGED IN SUCCESS", getApplicationContext());

                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                AppUtils.showLog("LoginActivity " + response.toString());

                                // Application code
                                try {
                                    Profile profile = Profile.getCurrentProfile();

                                    String email = object.getString("email");

                                    LoginModel.Data objData = new LoginModel.Data();
                                    objData.setEmail(email);
                                    objData.setName(profile.getFirstName());
                                    objData.setId(profile.getId());
                                    objData.setPhoto_uri(profile.getProfilePictureUri(400, 400));


                                    getInstance().setData(objData);


                                    String firstName = profile.getFirstName();

                                    AppUtils.showLog("firstName " + firstName);
//                                    System.out.println(profile.getProfilePictureUri(20, 20));
                                    System.out.println(profile.getLinkUri());

                                    String birthday = object.getString("birthday"); // 01/31/1980 format

                                    AppUtils.showLog("firstName " + firstName + ", " + email + ", " + birthday);


                                    startActivity(new Intent(Login.this, HomeActivity.class));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();


            }

            @Override
            public void onCancel() {
                // App code
                AppUtils.showToast("LOGIN CANCEl", getApplicationContext());
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                AppUtils.showToast("LOGIN TOKEN ERROR", getApplicationContext());
                startActivity(new Intent(Login.this, HomeActivity.class));
            }
        });


        SignInButton btnGoogle = (SignInButton) findViewById(R.id.btnGoogle);
        btnGoogle.setOnClickListener(this);

//        btnGoogle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);


        findViewById(R.id.btnGoogle).setOnClickListener(this);


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

        btnGoogle.setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnGoogle:
                signIn();
                break;
            case R.id.btnSignup:
                LoginModel.Data objData = new LoginModel.Data();
                objData.setEmail("a@a.com");
                objData.setName("asgfag");
                objData.setId("12434");
                objData.setPhoto_uri(null);
                getInstance().setData(objData);
                startActivity(new Intent(Login.this, HomeActivity.class));
//                TODO uncomment below
//                Intent mainIntent = new Intent(Login.this,LoginActivity.class);
//                Login.this.startActivity(mainIntent);
                break;

        }
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    class SignupPagerAdapter extends FragmentPagerAdapter {

        private int ITEMS = 8;

        public SignupPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position)
        {

//            switch (position) {
//                case 0:
//                    return getFragment(R.layout.fragment_layout_signup, "Page1", 0, 3);
//                case 1:
//                    return getFragment(R.layout.fragment_layout_signup, "Page2", 1, 3);
//                case 2:
//                    return getFragment(R.layout.fragment_layout_signup, "Page3", 2, 3);
//                default:
                    return getFragment(R.layout.fragment_layout_signup, "Page1", position, ITEMS);
//            }

        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

        @Override
        public int getCount() {

            return ITEMS;
        }
    }

    SignupPagerFragment getFragment(int page, String title, final int position, final int size) {

        SignupPagerFragment fragment = new SignupPagerFragment();
        fragment.addListener(new GenericListener<Boolean>() {
            @Override
            public void onResponse(int callerID, Boolean messages) {
                if (messages != null) {
                    if (messages) {
                        // setSelection(1);
                    }
                }
            }
        });
        Bundle args = new Bundle();
        args.putInt("layout_id", page);
        args.putString("layout_title", title);
        args.putInt("position", position);
        args.putInt("size", size);


        fragment.setArguments(args);

        return fragment;
    }
    private int RC_SIGN_IN =100;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        else
            callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    private void handleSignInResult(GoogleSignInResult result) {
        AppUtils.showLog("handleSignInResult:" + result.isSuccess());
//        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            LoginModel.Data objData = new LoginModel.Data();
//            objData.setEmail(acct.getEmail());
//            objData.setName(acct.getDisplayName());
//            objData.setId(acct.getId());
//
//            objData.setPhoto_uri(acct.getPhotoUrl());

            objData.setEmail("a@a.com");
            objData.setName("sfagga");
            objData.setId("12434");

            objData.setPhoto_uri(null);
            getInstance().setData(objData);

//            String personName = acct.getDisplayName();
//            String personEmail = acct.getEmail();
//            String personId = acct.getId();
//            Uri personPhoto = acct.getPhotoUrl();

//            mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));

            startActivity(new Intent(Login.this, HomeActivity.class));
//        } else {
//            // Signed out, show unauthenticated UI.
//            AppUtils.showLog("google logout : true");
//
//        }
    }


    private  static LoginModel objLoginModel;
    public static LoginModel getInstance()
    {
        if(objLoginModel==null)
            objLoginModel = new LoginModel();


        return objLoginModel;
    }


    /*
    * Get - User details by user id
    * @param userId - user id
    * */
    private void callGetUserDetailsService(int userId) {
        //Getting webservice instance which we need to call
        Call<GetUserDetails> callForUserDetailsFromID = apiService.getUserDetailsFromID(userId);
        //Calling Webservice by enqueue
        callForUserDetailsFromID.enqueue(new Callback<GetUserDetails>() {
            @Override
            public void onResponse(Call<GetUserDetails> call, Response<GetUserDetails> response) {
                if (response != null) {
                    GetUserDetailsBody getUserDetails = response.body().getBody();
                    Log.e(TAG, "Success  callGetUserDetailsService : " + getUserDetails);
                } else {
                    Log.e(TAG, "Success but null response");
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<GetUserDetails> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                dialog.dismiss();
            }
        });

    }

}
