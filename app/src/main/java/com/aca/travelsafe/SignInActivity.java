package com.aca.travelsafe;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.aca.travelsafe.Fragment.SignInFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignInActivity extends BaseActivity implements SignInFragment.SignInListener{

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.frameSignIn)
    FrameLayout frameSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initFragment();
    }

    private void initFragment() {
        setFragmentNoBackStack(frameSignIn.getId(), SignInFragment.newInstance());
    }

    @Override
    public void afterLogin() {

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
}
