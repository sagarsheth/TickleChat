package com.techpro.chat.ticklechat.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.techpro.chat.ticklechat.R;

public class ProfileFragment extends Fragment implements View.OnClickListener{
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ImageView ivProfileIcon;
    private EditText profilename;
    private EditText profileemail;
    private EditText profilephone;
    private EditText profile_date;
    private TextView tvBtnMale;
    private TextView tvBtnFemale;
    private TextView submit;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view == null)
        {
            view = inflater.inflate(R.layout.activity_profile, container, false);
        }
        initUi();
        return view;
    }
    private void initUi()
    {
        ivProfileIcon = (ImageView) view.findViewById(R.id.iv_profile_icon);
        profilename = (EditText) view.findViewById(R.id.profilename);
        profileemail = (EditText) view.findViewById(R.id.profileemail);
        profilephone = (EditText) view.findViewById(R.id.profilephone);
        profile_date = (EditText) view.findViewById(R.id.profile_date);
        tvBtnMale = (TextView) view.findViewById(R.id.tv_btn_male);
        tvBtnFemale = (TextView) view.findViewById(R.id.tv_btn_female);
        submit = (TextView) view.findViewById(R.id.submit);

        tvBtnMale.setSelected(true);
        tvBtnMale.setOnClickListener(this);
        tvBtnFemale.setOnClickListener(this);
        submit.setOnClickListener(this);
        ivProfileIcon.setOnClickListener(this);

    }

    @Override
    public void onClick (View v)
    {
        switch (v.getId())
        {
            case R.id.submit:
                // TODO: 30/10/16
                break;

            case R.id.iv_profile_icon:
                // TODO: 30/10/16
                break;

            case R.id.tv_btn_male:
                // TODO: 30/10/16
                tvBtnFemale.setSelected(false);
                tvBtnMale.setSelected(true);
                break;

            case R.id.tv_btn_female:
                // TODO: 30/10/16
                tvBtnMale.setSelected(false);
                tvBtnFemale.setSelected(true);
                break;
        }
    }
}
