package com.techpro.chat.ticklechat.activity.home;

import android.app.ProgressDialog;
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

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.techpro.chat.ticklechat.AppConfigs;
import com.techpro.chat.ticklechat.Constants;
import com.techpro.chat.ticklechat.R;
import com.techpro.chat.ticklechat.activity.ChatScreen;
import com.techpro.chat.ticklechat.activity.registration.Login;
import com.techpro.chat.ticklechat.activity.registration.PersonalProfileActivity;
import com.techpro.chat.ticklechat.database.DataBaseHelper;
import com.techpro.chat.ticklechat.fragments.HomeScreenFragment;
import com.techpro.chat.ticklechat.fragments.NewGroupFragment;
import com.techpro.chat.ticklechat.fragments.ProfileFragment;
import com.techpro.chat.ticklechat.fragments.SentenceFragment;
import com.techpro.chat.ticklechat.fragments.SettingsFragment;
import com.techpro.chat.ticklechat.fragments.StatusUpdateFragment;
import com.techpro.chat.ticklechat.fragments.TickleFriendFragment;
import com.techpro.chat.ticklechat.listeners.FragmentChangeCallback;
import com.techpro.chat.ticklechat.models.CustomModel;
import com.techpro.chat.ticklechat.models.DataStorage;
import com.techpro.chat.ticklechat.models.GetGroupDetails;
import com.techpro.chat.ticklechat.models.Group;
import com.techpro.chat.ticklechat.models.MenuItems;
import com.techpro.chat.ticklechat.models.message.AllMessages;
import com.techpro.chat.ticklechat.models.message.Tickles;
import com.techpro.chat.ticklechat.models.user.GetUserDetails;
import com.techpro.chat.ticklechat.models.user.GetUserDetailsBody;
import com.techpro.chat.ticklechat.models.user.User;
import com.techpro.chat.ticklechat.models.user.UserModel;
import com.techpro.chat.ticklechat.rest.ApiClient;
import com.techpro.chat.ticklechat.rest.ApiInterface;
import com.techpro.chat.ticklechat.utils.AppUtils;
import com.techpro.chat.ticklechat.utils.FragmentUtils;
import com.techpro.chat.ticklechat.utils.SharedPreferenceUtils;

import java.util.ArrayList;
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
    private static final String TAG = HomeActivity.class.getSimpleName();
    private ProgressDialog dialog;

    public static final String KEY_TITLE = "title";
    private ApiInterface apiService;
    private ApiInterface apiAUTService;
    private List<String> allUserID = null;
    private List<String> userid = null;
    private List<String> grpid = null;
    private boolean isUserDataSetReady = false;
    private boolean isGroupDataSetReady = false;

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
        gcmRegistration();


        DataStorage.mygrouplist  = new ArrayList<Group>();
        DataStorage.myAllUserlist = new ArrayList<User>();
        DataStorage.chatUserList = new ArrayList<User>();
        DataStorage.chatUserList = (List<User>) SharedPreferenceUtils.getColleactionObject(getApplicationContext(),SharedPreferenceUtils.chatUserID);
        DataStorage.mygrouplist = (List<Group>) SharedPreferenceUtils.getColleactionObject(getApplicationContext(),SharedPreferenceUtils.mygrouplist);
        DataStorage.myAllUserlist = (List<User>) SharedPreferenceUtils.getColleactionObject(getApplicationContext(),SharedPreferenceUtils.myuserlist);

        if (DataStorage.myAllUserlist == null || DataStorage.mygrouplist == null  || DataStorage.chatUserList == null) {
            DataStorage.mygrouplist  = new ArrayList<Group>();
            DataStorage.myAllUserlist = new ArrayList<User>();
            DataStorage.chatUserList = new ArrayList<User>();
            dialog = ProgressDialog.show(HomeActivity.this, "Loading", "Please wait...", true);
            apiService = ApiClient.getClient().create(ApiInterface.class);
            callGetUserDetailsService(Integer.parseInt(DataStorage.UserDetails.getId()), true, false);
            apiAUTService = ApiClient.createServiceWithAuth(DataStorage.UserDetails.getId()).create(ApiInterface.class);
            callMessage_ALL_Service();
        } else {
            Log.e("ssssssssssssss","mymessagelist & myAllUserlist ==> not null");
            Fragment fragment = new HomeScreenFragment();
            replaceFragment(fragment, getResources().getString(R.string.header_ticklers), false);
        }

//        TODO Vishal to call below method to get 5 messages form preloaded DB
//        Log.e("ssssssssssssss","==> "+new MessageController(getApplicationContext()).getMessages());

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
                        dialog = ProgressDialog.show(HomeActivity.this, "Loading", "Please wait...", true);
                        getRandomUser();
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
                        fragment = new SentenceFragment();
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

    /*
  * Get - User details by user chatUserList
  * @param userId - user chatUserList
  * */
    private void getRandomUser() {
        //Getting webservice instance which we need to call
        Call<GetUserDetails> callForUserDetailsFromID = (ApiClient.createServiceWithAuth(DataStorage.UserDetails.getId()).create(ApiInterface.class)).getRandomUser();
        //Calling Webservice by enqueue
        callForUserDetailsFromID.enqueue(new Callback<GetUserDetails>() {
            @Override
            public void onResponse(Call<GetUserDetails> call, Response<GetUserDetails> response) {
                if (response != null || response.body().getBody() != null) {
                    User usr = response.body().getBody().getUser();
                    if (usr != null) {
                        Intent intent = new Intent(getApplicationContext(), ChatScreen.class);
                        intent.putExtra("userid", usr.getId());
//                    intent.putExtra("israndom",true);
                        Log.d("DataStorage.randomUser", "user.getId()：" + usr.getId());
                        startActivity(intent);
                    } else {

                        Log.e(TAG, "onResponse callMessage_ALL_Service but null response");
                    }
                } else {
                    Log.e(TAG, "Success callMessage_ALL_Service but null response");
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<GetUserDetails> call, Throwable t) {
                Log.e(TAG, "onFailure callMessage_ALL_Service but null response");
                // Log error here since request failed
                Log.e(TAG, t.toString());
                dialog.dismiss();
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
        * Get - User details by user chatUserList
        * @param userId - user chatUserList
        * */
    private synchronized void callGetUserDetailsService(int userId, final boolean iscurrentuser, final boolean isgroup) {
        //Getting webservice instance which we need to call
        Call<GetUserDetails> callForUserDetailsFromID = apiService.getUserDetailsFromID(userId);
        //Calling Webservice by enqueue
        callForUserDetailsFromID.enqueue(new Callback<GetUserDetails>() {
            @Override
            public void onResponse(Call<GetUserDetails> call, Response<GetUserDetails> response) {
                if (response != null && response.body()!=null) {
                    GetUserDetailsBody getUserDetails = response.body().getBody();
                    User usr = getUserDetails.getUser();
                    if (iscurrentuser) {
                        allUserID = new ArrayList<String>();
                        DataStorage.currentUserDetailsBody = getUserDetails.getUser();
                    } else if (!isgroup) {

                        if (usr!=null && usr.getId()!= null  && usr.getName()!=null) {
                            DataStorage.chatUserList.add(usr);
//                            Log.e(TAG, "Success  ADDED USER: " + usr.getName());
//                            Log.e(TAG, "Success  ADDED USER: " + usr.getId());
                            List<AllMessages.MessageList.ChatMessagesList> usermessages = new ArrayList<>();
                            for (int i = 0; i < DataStorage.allMessages.size(); i++) {
                                AllMessages.MessageList.ChatMessagesList msg = DataStorage.allMessages.get(i);
                                if ((msg.getFrom_id().equals(usr.getId()) || (msg.getTo_id().equals(usr.getId()))) && msg.getIsgroup().equals("0")) {
//                                    Log.e(TAG, "Success  usermessages: " + msg.getMessage() );
                                    usermessages.add(msg);
                                }
                            }
                            SharedPreferenceUtils.setColleactionObject(getApplicationContext(),usr.getId(),usermessages);
                        }
                    }
                    if (!allUserID.contains(getUserDetails.getUser())) {
                        allUserID.add(getUserDetails.getUser().getId());
                        DataStorage.myAllUserlist.add(getUserDetails.getUser());
                    }

                    if (isUserDataSetReady && isGroupDataSetReady) {
                        dialog.dismiss();

                        SharedPreferenceUtils.setColleactionObject(getApplicationContext(),SharedPreferenceUtils.myuserlist,DataStorage.myAllUserlist);
                        SharedPreferenceUtils.setColleactionObject(getApplicationContext(),SharedPreferenceUtils.mygrouplist,DataStorage.mygrouplist);
                        SharedPreferenceUtils.setColleactionObject(getApplicationContext(),SharedPreferenceUtils.chatUserID,DataStorage.chatUserList);
                        getTicklesService();
                        Fragment fragment = new HomeScreenFragment();
                        replaceFragment(fragment, getResources().getString(R.string.header_ticklers), false);
                    }

////                                Calendar cl = Calendar.getInstance();
////                                cl.setTimeInMillis(Long.parseLong(msg.getRequested_at()));  //here your time in miliseconds
////                                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
////                                String datenew = format.format(cl.getTime());
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
* Get - User details by user chatUserList
* @param userId - user chatUserList
* */
    private void callMessage_ALL_Service() {
        //Getting webservice instance which we need to call
        Call<AllMessages> callForUserDetailsFromID = apiAUTService.getAllMessageList();
        //Calling Webservice by enqueue
        callForUserDetailsFromID.enqueue(new Callback<AllMessages>() {
            @Override
            public void onResponse(Call<AllMessages> call, Response<AllMessages> response) {
                if (response != null) {
                    DataStorage.allMessages = response.body().getBody().getMessages();
                    userid = new ArrayList<String>();
                    grpid = new ArrayList<String>();
                    userid.clear();
                    grpid.clear();
                    for (int i = 0; i < DataStorage.allMessages.size(); i++) {
                        if (i == DataStorage.allMessages.size()-1){
                            isUserDataSetReady = true;
                        }
                        AllMessages.MessageList.ChatMessagesList msg = DataStorage.allMessages.get(i);
                        if (msg.getIsgroup().equals("1")) {
                            String messageFromUserID = msg.getFrom_id();
                            String groupID = msg.getTo_id();
                            if (!grpid.contains(groupID)) {
                                grpid.add(groupID);
                                callGetGroupDetailsService(Integer.parseInt(groupID));
                            }
                        } else {
                            String toUser = msg.getTo_id(); //Should me current users ID
                            String messageFromUserID = msg.getFrom_id();
                            if (!userid.contains(messageFromUserID)) {
                                userid.add(messageFromUserID);
                                callGetUserDetailsService(Integer.parseInt(messageFromUserID), false ,false);
                            }
                        }

                    }
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
* Get - User details by user chatUserList
* @param userId - user chatUserList
* */
    private synchronized void callGetGroupDetailsService(int groupid) {
        //Getting webservice instance which we need to call
        Call<GetGroupDetails> callForUserDetailsFromID = apiAUTService.getGroupDetials(groupid);
        //Calling Webservice by enqueue
        callForUserDetailsFromID.enqueue(new Callback<GetGroupDetails>() {
            @Override
            public void onResponse(Call<GetGroupDetails> call, Response<GetGroupDetails> response) {
                if (response != null) {
                    Group grp = response.body().getBody().getGroup();

//                    Log.e(TAG, "Success  ADDED grp: " + grp.getName());
                    if (grp!=null && grp.getId()!= null  && grp.getName()!=null) {
                        DataStorage.mygrouplist.add(grp);
//                        Log.e(TAG, "Success  ADDED GROUP: " + grp.getName());
//                        Log.e(TAG, "Success  ADDED GROUP: " + grp.getId());

                        List<AllMessages.MessageList.ChatMessagesList> groupmessages = new ArrayList<>();
                        for (int i = 0; i < DataStorage.allMessages.size(); i++) {
                            AllMessages.MessageList.ChatMessagesList msg = DataStorage.allMessages.get(i);
                            if (msg.getTo_id().equals(grp.getId()) && msg.getIsgroup().equals("1")) {
//                                Log.e(TAG, "Success  groupmessages: " + msg.getMessage() );
                                groupmessages.add(msg);
                            }
                        }
                        SharedPreferenceUtils.setColleactionObject(getApplicationContext(),grp.getId(),groupmessages);


                        if (grp.getId() != null && grp.getMembers().contains(",")) {
                            String[] ids = grp.getMembers().split(",");
                            for (int i = 0; i < ids.length; i++) {
                                if (i == ids.length-1 && isUserDataSetReady){
                                    isGroupDataSetReady = true;
                                }
                                callGetUserDetailsService(Integer.parseInt(ids[i]), false, true);
                            }
                        }

                    }
                } else {
                    Log.e(TAG, "Success callTickles_Service but null response");
                }
            }

            @Override
            public void onFailure(Call<GetGroupDetails> call, Throwable t) {
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

    /*
* Get - User details by user chatUserList
* @param userId - user chatUserList
* */
    private synchronized void getTicklesService() {
        //Getting webservice instance which we need to call
        Call<Tickles> callForUserDetailsFromID = (ApiClient.createServiceWithAuth(DataStorage.UserDetails.getId())
                                                           .create(ApiInterface.class)).getTickles();
        //Calling Webservice by enqueue
        callForUserDetailsFromID.enqueue(new Callback<Tickles>() {
            @Override
            public void onResponse(Call<Tickles> call, Response<Tickles> response) {
                if (response != null) {
                    if (response.body() != null && response.body().getStatus().equals("success")) {
                        new DataBaseHelper(getApplicationContext()).insertMessages(response.body().getBody().getTickles());
                    }

                } else {
                    Log.e("profile", "Success callTickles_Service but null response");
                }
            }

            @Override
            public void onFailure(Call<Tickles> call, Throwable t) {
                // Log error here since request failed
                Log.e("profile", t.toString());
            }
        });

    }

    void gcmRegistration()
    {
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }
        // [END handle_data_extras]

        // Get token
        FirebaseApp.initializeApp(HomeActivity.this);
        String token = FirebaseInstanceId.getInstance().getToken();

        Log.e(TAG, token);
        UpdateDeviceTockan(token);
        Toast.makeText(HomeActivity.this, token, Toast.LENGTH_SHORT).show();

//        FirebaseApp.initializeApp(HomeActivity.this);
//                // [START subscribe_topics]
//                FirebaseMessaging.getInstance().subscribeToTopic("news");
//                // [END subscribe_topics]
//
//                // Log and toast
//                String msg = getString(R.string.app_name);
//                Log.d(TAG, msg);
//                Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
        }

    /*
* Get - User details by user chatUserList
* @param userId - user chatUserList
* */
    private synchronized void UpdateDeviceTockan(String userid) {
        //Getting webservice instance which we need to call
        Call<CustomModel> callForUserDetailsFromID = (ApiClient.createServiceWithAuth(DataStorage.UserDetails.getId())
                .create(ApiInterface.class)).UpdateDeviceTockan(userid);
        //Calling Webservice by enqueue
        callForUserDetailsFromID.enqueue(new Callback<CustomModel>() {
            @Override
            public void onResponse(Call<CustomModel> call, Response<CustomModel> response) {
                if (response != null) {
                    if (response.body() != null && response.body().getStatus().equals("success")) {
                        Log.e("UpdateDeviceTockan", "Success callTickles_Service done");

                    }

                } else {
                    Log.e("UpdateDeviceTockan", "Success callTickles_Service but null response");
                }
            }

            @Override
            public void onFailure(Call<CustomModel> call, Throwable t) {
                // Log error here since request failed
                Log.e("profile", t.toString());
            }
        });

    }

}