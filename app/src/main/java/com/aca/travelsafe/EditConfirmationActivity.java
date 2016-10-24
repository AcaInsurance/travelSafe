package com.aca.travelsafe;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.aca.travelsafe.Fragment.DashboardFragment;
import com.aca.travelsafe.Interface.DrawerListener;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.GeneralSetting;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EditConfirmationActivity extends BaseActivity implements DrawerListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fragment)
    FrameLayout fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_confirmatiin);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable drawable = getResources().getDrawable(R.drawable.ic_close);
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(drawable);

        initFragment();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GeneralSetting.delete(var.GENERAL_SETTING_EDIT_CONFIRMATION);
    }

    private void initFragment() {
        setFragment(fragment.getId(), DashboardFragment.newInstance());
    }


    private void onFinish (){
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onFinish();

    }


    @Override
    public void setSelectedDrawer(int navItem) {

    }

    @Override
    public void setAppBarBackground(boolean transparent) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home : finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
