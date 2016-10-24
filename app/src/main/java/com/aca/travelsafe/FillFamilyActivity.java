package com.aca.travelsafe;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.aca.travelsafe.Fragment.FillFamilyFragment;
import com.aca.travelsafe.Fragment.FillFlightDetailFragment;
import com.aca.travelsafe.Fragment.FillPaymentFragment;
import com.aca.travelsafe.Util.var;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FillFamilyActivity extends BaseActivity implements FillFamilyFragment.OnFragmentInteractionListener{

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.frameFamily)
    FrameLayout frameFamily;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_family);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initFragment();

    }


    private void initFragment() {
        if (getIntent().getExtras() != null) {
            String id = getIntent().getExtras().get(var.id).toString();
            setFragmentNoBackStack(frameFamily.getId(), FillFamilyFragment.newInstance(id));
        } else
            setFragmentNoBackStack(frameFamily.getId(), FillFamilyFragment.newInstance());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case  android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
