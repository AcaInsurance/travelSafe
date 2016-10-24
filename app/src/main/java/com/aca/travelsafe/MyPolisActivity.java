package com.aca.travelsafe;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.aca.travelsafe.Fragment.BeneficiaryFragment;
import com.aca.travelsafe.Fragment.CustomerFamilyFragment;
import com.aca.travelsafe.Fragment.FillConfirmationFragment;
import com.aca.travelsafe.Fragment.MyPolisFragment;
import com.aca.travelsafe.Interface.ScrollToListener;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.GeneralSetting;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MyPolisActivity extends BaseActivity
        implements
        FillConfirmationFragment.OnFragmentInteractionListener,
        ScrollToListener,
        CustomerFamilyFragment.OnFragmentInteractionListener,
        BeneficiaryFragment.OnFragmentInteractionListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fragment)
    FrameLayout fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_polis);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
        initFragment();
    }

    private void initFragment() {
        setFragmentNoBackStack(fragment.getId(), MyPolisFragment.newInstance());
    }

    private void init() {
        GeneralSetting.insert(var.GENERAL_SETTING_IS_POLIS_ACTIVITY, String.valueOf(true));
    }

    @Override
    public void scrollTo(View view) {

    }

    @Override
    protected void onDestroy() {
        GeneralSetting.delete(var.GENERAL_SETTING_IS_POLIS_ACTIVITY);
        super.onDestroy();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
