package com.techpro.chat.ticklechat;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.onesignal.OneSignal;

/**
 * Created by vishalrandive on 20/04/16.
 */
public class TechProApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal.startInit(this).init();
    }
}