package com.techpro.chat.ticklechat;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDexApplication;

import lombok.Getter;
import lombok.Setter;
/**
 * Created by vishalrandive on 06/04/16.
 */
public class AppConfigs extends MultiDexApplication {

    // TODO::THESE ARE LIVE CREDENTIAL COMMENT THEM WHEN TESTING
    public static final String QB_APP_ID = "32169";
    public static final String QB_AUTH_KEY = "3rawjCmMObWAVpJ";
    public static final String QB_AUTH_SECRET = "JM3bf7pd9g7AGbg";

    // TODO::  THESE ARE STAGING CREDENTIALS UNCOMMENT THEM DURING DEVELOPMENT
    //    private static final String QB_AUTH_KEY = "tHHJUqhaaJUNF7R";
    //    private static final String QB_AUTH_SECRET = "zBhWY-STxNVefZs";
    //    private static final String QB_APP_ID = "25037";

    // TODO:: CHAT RELATED DETAILS

    public static int QB_USER_ID;
    public static int QB_OPPONENT_ID;
    public static int QB_NOTIFICATION_OPPONENT;
    public static final int DEFAULT_CALLER_ID = 0;

    public static final int ERROR_CALLER_ID = 0;

    public static String CHAT_IMG_MSG = "~*Image*~";
    public static final int DEFAULT_EVENT_ID = 101;

    public static final int PLACEHOLDER_LARGE = R.mipmap.ic_launcher;
    public static final int PLACEHOLDER_CIRCULAR = R.mipmap.ic_launcher;

    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;

    public static String KONOTOR_APP_ID = "c894653a-92a1-4366-8e3c-1391ad7c8c82";
    public static String KONOTOR_APP_KEY= "1c37cbf4-00ee-4078-b1c0-37ccbcc38953";


    //Konotor
//    public static String KONOTOR_APP_ID ="e6b9d653-f2fc-4cef-b2b8-921ae7018f53";
//    public static String KONOTOR_APP_KEY ="1c2a756d-5339-4051-9b01-5ed9bb864f4b";


    @Getter
    @Setter
    private static boolean isNavigationHeaderUpdated = false;
//
//    @Getter
//    @Setter
//    private static List categoryList;
//
//    @Getter
//    @Setter
//    private static List locationList;

    @Getter
    @Setter
    private static boolean isVerified;

    private static AppConfigs instance;

    @Override
    public void onCreate() {
        super.onCreate();

//        if (ValidationUtils.validateObject(categoryList))
//            categoryList.clear();

        instance = this;
    }

    public static AppConfigs getInstance() {
        return instance;
    }

    public int getAppVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
}
