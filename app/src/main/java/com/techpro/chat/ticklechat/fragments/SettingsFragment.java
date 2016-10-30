package com.techpro.chat.ticklechat.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.techpro.chat.ticklechat.R;

public class SettingsFragment extends Fragment implements View.OnClickListener
{
    private View mView;
    private TextView mTvDeleteAccount;
    private LinearLayout mLlEnableNotifications;
    private Snackbar mSnackbar;
    private View mViewBlackOverlay;
    private RelativeLayout mRlMainContainer;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (mView == null)
        {
            mView = inflater.inflate(R.layout.fragment_setting, container, false);
        }
        initUi();
        return mView;
    }

    private void initUi ()
    {
        mLlEnableNotifications = (LinearLayout) mView.findViewById(R.id.ll_enable_notification);
        mTvDeleteAccount = (TextView) mView.findViewById(R.id.tv_delete_account);
        mViewBlackOverlay = mView.findViewById(R.id.blackout_view_for_snackbar);
        mRlMainContainer = (RelativeLayout) mView.findViewById(R.id.rl_main_container);

        mTvDeleteAccount.setOnClickListener(this);
        mTvDeleteAccount.setOnClickListener(this);
        mLlEnableNotifications.setOnClickListener(this);
        mViewBlackOverlay.setOnClickListener(this);
    }

    @Override
    public void onClick (View v)
    {
        switch (v.getId())
        {
            case R.id.tv_delete_account:
                showSnackBar();
                break;
            case R.id.ll_enable_notification:
                //TODO : add delete account event
                break;
            case R.id.blackout_view_for_snackbar:
                if (mSnackbar != null && mSnackbar.isShown())
                {
                    mSnackbar.dismiss();
                }
                mViewBlackOverlay.setVisibility(View.GONE);
                break;

        }
    }

    private void showSnackBar ()
    {

        mSnackbar = Snackbar.make(mViewBlackOverlay, "", Snackbar.LENGTH_INDEFINITE);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) mSnackbar.getView();
        layout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        TextView textView = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setVisibility(View.INVISIBLE);
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View snackView = mInflater.inflate(R.layout.layout_snackbar_delete_account, null);
        snackView.setBackgroundColor(getResources().getColor(R.color.white));

        final TextView mTvBtnDelete = (TextView) snackView.findViewById(R.id.tv_btn_delete);
//        final TextView mTvDeleteAccountInfo = (TextView) snackView.findViewById(R.id.tv_delete_account_info);
//        mTvDeleteAccountInfo.setMovementMethod(new ScrollingMovementMethod());

        View.OnClickListener snackbarViewClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick (View v)
            {

                if (mSnackbar != null && mSnackbar.isShown())
                {
                    mSnackbar.dismiss();
                }
                if (mViewBlackOverlay != null)
                {
                    mViewBlackOverlay.setVisibility(View.GONE);
                }
                switch (v.getId())
                {
                    case R.id.tv_delete_account:
                        // TODO: 29/10/16 add action

                        if (mTvBtnDelete.isSelected())
                        {
                            mTvBtnDelete.setSelected(false);
                        }
                        else
                        {
                            mTvBtnDelete.setSelected(true);
                        }
                        break;
                }
            }
        };

        mTvBtnDelete.setOnClickListener(snackbarViewClickListener);
        layout.addView(snackView, 0);

        mSnackbar.show();
        mViewBlackOverlay.setVisibility(View.VISIBLE);

    }

}
