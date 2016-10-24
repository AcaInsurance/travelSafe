package com.aca.travelsafe;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.aca.travelsafe.Fragment.PaymentFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PaymentActivity extends BaseActivity implements PaymentFragment.OnFragmentInteractionListener{

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fragment)
    FrameLayout fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable drawable = getResources().getDrawable(R.drawable.ic_close);
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(drawable);

        initfragment();
    }

    private void initfragment() {
        setFragmentNoBackStack(fragment.getId(), PaymentFragment.newInstance());
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case  android.R.id.home :
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
