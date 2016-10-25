package com.techpro.chat.ticklechat.activity.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.onesignal.OneSignal;
import com.techpro.chat.ticklechat.AppConfigs;
import com.techpro.chat.ticklechat.Constants;
import com.techpro.chat.ticklechat.R;
import com.techpro.chat.ticklechat.activity.registration.Login;
import com.techpro.chat.ticklechat.activity.registration.PersonalProfileActivity;
import com.techpro.chat.ticklechat.controller.MessageController;
import com.techpro.chat.ticklechat.fragments.HomeScreenFragment;
import com.techpro.chat.ticklechat.fragments.NewGroupFragment;
import com.techpro.chat.ticklechat.fragments.ProfileFragment;
import com.techpro.chat.ticklechat.fragments.SearchTicklerFragment;
import com.techpro.chat.ticklechat.fragments.SentanceFragment;
import com.techpro.chat.ticklechat.fragments.SettingsFragment;
import com.techpro.chat.ticklechat.fragments.StatusUpdateFragment;
import com.techpro.chat.ticklechat.fragments.TickleFriendFragment;
import com.techpro.chat.ticklechat.listeners.FragmentChangeCallback;
import com.techpro.chat.ticklechat.models.DataStorage;
import com.techpro.chat.ticklechat.models.MenuItems;
import com.techpro.chat.ticklechat.models.message.AllMessages;
import com.techpro.chat.ticklechat.models.message.Tickles;
import com.techpro.chat.ticklechat.models.user.GetUserDetails;
import com.techpro.chat.ticklechat.models.user.GetUserDetailsBody;
import com.techpro.chat.ticklechat.models.user.User;
import com.techpro.chat.ticklechat.models.user.UserDetailsModel;
import com.techpro.chat.ticklechat.rest.ApiClient;
import com.techpro.chat.ticklechat.rest.ApiInterface;
import com.techpro.chat.ticklechat.utils.AppUtils;
import com.techpro.chat.ticklechat.utils.FragmentUtils;
import com.techpro.chat.ticklechat.utils.SharedPreferenceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vishalrandive on 06/04/16.
 */
public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentChangeCallback {

    private NavigationView mNavigation;
    protected Toolbar mToolbar;
    protected DrawerLayout mDrawerLayout;
    private FrameLayout mContainer;
    private static final String TAG = Login.class.getSimpleName();
    private ProgressDialog dialog;

    public static final String KEY_TITLE = "title";
    private ApiInterface apiService;
    private ApiInterface apiAUTService;
    List<String> id = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_home);
        Display display = this.getWindowManager().getDefaultDisplay();

        AppConfigs.SCREEN_HEIGHT = display.getHeight();
        AppConfigs.SCREEN_WIDTH = display.getWidth();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mNavigation = (NavigationView) findViewById(R.id.navigation);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mContainer = (FrameLayout) findViewById(R.id.container);
        mNavigation.setNavigationItemSelectedListener(this);

//        initDefaultFragmentView();

        setUpHeaderLayout(mNavigation);
        initSlidingDrawer();
        initOneSignal();

        DataStorage.myuserlist = (List<User>) SharedPreferenceUtils.getColleactionObject(getApplicationContext(),SharedPreferenceUtils.myuserlist);
        DataStorage.mymessagelist = (HashMap<User, List<Tickles.MessageList.ChatMessagesTicklesList>>)
                SharedPreferenceUtils.getColleactionObject(getApplicationContext(),SharedPreferenceUtils.mymessagelist);

        if (DataStorage.mymessagelist == null || DataStorage.myuserlist == null) {
            Log.e("ssssssssssssss","mymessagelist & myuserlist ==> null");
            dialog = ProgressDialog.show(HomeActivity.this, "Loading", "Please wait...", true);
            apiService = ApiClient.getClient().create(ApiInterface.class);
            callGetUserDetailsService(Integer.parseInt(DataStorage.UserDetails.getId()), true);
            apiAUTService = ApiClient.createServiceWithAuth(DataStorage.UserDetails.getId()).create(ApiInterface.class);
            callMessage_ALL_Service();
            callTickles_Service();
        } else {
            Log.e("ssssssssssssss","mymessagelist & myuserlist ==> not null");
            Fragment fragment = new HomeScreenFragment();
            replaceFragment(fragment, getResources().getString(R.string.header_ticklers), false);
        }

//        TODO Vishal to call below method to get 5 messages form preloaded DB
        Log.e("ssssssssssssss","==> "+new MessageController(getApplicationContext()).getMessages());

    }

    ArrayList<MenuItems> objMenuItemsList = new ArrayList<>();

    void initSlidingDrawer() {

        final int TICKLE_A_FRIEND = 0;
        final int NEW_GROUP = 1;
        final int SEARCH_TICKLER = 2;
        final int PROFILE = 3;
        final int STATUS_UPDATE = 4;
        final int SENTENCE = 5;
        final int SETTING = 6;

        String menuItmesArray[] = {
                getResources().getString(R.string.menu_tickle_a_friend),
                getResources().getString(R.string.menu_new_group),
                getResources().getString(R.string.menu_search_tickler),
                getResources().getString(R.string.menu_profile),
                getResources().getString(R.string.menu_status_update),
                getResources().getString(R.string.menu_add_sentence),
                getResources().getString(R.string.menu_setting)
        };

        String menuItmesSubtitlesArray[] = {
                getResources().getString(R.string.menu_subtitle_1),
                getResources().getString(R.string.menu_subtitle_2),
                getResources().getString(R.string.menu_subtitle_3),
                getResources().getString(R.string.menu_subtitle_4),
                getResources().getString(R.string.menu_subtitle_5),
                getResources().getString(R.string.menu_subtitle_6),
                getResources().getString(R.string.menu_subtitle_7)
        };

        int menuItmesDrawableArray[] = {
                R.drawable.ic_launcher,
                R.drawable.ic_launcher,
                R.drawable.ic_launcher,
                R.drawable.ic_launcher,
                R.drawable.ic_launcher,
                R.drawable.ic_launcher,
                R.drawable.ic_launcher

        };


        for (int i = 0; i < menuItmesArray.length; i++) {
            MenuItems objMenuItems = new MenuItems();
            objMenuItems.setMenuItemName(menuItmesArray[i]);
            objMenuItems.setMenuItemDrawable(menuItmesDrawableArray[i]);
            objMenuItems.setMenuItemSubTitles(menuItmesSubtitlesArray[i]);
//            objMenuItems.setMenuItemDrawable(iconName[i]);
            objMenuItems.setMenuItemAlert("");

            objMenuItemsList.add(objMenuItems);
        }

        ListView lst_menu_items = (ListView) findViewById(R.id.lst_menu_items);
        MenuAdapter objMenuAdapter = new MenuAdapter(this, R.layout.layout_menu_main, objMenuItemsList);

        lst_menu_items.setAdapter(objMenuAdapter);

        lst_menu_items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "CLICKED ON POSITION " + position, Toast.LENGTH_SHORT).show();

                if (!AppUtils.isNetworkConnectionAvailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                mDrawerLayout.closeDrawers();
                Fragment fragment;
                switch (position) {
                    case TICKLE_A_FRIEND:
                        fragment = new TickleFriendFragment();
                        replaceFragment(fragment, getResources().getString(R.string.header_ticklers), true);
                        return;
                    case NEW_GROUP:
                        fragment = new NewGroupFragment();
                        replaceFragment(fragment, getResources().getString(R.string.menu_new_group), true);

                        return;
                    case SEARCH_TICKLER:
                        fragment = new SearchTicklerFragment();
                        replaceFragment(fragment, getResources().getString(R.string.menu_search_tickler), true);
                        return;
                    case PROFILE:
                        fragment = new ProfileFragment();
                        replaceFragment(fragment, getResources().getString(R.string.menu_profile), true);
                        return;
                    case STATUS_UPDATE:
                        fragment = new StatusUpdateFragment();
                        replaceFragment(fragment, getResources().getString(R.string.menu_status_update), true);
                        return;
                    case SENTENCE:
                        fragment = new SentanceFragment();
                        replaceFragment(fragment, getResources().getString(R.string.menu_add_sentence), true);
                        return;
                    case SETTING:
                        fragment = new SettingsFragment();
                        replaceFragment(fragment, getResources().getString(R.string.menu_setting), true);
                        return;
                    default:
                        break;
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);


            }
        });
    }


    @Override
    public void setContentView(int layoutResID) {
        getLayoutInflater().inflate(layoutResID, mContainer);
    }

    private void setUpHeaderLayout(NavigationView navigationView) {
//        View headerView = navigationView.inflateHeaderView(R.layout.layout_nav_header_main);
        TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvUserName.setText(DataStorage.UserDetails.getName());
        tvUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileView();
            }
        });

        TextView location = (TextView) findViewById(R.id.tv_location);
        location.setText("Mumbai");

        TextView editProfile = (TextView) findViewById(R.id.tv_edit_profile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileView();
            }
        });

        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        ivProfileImage.setVisibility(View.GONE);
        ImageView ivEditIcon = (ImageView) findViewById(R.id.ivEditIcon);
        ivEditIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileView();
            }
        });


        CircularImageView objCircularImageView = (CircularImageView) findViewById(R.id.ivProfileImg);


    }

    //
    private void showEditProfileView() {
        if (AppConfigs.isVerified()) {
            Intent intent = new Intent(HomeActivity.this, PersonalProfileActivity.class);
            startActivityForResult(intent, Constants.REQUEST_CODE_1003);
        } else {
            ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
        }
    }


    /*
        * Get - User details by user id
        * @param userId - user id
        * */
    private void callGetUserDetailsService(int userId, final boolean iscurrentuser) {
        //Getting webservice instance which we need to call
        Call<GetUserDetails> callForUserDetailsFromID = apiService.getUserDetailsFromID(userId);
        //Calling Webservice by enqueue
        callForUserDetailsFromID.enqueue(new Callback<GetUserDetails>() {
            @Override
            public void onResponse(Call<GetUserDetails> call, Response<GetUserDetails> response) {
                if (response != null) {
                    GetUserDetailsBody getUserDetails = response.body().getBody();
                    if (iscurrentuser) {
                        DataStorage.userDetailsBody = getUserDetails;
                    } else {
                        User usr = getUserDetails.getUser();
                        DataStorage.myuserlist.add(usr);

                        List<Tickles.MessageList.ChatMessagesTicklesList> messages = new ArrayList<Tickles.MessageList.ChatMessagesTicklesList>();
                        for (int i = 0; i < DataStorage.tickles.getTickles().size(); i++) {
                            Tickles.MessageList.ChatMessagesTicklesList msg = DataStorage.tickles.getTickles().get(i);
                            if(usr.getId().equals(msg.getUserid())) {
                                Calendar cl = Calendar.getInstance();
                                cl.setTimeInMillis(Long.parseLong(msg.getRequested_at()));  //here your time in miliseconds
                                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
                                String datenew = format.format(cl.getTime());
                                Log.e(TAG,"datenew ======> "+datenew);
                                msg.setRequested_at(datenew);
                                messages.add(msg);
                            }
                        }
                        Collections.sort(messages, new Comparator<Tickles.MessageList.ChatMessagesTicklesList>() {
                            @Override
                            public int compare(Tickles.MessageList.ChatMessagesTicklesList s1, Tickles.MessageList.ChatMessagesTicklesList s2) {
                                return s1.getRequested_at().compareToIgnoreCase(s1.getRequested_at());
                            }
                        });
                        Log.e(TAG,"messages ==> "+messages);
                        DataStorage.mymessagelist.put(usr,messages);
                        if (id.size() == DataStorage.myuserlist.size()) {
                            dialog.dismiss();

                            SharedPreferenceUtils.setColleactionObject(getApplicationContext(),SharedPreferenceUtils.myuserlist,DataStorage.myuserlist);
                            SharedPreferenceUtils.setColleactionObject(getApplicationContext(),SharedPreferenceUtils.mymessagelist,DataStorage.mymessagelist);

                            Fragment fragment = new HomeScreenFragment();
                            replaceFragment(fragment, getResources().getString(R.string.header_ticklers), false);


                        }
                    }
                } else {
                    Log.e(TAG, "Success but null response");
                }
            }

            @Override
            public void onFailure(Call<GetUserDetails> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
//                dialog.dismiss();
            }
        });

    }

    /*
* Get - User details by user id
* @param userId - user id
* */
    private void callMessage_ALL_Service() {
        //Getting webservice instance which we need to call
        Call<AllMessages> callForUserDetailsFromID = apiAUTService.getAllMessageList();
        //Calling Webservice by enqueue
        callForUserDetailsFromID.enqueue(new Callback<AllMessages>() {
            @Override
            public void onResponse(Call<AllMessages> call, Response<AllMessages> response) {
                if (response != null) {
                    DataStorage.allMessages = response.body().getBody();
                    Log.e(TAG, "Success  callMessage_ALL_Service response.getMessages: " + response.body().getBody().getMessages().get(0).getMessage());
                    Log.e(TAG, "Success  callMessage_ALL_Service response.getTickles: " + response.body().getBody().getTickles().get(0).getMessage());
//                    GetUserDetailsBody getUserDetails = response.body().getBody();
//                    DataStorage.userDetailsBody = getUserDetails;
//                    Log.e(TAG, "Success  callUserListService : " + getUserDetails);
                } else {
                    Log.e(TAG, "Success callMessage_ALL_Service but null response");
                }
            }

            @Override
            public void onFailure(Call<AllMessages> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
//                dialog.dismiss();
            }
        });

    }

    /*
* Get - User details by user id
* @param userId - user id
* */
    private void callTickles_Service() {
        //Getting webservice instance which we need to call
        Call<Tickles> callForUserDetailsFromID = apiAUTService.getTickles();
        //Calling Webservice by enqueue
        callForUserDetailsFromID.enqueue(new Callback<Tickles>() {
            @Override
            public void onResponse(Call<Tickles> call, Response<Tickles> response) {
                if (response != null) {
                    DataStorage.tickles = response.body().getBody();
                    Log.e(TAG, "Success  callTickles_Service response.getTickles: " + response.body().getBody().getTickles().get(0).getMessage());
//                    GetUserDetailsBody getUserDetails = response.body().getBody();
//                    DataStorage.userDetailsBody = getUserDetails;
//                    Log.e(TAG, "Success  callUserListService : " + getUserDetails);
                    DataStorage.mymessagelist = new HashMap<User, List<Tickles.MessageList.ChatMessagesTicklesList>>();
                    DataStorage.myuserlist = new ArrayList<User>();
                    id = new ArrayList<String>();
                    id.clear();
                    for (int i = 0; i < DataStorage.tickles.getTickles().size(); i++) {
                        Tickles.MessageList.ChatMessagesTicklesList msg = DataStorage.tickles.getTickles().get(i);
                        if(!id.contains(msg.getUserid())) {
                            if(msg.getUserid() != null && msg.getRequested_at() != null) {
                                Log.e(TAG, "Success  msg.getUserid() not null: " + msg.getUserid());
                                id.add(msg.getUserid());
                                callGetUserDetailsService(Integer.parseInt(msg.getUserid()), false);
                            }
                        }

                    }
                } else {
                    Log.e(TAG, "Success callTickles_Service but null response");
                }
            }

            @Override
            public void onFailure(Call<Tickles> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
//                dialog.dismiss();
            }
        });

    }
    private void replaceFragment(Fragment fragment, CharSequence title, boolean addToBackstack) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE, title.toString());

        fragment.setArguments(bundle);
        FragmentUtils.replaceFragment(this, fragment, R.id.container, addToBackstack);

        setTitle(title.toString());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onReplaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(fragment, getString(R.string.app_name), addToBackStack);
    }

    @Override
    public void onReplaceFragment(Fragment fragment) {
        replaceFragment(fragment, getString(R.string.app_name), false);
    }

    @Override
    public void onAddFragment(Fragment fragment) {
        //TODO:: Do Nothing
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    void initOneSignal() {
        OneSignal.enableNotificationsWhenActive(true);
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                Log.d("debug", "User:" + userId);
                if (registrationId != null)
                    Log.d("debug", "registrationId:" + registrationId);

                OneSignal.setEmail("V@V.com");


            }
        });
    }
}