package com.aca.travelsafe;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.aca.travelsafe.Fragment.ChooseCountryFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChooseCountryActivity extends BaseActivity implements ChooseCountryFragment.ChooseCountryFragmentListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fragment)
    FrameLayout fragment;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_country);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initFragment();
    }

    protected void initFragment() {
        setFragmentNoBackStack(fragment.getId(), ChooseCountryFragment.newInstance());
    }

    @Override
    public void onBackPressed() {
        performCancel();
    }

    @Override
    public void performOK() {
//        Intent returnIntent = new Intent();
//        returnIntent.putExtra(var.Country, Parcels.wrap(pickedCountry));
//        setResult(RESULT_OK, returnIntent);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void performCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                performCancel();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }


}
