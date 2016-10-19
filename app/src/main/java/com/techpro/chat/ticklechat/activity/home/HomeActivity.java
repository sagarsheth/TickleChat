package com.techpro.chat.ticklechat.activity.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.mikhaellopez.circularimageview.CircularImageView;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;
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
import com.techpro.chat.ticklechat.models.MenuItems;
import com.techpro.chat.ticklechat.retrofit.PlayServicesHelper;
import com.techpro.chat.ticklechat.utils.AppUtils;
import com.techpro.chat.ticklechat.utils.FragmentUtils;
import com.techpro.chat.ticklechat.utils.TickleSharedPrefrence;

import java.util.ArrayList;

/**
 * Created by vishalrandive on 06/04/16.
 */
public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentChangeCallback {

    private NavigationView mNavigation;
    protected Toolbar mToolbar;
    protected DrawerLayout mDrawerLayout;
    private FrameLayout mContainer;

    private PlayServicesHelper objPlayServicesHelper;
    TickleSharedPrefrence sp;
    public static final String KEY_TITLE = "title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_home);
        sp = TickleSharedPrefrence.getInstance(getApplicationContext());
        sp.addToSharedPreference(Constants.SHAREDPREF_LOGIN_SUCCESS, "true");
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
        Fragment fragment = new HomeScreenFragment();
        replaceFragment(fragment, getResources().getString(R.string.header_ticklers), false);

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
    protected void onResume() {
        super.onResume();

        //updating the navigation header when the service name or address is changed
        if (AppConfigs.isNavigationHeaderUpdated()) {
//            updateHeaderView();
            AppConfigs.setNavigationHeaderUpdated(false);
        }

    }

    @Override
    public void setContentView(int layoutResID) {
        getLayoutInflater().inflate(layoutResID, mContainer);
    }

    //
//    private void initDefaultFragmentView() {
//        Fragment fragment = new DashboardFragment();
//        replaceFragment(fragment, getResources().etString(R.string.dashboard), true);
//    }
//
    private void setUpHeaderLayout(NavigationView navigationView) {
//        View headerView = navigationView.inflateHeaderView(R.layout.layout_nav_header_main);
        TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvUserName.setText(Login.getInstance().getData().getName());
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

        AppUtils.showLog("url " + Login.getInstance().getData().getPhoto_uri() + ", " + Login.getInstance().getData().getName());

//        Picasso.with(HomeActivity.this)
//                .load(Login.getInstance().getData().getPhoto_uri())
//                .placeholder(R.drawable.camera_icon)
//                .error(R.drawable.camera_icon)
//                .into(ivProfileImage);

        Picasso.with(HomeActivity.this)
                .load(Login.getInstance().getData().getPhoto_uri())
                .placeholder(R.drawable.camera_icon)
                .error(R.drawable.camera_icon)
                .into(objCircularImageView);

//        Picasso.with(this).load(Login.getInstance().getData().getPhoto_uri()).into(ivEditIcon);

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

    //
//    private void updateHeaderView() {
//        ((TextView) findViewById(R.id.tv_service_name)).setText(LocalStorage.getInstance(this).getServiceName());
//        ((TextView) findViewById(R.id.tv_location)).setText(LocalStorage.getInstance(this).getAddress());
//    }
//
//    public static String [] KEY_LAST_RECORDS ={"CallsCount", /*"RecommendationCount",*/ "ReviewsCount", "ViewsCount"};
//
//    @Override
//    public void onBackPressed() {
//        Fragment currFragment = FragmentUtils.getCurrentVisibleFragment(this);
//
//        if (currFragment instanceof DashboardFragment) {
//            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//            if (drawer.isDrawerOpen(GravityCompat.START)) {
//                drawer.closeDrawer(GravityCompat.START);
//            } else {
//
//                /** save last records of count to get an idea of new records count **/
//
//                String DB_NAME = LocalStorage.getInstance(this).getMeta().getManager_id()+"SAVE_LAST_RECORDS";
//                SharedPreferences sharedpreferences = getSharedPreferences(DB_NAME, Context.MODE_PRIVATE);
//
//                //SharedPreferences sharedPref = getPreferences(getResources().getString(R.string.save_last_records),Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedpreferences.edit();
//
//
//                for (int i = 0; i < DashboardFragment.lastRecords.size();i++)
//                {
//                    int count =0;
//                    try{
//                        if(!TextUtils.isEmpty(DashboardFragment.lastRecords.get(i))){
//                            count = Integer.parseInt(DashboardFragment.lastRecords.get(i));
//                        }
//                    }catch (Exception e){e.printStackTrace();}
//
//                    editor.putInt(KEY_LAST_RECORDS[i], count);
//                }
//
//                editor.commit();
//
//                this.finish();
//            }
//        }else{
//            if (AppConfigs.isVerified()) {
//                Fragment fragment = new DashboardFragment();
//                replaceFragment(fragment, getResources().getString(R.string.dashboard),true);
//            }
//        }
//    }
//
//    @Override
//    public boolean onNavigationItemSelected(MenuItem menuItem) {
//
//        if (!CheckNetworkConnection.isConnectionAvailable(getApplicationContext())) {
//            Toast.makeText(getApplicationContext(),
//                    getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        mDrawerLayout.closeDrawers();
//        Fragment fragment = new NotVerifiedFragment();
//        switch (menuItem.getItemId()) {
//            case R.id.stats:
//                if (AppConfigs.isVerified()) {
//                    fragment = new DashboardFragment();
//                }
//                replaceFragment(fragment, getResources().getString(R.string.dashboard), true);
//
//                return true;
//            case R.id.chat:
//
//                if (AppConfigs.isVerified()) {
////                    fragment = new ChatFragment();
//                    fragment = new ChatFragmentNew();
//                }
//                replaceFragment(fragment, menuItem.getTitle(), true);
//
//                return true;
//            case R.id.invoices:
//                if (AppConfigs.isVerified()) {
//                    fragment = new InvoiceFragment();
//                }
//                replaceFragment(fragment, menuItem.getTitle(), true);
//                return true;
//            case R.id.reviews:
//                if (AppConfigs.isVerified()) {
//                    fragment = new ReviewsFragmentNew();
//                }
//                replaceFragment(fragment, menuItem.getTitle(), true);
//                return true;
//            case R.id.contact_us:
//                fragment = new ContactUsFragment();
//                replaceFragment(fragment, menuItem.getTitle(), true);
//                return true;
//            case R.id.logout:
//                AlertDialogHelper.showActionConfirmationAlertDialog(this,
//                        getString(R.string.logout),
//                        getString(R.string.logout_warning),
//                        logoutConfirmClickListener);
//                return true;
//            default:
//                break;
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
//
    private void replaceFragment(Fragment fragment, CharSequence title, boolean addToBackstack) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE, title.toString());

        fragment.setArguments(bundle);
        FragmentUtils.replaceFragment(this, fragment, R.id.container, addToBackstack);

        setTitle(title.toString());
    }

    //
//    DialogInterface.OnClickListener logoutConfirmClickListener = new DialogInterface.OnClickListener() {
//        @Override
//        public void onClick(DialogInterface dialog, int which) {
//            if (LocalStorage.getInstance(HomeSliderActivity.this).logout()) {
//                Toast.makeText(getApplicationContext(), getString(R.string.logout_success), Toast.LENGTH_LONG).show();
//                HomeSliderActivity.this.finish();
//                Intent intent = new Intent(HomeSliderActivity.this, SplashScreenActivity.class);
//                startActivity(intent);
//            } else {
//                Toast.makeText(getApplicationContext(), getString(R.string.logout_failed), Toast.LENGTH_LONG).show();
//            }
//        }
//    };
//
    public void startNewActivity(Context context, Class classObj) {
        Intent intent = new Intent(context, classObj);
        startActivity(intent);
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

//                App Version - Number
//                Lifestage - ID
//                Locality - ID
//                Gender - String
//                BabyGender - String
//                Area - String
//                UId - ID
//                Name - String

            }
        });
    }


}

//
//        //starting the gcm service to fetch the token
//        if (ValidationUtils.validateObject(LocalStorage.getInstance(this).getGcmToken())) {
//            startService(new Intent(this, GCMIntentService.class));
//        }
//
//        // Logging in Quick block account for initiating Chat
//        String userName = LocalStorage.getInstance(this).getMeta().getManager_email();
//        String password = LocalStorage.getInstance(this).getMeta().getManager_id() + "quick1234";
//


/**
 * GCM subscription for push notifications
 */
//        objPlayServicesHelper = new PlayServicesHelper(HomeSliderActivity.this);
//        objPlayServicesHelper.registerGcm(new GenericListener<String>() {
//            @Override
//            public void onResponse(int callerID, String messages) {
//                if (!TextUtils.isEmpty(messages)) {
//                    setGcmRegid(messages);
//                } else {
//                    if (objPlayServicesHelper != null && !TextUtils.isEmpty(objPlayServicesHelper.getRegistrationId())) {
//                        setGcmRegid(null);
//                    } else {
//                        if (objPlayServicesHelper == null) {
//                            objPlayServicesHelper = new PlayServicesHelper(HomeSliderActivity.this);
//                        }
//
//                        objPlayServicesHelper.registerGcm(new GenericListener<String>() {
//                            @Override
//                            public void onResponse(int callerID, String messages) {
//                                setGcmRegid(messages);
//                            }
//                        });
//
//
//                    }
//                }
//            }
//        });


//
//    private void setGcmRegid(String regid){
//
//        String gcmRegId =(regid==null)? objPlayServicesHelper.getRegistrationId(): regid;
//
//        Call<ResponseBody> apiCall = TechProServicesController.getInstance().getBabychakraServices()
//                .registerGCM(LocalStorage.getInstance(HomeActivity.this).getToken(),
//                        AppUtils.getDeviceID(getApplicationContext()),
//                        gcmRegId);
//
//        apiCall.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
//                AppUtils.showLog("GCM REGISTRATION SUCCESS "+response.body().toString());
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                AppUtils.showLog("GCM REGISTRATION ERROR " +t.toString());
//            }
//        });
//
//
//    }
//