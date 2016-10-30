package com.techpro.chat.ticklechat.activity.registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.techpro.chat.ticklechat.R;
import com.techpro.chat.ticklechat.activity.home.HomeActivity;
import com.techpro.chat.ticklechat.listeners.GenericListener;
import com.techpro.chat.ticklechat.models.DataStorage;
import com.techpro.chat.ticklechat.models.user.UserDetailsModel;
import com.techpro.chat.ticklechat.models.user.UserModel;
import com.techpro.chat.ticklechat.rest.ApiClient;
import com.techpro.chat.ticklechat.rest.ApiInterface;
import com.techpro.chat.ticklechat.utils.SharedPreferenceUtils;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vishalrandive on 06/04/16.
 */
public class Login extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog dialog;
    private static final String TAG = Login.class.getSimpleName();
    private ApiInterface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager_signup);
        // Add code to print out the key hash
        CirclePageIndicator titlePageIndicator = (CirclePageIndicator) findViewById(R.id.pageIndicator);
        viewPager.setAdapter(new SignupPagerAdapter(getSupportFragmentManager()));
        titlePageIndicator.setViewPager(viewPager);

        apiService = ApiClient.getClient().create(ApiInterface.class);

        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.btnSignup).setOnClickListener(this);
//        findViewById(R.chatUserID.btnGoogle).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnLogin:
//                break;
            case R.id.btnSignup:

//                TODO create Dialog to ask phone and pass and use SHA1 for pass
                dialog = ProgressDialog.show(Login.this, "Loading", "Please wait...", true);
                callLoginService("8652355351", "2233c15a7f3371fc6e6a8afeb5089b5411db19a1");

//                LoginModel.Data objData = new LoginModel.Data();
//                objData.setEmail("a@a.com");
//                objData.setName("asgfag");
//                objData.setId("12434");
//                objData.setPhoto_uri(null);
//                getInstance().setData(objData);
//                startActivity(new Intent(Login.this, HomeActivity.class));
//                TODO uncomment below
//                Intent mainIntent = new Intent(Login.this,LoginActivity.class);
//                Login.this.startActivity(mainIntent);
                break;

        }
    }

    class SignupPagerAdapter extends FragmentPagerAdapter {

        private int ITEMS = 8;

        public SignupPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            return getFragment(R.layout.fragment_layout_signup, "Page1", position, ITEMS);
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

    public SignupPagerFragment getFragment(int page, String title, final int position, final int size) {

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


    private void callLoginService(String username, String pass) {
        //Getting webservice instance which we need to call
        Call<UserModel> callForUserDetailsFromID = apiService.loginUser(username,pass);
        //Calling Webservice by enqueue
        callForUserDetailsFromID.enqueue(new Callback<UserModel>() {

            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response != null) {
                    UserDetailsModel getUserDetails = response.body().getBody();
                    DataStorage.UserDetails = getUserDetails;
                    Log.e(TAG, "Success  callLoginService : " + getUserDetails);
                    Log.e(TAG, "Success  getUserDetails.getId() : " + getUserDetails.getId());
                    Gson gson = new Gson();
                    String json = gson.toJson(getUserDetails);
                    SharedPreferenceUtils.setValue(getApplicationContext(),SharedPreferenceUtils.LoginuserDetailsPreference,json);
                    Log.e("onResponse","getUserDetails ==> "+json);
                    startActivity(new Intent(Login.this, HomeActivity.class));
                    finish();
                } else {
                    Log.e(TAG, "Success but null response");
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
//                dialog.dismiss();
            }
        });
    }


// For Password
    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        StringBuilder buf = new StringBuilder();
        for (byte b : sha1hash) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }
}
