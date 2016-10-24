package com.aca.travelsafe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aca.travelsafe.Fragment.CustomerDetailFragment;
import com.aca.travelsafe.Fragment.CustomerIndividuFragment;
import com.aca.travelsafe.Fragment.MyProfileFragment;
import com.aca.travelsafe.Interface.DrawerListener;
import com.aca.travelsafe.Interface.ScrollToListener;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.GeneralSetting;
import com.aca.travelsafe.gcm.QuickstartPreferences;
import com.aca.travelsafe.gcm.RegistrationIntentService;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MyProfileActivity extends BaseActivity
        implements
        DrawerListener,

        ScrollToListener{

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fragment)
    FrameLayout fragment;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @Bind(R.id.appBarLayout)
    AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbarLayout.setTitle(getString(R.string.profile));
        GeneralSetting.delete(var.GENERAL_SETTING_IS_POLIS_ACTIVITY);
        initFragment();

    }

    private void initFragment() {
        setFragmentNoBackStack(fragment.getId(), MyProfileFragment.newInstance());
    }


    @Override
    public void setSelectedDrawer(int navItem) {

    }

    @Override
    public void setAppBarBackground(boolean transparent) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                break;
        }
        return true;
    }

    @Override
    public void scrollTo(View view) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

