package com.techpro.chat.ticklechat.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.google.gson.Gson;
import com.techpro.chat.ticklechat.Constants;
import com.techpro.chat.ticklechat.R;
import com.techpro.chat.ticklechat.activity.home.HomeActivity;
import com.techpro.chat.ticklechat.activity.registration.Login;
import com.techpro.chat.ticklechat.models.DataStorage;
import com.techpro.chat.ticklechat.models.user.UserDetailsModel;
import com.techpro.chat.ticklechat.utils.SharedPreferenceUtils;
import com.techpro.chat.ticklechat.utils.TickleSharedPrefrence;

public class SplashActivity extends AppCompatActivity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                String json = SharedPreferenceUtils.getValue(getApplicationContext(),SharedPreferenceUtils.LoginuserDetailsPreference,"");
                Log.e("onCreate","user ==> "+json);
                if (json.equals("")) {
                    Intent mainIntent = new Intent(SplashActivity.this,Login.class);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();
                } else {
                    Gson gson = new Gson();
                    UserDetailsModel obj = gson.fromJson(json, UserDetailsModel.class);
                    DataStorage.UserDetails = obj;
                    Intent mainIntent = new Intent(SplashActivity.this,HomeActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
