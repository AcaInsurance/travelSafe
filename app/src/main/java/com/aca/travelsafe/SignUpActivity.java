package com.aca.travelsafe;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.aca.travelsafe.Fragment.CustomerDetailFragment;
import com.aca.travelsafe.Fragment.CustomerIndividuFragment;
import com.aca.travelsafe.Fragment.SignupFragment;
import com.aca.travelsafe.Interface.ScrollToListener;

public class SignUpActivity extends BaseActivity
        implements
        SignupFragment.OnFragmentInteractionListener,
        ScrollToListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initFragment();
    }

    protected void initFragment() {
        setFragmentNoBackStack(R.id.fragment, SignupFragment.newInstance());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                transitionSlideExit();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        transitionSlideExit();
    }

    @Override
    public void scrollTo(View view) {

    }
}
