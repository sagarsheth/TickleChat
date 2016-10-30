package com.techpro.chat.ticklechat.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techpro.chat.ticklechat.R;

public class SentenceFragment extends Fragment implements View.OnClickListener{

    View mView;
    TextView mTvAddNewTickle;
    TextView mTvBtnSave;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(mView ==null)
        {
            mView = inflater.inflate(R.layout.fragment_sentence_fragment, container, false);
        }
        initUi();
        return mView;
    }

    private void initUi()
    {
        mTvAddNewTickle = (TextView) mView.findViewById(R.id.tv_add_new_tickle);
        mTvBtnSave = (TextView) mView.findViewById(R.id.tv_btn_save);
        mTvBtnSave.setOnClickListener(this);
    }

    @Override
    public void onClick (View v)
    {
        switch (v.getId())
        {
            case R.id.tv_btn_save:
                break;

        }

    }
}
