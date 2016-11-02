package com.techpro.chat.ticklechat.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.techpro.chat.ticklechat.R;
import com.techpro.chat.ticklechat.adapters.AddGroupMembersAdapter;
import com.techpro.chat.ticklechat.models.CustomModel;
import com.techpro.chat.ticklechat.models.DataStorage;
import com.techpro.chat.ticklechat.models.Group;
import com.techpro.chat.ticklechat.models.TickleFriend;
import com.techpro.chat.ticklechat.models.message.CreateGroup;
import com.techpro.chat.ticklechat.rest.ApiClient;
import com.techpro.chat.ticklechat.rest.ApiInterface;
import com.techpro.chat.ticklechat.utils.SharedPreferenceUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewGroupFragment extends Fragment {
    private Button createGroup;
    private Activity mActivity = null;
    private AddGroupMembersAdapter mAdapter;
    private List<TickleFriend> movieList = new ArrayList<>();
    EditText groupname;
    private RecyclerView mRecyclerView;
    Group grp = null;
    public static List<Integer> addedUser =  new ArrayList<Integer>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        grp = new Group();
        View view = inflater.inflate(R.layout.activity_create_group,
                container, false);
        createGroup = (Button) view.findViewById(R.id.creategroup);
        groupname = (EditText)view.findViewById(R.id.groupname);
        mActivity = getActivity();
        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callCreateGroupService(groupname.getText().toString(),"" ,DataStorage.UserDetails.getId(),DataStorage.UserDetails.getId());
            }
        });
        return view;
    }


    /*
* Get - User details by user chatUserList
* @param userId - user chatUserList
* */
    private void callCreateGroupService(String name, String image, String created_by, String admin) {
        //Getting webservice instance which we need to call
        Call<CreateGroup> callForUserDetailsFromID = ApiClient.createServiceWithAuth(DataStorage.UserDetails.getId()).
                create(ApiInterface.class).postNewGroup(name,image,created_by,admin);
        //Calling Webservice by enqueue
        callForUserDetailsFromID.enqueue(new Callback<CreateGroup>() {
            @Override
            public void onResponse(Call<CreateGroup> call, Response<CreateGroup> response) {
                if (response != null) {
                    if (response.body().getStatus().equals("success")) {
                        grp.setId(response.body().getBody().getId());
                        grp.setAdmin(response.body().getBody().getId());
                        grp.setCreated_at(response.body().getBody().getCreated_at());
                        grp.setCreated_by(response.body().getBody().getCreated_by());
                        grp.setGroup_image(response.body().getBody().getGroup_image());
                        grp.setImage(response.body().getBody().getImage());
                        grp.setName(response.body().getBody().getName());
                        grp.setUpdated_at(response.body().getBody().getUpdated_at());

                        final Dialog dialog = new Dialog(mActivity);
                        // Include dialog.xml file
                        dialog.setContentView(R.layout.dialog_add_group_members);
                        // Set dialog title
                        dialog.setTitle("Select Group Members");

                        // set values for custom dialog components - text, image and button
                        mRecyclerView = (RecyclerView) dialog.findViewById(R.id.my_recycler_view);
                        (dialog.findViewById(R.id.buttonok)).
                                setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.e("Create group","ssssssssssssss == "+addedUser);
                                callAddMembersService(addedUser,Integer.parseInt(grp.getId()));

                            }
                        });

                        (dialog.findViewById(R.id.buttoncancle)).
                                setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });
                        addedUser.clear();
                        mAdapter = new AddGroupMembersAdapter(DataStorage.chatUserList,mActivity.getApplicationContext(),true,true);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        mRecyclerView.setAdapter(mAdapter);
                        dialog.show();

                    }
                } else {
                    Log.e("SendMessage", "Success callMessage_ALL_Service but null response");
                }
            }

            @Override
            public void onFailure(Call<CreateGroup> call, Throwable t) {
                // Log error here since request failed
                Log.e("SendMessage", t.toString());
//                dialog.dismiss();
            }
        });

    }




    /*
* Get - User details by user chatUserList
* @param userId - user chatUserList
* */
    private void callAddMembersService(final List<Integer> members, int groupId) {
        //Getting webservice instance which we need to call
        String idList = members.toString();
        String csv = idList.substring(1, idList.length() - 1).replace(", ", ",");
        Call<CustomModel> callForUserDetailsFromID = ApiClient.createServiceWithAuth(DataStorage.UserDetails.getId()).
                create(ApiInterface.class).postGroupMembers(groupId,csv.toString());
        //Calling Webservice by enqueue
        callForUserDetailsFromID.enqueue(new Callback<CustomModel>() {
            @Override
            public void onResponse(Call<CustomModel> call, Response<CustomModel> response) {
                if (response != null) {
                    Log.e("SendMessage", "response.message(: "+response.message());
                    Log.e("SendMessage", "response.code(: "+response.code());
                    if (response.body() != null && response.body().getStatus().equals("success")) {
                        grp.setMembers(members.toString().replace("[","").replace("]",""));
                        DataStorage.mygrouplist.add(grp);
                        SharedPreferenceUtils.setColleactionObject(mActivity.getApplicationContext(),
                                SharedPreferenceUtils.mygrouplist,DataStorage.mygrouplist);
                    }
                } else {
                    Log.e("SendMessage", "Success callMessage_ALL_Service but null response");
                }
            }

            @Override
            public void onFailure(Call<CustomModel> call, Throwable t) {
                // Log error here since request failed
                Log.e("SendMessage", t.toString());
//                dialog.dismiss();
            }
        });

    }
}
